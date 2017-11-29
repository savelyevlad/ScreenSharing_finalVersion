package com.savelyevlad.screensharing.sharing;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.hardware.display.DisplayManager;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.widget.Button;
import android.widget.TextView;

import com.savelyevlad.screensharing.R;
import com.savelyevlad.screensharing.settings.PublicStaticObjects;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class SharingActivity extends Activity {

    private Button startButton;
    private Button stopButton;
    private TextView textID;

    private boolean running = false;

    private MediaProjectionManager mediaProjectionManager;

    private int id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sharing);

        PublicStaticObjects.initSocket();

        textID = findViewById(R.id.sharingID);
        startButton = findViewById(R.id.startButton);
        stopButton = findViewById(R.id.stopButton);

        mediaProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);

        startButton.setOnClickListener((v) -> {
            if(running) {
                return;
            }
            running = true;
            new Thread(() -> {
                try {
                    PublicStaticObjects.getObjectOutputStream().writeObject(-1);
                    Object object = PublicStaticObjects.getObjectInputStream().readObject();
                    id = (Integer) object;
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }).start();
            textID.setText(id);
            startActivityForResult(mediaProjectionManager.createScreenCaptureIntent(), 228);
        });
    }

    private MediaProjection mediaProjection;
    private int displayWidth;
    private int displayHeight;

    private ImageReader imageReader;

    private Handler handler;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 228) {
            mediaProjection = mediaProjectionManager.getMediaProjection(resultCode, data);

            if(mediaProjection != null) {
                DisplayMetrics metrics = getResources().getDisplayMetrics();
                int density = metrics.densityDpi;
                int flags = DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY
                        | DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC;

                Display display = getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                displayHeight = size.y;
                displayWidth = size.x;

                imageReader = ImageReader.newInstance(size.x, size.y, PixelFormat.RGBA_8888, 16);

                mediaProjection.createVirtualDisplay("screencap", size.x, size.y, density,
                        flags, imageReader.getSurface(), null, handler);
                imageReader.setOnImageAvailableListener(new ImageAvailableListener(), handler);
            }
        }
    }

    @SuppressLint("NewApi")
    private class ImageAvailableListener implements ImageReader.OnImageAvailableListener {

        private Bitmap bitmap;

        @Override
        public void onImageAvailable(ImageReader imageReader) {
            Image image = null;
            FileOutputStream fileOutputStream = null;
            ByteArrayOutputStream byteArrayOutputStream = null;

            try {
                image = imageReader.acquireLatestImage();
                if (image != null) {
                    Image.Plane[] planes = image.getPlanes();
                    ByteBuffer buffer = planes[0].getBuffer();
                    int pixelStride = planes[0].getPixelStride();
                    int rowStride = planes[0].getRowStride();
                    int rowPadding = rowStride - pixelStride * displayWidth;

                    bitmap = Bitmap.createBitmap(displayWidth + rowPadding / pixelStride,
                            displayHeight, Bitmap.Config.ARGB_4444);
                    bitmap.copyPixelsFromBuffer(buffer);
                    Bitmap bit = bitmap.copy(Bitmap.Config.ARGB_4444, false);

                    sendImage(bit);

                }
            } catch (Throwable e) {
                Log.e("Throwable", e.getMessage());
            } finally {

                if (bitmap != null) {
                    bitmap.recycle();
                }

                if (image != null) {
                    image.close();
                }
            }
        }
    }

    private byte[] concat(byte[] a, byte[] b) {
        byte[] t = new byte[a.length + b.length];
        System.arraycopy(a, 0, t, 0, a.length);
        System.arraycopy(b, 0, t, a.length, b.length);
        return t;
    }

    private synchronized void sendImage(final Bitmap bitmap) {
        Thread thread = new Thread(new Runnable() {

            ByteArrayOutputStream bytes;

            @Override
            public void run() {
                bytes = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 5, bytes);

                try {
                    PublicStaticObjects.getObjectOutputStream().flush();
                    PublicStaticObjects.getObjectOutputStream().reset();
                    PublicStaticObjects.getObjectOutputStream().writeObject(bytes.toByteArray());
                    PublicStaticObjects.getObjectOutputStream().flush();
                    PublicStaticObjects.getObjectOutputStream().reset();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.setPriority(Thread.MAX_PRIORITY);
        thread.start();
    }
}
