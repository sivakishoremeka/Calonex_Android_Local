package mp.app.calonex.common.utility;


import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;

import mp.app.calonex.R;

public class CropImageActivity extends CxBaseActivityKotlin {

    private Bitmap bitmap;
    boolean firstLaunch = true;
    CropImageView cropImageView;

    TextView crop_cancel;
    TextView crop_done;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_image);
        crop_cancel = findViewById(R.id.cropImageView_cancel);
        crop_done = findViewById(R.id.cropImageView_done);
        try {
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), getIntent().getData());
        } catch (IOException e) {
            e.printStackTrace();
        }

        crop_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        crop_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //        showProgress("crop");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (cropImageView.saveBitmapInToFolder()) {
                                Intent intent = new Intent();
                                intent.setData(cropImageView.getImageUri());
                                setResult(RESULT_OK, intent);
                            }
                        } catch (Exception e) {

                        }
                        finish();
                    }
                }, 1000);
//        hideProgress();

            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && firstLaunch && bitmap != null) {
            firstLaunch = false;
            Bitmap newbitmap = Bitmap.createScaledBitmap(bitmap,
                    cropImageView.getWidth(),
                    cropImageView.getHeight(),
                    true);
            cropImageView.setImageBitmap(newbitmap);

            if (getIntent().getBooleanExtra("isCircular", false)) {
                cropImageView.setCropMode(CropImageView.CropMode.CIRCLE);
            } else
                cropImageView.setCropMode(CropImageView.CropMode.RATIO_FREE);
        }
    }



}
