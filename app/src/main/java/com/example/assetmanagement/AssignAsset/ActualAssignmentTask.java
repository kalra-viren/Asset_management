package com.example.assetmanagement.AssignAsset;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.VolleyError;
import com.example.assetmanagement.Dashboard.DashboardActivity;
import com.example.assetmanagement.Login.UserCredentials;
import com.example.assetmanagement.Remote.RequestBuilder;
import com.example.assetmanagement.Remote.model.API_Interface;
import com.example.assetmanagement.R;
import com.example.assetmanagement.Util.API_URLs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ActualAssignmentTask extends AppCompatActivity {
    private TextView tvAssetCode, tvSerialNumber, tvAssetName;
    private Spinner spinnerLocation;
    private Spinner spOwnership;
    private Button btnSubmit;
    private RequestBuilder requestBuilder;
    private String scannedUID;
    private Context context;
    HashMap<String, Integer> globalLocationMap;
    HashMap<String, String> userMap;
    private String selectedLocation_body;
    private String userName_body;
    private LinearLayout spinnerLinearLayout;
    private TextView tv_selected_loc;
    private TextView tv_selected_own;
    private LinearLayout ownershipLinearLayout;
    private Button btnReassign;
    private AutoCompleteTextView autoCompleteLocation;
    private AutoCompleteTextView autoCompleteOwnership;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actual_assignment);

        context = this;
        @SuppressLint("UseCompatLoadingForDrawables") Drawable d=getDrawable(R.drawable.card_gradient);
        getSupportActionBar().setBackgroundDrawable(d);
        requestBuilder = new RequestBuilder(this);
        globalLocationMap = new HashMap<>();
        userMap = new HashMap<>();
        // Initialize Views
        tvAssetCode = findViewById(R.id.tv_asset_code);
        tvSerialNumber = findViewById(R.id.tv_serial_number);
        tvAssetName = findViewById(R.id.tv_asset_name);
//        spinnerLocation = findViewById(R.id.spinner_location);
//        spOwnership = findViewById(R.id.et_ownership);
        autoCompleteLocation = findViewById(R.id.autoCompleteLocation);
        autoCompleteOwnership = findViewById(R.id.autoCompleteOwnership);
        btnSubmit = findViewById(R.id.btn_submit);
        spinnerLinearLayout = findViewById(R.id.layout_spinner_section);
        tv_selected_loc = findViewById(R.id.tv_selected_loc);
        tv_selected_own = findViewById(R.id.tv_selected_ownership);
        ownershipLinearLayout = findViewById(R.id.layout_ownership);
        btnReassign = findViewById(R.id.btn_reassign);
        autoCompleteLocation = findViewById(R.id.autoCompleteLocation);

        // Get UID from previous screen
        scannedUID = getIntent().getStringExtra("SCANNED_UID");

        // Populate Spinner with sample locations
