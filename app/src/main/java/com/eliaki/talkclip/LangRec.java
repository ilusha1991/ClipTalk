package com.eliaki.talkclip;

import android.util.Log;

import com.cybozu.labs.langdetect.Detector;
import com.cybozu.labs.langdetect.DetectorFactory;
import com.cybozu.labs.langdetect.LangDetectException;
import com.eliaki.talkclip.interfaces.LangRecInterface;

import java.util.ArrayList;


/**
 * Created by eliyahuakiniyazov on 21/04/2016.
 */
public class LangRec implements LangRecInterface {



    public String analyze(String s) {



        int sum=0;
        for (int i = 0; i < Character.codePointCount(s, 0, s.length()); i++) {
            if (s!=" ") {
                sum += s.codePointAt(i);

            }


            //if (c >= 0x0000 && c <=0x007F)
        }
        sum= sum/s.length();

        Log.i(LogTag.TAG,"avg is = " + sum);

        if (sum<700) {
            return "en";
        }
        if (sum > 700 && sum < 1100) {
            return "ru";
        }
        if (sum>1100) {
            return "he";
        }

        return "err";

    }

}
