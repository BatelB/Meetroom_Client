package com.meetingroomscheduler.Adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.meetingroomscheduler.Activity.ManageRooms;
import com.meetingroomscheduler.Activity.ManageUsers;
import com.meetingroomscheduler.Class.Room;
import com.meetingroomscheduler.Class.User;
import com.meetingroomscheduler.Global;
import com.meetingroomscheduler.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class ListRoomsAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final ArrayList<Room> arg_0;

    private boolean sent = false;

    public ListRoomsAdapter(Context context, ArrayList<Room> arg_0) {

        super(context, R.layout.list_rooms_item, new String[arg_0.size()]);

        this.context = (Activity) context;
        this.arg_0 = arg_0;

        sent = false;
    }

    @Override
    public View getView(final int pos, View view, ViewGroup parent) {

        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.list_rooms_item, null, true);

        TextView id = (TextView) rowView.findViewById(R.id.room_item_id);
        TextView number = (TextView) rowView.findViewById(R.id.room_item_number);
        TextView floor = (TextView) rowView.findViewById(R.id.room_item_floor);
        TextView chairs = (TextView) rowView.findViewById(R.id.room_item_chairs);
        TextView equipment = (TextView) rowView.findViewById(R.id.room_item_equipment);

        ImageView delete = (ImageView) rowView.findViewById(R.id.room_item_delete);

        id.setText("#" + arg_0.get(pos).id);
        number   .setText("Number    : " + arg_0.get(pos).number);
        floor    .setText("Floor     : " + arg_0.get(pos).floor);
        chairs   .setText("Chairs    : " + arg_0.get(pos).chairs);
        equipment.setText("Equipment : " + arg_0.get(pos).equipment);

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteRoom(pos);
            }
        });


        return rowView;
    }

    private void deleteRoom(final int index){

        ManageRooms.progressbar.setVisibility(View.VISIBLE);
        sent = true;

        Map<String,String> map = new HashMap<>();
        map.put("email", Global.email);
        map.put("password",Global.password);
        map.put("id", arg_0.get(index).id);
        map.put("action", "delete_room");
        String params = new JSONObject(map).toString();
        String response = Global.query(params);

        ManageRooms.progressbar.setVisibility(View.GONE);
        sent = false;

        if(response.equals("fail")){
            Toast.makeText(context, "Error, please make sure there is internet connection and retry", Toast.LENGTH_LONG).show();
        }if(response.equals("bad_request")){
            Toast.makeText(context, "Database error", Toast.LENGTH_LONG).show();
        }else if(response.equals("success")){
            if(response.equals("success")){
                Toast.makeText(context, "Room #" + arg_0.get(index).id + " deleted!", Toast.LENGTH_SHORT).show();
                ManageRooms.rooms_list.remove(index);
                ManageRooms.renderList();
            }else{
                Toast.makeText(context, "Error, could not delete user", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(context, "Error, could not create user", Toast.LENGTH_SHORT).show();
        }

    }

}