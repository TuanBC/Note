package cmc.note.fragments;

import android.app.Activity;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.List;

import cmc.note.R;
import cmc.note.activities.MainActivity;
import cmc.note.activities.NoteEditorActivity;
import cmc.note.adapter.CategoryListAdapter;
import cmc.note.adapter.CategoryListAdapter;
import cmc.note.data.CategoryManager;
import cmc.note.data.CategoryManager;
import cmc.note.data.NoteManager;
import cmc.note.models.Category;
import cmc.note.models.CustomSearchView;
import cmc.note.models.Note;


/**
 * Created by tuanb on 10-Oct-16.
 */

public class CategoryListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, SearchView.OnQueryTextListener{
    private View mRootView;
    private List<Category> mCategories;
    private RecyclerView mRecyclerView;
    private CategoryListAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private CategoryListAdapter tempAdapter;
    private String mListOrder;
    private String catListOrder;
    private MainActivity mActivity;

    private final String TAG = "CAT LIST Fragment";

    public CategoryListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment and hold the reference in mRootView
        mRootView = inflater.inflate(R.layout.fragment_category_list, container, false);
        setupList();

        mRootView.findViewById(R.id.btn_floating).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                promptToAddCategory();
            }
        });

        View view = getActivity().findViewById(R.id.buttons);
        view.setVisibility(View.GONE);
        view = getActivity().findViewById(R.id.btn_set_order);
        view.setVisibility(View.GONE);

        return mRootView;
    }

    private void promptToAddCategory() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mActivity);

        final EditText input = new EditText(mActivity);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);

        alertDialog.setView(input); // uncomment this line
        alertDialog.setTitle("Enter title of the category ");
        alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String category_title = input.getText().toString();
                if (category_title.equals("")){dialog.dismiss();}
                else {
                    Category category = new Category();
                    category.setTitle(category_title);
                    CategoryManager.newInstance(mActivity).create(category);

                    FragmentManager fragmentManager = mActivity.getSupportFragmentManager();
                    CategoryListFragment myParentFragment = (CategoryListFragment) fragmentManager.findFragmentById(R.id.container);
                    myParentFragment.onResume();
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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (MainActivity) context;
    }

    @Override
    public void onPause() {
        View view = getActivity().findViewById(R.id.buttons);
        view.setVisibility(View.VISIBLE);
        view = getActivity().findViewById(R.id.btn_set_order);
        view.setVisibility(View.VISIBLE);
//        makeToast("PAUSE");
        super.onPause();
    }


    @Override
    public void onResume() {
        View view = getActivity().findViewById(R.id.buttons);
        view.setVisibility(View.GONE);
        view = getActivity().findViewById(R.id.btn_set_order);
        view.setVisibility(View.GONE);
        mCategories = CategoryManager.newInstance(getActivity()).getAllCategoriesSortedBy(catListOrder);
        mAdapter = new CategoryListAdapter(mCategories, getActivity());
        mRecyclerView.setAdapter(mAdapter);
//        makeToast("RESUMED");
        super.onResume();
    }

    private void setupList() {
        mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.list_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mCategories = CategoryManager.newInstance(getActivity()).getAllCategoriesSortedBy(catListOrder);
        mAdapter = new CategoryListAdapter(mCategories, getActivity());
        mRecyclerView.setAdapter(mAdapter);

        mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(),
                mRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {
                //Values are passing to activity & to fragment as well
                //OPEN DIALOG TO SELECT CATEGORY

                Category selectedCategory = mCategories.get(position);

                Bundle args = new Bundle();
                args.putLong("cat_id", selectedCategory.getId());
                args.putString("list_order", mListOrder);

                NoteListFragment newFragment = new NoteListFragment();
                newFragment.setArguments(args);

                mActivity.openFragment(newFragment, selectedCategory.getTitle());
            }

            @Override
            public void onLongItemClick(View view, int position) {
                confirmDialog(position);
            }
        }));
    }

    public void confirmDialog(int position) {
        Category selectedCategory = mCategories.get(position);

        Bundle args = new Bundle();
        args.putLong("id", selectedCategory.getId());
        args.putString("cat_list_order", catListOrder);


        CategoryListDialogFragment newFragment = new CategoryListDialogFragment();
        newFragment.setArguments(args);
        newFragment.setActivity(mActivity);
//        newFragment.setTargetFragment(this, 1);
        newFragment.show(getFragmentManager().beginTransaction(), "CATEGORY OPTIONS");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case 1:
                if (resultCode == Activity.RESULT_OK) {
                    this.onRefresh();
                }
                break;
        }
    }

    //ADD MENU
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
//        inflater.inflate(R.menu.menu_main, menu);

//        MenuItem item = menu.getItem(1);
//        item.setIcon(new IconicsDrawable(getActivity())
//                .icon(FontAwesome.Icon.faw_search)
//                .color(Color.BLACK)
//                .sizeDp(20));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_search:
                setupSearchBox(item);
                break;
            case R.id.action_exit:
                promptToExit();
                break;
            case R.id.action_sort:
                promptToSort();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupSearchBox(MenuItem item) {
        MenuItemCompat.setOnActionExpandListener(item,
                new MenuItemCompat.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionExpand(MenuItem item) {
                        mRecyclerView.setAdapter(tempAdapter);
                        View view = getActivity().findViewById(R.id.buttons);
                        view.setVisibility(View.GONE);
                        view = getActivity().findViewById(R.id.btn_set_order);
                        view.setVisibility(View.GONE);
                        return true; // KEEP IT TO TRUE OR IT DOESN'T OPEN !!
                    }
                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem item) {
                        mRecyclerView.setAdapter(mAdapter);
                        View view = getActivity().findViewById(R.id.buttons);
                        view.setVisibility(View.VISIBLE);
                        view = getActivity().findViewById(R.id.btn_set_order);
                        view.setVisibility(View.VISIBLE);
                        mCategories = CategoryManager.newInstance(getActivity()).getAllCategoriesSortedBy(catListOrder);
                        return true; // OR FALSE IF YOU DIDN'T WANT IT TO CLOSE!
                    }
                });

        CustomSearchView searchView = (CustomSearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);

        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(
                new ComponentName(getActivity(), MainActivity.class)));//???????????????????????????????????????????
    }

    @Override
    public boolean onQueryTextSubmit(String input) {
        List<Category> temp_categories;
        temp_categories = CategoryManager.newInstance(getActivity()).getAllCategoriesSortedBy(input);
        tempAdapter = new CategoryListAdapter(temp_categories, getActivity());
        mCategories=temp_categories;
        mRecyclerView.setAdapter(tempAdapter);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String input) {
        final List<Category> temp_categories;
        temp_categories = CategoryManager.newInstance(getActivity()).getAllCategoriesSortedBy(input);
        tempAdapter = new CategoryListAdapter(temp_categories, getActivity());
        mCategories=temp_categories;
        mRecyclerView.setAdapter(tempAdapter);
        return false;
    }

    private void promptToSort() {
        String[] array_sort = {"Title", "Note count"};
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle(R.string.sort)
                .setItems(array_sort, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:
                                catListOrder = "abc_asc";
                                break;
                            case 1:
                                catListOrder = "created_desc";
                                break;
                        }
                        mCategories = CategoryManager.newInstance(getActivity()).getAllCategoriesSortedBy(catListOrder);
                        mAdapter = new CategoryListAdapter(mCategories, getActivity());
                        setupList();
                    }
                });
        alertDialog.show();
    }

    private void promptToExit() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
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

    @Override
    public void onRefresh() {
        setupList();
    }

    private void makeToast(String s) {
        Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
    }
}