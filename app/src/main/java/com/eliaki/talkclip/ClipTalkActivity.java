package com.eliaki.talkclip;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

public class ClipTalkActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clip_talk);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startService();
                }
            });
        }
        startService();

    }


    @Override
    protected void onStop() {
        super.onStop();
        stopService();

    }

    public void startService() {
        startService(new Intent(this, ClipTalkService.class));
    }

    // Stop the service
    public void stopService() {
        stopService(new Intent(this, ClipTalkService.class));
    }


}
