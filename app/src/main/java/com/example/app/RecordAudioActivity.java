package com.example.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class RecordAudioActivity extends Activity {

    private String fileName;
    private String trainee_id;
    private EditText fName;
    private Button playButton;
    private Button recordButton;
    private Button stopButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_audio);
        Intent intent = getIntent();
        trainee_id = intent.getStringExtra("trainee_id");

        fName =(EditText) findViewById(R.id.fileName);
        playButton = (Button) findViewById(R.id.Play);
        stopButton = (Button) findViewById(R.id.Stop);
        recordButton = (Button) findViewById(R.id.Record);


        findViewById(R.id.Play).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playRecording();
            }
        });
        findViewById(R.id.Record).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {startRecording();
            }
        });
        findViewById(R.id.Stop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopRecording();
            }
        });
        findViewById(R.id.Send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendRecording();
            }
        });
    }


    public void playRecording(){
        stopButton.setEnabled(false);
        recordButton.setEnabled(false);
        //TODO: Add code to play recorded and saved audio.
        //Enable other buttons after play is done.
        stopButton.setEnabled(true);
        recordButton.setEnabled(true);

    }

    public void stopRecording(){
        //TODO: Add code to stop and save recorded audio.
        stopButton.setEnabled(false);
        recordButton.setEnabled(true);
    }

    public void startRecording(){
        //TODO: Add code to record audio.
        recordButton.setEnabled(false);
        stopButton.setEnabled(true);
    }

    public void sendRecording(){
        fileName = fName.getText().toString().trim();
        fName.setText("");
        //TODO: Send the recorded audio.
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.record_audio, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
