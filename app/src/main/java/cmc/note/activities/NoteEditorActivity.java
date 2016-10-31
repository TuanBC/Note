package cmc.note.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import cmc.note.R;
import cmc.note.data.ChecklistManager;
import cmc.note.data.NoteManager;
import cmc.note.fragments.ChecklistEditorFragment;
import cmc.note.fragments.NoteLinedEditorFragment;
import cmc.note.models.CheckItem;
import cmc.note.models.Note;

/**
 * Created by tuanb on 10-Oct-16.
 */

public class NoteEditorActivity extends MainActivity {
    private Toolbar mToolbar;
    private Note mCurrentNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_editor);



        mToolbar = (Toolbar)findViewById(R.id.toolbar);
        if (mToolbar != null)
           setSupportActionBar(mToolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);    //backbutton

        if (savedInstanceState == null){
            Bundle args = getIntent().getExtras();
            if (args != null) {
                if (args.containsKey("id")) {
                    long id = args.getLong("id", 0);

                    if (id > 0) { //OPEN SPECIFIC FRAGMENT WITH GIVEN ID
                        mCurrentNote = NoteManager.newInstance(this).getNote(id);

                        if (mCurrentNote.getType().equals("checklist")) {
                            openFragment(ChecklistEditorFragment.newInstance(id), "Checklist");
                        } else if (mCurrentNote.getType().equals("note")) {
                            openFragment(NoteLinedEditorFragment.newInstance(id), "Editor");
                        }
                    }
                } else if (args.containsKey("type")) {
                    String type = args.getString("type", "");


                    if (type.equals("note"))
                        openFragment(NoteLinedEditorFragment.newInstance(0), "Editor");
                    else if (type.equals("checklist")) {
                        long checklist_id = args.getLong("checklist_id", 0);
                        openFragment(ChecklistEditorFragment.newInstance(checklist_id), "Checklist");
                    }
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (mCurrentNote==null) this.finish();
        else {
            if (mCurrentNote.getType().equals("note")) {
                NoteLinedEditorFragment fragment = (NoteLinedEditorFragment) getSupportFragmentManager().findFragmentById(R.id.container);
                fragment.onBackClicked();
            } else if (mCurrentNote.getType().equals("checklist")) {
                ChecklistEditorFragment fragment = (ChecklistEditorFragment) getSupportFragmentManager().findFragmentById(R.id.container);
                fragment.onBackClicked();
            }
        }
    }

    private void openFragment(final Fragment fragment, String title){
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
}
