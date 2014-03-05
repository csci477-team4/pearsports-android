package com.example.app.tests;

import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;
import android.widget.*;

import com.example.app.LoginActivity;
import com.example.app.R;

/**
 * Created by Marc on 2/22/14.
 */
public class LoginActivityTest extends ActivityInstrumentationTestCase2<LoginActivity> {

    private LoginActivity mActivity;
    private ScrollView mLoginForm;
    private LinearLayout mLoginProgress;


    public LoginActivityTest() {
        super(LoginActivity.class);
    }

    protected void setUp() throws Exception {
        super.setUp();

        setActivityInitialTouchMode(false);

        // Start the main activity of the application under test
        mActivity = (LoginActivity) getActivity();
        // Get a handle to the Activity object's main UI widget, a Spinner
        mLoginForm = (ScrollView) mActivity.findViewById(R.id.login_form);
        mLoginProgress = (LinearLayout) mActivity.findViewById(R.id.login_status);

    }

    @MediumTest
    public void testLoginForm() {
        assertNotNull(mLoginForm);
        ImageView logo = (ImageView) mLoginForm.findViewById(R.id.pear_logo);
        EditText emailText = (EditText) mLoginForm.findViewById(R.id.email);
        EditText passwordText = (EditText) mLoginForm.findViewById(R.id.password);
        Button signInButton = (Button) mLoginForm.findViewById(R.id.sign_in_button);

        assertNotNull(logo);
        assertNotNull(emailText);
        assertNotNull(passwordText);
        assertTrue(signInButton.isClickable());
    }

    @MediumTest
    public void testLoginProgress() {
        assertNotNull(mLoginProgress);
        TextView progressText = (TextView) mLoginProgress.findViewById(R.id.login_status_message);

        assertNotNull(progressText.getText().toString());
    }

    @UiThreadTest
    public void testValidLogin() {
        EditText emailText = (EditText) mLoginForm.findViewById(R.id.email);
        EditText passwordText = (EditText) mLoginForm.findViewById(R.id.password);
        emailText.setText("marc@somefakeemail.com");
        passwordText.setText("password1");
        mActivity.attemptLogin();

        // Thread is put to sleep to make sure the UI thread finishes before checking the token.
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertNotNull(mActivity.getToken());

    }

    @UiThreadTest
    public void testInvalidLogin() {

        EditText emailText = (EditText) mLoginForm.findViewById(R.id.email);
        EditText passwordText = (EditText) mLoginForm.findViewById(R.id.password);
        emailText.setText("invalidEmail");
        passwordText.setText("password1");
        mActivity.attemptLogin();

        // Thread is put to sleep to make sure the UI thread finishes before checking the token.
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        assertNull(mActivity.getToken());

    }

    @UiThreadTest
    public void testReset() {


    }

}


