package com.example.assetmanagement.NewAsset;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
//import com.example.assetmanagement.QRCode.QRCodeActivity;
import com.android.volley.VolleyError;
import com.example.assetmanagement.Dashboard.DashboardActivity;
import com.example.assetmanagement.NewAsset.QRCodeScreen.QRCodeActivity;
import com.example.assetmanagement.R;
import com.example.assetmanagement.Remote.RequestBuilder;
import com.example.assetmanagement.Remote.model.API_Interface;
import com.example.assetmanagement.Util.API_URLs;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NewAssetActivity extends AppCompatActivity {

    private Spinner spinnerAssetType;
    private EditText etSerialNumber, etAssetCode;
    private Button btnPrintQRCode;
    private RequestBuilder requestBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_asset);
        requestBuilder = new RequestBuilder(this);

        // Initialize Views
        spinnerAssetType = findViewById(R.id.spinnerAssetType);
        etSerialNumber = findViewById(R.id.etSerialNumber);
        etAssetCode = findViewById(R.id.etAssetCode);
        btnPrintQRCode = findViewById(R.id.btnPrintQRCode);

        // Populate Spinner with asset types
        boolean isDebugMode = false;
        if (isDebugMode) {
            String[] assetTypes = {"Laptop", "Desktop", "Printer", "Scanner", "Projector"};
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, assetTypes);
            spinnerAssetType.setAdapter(adapter);
        } else {
            fetchAssetTypes();
        }

        // Click Listener for QR Code Button
        btnPrintQRCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateFields()) {
                    showConfirmationDialog();
                }
            }
        });
    }

    private boolean validateFields() {
        String serialNumber = etSerialNumber.getText().toString().trim();
        String assetCode = etAssetCode.getText().toString().trim();
        String assetType = spinnerAssetType.getSelectedItem() != null ? spinnerAssetType.getSelectedItem().toString() : "";

        if (serialNumber.isEmpty() || assetCode.isEmpty() || assetType.isEmpty()) {
            showAlertDialog("Incomplete Fields", "Please fill in all the fields before proceeding.");
            return false;
        }
        return true;
    }

    private void showAlertDialog(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void fetchAssetTypes() {
        ProgressBar progressBar = findViewById(R.id.progressBarSpinner);
        progressBar.setVisibility(View.VISIBLE);
        String url = API_URLs.fetch_assets_type_url;
        requestBuilder.getRequest(url, new API_Interface() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    if (response.getBoolean("success")) {
                        progressBar.setVisibility(View.GONE);
                        JSONArray assetsArray = response.getJSONObject("data").getJSONArray("Assets");
                        List<String> assetTypes = new ArrayList<>();

                        for (int i = 0; i < assetsArray.length(); i++) {
                            JSONObject asset = assetsArray.getJSONObject(i);
                            assetTypes.add(asset.getString("Name"));
                        }

                        // Populate Spinner
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(NewAssetActivity.this,
                                android.R.layout.simple_spinner_dropdown_item, assetTypes);
                        spinnerAssetType.setAdapter(adapter);
                    } else {
                        Toast.makeText(NewAssetActivity.this, "Failed to fetch asset types", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(NewAssetActivity.this, "Error parsing response", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(NewAssetActivity.this, "API Request Failed: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Generate QR Code")
                .setMessage("Do you want to generate the QR code?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        call the api to get the uid for the qr code.
                        openQRCodeScreen();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void openQRCodeScreen() {
        // Get values from input fields
        String assetType = spinnerAssetType.getSelectedItem().toString();
        String serialNumber = etSerialNumber.getText().toString();
        String assetCode = etAssetCode.getText().toString();


        // Open the QR Code Activity with data
        Intent intent = new Intent(NewAssetActivity.this, QRCodeActivity.class);
        intent.putExtra("ASSET_TYPE", assetType);
        intent.putExtra("SERIAL_NUMBER", serialNumber);
        intent.putExtra("ASSET_CODE", assetCode);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed(); // Call the default back press behavior
        Intent intent = new Intent(NewAssetActivity.this, DashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}

