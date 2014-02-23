package com.example.app.tests;

import android.test.ActivityInstrumentationTestCase2;
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
    private ScrollView loginForm;
    private LinearLayout loginProgress;

    public LoginActivityTest() {
        super(LoginActivity.class);
    }

    protected void setUp() throws Exception {
        super.setUp();

        setActivityInitialTouchMode(false);

        // Start the main activity of the application under test
        mActivity = (LoginActivity) getActivity();

        // Get a handle to the Activity object's main UI widget, a Spinner
        loginForm = (ScrollView) mActivity.findViewById(R.id.login_form);
        loginProgress = (LinearLayout) mActivity.findViewById(R.id.login_status);
    }

    @MediumTest
    public void testLoginForm() {
        assertNotNull(loginForm);
        ImageView logo = (ImageView) loginForm.findViewById(R.id.pear_logo);
        EditText emailText = (EditText) loginForm.findViewById(R.id.email);
        EditText passwordText = (EditText) loginForm.findViewById(R.id.password);
        Button signInButton = (Button) loginForm.findViewById(R.id.sign_in_button);

        assertNotNull(logo);
        assertNotNull(emailText);
        assertNotNull(passwordText);
        assertTrue(signInButton.isClickable());
    }

    @MediumTest
    public void testLoginProgress() {
        assertNotNull(loginProgress);
        TextView progressText = (TextView) loginProgress.findViewById(R.id.login_status_message);

       assertNotNull(progressText.getText().toString());
    }

    @MediumTest
    public void testTokens() {
        assertNotNull(loginProgress);
        TextView progressText = (TextView) loginProgress.findViewById(R.id.login_status_message);

        assertNotNull(progressText.getText().toString());
    }
}
