package es.hackxcrack.andHxC;

import android.app.Activity;
import android.os.Bundle;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

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



public class Settings extends Activity {

    /**
     * Descripción: Muestra el diálogo para seleccionar el tema.
     *
     */
    public void changeTheme(){

    }

    public String getCurrentThemeName(){
        int theme = 0;
        try {
            String packageName = getClass().getPackage().getName();
            PackageInfo packageInfo = getPackageManager().getPackageInfo(packageName, PackageManager.GET_META_DATA);
            theme = packageInfo.applicationInfo.theme;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        if (theme == 0){
            return null;
        }

        String themeName = getResources().getResourceName(theme);
        String[] parts = themeName.split("/");
        return parts[parts.length - 1];
    }


    /** LLamado cuando la actividad se crea por primera vez. */
    @Override
    public void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        // Declara el callback
        RelativeLayout chooseThemeBar = (RelativeLayout) findViewById(R.id.choose_theme_bar);
        chooseThemeBar.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    changeTheme();
                }
            });

        TextView tvThemeName = (TextView) findViewById(R.id.textv_current_theme);
        tvThemeName.setText(getCurrentThemeName());

    }
}
