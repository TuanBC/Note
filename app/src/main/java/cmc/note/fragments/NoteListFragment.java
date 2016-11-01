package cmc.note.fragments;

import android.app.ActionBar;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import cmc.note.R;
import cmc.note.activities.MainActivity;
import cmc.note.activities.NoteEditorActivity;
import cmc.note.adapter.NoteListAdapter;
import cmc.note.data.DatabaseHelper;
import cmc.note.data.NoteManager;
import cmc.note.models.Note;
import cmc.note.fragments.RecyclerItemClickListener;

import static cmc.note.R.id.container;


/**
 * Created by tuanb on 10-Oct-16.
 */

public class NoteListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, SearchView.OnQueryTextListener {
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
        // Inflate the layout for this fragment and hold the reference in mRootView
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
                Note selectedNote = mNotes.get(position);

                Intent editorIntent = new Intent(getActivity(), NoteEditorActivity.class);
                editorIntent.putExtra("id", selectedNote.getId());

                startActivity(editorIntent);
            }

            @Override
            public void onLongItemClick(View view, int position) {
                confirmDialog(position);
            }
        }));
    }

    public void confirmDialog(int position) {
        Note selectedNote = mNotes.get(position);

        Bundle args = new Bundle();
        args.putLong("id", selectedNote.getId());

        DialogFragment newFragment = new ListDialogFragment();
        newFragment.setArguments(args);
        newFragment.show(getFragmentManager(), "NOTE OPTIONS");
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

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_search:
                break;
            case R.id.action_exit:
                    promptToExit();
                break;
            case R.id.action_sort:
                    promptToSort();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void promptToSort() {
        String[] array_sort = {"Alphabetical", "Created time", "Modified time"};
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle(R.string.sort)
                .setItems(array_sort, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:
                                mNotes = NoteManager.newInstance(getActivity()).getAllNotesSortedBy("abc_asc");
                                break;
                            case 1:
                                mNotes = NoteManager.newInstance(getActivity()).getAllNotesSortedBy("created_desc");
                                break;
                            case 2:
                                mNotes = NoteManager.newInstance(getActivity()).getAllNotesSortedBy("modified_desc");
                                break;
                        }
                        mAdapter = new NoteListAdapter(mNotes, getActivity());
                        mRecyclerView.setAdapter(mAdapter);
                    }
                });
        alertDialog.show();
    }

    private void promptToExit() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setMessage("Exit the program?");
        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(0);
            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.show();
    }

    @Override
    public void onRefresh() {
        setupList();
    }

    private void makeToast(String s) {
        Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onQueryTextSubmit(String input) {
        List<Note> temp_notes;
        temp_notes = NoteManager.newInstance(getActivity()).getAllNotesWithKey(input);
        mAdapter = new NoteListAdapter(temp_notes, getActivity());
        mRecyclerView.setAdapter(mAdapter);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String input) {
        List<Note> temp_notes;
        temp_notes = NoteManager.newInstance(getActivity()).getAllNotesWithKey(input);
        mAdapter = new NoteListAdapter(temp_notes, getActivity());
        mRecyclerView.setAdapter(mAdapter);
        return false;
    }
}