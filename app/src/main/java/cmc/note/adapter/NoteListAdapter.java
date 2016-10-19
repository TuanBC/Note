package cmc.note.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cmc.note.R;
import cmc.note.activities.MainActivity;
import cmc.note.activities.NoteEditorActivity;
import cmc.note.models.Note;

import static android.support.v4.app.ActivityCompat.startActivity;

/**
* Created by tuanb on 10-Oct-16.
*/

public class NoteListAdapter extends RecyclerView.Adapter<NoteListAdapter.ViewHolder>{

    private List<Note> mNotes;
    private Context mContext;

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
        holder.noteModifyDate.setText(getReadableModifiedDate(mNotes.get(position).getDateModified()));
    }

    @Override
    public int getItemCount() {
        return mNotes.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView noteTitle, noteModifyDate;

        public ViewHolder(View itemView) {
            super(itemView);
            noteTitle = (TextView)itemView.findViewById(R.id.text_view_note_title);
            noteModifyDate = (TextView)itemView.findViewById(R.id.text_view_note_date);

//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Log.d("log holder", getAdapterPosition() + "position clicked.");
//
//                    long position = getAdapterPosition();
//                    Intent editorIntent = new Intent(mContext, NoteEditorActivity.class);
//                    editorIntent.putExtra("id", position);
//
//
//                    mContext.startActivity(editorIntent);
//                }
//            });
//
//            itemView.setOnLongClickListener(new View.OnLongClickListener() {
//
//                @Override
//                public boolean onLongClick(View view) {
//                    Log.d("holder", "Element " + getAdapterPosition() + " long clicked.");
//                    return true;
//                }
//            });
        }

    }

    private static String getReadableModifiedDate(long date){
        return new SimpleDateFormat("MMM dd, yyyy - h:mm a").format(new Date(date));
    }

}
