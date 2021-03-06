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
import cmc.note.models.Note;

/**
* Created by tuanb on 10-Oct-16.
*/

public class NoteListAdapter extends RecyclerView.Adapter<NoteListAdapter.ViewHolder>{
    private List<Note> mNotes;
    private Context mContext;
//    private String mListOrder;

    public NoteListAdapter(List<Note> notes, Context context){
        mNotes = notes;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rowView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_note_list, parent, false);
        return new ViewHolder(rowView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.noteTitle.setText(mNotes.get(position).getTitle());
        holder.noteModifiedDate.setText(getReadableModifiedDate(mNotes.get(position).getDateModified()));
        if (mNotes.get(position).getType().equals("checklist")){
            holder.noteType.setImageResource(R.drawable.ic_check);
        }
    }

    @Override
    public int getItemCount() {
        return mNotes.size();
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
