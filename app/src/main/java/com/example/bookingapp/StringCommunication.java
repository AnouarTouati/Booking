package com.example.bookingapp;

import android.support.annotation.Nullable;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.Map;

public class StringCommunication extends StringRequest {
    Map<String, String> params;

    public StringCommunication(int method, String url, Map<String, String> data, Response.Listener<String> listener, @Nullable Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
        params = data;

    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
