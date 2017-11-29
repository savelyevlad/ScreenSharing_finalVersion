package com.savelyevlad.screensharing.watch;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.EditText;
import android.widget.ImageView;

import com.savelyevlad.screensharing.R;

public class WatchActivity extends Activity {

    private EditText editTextID;
    private ImageView imageView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch);

        editTextID = findViewById(R.id.editText_ID);
        imageView = findViewById(R.id.imageView);
    }
}
