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

import org.apache.commons.lang.StringEscapeUtils;


public class ForumThread extends Activity{

    public List<MessageInfo> msgList;
    private int lastPageRendered;
    private int threadId;
    private MessageListAdapter adapter;
    private final int messagesPerPage = 10;

    /**
     * Descripción: Crea el menú a partir de submenu.xml .
     *
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        if (ForumManager.getSessionCookie() != null){
            inflater.inflate(R.menu.thread_logged, menu);
        } else {
            inflater.inflate(R.menu.thread, menu);
        }
        return true;
    }


    /**
     * Descripción: Maneja la acción de seleccionar un item del menú.
     *
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent i;

        // Handle item selection
        switch (item.getItemId()) {
        case R.id.goto_news_menu_item:
            i = new Intent();
            i.setClass(this, ForumNews.class);
            startActivity(i);
            return true;

        case R.id.setting_menu_item:
            i = new Intent();
            i.setClass(this, Settings.class);
            startActivity(i);
            return true;

        case R.id.private_messages_menu_item:
            i = new Intent();
            i.setClass(this, Messages.class);
            startActivity(i);
            return true;

        default:
            return super.onOptionsItemSelected(item);
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
                return ForumManager.getItemsFromThread(threadId, lastPageRendered);
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
     * Descripción: Muestra la lista de mensajes.
     *
     */
    public void showMessages(){
        ListView listView = (ListView) findViewById(R.id.message_list);

        adapter = new MessageListAdapter(this, R.layout.thread_row_layout, this.msgList, messagesPerPage);

        listView.setAdapter(adapter);
    }


    /**
     * Descripción: Prepara todas las estructuras para mostrar la página `page`
     *  del hilo con el id seleccionado.
     *
     * @param threadId El ID del hilo.
     * @param page La página del hilo a mostrar (la 0 sería la primera).
     *
     * @return boolean True si la operación se ha realizado correctamente.
     */
    private boolean populateFromPage(int threadId, int page){
        msgList = ForumManager.getItemsFromThread(threadId, page);
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

        Intent i = getIntent();

        threadId = i.getIntExtra("id", -1);

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



        // Salta a la vista de carga temporalmente
        setContentView(R.layout.loading_view);

        // Carga en segundo plano los posts mientras muestra la pantalla de error
        new AsyncTask<Void, Void, Boolean>() {
            @Override
                protected Boolean doInBackground(Void... params) {
                return populateFromPage(threadId, 0);
            }

            @Override
                protected void onPostExecute(Boolean populated) {
                // Si falló al tomar la lista de elementos
                if (!populated){
                    finish();
                }

                lastPageRendered = 0;
                // Mostrar la lista
                setContentView(R.layout.forum_thread);

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
