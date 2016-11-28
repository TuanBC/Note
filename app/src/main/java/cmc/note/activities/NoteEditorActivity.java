package cmc.note.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import cmc.note.R;
import cmc.note.data.CategoryManager;
import cmc.note.data.NoteManager;
import cmc.note.fragments.ChecklistEditorFragment;
import cmc.note.fragments.NoteLinedEditorFragment;
import cmc.note.models.Note;

/**
 * Created by tuanb on 10-Oct-16.
 */

public class NoteEditorActivity extends MainActivity {
    private Toolbar mToolbar;
    private Note mCurrentNote;
    private String mListOrder;

    private final String TAG = "NOTEEDITOR ACTIVITY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_editor);

        mToolbar = (Toolbar)findViewById(R.id.toolbar);
        if (mToolbar != null)
           setSupportActionBar(mToolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);    //backbutton

        if (savedInstanceState == null){
            Bundle mArgs = getIntent().getExtras();
            if (mArgs != null) {
                this.mListOrder=mArgs.getString("list_order");
                Log.i(TAG, "get order " + mListOrder);
                if (mArgs.containsKey("id")) { //if note existed
                    long id = mArgs.getLong("id", 0);
                    if (id > 0) { //OPEN SPECIFIC FRAGMENT WITH GIVEN ID
                        mCurrentNote = NoteManager.newInstance(this).getNote(id);

                        if (mCurrentNote.getType().equals("checklist")) {
                            ChecklistEditorFragment f = ChecklistEditorFragment.newInstance(id);
                            f.setListOrder(mListOrder);
                            openFragment(f, "Checklist");
                        } else if (mCurrentNote.getType().equals("note")) {
                            NoteLinedEditorFragment f = NoteLinedEditorFragment.newInstance(id);
                            f.setListOrder(mListOrder);
                            openFragment(f, "Editor");
                        }
                    }
                } else if (mArgs.containsKey("type")) {
                    String type = mArgs.getString("type");

                    if (type.equals("note")) {
                        Note note = new Note();
                        note.setTitle("");
                        note.setType("note");
                        note.setCategoryId(CategoryManager.newInstance(this).getFirstCategory().getId());
                        note.setContent("");
                        NoteManager.newInstance(this).create(note);
                        mCurrentNote = NoteManager.newInstance(this).getLastNote();

                        NoteLinedEditorFragment f = NoteLinedEditorFragment.newInstance(0);

                        f.setListOrder(mListOrder);
                        openFragment(f, "Editor");
                    } else if (type.equals("checklist")) {
                        if (mArgs.containsKey("checklist_id")) {
                            long checklist_id = mArgs.getLong("checklist_id", 0);
                            ChecklistEditorFragment f = ChecklistEditorFragment.newInstance(checklist_id);
                            f.setListOrder(mListOrder);
                            openFragment(f, "Checklist");
                        } else {
                            Note note = new Note();
                            note.setTitle("");
                            note.setType("checklist");
                            note.setCategoryId(CategoryManager.newInstance(this).getFirstCategory().getId());
                            note.setContent("");
                            NoteManager.newInstance(this).create(note);
                            mCurrentNote = NoteManager.newInstance(this).getLastNote();

                            ChecklistEditorFragment f = ChecklistEditorFragment.newInstance(0);
                            f.setListOrder(mListOrder);
                            openFragment(f, "Checklist");
                        }
//                        long checklist_id = args.getLong("checklist_id", 0);
//                        openFragment(ChecklistEditorFragment.newInstance(checklist_id), "Checklist");
                    }
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (mCurrentNote.getType().equals("note")) {
            NoteLinedEditorFragment fragment = (NoteLinedEditorFragment) getSupportFragmentManager().findFragmentById(R.id.container);
            fragment.onBackClicked();
        } else if (mCurrentNote.getType().equals("checklist")) {
            ChecklistEditorFragment fragment = (ChecklistEditorFragment) getSupportFragmentManager().findFragmentById(R.id.container);
            fragment.onBackClicked();
        }
    }

    public void openFragment(final Fragment fragment, String title){
        getSupportFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.container, fragment)
                .addToBackStack(null)
                .commit();
        getSupportActionBar().setTitle(title);
    }

    public void onClickListener(View v) {
        switch (v.getId()){
            case R.id.btn_add_checkItem:
                ChecklistEditorFragment fragment = (ChecklistEditorFragment) getSupportFragmentManager().findFragmentById(R.id.container);
                fragment.promptToAddItem();
        }
    }

    private void makeToast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }
}
