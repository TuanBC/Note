package cmc.note.fragments;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
    private List<CheckItem> mAddedItems = new ArrayList<>();

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
    public void onResume() {
        super.onResume();
        if (mCurrentNote != null){
            populateFields();
        } else mModifiedTime.setText(getReadableDate(System.currentTimeMillis()));
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
        //otherwise mCurrentItems=<>

        mAdapter = new ChecklistOptionsListAdapter(mCurrentItems, getActivity());
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(),
                mRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {
                //Values are passing to activity & to fragment as well

                CheckItem selectedItem;

                if (position < mCurrentItems.size()){
                    selectedItem = mCurrentItems.get(position);
                    selectedItem.check();
                    mCurrentItems.remove(position);
                    mCurrentItems.add(position,selectedItem);
                } else {
                    selectedItem = mAddedItems.get(position-mCurrentItems.size());
                    selectedItem.check();
                    mAddedItems.remove(position-mCurrentItems.size());
                    mAddedItems.add(position-mCurrentItems.size(),selectedItem);
                }

                Log.i("LOG", "log item status"+selectedItem.getStatus());
                //ADD STRIKE THROUGH
                if (selectedItem.getStatus()) {
                    TextView temp_text_view = (TextView) view.findViewById(R.id.text_view_item_name);
                    strikeThrough(temp_text_view);
                } else {
                    TextView temp_text_view = (TextView) view.findViewById(R.id.text_view_item_name);
                    removeStrikeThrough(temp_text_view);
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));
    }




    private boolean saveNote(){
        int size = mAddedItems.size();

        String title = mTitleEditText.getText().toString();
        if (TextUtils.isEmpty(title)){
//            mTitleEditText.setError("Title required");
            mCurrentNote.setTitle(getReadableDateWithoutHour(System.currentTimeMillis()));
        }


        if (mCurrentNote!=null){
            mCurrentNote.setTitle(title);
            NoteManager.newInstance(getActivity()).update(mCurrentNote);

            for (CheckItem item : mAddedItems){
                ChecklistManager.newInstance(getActivity()).create(item);
            }

            new Thread(new Runnable() {
                public void run(){
                    List<CheckItem> mTempItems = mCurrentItems;
                    mTempItems.addAll(mAddedItems);
                    for (CheckItem item : mTempItems){
                        ChecklistManager.newInstance(getActivity()).update(item);
                    }
                }
            }).start();

            mAddedItems.clear();

            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
        }else {
            if (size==0){
                mTitleEditText.setError("No checklist items included");
                return false;
            }

            Note note = new Note();
            note.setId(mCurrentNote.getId());
            note.setTitle(title);
            note.setType("checklist");
            NoteManager.newInstance(getActivity()).update(note);

            for (CheckItem item : mAddedItems){
                item.setNoteId(mCurrentNote.getId());
                ChecklistManager.newInstance(getActivity()).update(item);
            }
            mAddedItems.clear();
            ChecklistEditorFragment.newInstance(mCurrentNote.getId());
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
                mCurrentItems.clear();
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

    public void promptToAddItem(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());

        final EditText input = new EditText(getActivity());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alertDialog.setView(input);
        alertDialog.setTitle("Add Item ");
        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (input.getText().toString().equals("")){dialog.dismiss();}
                else {
                    CheckItem item = new CheckItem();
                    item.setName(input.getText().toString());
                    item.setNoteId(mCurrentNote.getId());
                    Log.i("LOG", "mcurrentitems added");
                    mAddedItems.add(item);
                    refreshAddedItems();
                }
            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.setNeutralButton("Next", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                if (input.getText().toString().equals("")){dialog.dismiss();}
                else {
                    CheckItem item = new CheckItem();
                    item.setName(input.getText().toString());
                    item.setNoteId(mCurrentNote.getId());
                    mAddedItems.add(item);
                    refreshAddedItems();
                    promptToAddItem();
                }
            }
        });
        alertDialog.show();
    }

    //POPULATE INFO OF CURRENT NOTE TO EDIT NOTE
    private void populateFields() {
        mTitleEditText.setText(mCurrentNote.getTitle());

        //TIME
        long temp_modified_time=mCurrentNote.getDateModified();
        mModifiedTime.setText(getReadableDate(temp_modified_time));

        long temp_time_ago = (System.currentTimeMillis() - temp_modified_time)/1000 ;
        if (temp_time_ago < 60) mTimeAgo.setText(temp_time_ago + " seconds ago");
        else if (temp_time_ago < 3600) mTimeAgo.setText(temp_time_ago/60 + " minute(s) ago");
        else if (temp_time_ago < 86400) mTimeAgo.setText(temp_time_ago/3600 + " hour(s) ago");
        else mTimeAgo.setText(temp_time_ago/86400 + " day(s) ago");
    }

    private static String getReadableDate(long date){
        return new SimpleDateFormat("MMM dd, yyyy - h:mm a").format(new Date(date));
    }

    private static String getReadableDateWithoutHour(long date){
        return new SimpleDateFormat("MMM dd, yyyy").format(new Date(date));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_note_edit, menu);
    }

    public void refreshAddedItems() {
        new Thread(new Runnable() {
            public void run(){
                List<CheckItem> mTempItems = mCurrentItems;
                mTempItems.add(mAddedItems.get(mAddedItems.size()-1));
                mAdapter = new ChecklistOptionsListAdapter(mTempItems, getActivity());
            }
        }).start();
        mRecyclerView.setAdapter(mAdapter);
    }

    public void refresh(){
        new Thread(new Runnable() {
            public void run(){
                List<CheckItem> mTempItems = mCurrentItems;

                    mTempItems.addAll(mAddedItems);
                mAdapter = new ChecklistOptionsListAdapter(mTempItems, getActivity());
            }
        }).start();
        mRecyclerView.setAdapter(mAdapter);
    }

    public static void strikeThrough(TextView textView) {
        textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
    }
    public static void removeStrikeThrough(TextView textView) {
        textView.setPaintFlags(0);
    }
}
