package cmc.note.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import cmc.note.R;

/**
 * Created by tuanb on 10-Oct-16.
 */

public class NotePlainEditorFragment extends Fragment {
    public NotePlainEditorFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_note_plain_editor, container, false);
    }



}
