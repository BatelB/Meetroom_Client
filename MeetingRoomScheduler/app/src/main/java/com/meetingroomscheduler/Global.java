package com.meetingroomscheduler;

import android.os.CountDownTimer;
import android.renderscript.ScriptGroup;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.meetingroomscheduler.Class.User;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Map;

/**
 *
 */

public class Global {

    public static String email = "";
    public static String password = "";

    public static String base_URL = "https://meetingproject.000webhostapp.com";

    public static User current_user = new User();

    public static RequestQueue requestQueue;

    public static int view_room_index = 0;

    public static String edit_schedule_id = "";

    //######################### SOCKET #########################
    public static String adress = "35.169.147.205";//"172.31.57.222";//"34.201.38.146"; //"192.168.100.4";
    public static int port = 55555;

    public static String result = "fail";
    public static String line = "";

    // String query gets string quary and sends it
    public static String query(final String params){
        Thread tempThread = new Thread(new Runnable(){

            public void run(){
                try {
                    Socket socket = new Socket(adress, port);
                    DataInputStream inputStream = new DataInputStream(socket.getInputStream());
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                    try {
                        outputStream.writeBytes(params + "\n");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                        while (true) {
                                try {
                                    line = bufferedReader.readLine();
                                    if (line == null) {
                                        clear(bufferedReader, inputStream, outputStream, socket);
                                        return;
                                    }else{
                                        result = line;
                                        clear(bufferedReader, inputStream, outputStream, socket);
                                        return;
                                    }
                                } catch (IOException e) {
                                    Log.d("MainActivity.class", "listening exception");
                                    e.printStackTrace();
                                    clear(bufferedReader, inputStream, outputStream, socket);
                                    return;
                                }
                        }

                } catch (UnknownHostException e) {
                    Log.d("MainActivity.class", "Don't know about host: hostname");
                } catch (IOException e) {
                    Log.d("MainActivity.class", "Couldn't get I/O for the connection to: hostname");
                }

            }
        });

        tempThread.start();
        try {
            tempThread.join(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return result;
    }

    public static void clear(BufferedReader bufferedReader, DataInputStream inputStream, DataOutputStream outputStream, Socket socket){

        Log.d("MainActivity.class", "clear()");

        try {
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //######################### SOCKET #########################


}
