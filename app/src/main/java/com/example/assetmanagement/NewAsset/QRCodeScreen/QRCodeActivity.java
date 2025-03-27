package com.example.assetmanagement.NewAsset.QRCodeScreen;

import static com.example.assetmanagement.NewAsset.QRCodeScreen.BitmapToZPLConverter.sendBitmapToPrinter;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.VolleyError;
import com.example.assetmanagement.Login.UserCredentials;
import com.example.assetmanagement.R;
import com.example.assetmanagement.Remote.RequestBuilder;
import com.example.assetmanagement.Remote.model.API_Interface;
import com.example.assetmanagement.Util.API_URLs;
import com.example.assetmanagement.Util.Config;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.zebra.sdk.comm.BluetoothConnection;
import com.zebra.sdk.comm.ConnectionException;
import com.zebra.sdk.printer.PrinterLanguage;
import com.zebra.sdk.printer.ZebraPrinter;
import com.zebra.sdk.printer.ZebraPrinterFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
//import java.sql.Connection;
import java.sql.SQLException;
import java.util.Set;
import com.example.assetmanagement.Util.Logger;

import com.zebra.sdk.comm.BluetoothConnection;
import com.zebra.sdk.comm.Connection;
import com.zebra.sdk.printer.PrinterLanguage;
import com.zebra.sdk.printer.ZebraPrinter;
import com.zebra.sdk.printer.PrinterLanguage;




public class QRCodeActivity extends AppCompatActivity {

    private ImageView ivQrCode;
    private Button btnPrintQr;
    private RequestBuilder requestBuilder;

