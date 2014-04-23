package com.example.app;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
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
    private MediaPlayer mPlayer;
    private SeekBar seek;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_audio);
        Intent intent = getIntent();
        trainee_id = intent.getStringExtra("trainee_id");

        defaultOutFile = Environment.getExternalStorageDirectory().getAbsolutePath();
        defaultOutFile += "/recording.3gp";

        fName = (EditText) findViewById(R.id.fileName);
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
            public void onClick(View view) {
                startRecording();
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

        final SeekBar sk = (SeekBar)findViewById(R.id.seekBar);
        seek = sk;
        mHandler = new Handler();

        sk.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(mPlayer != null && fromUser == true)
                {
                    int newDuration = sk.getProgress();
                    mPlayer.seekTo(newDuration);
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mRecorder != null) {
            stopRecording();
        }

        if (mPlayer != null) {
            mPlayer.stop();
        }

        File outFile = new File(defaultOutFile);

        if (!outFile.exists()) {
            return;
        } else {
            outFile.delete();
        }
    }


    public void playRecording() {
        File outFile = new File(defaultOutFile);

        if (!outFile.exists()) {
            Toast.makeText(RecordAudioActivity.this, "No Recording to Play", Toast.LENGTH_SHORT).show();
            return;
        }

        stopButton.setEnabled(false);
        recordButton.setEnabled(false);

        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.reset();
        }

        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(defaultOutFile);
            mPlayer.prepare();
            seek.setMax(mPlayer.getDuration());
            mPlayer.start();
            updateProgressBar();
        } catch (IOException e) {
        }

        stopButton.setEnabled(true);
        recordButton.setEnabled(true);
    }

    private void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 500);
    }

    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            if(mPlayer != null) {
                int newProgress = mPlayer.getCurrentPosition();
                seek.setProgress(newProgress);
            }
            mHandler.postDelayed(this, 500);
        }
    };

    public void stopRecording() {
        if (mRecorder != null) {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        }

        stopButton.setEnabled(false);
        recordButton.setEnabled(true);
    }

    public void startRecording() {
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

    public void sendRecording() {
        fileName = fName.getText().toString().trim();
        fName.setText("");
        SendUploadRequestTask mUploadTask = new SendUploadRequestTask();
        mUploadTask.execute((Void) null);
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
            Intent i = new Intent(RecordAudioActivity.this, SettingsActivity.class);
            startActivity(i);
        }
        if (id == R.id.action_logout) {
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor edit = pref.edit();
            edit.remove("token");
            edit.remove("trainee_id");
            edit.apply();

            Intent i = new Intent(RecordAudioActivity.this, LoginActivity.class);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }

    public class SendUploadRequestTask extends AsyncTask<Void, Void, Boolean> {
        private boolean finalResponse;
        private boolean fileNotFound = false;

        @Override
        protected Boolean doInBackground(Void... params) {
            if (mRecorder != null) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        stopRecording();
                    }
                });
            }

            File outFile = new File(defaultOutFile);

            if (!outFile.exists()) {
                fileNotFound = true;
                return false;
            }

            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            String traineeId = pref.getString("trainee_id", "");
            String key = pref.getString("token", "");

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(RecordAudioActivity.this, "Uploading Starting...", Toast.LENGTH_LONG).show();
                }
            });

            JSONObject response = APIHandler.sendAudioUploadRequest("message/audio", key, "", outFile, fileName, traineeId);

            if (response == null) {
                return false;
            } else {
                String message = null;
                try {
                    message = response.getString("message");
                } catch (JSONException e) {
                    return false;
                }

                if (message.equals("upload successful")) {
                    return true;
                }
            }

            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                Toast.makeText(RecordAudioActivity.this, "Successfully Uploaded Audio File", Toast.LENGTH_LONG).show();
            } else if (fileNotFound == true) {
                Toast.makeText(RecordAudioActivity.this, "No Audio Recording Found", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(RecordAudioActivity.this, "Error Uploading Audio File", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onCancelled() {
        }
    }
}
