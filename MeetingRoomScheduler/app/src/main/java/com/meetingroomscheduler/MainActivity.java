package com.meetingroomscheduler;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.meetingroomscheduler.Activity.AdminPage;
import com.meetingroomscheduler.Activity.ReadOnlyUserPage;
import com.meetingroomscheduler.Activity.ReadWriteUserPage;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    // Fields in the login screen
    EditText edit_email, edit_password;
    ProgressBar progressbar;
    TextView submit;

    // boolean variable to indicate if we sent the requst
    boolean sent = false;


    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("MainActivity.class", "onCreate()");

        sent = false;

        edit_email = (EditText) findViewById(R.id.main_edit_email);
        edit_password = (EditText) findViewById(R.id.main_edit_password);
        progressbar = (ProgressBar) findViewById(R.id.main_progressbar);
        submit = (TextView) findViewById(R.id.main_submit);

        prefs = this.getSharedPreferences("m_r_schedule", Context.MODE_PRIVATE);
        editor = prefs.edit();

        Global.email = prefs.getString("email", "");
        Global.password = prefs.getString("password", "");

        edit_email.setText(Global.email);
        edit_password.setText(Global.password);

        // submit click
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                edit_email.setError(null);
                edit_password.setError(null);

                if(sent) return;

                Global.email = edit_email.getText().toString();

                // mandatory email validation
                if(Global.email.length() == 0){
                    edit_email.setError("All fields are mandatory");
                    return;
                }

                // mandatory password validation
                Global.password = edit_password.getText().toString();
                if(Global.password.length() == 0){
                    edit_password.setError("All fields are mandatory");
                    return;
                }

                authenticateUser();
            }
        });

    }

    // user authentication function
    private void authenticateUser(){

        Log.d("MainActivity.class", "authenticateUser()");
        sent = true;
        progressbar.setVisibility(View.VISIBLE);

        // create hash map to store the login action with email and password parameters
        Map<String,String> map = new HashMap<>();
        map.put("email", Global.email);
        map.put("password",Global.password);
        map.put("action", "login_user");
        String params = new JSONObject(map).toString();

        String response = Global.query(params);

        Log.d("MainActivity.class", "response: " + response);
        progressbar.setVisibility(View.GONE);
        sent = false;

        if(response.equals("fail")){
            Toast.makeText(MainActivity.this, "Error, please make sure there is internet connection and retry", Toast.LENGTH_LONG).show();
        }else {
            try {
                JSONObject json = new JSONObject(response);

                if (json.getString("status").equals("true")) {
                    editor.putString("email", Global.email);
                    editor.putString("password", Global.password);
                    editor.commit();
                    Toast.makeText(MainActivity.this, json.getString("message"), Toast.LENGTH_SHORT).show();
                    Global.current_user.id = json.getString("id");
                    Global.current_user.email = Global.email;
                    Global.current_user.type = json.getString("type");
                    Global.current_user.fullname = json.getString("fullname");
                    if (Global.current_user.type.equals("admin")) {
                        startActivity(new Intent(MainActivity.this, AdminPage.class));
                        finish();
                    } else if (Global.current_user.type.equals("read_write")) {
                        startActivity(new Intent(MainActivity.this, ReadWriteUserPage.class));
                        finish();
                    } else if (Global.current_user.type.equals("read_only")) {
                        startActivity(new Intent(MainActivity.this, ReadOnlyUserPage.class));
                        finish();
                    }
                } else {
                    Toast.makeText(MainActivity.this, json.getString("message"), Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

}
