package es.hackxcrack.andHxC;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;

import android.content.Context;
import android.content.Intent;

import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import android.view.View;

import android.util.Log;

public class ForumCategory extends Activity{

    private List<String> postNameList;
    private List<String> postIdList;


    /**
     * Descripción: Muestra el post correspondiente cuando el
     *  usuario lo selecciona.
     *
     * @param position Posición del item seleccionado.
     */
    public void touchCallback(int position){
        String name = this.postNameList.get(position);
        String id = this.postIdList.get(position);

        /*
        Intent i = new Intent();
        i.setClass(this, PostActivity.class);
        i.putExtra("id", id);
        i.putExtra("name", name);
        startActivity(i);
        */
    }


    /**
     * Descripción: Muestra la lista de posts tomandola de this.postNameList.
     *
     */
    public void showPosts(){
        ListView listView = (ListView) findViewById(R.id.post_list);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                                                                R.layout.category_row_layout,
                                                                R.id.post_name,
                                                                this.postNameList);
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
        List<Pair<String, String>> posts = ForumManager.getPostsFromCategory(categoryId, page);
        if (posts == null){
            Context context = getApplicationContext();
            CharSequence text = "Error requesting posts";
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            return false;
        }

        this.postNameList = new ArrayList<String>();
        this.postIdList = new ArrayList<String>();


        for (Pair<String, String> post : posts){
            String name = post.left;
            String id = post.right;

            this.postNameList.add(name);
            this.postIdList.add(id);
        }

        return posts.size() != 0;
    }

    /** LLamado cuando la actividad se crea por primera vez. */
    @Override
    public void onCreate(Bundle savedInstanceState){

        Intent i = getIntent();

        int id = i.getIntExtra("id", -1);
        String name = i.getStringExtra("name");


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
                setContentView(R.layout.forum_category);

                ListView listView = (ListView) findViewById(R.id.post_list);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            touchCallback(position);
                        }
                    });

                showPosts();
            }
        }.execute(id);
    }

}
