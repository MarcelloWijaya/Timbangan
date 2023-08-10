package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    private ImageView canopyNotFilledImageView;
    private ImageView canopyFilledImageView;
    private Button onButton;
    private Button startButton;
    private TextView textView;
    private String statusCanopy = "tertutup";
    private static final String ESP32_IP_ADDRESS = "192.168.0.100"; // Ganti dengan IP ESP32 Anda
    private static final int ESP32_PORT = 80; // Ganti dengan port yang digunakan pada ESP32 Anda

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.connection);
        onButton = findViewById(R.id.onButton);
        startButton = findViewById(R.id.startButton);

        onButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    // When the button is pressed, send the "open" instruction to Arduino
                    new SendCommandTask().execute("OPEN");
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    // When the button is released, send the "stop" instruction to Arduino
                    new SendCommandTask().execute("STOP OPEN");
                }
                return false;
            }
        });
        startButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    // When the button is pressed, send the "open" instruction to Arduino
                    new SendCommandTask().execute("CLOSE");
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    // When the button is released, send the "stop" instruction to Arduino
                    new SendCommandTask().execute("STOP CLOSE");
                }
                return false;
            }
        });
    }

    private class SendCommandTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... commands) {
            try {
                Socket socket = new Socket(ESP32_IP_ADDRESS, ESP32_PORT);

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

        @Override
        protected void onPostExecute(String result) {

            // Mengupdate status canopy berdasarkan hasil dari perintah yang dikirim
            if (result.equalsIgnoreCase("Terbuka")) {
                statusCanopy = "terbuka";
            } else if (result.equalsIgnoreCase("Tertutup")) {
                statusCanopy = "tertutup";
            } else {
                statusCanopy = "unknown"; // Jika status tidak dikenali atau terjadi kesalahan
            }

            // Memperbarui tampilan tombol berdasarkan status canopy
            if ("terbuka".equals(statusCanopy)) {
                canopyNotFilledImageView.setVisibility(View.GONE);
                canopyFilledImageView.setVisibility(View.VISIBLE);
            } else if ("tertutup".equals(statusCanopy)) {
                canopyFilledImageView.setVisibility(View.GONE);
                canopyNotFilledImageView.setVisibility(View.VISIBLE);
            }

            // Mengatur teks di textView dengan hasil dari perintah yang dikirim
            textView.setText("Hasil koneksi: " + result);
        }
    }
}
