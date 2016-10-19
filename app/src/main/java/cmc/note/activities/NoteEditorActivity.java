package cmc.note.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import cmc.note.R;
import cmc.note.fragments.NoteLinedEditorFragment;
import cmc.note.fragments.NotePlainEditorFragment;

/**
 * Created by tuanb on 10-Oct-16.
 */

public class NoteEditorActivity extends MainActivity {
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_editor);

        mToolbar = (Toolbar)findViewById(R.id.toolbar);
        if (mToolbar != null)
           setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Log.i("LOG noteEditActivity","NEA started");

        if (savedInstanceState == null){
            Bundle args = getIntent().getExtras();
            if (args != null && args.containsKey("id")){
                long id = args.getLong("id", 0);

                Log.i("Note Editor", "got_index="+id);

                if (id > 0){ //OPEN SPECIFIC FRAGMENT WITH GIVEN ID
                    NoteLinedEditorFragment fragment = NoteLinedEditorFragment.newInstance(id);
                    openFragment(fragment, "Editor");
                }
            }
            else {
                Log.i("Note Editor", "dont got_index");
                openFragment(NoteLinedEditorFragment.newInstance(0), "Editor");
            }
        }
    }

    @Override
    public void onBackPressed() {
        finish();
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
}
