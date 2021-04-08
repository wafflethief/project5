package com.example.project_5;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener{
    private final int ACT_CHECK_TTS_DATA = 1000;
    Button button_kanye;
    private TextToSpeech tts = null;
    EditText ed1;
    String quote = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR){
                    tts.setPitch(0.1f);
                    tts.setSpeechRate(0.1f);
                    tts.setLanguage(Locale.US);
                    tts.speak("hello", TextToSpeech.QUEUE_FLUSH, null);
                }
            }
        });
        // assign buttons
        button_kanye = (Button) findViewById(R.id.button_kanye);
        // onClick listeners for each button
        button_kanye.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                // Instantiate the RequestQueue.
                RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                // this url is a json OBJECT
                String url = "https://api.kanye.rest";
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                        (Request.Method.GET, url, null, new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            quote = response.getString("quote");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(MainActivity.this, quote, Toast.LENGTH_LONG).show();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, "error", Toast.LENGTH_LONG).show();
                    }
                });
                // Add the request to the RequestQueue.
                queue.add(jsonObjectRequest);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    tts.speak(quote, TextToSpeech.QUEUE_FLUSH, null, null);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ACT_CHECK_TTS_DATA) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS){
                // Data exists, so we instantiate the TTS engine
                tts = new TextToSpeech(this, this);
            } else{
                // Data is missing, so we start the TTS
                // installation process
                Intent installIntent = new Intent();
                installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installIntent);
            }
        }
    }
    /*
    public void speak(String quote, int qmode){
        tts.setPitch(0.1f);
        tts.setSpeechRate(0.1f);
        tts.speak(quote, TextToSpeech.QUEUE_FLUSH, null);
    }*/

    @Override
    public void onInit(int status) {
        if(status == TextToSpeech.SUCCESS){
            if(tts !=null) {
                int result = tts.setLanguage(Locale.ENGLISH);
                if (result == TextToSpeech.LANG_MISSING_DATA
                        || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Toast.makeText(MainActivity.this,"TTS language Not supported", Toast.LENGTH_SHORT).show();
                } else {
                    button_kanye.setEnabled(true);
                }
            }
        }
        else{
            Toast.makeText(MainActivity.this, "TTS failed", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy(){
        if (tts != null){
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    public void onPause(){
        if(tts != null){
            tts.stop();
            tts.shutdown();
        }
        super.onPause();
    }
}