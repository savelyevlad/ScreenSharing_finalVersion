package com.savelyevlad.screensharing.watch;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.savelyevlad.screensharing.settings.PublicStaticObjects;

import java.io.IOException;

public class Receiver implements Runnable {

    private byte[] concat(byte[] a, byte[] b) {
        byte[] t = new byte[a.length + b.length];
        System.arraycopy(a, 0, t, 0, a.length);
        System.arraycopy(b, 0, t, a.length, b.length);
        return t;
    }

    private ImageView imageView;

    private ActivityWatch activityWatch;

    public Receiver(ActivityWatch activityWatch, ImageView imageView) {
        this.imageView = imageView;
        this.activityWatch = activityWatch;
    }

    @Override
    public void run() {
        while (true) {
            try {
                byte[] buf = (byte[]) PublicStaticObjects.getObjectInputStream().readObject();
                Bitmap receiveBitmap = BitmapFactory.decodeByteArray(buf, 0, buf.length);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void changeImage(final Bitmap bitmap) {
        activityWatch.runOnUiThread(() -> imageView.setImageBitmap(bitmap));
    }
}