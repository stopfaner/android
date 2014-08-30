package kpi.ua.auttaa.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.android.Util;

import org.json.JSONObject;

import kpi.ua.auttaa.R;

/**
 * A login screen that offers login via email/password.

 */
public class LoginActivity extends Activity {

    public static String mAPP_ID;
    public Facebook mFacebook = new Facebook(mAPP_ID);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAPP_ID = getResources().getString(R.string.app_id);
        setContentView(R.layout.activity_login);
        ((Button)findViewById(R.id.authButton)).setOnClickListener( loginButtonListener );
        SessionStore.restore(mFacebook, this);
    }


    //***********************************************************
    // onActivityResult
    //***********************************************************
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mFacebook.authorizeCallback(requestCode, resultCode, data);
    }


    //----------------------------------------------
    // loginButtonListener
    //----------------------------------------------
    private View.OnClickListener loginButtonListener = new View.OnClickListener() {
        public void onClick( View v ) {
            if( !mFacebook.isSessionValid() ) {
                Toast.makeText(LoginActivity.this, "Authorizing", Toast.LENGTH_SHORT).show();
                mFacebook.authorize(LoginActivity.this, new String[] { "" }, new LoginDialogListener());
            }
            else {
                Toast.makeText( LoginActivity.this, "Has valid session", Toast.LENGTH_SHORT).show();
                try {
                    JSONObject json = Util.parseJson(mFacebook.request("me"));
                    String facebookID = json.getString("id");
                    String firstName = json.getString("first_name");
                    String lastName = json.getString("last_name");
                    Toast.makeText(LoginActivity.this, "You already have a valid session, " + firstName + " " + lastName + ". No need to re-authorize.", Toast.LENGTH_SHORT).show();
                }
                catch( Exception error ) {
                    Toast.makeText( LoginActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    };


    //***********************************************************************
    //***********************************************************************
    // LoginDialogListener
    //***********************************************************************
    //***********************************************************************
    public final class LoginDialogListener implements Facebook.DialogListener {
        public void onComplete(Bundle values) {
            try {
                //The user has logged in, so now you can query and use their Facebook info
                JSONObject json = Util.parseJson(mFacebook.request("me"));
                String facebookID = json.getString("id");
                String firstName = json.getString("first_name");
                String lastName = json.getString("last_name");
                Toast.makeText( LoginActivity.this, "Thank you for Logging In, " + firstName + " " + lastName + "!", Toast.LENGTH_SHORT).show();
                SessionStore.save(mFacebook, LoginActivity.this);
            }
            catch( Exception error ) {
                Toast.makeText( LoginActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }

        }

        public void onFacebookError(FacebookError error) {
            Toast.makeText( LoginActivity.this, "Something went wrong. Please try again.", Toast.LENGTH_LONG).show();
        }

        public void onError(DialogError error) {
            Toast.makeText( LoginActivity.this, "Something went wrong. Please try again.", Toast.LENGTH_LONG).show();
        }

        public void onCancel() {
            Toast.makeText( LoginActivity.this, "Something went wrong. Please try again.", Toast.LENGTH_LONG).show();
        }
    }

}



