package com.savelyevlad.screensharing.watch;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.widget.EditText;
import android.widget.ImageView;

import com.savelyevlad.screensharing.R;
import com.savelyevlad.screensharing.settings.PublicStaticObjects;

import java.io.IOException;

public class ActivityWatch extends Activity {

    private FloatingActionButton startFab;
    private FloatingActionButton stopFab;
    private ImageView imageView;

    private EditText editText;

    private boolean canJoin = false;

    private Thread thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch);

        editText = findViewById(R.id.editText_ID);

        // ATTENTION:!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        startFab = findViewById(R.id.startFab);
      //  stopFab = findViewById(R.id.stopFab);
        imageView = findViewById(R.id.imageView);

        PublicStaticObjects.initSocket();

        startFab.setOnClickListener((v) -> {
            // Can I join someone?
            mustBeAlive = true;
            thread = new Thread(() -> {
                try {
                    PublicStaticObjects.getObjectOutputStream().writeObject(-3);
                    PublicStaticObjects.getObjectOutputStream().writeObject(Integer.valueOf(String.valueOf(editText.getText())));
                    try {
                        Object object = PublicStaticObjects.getObjectInputStream().readObject();
                        if(!object.equals("-3")) {
                            canJoin = true;
                        }
                        else {
                            canJoin = false;
//                        TODO: toast
//                        Toast.makeText();
                        }
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(canJoin) {
                Thread thread1 = new Thread(new Receiver(this, imageView));
                thread1.setPriority(Thread.MAX_PRIORITY);
                thread1.start();
            }
        });

    /*    stopFab.setOnClickListener((v) -> {
            new Thread(() -> {
                try {
                    PublicStaticObjects.getObjectOutputStream().writeObject(-4);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
            if(thread == null) {
                return;
            }
            if(thread.isAlive()) {
                thread.interrupt();
                mustBeAlive = false;
            }
        });*/
    }

    public static boolean isMustBeAlive() {
        return mustBeAlive;
    }

    public static void setMustBeAlive(boolean mustBeAlive) {
        ActivityWatch.mustBeAlive = mustBeAlive;
    }

    private static boolean mustBeAlive = false;

}
