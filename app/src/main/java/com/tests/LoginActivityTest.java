package com.tests;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;
import android.widget.TextView;

import com.example.app.LoginActivity;
/**
 * Created by Marc on 2/22/14.
 */
public class LoginActivityTest extends ActivityInstrumentationTestCase2<LoginActivity> {

    private LoginActivity activity;
    private TextView textView1;

    public LoginActivityTest() {
        super(LoginActivity.class);
    }

    protected void setUp() throws Exception {
        super.setUp();
        activity = (LoginActivity) getActivity();
        textView1 = (TextView) activity.findViewById(R.id.main_text);
    }

    @SmallTest
    public void testToPass() {
        assertEquals("testing nothing: 1=1", 1, 1);
    }

    @MediumTest
    public void testWelcomeText() {
        assertNotNull(textView1);
        String str1 = textView1.getText().toString();
        assertNotNull(str1);
        assertEquals("check text1", str1, "Hello world!");
    }

}
