package com.cameratest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.view.WindowManager;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata;
import com.google.firebase.auth.AuthResult;
import com.otaliastudios.cameraview.BitmapCallback;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraLogger;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.FileCallback;
import com.otaliastudios.cameraview.PictureResult;
import com.otaliastudios.cameraview.VideoResult;
import com.otaliastudios.cameraview.frame.Frame;
import com.otaliastudios.cameraview.frame.FrameProcessor;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private CameraView camera;
    private Bitmap bmap;
    private File fname;
    private Button b1;
    private Button b2;
    private Button b3;
    private boolean toggle_fproc = true;
    private final static boolean DECODE_BITMAP = false;
    private final static CameraLogger LOG = CameraLogger.create("MyApp");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //will hide the title
        getSupportActionBar().hide(); // hide the title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //enable full screen
        setContentView(R.layout.activity_main2);
        camera = findViewById(R.id.camera);
        camera.setLifecycleOwner(this);
        b1 = (Button)findViewById(R.id.takepic);
        b2 = (Button)findViewById(R.id.vbmap);
        b3 = (Button)findViewById(R.id.toggleframeproc);
        SharedPreferences sharedPrefs = getSharedPreferences("com.camtest", MODE_PRIVATE);
        toggle_fproc = sharedPrefs.getBoolean("toggle_fproc", true);
        if(toggle_fproc)
            b3.setText("toggle on");
        else
            b3.setText("toggle off");
        ////setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR | ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        camera.addFrameProcessor(new FrameProcessor() {
            private long lastTime = System.currentTimeMillis();
            @Override
            public void process(@NonNull Frame frame) {
                if(toggle_fproc) {
                    long newTime = frame.getTime();
                    long delay = newTime - lastTime;
                    lastTime = newTime;
                    ////LOG.e("Frame delayMillis:", delay, "FPS:", 1000 / delay);
                    if (DECODE_BITMAP) {
                        YuvImage yuvImage = new YuvImage(frame.getData(), ImageFormat.NV21,
                                frame.getSize().getWidth(),
                                frame.getSize().getHeight(),
                                null);
                        ByteArrayOutputStream jpegStream = new ByteArrayOutputStream();
                        yuvImage.compressToJpeg(new Rect(0, 0,
                                frame.getSize().getWidth(),
                                frame.getSize().getHeight()), 100, jpegStream);
                        byte[] jpegByteArray = jpegStream.toByteArray();
                        Bitmap bitmap = BitmapFactory.decodeByteArray(jpegByteArray, 0, jpegByteArray.length);
                        //noinspection ResultOfMethodCallIgnored
                        ////bitmap.toString();
                        ////LOG.e("BItMap ByteCount : ", bitmap.getByteCount());
                    } else {
                        FirebaseVisionImage image = FirebaseVisionImage.fromByteArray(frame.getData(), extractFrameMetadata(frame));
                        ////AuthResult result = Tasks.await(firebaseDetector.detectInImage(image));
                        LOG.e("firebase BItMap ByteCount : ", image.getBitmap().getAllocationByteCount());
                    }
                }
            }
        });
        camera.addCameraListener(new CameraListener() {
            @Override
            public void onPictureTaken(PictureResult result) {
                // A Picture was taken!
                // If planning to show a Bitmap, we will take care of
                // EXIF rotation and background threading for you...
                File file = new File(Environment.getExternalStorageDirectory()+"/"+ UUID.randomUUID().toString()+".jpg");
                ////File file = new File(Environment.getExternalStorageDirectory()+"/"+ "xxx"+".jpg");
                result.toBitmap(result.getSize().getWidth(), result.getSize().getHeight(), new BitmapCallback() {
                    @Override
                    public void onBitmapReady(Bitmap bitmap) {
                        bmap = bitmap;
                        AsyncTask.execute(new Runnable() {
                            @Override
                            public void run() {
                                //TODO your background code
                                File cacheDir = getBaseContext().getCacheDir();
                                File f = new File(cacheDir, "pic");
                                try {
                                    FileOutputStream out = new FileOutputStream(
                                            f);
                                    bmap.compress(
                                            Bitmap.CompressFormat.JPEG,
                                            100, out);
                                    out.flush();
                                    out.close();

                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                MainActivity.this.runOnUiThread(new Runnable() {
                                    public void run() {
                                        b1.setEnabled(true);
                                        b2.setEnabled(true);
                                        ////setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                                    }
                                });
                            }
                        });
                    }
                });
                // If planning to save a file on a background thread,
                // just use toFile. Ensure you have permissions.
                result.toFile(file,  new FileCallback() {
                    @Override
                    public void onFileReady(@Nullable File file) {
                        fname = file;
                    }
                });
                // Access the raw data if needed.
                ////byte[] data = result.getData();
            }

            @Override
            public void onVideoTaken(VideoResult result) {
                // A Video was taken!
            }

            // And much more
        });
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                camera.takePicture();
                b1.setEnabled(false);
                b2.setEnabled(false);
                ////setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this,ViewBmap.class);
                MainActivity.this.startActivity(i);
            }
        });
        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggle_fproc = !toggle_fproc;
                SharedPreferences.Editor editor = getSharedPreferences("com.camtest", MODE_PRIVATE).edit();
                editor.putBoolean("toggle_fproc", toggle_fproc);
                editor.commit();
                if(toggle_fproc)
                    b3.setText("toggle on");
                else
                    b3.setText("toggle off");
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        camera.open();
    }

    @Override
    protected void onPause() {
        super.onPause();
        camera.close();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        camera.destroy();
    }
    private FirebaseVisionImageMetadata extractFrameMetadata (Frame frame){
        return new FirebaseVisionImageMetadata.Builder()
                .setWidth(frame.getSize().getWidth())
                .setHeight(frame.getSize().getHeight())
                .setFormat(frame.getFormat())
                .setRotation(frame.getRotation() / 90)
                .build();
    }
}
