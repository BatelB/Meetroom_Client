package com.meetingroomscheduler.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.meetingroomscheduler.Global;
import com.meetingroomscheduler.R;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */

public class EditRoom  extends AppCompatActivity {

    EditText edit_number, edit_floor, edit_chairs, edit_equipment;

    ProgressBar progressbar;

    TextView back, submit;

    boolean sent = false;

    String number = "", floor = "", chairs = "", equipment = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_room);

        sent = false;
        if(Global.requestQueue == null) {
            Global.requestQueue = Volley.newRequestQueue(this);
        }

        edit_number = (EditText) findViewById(R.id.add_room_edit_number);
        edit_floor = (EditText) findViewById(R.id.add_room_edit_floor);
        edit_chairs = (EditText) findViewById(R.id.add_room_edit_chairs);
        edit_equipment = (EditText) findViewById(R.id.add_room_edit_equipment);

        progressbar = (ProgressBar) findViewById(R.id.progressbar);
        back = (TextView) findViewById(R.id.back);
        submit = (TextView) findViewById(R.id.submit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(sent) return;

                number = edit_number.getText().toString();
                if(number.length() == 0){
                    edit_number.setError("All fields are mandatory");
                    return;
                }

                floor = edit_floor.getText().toString();
                if(floor.length() == 0){
                    edit_floor.setError("All fields are mandatory");
                    return;
                }

                chairs = edit_chairs.getText().toString();
                if(chairs.length() == 0){
                    edit_chairs.setError("All fields are mandatory");
                    return;
                }

                equipment = edit_equipment.getText().toString();
                if(edit_equipment.length() == 0){
                    edit_equipment.setError("All fields are mandatory");
                    return;
                }

                editRoom();
            }
        });


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    void editRoom(){

        progressbar.setVisibility(View.VISIBLE);
        sent = true;

        Map<String,String> map = new HashMap<>();
        map.put("email", Global.email);
        map.put("password",Global.password);
        map.put("room_id", number);
        map.put("number", number);
        map.put("floor", floor);
        map.put("chairs", chairs);
        map.put("equipment", equipment);
        map.put("action", "edit_room");
        String params = new JSONObject(map).toString();
        String response = Global.query(params);

        progressbar.setVisibility(View.GONE);
        sent = false;

        if(response.equals("fail")){
            Toast.makeText(EditRoom.this, "Error, please make sure there is internet connection and retry", Toast.LENGTH_LONG).show();
        }if(response.equals("bad_request")){
            Toast.makeText(EditRoom.this, "Database error", Toast.LENGTH_LONG).show();
        }else if(response.equals("success")){
            Toast.makeText(EditRoom.this, "Room updated !", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(EditRoom.this, "Error, could not create user", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onBackPressed(){
        finish();
    }
}
