package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import org.json.JSONException;


import org.json.JSONObject;

public class MainActivity2 extends AppCompatActivity {

    private Button startButton;

    private Button loadCellButton;
    private EditText motor_speed;
    private EditText run_time;
    private static final String MEGA_IP_ADDRESS = "192.168.0.100"; // Ganti dengan IP ESP32 Anda
    private static final int MEGA_PORT = 80; // Ganti dengan port yang digunakan pada ESP32 Anda

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        motor_speed = findViewById(R.id.inputMotorSpeed);
        run_time = findViewById(R.id.inputRunTime);
        startButton = findViewById(R.id.startButton);
        loadCellButton = findViewById(R.id.buttonLoadCell);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the target weight value from the EditText
                String motorSpeedValue = motor_speed.getText().toString();
                String runTimeValue = run_time.getText().toString();

                // Create a JSON object with the target weight value
                JSONObject jsonCommand = new JSONObject();
                try {
                    jsonCommand.put("motor_speed", motorSpeedValue);
                    jsonCommand.put("run_time", runTimeValue);
                } catch (JSONException e) {
                    e.printStackTrace();
                    return; // Return if there's an error creating the JSON object
                }

                // Send the JSON command to the Arduino
                new SendCommandTask().execute(jsonCommand.toString());
            }
        });

        loadCellButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Close the current activity and return to MainActivity
            }
        });
    }

    private class SendCommandTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... commands) {
            try {
                Socket socket = new Socket(MEGA_IP_ADDRESS, MEGA_PORT);

                PrintWriter out = new PrintWriter(socket.getOutputStream());
                out.println(commands[0]);
                out.flush();

                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String messageFromESP32 = in.readLine();

                socket.close();

                return messageFromESP32;
            } catch (IOException e) {
                e.printStackTrace();
                return "Error: " + e.getMessage();
            }
        }
    }
}
