package com.bigdatauniversity.bduapp.api;

import android.content.Context;
import android.content.SharedPreferences;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bigdatauniversity.bduapp.BuildConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by laoqui on 2016-10-24.
 */

public class BDUAPI {

    public static final String PREFS_NAME = "BDUAPIPrefs";
    public static final String PREFS_OAUTH_TOKEN_KEY = "oauthToken";

    private static BDUAPI mInstance;
    private RequestQueue mRequestQueue;
    private static Context mContext;

    private BDUAPI(Context context) {
        mContext = context;
        mRequestQueue = getRequestQueue();
    }

    public static synchronized BDUAPI getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new BDUAPI(context);
        }

        return mInstance;
    }

    public void authenticate(final String email, final String password, final Response.Listener<String> onSuccess, final Response.ErrorListener onError) {
        if (email.isEmpty() || password.isEmpty()) {
            VolleyError error = new VolleyError(new String("Email and Password are required."));
            onError.onErrorResponse(error);
            return;
        }

        StringRequest oauthTokenRequest = new StringRequest(
                Request.Method.POST,
                BuildConfig.BDU_URL + "/oauth2/access_token",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject resp = new JSONObject(response);
                            String token = resp.getString("access_token");

                            SharedPreferences settings = mContext.getSharedPreferences(PREFS_NAME, 0);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putString(PREFS_OAUTH_TOKEN_KEY, token);
                            editor.commit();

                            onSuccess.onResponse(response);

                        } catch (JSONException e) {
                            VolleyError error = new VolleyError("Failed to parse response.");
                            onError.onErrorResponse(error);
                        }
                    }
                },
                onError) {

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("client_id", BuildConfig.BDU_CLIENT_ID);
                params.put("grant_type", "password");
                params.put("username", email);
                params.put("password", password);

                return params;
            }
        };

        getRequestQueue().add(oauthTokenRequest);
    }

    public void getEnrolledCourses(Response.Listener<String> onSuccess, Response.ErrorListener onError) {

        StringRequest enrolledCoursesRequest = new StringRequest(
                Request.Method.GET,
                BuildConfig.BDU_URL + "/api/enrollment/v1/enrollment",
                onSuccess,
                onError) {
            @Override
            public Map<String, String> getHeaders() {
                SharedPreferences settings = mContext.getSharedPreferences(PREFS_NAME, 0);
                String oauthToken = settings.getString(PREFS_OAUTH_TOKEN_KEY, "");

                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "Bearer " + oauthToken);

                return headers;
            }
        };

        getRequestQueue().add(enrolledCoursesRequest);
    }

    private RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        }

        return  mRequestQueue;
    }
}
