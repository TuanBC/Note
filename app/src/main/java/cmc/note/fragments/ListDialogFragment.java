package cmc.note.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.MenuItem;
import android.widget.AdapterView;

import cmc.note.R;
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
        Log.i("Dialog","which = ");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Choose and Option")
                .setItems(list_options, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i("Dialog","which = "+which);
//                        switch (which){
//                            case ("Edit"):
//                        }
                    }
                });
        return builder.create();
    }
}
