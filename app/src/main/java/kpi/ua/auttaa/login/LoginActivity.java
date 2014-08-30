package kpi.ua.auttaa.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;

import kpi.ua.auttaa.R;
/**
 * A login screen that offers login via email/password.

 */
public class LoginActivity extends FragmentActivity {

    public static final String IS_USER_LOGGED_IN = "IS_USER_LOGGED_IN";
    public static final String SECRET_STRING = "SECRET_STRING";
    public static final String USER_ID = "USER_ID";


    private LoginButton loginButton;
    private UiLifecycleHelper uiHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uiHelper = new UiLifecycleHelper(this, statusCallback);
        uiHelper.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        loginButton = (LoginButton) findViewById(R.id.authButton);

        loginButton.setUserInfoChangedCallback(new LoginButton.UserInfoChangedCallback() {
            @Override
            public void onUserInfoFetched(GraphUser user) {
                if (user != null) {
                    //TODO: generate secret string!
                    String secretString = "veryfuckingsecretstring";
                    String userId = user.getId();

                    SharedPreferences prefs = LoginActivity.this.getSharedPreferences("kpi.ua.auttaa", MODE_PRIVATE);
                    prefs.edit().putBoolean(IS_USER_LOGGED_IN, true).putString(SECRET_STRING, secretString).putString(USER_ID, userId).apply();
                    Toast.makeText(getApplicationContext(), "Hi, " + userId, Toast.LENGTH_SHORT).show();
                    LoginActivity.this.finish();
                } else {
                    Toast.makeText(getApplicationContext(), "You are not logged in", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private Session.StatusCallback statusCallback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state,
                         Exception exception) {
            if (state.isOpened()) {

                Log.d("FacebookSampleActivity", "Facebook session opened");
            } else if (state.isClosed()) {
                Log.d("FacebookSampleActivity", "Facebook session closed");
            }
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onSaveInstanceState(Bundle savedState) {
        super.onSaveInstanceState(savedState);
        uiHelper.onSaveInstanceState(savedState);
    }
}



