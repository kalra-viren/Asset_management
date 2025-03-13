package com.example.assetmanagement.Audit1;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.VolleyError;
import com.example.assetmanagement.Dashboard.DashboardActivity;
import com.example.assetmanagement.R;
import com.example.assetmanagement.Remote.RequestBuilder;
import com.example.assetmanagement.Remote.model.API_Interface;
import com.example.assetmanagement.Util.API_URLs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DisplayAssetDetailsActivity extends AppCompatActivity {

    private TextView tvAssetCode, tvSerialNumber, tvAssetName, tvUsername, tvName, tvLocation;
    private RequestBuilder requestBuilder;
    private String scannedUID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_asset_details);

        // Initialize views
        tvAssetCode = findViewById(R.id.tv_asset_code);
        tvSerialNumber = findViewById(R.id.tv_serial_number);
        tvAssetName = findViewById(R.id.tv_asset_name);
        tvUsername = findViewById(R.id.tv_username);
        tvName = findViewById(R.id.tv_name);
        tvLocation = findViewById(R.id.tv_location);

        requestBuilder = new RequestBuilder(this);

        // Get UID from Intent
        Intent intent = getIntent();
        if (intent != null) {
            scannedUID = intent.getStringExtra("SCANNED_UID");
            if (scannedUID != null) {
                fetchAssetDetails(scannedUID);
            } else {
                Toast.makeText(this, "No UID received", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void fetchAssetDetails(String uid) {
        String apiUrl = API_URLs.audit_fetch_details_url; // Replace with actual API URL

        try {
            JSONObject requestBody = new JSONObject();
            requestBody.put("uid", uid);
            requestBody.put("type", "Audit");

            requestBuilder.postRequest(apiUrl, requestBody, new API_Interface() {
                @Override
                public void onSuccess(JSONObject response) {
                    Log.d("API Response", response.toString());

                    try {
                        boolean success = response.getBoolean("success");
                        if (success) {
                            JSONObject data = response.getJSONObject("data");
                            JSONArray assetDetails = data.getJSONArray("Asset Details");

                            if (assetDetails.length() > 0) {
                                JSONObject asset = assetDetails.getJSONObject(0);

                                tvAssetCode.setText(asset.getString("Asset_Code"));
                                tvSerialNumber.setText(asset.getString("Serial_Number"));
                                tvAssetName.setText(asset.getString("Asset_Name"));
                                tvUsername.setText(asset.getString("Username"));
                                tvName.setText(asset.getString("Name"));
                                tvLocation.setText(asset.getString("Location"));
                            }
                        } else {
                            Toast.makeText(DisplayAssetDetailsActivity.this, "No data found", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(DisplayAssetDetailsActivity.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(VolleyError error) {
                    Log.e("API Error", error.toString());
                    Toast.makeText(DisplayAssetDetailsActivity.this, "Failed to fetch data", Toast.LENGTH_SHORT).show();
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error creating request body", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(DisplayAssetDetailsActivity.this, DashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish(); // Finish the current activity
    }

}
