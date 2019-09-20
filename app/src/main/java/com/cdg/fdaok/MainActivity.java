package com.cdg.fdaok;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    public static final int PERMISSION_CODE = 111;

    // Max width (portrait mode)
    private Integer mImageMaxWidth;
    // Max height (portrait mode)
    private Integer mImageMaxHeight;

    private ImageView mImageView;
    private Button mGalleryButton;
    private Button mHistoryButton;
    private Bitmap mSelectedImage;
    private GraphicOverlay mGraphicOverlay;
    private TextView mFdaNumberView;
    private int gellary = 1;

    private String fdaText;
    private DataEntity entity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(!checkPermission()){
            requestPermission();
        }
        mFdaNumberView = findViewById(R.id.fda_number_txt);
        mImageView = findViewById(R.id.image_view);
        mGraphicOverlay = findViewById(R.id.graphic_overlay);

        mGalleryButton = findViewById(R.id.gallery_btn);
        mHistoryButton = findViewById(R.id.history_btn);

        entity = new DataEntity();
        entity.setCncnm("สถานะผลิตภัณฑ์(คงอยู่)\\\\ สถานะสถานที่ (คงอยู่)");
        entity.setTypepro("อาหาร (สบ.5/สบ.7)");
        entity.setLcnno("10-1-04442-1-0365");
        entity.setProductha("ไอซ์คอนเฟคชันรสกะทิราดด้วยคอมปาวนด์ช็อกโกแลตและถั่วลิสง (ไอศกรีมดัดแปลงผสม)(ตรา แมกโนเลีย แมก อะ โคน โคโค่ พาราไดส์)");
        entity.setProduceng("COCONUT MILK ICE CONFECTION TOPPED WITH COMPOUND CHOCOLATE AND PEANUTS (MAGNOLIA MAG A CONE COCO PARADISE BRAND)");
        entity.setLicen("บริษัทเอฟแอนด์เอ็น ยูไนเต็ด  จำกัด");
        entity.setThanm("บริษัทเอฟแอนด์เอ็น ยูไนเต็ด  จำกัด");
        entity.setAddr("บ้านเลขที่95 ถนนท่าข้าม ตำบลแสมดำ อำเภอบางขุนเทียน จังหวัดกรุงเทพมหานคร 10150");
        entity.setNewCode("U1FE0001011010444210365C");

        mGalleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
        mHistoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProductDetail();
            }
        });
    }

    public void showProductDetail(){
        ProductDetailMBS productDetail = new ProductDetailMBS(entity);
        productDetail.show(getSupportFragmentManager(),"ProductDetail");
    }

    public boolean checkPermission(){
        int result = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int result2 = ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.CAMERA);
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED;

    }

    public void requestPermission(){
        ActivityCompat.requestPermissions(MainActivity.this,new String[] {Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA},PERMISSION_CODE);
    }

    private void openGallery() {
        Log.i(TAG, "selectPhoto()");
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select File"),gellary);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){
            if (requestCode == gellary){
                OpenGellaryResult(data);
            }
        }
    }


    // Returns max image width, always for portrait mode. Caller needs to swap width / height for
    // landscape mode.
    private Integer getImageMaxWidth() {
        if (mImageMaxWidth == null) {
            // Calculate the max width in portrait mode. This is done lazily since we need to
            // wait for
            // a UI layout pass to get the right values. So delay it to first time image
            // rendering time.
            mImageMaxWidth = mImageView.getWidth();
        }

        return mImageMaxWidth;
    }

    // Returns max image height, always for portrait mode. Caller needs to swap width / height for
    // landscape mode.
    private Integer getImageMaxHeight() {
        if (mImageMaxHeight == null) {
            // Calculate the max width in portrait mode. This is done lazily since we need to
            // wait for
            // a UI layout pass to get the right values. So delay it to first time image
            // rendering time.
            mImageMaxHeight =
                    mImageView.getHeight();
        }

        return mImageMaxHeight;
    }

    // Gets the targeted width / height.
    private Pair<Integer, Integer> getTargetedWidthHeight() {
        int targetWidth;
        int targetHeight;
        int maxWidthForPortraitMode = getImageMaxWidth();
        int maxHeightForPortraitMode = getImageMaxHeight();
        targetWidth = maxWidthForPortraitMode;
        targetHeight = maxHeightForPortraitMode;
        return new Pair<>(targetWidth, targetHeight);
    }


    private void OpenGellaryResult(Intent data) {
        mGraphicOverlay.clear();

        Bitmap bitmap = null;

        if (data != null){
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),data.getData());

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        mSelectedImage = bitmap;
//        GraphicOverlay.Graphic textGraphic = new TextGraphic(mGraphicOverlay, elements.get(k));
//        mGraphicOverlay.add(textGraphic);

        // Get the dimensions of the View
        Pair<Integer, Integer> targetedSize = getTargetedWidthHeight();

        int targetWidth = targetedSize.first;
        int maxHeight = targetedSize.second;

        // Determine how much to scale down the image
        float scaleFactor =
                Math.max(
                        (float) mSelectedImage.getWidth() / (float) targetWidth,
                        (float) mSelectedImage.getHeight() / (float) maxHeight);

        Bitmap resizedBitmap =
                Bitmap.createScaledBitmap(
                        mSelectedImage,
                        (int) (mSelectedImage.getWidth() / scaleFactor),
                        (int) (mSelectedImage.getHeight() / scaleFactor),
                        true);

        mImageView.setImageBitmap(resizedBitmap);
        mSelectedImage = resizedBitmap;
        runTextRecognition();
    }

    private void runTextRecognition() {
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(mSelectedImage);
        FirebaseVisionTextRecognizer recognizer = FirebaseVision.getInstance()
                .getOnDeviceTextRecognizer();
        recognizer.processImage(image)
                .addOnSuccessListener(
                        new OnSuccessListener<FirebaseVisionText>() {
                            @Override
                            public void onSuccess(FirebaseVisionText texts) {
                                processTextRecognitionResult(texts);
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Task failed with an exception
                                e.printStackTrace();
                            }
                        });
    }

    private void processTextRecognitionResult(FirebaseVisionText texts) {
        List<FirebaseVisionText.TextBlock> blocks = texts.getTextBlocks();
        if (blocks.size() == 0) {
            showToast("No text found");
            return;
        }
        mFdaNumberView.setText(null);
        mGraphicOverlay.clear();
        fdaText = null;
        for (int i = 0; i < blocks.size(); i++) {
            List<FirebaseVisionText.Line> lines = blocks.get(i).getLines();
            for (int j = 0; j < lines.size(); j++) {
                List<FirebaseVisionText.Element> elements = lines.get(j).getElements();
                for (int k = 0; k < elements.size(); k++) {

                    FirebaseVisionText.Element elem = elements.get(k);
                    if(Pattern.matches("\\d{2}-\\d-\\d{5}-\\d-\\d{4}",elem.getText())){
                        fdaText = elem.getText();
                        GraphicOverlay.Graphic textGraphic = new TextGraphic(mGraphicOverlay, elements.get(k));
                        mGraphicOverlay.add(textGraphic);
                    }
                }
            }
        }
        showMessage();
    }

    private void showMessage(){
        if(fdaText != null){
            mFdaNumberView.setText("FDA NUM : "+fdaText);
//            showToast("id = " + fdaText);
        }else{
            showToast("id not found");
        }
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

}
