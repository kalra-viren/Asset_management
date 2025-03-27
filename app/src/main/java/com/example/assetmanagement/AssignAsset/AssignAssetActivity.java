package com.example.assetmanagement.AssignAsset;

import androidx.appcompat.app.AppCompatActivity;


import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.assetmanagement.Dashboard.DashboardActivity;
import com.example.assetmanagement.Login.LoginActivity;
import com.example.assetmanagement.Login.UserCredentials;
import com.example.assetmanagement.NewAsset.NewAssetActivity;
import com.example.assetmanagement.R;
import com.example.assetmanagement.Util.Config;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class AssignAssetActivity extends AppCompatActivity {

    private ZXingScannerView scannerView;
    private static final int CAMERA_PERMISSION_REQUEST = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.assign_asset_activity);
        @SuppressLint("UseCompatLoadingForDrawables") Drawable d = getDrawable(R.drawable.card_gradient);
        getSupportActionBar().setBackgroundDrawable(d);
//        View scannerLine = findViewById(R.id.scanner_line);
//        ObjectAnimator animator = ObjectAnimator.ofFloat(scannerLine, "translationY", 0f, 230f);
//        animator.setDuration(1200);
//        animator.setRepeatMode(ValueAnimator.REVERSE);
//        animator.setRepeatCount(ValueAnimator.INFINITE);
//        animator.start();


        // Check Camera Permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST);
        } else {

            startCamera();
        }
    }

    private void startCamera() {
        if (scannerView == null) {
            scannerView = findViewById(R.id.qr_camera_preview);
            scannerView.setFormats(java.util.Collections.singletonList(BarcodeFormat.QR_CODE));

        }

        scannerView.setAspectTolerance(0.5f);
        scannerView.setResultHandler(new ZXingScannerView.ResultHandler() {
            @Override
            public void handleResult(Result result) {
                handle_Result(result);
            }
        });
        scannerView.startCamera();
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        if (scannerView != null) {
//            try {
//                scannerView.stopCamera(); // Ensuring the camera is actually active before stopping
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }

    public void handle_Result(Result rawResult) {
        if (scannerView != null) {
            scannerView.stopCamera();
        }
        String scannedUID = rawResult.getText();
        Toast.makeText(this, "Scanned: " + scannedUID, Toast.LENGTH_SHORT).show();

        // Open new screen with scanned UID details
        Intent intent = new Intent(this, ActualAssignmentTask.class);
        intent.putExtra("SCANNED_UID", scannedUID);

        startActivity(intent);
        finish();
    }

    // Handle the permission result
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera();
            } else {
                Toast.makeText(this, "Camera permission is required", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (scannerView != null) {
            scannerView.stopCamera();
        }
        super.onBackPressed(); // Call the default back press behavior
        Intent intent = new Intent(AssignAssetActivity.this, DashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}

