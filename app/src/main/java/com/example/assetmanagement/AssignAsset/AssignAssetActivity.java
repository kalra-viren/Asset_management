package com.example.assetmanagement.AssignAsset;

import androidx.appcompat.app.AppCompatActivity;


import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
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

public class AssignAssetActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView scannerView;
    private static final int CAMERA_PERMISSION_REQUEST = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.assign_asset_activity);
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
        scannerView = new ZXingScannerView(this);
        scannerView.setFormats(java.util.Collections.singletonList(BarcodeFormat.QR_CODE));

        ViewGroup previewContainer = findViewById(R.id.qr_camera_preview);
        previewContainer.addView(scannerView);
//        boolean isDebugMode = false;
        if (Config.isDebugMode) {

            Intent intent = new Intent(AssignAssetActivity.this, ActualAssignmentTask.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (scannerView != null) {
            scannerView.setResultHandler(this);
            scannerView.startCamera();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (scannerView != null) {
            scannerView.stopCamera();
        }
    }

    @Override
    public void handleResult(Result rawResult) {
        String scannedUID = rawResult.getText();
        Toast.makeText(this, "Scanned: " + scannedUID, Toast.LENGTH_SHORT).show();

        // Open new screen with scanned UID details
        Intent intent = new Intent(this, ActualAssignmentTask.class);
        intent.putExtra("SCANNED_UID", scannedUID);
        startActivity(intent);
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
        super.onBackPressed(); // Call the default back press behavior
        Intent intent = new Intent(AssignAssetActivity.this, DashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}

