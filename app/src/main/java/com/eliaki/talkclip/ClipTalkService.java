package com.eliaki.talkclip;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;

import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.Date;

/**
 * Created by eliyahuakiniyazov on 18/04/2016.
 */
public class ClipTalkService extends Service {

    BroadcastReceiver receiver;

    //private NotificationManager mNotificationManager;
    private ClipboardManager mClipboardManager;

    static boolean bHasClipChangedListener = false;
    ClipboardManager.OnPrimaryClipChangedListener mPrimaryClipChangedListener = new ClipboardManager.OnPrimaryClipChangedListener() {
        public void onPrimaryClipChanged() {
            ClipData clipData = mClipboardManager.getPrimaryClip();
            CharSequence cs =clipData.getItemAt(0).getText();
            if (cs!=null) {
                dupliCatcher(cs.toString());

            }
        }

    };
    private NotificationManager mNotificationManager;

    private void dupliCatcher(String text) {
        if (text==null) {
            return;
        }
        Date date = new Date();
        long now = date.getTime();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        long lastClip = sp.getLong("lastClip", 0 );
        long dif = now - lastClip;
        Log.d("dupli", "dif is "+ dif );
        if (dif>5000) {
            Log.d("dupli", "text is: "+ text);
            Context context = getApplicationContext();
            TtsProviderFactory ttsProviderImpl = TtsProviderFactory.getInstance();
            if (ttsProviderImpl != null) {
                ttsProviderImpl.init(context ,text);
                showNotification();
            }



        }
        SharedPreferences.Editor editor = sp.edit();
        editor.putLong("lastClip", now );
        // Commit the edits!
        editor.commit();

    }

    private void showNotification(){


        Intent nextIntent = new Intent(getApplicationContext() , PlaybackService.class);
        PendingIntent pausePendingIntent = PendingIntent.getService(this, 0, nextIntent,0 );


        NotificationCompat.Builder mBuilder =
                null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            mBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.myclips_icon)
                    .setContentText("")
                    .setContentTitle("Clip Talk")
                    //.addAction(R.drawable.skip_previous, "Previous", prevPendingIntent) // #0
                    .addAction(R.mipmap.play_pause,"", pausePendingIntent)  ;// #1
            //.addAction(R.drawable.skip_next, "Next", nextPendingIntent)     // #2
            // Apply the media style template
            //.setStyle(new Notification.MediaStyle()
            //      .setShowActionsInCompactView(1 /* #1: pause button */);
            //.setMediaSession(mMediaSession.getSessionToken());
        }


        mNotificationManager =
                (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        // mId allows you to update the notification later on.
        mNotificationManager.notify(0, mBuilder.build());
    }

    private void hideNotification(){
        mNotificationManager.cancel(0);

    }



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter filter = new IntentFilter();
        filter.addAction(TtsProviderImpl.PAUSE_PLAY);
        // Add other actions as needed

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(TtsProviderImpl.PAUSE_PLAY)) {
                    TtsProviderFactory ttsProviderImpl = TtsProviderFactory.getInstance();
                    if (ttsProviderImpl != null) {
                        ttsProviderImpl.pause();
                    }
                }

            }
        };

        registerReceiver(receiver, filter);



        // mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mClipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        RegPrimaryClipChanged();
    }

    @Override
    public void onDestroy() {
//        TtsProviderFactory ttsProviderImpl = TtsProviderFactory.getInstance();
//        if (ttsProviderImpl!=null) {
//            ttsProviderImpl.shutdown();
//        }
        unregisterReceiver(receiver);
        super.onDestroy();

    }


    private void RegPrimaryClipChanged(){

        mClipboardManager.addPrimaryClipChangedListener(mPrimaryClipChangedListener);

    }


    private class PlaybackService extends Service{

        @Nullable
        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            TtsProviderFactory ttsProviderImpl = TtsProviderFactory.getInstance();
            if (ttsProviderImpl != null) {
                ttsProviderImpl.pause();
            }


            return super.onStartCommand(intent, flags, startId);
        }
    }
}
