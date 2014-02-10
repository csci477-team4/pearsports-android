package com.example.app;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.EditText;
import android.widget.Toast;

import com.example.app.Database.Message;
import com.example.app.Database.MessageDataSource;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.util.Calendar;

public class TextActivity extends Activity {

    /**
     * Keep track of the send text task to ensure we can cancel it if requested.
     */
    private SendTextMessageTask mSendTask = null;

    //Message to be sent.
    private String mSendMessage;
    private long mTimestamp;
    private String mToUser;
    private static TextActivity instance;

    //UI References.
    private EditText mTextContent;

    //Database references.
    private MessageDataSource datasource;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text);
        mTextContent = (EditText) findViewById(R.id.edit_text_content);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

        findViewById(R.id.send_text_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleSendText();
            }
        });

        instance = this;
        datasource = new MessageDataSource(this);
        datasource.open();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.text, menu);
        return true;
    }

    public static TextActivity getInstance() {
        return instance;
    }

    public long getTimestamp() {
        return this.mTimestamp;
    }

    /**
     * Stores text message in local SQLite DB and then sends call to server to update the messages
     * stored on DB there.
     */
    public void handleSendText() {
        if (mSendTask != null) {
            return;
        }

        // Reset errors.
        mTextContent.setError(null);

        //Message validity checks.
        if (mTextContent.getText().toString().isEmpty()) {
            Toast.makeText(TextActivity.this, R.string.empty_message, Toast.LENGTH_SHORT).show();
            return;
        }
        if (mTextContent.getText().length() > 280) {
            Toast.makeText(TextActivity.this, R.string.too_long_message, Toast.LENGTH_SHORT).show();
            return;
        }

        //Store value of message.
        mSendMessage = mTextContent.getText().toString();

        //Store selected user.  TODO: Don't hardcode this anymore.  Get it from front-end.
        mToUser = "Jean-Ralphio Saperstein";

        //Store timestamp.
        mTimestamp = Calendar.getInstance().getTimeInMillis();

        //Put this message in local DB. TODO: Build database.
        putMessageInDB(mToUser, mSendMessage, mTimestamp);

        //Clear the text field and start sync task with backend.
        mTextContent.setText(null);
        mSendTask = new SendTextMessageTask();
        mSendTask.execute((Void) null);
    }

    public void putMessageInDB(String to, String text, long timestamp){
        Message message = null;
        message = datasource.createMessage(to, text, timestamp);
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

    @Override
    protected void onResume() {
        datasource.open();
        super.onResume();
    }

    @Override
    protected void onPause() {
        datasource.close();
        super.onPause();
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_text, container, false);
            return rootView;
        }
    }

    public class SendTextMessageTask extends AsyncTask<Void, Void, Boolean> {

        private HttpResponse finalResponse;

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpGet sendTextRequest = createSendTextRequest(LoginActivity.getInstance().getToken());
                HttpResponse response = httpclient.execute(sendTextRequest);
                finalResponse = response;

                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    return true;
                } else {
                    return false;
                }
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return false;
        }

        private HttpGet createSendTextRequest(String token) {
            HttpGet httpGet = new HttpGet(buildSendTextURL(token));
            return httpGet;
        }

        private String buildSendTextURL(String token) {
            StringBuilder urlString = new StringBuilder();
            urlString.append(getResources().getString(R.string.api_url));
            urlString.append("/");
            urlString.append(getResources().getString(R.string.sendTextPrefix));
            urlString.append("/");
            urlString.append(token);
            urlString.append("/");
            urlString.append(mToUser);
            urlString.append("/");
            urlString.append(mSendMessage);
            urlString.append("/");
            urlString.append(mTimestamp);
            return urlString.toString();
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mSendTask = null;

            if (success) {
                //Toast.makeText(LoginActivity.this, "Password Reset Email Sent", Toast.LENGTH_LONG).show();
            } else if (finalResponse != null && finalResponse.getStatusLine().getStatusCode() == HttpStatus.SC_NOT_FOUND) {
                //Toast.makeText(LoginActivity.this, "Account Not Found", Toast.LENGTH_LONG).show();
            } else {
                //Toast.makeText(LoginActivity.this, "Unable to Reset Password", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onCancelled() {
            mSendTask = null;
            //showProgress(false);
        }
    }

}