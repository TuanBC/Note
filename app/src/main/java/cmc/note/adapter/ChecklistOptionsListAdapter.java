package cmc.note.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import cmc.note.R;
import cmc.note.models.CheckItem;

/**
 * Created by tuanb on 21-Oct-16.
 */

public class ChecklistOptionsListAdapter extends RecyclerView.Adapter<ChecklistOptionsListAdapter.ViewHolder> {

    private List<CheckItem> mCheckItems;
    private Context mContext;

    public ChecklistOptionsListAdapter(List<CheckItem> checkItems, Context context){
        mCheckItems = checkItems;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_check_item, parent, false);
        return new ViewHolder(rowView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.itemName.setText(mCheckItems.get(position).getName());
        if (mCheckItems.get(position).getStatus()) {
            holder.itemName.setTextColor(Color.GRAY);
            holder.itemName.setPaintFlags(holder.itemName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else{
            holder.itemName.setPaintFlags(0);
            holder.itemName.setTextColor(Color.BLACK);
        }
    }

    @Override
    public int getItemCount() {
        return mCheckItems.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        final TextView itemName;
        ViewHolder(View itemView) {
            super(itemView);
            itemName = (TextView)itemView.findViewById(R.id.text_view_item_name);
        }
    }



}
