package cmc.note.fragments;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.Log;

import cmc.note.R;
import cmc.note.activities.SettingActivity;

/**
 * Created by tuanb on 10-Nov-16.
 */

public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);

        findPreference("sync_open").setOnPreferenceClickListener(this);
        findPreference("sync_save").setOnPreferenceClickListener(this);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        switch (preference.getKey()) {
            case ("sync_open"): {
                SettingActivity activity = (SettingActivity) getActivity();
                activity.onClickOpenFile();
                break;
            }
            case ("sync_save"): {
                SettingActivity activity = (SettingActivity) getActivity();
                activity.onClickSaveFile();
                break;
            }
        }
        return false;
    }
}
