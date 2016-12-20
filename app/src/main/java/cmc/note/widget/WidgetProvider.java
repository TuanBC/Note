package cmc.note.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

import cmc.note.R;
import cmc.note.activities.NoteEditorActivity;

/**
 * Created by tuanb on 31-Oct-16.
 */

public class WidgetProvider extends AppWidgetProvider {
    public static String ACTION_WIDGET_NOTE = "ActionReceiverNote";
    public static String ACTION_WIDGET_CHECKLIST = "ActionReceiverChecklist";
    public static String ACTION_WIDGET_PHOTO = "ActionReceiverPhoto";

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.xml.widget);

        Intent active = new Intent(context, NoteEditorActivity.class);
        active.setAction(ACTION_WIDGET_NOTE);
        active.putExtra("type", "note");

        PendingIntent actionPendingIntent = PendingIntent.getActivity(context, 0, active, 0);
        remoteViews.setOnClickPendingIntent(R.id.btn_add_note, actionPendingIntent);

//        active = new Intent(context, NoteEditorActivity.class);
//        active.setAction(ACTION_WIDGET_CHECKLIST);
//        active.putExtra("type", "checklist");
//        actionPendingIntent = PendingIntent.getActivity(context, 0, active, 0);
//        remoteViews.setOnClickPendingIntent(R.id.btn_add_checklist, actionPendingIntent);
//
//        active = new Intent(context, NoteEditorActivity.class);
//        active.setAction(ACTION_WIDGET_PHOTO);
//        active.putExtra("type", "photo");
//        actionPendingIntent = PendingIntent.getBroadcast(context, 0, active, 0);
//        remoteViews.setOnClickPendingIntent(R.id.btn_add_photo, actionPendingIntent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.xml.widget);

        Intent active = new Intent(context, NoteEditorActivity.class);
//        active.setAction(ACTION_WIDGET_NOTE);
        active.putExtra("type", "note");
        PendingIntent actionPendingIntent = PendingIntent.getActivity(context, 0, active, PendingIntent.FLAG_CANCEL_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.btn_add_note, actionPendingIntent);

        active = new Intent(context, NoteEditorActivity.class);
        active.setAction(ACTION_WIDGET_CHECKLIST);
        active.putExtra("type", "checklist");
        actionPendingIntent = PendingIntent.getActivity(context, 0, active, PendingIntent.FLAG_CANCEL_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.btn_add_checklist, actionPendingIntent);
//
//        active = new Intent(context, NoteEditorActivity.class);
//        active.setAction(ACTION_WIDGET_PHOTO);
//        active.putExtra("type", "photo");
//        actionPendingIntent = PendingIntent.getBroadcast(context, 0, active, 0);
//        remoteViews.setOnClickPendingIntent(R.id.btn_add_photo, actionPendingIntent);

        appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ACTION_WIDGET_NOTE)) {
            Log.i("onReceive", ACTION_WIDGET_NOTE);
        } else if (intent.getAction().equals(ACTION_WIDGET_CHECKLIST)) {
            Log.i("onReceive", ACTION_WIDGET_CHECKLIST);
        } else if (intent.getAction().equals(ACTION_WIDGET_PHOTO)) {
            Log.i("onReceive", ACTION_WIDGET_PHOTO);
        } else {
            super.onReceive(context, intent);
        }
    }
}
