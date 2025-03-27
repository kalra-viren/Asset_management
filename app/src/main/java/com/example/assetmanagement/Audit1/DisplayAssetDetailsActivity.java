package com.example.assetmanagement.Audit1;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.VolleyError;
import com.example.assetmanagement.Dashboard.DashboardActivity;
import com.example.assetmanagement.Login.UserCredentials;
import com.example.assetmanagement.R;
import com.example.assetmanagement.Remote.RequestBuilder;
import com.example.assetmanagement.Remote.model.API_Interface;
import com.example.assetmanagement.Util.API_URLs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;



public class DisplayAssetDetailsActivity extends AppCompatActivity {

    private TextView tvAssetCode, tvSerialNumber, tvAssetName, tvUsername, tvName, tvLocation, tvAddedat;
    private RequestBuilder requestBuilder;
    private String scannedUID;
    private Button buttonApprove, buttonReject;
    private EditText etRemark;
    private String asset_code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_asset_details);
        @SuppressLint("UseCompatLoadingForDrawables") Drawable d = getDrawable(R.drawable.card_gradient);
        getSupportActionBar().setBackgroundDrawable(d);
        // Initialize views
        tvAssetCode = findViewById(R.id.tv_asset_code);
        tvSerialNumber = findViewById(R.id.tv_serial_number);
        tvAssetName = findViewById(R.id.tv_asset_name);
        tvUsername = findViewById(R.id.tv_username);
        tvName = findViewById(R.id.tv_name);
        tvLocation = findViewById(R.id.tv_location);
        tvAddedat = findViewById(R.id.tv_added_at);
        requestBuilder = new RequestBuilder(this);
        buttonApprove = findViewById(R.id.buttonApprove);
        buttonReject = findViewById(R.id.buttonReject);
        etRemark = findViewById(R.id.etRemark);
        asset_code = "";

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


        buttonApprove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                audit_update("approve","");

            }
        });

        buttonReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String remrk = etRemark.getText().toString().trim();
                if (!remrk.isEmpty()) {
                    audit_update("reject",remrk);
                    Toast.makeText(DisplayAssetDetailsActivity.this, "Rejected!", Toast.LENGTH_SHORT).show();
                    // You can add further logic here (e.g., API calls, updating UI)
                    Intent intent = new Intent(DisplayAssetDetailsActivity.this, DashboardActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(DisplayAssetDetailsActivity.this, "Please enter the remark!", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void fetchAssetDetails(String uid) {
        String apiUrl = API_URLs.audit_fetch_details_url; // Replace with actual API URL

        try {
            JSONObject requestBody = new JSONObject();
            requestBody.put("uid", uid);
            requestBody.put("type", "Audit");

            requestBuilder.postRequest(apiUrl, requestBody, new API_Interface() {
                @SuppressLint("SetTextI18n")
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
                                asset_code = asset.getString("Asset_Code");
                                tvAssetCode.setText("Asset code: " + asset_code);
                                tvSerialNumber.setText("Serial Number: " + asset.getString("Serial_Number"));
                                tvAssetName.setText("Asset Name: " + asset.getString("Asset_Name"));
                                tvUsername.setText(asset.getString("Username"));
                                tvName.setText(asset.getString("Name"));
                                tvLocation.setText(asset.getString("Location"));
                                try {
                                    String dateTimeString = asset.getString("Latest_Assigned_Date"); // "2025-03-19T10:23:25.457"

                                    // Parse the date from API response
                                    SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.ENGLISH);
                                    Date date = inputFormat.parse(dateTimeString);

                                    // Convert it to "19th March 2025" format
                                    SimpleDateFormat outputFormat = new SimpleDateFormat("d MMMM yyyy", Locale.ENGLISH);
                                    String formattedDate = formatDateWithSuffix(outputFormat.format(date));

                                    // Set text in TextView
                                    tvAddedat.setText(formattedDate);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }                        }
                        } else {
                            Toast.makeText(DisplayAssetDetailsActivity.this, "No data found", Toast.LENGTH_SHORT).show();
                            finish();
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

    private void audit_update(String status, String r) {
//        ProgressBar progressBar = findViewById(R.id.progress_bar);
//        progressBar.setVisibility(View.VISIBLE);
        String url = API_URLs.submit_audit_status_url;
        JSONObject jsonBody = new JSONObject();
        UserCredentials userCredentials = UserCredentials.getInstance(this);
        String userId = userCredentials.getUserId();


        try {
            jsonBody.put("user_id", userId);
            jsonBody.put("remark", r);
            jsonBody.put("status", status);
            jsonBody.put("asset_code", asset_code);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBuilder requestBuilder = new RequestBuilder(this);
        requestBuilder.postRequest(url, jsonBody, new API_Interface() {
            @Override
            public void onSuccess(JSONObject response) {
                Log.i(TAG, "API Success: " + response.toString());
//                progressBar.setVisibility(View.GONE);

                try {
                    boolean success = response.getBoolean("success");
                    String message = response.getString("message");

                    if (success) {
                        JSONObject data = response.getJSONObject("data");
                        JSONArray auditReportArray = data.getJSONArray("Audit Report");

                        if (auditReportArray.length() > 0) {
                            JSONObject reportObject = auditReportArray.getJSONObject(0);
                            String auditMessage = reportObject.getString("message");

                            if ("Submitted successfully".equalsIgnoreCase(auditMessage)) {
                                Toast.makeText(DisplayAssetDetailsActivity.this, "Audit Completed: " + auditMessage, Toast.LENGTH_SHORT).show();
                                // You can add further logic here (e.g., API calls, updating UI)
                                Intent intent = new Intent(DisplayAssetDetailsActivity.this, DashboardActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                                Log.i(TAG, "Audit Success: " + auditMessage);
                            } else if ("Asset Code not found".equalsIgnoreCase(auditMessage)) {
                                Toast.makeText(DisplayAssetDetailsActivity.this, "Error: " + auditMessage, Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "Audit Error: " + auditMessage);
                            } else {
                                Toast.makeText(DisplayAssetDetailsActivity.this, "Unexpected Response: " + auditMessage, Toast.LENGTH_SHORT).show();
                                Log.w(TAG, "Unexpected Audit Message: " + auditMessage);
                            }
                        } else {
                            Toast.makeText(DisplayAssetDetailsActivity.this, "No audit report found", Toast.LENGTH_SHORT).show();
                            Log.w(TAG, "Audit Report Array is Empty");
                        }
                    } else {
                        Toast.makeText(DisplayAssetDetailsActivity.this, "Audit Failed: " + message, Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Audit Failed: " + message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(DisplayAssetDetailsActivity.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "JSON Parsing Error: " + e.toString());
                }
            }

            @Override
            public void onFailure(VolleyError error) {
                Log.e(TAG, "API Failure: " + error.toString());
//                progressBar.setVisibility(View.GONE);

                String errorMessage = "An error occurred";

                if (error.networkResponse != null) {
                    int statusCode = error.networkResponse.statusCode;
                    errorMessage = "Error Code: " + statusCode;

                    if (statusCode == 400) {
                        errorMessage = "Bad Request: Please check your input.";
                    } else if (statusCode == 401) {
                        errorMessage = "Unauthorized: Please check your credentials.";
                    } else if (statusCode == 403) {
                        errorMessage = "Forbidden: You don't have permission.";
                    } else if (statusCode == 404) {
                        errorMessage = "Not Found: The requested resource was not found.";
                    } else if (statusCode == 500) {
                        errorMessage = "Server Error: Please try again later.";
                    }
                } else if (error instanceof com.android.volley.TimeoutError) {
                    errorMessage = "Request Timeout. Please check your network and try again.";
                } else if (error instanceof com.android.volley.NoConnectionError) {
                    errorMessage = "No Internet connection. Please check your network.";
                } else if (error instanceof com.android.volley.NetworkError) {
                    errorMessage = "Network Error. Please try again.";
                }

                Toast.makeText(DisplayAssetDetailsActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                Log.e(TAG, "API Error: " + errorMessage);
            }

        });
    }

    private String formatDateWithSuffix(String date) {
        String[] splitDate = date.split(" ");
        int day = Integer.parseInt(splitDate[0]); // Extract day

        String suffix;
        if (day >= 11 && day <= 13) {
            suffix = "th";
        } else {
            switch (day % 10) {
                case 1:  suffix = "st"; break;
                case 2:  suffix = "nd"; break;
                case 3:  suffix = "rd"; break;
                default: suffix = "th";
            }
        }

        return day + suffix + " " + splitDate[1] + " " + splitDate[2]; // e.g., "19th March 2025"
    }


}
