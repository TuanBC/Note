package cmc.note.fragments;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cmc.note.R;
import cmc.note.activities.MainActivity;
import cmc.note.adapter.ChecklistOptionsListAdapter;
import cmc.note.data.ChecklistManager;
import cmc.note.data.NoteManager;
import cmc.note.models.CheckItem;
import cmc.note.models.Note;

/**
 * Created by tuanb on 20-Oct-16.
 */

public class ChecklistEditorFragment extends Fragment {
    private View mRootView;
    private EditText mTitleEditText;
    private Note mCurrentNote;
    private TextView mTimeAgo;
    private TextView mModifiedTime;

    private RecyclerView mRecyclerView;
    private ChecklistOptionsListAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private List<CheckItem> mCurrentItems = new ArrayList<>();

    public ChecklistEditorFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_checklist_editor, container, false);
        mTitleEditText = (EditText)mRootView.findViewById(R.id.edit_text_title);
        mTimeAgo = (TextView)mRootView.findViewById(R.id.time_ago);
        mModifiedTime = (TextView)mRootView.findViewById(R.id.modified_time);
        setupList();
        return mRootView;
    }

    public static ChecklistEditorFragment newInstance(long id){
        ChecklistEditorFragment fragment = new ChecklistEditorFragment();

        if (id > 0){
            Bundle bundle = new Bundle();
            bundle.putLong("id", id);
            fragment.setArguments(bundle);
        }

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        getCurrentNote();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_note_edit, menu);
    }


    private void getCurrentNote() {                     //Extract current note to get id => get appropriate check items
        Bundle args = getArguments();
        if (args != null && args.containsKey("id")) {
            Long id = args.getLong("id",0);
            Log.i("log getcurrentnote", "id" + id);
            if (id > 0) {
                mCurrentNote = NoteManager.newInstance(getActivity()).getNote(id);
            }
        }
    }


    private void setupList() {
        mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.checklist_options_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        if (mCurrentNote!=null)
            mCurrentItems = ChecklistManager.newInstance(getActivity()).getChecklistItemByNoteId(mCurrentNote.getId());

        mAdapter = new ChecklistOptionsListAdapter(mCurrentItems, getActivity());
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(),
                mRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {
                //Values are passing to activity & to fragment as well
                Log.i("LOG","LOG ITEM ONCLICK" + position);

                CheckItem selectedItem = mCurrentItems.get(position);
                selectedItem.check();
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));
    }

//    private LinearLayout addCheckItem() {
//        LinearLayout checkitemLL = (LinearLayout) inflater.inflate(R.layout.row_checkitem, null);
//        EditText itemEdit = (EditText) checkitemLL.findViewById(R.id.item_et);
//        itemEdit.setTypeface(font);
//        checklistLL.addView(checkitemLL);
//
//        return checkitemLL;
//    }




    private boolean saveNote(){
        int size = mCurrentItems.size();
        if (size==0){
            mTitleEditText.setError("No checklist items included");
            return false;
        }

        String title = mTitleEditText.getText().toString();
        if (TextUtils.isEmpty(title)){
            mTitleEditText.setError("Title required");
        }

        if (mCurrentNote!=null){
            mCurrentNote.setTitle(title);
            NoteManager.newInstance(getActivity()).update(mCurrentNote);

            for (CheckItem item : mCurrentItems){
                item.setNoteId(mCurrentNote.getId());
                ChecklistManager.newInstance(getActivity()).update(item);
            }

            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
        }else {
            Note note = new Note();
            note.setTitle(title);
            note.setType("checklist");
            NoteManager.newInstance(getActivity()).create(note);

            Note mNote = NoteManager.newInstance(getActivity()).getNoteByTitle(title);
            for (CheckItem item : mCurrentItems){
                item.setNoteId(mNote.getId());
                ChecklistManager.newInstance(getActivity()).create(item);
            }

            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
        }
        return true;
    }


    //MENU ONCLICK LISTENER
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_save:
                if (saveNote()) { //IF NULL => BLANK NOTE
                    makeToast(mCurrentNote !=  null ? "Note updated" : "Note saved");
                }
                break;

            case R.id.action_delete:
                if (mCurrentNote != null){
                    promptForDelete();
                }else {
                    makeToast("Cannot delete note that has not been saved");
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void promptForDelete(){
        final String titleOfNoteTobeDeleted = mCurrentNote.getTitle();
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle("Delete " + titleOfNoteTobeDeleted + " ?");
        alertDialog.setMessage("Are you sure you want to delete the note " + titleOfNoteTobeDeleted + "?");
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                NoteManager.newInstance(getActivity()).delete(mCurrentNote);
                ChecklistManager.newInstance(getActivity()).deleteByNoteId(mCurrentNote.getId());

                makeToast(titleOfNoteTobeDeleted + "deleted");
                startActivity(new Intent(getActivity(), MainActivity.class));
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

    public void onBackClicked(){
        promptForDiscard();
    }

    public void promptForDiscard(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle("Discard?");
        alertDialog.setMessage("Are you sure you want to go back? All changes will be discarded");
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ChecklistManager.newInstance(getActivity()).deleteByNoteId((long) 0);
                getActivity().finish();
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

    private void makeToast(String s) {
        Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
    }
}
