package com.meetingroomscheduler.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.meetingroomscheduler.Adapter.ListUsersAdapter;
import com.meetingroomscheduler.Class.Invitation;
import com.meetingroomscheduler.Class.Schedule;
import com.meetingroomscheduler.Class.User;
import com.meetingroomscheduler.Global;
import com.meetingroomscheduler.MainActivity;
import com.meetingroomscheduler.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Exchanger;

/**
 * manage users
 *
 * Admin user can add, edit and delete users
 */

public class ManageUsers  extends AppCompatActivity {

    TextView back, add_user;

    public static ProgressBar progressbar;

    public static ListView listview;
    public static ListUsersAdapter adapter;

    boolean sent = false;

    public static ArrayList<User> users_list;

    public static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
       // display view
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_manage_users_layout);

        context = ManageUsers.this;

        sent = false;
        paused = false;

        if(Global.requestQueue == null) {
            Global.requestQueue = Volley.newRequestQueue(this);
        }

        // Back button
        back  = (TextView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Add user button
        add_user = (TextView) findViewById(R.id.add);
        add_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ManageUsers.this, CreateUser.class));
            }
        });

        progressbar = (ProgressBar) findViewById(R.id.manage_users_progressbar);
        listview = (ListView) findViewById(R.id.listview);

        getUsers();

    }

    boolean paused = false;

    @Override public void onPause(){
        super.onPause();
        paused = true;
    }

    @Override public void onResume(){
        super.onResume();
        if(paused){
            paused = false;
            getUsers();
        }
    }

    void getUsers(){

        progressbar.setVisibility(View.VISIBLE);
        sent = true;
        // create hashmap with all elements
        Map<String,String> map = new HashMap<>();
        map.put("email", Global.email);
        map.put("password",Global.password);
        map.put("action", "get_users_list");
        String params = new JSONObject(map).toString();
        // send request and store response
        String response = Global.query(params);

        progressbar.setVisibility(View.GONE);
        sent = false;

        // if failed show error if not show all users
        if(response.equals("fail")){
            Toast.makeText(ManageUsers.this, "Error, please make sure there is internet connection and retry", Toast.LENGTH_LONG).show();
        }else {
            users_list = new ArrayList<User>();
            try {
                JSONArray json = new JSONArray(response);

                for(int i=0;i<json.length();i++){
                    JSONObject json_item = new JSONObject(json.get(i).toString());

                    // display users one by one
                    User new_item = new User();
                    new_item.id = json_item.getString("id");
                    new_item.entry_date = json_item.getString("entry_date");
                    new_item.email = json_item.getString("email");
                    new_item.fullname = json_item.getString("fullname");
                    new_item.type = json_item.getString("type");

                    users_list.add(new_item);

                }

                renderList();

            }catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    public static void renderList(){

        if(users_list.size() > 0){
            adapter = new ListUsersAdapter(context, users_list);
            listview.setAdapter(adapter);
        }

    }

}
