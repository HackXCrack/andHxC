package es.hackxcrack.andHxC;

import android.app.Activity;
import android.os.Bundle;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.util.Log;



public class SubMain extends Activity{



    /**
     * Description: Maneja las filas de la lista del foro principal.
     *
     */
    private class ForumRowAdapter extends CursorAdapter {
        private Cursor cursor;

        public ForumRowAdapter(Context context, int Layout, Cursor c) {
            super(context, c);
        }


        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent){
            Cursor c = getCursor();

            final LayoutInflater layoutInflater = (LayoutInflater)LayoutInflater.from(context);
            View v = layoutInflater.inflate(R.layout.forum_row_layout, parent, false);

            bindView(v, context, c);
            return v;
        }


        @Override
        public void bindView(View v, Context context, Cursor c) {

            // Construcción del mapa que relaciona posición con subforo
            int id = c.getInt(0);
            int code = c.getInt(2);
            positionToCode.put(id, code);

            // Construcción de la vista
            String name = c.getString(1);
            String subforums = c.getString(3);

            TextView tvName = (TextView) v.findViewById(R.id.forum_name);
            if (tvName != null) {
                tvName.setText(name);
            }

            TextView tvSubforums = (TextView) v.findViewById(R.id.forum_subforums);
            if (tvSubforums != null) {
                tvSubforums.setText(subforums);
            }
        }
    }




    private Map<Integer, Integer> positionToCode = new HashMap<Integer, Integer>();
    private UserManager userManager = new UserManager();
    private DBManager dbManager;


    /**
     * Descripción: Crea el menú a partir de submenu.xml .
     *
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.submain, menu);
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
     * Descripción: Muestra la lista de subforos tomandola de this.defaultSubForumList.
     *
     */
    public void showSubForumList(){
        ListView listView = (ListView) findViewById(R.id.subforum_list);

        ForumRowAdapter adapter = new ForumRowAdapter(
            this, R.layout.forum_row_layout, dbManager.getCategoriesCursor());

        listView.setAdapter(adapter);
    }


    /**
     * Descripción: Muestra el subforo correspondiente cuando el
     *  usuario lo selecciona.
     *
     * @param position Posición del item seleccionado.
     */
    public void touchCallback(int position){
        int id = positionToCode.get(position);

        Intent i = new Intent();
        i.setClass(this, ForumCategory.class);
        i.putExtra("id", id);
        startActivity(i);
    }


    /** LLamado cuando la actividad se crea por primera vez. */
    @Override
    public void onCreate(Bundle savedInstanceState){

        dbManager = new DBManager(getApplicationContext());
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


        setContentView(R.layout.submain);

        // Declara el callback
        ListView listView = (ListView) findViewById(R.id.subforum_list);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    touchCallback(position);
                }
            });

        this.showSubForumList();

    }
}
