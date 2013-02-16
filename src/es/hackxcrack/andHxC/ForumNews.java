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

public class ForumNews extends Activity{

    private static Context context;

    /**
     * Description: Maneja las filas de la lista de posts.
     *
     */
    private class PostListAdapter extends ArrayAdapter<PostInfo> {
        private List<PostInfo> items;

        public PostListAdapter(Context context, int textViewResourceId, List<PostInfo> items) {
            super(context, textViewResourceId, items);
            this.items = items;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater layout = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = layout.inflate(R.layout.category_row_layout, null);
            }

            final PostInfo post = items.get(position);
            if (post != null) {
                TextView tvPostName = (TextView) v.findViewById(R.id.post_name);
                TextView tvAuthor = (TextView) v.findViewById(R.id.post_author);
                TextView tvResponseNum = (TextView) v.findViewById(R.id.response_num);
                if (tvPostName != null) {
                    tvPostName.setText(StringEscapeUtils.unescapeHtml(post.getName()));

                    if (post.isSubforum()){
                        tvPostName.setTypeface(null, Typeface.BOLD);
                    }
                    else{
                        tvPostName.setTypeface(null, Typeface.NORMAL);
                    }
                }

                String author = post.getAuthor();
                if (tvAuthor != null){
                    if (author != null){
                        tvAuthor.setText(getString(R.string.posted_by) + " " + author);
                    }
                    else{
                        tvAuthor.setText("");
                    }
                }

                Integer responseNum = post.getResponseNumber();
                if(tvResponseNum != null){
                    if (responseNum != null){
                        switch(responseNum){
                        case 0:
                            if (post.isSubforum()){
                                tvResponseNum.setText(getString(R.string.no_threads));
                            }
                            else{
                                tvResponseNum.setText(getString(R.string.no_responses));
                            }
                            break;

                        case 1:
                            if (post.isSubforum()){
                                tvResponseNum.setText(getString(R.string.one_thread));
                            }
                            else{
                                tvResponseNum.setText(getString(R.string.one_response));
                            }
                            break;

                        default:
                            if (post.isSubforum()){
                                tvResponseNum.setText(responseNum + " " + getString(R.string.threads));
                            }
                            else{
                                tvResponseNum.setText(responseNum + " " + getString(R.string.responses));
                            }
                        }
                    }
                    else{
                        tvResponseNum.setText("");
                    }
                }
            }
            return v;
        }
    }



    private List<PostInfo> postList;


    /**
     * Descripción: Crea el menú a partir de submenu.xml .
     *
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.news, menu);
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
        case R.id.goto_main_menu_item:
            Intent i = new Intent();
            i.setClass(this, SubMain.class);
            startActivity(i);
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }


    /**
     * Descripción: Muestra el post correspondiente cuando el
     *  usuario lo selecciona.
     *
     * @param position Posición del item seleccionado.
     */
    public void touchCallback(int position){
        PostInfo post = this.postList.get(position);

        // Jump to subforum
        if (post.isSubforum()){

            Intent i = new Intent();
            i.setClass(ForumNews.context, ForumNews.class);
            i.putExtra("id", post.getId());
            i.putExtra("name", post.getName());
            startActivity(i);
        }
        else {
            Intent i = new Intent();
            i.setClass(ForumNews.context, ForumThread.class);
            i.putExtra("id", post.getId());
            startActivity(i);
        }
    }


    /**
     * Descripción: Muestra la lista de posts.
     *
     */
    public void showPosts(){
        ListView listView = (ListView) findViewById(R.id.post_list);

        PostListAdapter adapter = new PostListAdapter(
            this, R.layout.category_row_layout, this.postList);

        listView.setAdapter(adapter);
    }


    /**
     * Descripción: Prepara todas las estructuras para mostrar las novedades.
     *
     * @return boolean True si la operación se ha realizado correctamente.
     */
    private boolean populateNews(){
        postList = ForumManager.getNews();
        if (postList == null){
            Context context = getApplicationContext();
            CharSequence text = "Error requesting posts";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            return false;
        }

        Log.d("andHxC", postList.size() + "");

        return postList.size() != 0;
    }

    /** LLamado cuando la actividad se crea por primera vez. */
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        ForumNews.context = getApplicationContext();

        // Salta a la vista de carga temporalmente
        setContentView(R.layout.loading_view);

        // Carga en segundo plano los posts mientras muestra la pantalla de error
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params){
                return populateNews();
            }

            @Override
            protected void onPostExecute(Boolean populated) {
                // Si falló al tomar la lista de elementos
                if (!populated){
                    finish();
                }

                // Mostrar la lista
                setContentView(R.layout.forum_category);

                ListView listView = (ListView) findViewById(R.id.post_list);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            touchCallback(position);
                        }
                    });

                showPosts();
            }
        }.execute();
    }
}
