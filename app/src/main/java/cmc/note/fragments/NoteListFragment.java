package cmc.note.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.widget.Button;
import android.widget.Toast;

import java.util.List;

import cmc.note.R;
import cmc.note.activities.MainActivity;
import cmc.note.activities.NoteEditorActivity;
import cmc.note.adapter.NoteListAdapter;
import cmc.note.data.NoteManager;
import cmc.note.models.Note;
import cmc.note.fragments.RecyclerItemClickListener;

import static cmc.note.R.id.container;


/**
 * Created by tuanb on 10-Oct-16.
 */

public class NoteListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private View mRootView;
    private List<Note> mNotes;
    private RecyclerView mRecyclerView;
    private NoteListAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public NoteListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment and hold the reference
        //in mRootView
        mRootView = inflater.inflate(R.layout.fragment_note_list, container, false);
        setupList();

        return mRootView;
    }

    private void setupList() {
        mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.note_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mNotes = NoteManager.newInstance(getActivity()).getAllNotes();
        mAdapter = new NoteListAdapter(mNotes, getActivity());
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(),
                mRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {
                //Values are passing to activity & to fragment as well
                Log.i("LOG","LOG ITEM ONCLICK" + position);

                Note selectedNote = mNotes.get(position);

                Intent editorIntent = new Intent(getActivity(), NoteEditorActivity.class);
                editorIntent.putExtra("id", selectedNote.getId());

                startActivity(editorIntent);
            }

            @Override
            public void onLongItemClick(View view, int position) {
                confirmDialog();
            }
        }));
    }

    public void confirmDialog() {
        DialogFragment newFragment = new ListDialogFragment();
        newFragment.show(getFragmentManager(), "note options");
    }

    //ADD MENU
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_add:
                    startActivity(new Intent(getActivity(), NoteEditorActivity.class));
                break;
            case R.id.action_search:

                break;

        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onRefresh() {
        setupList();
    }

}