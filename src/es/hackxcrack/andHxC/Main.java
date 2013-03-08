package es.hackxcrack.andHxC;

import android.app.Activity;
import android.os.Bundle;
import android.os.AsyncTask;

import android.content.Intent;
import android.content.SharedPreferences;

/**
 * Maneja la entrada a la aplicación.
 *
 */
public class Main extends Activity{
    /**
     * LLamado cuando la actividad se crea por primera vez.
     * Pasa al siguiente Activity.
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Selecciona el punto de entrada
        SharedPreferences sp = getApplication().getSharedPreferences("global", 0);
        int initialActivity = 0;
        if (sp.contains("initialActivity")){
            initialActivity = sp.getInt("initialActivity", 0);
        }

        Intent i;

        // If no activity is selected show the login menu
        if (initialActivity == 0){
            i = new Intent();
            i.setClass(this, Login.class);
            startActivity(i);
        }
    }
}
