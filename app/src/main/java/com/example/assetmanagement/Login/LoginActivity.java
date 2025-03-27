package com.example.assetmanagement.Login;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.security.crypto.EncryptedSharedPreferences;

import com.android.volley.VolleyError;
import com.example.assetmanagement.Dashboard.DashboardActivity;
import com.example.assetmanagement.R;
import com.example.assetmanagement.Remote.RequestBuilder;
import com.example.assetmanagement.Remote.model.API_Interface;
import com.example.assetmanagement.Util.API_URLs;
import com.example.assetmanagement.Util.Config;

import java.io.IOException;
import java.security.GeneralSecurityException;


import androidx.security.crypto.MasterKey;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.zebra.sdk.printer.ZebraPrinter;



public class LoginActivity extends AppCompatActivity {

    private EditText etUsername, pass;
    private Button btnLogin;
    private SharedPreferences encryptedSharedPreferences;
    private Context context;
    private CheckBox rememberMeCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        context = this;
        assert context != null;
        @SuppressLint("UseCompatLoadingForDrawables") Drawable d=getDrawable(R.drawable.card_gradient);
        getSupportActionBar().setBackgroundDrawable(d);
        setContentView(R.layout.try_for_login);

        etUsername = findViewById(R.id.etUsername);
        pass = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        ImageView togglePassword = findViewById(R.id.toggle_password_visibility);
//        etUsername.setOnFocusChangeListener((v, hasFocus) -> {
//            if (hasFocus) {
//                showKeyboard(v);
//            }
//        });
//        View rootView = findViewById(android.R.id.content); // Gets the root view of the activity
//
////        rootView.setOnTouchListener((v, event) -> {
////            if (event.getAction() == MotionEvent.ACTION_DOWN) {
////                hideKeyboard();
////            }
////            return false; // Allow other touch events to proceed
////        });

        try {
            // Use MasterKey instead of MasterKeys.getOrCreate()
            MasterKey masterKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            encryptedSharedPreferences = EncryptedSharedPreferences.create(
                    context,  // Change file name if needed
                    "login_prefs",      // Secure master key
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException | IOException e) {
            Log.e(TAG, "onCreateView: " + e.getMessage());  // Remove BuildConfig.DEBUG if not needed
        }

        prefillCredentials(findViewById(android.R.id.content));

        togglePassword.setOnClickListener(v ->
        {
            if (pass.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                pass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                togglePassword.setImageResource(R.drawable.ic_eye); // Update to open-eye icon
            } else {
                pass.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                togglePassword.setImageResource(R.drawable.ic_eye_closed); // Update to closed-eye icon
            }
            pass.setSelection(pass.getText().length()); // Keep cursor at the end
        });
        rememberMeCheckBox = findViewById(R.id.remember_me_checkbox);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get user input from EditText fields
                String username = etUsername.getText().toString().trim();
                String password = pass.getText().toString().trim();

                // Validate inputs
                if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
                    Toast.makeText(LoginActivity.this, "Please enter both Username and Password", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Call validation function (replace with actual API call if needed)

                    if (Config.isDebugMode) {
                    UserCredentials userCredentials = UserCredentials.getInstance(context);
                    userCredentials.saveCredentials(username, password);

                    if (rememberMeCheckBox.isChecked()) {
                        encryptedSharedPreferences.edit()
                                .putString("user_id", username)
                                .putString("password", password)
                                .apply();
                    } else {
                        encryptedSharedPreferences.edit().clear().apply();
                    }
                    Intent intent=new Intent(LoginActivity.this,DashboardActivity.class);
                    startActivity(intent);
                } else {
                    validateCredentials(username, password);
                }


            }
        });

    }

    private void prefillCredentials(View view) {
        String savedUserId = encryptedSharedPreferences.getString("user_id", null);
        String savedPassword = encryptedSharedPreferences.getString("password", null);

        if (savedUserId != null && savedPassword != null) {
            // Prefill the EditText fields
            EditText userIdField = view.findViewById(R.id.etUsername);
            EditText passwordField = view.findViewById(R.id.etPassword);

            userIdField.setText(savedUserId);
            passwordField.setText(savedPassword);
        }
    }

    private void validateCredentials(String username, String password) {
        ProgressBar progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);
        String url = API_URLs.validate_login_url;
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("userId", username);
            jsonBody.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBuilder requestBuilder = new RequestBuilder(this);
        requestBuilder.postRequest(url, jsonBody, new API_Interface() {
            @Override
            public void onSuccess(JSONObject response) {
                Log.i(TAG, "API Success: " + response.toString());
                progressBar.setVisibility(View.GONE);

                try {
                    boolean success = response.getBoolean("success");
                    String message = response.getString("message");

                    if (success && "Yes".equalsIgnoreCase(message)) {
                        JSONObject data = response.getJSONObject("data");
                        JSONArray usersArray = data.getJSONArray("User");

                        if (usersArray.length() > 0) {
                            JSONObject userObject = usersArray.getJSONObject(0);
                            String userId = userObject.getString("id");
                            String name = userObject.getString("name");
                            String email = userObject.getString("email");
                            String exist = userObject.getString("exist");

                            if ("Yes".equalsIgnoreCase(exist)) {
                                Toast.makeText(context, "Credentials are valid", Toast.LENGTH_SHORT).show();
                                Log.i("API Response", "User exists: " + name);

                                // Save user credentials
                                UserCredentials userCredentials = UserCredentials.getInstance(context);
                                userCredentials.saveCredentials(username, password);

                                if (rememberMeCheckBox.isChecked()) {
                                    encryptedSharedPreferences.edit()
                                            .putString("user_id", username)
                                            .putString("password", password)
                                            .apply();
                                } else {
                                    encryptedSharedPreferences.edit().clear().apply();
                                }

                                navigateToTabsScreen();
                            } else {
                                Toast.makeText(context, "User does not exist", Toast.LENGTH_SHORT).show();
                                Log.i("API Response", "User does not exist");
                            }
                        } else {
                            Toast.makeText(context, "No user data received", Toast.LENGTH_SHORT).show();
                            Log.i("API Response", "Empty user array");
                        }
                    } else {
                        Toast.makeText(context, "Invalid credentials", Toast.LENGTH_SHORT).show();
                        Log.i("API Response", "Invalid credentials");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Error parsing response", Toast.LENGTH_SHORT).show();
                    Log.e("API Error", "JSON Parsing Error: " + e.toString());
                }
            }


            @Override
            public void onFailure(VolleyError error) {
                Log.e(TAG, "API Failure: " + error.toString());

                progressBar.setVisibility(View.GONE);
                String errorMessage = "An error occurred";
                if (error.networkResponse != null) {
                    errorMessage = "Error Code: " + error.networkResponse.statusCode;
                } else if (error instanceof com.android.volley.TimeoutError) {
                    errorMessage = "Request Timeout. Please try again.";
                } else if (error instanceof com.android.volley.NoConnectionError) {
                    errorMessage = "No Internet connection.";
                }

                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
                Log.e("API Error", error.toString());
            }
        });
    }

    private void navigateToTabsScreen() {
        Intent intent=new Intent(LoginActivity.this,DashboardActivity.class);
        startActivity(intent);
    }
//    private void hideKeyboard() {
//        View view = getCurrentFocus(); // Get the current focused view
//        if (view != null) {
//            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//            if (imm != null) {
//                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
//            }
//        }
//    }
//    private void showKeyboard(View view) {
//        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        if (imm != null) {
//            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
//        }
//    }
}