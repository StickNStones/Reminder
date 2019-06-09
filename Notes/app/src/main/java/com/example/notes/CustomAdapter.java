package com.example.notes;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

    private ArrayList<String> noteArray;
    private Context thisContext;


    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView note;

        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.text_view);
            note = (TextView) itemView.findViewById(R.id.moreText);
        }
    }

    public CustomAdapter(ArrayList<String> reminders) {
        noteArray = reminders;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.list_items, viewGroup, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final CustomAdapter.ViewHolder viewHolder, final int position){
        Log.i("position" , Integer.toString(position));

        String note = noteArray.get(position);

        TextView textView = viewHolder.title;
        TextView noteView = viewHolder.note;
        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
        Date date = new Date(System.currentTimeMillis());
        System.out.println(formatter.format(date));
        textView.setText(note);
        noteView.setText(date.toString());

        viewHolder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Log.i("clickedon", Integer.toString(position));
                Intent intent = new Intent(v.getContext(), NoteEditingActivity.class);
                intent.putExtra("noteID", position);
                v.getContext().startActivity(intent);

            }

        });
        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final Context context = v.getContext();
                new AlertDialog.Builder(v.getContext())
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Delete Reminder")
                        .setMessage("Do you want to delete this note?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                // If the one to delete is the current note that is in the notification
                                // Notification must be deleted and a new one started
                                if (MainActivity.notes.get(position).equals(MainActivity.topOfList)){
                                    MainActivity.mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                                    MainActivity.mNotificationManager.cancel(MainActivity.notificationID);

                                    if (MainActivity.notes.size() > 1) {
                                        MainActivity.topOfList = MainActivity.notes.get(position + 1);
                                        MainActivity.notes.remove(position);
                                        MainActivity.customAdapter.notifyDataSetChanged();
                                        MainActivity.generateNotification(context);
                                    } else {
                                        MainActivity.notes.remove(position);
                                        MainActivity.customAdapter.notifyDataSetChanged();
                                    }

                                } else {
                                    MainActivity.notes.remove(position);
                                    MainActivity.customAdapter.notifyDataSetChanged();
                                }

                                NoteEditingActivity.saveNotesSharedPreferences(context);

                            }
                        })
                        .setNegativeButton("No", null)
                        .show();


                // false doesn't allow it to happen
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return noteArray.size();
    }

}
