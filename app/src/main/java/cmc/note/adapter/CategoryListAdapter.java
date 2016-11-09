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
        holder.noteTitle.setText(mCategories.get(position).getTitle());
        holder.noteModifiedDate.setText(getReadableModifiedDate(mCategories.get(position).getDateModified()));
    }

    @Override
    public int getItemCount() {
        return mCategories.size();
    }
    
    class ViewHolder extends RecyclerView.ViewHolder {
        final TextView noteTitle, noteModifiedDate;
        final ImageView noteType;

        ViewHolder(View itemView) {
            super(itemView);
            noteTitle = (TextView)itemView.findViewById(R.id.text_view_note_title);
            noteModifiedDate = (TextView)itemView.findViewById(R.id.text_view_note_date);
            noteType = (ImageView)itemView.findViewById(R.id.image_view_note_type);
        }
    }

    private static String getReadableModifiedDate(long date){
        return new SimpleDateFormat("MMM dd, yyyy\nh:mm a").format(new Date(date));
    }
}
