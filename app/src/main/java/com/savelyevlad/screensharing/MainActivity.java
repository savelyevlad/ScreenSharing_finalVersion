package com.savelyevlad.screensharing;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

import com.savelyevlad.screensharing.settings.SettingsActivity;
import com.savelyevlad.screensharing.watch.WatchActivity;

public class MainActivity extends AppCompatActivity {

    private Button buttonHelp;
    private Button buttonSettings;
    private Button buttonShare;
    private Button buttonWatch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonHelp = findViewById(R.id.button_help);
        buttonSettings = findViewById(R.id.button_settings);
        buttonShare = findViewById(R.id.button_share);
        buttonWatch = findViewById(R.id.button_watch);

        buttonSettings.setOnClickListener((v) -> {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        });

        buttonWatch.setOnClickListener((v) -> {
            Intent intent = new Intent(MainActivity.this, WatchActivity.class);
            startActivity(intent);
        });
    }
}
