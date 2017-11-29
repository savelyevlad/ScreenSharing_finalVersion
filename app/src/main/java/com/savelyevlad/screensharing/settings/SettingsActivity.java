package com.savelyevlad.screensharing.settings;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.savelyevlad.screensharing.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;

@SuppressLint("Registered")
public class SettingsActivity extends Activity {

    private EditText editTextIp;
    private EditText editTextPORT;

    private Button buttonSave;

    private String folderPath = Environment.getExternalStorageDirectory().getPath() + "/ScreenSharing/";

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        editTextIp = findViewById(R.id.editText_ip);
        editTextPORT = findViewById(R.id.editText_port);

        buttonSave = findViewById(R.id.button_save);

        Log.e("", (editTextPORT == null) + "");

        String s = String.valueOf(PublicStaticObjects.getIp());
        s = s.substring(1);

        editTextPORT.setText(String.valueOf(PublicStaticObjects.getPORT()));
        editTextIp.setText(s);

        buttonSave.setOnClickListener((v) -> {
            String IP = String.valueOf(editTextIp.getText());
            String PORT = String.valueOf(editTextPORT.getText());

            try {
                InetAddress inetAddress = InetAddress.getByName(IP);
            } catch (Exception e) {
                Context context = getApplicationContext();
                CharSequence text = "It's not an IP!";
                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
                return;
            }

            File file = new File(folderPath + "Config.txt");

            try {
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                fileOutputStream.write((IP + " " + PORT).getBytes());
                PublicStaticObjects.setIp(InetAddress.getByName(IP));
                PublicStaticObjects.setPORT(Integer.valueOf(PORT));
                fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
    }
}
