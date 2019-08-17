package com.example.sushi.sirilike;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    TextView txtTranslate;
    ImageView btnMic;
    Context context;
    final int REQUEST_CODE = 1;
    final int RECOGNITION_RESULT = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setPointer();
        askForPermission();
    }

    private void askForPermission() {
        //create a list of permission that we want
        List<String> listPerm = new ArrayList<>();
        //get permission status for audio record
        int audioPerm = ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO);
        //check if the record audio permission is granted
        if (audioPerm != PackageManager.PERMISSION_GRANTED) {
            listPerm.add(Manifest.permission.RECORD_AUDIO);
        }
        //check our list if it not empty
        if (!listPerm.isEmpty()) {
            //ask permission by requests
            ActivityCompat.requestPermissions(this, listPerm.toArray(new String[listPerm.size()]), REQUEST_CODE);
        }


    }

    private void setPointer() {
        this.context = this;
        txtTranslate = findViewById(R.id.txtTranslate);
        btnMic = findViewById(R.id.btnMic);
        btnMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runSpeechRecognition();
            }
        });
    }

    private void runSpeechRecognition() {
        //handle the speech recognition intent
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        //tell the intent that we want to speak freely
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        //tell the intent that we want to use the default languge.
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        //show the user a text to explain what we want.
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.speak));
        //we can have exception that the language is not supported, and we want to handle it.
        try {
            startActivityForResult(intent,RECOGNITION_RESULT);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, getString(R.string.lngNotSupported), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //we can do what ever we like....
            } else {
                Toast.makeText(context, getResources().getString(R.string.needPerm), Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode)
        {
            //in case the request code is 2 - recognition intent
            case RECOGNITION_RESULT:
                //check if the result code is OK (user didn't cancel the request and we have something back in our data
                if (resultCode == RESULT_OK && data != null)
                {
                    //get array list of all our result (can be 1,can be 5)
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    //set the text into our text language
                    txtTranslate.setText(result.get(0));
                    //get all the result to view....
                    Log.e("result", "onActivityResult: "+result.toString() );
                }
                break;

            default:
                Toast.makeText(context, "programmer not so bright", Toast.LENGTH_LONG).show();
        }
    }
}