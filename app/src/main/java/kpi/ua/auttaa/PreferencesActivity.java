package kpi.ua.auttaa;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Created by okovalchuk on 8/30/14.
 */
public class PreferencesActivity extends PreferenceActivity {
    public static final String PREF_UPD_FREQ = "PREF_UPD_FREQ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.userpreferences);
    }
}
