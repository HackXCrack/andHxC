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

public class ForumCategory extends Activity{

    private static Context context;

    /**
     * Description: Maneja las filas de la lista de posts.
     *
     */
    private class PostListAdapter extends ArrayAdapter<PostInfo> {
        private List<PostInfo> items;
        private boolean loading;

        public PostListAdapter(Context context, int textViewResourceId, List<PostInfo> items) {
            super(context, textViewResourceId, items);
            this.items = items;
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
            return items.size() + (((loading) || (items.size() >= (
                                                      (lastPageRendered + 1) * 25)))
                                   ? 1
                                   : 0);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater layout = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = layout.inflate(R.layout.category_row_layout, null);
            }
            TextView tvPostName = (TextView) v.findViewById(R.id.post_name);
            TextView tvAuthor = (TextView) v.findViewById(R.id.post_author);
            TextView tvResponseNum = (TextView) v.findViewById(R.id.response_num);

            if (position >= items.size()){
                if (tvPostName != null){
                    if (loading){
                        tvPostName.setText(getString(R.string.loading_posts));
                    }
                    else{
                        tvPostName.setText(getString(R.string.load_more_posts));
                    }
                }
                if (tvAuthor != null){
                    tvAuthor.setText("");
                }
                if (tvResponseNum != null){
                    tvResponseNum.setText("");
                }
                return v;
            }

            final PostInfo post = items.get(position);
            if (post != null) {
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
    private int lastPageRendered;
    private int categoryId;
    private PostListAdapter adapter;

    /**
     * Descripción: Crea el menú a partir de submenu.xml .
     *
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.category, menu);
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

        default:
            return super.onOptionsItemSelected(item);
        }
    }


    /**
     * Descripción: Muestra la siguiente página de posts.
     *
     */
    public void renderNextPage(){
        lastPageRendered++;

        adapter.setLoading(true);
        adapter.notifyDataSetChanged();

        // Carga en segundo plano los posts mientras muestra la pantalla de error
        new AsyncTask<Void, Void, List<PostInfo>>() {
            @Override
            protected List<PostInfo> doInBackground(Void... params) {
                return ForumManager.getItemsFromCategory(categoryId, lastPageRendered);
            }

            @Override
            protected void onPostExecute(List<PostInfo> posts) {
                int pos = adapter.getCount();
                for(PostInfo post: posts){
                    adapter.add(post);
                }

                adapter.setLoading(false);
                adapter.notifyDataSetChanged();
            }
        }.execute();
    }


    /**
     * Descripción: Muestra el post correspondiente cuando el
     *  usuario lo selecciona.
     *
     * @param position Posición del item seleccionado.
     */
    public synchronized void touchCallback(int position){
        if (position < this.postList.size()){
            PostInfo post = this.postList.get(position);

            // Jump to subforum
            if (post.isSubforum()){

                Intent i = new Intent();
                i.setClass(ForumCategory.context, ForumCategory.class);
                i.putExtra("id", post.getId());
                i.putExtra("name", post.getName());
                startActivity(i);
            }
            else {
                Intent i = new Intent();
                i.setClass(ForumCategory.context, ForumThread.class);
                i.putExtra("id", post.getId());
                startActivity(i);
            }
        }
        else if (!adapter.isLoading()){
            renderNextPage();
        }
    }


    /**
     * Descripción: Muestra la lista de posts.
     *
     */
    public void showPosts(){
        ListView listView = (ListView) findViewById(R.id.post_list);

        adapter = new PostListAdapter(this, R.layout.category_row_layout, this.postList);

        listView.setAdapter(adapter);
    }


    /**
     * Descripción: Prepara todas las estructuras para mostrar la página `page`
     *  de la categoría con el id seleccionado.
     *
     * @param categoryId El ID de la categoría.
     * @param page La página de la categoría a mostrar (la 0 sería la primera).
     *
     * @return boolean True si la operación se ha realizado correctamente.
     */
    private boolean populateFromPage(int categoryId, int page){
        postList = ForumManager.getItemsFromCategory(categoryId, page);
        if (postList == null){
            Context context = getApplicationContext();
            CharSequence text = "Error requesting posts";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            return false;
        }

        return postList.size() != 0;
    }


    /** LLamado cuando la actividad se crea por primera vez. */
    @Override
    public void onCreate(Bundle savedInstanceState){

        Intent i = getIntent();

        categoryId = i.getIntExtra("id", -1);

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


        ForumCategory.context = getApplicationContext();

        // Salta a la vista de carga temporalmente
        setContentView(R.layout.loading_view);

        // Carga en segundo plano los posts mientras muestra la pantalla de error
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                return populateFromPage(categoryId, 0);
            }

            @Override
            protected void onPostExecute(Boolean populated) {
                // Si falló al tomar la lista de elementos
                if (!populated){
                    finish();
                }

                lastPageRendered = 0;
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
