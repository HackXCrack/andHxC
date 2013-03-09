package es.hackxcrack.andHxC;

import android.content.Context;

import android.database.sqlite.*;
import android.database.Cursor;

import android.util.Log;

/**
 * Se encarga de la creación, almacenamiento y recogida de datos.
 *
 */
public class DBManager {
    private SQLiteDatabase db;
    private Context context;
    private DBOpenHelper dbOpener;

    private final static String FORUM_DB_NAME = "FORUM_DB";
    private final static int DB_VERSION = 1;

    private final static String CATEGORY_TABLE = "CATEGORY";

    private class DBOpenHelper extends SQLiteOpenHelper {
        public DBOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
            super(context, name, factory, version);
        }


        public void onCreate(SQLiteDatabase db){
            for (String query: createDBQuery){
                db.execSQL(query);
            }

            for (String query: populateDBQuery){
                db.execSQL(query);
            }
        }


        public void onUpgrade(SQLiteDatabase db, int d,int e){
        }
    }


    /**
     * Constructor de la clase.
     *
     * @param Context context El contexto en el que se ejecuta la base de datos.
     *
     */
    public DBManager(Context context){
        this.context = context;
        dbOpener = new DBOpenHelper(context, FORUM_DB_NAME, null, DB_VERSION);
    }


    public Cursor getCategoriesCursor(){
        SQLiteDatabase db = dbOpener.getWritableDatabase();
        return db.rawQuery(getCategoriesQuery, null);
    }


    private final static String getCategoriesQuery =
        "SELECT C._id, C.NAME, C.CODE, GROUP_CONCAT(SC.NAME)" +
        " FROM CATEGORY C LEFT JOIN SUBCATEGORY SC on C._id = SC.CATEGORY_ID " +
        " GROUP BY C._id;";


    // Queries para generar la base de datos
    // Estructura de la base de datos
    private final static String[] createDBQuery = new String[]{
        // Tabla de secciones
        "CREATE TABLE IF NOT EXISTS SECTION" +
        " ( _id INTEGER PRIMARY KEY AUTOINCREMENT, " +
        " NAME VARCHAR); ",

        // Tabla de categorías
        "CREATE TABLE IF NOT EXISTS CATEGORY" +
        " ( _id INTEGER PRIMARY KEY AUTOINCREMENT, " +
        " NAME VARCHAR, CODE INT, SECTION_ID INT," +
        " FOREIGN KEY (SECTION_ID) REFERENCES SECTION(_id));",

        // Tabla de subcategorías
        "CREATE TABLE IF NOT EXISTS SUBCATEGORY" +
        " ( _id INTEGER PRIMARY KEY AUTOINCREMENT, " +
        " NAME VARCHAR, CATEGORY_ID INT, " +
        " FOREIGN KEY(CATEGORY_ID) REFERENCES CATEGORY(_id));"};

    // Datos semilla (relación de categorías y subcategorías)
    /* Se fija la _id de algunos para conocerlas, ya que son necesarias como
     * claves foráneas.
     */
    private final static String[] populateDBQuery = new String[]{

        // Tabla de secciones
        "INSERT INTO SECTION (_id, NAME) VALUES ( 0, 'Hack X Crack');",
        "INSERT INTO SECTION (_id, NAME) VALUES ( 1, 'Cuadernos HXC');",
        "INSERT INTO SECTION (_id, NAME) VALUES ( 2, 'Preparación para certificados');",
        "INSERT INTO SECTION (_id, NAME) VALUES ( 3, 'Wargames');",
        "INSERT INTO SECTION (_id, NAME) VALUES ( 4, 'Seguridad Informática');",
        "INSERT INTO SECTION (_id, NAME) VALUES ( 5, 'Redes y comunicación');",
        "INSERT INTO SECTION (_id, NAME) VALUES ( 6, 'Programación');",
        "INSERT INTO SECTION (_id, NAME) VALUES ( 7, 'Sistemas operativos');",
        "INSERT INTO SECTION (_id, NAME) VALUES ( 8, 'Electrónica');",
        "INSERT INTO SECTION (_id, NAME) VALUES ( 9, 'Informática');",
        "INSERT INTO SECTION (_id, NAME) VALUES (10, 'Biblioteca');",


        // Tabla de categorías
        "INSERT INTO CATEGORY (_id, NAME, CODE, SECTION_ID) VALUES ( 0, 'Hack X Crack', 3, 0);",
        "INSERT INTO CATEGORY (_id, NAME, CODE, SECTION_ID) VALUES ( 1, 'Noticias informáticas', 23, 0);",
        "INSERT INTO CATEGORY (_id, NAME, CODE, SECTION_ID) VALUES ( 2, 'Dudas generales', 11, 0);",
        "INSERT INTO CATEGORY (_id, NAME, CODE, SECTION_ID) VALUES ( 3, 'Off-Topic', 24, 0);",

        "INSERT INTO CATEGORY (_id, NAME, CODE, SECTION_ID) VALUES ( 4, 'Antiguos cuadernos', 9, 1);",
        "INSERT INTO CATEGORY (_id, NAME, CODE, SECTION_ID) VALUES ( 5, 'Nuevos cuadernos', 10, 1);",

        "INSERT INTO CATEGORY (_id, NAME, CODE, SECTION_ID) VALUES ( 6, 'CCNA', 72, 2);",

        "INSERT INTO CATEGORY (_id, NAME, CODE, SECTION_ID) VALUES ( 7, 'Wargames', 57, 3);",

        "INSERT INTO CATEGORY (_id, NAME, CODE, SECTION_ID) VALUES ( 8, 'Hacking', 14, 4);",
        "INSERT INTO CATEGORY (_id, NAME, CODE, SECTION_ID) VALUES ( 9, 'Ingeniería inversa', 48, 4);",
        "INSERT INTO CATEGORY (_id, NAME, CODE, SECTION_ID) VALUES (10, 'Bugs y exploits', 15, 4);",
        "INSERT INTO CATEGORY (_id, NAME, CODE, SECTION_ID) VALUES (11, 'Malware', 16, 4);",
        "INSERT INTO CATEGORY (_id, NAME, CODE, SECTION_ID) VALUES (12, 'Seguridad infomática', 27, 4);",
        "INSERT INTO CATEGORY (_id, NAME, CODE, SECTION_ID) VALUES (13, 'Criptología', 37, 4);",

        "INSERT INTO CATEGORY (_id, NAME, CODE, SECTION_ID) VALUES (14, 'Hacking wireless', 31, 5);",
        "INSERT INTO CATEGORY (_id, NAME, CODE, SECTION_ID) VALUES (15, 'Redes (WAN, LAN, MAN, CAM, ...)', 32, 5);",
        "INSERT INTO CATEGORY (_id, NAME, CODE, SECTION_ID) VALUES (16, 'Phreak', 50, 5);",

        "INSERT INTO CATEGORY (_id, NAME, CODE, SECTION_ID) VALUES (17, 'Programación general', 51, 6);",
        "INSERT INTO CATEGORY (_id, NAME, CODE, SECTION_ID) VALUES (18, 'Sources', 59, 6);",
        "INSERT INTO CATEGORY (_id, NAME, CODE, SECTION_ID) VALUES (19, 'Scripting', 19, 6);",
        "INSERT INTO CATEGORY (_id, NAME, CODE, SECTION_ID) VALUES (20, 'Programación web', 20, 6);",

        "INSERT INTO CATEGORY (_id, NAME, CODE, SECTION_ID) VALUES (21, 'Windows', 1, 7);",
        "INSERT INTO CATEGORY (_id, NAME, CODE, SECTION_ID) VALUES (22, 'Gnu / Linux', 4, 7);",
        "INSERT INTO CATEGORY (_id, NAME, CODE, SECTION_ID) VALUES (23, 'Mac OS X', 5, 7);",

        "INSERT INTO CATEGORY (_id, NAME, CODE, SECTION_ID) VALUES (24, 'Electrónica', 71, 8);",

        "INSERT INTO CATEGORY (_id, NAME, CODE, SECTION_ID) VALUES (25, 'Hardware', 25, 9);",
        "INSERT INTO CATEGORY (_id, NAME, CODE, SECTION_ID) VALUES (26, 'Software', 26, 9);",
        "INSERT INTO CATEGORY (_id, NAME, CODE, SECTION_ID) VALUES (27, 'Diseño gráfico', 38, 9);",

        "INSERT INTO CATEGORY (_id, NAME, CODE, SECTION_ID) VALUES (28, 'Manuales y revistas', 6 , 10);",
        "INSERT INTO CATEGORY (_id, NAME, CODE, SECTION_ID) VALUES (29, 'Videotutoriales', 7, 10);",



        // Tabla de subcategorías
        "INSERT INTO SUBCATEGORY (NAME, CATEGORY_ID) VALUES ('Reglas', 0);",
        "INSERT INTO SUBCATEGORY (NAME, CATEGORY_ID) VALUES ('Sugerencias', 0);",
        "INSERT INTO SUBCATEGORY (NAME, CATEGORY_ID) VALUES ('Proyectos Hack X Crack', 0);",
        "INSERT INTO SUBCATEGORY (NAME, CATEGORY_ID) VALUES ('Presentaciones', 0);",

        "INSERT INTO SUBCATEGORY (NAME, CATEGORY_ID) VALUES ('Lockpicking', 3);",
        "INSERT INTO SUBCATEGORY (NAME, CATEGORY_ID) VALUES ('Debates semanales', 3);",

        "INSERT INTO SUBCATEGORY (NAME, CATEGORY_ID) VALUES ('Forense', 8);",

        "INSERT INTO SUBCATEGORY (NAME, CATEGORY_ID) VALUES ('Tutoriales y videotutoriales reversing', 9);",
        "INSERT INTO SUBCATEGORY (NAME, CATEGORY_ID) VALUES ('Defacing', 10);",

        "INSERT INTO SUBCATEGORY (NAME, CATEGORY_ID) VALUES ('Tutoriales', 11);",
        "INSERT INTO SUBCATEGORY (NAME, CATEGORY_ID) VALUES ('Descarga malware', 11);",
        "INSERT INTO SUBCATEGORY (NAME, CATEGORY_ID) VALUES ('Desarrollo y estudio de malware', 11);",

        "INSERT INTO SUBCATEGORY (NAME, CATEGORY_ID) VALUES ('C / C++', 17);",
        "INSERT INTO SUBCATEGORY (NAME, CATEGORY_ID) VALUES ('Java-Android', 17);",
        "INSERT INTO SUBCATEGORY (NAME, CATEGORY_ID) VALUES ('Visual Basic', 17);",
        "INSERT INTO SUBCATEGORY (NAME, CATEGORY_ID) VALUES ('Ejercicios resueltos', 17);",

        "INSERT INTO SUBCATEGORY (NAME, CATEGORY_ID) VALUES ('Batch / Bash', 19);",
        "INSERT INTO SUBCATEGORY (NAME, CATEGORY_ID) VALUES ('Perl', 19);",
        "INSERT INTO SUBCATEGORY (NAME, CATEGORY_ID) VALUES ('Python', 19);",

        "INSERT INTO SUBCATEGORY (NAME, CATEGORY_ID) VALUES ('Software libre y GNU/Linux libre', 22);",

        "INSERT INTO SUBCATEGORY (NAME, CATEGORY_ID) VALUES ('Soporte técnico y repareción', 25);",
        "INSERT INTO SUBCATEGORY (NAME, CATEGORY_ID) VALUES ('Modding y overclocking', 25);",

        "INSERT INTO SUBCATEGORY (NAME, CATEGORY_ID) VALUES ('Room Gammer [Friki]', 27);",
    };
}
