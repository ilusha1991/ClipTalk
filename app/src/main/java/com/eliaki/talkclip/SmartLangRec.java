package com.eliaki.talkclip;

import android.content.Context;
import android.util.Log;

import com.cybozu.labs.langdetect.Detector;
import com.cybozu.labs.langdetect.DetectorFactory;
import com.cybozu.labs.langdetect.LangDetectException;
import com.eliaki.talkclip.interfaces.LangRecInterface;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;

/**
 * Created by eliyahuakiniyazov on 04/05/2016.
 */
public class SmartLangRec implements LangRecInterface {

    Context mContext;

    SmartLangRec(Context context){
        mContext= context;

    }

    @Override
    public String analyze(String s) {


        String lang="err";

        LangDetectSample ds = new LangDetectSample();
        try {
            ds.init(mContext);
            lang = ds.detect(s);
            Log.i(LogTag.TAG,"lang is ===== " +lang);

        } catch (LangDetectException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lang;
    }


    class LangDetectSample {
        public void init(Context context) throws LangDetectException, IOException, URISyntaxException {

            File file = context.getExternalFilesDir(null);
            String path = file.getPath() + "/" + "profiles";
            moveAssetToStorageDir("profiles");
            DetectorFactory.clear();
            DetectorFactory.loadProfile(path);
        }
        public String detect(String text) throws LangDetectException {
            Detector detector = DetectorFactory.create();
            detector.append(text);
            return detector.detect();
        }
        public ArrayList detectLangs(String text) throws LangDetectException {
            Detector detector = DetectorFactory.create();
            detector.append(text);
            return detector.getProbabilities();
        }

        public void moveAssetToStorageDir(String path){
            File file = mContext.getExternalFilesDir(null);

            String rootPath = file.getPath() + "/" + path;
            File dir = new File(rootPath);
            try{
                dir.mkdir();

                String [] paths = mContext.getAssets().list(path);
                for(int i=0; i<paths.length; i++){

                        File dest = null;
                        InputStream in = null;
                        if(path.length() == 0) {
                            dest = new File(rootPath + paths[i]);
                            in = mContext.getAssets().open(paths[i]);
                        }else{
                            dest = new File(rootPath + "/" + paths[i]);
                            in = mContext.getAssets().open(path + "/" + paths[i]);
                        }
                        dest.createNewFile();
                        FileOutputStream out = new FileOutputStream(dest);
                        byte [] buff = new byte[in.available()];
                        in.read(buff);
                        out.write(buff);
                        out.close();
                        in.close();
                    }

            }catch (Exception exp){
                exp.printStackTrace();
            }
        }



    }
}