    private static final String TAG = "ZebraPrinter";
    private static final int REQUEST_CODE = 1001;
    private BluetoothAdapter bluetoothAdapter;
    private Connection printerConnection;
    private ZebraPrinter zebraPrinter;
    private String PRINTER_MAC_ADDRESS = "60:95:32:2F:70:7A"; // Replace with your printer's MAC address
    private Button btnPrint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code);
        @SuppressLint("UseCompatLoadingForDrawables") Drawable d=getDrawable(R.drawable.card_gradient);
        getSupportActionBar().setBackgroundDrawable(d);
        requestBuilder = new RequestBuilder(this);
        Intent intent = getIntent();
        if (intent != null) {
            String assetType = intent.getStringExtra("ASSET_TYPE");
            String serialNumber = intent.getStringExtra("SERIAL_NUMBER");
            String assetCode = intent.getStringExtra("ASSET_CODE");
            String descp=intent.getStringExtra("DESCRIPTION");

            // Log values for debugging
            Log.d("QRCodeActivity", "Asset Type: " + assetType);
            Log.d("QRCodeActivity", "Serial Number: " + serialNumber);
            Log.d("QRCodeActivity", "Asset Code: " + assetCode);

            // Initialize views
            ivQrCode = findViewById(R.id.iv_qr_code);
            btnPrintQr = findViewById(R.id.btn_print_qr);
            TextView tvAssetType = findViewById(R.id.tv_asset_type);
            TextView tvAssetCode = findViewById(R.id.tv_asset_code);
            TextView tvSerialNumber = findViewById(R.id.tv_serial_number);

            tvAssetCode.setText(assetCode);
            tvAssetType.setText(assetType);
            tvSerialNumber.setText(serialNumber);
//            sendAPIRequest(assetCode, serialNumber, assetType);


            // Get QR code from Intent
//        if (intent != null && intent.hasExtra("QR_CODE_IMAGE")) {
//            byte[] byteArray = intent.getByteArrayExtra("QR_CODE_IMAGE");
//            Bitmap qrBitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
//            ivQrCode.setImageBitmap(qrBitmap);
//        } else {
//            Log.e("QRCodeActivity", "No QR Code image found in Intent");
//        }

            // Handle Print QR Code Button Click
//            new code
            connectToPrinter();
            btnPrintQr.setOnClickListener(v -> connectToPrinter());
//            old code
//            btnPrintQr.setOnClickListener(v -> {
//
//                Log.d("QRCodeActivity", "Print QR Code button clicked.");
//                Toast.makeText(this, "Print command executed", Toast.LENGTH_SHORT).show();
//                Intent intent1=new Intent(QRCodeActivity.this, DashboardActivity.class);
//                startActivity(intent1);
//                // Implement actual printing logic here if required
//            });
            Bitmap qrBitmap;
            if (Config.isDebugMode) {
                String uid="fd3e6b27-ef8a-4d84-8433-fc9657fd2654";
                qrBitmap = generateQRCodeWithText(uid,serialNumber);
                ivQrCode.setImageBitmap(qrBitmap);
            }
            else{
                sendAPIRequest(assetCode,serialNumber,assetType,descp);
            }

        }
    }

    public Bitmap generateQRCodeWithText(String uid, String serialNumber) {
        try {
            if (uid == null || uid.isEmpty()) {
                Log.e("QR_GENERATION", "UID is null or empty!");
                return null;
            }
            MultiFormatWriter writer = new MultiFormatWriter();
            BitMatrix bitMatrix = writer.encode(uid, BarcodeFormat.QR_CODE, 400, 400);
            if (bitMatrix == null) {
                Log.e("QR_GENERATION", "BitMatrix is null!");
                return null;
            }
            BarcodeEncoder encoder = new BarcodeEncoder();
            Bitmap qrBitmap = encoder.createBitmap(bitMatrix);
            if (qrBitmap == null) {
                Log.e("QR_GENERATION", "QR Bitmap is null!");
                return null;
            }

            // Create a new bitmap with space for text
            Bitmap combinedBitmap = Bitmap.createBitmap(qrBitmap.getWidth(), qrBitmap.getHeight() + 50, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(combinedBitmap);
            canvas.drawBitmap(qrBitmap, 0, 0, null);

            // Add text below QR code
            Paint paint = new Paint();
            paint.setColor(Color.BLACK);
            paint.setTextSize(40);
            paint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText("" + serialNumber, qrBitmap.getWidth() / 2, qrBitmap.getHeight() + 40, paint);

            return combinedBitmap;
        } catch (WriterException e) {
            e.printStackTrace();
            Log.e("QR_GENERATION", "Error generating QR code: " + e.getMessage());
            return null;
        }
    }

    private void sendAPIRequest(String assetCode, String serialNumber, String assetType, String d) {
        String apiUrl = API_URLs.generate_QR_url; // Replace with your API URL

        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("asset_code", assetCode);
            requestBody.put("serial_no", serialNumber);
            requestBody.put("asset_type", assetType);
            UserCredentials userCredentials = UserCredentials.getInstance(this);
            String userId = userCredentials.getUserId();
            requestBody.put("userId", userId);
            requestBody.put("description",d);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        requestBuilder.postRequest(apiUrl, requestBody, new API_Interface() {
            @Override
            public void onSuccess(JSONObject response) {
                Log.d("APIResponse", response.toString());

                try {
                    boolean success = response.getBoolean("success");
                    String message = response.getString("message");

                    if (success) {
                        JSONObject data = response.getJSONObject("data");
                        JSONArray qrArray = data.getJSONArray("QR");

                        if (qrArray.length() > 0) {
                            JSONObject qrObject = qrArray.getJSONObject(0);

                            if (qrObject.has("message")) {
                                // If the response contains a message like "Asset Code already assigned" or "Serial Number already exists"
                                showAlert(qrObject.getString("message"), assetCode, serialNumber,assetType);
                            } else {
                                // If a valid UID is returned, generate and display the QR code
                                String uid = qrObject.getString("UID");
                                Bitmap qrBitmap = generateQRCodeWithText(uid, serialNumber);
                                ivQrCode.setImageBitmap(qrBitmap);
//                                sendBitmapToPrinter(zebraPrinter, qrBitmap);
                                Toast.makeText(QRCodeActivity.this, "QR Code Generated!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        showErrorAndGoBack(message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    showErrorAndGoBack("Error parsing response");
                }
            }

            @Override
            public void onFailure(VolleyError error) {
                Log.e("APIError", "Error: " + error.getMessage());
                showErrorAndGoBack("API Call Failed");
            }
        });
    }

    private void showAlert(String message, String AssetCode, String SerialNumber, String AssetType ) {
        new android.app.AlertDialog.Builder(this)
                .setTitle("Alert")
                .setMessage(message)
                .setCancelable(false) // Prevents dismissing by tapping outside
                .setPositiveButton("OK", (dialog, which) -> {
                    dialog.dismiss();
                    finish(); // Go back to the previous screen
                })
                .setNegativeButton("Reprint", (dialog, which) -> {
                    // Call API again to get a new QR code
                    regenerateQRCode(AssetCode, SerialNumber, AssetType);
                    Toast.makeText(QRCodeActivity.this, "Reprinting QR Code...", Toast.LENGTH_SHORT).show();
                    dialog.dismiss(); // Close the alert dialog
                })
                .show();
    }

    private void showErrorAndGoBack(String message) {
        new android.app.AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage(message)
                .setCancelable(false) // Prevents dismissing by tapping outside
                .setPositiveButton("OK", (dialog, which) -> {
                    dialog.dismiss();
                    finish(); // Go back to the previous screen
                })
                .show();
    }

    private void regenerateQRCode(String AssetCode, String SerialNumber, String AssetType){
        String apiUrl = API_URLs.regenerateQRCode_url; // Replace with your API URL

        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("asset_code", AssetCode);
            requestBody.put("serial_no", SerialNumber);
            requestBody.put("asset_type", AssetType);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        requestBuilder.postRequest(apiUrl, requestBody, new API_Interface() {
            @Override
            public void onSuccess(JSONObject response) {
                Log.d("APIResponse", response.toString());

                try {
                    boolean success = response.getBoolean("success");
                    String message = response.getString("message");

                    if (success) {
                        JSONObject data = response.getJSONObject("data");
                        JSONArray qrArray = data.getJSONArray("QR");

                        if (qrArray.length() > 0) {
                            JSONObject qrObject = qrArray.getJSONObject(0);

                            if (qrObject.has("message")) {
                                // If response contains a message instead of UID, show alert and go back
                                showErrorAndGoBack(qrObject.getString("message"));
                            } else if (qrObject.has("UID")) {
                                // If UID is present, generate and display QR code
                                String uid = qrObject.getString("UID");
                                Bitmap qrBitmap = generateQRCodeWithText(uid,SerialNumber);
                                ivQrCode.setImageBitmap(qrBitmap);
                                Toast.makeText(QRCodeActivity.this, "QR Code Regenerated!", Toast.LENGTH_SHORT).show();

                                // Show alert with "Reprint" and "OK" options
                            }
                        }
                    } else {
                        // Handle failure case
                        showErrorAndGoBack(message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    showErrorAndGoBack("Error parsing response");
                }
            }

            @Override
            public void onFailure(VolleyError error) {
                Log.e("APIError", "Error: " + error.getMessage());
                showErrorAndGoBack("API Call Failed");
            }
        });
    }

    private void connectToPrinter() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Toast.makeText(this, "Enable Bluetooth first", Toast.LENGTH_SHORT).show();
//            return;
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN},
                    REQUEST_CODE);
        }


        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if (!pairedDevices.isEmpty()) {
            for (BluetoothDevice device : pairedDevices) {
                if (device.getAddress().equals(PRINTER_MAC_ADDRESS)) {
                    try {
                        printerConnection = new BluetoothConnection(PRINTER_MAC_ADDRESS);
                        try {
                            printerConnection.open();
                            zebraPrinter = ZebraPrinterFactory.getInstance(printerConnection);
                            PrinterLanguage pl = zebraPrinter.getPrinterControlLanguage();
                            Log.d("Printer", "Printer Language: " + pl);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e("Printer", "Error connecting to printer: " + e.getMessage());
                        }


//                        Log.d(TAG, "Connected to printer: " + pl.name());
                        Toast.makeText(this, "Connected to Zebra Printer!", Toast.LENGTH_SHORT).show();
                        Bitmap qrBitmap = generateQRCodeWithText("123456789", "SN12345");
                        sendBitmapToPrinter(zebraPrinter, qrBitmap);
                        printSampleLabel();
                        printerConnection.close();

                    } catch (Exception e) {
                        Log.e(TAG, "Could not connect to printer", e);
                        Toast.makeText(this, "Printer connection failed!"+e.getMessage(), Toast.LENGTH_LONG).show();
//                        Logger.writeLog("Bluetooth Connection Failed: " + e.getMessage());
                        Logger.writeLog(getApplicationContext(), "This is a test log entry.");
                    }
                    return;
                }
            }
        } else {
            Toast.makeText(this, "No paired Zebra printers found", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                // Both permissions granted, proceed with Bluetooth operations
                Toast.makeText(this, "Permissions granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permissions denied", Toast.LENGTH_SHORT).show();
            }
        }
    }




    private void printSampleLabel() {
        if (printerConnection == null || zebraPrinter == null) {
            Toast.makeText(this, "Printer not connected!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            String zplCommand = "^XA\n" +
                    "^FO50,50^A0N,50,50^FDHello Viren!^FS\n" +
                    "^FO50,150^B3N,N,100,Y,N^FD1234567890^FS\n" +
                    "^XZ";
            printerConnection.write(zplCommand.getBytes());
//            printerConnection.close();
            Toast.makeText(this, "Label Printed!", Toast.LENGTH_SHORT).show();
        } catch (ConnectionException e) {
            throw new RuntimeException(e);
        }
    }


}

