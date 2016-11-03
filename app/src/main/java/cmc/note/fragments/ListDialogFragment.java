package cmc.note.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.MenuItem;
import android.widget.AdapterView;

import cmc.note.R;
import cmc.note.activities.NoteEditorActivity;
import cmc.note.adapter.NoteListAdapter;
import cmc.note.data.NoteManager;
import cmc.note.models.Note;

/**
 * Created by tuanb on 19-Oct-16.
 */

public class ListDialogFragment extends DialogFragment {
    CharSequence list_options[] = new CharSequence[] {"Edit", "Delete"};

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Bundle mArgs = getArguments();
        final long note_id = mArgs.getLong("id");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Choose an Option")
                .setItems(list_options, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i("Dialog","which = "+which);
                        switch (which){
                            case 0:
                                Intent editorIntent = new Intent(getActivity(), NoteEditorActivity.class);
                                editorIntent.putExtra("bundle", mArgs);

                                startActivity(editorIntent);
                                break;
                            case 1:
                                Note temp_note = NoteManager.newInstance(getActivity()).getNote(note_id);
                                NoteManager.newInstance(getActivity()).delete(temp_note);
                                Activity a = getActivity();
                                a.finish();
                                a.overridePendingTransition(0, 0);
                                a.startActivity(a.getIntent());
                                a.overridePendingTransition(0, 0);
                                break;
                        }
                    }
                });
        return builder.create();
    }
}
