package es.hackxcrack.andHxC;

import android.preference.PreferenceActivity;
import android.os.Bundle;

import android.content.Context;
import android.content.Intent;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceScreen;
import android.preference.ListPreference;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.RelativeLayout;

import android.util.TypedValue;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import android.util.Log;

public class Settings extends PreferenceActivity {

    private class PreferenceChangeMonitor implements OnPreferenceChangeListener {
        public void PreferenceChangeMonitor(){}

        public boolean onPreferenceChange(Preference preference, Object newValue){
            String key = preference.getKey();
            if (key.equals("theme")){
                int themeId = themeNameToId((String) newValue);
                setThemeId(themeId);
            }

            fillValues();

            return true;
        }
    }

    private static final String PREFERENCE_THEME = "theme";

    private ListPreference mTheme = null;


    private void setThemeId(int themeId){
        SharedPreferences preferences = getApplication().getSharedPreferences("global", 0);

        getApplication().setTheme(themeId);

        Editor editor = preferences.edit();
        editor.putInt("themeId", themeId);
        editor.commit();
    }


    private ListPreference setupListPreference(final String key, final String value) {
        final ListPreference prefView = (ListPreference) findPreference(key);
        prefView.setValue(value);
        prefView.setSummary(prefView.getEntry());
        prefView.setOnPreferenceChangeListener(new PreferenceChangeMonitor());
        return prefView;
    }


    private void saveSettings() {
        SharedPreferences preferences = getApplication().getSharedPreferences("global", 0);

        String themeName = mTheme.getValue();
        int themeId = themeNameToId(mTheme.getValue());
        getApplication().setTheme(themeId);

        Editor editor = preferences.edit();
        editor.putInt("themeId", themeId);
        editor.commit();

        Log.d("andHxC", "Saving theme name: " + themeName);
        fillValues();
    }


    private static int themeNameToId(String theme) {
        if (theme.equals("Oscuro")) {
            return R.style.Oscuro;
        }
        else if (theme.equals("Claro")) {
            return R.style.Claro;
        }
        else{
            return 0;
        }
    }


    private String themeIdToName(int id){
        if (id == 0){
            return null;
        }
        String themeName = getResources().getResourceName(id);
        String[] parts = themeName.split("/");
        return parts[parts.length - 1];
    }


    public int getCurrentThemeId(){
        SharedPreferences sp = getApplication().getSharedPreferences("global", 0);
        int theme = 0;
        if (sp.contains("themeId")){
            theme = sp.getInt("themeId", 0);
        }

        if (theme != 0){
            return theme;
        }

        try {
            String packageName = getClass().getPackage().getName();
            PackageInfo packageInfo = getPackageManager().getPackageInfo(packageName, PackageManager.GET_META_DATA);
            theme = packageInfo.applicationInfo.theme;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return theme;
    }


    private void fillValues(){
        String themeName = themeIdToName(getCurrentThemeId());
        Log.d("andHxC", "Theme name:" + themeName);
        mTheme = setupListPreference(PREFERENCE_THEME, themeName);
    }


    @Override
    protected void onPause() {
        saveSettings();
        super.onPause();
    }


    /** LLamado cuando la actividad se crea por primera vez. */
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        // Seleccionar tema
        SharedPreferences sp = getApplication().getSharedPreferences("global", 0);
        int themeId = 0;
        if (sp.contains("themeId")){
            themeId = sp.getInt("themeId", 0);
        }

        if (themeId != 0){
            setTheme(themeId);
        }



        addPreferencesFromResource(R.xml.global_preferences);
        setContentView(R.layout.settings);

        fillValues();
    }
}
