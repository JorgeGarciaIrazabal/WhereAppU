package com.application.jorge.whereappu.Activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.application.jorge.whereappu.Classes.alert;
import com.application.jorge.whereappu.DataBase.User;
import com.application.jorge.whereappu.R;
import com.application.jorge.whereappu.WebSocket.FunctionResult;
import com.application.jorge.whereappu.WebSocket.WSServer;
import org.json.JSONException;


public class LoggingActivity extends AppCompatActivity {
    @InjectView(R.id.NameField)
    EditText nameField;
    @InjectView(R.id.EmailField)
    EditText emailField;
    @InjectView(R.id.phoneField)
    EditText phoneField;
    @InjectView(R.id.LogInButton)
    Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logging);
        ButterKnife.inject(this);
        nameField.setText("Jorge");
        emailField.setText("jorge.girazabal@gmail.com");
        phoneField.setText("653961314");
    }

    @OnClick(R.id.LogInButton)
    public void onLogIn() {
        if (!WSServer.connection.isConnected())
            alert.popUp(this, "Unable to connect with server, check internetConnection");
        else {
            final String gcmId = MainActivity.GCMF.getRegId();
            try {
                final String phone = "+34" + phoneField.getText();
                final String name = nameField.getText().toString();
                final String email = emailField.getText().toString();
                WSServer.LoggingHub.logIn(phone, gcmId, name, email).done(new FunctionResult.Handler() {
                    @Override
                    public void onSuccess(Object userId) {
                        User mySelf = new User(name, email, phone, gcmId, (Integer) userId);
                        App.storeUserId((Integer)userId);
                        mySelf.save();
                        finish();
                    }

                    @Override
                    public void onError(final Object input) {
                        LoggingActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                alert.soft("Unable to log in, server exception");
                            }
                        });
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_logging, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
