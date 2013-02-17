package es.hackxcrack.andHxC;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;

import android.content.Context;
import android.content.Intent;

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

    /**
     * Description: Maneja las filas de la lista de mensajes.
     *
     */
    private class MessageListAdapter extends ArrayAdapter<MessageInfo> {
        private List<MessageInfo> messages;
        private boolean loading;

        public MessageListAdapter(Context context, int textViewResourceId, List<MessageInfo> messages) {
            super(context, textViewResourceId, messages);
            this.messages = messages;
            loading = false;
        }

        public void setLoading(boolean loading){
            this.loading = loading;
        }


        public boolean isLoading(){
            return loading;
        }


        @Override
        public int getCount(){
            return messages.size() + ((messages.size() >= ((lastPageRendered + 1) * 10))? 1: 0);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater layout = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = layout.inflate(R.layout.thread_row_layout, null);
            }
            TextView tvAuthor = (TextView) v.findViewById(R.id.message_author);
            TextView tvText = (TextView) v.findViewById(R.id.message_text);

            if (position >= messages.size()){
                if (tvAuthor != null){
                    tvAuthor.setText("");
                }
                if (tvText != null){
                    if (loading){
                        tvText.setText(getString(R.string.loading_messages));
                    }
                    else{
                        tvText.setText(getString(R.string.load_more_messages));
                    }
                }

                return v;
            }

            final MessageInfo msg = messages.get(position);
            if (msg != null){
                if (tvAuthor != null){
                    String author = StringEscapeUtils.unescapeHtml(msg.getAuthor());
                    if (!author.equals("")){
                        tvAuthor.setText(getString(R.string.posted_by) + " " + author);
                    }
                }

                if (tvText != null){
                    tvText.setText(msg.getMessage());
                }
            }
            return v;
        }
    }


    public List<MessageInfo> msgList;
    private int lastPageRendered;
    private int threadId;
    private MessageListAdapter adapter;

    /**
     * Descripción: Crea el menú a partir de submenu.xml .
     *
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.thread, menu);
        return true;
    }


    /**
     * Descripción: Maneja la acción de seleccionar un item del menú.
     *
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
        case R.id.goto_news_menu_item:
            Intent i = new Intent();
            i.setClass(this, ForumNews.class);
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
        // Carga en segundo plano los posts mientras muestra la pantalla de error
        new AsyncTask<Void, Void, List<MessageInfo>>() {
            @Override
            protected List<MessageInfo> doInBackground(Void... params) {
                return ForumManager.getItemsFromThread(threadId, lastPageRendered);
            }

            @Override
            protected void onPostExecute(List<MessageInfo> messages) {
                msgList.addAll(messages);

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

        adapter = new MessageListAdapter(this, R.layout.thread_row_layout, this.msgList);

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
