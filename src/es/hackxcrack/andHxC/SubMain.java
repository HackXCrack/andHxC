package es.hackxcrack.andHxC;

import android.app.Activity;
import android.os.Bundle;

import android.content.Intent;

import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ArrayAdapter;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import android.view.View;

import android.util.Log;

public class SubMain extends Activity{

    private HashMap<String, String> subForumNameIdMap;
    private List<String> defaultSubForumList;
    private HashMap<String, String> defaultSubForumNameIdMap;
    private UserManager userManager = new UserManager();

    /**
     * Descripción: Muestra la lista de subforos tomandola de this.defaultSubForumList.
     *
     */
    public void showSubForumList(){
        ListView listView = (ListView) findViewById(R.id.subforum_list);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                                                                R.layout.forum_row_layout,
                                                                R.id.forum_name,
                                                                this.defaultSubForumList);
        listView.setAdapter(adapter);
    }


    /**
     * Descripción: Muestra el subforo correspondiente cuando el
     *  usuario lo selecciona.
     *
     * @param position Posición del item seleccionado.
     */
    public void touchCallback(int position){
        String subForum = this.defaultSubForumList.get(position);
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

        List<String> list;
        HashMap<String, String> map;

        /* Lista de nombres de subforos, la capacidad inicial es
         *  29, el número de subforos.
         */
        this.defaultSubForumList = list = new ArrayList<String>(29);

        this.defaultSubForumNameIdMap = map = new HashMap<String, String>();

        // Esto es bastante sucio, habría que buscar otra forma de hacerlo :/
        map.put("Hack x Crack", "3");           list.add("Hack x Crack");
        map.put("Noticias Informáticas", "23"); list.add("Noticias Informáticas");
        map.put("Dudas Generales", "11");       list.add("Dudas Generales");
        map.put("Off-Topic", "24");             list.add("Off-Topic");

        map.put("Antiguos Cuadernos", "9");     list.add("Antiguos Cuadernos");
        map.put("Nuevos Cuadernos", "10");      list.add("Nuevos Cuadernos");

        map.put("Wargames", "57");              list.add("Wargames");

        map.put("Hacking", "14");               list.add("Hacking");
        map.put("Ingenieria Inversa", "48");    list.add("Ingenieria Inversa");
        map.put("Bugs y Exploits", "15");       list.add("Bugs y Exploits");
        map.put("Malware", "16");               list.add("Malware");
        map.put("Seguridad informatica", "27"); list.add("Seguridad informatica");
        map.put("Criptografía & Esteganografía", "37"); list.add("Criptografía & Esteganografía");

        map.put("Hacking Wireless", "31");      list.add("Hacking Wireless");
        map.put("Redes (WAN, LAN, MAN, CAM, ...)", "32"); list.add("Redes (WAN, LAN, MAN, CAM, ...)");
        map.put("Phreak", "50");                list.add("Phreak");

        map.put("Programación General", "51");  list.add("Programación General");
        map.put("Sources", "59");               list.add("Sources");
        map.put("Scripting", "19");             list.add("Scripting");
        map.put("Programación Web", "20");      list.add("Programación Web");

        map.put("Windows", "1");                list.add("Windows");
        map.put("GNU/Linux", "4");              list.add("GNU/Linux");
        map.put("Mac OS X", "5");               list.add("Mac OS X");

        map.put("Electrónica", "71");           list.add("Electrónica");

        map.put("Hardware", "25");              list.add("Hardware");
        map.put("Software", "26");              list.add("Software");
        map.put("Diseño Gráfico", "38");        list.add("Diseño Gráfico");

        map.put("Manuales y revistas", "6");    list.add("Manuales y revistas");
        map.put("Videotutoriales", "7");        list.add("Videotutoriales");

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
        setContentView(R.layout.main);

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
