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
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.meetingroomscheduler.Adapter.ListRoomsAdapter;
import com.meetingroomscheduler.Adapter.ListUsersAdapter;
import com.meetingroomscheduler.Class.Room;
import com.meetingroomscheduler.Class.User;
import com.meetingroomscheduler.Global;
import com.meetingroomscheduler.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Manage rooms
 *
 * Admin has the option to add edit and delete rooms
 */

public class ManageRooms extends AppCompatActivity {

    TextView back, add_room;

    public static ProgressBar progressbar;

    public static ListView listview;
    public static ListRoomsAdapter adapter;

    boolean sent = false;

    public static ArrayList<Room> rooms_list;

    public static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // display view
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_manage_rooms_layout);

        context = ManageRooms.this;

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

        // Add room button
        add_room = (TextView) findViewById(R.id.add);
        add_room.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ManageRooms.this, CreateRoom.class));
            }
        });

        progressbar = (ProgressBar) findViewById(R.id.manage_rooms_progressbar);
        listview = (ListView) findViewById(R.id.listview);

        getRooms();

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
            getRooms();
        }
    }

    void getRooms() {

        Log.d("MainActivity.class", "getRooms()");

        sent = true;
        progressbar.setVisibility(View.VISIBLE);

        Map<String, String> map = new HashMap<>();
        map.put("email", Global.email);
        map.put("password", Global.password);
        map.put("action", "get_rooms_list");
        String params = new JSONObject(map).toString();
        Log.d("MainActivity.class", "params: " + params);
        String response = Global.query(params);

        Log.d("MainActivity.class", "response: " + response);
        progressbar.setVisibility(View.GONE);
        sent = false;

        if (response.equals("fail")) {
            Toast.makeText(ManageRooms.this, "Error, please make sure there is internet connection and retry", Toast.LENGTH_LONG).show();
        } else if (response.equals("bad_request")) {
            Toast.makeText(ManageRooms.this, "Error, bad user request, please authenticate again", Toast.LENGTH_LONG).show();
        } else {
            rooms_list = new ArrayList<Room>();

            try {
                JSONArray json = new JSONArray(response);
                for (int i = 0; i < json.length(); i++) {
                    JSONObject json_item = new JSONObject(json.get(i).toString());
                    Room new_item = new Room();
                    new_item.id = json_item.getString("id");
                    new_item.number = json_item.getString("number");
                    new_item.floor = json_item.getString("floor");
                    new_item.chairs = json_item.getString("chairs");
                    new_item.equipment = json_item.getString("equipment");
                    rooms_list.add(new_item);
                }

                renderList();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    public static void renderList(){

        if(rooms_list.size() > 0){
            adapter = new ListRoomsAdapter(context, rooms_list);
            listview.setAdapter(adapter);
        }

    }

}