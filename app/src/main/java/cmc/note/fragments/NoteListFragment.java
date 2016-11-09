package cmc.note.fragments;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import java.util.List;

import cmc.note.R;
import cmc.note.activities.MainActivity;
import cmc.note.activities.NoteEditorActivity;
import cmc.note.adapter.NoteListAdapter;
import cmc.note.data.NoteManager;
import cmc.note.models.CustomSearchView;
import cmc.note.models.Note;


/**
 * Created by tuanb on 10-Oct-16.
 */

public class NoteListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, SearchView.OnQueryTextListener {
    private View mRootView;
    private List<Note> mNotes;
    private RecyclerView mRecyclerView;
    private NoteListAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private NoteListAdapter tempAdapter;
    private String mListOrder; //CHECKED

    private final String TAG = "NOTELIST Fragment";

    public NoteListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment and hold the reference in mRootView
        mRootView = inflater.inflate(R.layout.fragment_note_list, container, false);
        Log.i(TAG,"get order" + mListOrder);
        setupList();

        return mRootView;
    }

    public void setListOrder(String s){
        this.mListOrder=s;
    }

    private void setupList() {
        mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.list_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mNotes = NoteManager.newInstance(getActivity()).getAllNotesSortedBy(mListOrder);
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
                editorIntent.putExtra("list_order", mListOrder);

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
        args.putString("list_order", mListOrder);

//        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
//        ListDialogFragment newFragment = new ListDialogFragment();
//        newFragment.setArguments(args);
//        ft.add(android.R.id.content, newFragment).addToBackStack(null).commitAllowingStateLoss();

        ListDialogFragment newFragment = new ListDialogFragment();
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
//        MenuItem item = menu.getItem(1);
//        item.setIcon(new IconicsDrawable(getActivity())
//                .icon(FontAwesome.Icon.faw_search)
//                .color(Color.BLACK)
//                .sizeDp(20));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_search:
                    setupSearchBox(item);
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

    private void setupSearchBox(MenuItem item) {
        MenuItemCompat.setOnActionExpandListener(item,
                new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                mRecyclerView.setAdapter(tempAdapter);
                View view = getActivity().findViewById(R.id.buttons);
                view.setVisibility(View.GONE);
                view = getActivity().findViewById(R.id.btn_set_order);
                view.setVisibility(View.GONE);
                return true; // KEEP IT TO TRUE OR IT DOESN'T OPEN !!
            }
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                mRecyclerView.setAdapter(mAdapter);
                View view = getActivity().findViewById(R.id.buttons);
                view.setVisibility(View.VISIBLE);
                view = getActivity().findViewById(R.id.btn_set_order);
                view.setVisibility(View.VISIBLE);
                mNotes = NoteManager.newInstance(getActivity()).getAllNotesSortedBy(mListOrder);
                return true; // OR FALSE IF YOU DIDN'T WANT IT TO CLOSE!
            }
        });

        CustomSearchView searchView = (CustomSearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);

        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(
                new ComponentName(getActivity(), MainActivity.class)));//???????????????????????????????????????????
    }

    @Override
    public boolean onQueryTextSubmit(String input) {
        List<Note> temp_notes;
        temp_notes = NoteManager.newInstance(getActivity()).getAllNotesWithKey(input);
        tempAdapter = new NoteListAdapter(temp_notes, getActivity());
        mNotes=temp_notes;
        mRecyclerView.setAdapter(tempAdapter);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String input) {
        final List<Note> temp_notes;
        temp_notes = NoteManager.newInstance(getActivity()).getAllNotesWithKey(input);
        tempAdapter = new NoteListAdapter(temp_notes, getActivity());
        mNotes=temp_notes;
        mRecyclerView.setAdapter(tempAdapter);
        return false;
    }

    private void promptToSort() {
        String[] array_sort = {"Alphabetical", "Created time", "Modified time"};
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle(R.string.sort)
                .setItems(array_sort, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:
                                mListOrder = "abc_asc";
                                break;
                            case 1:
                                mListOrder = "created_desc";
                                break;
                            case 2:
                                mListOrder = "modified_desc";
                                break;
                        }
                        mNotes = NoteManager.newInstance(getActivity()).getAllNotesSortedBy(mListOrder);
                        mAdapter = new NoteListAdapter(mNotes, getActivity());
                        setupList();
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
}