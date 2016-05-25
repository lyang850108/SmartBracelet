package com.smartbracelet.com.smartbracelet.activity;


import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.preference.SwitchPreference;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;

import com.smartbracelet.com.smartbracelet.R;
import com.smartbracelet.com.smartbracelet.util.ConstDefine;
import com.smartbracelet.com.smartbracelet.util.LogUtil;
import com.smartbracelet.com.smartbracelet.util.SharedPreferencesHelper;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p/>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends AppCompatPreferenceActivity implements Preference.OnPreferenceChangeListener, ConstDefine {

    private SwitchPreference mNewMessagePref;
    private SwitchPreference mEnableAutoLinkPref;
    private SwitchPreference mVibratePref;
    private RingtonePreference mRingtonePref;
    private ListPreference mSyncPref;
    private Preference mAddressPrefs;
    // Menu entries
    private static final int MENU_RESTORE_DEFAULTS    = 1;

    private SharedPreferencesHelper sharedPreferencesHelper;


    public static final String KEY_NEW_MESSAGE     = "notifications_new_message";
    public static final String KEY_AUTO_LINK= "auto_link";
    public static final String KEY_VIBRATE    = "notifications_new_message_vibrate";
    public static final String KEY_RINGTONE= "notifications_new_message_ringtone";
    public static final String KEY_SYNC_FREQUENCE    = "sync_frequency";
    public static final String KEY_DEVICE_ADDRESS    = "mac_address_key";

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        boolean result = false;
        if (preference == mNewMessagePref) {
            LogUtil.d("mNewMessagePref" + newValue);
            if (null != sharedPreferencesHelper) {
                boolean notificationPrefs = (boolean) newValue;
                sharedPreferencesHelper.putBoolean(NOTIFICATION_PREF, notificationPrefs);
                result = true;
            }
        } else if (preference == mSyncPref) {
            final String summary = newValue.toString();
            int internal = 0;
            int index = mSyncPref.findIndexOfValue(summary);
            mSyncPref.setSummary(mSyncPref.getEntries()[index]);
            mSyncPref.setValue(summary);
            switch (index) {
                case 0:
                    internal = 15000;
                    break;
                case 1:
                    internal = 20000;
                    break;
                case 2:
                    internal = 60000;
                    break;
                case 3:
                    internal = 180000;
                    break;
                case 4:
                    internal = 360000;
                    break;

            }
            if (null != sharedPreferencesHelper) {
                if (null != App.timerTask) {
                    LogUtil.d("times == " + internal);
                    App.timerTask.setPeriod(internal);
                }
                sharedPreferencesHelper.putInt(SP_POST_INTERNAL, internal);
                result = true;
            }
        }
        return result;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadPrefs();
        setTitle("设置");
        setupActionBar();
    }

    private void loadPrefs() {
        LogUtil.d("loadPrefs");
        addPreferencesFromResource(R.xml.pref_headers);
        sharedPreferencesHelper = SharedPreferencesHelper.getInstance();

        mEnableAutoLinkPref = (SwitchPreference) findPreference(KEY_AUTO_LINK);

        mNewMessagePref = (SwitchPreference) findPreference(KEY_NEW_MESSAGE);
        mVibratePref = (SwitchPreference) findPreference(KEY_VIBRATE);
        //获取硬件地址
        String bindAddress = sharedPreferencesHelper.getString(BLE_ADDRESS_PREF);
        mAddressPrefs = (Preference) findPreference(KEY_DEVICE_ADDRESS);
        mAddressPrefs.setSummary(bindAddress);


        mRingtonePref = (RingtonePreference) findPreference(KEY_RINGTONE);
        mRingtonePref.setShowDefault(false);
        mSyncPref
                = (ListPreference) findPreference(KEY_SYNC_FREQUENCE);
        mSyncPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                final String summary = newValue.toString();
                int internal = 0;
                int index = mSyncPref.findIndexOfValue(summary);
                mSyncPref.setSummary(mSyncPref.getEntries()[index]);
                mSyncPref.setValue(summary);
                switch (index) {
                    case 0:
                        internal = 15000;
                        break;
                    case 1:
                        internal = 20000;
                        break;
                    case 2:
                        internal = 60000;
                        break;
                    case 3:
                        internal = 180000;
                        break;
                    case 4:
                        internal = 360000;
                        break;

                }
                if (null != sharedPreferencesHelper) {
                    if (null != App.timerTask) {
                        LogUtil.d("times == " + internal);
                        App.timerTask.setPeriod(internal);
                    }
                    sharedPreferencesHelper.putInt(SP_POST_INTERNAL, internal);
                }
                return true;
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.clear();
        menu.add(0, MENU_RESTORE_DEFAULTS, 0, R.string.restore_default);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerListeners();
        invalidateOptionsMenu();
    }

    private void registerListeners() {
        mNewMessagePref.setOnPreferenceChangeListener(this);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_RESTORE_DEFAULTS:
                restoreDefaultPreferences();
                return true;

            case android.R.id.home:
                // The user clicked on the Messaging icon in the action bar. Take them back from
                // wherever they came from
                finish();
                return true;
        }
        return false;
    }
    private void restoreDefaultPreferences() {
        PreferenceManager.getDefaultSharedPreferences(this).edit().clear().apply();
        setPreferenceScreen(null);

    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

}
