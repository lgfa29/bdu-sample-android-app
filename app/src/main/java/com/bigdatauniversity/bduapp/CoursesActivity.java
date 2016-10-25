package com.bigdatauniversity.bduapp;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bigdatauniversity.bduapp.api.BDUAPI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class CoursesActivity extends AppCompatActivity {

    private ListView mCoursesList;
    private ArrayAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courses);

        // create list adapter
        mAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                android.R.id.text1);

        // set adapter
        mCoursesList = (ListView) findViewById(R.id.coursesList);
        mCoursesList.setAdapter(mAdapter);

        // get user's enrolled courses
        BDUAPI.getInstance(this).getEnrolledCourses(
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            // parse JSON repsonse and add course ids to ListView
                            JSONArray courses = new JSONArray(response);
                            for (int i = 0; i < courses.length(); i++) {
                                JSONObject course = courses.getJSONObject(i);

                                mAdapter.add(course
                                        .getJSONObject("course_details")
                                        .getString("course_id"));
                            }

                            mAdapter.notifyDataSetChanged();

                        } catch (JSONException e) {
                            Toast.makeText(CoursesActivity.this, "Failed to parse response", Toast.LENGTH_LONG).show();
                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String body = new String(error.networkResponse.data, Charset.forName("UTF-8"));
                        Toast.makeText(CoursesActivity.this, body, Toast.LENGTH_LONG).show();
                    }
                });
    }
}
