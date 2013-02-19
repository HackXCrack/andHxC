package es.hackxcrack.andHxC;

import android.app.Activity;
import android.os.Bundle;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

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

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import android.util.Log;



public class SubMain extends Activity{


    /**
     * Descripción: Encapsula la información sobre un post para la vista.
     *
     */
    private class ForumInfo {
        private String name;
        private int postNumber;

        ForumInfo(String name, int postNumber){
            this.name = name;
            this.postNumber = postNumber;
        }

        public String getName(){
            return name;
        }

        public String getPostNumberStr(){
            if (postNumber < 0){
                return "";
            }
            else{
                switch(postNumber){
                case 0:
                    return getString(R.string.no_posts);

                case 1:
                    return getString(R.string.one_post);

                default:
                    return postNumber + " " + getString(R.string.posts);
                }
            }
        }
    }


    /**
     * Description: Maneja las filas de la lista del foro principal.
     *
     */
    private class ForumRowAdapter extends ArrayAdapter<ForumInfo> {
        private List<ForumInfo> items;

        public ForumRowAdapter(Context context, int textViewResourceId, List<ForumInfo> items) {
            super(context, textViewResourceId, items);
            this.items = items;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater layout = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = layout.inflate(R.layout.forum_row_layout, null);
            }
            ForumInfo forum = items.get(position);
            if (forum != null) {
                TextView forumName = (TextView) v.findViewById(R.id.forum_name);
                TextView postNum = (TextView) v.findViewById(R.id.post_num);
                if (forumName != null) {
                    forumName.setText(forum.getName());
                }
                if(postNum != null){
                    postNum.setText(forum.getPostNumberStr());
                }
            }
            return v;
        }
    }





    private HashMap<String, String> subForumNameIdMap;
    private List<ForumInfo> defaultSubForumList;
    private HashMap<String, String> defaultSubForumNameIdMap;
    private UserManager userManager = new UserManager();


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
            this, R.layout.forum_row_layout, this.defaultSubForumList);

        listView.setAdapter(adapter);
    }


    /**
     * Descripción: Muestra el subforo correspondiente cuando el
     *  usuario lo selecciona.
     *
     * @param position Posición del item seleccionado.
     */
    public void touchCallback(int position){
        String subForum = this.defaultSubForumList.get(position).getName();
        int id = Integer.parseInt(this.subForumNameIdMap.get(subForum));

        Intent i = new Intent();
        i.setClass(this, ForumCategory.class);
        i.putExtra("id", id);
        i.putExtra("name", subForum);
        startActivity(i);
    }


    /**
     * Descripción: Rellena la lista de subforos con entradas estáticas.
     *  Dado que normalmente no va a haber cambios en la lista, de esta forma
     *  no hay que volver a cargarla todas las veces.
     *
     */
    public void prePopulateSubForumList(){

        List<ForumInfo> list;
        HashMap<String, String> map;

        /* Lista de nombres de subforos, la capacidad inicial es
         *  29, el número de subforos.
         */
        this.defaultSubForumList = list = new ArrayList<ForumInfo>(29);

        this.defaultSubForumNameIdMap = map = new HashMap<String, String>();

        // Esto es bastante sucio, habría que buscar otra forma de hacerlo :/
        map.put("Hack x Crack", "3");           list.add(new ForumInfo("Hack x Crack", -1));
        map.put("Noticias Informáticas", "23"); list.add(new ForumInfo("Noticias Informáticas", -1));
        map.put("Dudas Generales", "11");       list.add(new ForumInfo("Dudas Generales", -1));
        map.put("Off-Topic", "24");             list.add(new ForumInfo("Off-Topic", -1));

        map.put("Antiguos Cuadernos", "9");     list.add(new ForumInfo("Antiguos Cuadernos", -1));
        map.put("Nuevos Cuadernos", "10");      list.add(new ForumInfo("Nuevos Cuadernos", -1));

        map.put("Wargames", "57");              list.add(new ForumInfo("Wargames", -1));

        map.put("Hacking", "14");               list.add(new ForumInfo("Hacking", -1));
        map.put("Ingenieria Inversa", "48");    list.add(new ForumInfo("Ingenieria Inversa", -1));
        map.put("Bugs y Exploits", "15");       list.add(new ForumInfo("Bugs y Exploits", -1));
        map.put("Malware", "16");               list.add(new ForumInfo("Malware", -1));
        map.put("Seguridad informatica", "27"); list.add(new ForumInfo("Seguridad informatica", -1));
        map.put("Criptografía & Esteganografía", "37"); list.add(new ForumInfo("Criptografía & Esteganografía", -1));

        map.put("Hacking Wireless", "31");      list.add(new ForumInfo("Hacking Wireless", -1));
        map.put("Redes (WAN, LAN, MAN, CAM, ...)", "32"); list.add(new ForumInfo("Redes (WAN, LAN, MAN, CAM, ...)", -1));
        map.put("Phreak", "50");                list.add(new ForumInfo("Phreak", -1));

        map.put("Programación General", "51");  list.add(new ForumInfo("Programación General", -1));
        map.put("Sources", "59");               list.add(new ForumInfo("Sources", -1));
        map.put("Scripting", "19");             list.add(new ForumInfo("Scripting", -1));
        map.put("Programación Web", "20");      list.add(new ForumInfo("Programación Web", -1));

        map.put("Windows", "1");                list.add(new ForumInfo("Windows", -1));
        map.put("GNU/Linux", "4");              list.add(new ForumInfo("GNU/Linux", -1));
        map.put("Mac OS X", "5");               list.add(new ForumInfo("Mac OS X", -1));

        map.put("Electrónica", "71");           list.add(new ForumInfo("Electrónica", -1));

        map.put("Hardware", "25");              list.add(new ForumInfo("Hardware", -1));
        map.put("Software", "26");              list.add(new ForumInfo("Software", -1));
        map.put("Diseño Gráfico", "38");        list.add(new ForumInfo("Diseño Gráfico", -1));

        map.put("Manuales y revistas", "6");    list.add(new ForumInfo("Manuales y revistas", -1));
        map.put("Videotutoriales", "7");        list.add(new ForumInfo("Videotutoriales", -1));


        this.subForumNameIdMap = this.defaultSubForumNameIdMap;
        this.showSubForumList();
    }


    /**
     * Descripción: Rellena la lista de subforos.
     *
     */
    public void populateSubForumList(){
        this.prePopulateSubForumList();
    }


    /** LLamado cuando la actividad se crea por primera vez. */
    @Override
    public void onCreate(Bundle savedInstanceState){

        this.subForumNameIdMap = new HashMap<String, String>();

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

        this.populateSubForumList();

    }
}
