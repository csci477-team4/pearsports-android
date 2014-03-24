package com.example.app;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;


public class RecordAudioActivity extends Activity {

    private String fileName;
    private String defaultOutFile;
    private String trainee_id;
    private EditText fName;
    private Button playButton;
    private Button recordButton;
    private Button stopButton;

    private MediaRecorder mRecorder;
    private MediaPlayer   mPlayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_audio);
        Intent intent = getIntent();
        trainee_id = intent.getStringExtra("trainee_id");

        defaultOutFile = Environment.getExternalStorageDirectory().getAbsolutePath();
        defaultOutFile += "/recording.3gp";

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

        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(defaultOutFile);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
        }

        stopButton.setEnabled(true);
        recordButton.setEnabled(true);
    }

    public void stopRecording(){
        if(mRecorder != null)
        {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        }

        stopButton.setEnabled(false);
        recordButton.setEnabled(true);
    }

    public void startRecording(){
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
        mRecorder.setOutputFile(defaultOutFile);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Toast.makeText(RecordAudioActivity.this, "Cannot start recorder (no device?)", Toast.LENGTH_LONG).show();
        }

        recordButton.setEnabled(false);
        stopButton.setEnabled(true);
        mRecorder.start();
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
