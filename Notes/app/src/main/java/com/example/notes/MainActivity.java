package com.example.notes;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.service.notification.StatusBarNotification;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    static ArrayList<String> notes = new ArrayList<>();
    static CustomAdapter customAdapter;
    SharedPreferences sharedPreferences;
    static String CHANNEL_ID = "ca.example.notesNotifcation";
    static int notified = 0;
    static String topOfList = "";
    static NotificationManager mNotificationManager;

    // Magic Number for notification ID
    static int notificationID = 56;

    @Override
    protected void onResume() {
        Log.i("Resume", "it happened");
        super.onResume();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        sharedPreferences = getApplicationContext().getSharedPreferences("com.example.notes", Context.MODE_PRIVATE);

        String stringOfNotes = sharedPreferences.getString("Notes", null);
        String currentTopList;
        if (stringOfNotes == null){
            notes.add("EXAMPLE NOTE");
        } else {
            Log.i("stringArray2", stringOfNotes);
            String[] stringArray = stringOfNotes.split("@@3%");
            if (stringArray.length > 0) {
                currentTopList = stringArray[0];
                Log.i("notified_notified",  " notified is " + Integer.toString(notified) + " current toplist = " + currentTopList + ", topOfList = " + topOfList);

                findNotification();

                if (notified == 0) {
                    Log.i("notified_notified", "notified == 0");
                    topOfList = currentTopList;
                   // createNotificationChannel(this);
                    generateNotification(this);
                    notified = 1;
                } else if (notified == 1) {
                    Log.i("notified_notified", "notified == 1");
                    if (!currentTopList.equals(topOfList)) {
                        Log.i("notified_notified", "currentTopList != topOfList is facts");
                        cancelNotification();
                        topOfList = currentTopList;
                        //createNotificationChannel(this);
                        generateNotification(this);
                    }
                }
            }
        }

        RecyclerView recyclerView = findViewById(R.id.listView);

        customAdapter = new CustomAdapter(notes);

        recyclerView.setAdapter(customAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        notes.clear();
        createNotificationChannel(this);
        setContentView(R.layout.activity_main);
        sharedPreferences = getApplicationContext().getSharedPreferences("com.example.notes", Context.MODE_PRIVATE);

        String stringOfNotes = sharedPreferences.getString("Notes", null);
        Log.i("i_notified", "StringOfNotes is " + stringOfNotes);

        if (stringOfNotes == null){
            notes.add("EXAMPLE NOTE");
            topOfList = notes.get(0);
        } else {
            Log.i("stringArray1", stringOfNotes);
            String[] stringArray = stringOfNotes.split("@@3%");
         //   Log.i("stringArray", stringOfNotes);
            for (int i = 0; i < stringArray.length; i++) {
                notes.add(stringArray[i]);
                Log.i("i_notified", "I is " + Integer.toString(i) + " notified is " + Integer.toString(notified));
                findNotification();
                if (i == 0 && notified == 0) {
                    topOfList = notes.get(i);
                    createNotificationChannel(this);
                    generateNotification(this);
                }
            }
        }

        RecyclerView recyclerView = findViewById(R.id.listView);

        customAdapter = new CustomAdapter(notes);

        recyclerView.setAdapter(customAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_note, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.add_note:
                Intent intent = new Intent(getApplicationContext(), NoteEditingActivity.class);
                startActivity(intent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void findNotification() {
        // Credit https://stackoverflow.com/a/39315744
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        StatusBarNotification[] notifications = mNotificationManager.getActiveNotifications();
        for (StatusBarNotification notification : notifications) {
            if (notification.getId() == 56) {
                notified = 1;
            }
        }
    }

    static void generateNotification(Context ctx) {
        // 10000 is just a magic number for unique number for notifications
        // Might want to use some math trickery to increment it so that each next program can know
        // That the last id of a notification was simply notificationID - 1
        String textTitle;
        String textContent;
        PendingIntent pendingIntent;
        textTitle = "Reminder";
        textContent = topOfList;
        Intent intentForOther = new Intent(ctx.getApplicationContext(), MainActivity.class);
        pendingIntent = PendingIntent.getActivity(ctx, notificationID, intentForOther,0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle(textTitle)
                .setContentText(textContent)
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .setOngoing(true)
                .setContentIntent(pendingIntent)
               .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(ctx);
        notificationManager.notify(notificationID, builder.build());
    }

    static void createNotificationChannel(Context ctx) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channel_name = "Sticky Notification";
            String channel_description = "Top Note to Remember";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, channel_name, importance);
            mChannel.setDescription(channel_description);
            NotificationManager notificationManager = ctx.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(mChannel);
        }
    }

    public void cancelNotification() {
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(notificationID); // Notification ID to cancel
    }
}
