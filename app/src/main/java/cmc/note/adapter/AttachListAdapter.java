package cmc.note.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cmc.note.R;
import cmc.note.models.Attachment;
import cmc.note.models.Note;

/**
 * Created by tuanb on 27-Nov-16.
 */

public class AttachListAdapter extends RecyclerView.Adapter<AttachListAdapter.ViewHolder> {
    private List<String> mAttachItems;
    private Context mContext;
//    private String mListOrder;

    public AttachListAdapter(List<String> attachments, Context context){
        mAttachItems = attachments;
        mContext = context;
    }

    @Override
    public AttachListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_attach, parent, false);
        return new AttachListAdapter.ViewHolder(rowView);
    }

    @Override
    public void onBindViewHolder(final AttachListAdapter.ViewHolder holder, int position) {
        holder.attach_item_title.setText(mAttachItems.get(position));
    }

    @Override
    public int getItemCount() {
        if (mAttachItems!=null)
            return mAttachItems.size();
        else return 0;
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        final TextView attach_item_title;

        ViewHolder(View itemView) {
            super(itemView);
            attach_item_title = (TextView)itemView.findViewById(R.id.text_view_attach_item_title);
        }
    }

    private static String getReadableModifiedDate(long date){
        return new SimpleDateFormat("MMM dd, yyyy\nh:mm a").format(new Date(date));
    }
}
