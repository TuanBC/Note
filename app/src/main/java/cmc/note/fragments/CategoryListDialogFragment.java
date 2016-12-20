package cmc.note.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import cmc.note.R;
import cmc.note.activities.MainActivity;
import cmc.note.activities.NoteEditorActivity;
import cmc.note.data.CategoryManager;
import cmc.note.data.NoteManager;
import cmc.note.models.Category;
import cmc.note.models.Note;

/**
 * Created by tuanb on 14-Nov-16.
 */

public class CategoryListDialogFragment extends DialogFragment {
    CharSequence list_options[];
    MainActivity mActivity;

    //WORKAROUND TO CALL RESUME
    public void setActivity(MainActivity a){
        this.mActivity = a;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Bundle mArgs = getArguments();
        final long cat_id = mArgs.getLong("id");
        if (cat_id==CategoryManager.newInstance(mActivity).getFirstCategory().getId()){
            list_options = new CharSequence[] {"Edit title"};
        } else list_options = new CharSequence[] {"Edit title", "Delete"};

//        final String list_order = mArgs.getString("list_order");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Choose an Option")
                .setItems(list_options, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i("Dialog","which = "+which);
                        switch (which){
                            case 0:
                                final Category temp_category = CategoryManager.newInstance(getActivity()).getCategory(cat_id);

                                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());

                                final EditText input = new EditText(getActivity());
                                input.setText(temp_category.getTitle());
                                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.MATCH_PARENT,
                                        LinearLayout.LayoutParams.MATCH_PARENT);
                                input.setLayoutParams(lp);

                                alertDialog.setView(input); // uncomment this line
                                alertDialog.setTitle("Enter new title ");
                                alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String cat_title = input.getText().toString();
                                        if (cat_title.equals("")){
                                            makeToast("Title unchanged");
                                            dialog.dismiss();
                                        }
                                        else {
                                            temp_category.setTitle(cat_title);
                                            CategoryManager.newInstance(getActivity()).update(temp_category);
//                                              getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, getActivity().getIntent());

                                            //WORKAROUND TO CALL RESUME
                                            FragmentManager fragmentManager = mActivity.getSupportFragmentManager();
                                            CategoryListFragment myParentFragment = (CategoryListFragment) fragmentManager.findFragmentById(R.id.container);
                                            myParentFragment.onResume();

                                            dialog.dismiss();
                                        }
                                    }
                                });
                                alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                alertDialog.show();
                                break;
                            case 1:
                                Category temp_cat = CategoryManager.newInstance(getActivity()).getCategory(cat_id);
                                CategoryManager.newInstance(getActivity()).delete(temp_cat.getId());
                                FragmentManager fragmentManager = mActivity.getSupportFragmentManager();
                                CategoryListFragment myParentFragment = (CategoryListFragment) fragmentManager.findFragmentById(R.id.container);
                                myParentFragment.onResume();
                                break;
                        }
                    }
                });

        AlertDialog alert = builder.create();

        return alert;
    }

    private void makeToast(String s) {
        Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
    }
}
