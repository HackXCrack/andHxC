package es.hackxcrack.andHxC;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.graphics.Typeface;


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
import android.widget.Toast;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import android.view.View;

import android.util.Log;


public class Messages extends Activity{

    public List<MessageInfo> msgList;
    private MessageListAdapter adapter;
    private int lastPageRendered;
    private final int messagesPerPage = 15;

    /**
     * Descripción: Muestra la lista de mensajes.
     *
     */
    public void showMessages(){
        ListView listView = (ListView) findViewById(R.id.message_list);

        adapter = new MessageListAdapter(this, R.layout.message_row_layout, this.msgList, messagesPerPage);

        listView.setAdapter(adapter);
    }


    /**
     * Descripción: Acción (por definir) cuando se actúa sobre un mensaje.
     *
     * @param position Posición del item seleccionado.
     * @TODO Definir acción a realizar.
     */
    public synchronized void touchCallback(int position){
        if (position >= msgList.size()){
            if (!adapter.isLoading()){
                renderNextPage();
            }
        }
    }


    /**
     * Descripción: Muestra la siguiente página de mensajes.
     *
     */
    public void renderNextPage(){
        lastPageRendered++;

        adapter.setLoading(true);
        adapter.notifyDataSetChanged();

        // Carga en segundo plano los posts mientras muestra la pantalla de error
        new AsyncTask<Void, Void, List<MessageInfo>>() {
            @Override
            protected List<MessageInfo> doInBackground(Void... params) {
                return ForumManager.getPrivateMessages(lastPageRendered);
            }

            @Override
            protected void onPostExecute(List<MessageInfo> messages) {
                int pos = adapter.getCount();
                for(MessageInfo msg: messages){
                    adapter.add(msg);
                }

                adapter.setLoading(false);
                adapter.notifyDataSetChanged();
            }
        }.execute();
    }


    /**
     * Descripción: Prepara todas las estructuras para mostrar la página `page`
     *  de la lista de mensajes.
     *
     * @param page La página del hilo a mostrar (la 0 sería la primera).
     *
     * @return boolean True si la operación se ha realizado correctamente.
     */
    private boolean populateFromPage(int page){
        msgList = ForumManager.getPrivateMessages(page);
        if (msgList == null){
            Context context = getApplicationContext();
            CharSequence text = "Error requesting messages";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            return false;
        }

        return msgList.size() != 0;
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


        // Messages.context = getApplicationContext();

        // Salta a la vista de carga temporalmente
        setContentView(R.layout.loading_view);

        // Carga en segundo plano los posts mientras muestra la pantalla de carga
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                return populateFromPage(0);
            }

            @Override
            protected void onPostExecute(Boolean populated) {
                // Si falló al tomar la lista de elementos
                if (!populated){
                    finish();
                }

                lastPageRendered = 0;
                // Mostrar la lista
                setContentView(R.layout.messages);

                ListView listView = (ListView) findViewById(R.id.message_list);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            touchCallback(position);
                        }
                    });

                showMessages();
            }
        }.execute();
    }
}
