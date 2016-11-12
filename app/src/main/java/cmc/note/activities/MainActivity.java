package cmc.note.activities;

import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.context.IconicsLayoutInflater;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;
import com.mikepenz.materialdrawer.util.KeyboardUtil;

import cmc.note.R;
import cmc.note.data.DatabaseHelper;
import cmc.note.data.NoteManager;
import cmc.note.fragments.NoteListFragment;
import cmc.note.models.Note;

public class MainActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private String mListOrder;
    private final String TAG = "MAIN ACTIVITY";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LayoutInflaterCompat.setFactory(getLayoutInflater(), new IconicsLayoutInflater(getDelegate()));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(this);
        databaseHelper.getWritableDatabase();

        //ADD COMMAND TO GET mListOrder
        mToolbar = (Toolbar)findViewById(R.id.toolbar);
        if (mToolbar != null)
           setSupportActionBar(mToolbar);

        if (savedInstanceState == null){
            Bundle mArgs = getIntent().getExtras();
            if (mArgs!=null) {
                mListOrder = mArgs.getString("list_order");
                Log.i (TAG, "get order = "+mListOrder);
            }
            NoteListFragment fragment = new NoteListFragment();
            fragment.setListOrder(mListOrder);
            openFragment(fragment, "Note List");
        }

        findViewById(R.id.btn_add_note).setOnClickListener(button_listener);
        findViewById(R.id.btn_add_checklist).setOnClickListener(button_listener);
        findViewById(R.id.btn_add_photo).setOnClickListener(button_listener);
        Button sort_button = (Button)findViewById(R.id.btn_set_order);
        sort_button.setOnClickListener(button_listener);
        if (mListOrder==null) {
            Log.i(TAG," didn't receive list order");
            mListOrder = "modified_desc";
        }

        switch (mListOrder){
            case "abc_asc":
                sort_button.setText("Sorted by alphabetical");
                break;
            case "created_desc":
                sort_button.setText("Sorted by created time");
                break;
            default:
                sort_button.setText("Sorted by modified time");
                break;
        }

        //Now build the navigation drawer and pass the AccountHeader
        new DrawerBuilder()
                .withActivity(this)
                .withToolbar(mToolbar)
                .withActionBarDrawerToggle(true)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.title_home).withIcon(FontAwesome.Icon.faw_sticky_note).withIdentifier(1),
                        new PrimaryDrawerItem().withName(R.string.title_setting).withIcon(FontAwesome.Icon.faw_cog).withIdentifier(2)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int i, IDrawerItem drawerItem) {
                        if (drawerItem != null && drawerItem instanceof Nameable) {
                            String name = ((Nameable) drawerItem).getName().getText(MainActivity.this);
                            mToolbar.setTitle(name);
                        }

                        if (drawerItem != null) {
                            int selectedScren = (int) drawerItem.getIdentifier();
                            switch (selectedScren) {
                                case 1:
                                    //nothing
                                    break;
                                case 2:
                                    //go to settings screen
                                    Intent intent = new Intent(MainActivity.this, SettingActivity.class);
//                                    intent.putExtra("type", "note");
//                                    intent.putExtra("list_order", mListOrder);
                                    startActivity(intent);
                                    Toast.makeText(MainActivity.this, "Settings Clicked", Toast.LENGTH_SHORT).show();
                                    break;
                            }
                        }
                        return false;
                    }


                })
                .withOnDrawerListener(new Drawer.OnDrawerListener() {
                    @Override
                    public void onDrawerOpened(View view) {
                        KeyboardUtil.hideKeyboard(MainActivity.this);
                    }

                    @Override
                    public void onDrawerClosed(View view) {

                    }

                    @Override
                    public void onDrawerSlide(View view, float v) {

                    }
                })
                .withFireOnInitialOnClick(true)
                .withSavedInstance(savedInstanceState)
                .build();
    }

    @Override
    public void onBackPressed() {
        promptToExit();
    }

    private void promptToExit() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
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

    private View.OnClickListener button_listener = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btn_add_note:
                    Intent editorIntent = new Intent(MainActivity.this, NoteEditorActivity.class);
                    editorIntent.putExtra("type", "note");
                    editorIntent.putExtra("list_order", mListOrder);
                    Log.i(TAG, "add note clicked");
                    startActivity(editorIntent);
                    break;
                case R.id.btn_add_checklist:
                    promptToAddChecklistNote();
//                    editorIntent.putExtra("type", "checklist");
//                    startActivity(editorIntent);
                    break;
                case R.id.btn_set_order:
                    promptToSort();
                    break;
            }
        }
    };

    private void promptToSort() {
        final Button sort_button = (Button)findViewById(R.id.btn_set_order);
        String[] array_sort = {"Alphabetical", "Created time", "Modified time"};
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(R.string.sort)
                .setItems(array_sort, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:
                                mListOrder = "abc_asc";
                                sort_button.setText("Sorted by alphabetical");
                                break;
                            case 1:
                                mListOrder = "created_desc";
                                sort_button.setText("Sorted by created time");
                                break;
                            case 2:
                                mListOrder = "modified_desc";
                                sort_button.setText("Sorted by modified time");
                                break;
                        }

//                        Intent mIntent = getIntent();
//                        mIntent.putExtra("list_order", mListOrder);
//                        mIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//                        finish();
//                        startActivity(mIntent);
                        NoteListFragment fragment = new NoteListFragment();
                        fragment.setListOrder(mListOrder);
                        openFragment(fragment, "Note List");
                    }
                });
        alertDialog.show();
    }

    private void promptToAddChecklistNote() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        final EditText input = new EditText(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);

        alertDialog.setView(input); // uncomment this line
        alertDialog.setTitle("Enter title of the checklist ");
        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String note_title = input.getText().toString();
                if (note_title.equals("")){dialog.dismiss();}
                else {
                    Note note = new Note();
                    note.setTitle(note_title);
                    note.setType("checklist");
                    note.setContent("");
                    NoteManager.newInstance(MainActivity.this).create(note);
                    Note temp_note = NoteManager.newInstance(MainActivity.this).getNoteByTitle(note_title);

                    Intent intent = new Intent(MainActivity.this, NoteEditorActivity.class);

//                    Bundle args = new Bundle();
//                    args.putLong("id", temp_note.getId());
//                    args.putString("type","checklist");
//                    args.putString("list_order", mListOrder);

                    intent.putExtra("checklist_id", temp_note.getId());
                    intent.putExtra("type","checklist");
                    intent.putExtra("list_order", mListOrder);
                    startActivity(intent);
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

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Toast.makeText(this, "Searching by: "+ query, Toast.LENGTH_SHORT).show();

        } else if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            String uri = intent.getDataString();
            Toast.makeText(this, "Suggestion: "+ uri, Toast.LENGTH_SHORT).show();
        }
    }
}