//        List<String> locations = Arrays.asList("Mumbai", "Delhi", "Bangalore", "Chennai", "Parinee BKC");
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, locations);
//        spinnerLocation.setAdapter(adapter);

        // Fetch asset details


        fetchAssetDetails();
        fetchLocations();


        // Submit Button Click Listener
        btnSubmit.setOnClickListener(v -> showConfirmationDialog());
        btnReassign.setOnClickListener(v -> showConfirmationDialogForReassign());
    }

    // Fetch asset details from API
    private void fetchAssetDetails() {
        String apiUrl = API_URLs.assign_fetch_details_url; // Replace with actual URL

        try {
            JSONObject requestBody = new JSONObject();
            requestBody.put("uid", scannedUID);
            requestBody.put("type", "Assign");

            requestBuilder.postRequest(apiUrl, requestBody, new API_Interface() {
                @Override
                public void onSuccess(JSONObject response) {
                    try {
                        if (response.getBoolean("success")) {
                            JSONArray assetArray = response.getJSONObject("data").getJSONArray("Asset Details");
                            if (assetArray.length() > 0) {
                                JSONObject asset = assetArray.getJSONObject(0);
                                if (asset.has("Username") && asset.has("Name") && asset.has("Location")) {

                                    String username = asset.getString("Username");
                                    String name = asset.getString("Name");
                                    String location = asset.getString("Location");
                                    new AlertDialog.Builder(context)
                                            .setTitle("Asset Already Assigned")
                                            .setMessage("This asset has already been assigned to: " + name + " (" + username + ") at " + location)
                                            .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                                            .show();
                                    spinnerLinearLayout.setVisibility(View.VISIBLE);
                                    ownershipLinearLayout.setVisibility(View.VISIBLE);
                                    tv_selected_loc.setText("Selected Location: " + location);
                                    tv_selected_own.setText("Selected Ownership: " + username);
                                    btnSubmit.setVisibility(View.GONE);
                                    btnReassign.setVisibility(View.VISIBLE);
                                }
                                tvAssetCode.setText("Asset Code: " + asset.getString("Asset_Code"));
                                tvSerialNumber.setText("Serial Number: " + asset.getString("Serial_Number"));
                                tvAssetName.setText("Asset Name: " + asset.getString("Asset_Name"));
                            }
                        } else {
                            Toast.makeText(context, "Failed to fetch asset details", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(context, "Error parsing response", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(VolleyError error) {
                    Toast.makeText(context, "API Request Failed", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // Show Confirmation Dialog
    private void showConfirmationDialog() {
        // Get selected values from spinners
        String selectedLocation = autoCompleteLocation.getText().toString().trim();
        String selectedOwnership = autoCompleteOwnership.getText().toString().trim();

        // Check if spinners are filled correctly
//        if (selectedLocation.equals("Select Location") || selectedOwnership.equals("Select Ownership")) {
//            new AlertDialog.Builder(this)
//                    .setTitle("Incomplete Selection")
//                    .setMessage("Please select both location and ownership before proceeding.")
//                    .setPositiveButton("OK", null)
//                    .show();
//        } else {
//            // Show confirmation dialog if selections are valid
//            new AlertDialog.Builder(this)
//                    .setTitle("Confirm Assignment")
//                    .setMessage("Are you sure you want to assign this asset?")
//                    .setPositiveButton("Yes", (dialog, which) -> assignAsset())
//                    .setNegativeButton("No", null)
//                    .show();
//        }
        if (selectedLocation.isEmpty() || selectedOwnership.isEmpty() ||
                selectedLocation.equals("Select Location") || selectedOwnership.equals("Select Ownership")) {
            new AlertDialog.Builder(this)
                    .setTitle("Incomplete Selection")
                    .setMessage("Please select both location and ownership before proceeding.")
                    .setPositiveButton("OK", null)
                    .show();
        } else {
            // Show confirmation dialog if selections are valid
            new AlertDialog.Builder(this)
                    .setTitle("Confirm Assignment")
                    .setMessage("Are you sure you want to assign this asset?")
                    .setPositiveButton("Yes", (dialog, which) -> assignAsset())
                    .setNegativeButton("No", null)
                    .show();
        }
    }

    private void showConfirmationDialogForReassign() {
        String selectedLocation = autoCompleteLocation.getText().toString().trim();
        String selectedOwnership = autoCompleteOwnership.getText().toString().trim();

        // Check if spinners are filled correctly
//        if (selectedLocation.equals("Select Location") || selectedOwnership.equals("Select Ownership")) {
//            new AlertDialog.Builder(this)
//                    .setTitle("Incomplete Selection")
//                    .setMessage("Please select both location and ownership before proceeding.")
//                    .setPositiveButton("OK", null)
//                    .show();
//        } else {
//            // Show confirmation dialog if selections are valid
//            new AlertDialog.Builder(this)
//                    .setTitle("Confirm Assignment")
//                    .setMessage("Are you sure you want to assign this asset?")
//                    .setPositiveButton("Yes", (dialog, which) -> reAssign())
//                    .setNegativeButton("No", null)
//                    .show();
//        }
        if (selectedLocation.isEmpty() || selectedOwnership.isEmpty() ||
                selectedLocation.equals("Select Location") || selectedOwnership.equals("Select Ownership")) {
            new AlertDialog.Builder(this)
                    .setTitle("Incomplete Selection")
                    .setMessage("Please select both location and ownership before proceeding.")
                    .setPositiveButton("OK", null)
                    .show();
        } else {
            // Show confirmation dialog if selections are valid
            new AlertDialog.Builder(this)
                    .setTitle("Confirm Assignment")
                    .setMessage("Are you sure you want to assign this asset?")
                    .setPositiveButton("Yes", (dialog, which) -> reAssign())
                    .setNegativeButton("No", null)
                    .show();
        }
    }

    private void assignAsset() {
        try {
            JSONObject requestBody = new JSONObject();
            requestBody.put("uid", scannedUID); //read the comment below.
//            put loation and ownership from the apis
            requestBody.put("location", selectedLocation_body);
            requestBody.put("empId", userName_body);
            UserCredentials userCredentials = UserCredentials.getInstance(this);
            String s = userCredentials.getUserId();
            requestBody.put("userId", s); // Static user ID, change as needed
            requestBody.put("type", "Assign");

            String endpoint = API_URLs.assign_update_details_url; // Replace with actual API URL

            requestBuilder.postRequest(endpoint, requestBody, new API_Interface() {
                @Override
                public void onSuccess(JSONObject response) {
                    try {
                        String message = response.getJSONObject("data")
                                .getJSONArray("Update")
                                .getJSONObject(0)
                                .getString("message");

                        if ("Assignment Successful".equals(message)) {
                            Toast.makeText(ActualAssignmentTask.this, "Assignment Successful", Toast.LENGTH_SHORT).show();
                            openNextScreen();
                        } else {
                            Toast.makeText(ActualAssignmentTask.this, "Assignment Failed", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(ActualAssignmentTask.this, "Error processing response", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(VolleyError error) {
                    Toast.makeText(ActualAssignmentTask.this, "Assignment failed", Toast.LENGTH_SHORT).show();
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void reAssign() {
        try {
            JSONObject requestBody = new JSONObject();
            requestBody.put("uid", scannedUID); //read the comment below.
//            put loation and ownership from the apis
            requestBody.put("location", selectedLocation_body);
            requestBody.put("empId", userName_body);
            UserCredentials userCredentials = UserCredentials.getInstance(this);
            String s = userCredentials.getUserId();
            requestBody.put("userId", s); // Static user ID, change as needed
            requestBody.put("type", "Reassign");

            String endpoint = API_URLs.assign_update_details_url; // Replace with actual API URL

            requestBuilder.postRequest(endpoint, requestBody, new API_Interface() {
                @Override
                public void onSuccess(JSONObject response) {
                    try {
                        if (response.getBoolean("success")) {
                            JSONArray updateArray = response.getJSONObject("data").getJSONArray("Update");
                            if (updateArray.length() > 0) {
                                String message = updateArray.getJSONObject(0).getString("message");

                                switch (message) {
                                    case "Reassignment Successful":
                                        Toast.makeText(ActualAssignmentTask.this, "Reassignment Successful", Toast.LENGTH_SHORT).show();
                                        openNextScreen();
                                        break;
                                    case "Asset cannot be reassigned to same employee":
                                        Toast.makeText(ActualAssignmentTask.this, "Asset cannot be reassigned to the same employee", Toast.LENGTH_LONG).show();
                                        break;
                                    case "Location mismatch":
                                        Toast.makeText(ActualAssignmentTask.this, "Location mismatch! Please verify.", Toast.LENGTH_LONG).show();
                                        break;
                                    case "User not found":
                                        Toast.makeText(ActualAssignmentTask.this, "User not found! Please check the details.", Toast.LENGTH_LONG).show();
                                        break;
                                    default:
                                        Toast.makeText(ActualAssignmentTask.this, "Unexpected response: " + message, Toast.LENGTH_LONG).show();
                                        break;
                                }
                            } else {
                                Toast.makeText(ActualAssignmentTask.this, "No update information received.", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(ActualAssignmentTask.this, "Update process failed!", Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(ActualAssignmentTask.this, "Error processing response", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(VolleyError error) {
                    Toast.makeText(ActualAssignmentTask.this, "API Request Failed: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // Open Next Screen
    private void openNextScreen() {
        Intent intent = new Intent(ActualAssignmentTask.this, DashboardActivity.class);
        startActivity(intent);
    }

    private void fetchLocations() {
        String apiUrl = API_URLs.fetch_locations_url; // Replace with actual API URL

        requestBuilder.getRequest(apiUrl, new API_Interface() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    if (response.getBoolean("success")) {
                        JSONArray locationArray = response.getJSONObject("data").getJSONArray("Locations");

                        // HashMap to store Location as Key and ID as Value
                        HashMap<String, Integer> locationMap = new HashMap<>();
                        List<String> locationNames = new ArrayList<>();

                        for (int i = 0; i < locationArray.length(); i++) {
                            JSONObject locationObject = locationArray.getJSONObject(i);
                            int id = locationObject.getInt("Id");
                            String location = locationObject.getString("Location");

                            locationMap.put(location, id);
                            locationNames.add(location); // Adding names for the spinner
                        }

                        // Store the locationMap for future use (Optional)
                        globalLocationMap = locationMap;

                        // Update Spinner with fetched location names
                        updateSpinner(autoCompleteLocation, locationNames);

                        autoCompleteLocation.setOnItemClickListener((parent, view, position, id) -> {
                            selectedLocation_body = (String) parent.getItemAtPosition(position);
                            if (globalLocationMap.containsKey(selectedLocation_body)) {
                                int locId = globalLocationMap.get(selectedLocation_body);
                                fetchUsersByLocation(locId); // Fetch users based on selected location
                            }
                        });
//                        spinnerLocation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                            @Override
//                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                                selectedLocation_body = parent.getItemAtPosition(position).toString();
//                                int locId = globalLocationMap.get(selectedLocation_body); // Get loc_id
//                                fetchUsersByLocation(locId); // Call function to get users
//                            }
//
//                            @Override
//                            public void onNothingSelected(AdapterView<?> parent) {
//                            }
//                        });

                    } else {
                        Toast.makeText(context, "Failed to fetch locations", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Error parsing response", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(VolleyError error) {
                Toast.makeText(context, "API Request Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchUsersByLocation(int locId) {
        String apiUrl = API_URLs.fetchUsersByLocation_url + locId;
        requestBuilder.getRequest(apiUrl, new API_Interface() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    if (response.getBoolean("success")) {
                        JSONArray usersArray = response.getJSONObject("data").getJSONArray("Users");

                        userMap.clear(); // Clear previous data
                        List<String> userNames = new ArrayList<>();

                        for (int i = 0; i < usersArray.length(); i++) {
                            JSONObject userObject = usersArray.getJSONObject(i);
                            String name = userObject.getString("Name");
                            String username = userObject.getString("Username");

                            userMap.put(name, username);
                            userNames.add(name);
                        }

                        // Populate the user spinner
                        updateSpinner(autoCompleteOwnership, userNames);

//                        spOwnership.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                            @Override
//                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                                String name = parent.getItemAtPosition(position).toString();
//                                userName_body = userMap.get(name); // Get loc_id
//                            }
//
//                            @Override
//                            public void onNothingSelected(AdapterView<?> parent) {
//                            }
//                        });
                        autoCompleteOwnership.setOnItemClickListener((parent, view, position, id) -> {
                            String name = (String) parent.getItemAtPosition(position);
                            if (userMap.containsKey(name)) {
                                userName_body = userMap.get(name); // Get loc_id
                            }
                        });


                    } else {
                        Toast.makeText(context, "No users found for this location", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Error parsing user response", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(VolleyError error) {
                Toast.makeText(context, "API Request Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateSpinner(AutoCompleteTextView autoCompleteTextView, List<String> dataList) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, dataList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        autoCompleteTextView.setAdapter(adapter);
        autoCompleteTextView.setThreshold(1);
        autoCompleteTextView.setOnClickListener(v -> autoCompleteTextView.showDropDown());

// Show dropdown when it gains focus
        autoCompleteTextView.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                autoCompleteTextView.showDropDown();
            }
        });

    }

}

