package cmc.note.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cmc.note.R;
import cmc.note.models.Category;

/**
 * Created by tuanb on 07-Nov-16.
 */

public class CategoryListAdapter extends RecyclerView.Adapter<CategoryListAdapter.ViewHolder>{
    private List<Category> mCategories;
    private Context mContext;
//    private String mListOrder;

    public CategoryListAdapter(List<Category> categories, Context context){
        mCategories = categories;
        mContext = context;
    }

    @Override
    public CategoryListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_category, parent, false);
        return new CategoryListAdapter.ViewHolder(rowView);
    }

    @Override
    public void onBindViewHolder(final CategoryListAdapter.ViewHolder holder, int position) {
        holder.categoryTitle.setText(mCategories.get(position).getTitle());
        if (mCategories.get(position).getNoteCount() == 1)
            holder.noteCount.setText(mCategories.get(position).getNoteCount().toString()+" note");
        else holder.noteCount.setText(mCategories.get(position).getNoteCount().toString()+" notes");
    }

    @Override
    public int getItemCount() {
        return mCategories.size();
    }
    
    class ViewHolder extends RecyclerView.ViewHolder {
        final TextView categoryTitle, noteCount;

        ViewHolder(View itemView) {
            super(itemView);
            categoryTitle = (TextView)itemView.findViewById(R.id.text_view_category_title);
            noteCount = (TextView)itemView.findViewById(R.id.text_view_note_count);
        }
    }

    private static String getReadableModifiedDate(long date){
        return new SimpleDateFormat("MMM dd, yyyy").format(new Date(date));
    }
}
