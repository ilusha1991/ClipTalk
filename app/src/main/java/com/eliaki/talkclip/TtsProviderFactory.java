package com.eliaki.talkclip;

import android.content.Context;
import android.os.Build;

/**
 * Created by eliyahuakiniyazov on 21/04/2016.
 */




public abstract class  TtsProviderFactory extends Thread{




    //private abstract void say(String sayThis);

    public abstract void init(Context context,String text);

    public abstract void shutdown();

    public abstract void pause();



    private static TtsProviderFactory sInstance;

    public static TtsProviderFactory getInstance() {
        if (sInstance == null) {
            int sdkVersion = Integer.parseInt(Build.VERSION.SDK);
            if (sdkVersion < Build.VERSION_CODES.DONUT) {
                return null;
            }

            try {
                String className = "TtsProviderImpl";
                Class<? extends TtsProviderFactory> clazz =
                        Class.forName(TtsProviderFactory.class.getPackage().getName() + "." + className)
                                .asSubclass(TtsProviderFactory.class);
                sInstance = clazz.newInstance();
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        }
        return sInstance;
    }
}

