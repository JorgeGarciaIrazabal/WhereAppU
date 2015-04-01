package com.application.jorge.whereappu.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.application.jorge.whereappu.DataBase.User;
import com.application.jorge.whereappu.R;
import com.application.jorge.whereappu.Classes.QueryTable;
import com.application.jorge.whereappu.Connections.ServerComm;


/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class LoginActivity extends Activity {
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */

    // Values for email and password at the time of the login attempt.
    private String mEmail;
    private String mName;
    private EditText email;
    private EditText name;
    private EditText phone;
    private View mLoginStatusView;
    private TextView mLoginStatusMessageView;
    private Button signButton;

    public static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        App.isNetworkAvailable(true);
        // app.whereappu.Set up the login form.
        email = (EditText) findViewById(R.id.login_email);
        name = (EditText) findViewById(R.id.login_name);
        phone = (EditText) findViewById(R.id.login_phoneNumber);
        mLoginStatusView = findViewById(R.id.login_status);
        mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);
        signButton = (Button) findViewById(R.id.sign_in_button);
        signButton.setEnabled(true);
        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    attemptLogin();
                } catch (Exception e) {
                    e.printStackTrace();
                    App.softAlert(e.getMessage());
                }
            }
        });
    }

    @Override
    protected void onResume() {
        App.Activity = this;
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() throws Exception {
        // Reset errors.
        email.setError(null);
        name.setError(null);

        // Store values at the time of the login attempt.
        mEmail = email.getText().toString();
        mName = name.getText().toString();

        boolean cancel = false;
        View focusView = null;

        /*// Check for a valid password.
        if (TextUtils.isEmpty(mName)) {
            name.setError(getString(R.string.error_field_required));
            focusView = name;
            cancel = true;
        }

        // Check for a valid email address.

        if (!TextUtils.isEmpty(mEmail) && !mEmail.contains("@")) {
            email.setError(getString(R.string.error_invalid_email));
            focusView = email;
            cancel = true;
        }*/

        if (cancel) {
            focusView.requestFocus();
        } else {
            mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
            App.showProgress(true, mLoginStatusView);
            loginTask();
        }
    }

    public void loginTask() throws Exception {//todo: change qt for user
        String serverReturn = "";
        String phoneNumber = phone.getText().toString();
        final User user = new User("nick", email.getText().toString(), "0034" + phone.getText().toString(), MainActivity.GCMF.getRegId());
        if (App.getUserId() == 0) {
            /*MainActivity.serverComm.postCommInBackground("login", loginData.getJSONObject().toString(), new ServerComm.CallBack() {
                @Override
                public void execute(final String userId) {
                    LoginActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                MainActivity.myUserId = Long.parseLong(userId);
                                App.storeUserId(MainActivity.myUserId);
                                user.serverId=MainActivity.myUserId;
                                MainActivity.db.login(user);
                                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                                LoginActivity.this.startActivity(i);
                                finish();
                            } catch (Exception qtExcep) {
                                qtExcep.printStackTrace();
                                App.showProgress(false, mLoginStatusView);
                                App.storeUserId(0);
                            }
                        }
                    });
                }
            });*/
        }
    }
}