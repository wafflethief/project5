package com.example.project_5;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    public Button checkSentiment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkSentiment = (Button) findViewById(R.id.button);
        checkSentiment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                executeHTTP();

            }
        });
    }

    public static String executeHTTP(){
        try {
            URL url = new URL("rest.bandsintown.com");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");

            DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
            wr.writeBytes("poop");
            wr.close();

            InputStream istream = conn.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(istream));
            StringBuilder resp = new StringBuilder();
            String line;
            while((line = reader.readLine()) != null){
                resp.append(line);
                resp.append('\r');
            }
            reader.close();
            System.out.println(resp);
            return resp.toString();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}