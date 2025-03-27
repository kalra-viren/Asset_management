package com.example.assetmanagement.Dashboard;

import android.annotation.SuppressLint;
import android.content.Intent;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.assetmanagement.AssignAsset.AssignAssetActivity;
import com.example.assetmanagement.Audit1.Audit1Activity;
import com.example.assetmanagement.Login.LoginActivity;
import com.example.assetmanagement.NewAsset.NewAssetActivity;
import com.example.assetmanagement.R;

public class DashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Initialize CardViews
        CardView cardNewAsset = findViewById(R.id.card_new_asset);
        CardView cardAssignAsset = findViewById(R.id.card_assign_asset);
        CardView cardAudit1 = findViewById(R.id.card_audit1);
//        android:background="#BFCF8D" cream color

        CardView cardAudit2 = findViewById(R.id.card_audit2);
        @SuppressLint("UseCompatLoadingForDrawables") Drawable d=getDrawable(R.drawable.card_gradient);
        getSupportActionBar().setBackgroundDrawable(d);

        // Set click listeners for each CardView
        LinearLayout llNewAsset = findViewById(R.id.ll_new_asset);

        llNewAsset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this, NewAssetActivity.class);
                startActivity(intent);
                finish();
            }
        });
        LinearLayout llAssignAsset = findViewById(R.id.ll_assign_asset);
        // Click listener for "Assign Asset"
        llAssignAsset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this, AssignAssetActivity.class);
                startActivity(intent);
                finish();
            }
        });

        LinearLayout llAudit1 = findViewById(R.id.ll_audit1);
        LinearLayout llAudit2 = findViewById(R.id.ll_audit2);

// Click listener for "Audit 1"
        llAudit1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardActivity.this, Audit1Activity.class);
                startActivity(intent);
            }
        });

// Click listener for "Audit 2" (Uncomment when Audit2Activity is implemented)
        llAudit2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Uncomment the line below once Audit2Activity is created
                // Intent intent = new Intent(DashboardActivity.this, Audit2Activity.class);
                // startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed(); // Call the default back press behavior
        Intent intent = new Intent(DashboardActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

}

