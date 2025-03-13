package com.example.assetmanagement.Remote;



import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.assetmanagement.Remote.model.API_Interface;


import org.json.JSONObject;



public class RequestBuilder {
    private RequestQueue requestQueue;

    public RequestBuilder(Context context) {
        requestQueue = Volley.newRequestQueue(context);
    }

    // POST Request
    public void postRequest(String endpoint, JSONObject requestBody, API_Interface callback) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, endpoint, requestBody,
                response -> {
                    if (callback != null) {
                        callback.onSuccess(response);
                    }
                },
                error -> {
                    if (callback != null) {
                        callback.onFailure(error);
                    }
                });

        requestQueue.add(jsonObjectRequest);
    }

    // GET Request
    public void getRequest(String endpoint, API_Interface callback) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, endpoint, null,
                response -> {
                    if (callback != null) {
                        callback.onSuccess(response);
                    }
                },
                error -> {
                    if (callback != null) {
                        callback.onFailure(error);
                    }
                });

        requestQueue.add(jsonObjectRequest);
    }
}

