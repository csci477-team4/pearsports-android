package com.example.app;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class LoginActivity extends Activity {
    /**
     * The default email to populate the email field with.
     */
    public static final String EXTRA_EMAIL = "com.example.android.authenticatordemo.extra.EMAIL";

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // Values for email and password at the time of the login attempt.
    public static String EMAIL;
    public static String PASSWORD;
    private String mToken;

    // UI references.
    private EditText mEmailView;
    private EditText mPasswordView;
    private View mLoginFormView;
    private View mLoginStatusView;
    private TextView mLoginStatusMessageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        // Remember that you should never show the action bar if the
        // status bar is hidden, so hide that too if necessary.
        ActionBar actionBar = getActionBar();
        actionBar.hide();

        String forgot = "Forgot Password?";
        SpannableString content = new SpannableString(forgot);
        content.setSpan(new UnderlineSpan(), 0, forgot.length(), 0);
        TextView mForgotPassword = (TextView) findViewById(R.id.text_forgotPassword);
        mForgotPassword.setText(content);

        String create = "Create Account";
        SpannableString content2 = new SpannableString(create);
        content2.setSpan(new UnderlineSpan(), 0, create.length(), 0);
        TextView mCreateAccount = (TextView) findViewById(R.id.text_createAccount);
        mCreateAccount.setText(content2);

        // Set up the login form.
        EMAIL = getIntent().getStringExtra(EXTRA_EMAIL);
        mEmailView = (EditText) findViewById(R.id.email);
        mEmailView.setText(EMAIL);

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mLoginStatusView = findViewById(R.id.login_status);
        mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        TextView view = (TextView) findViewById(R.id.text_forgotPassword);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptReset();
            }
        });

        findViewById(R.id.text_createAccount).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptCreateNewAccount();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        EMAIL = mEmailView.getText().toString();
        PASSWORD = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password.
        if (TextUtils.isEmpty(PASSWORD)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        } else if (PASSWORD.length() < 4) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(EMAIL)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!EMAIL.contains("@")) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {

            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.


            mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
            showProgress(true);
            mAuthTask = new UserLoginTask();
            mAuthTask.execute((Void) null);
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginStatusView.setVisibility(View.VISIBLE);
            mLoginStatusView.animate()
                    .setDuration(shortAnimTime)
                    .alpha(show ? 1 : 0)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
                        }
                    });

            mLoginFormView.setVisibility(View.VISIBLE);
            mLoginFormView.animate()
                    .setDuration(shortAnimTime)
                    .alpha(show ? 0 : 1)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                        }
                    });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    public void attemptReset()
    {
        mEmailView.setError(null);
        mPasswordView.setError(null);

        EMAIL = mEmailView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(EMAIL)) {
            mEmailView.setError(getString(R.string.error_field_required));
            cancel = true;
        } else if (!EMAIL.contains("@")) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            cancel = true;
        }


        if (!cancel) {
            ResetPasswordTask resetTask = new ResetPasswordTask();
            resetTask.execute();
        }
    }

    public void attemptCreateNewAccount()
    {
        mEmailView.setError(null);
        mPasswordView.setError(null);

        EMAIL = mEmailView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(EMAIL)) {
            mEmailView.setError(getString(R.string.error_field_required));
            cancel = true;
        } else if (!EMAIL.contains("@")) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            cancel = true;
        }


        if (!cancel) {
            CreateAccountTask accountTask = new CreateAccountTask();
            accountTask.execute();
        }
    }

    public String getToken() {
        return mToken;
    }
    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpParams httpParameters = httpclient.getParams();
                HttpConnectionParams.setConnectionTimeout(httpParameters, 5000);
                HttpConnectionParams.setSoTimeout(httpParameters, 10000);

                HttpPost signInRequest = createSignInRequest();
                HttpResponse response = httpclient.execute(signInRequest);

                if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
                    String loginToken = getLoginToken(response);
                    mToken = loginToken;

                    if(loginToken != null){
                        mToken = loginToken;
                        return true;
                    }
                    else{
                        return false;
                    }
                }
                else{
                    return false;
                }
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return false;
        }

        private HttpPost createSignInRequest()
        {
            HttpPost httpPost = new HttpPost(buildSignInURL());
            String base64EncodedCredentials = "Basic " + Base64.encodeToString((EMAIL + ":" + PASSWORD).getBytes(),Base64.NO_WRAP);
            httpPost.setHeader("Authorization",base64EncodedCredentials);
            return httpPost;
        }

        private String buildSignInURL()
        {
            StringBuilder urlString = new StringBuilder();
            urlString.append(getResources().getString(R.string.api_url));
            urlString.append("/");
            urlString.append(getResources().getString(R.string.signinPrefix));
            return urlString.toString();
        }

        private String getLoginToken(HttpResponse response)
        {
            try {

                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
                StringBuilder builder = new StringBuilder();

                for (String line = null; (line = reader.readLine()) != null;) {
                    builder.append(line).append("\n");
                }

                JSONObject temp = new JSONObject(builder.toString());
                JSONObject jsonResponse = temp.getJSONObject("trainer_info");
                return jsonResponse.getString("token");
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            if (success) {
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor edit= pref.edit();
                edit.putString("token", mToken);
                edit.apply();

                Intent i = new Intent(LoginActivity.this, TraineeListActivity.class);
                i.putExtra("token", mToken);
                startActivity(i);
                finish();
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    public class ResetPasswordTask extends AsyncTask<Void, Void, Boolean> {
        private HttpResponse finalResponse;

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpParams httpParameters = httpclient.getParams();
                HttpConnectionParams.setConnectionTimeout(httpParameters, 5000);
                HttpConnectionParams.setSoTimeout(httpParameters, 10000);

                HttpGet resetRequest = createResetRequest(EMAIL);
                HttpResponse response = httpclient.execute(resetRequest);
                finalResponse = response;

                if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
                {
                    return true;
                }
                else{
                    return false;
                }
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return false;
        }

        private HttpGet createResetRequest(String email)
        {
            HttpGet httpGet = new HttpGet(buildResetURL(email));
            return httpGet;
        }

        private String buildResetURL(String email)
        {
            StringBuilder urlString = new StringBuilder();
            urlString.append(getResources().getString(R.string.api_url));
            urlString.append("/");
            urlString.append(getResources().getString(R.string.resetPasswordPrefix));
            urlString.append("/");
            urlString.append(email);
            return urlString.toString();
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;

            if (success) {
                Toast.makeText(LoginActivity.this, "Password Reset Email Sent", Toast.LENGTH_LONG).show();
            }
            else if(finalResponse != null && finalResponse.getStatusLine().getStatusCode() == HttpStatus.SC_NOT_FOUND){
                Toast.makeText(LoginActivity.this, "Account Not Found", Toast.LENGTH_LONG).show();
            }
            else{
                Toast.makeText(LoginActivity.this, "Unable to Reset Password", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    public class CreateAccountTask extends AsyncTask<Void, Void, Boolean> {
        private boolean finalResponse;

        @Override
        protected Boolean doInBackground(Void... params) {
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
            nameValuePairs.add(new BasicNameValuePair("email", EMAIL));
            String prefix = getResources().getString(R.string.newAccountPrefix);
            try {
                JSONObject myObj = APIHandler.sendAPIRequest(prefix, APIHandler.POST, nameValuePairs);

                try {
                    String status = myObj.get("status").toString();

                    if (status.equals("200")) {
                        return true;
                    }
                } catch (JSONException e) {
                    finalResponse = false;
                }
            } catch (IOException e) {
                Log.e("LoginActivity::IOException >> ", e.getMessage());
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;

            if (success) {
                Toast.makeText(LoginActivity.this, "Request for New Account Sent", Toast.LENGTH_LONG).show();
            }
            else{
                Toast.makeText(LoginActivity.this, "Unable to send a request for a new account", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

