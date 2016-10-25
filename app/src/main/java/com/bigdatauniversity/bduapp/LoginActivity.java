package com.bigdatauniversity.bduapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bigdatauniversity.bduapp.api.BDUAPI;

import java.nio.charset.Charset;

public class LoginActivity extends AppCompatActivity {

    private EditText mEmailField;
    private EditText mPasswordField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmailField = (EditText) findViewById(R.id.email);
        mPasswordField = (EditText) findViewById(R.id.password);
        Button loginButton = (Button) findViewById(R.id.loginButton);

        // setup button click event listener
        loginButton.setOnClickListener(new LoginHandler());
    }

    private class LoginHandler implements View.OnClickListener {
        private String email;
        private String password;

        public void onClick(View v) {
            final Context context = LoginActivity.this;
            BDUAPI bduApi = BDUAPI.getInstance(context);

            email = mEmailField.getText().toString();
            password = mPasswordField.getText().toString();

            bduApi.authenticate(email, password,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Toast.makeText(context, "Got token", Toast.LENGTH_LONG).show();

                            Intent intent = new Intent(context, CoursesActivity.class);
                            startActivity(intent);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            String body = new String(error.networkResponse.data, Charset.forName("UTF-8"));
                            Toast.makeText(context, body, Toast.LENGTH_LONG).show();
                        }
                    });

        }
    }
}
