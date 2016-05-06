package com.eliaki.talkclip;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.eliaki.talkclip.interfaces.LangRecInterface;


import java.util.HashMap;
import java.util.Locale;

public class TtsProviderImpl extends TtsProviderFactory implements TextToSpeech.OnInitListener {

    public static final String PAUSE_PLAY = "com.eliaki.talkclip.";
    private TextToSpeech tts;
    private Context mContext;
    private String mSpeach= "Nothing to say";
    NotificationManager mNotificationManager;



    @Override
    public void init(Context context, String text) {
        mContext=context;
        mSpeach= text;
        //if (tts == null) {
            tts = new TextToSpeech(context, this);
        //} else {
            //say(text);
        //}
    }

    private void say(String sayThis) {


        SmartLangRec rec = new SmartLangRec(mContext);
        String lang =rec.analyze(sayThis);
        Locale loc = new Locale(lang , "" , "");
        if (tts.isLanguageAvailable(loc) >= TextToSpeech.LANG_AVAILABLE) {
            tts.setLanguage(loc);
        } else {
            Log.d(LogTag.TAG,"no such  language");
        }
        tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String s) {

            }

            @Override
            public void onDone(String s) {
                shutdown();
            }

            @Override
            public void onError(String s) {
                shutdown();
            }
        });
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,"messageID");

        tts.speak(sayThis , TextToSpeech.QUEUE_FLUSH, map);


    }





    @Override
    public void onInit(int status) {
        Locale loc = new Locale("en", "", "");
        if (tts.isLanguageAvailable(loc) >= TextToSpeech.LANG_AVAILABLE) {
            tts.setLanguage(loc);
        }
        Log.i(LogTag.TAG,"-----about to say something------");

        say(mSpeach);
    }




    public void shutdown() {
        tts.shutdown();
        tts=null;
        interrupt();
        Log.i(LogTag.TAG,"-----tts shutdown------");
    }

    @Override
    public void pause() {
        tts.stop();
    }

}

