package es.hackxcrack.andHxC;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;

import android.content.Context;
import android.content.Intent;

import android.graphics.Typeface;

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

        public MessageListAdapter(Context context, int textViewResourceId, List<MessageInfo> messages) {
            super(context, textViewResourceId, messages);
            this.messages = messages;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater layout = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = layout.inflate(R.layout.thread_row_layout, null);
            }

            final MessageInfo msg = messages.get(position);
            return v;
        }
    }


    public List<MessageInfo> msgList;


    /**
     * Descripción: Acción (por definir) cuando se actúa sobre un mensaje.
     *
     * @param position Posición del item seleccionado.
     * @TODO Definir acción a realizar.
     */
    public void touchCallback(int position){
    }


    /**
     * Descripción: Muestra la lista de mensajes.
     *
     */
    public void showMessages(){
        ListView listView = (ListView) findViewById(R.id.message_list);

        MessageListAdapter adapter = new MessageListAdapter(
            this, R.layout.thread_row_layout, this.msgList);

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

        int id = i.getIntExtra("id", -1);

        super.onCreate(savedInstanceState);

        // Salta a la vista de carga temporalmente
        setContentView(R.layout.loading_view);

        // Carga en segundo plano los posts mientras muestra la pantalla de error
        new AsyncTask<Integer, Void, Boolean>() {
            @Override
                protected Boolean doInBackground(Integer... id) {
                return populateFromPage(id[0], 0);
            }

            @Override
                protected void onPostExecute(Boolean populated) {
                // Si falló al tomar la lista de elementos
                if (!populated){
                    finish();
                }

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
        }.execute(id);
    }

}
