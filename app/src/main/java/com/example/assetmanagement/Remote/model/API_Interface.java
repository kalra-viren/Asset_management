package com.example.assetmanagement.Remote.model;


import com.android.volley.VolleyError;

import org.json.JSONObject;

public interface API_Interface {
    void onSuccess(JSONObject response);
    void onFailure(VolleyError error);
}

