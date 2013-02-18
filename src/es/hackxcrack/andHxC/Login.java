package es.hackxcrack.andHxC;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Vector;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;


import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;

import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import es.hackxcrack.andHxC.R;
import es.hackxcrack.andHxC.SubMain;

public class Login extends Activity{

    private PopupWindow popupInfo = null;
    private PopupWindow popupLogin = null;

    private Activity me = null;
    private UserManager loginUser = null;

    private Point puntoPopups = null;


    /**
     * Descripción: Crea el menú a partir de submenu.xml .
     *
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.login, menu);
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
     * Descripción : Carga SubMain.java (activity SubMain). Contendra todas las funcionalidades de la app.
     *
     *
     */
    public void touchCallback(){
        Intent i = new Intent();
        i.setClass(this, SubMain.class);
        startActivity(i);
    }


    /**
     * Descripcion : Metodo que se encarga de comprobar si la app cuenta con conexion a internet.
     *              @param cxt Context Contexto de la clase MAIN.java
     *              @return boolean true cuando la app tiene conexion false cuando no dispone de conexion
     */
    private boolean getInternetState(Context cxt){
        ConnectivityManager conexion = null;
        NetworkInfo[] infoConexion = null;

        conexion = (ConnectivityManager) cxt
            .getSystemService(Context.CONNECTIVITY_SERVICE);
        if ( conexion == null )
            return false;

        infoConexion = conexion.getAllNetworkInfo();

        if ( infoConexion == null )
            return false;

        for(int i = 0; i < infoConexion.length; i++) {
            if ( infoConexion[i].getState() == NetworkInfo.State.CONNECTED )
                return true;
        }

        return false;
    }


    /**
     * Descripcion: Carga el popup que avisa de que la app no dispone de internet.
     *              @param me Activity le pasamos la referencia a this
     */
    private void showPopupInfo(final Activity context, String mensaje) {
        TextView textMensaje = null;

        // Creamos el punto donde aparecerá el popup
        if ( puntoPopups == null ){
            puntoPopups = new Point();
            puntoPopups.x = 20;
            puntoPopups.y = 120;
        }

        // Inflate the popup_layout.xml
        LinearLayout viewGroup = (LinearLayout) context.findViewById(R.id.popup_info);
        LayoutInflater layoutInflater = (LayoutInflater) context
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.popup_info, viewGroup);

        if ( layout != null ){

            // Creamos el popup
            if ( popupInfo == null ){

                // Asignamos tamaño al popup
                int popupWidth = context.getWindowManager().getDefaultDisplay().getWidth() - 40;
                int popupHeight = context.getWindowManager().getDefaultDisplay().getHeight()/4;

                popupInfo  = new PopupWindow(context);
                popupInfo.setContentView(layout);
                popupInfo.setWidth(popupWidth);
                popupInfo.setHeight(popupHeight);
                popupInfo.setFocusable(true);

                // Limpiamos el background del layout, dejandolo transparente
                popupInfo.setBackgroundDrawable(new BitmapDrawable());
            }

            textMensaje = (TextView) layout.findViewById(R.id.textView1);
            if ( textMensaje != null ){
                if ( !textMensaje.getText().toString().equals(mensaje) )
                    textMensaje.setText(mensaje);
            }
            // Mostramos el layout
            popupInfo.showAtLocation(layout, Gravity.NO_GRAVITY, puntoPopups.x, puntoPopups.y );

        }


        viewGroup = null;
        layoutInflater = null;
        layout = null;
        textMensaje = null;
    }


    private void showPopupLogin(final Activity context) {

        // Creamos el punto donde aparecerá el popup
        if ( puntoPopups == null ){
            puntoPopups = new Point();
            puntoPopups.x = 20;
            puntoPopups.y = 120;
        }

        // Inflate the popup_layout.xml
        LinearLayout viewGroup = (LinearLayout) context.findViewById(R.id.popup_login);
        LayoutInflater layoutInflater = (LayoutInflater) context
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.popup_login, viewGroup);

        if ( layout != null ){

            // Creamos el popup
            if ( popupLogin == null ){

                // Asignamos tamaño al popup
                int popupWidth = context.getWindowManager().getDefaultDisplay().getWidth() - 40;
                int popupHeight = context.getWindowManager().getDefaultDisplay().getHeight()/2;

                popupLogin = new PopupWindow(context);
                popupLogin.setContentView(layout);
                popupLogin.setWidth(popupWidth);
                popupLogin.setHeight(popupHeight);
                popupLogin.setFocusable(true);

                // Limpiamos el background del layout, dejandolo transparente
                popupLogin.setBackgroundDrawable(new BitmapDrawable());

                ((EditText) layout.findViewById(R.id.login_editext_user)).setOnFocusChangeListener(new OnFocusChangeListener(){

                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            EditText tmpEditext = null;
                            tmpEditext = (EditText) v.findViewById(R.id.login_editext_user);

                            if ( hasFocus ){
                                if ( tmpEditext.getText().toString().equals("::Escribe tu usuario") )
                                    tmpEditext.setText("");
                            }

                            tmpEditext = null;
                        }

                    });

                ((EditText) layout.findViewById(R.id.login_editext_password)).setOnFocusChangeListener(new OnFocusChangeListener(){

                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            EditText tmpEditext = null;
                            tmpEditext = (EditText) v.findViewById(R.id.login_editext_password);

                            if ( hasFocus ){
                                if ( tmpEditext.getText().toString().equals("::Escribe tu password") )
                                    tmpEditext.setText("");
                            }

                            tmpEditext = null;
                        }

                    });

                ((Button) layout.findViewById(R.id.login_btn_ingresar)).setOnClickListener(new OnClickListener(){


                        @Override
                        public void onClick(View v) {
                            String tmp_User = ((EditText) v.getRootView().findViewById(R.id.login_editext_user)).getText().toString();
                            String tmp_Password = ((EditText) v.getRootView().findViewById(R.id.login_editext_password)).getText().toString();

                            loginUser = new UserManager();
                            if ( tmp_User.length() != 0 && tmp_Password.length() != 0 ){
                                loginUser.execute(tmp_User, tmp_Password);
                                popupLogin.dismiss();
                            }

                            tmp_User = null;
                            tmp_Password = null;
                        }

                    });


            }

            // Mostramos el layout
            popupLogin.showAtLocation(layout, Gravity.NO_GRAVITY, puntoPopups.x, puntoPopups.y );

        }

        viewGroup = null;
        layoutInflater = null;
        layout = null;
    }


    /** LLamado cuando la actividad se crea por primera vez. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        me = this;

        // Asignamos los eventos a los componentes

        ((Button) this.findViewById(R.id.btn_anonimo)).setOnClickListener(new OnClickListener(){

                public void onClick( View v ){
                    /* No lo he probado en el movil, en el emulador siempre da CONECTADO */
                    if ( getInternetState(me.getApplicationContext()) ){
                        touchCallback();
                    }else
                        showPopupInfo(me,"Hey! No tengo internet. Revisa tu conexión.");
                }

            });

        ((Button) this.findViewById(R.id.btn_login)).setOnClickListener(new OnClickListener(){

                public void onClick ( View v ) {
                    if ( getInternetState(me.getApplicationContext()) )
                        showPopupLogin(me);
                    else
                        showPopupInfo(me,"Hey! No tengo internet. Revisa tu conexión.");
                }

            });

    }


    /**
     * Descripcion : Clase interna que se realiza en un hilo independiente de la UI.
     **/

    public class UserManager extends AsyncTask<String, Void, Integer>{

        /** Url del que tomar la cookie. */
        private final static String GET_NEW_COOKIE_URL = "http://www.hackxcrack.es/forum/";

        /** Url a la que se debe enviar la petición de login. */
        private final static String LOGIN_URL =
            "http://www.hackxcrack.es/forum/index.php?action=login2";

        private boolean loggedIn = false; // El usuario está logueado
        private String userName = null;   // Nick del usuario
        private String cookie = null;     // Cookie de la sesión
        ProgressBar progressBar = null;

        public UserManager(){}

        /**
         * Se ejecutará antes de doInBackground
         **/
        @Override
        protected void onPreExecute(){
            progressBar = (ProgressBar) me.findViewById(R.id.progress_login);
            progressBar.setVisibility(View.VISIBLE);
        }

        /**
         * Descripcion : Es el antiguo metodo login.
         * @param String[] args contiene los strings pasados por parametros
         * @return si devuelte 0 el login es correcto, -1 el login es incorrecto
         **/
        @Override
        protected Integer doInBackground(String...args) {
            int correctUserLogin = -1;
            this.cookie = this.getNewCookie();

            progressBar.setProgress(10);

            if (this.cookie == null) {
                return correctUserLogin;
            }

            // Se hace la petición al sistema de login
            progressBar.setProgress(20);
            HttpClient httpclient = new DefaultHttpClient();

            try {
                HttpPost request = new HttpPost(LOGIN_URL);

                request.addHeader("Cookie", this.cookie);
                request.addHeader("Content-type", "application/x-www-form-urlencoded");

                request.setEntity(new StringEntity("user=" + args[0]
                                                   + "&passwrd=" + args[1]
                                                   + "&openid_identifier=&cookielength=600&hash_password="));

                HttpResponse response = httpclient.execute(request);
                progressBar.setProgress(40);

                // Si responde redireccionando es que fue bien ^^
                Header locationHeader = response.getLastHeader("location");
                if (locationHeader != null) {
                    correctUserLogin = 0;
                    this.userName = args[0];
                    progressBar.setProgress(60);

                    // Y solo queda cojer las cookies
                    Header[] cookieHeaders = response.getHeaders("set-cookie");
                    Vector<String> cookies = new Vector<String>();
                    this.cookie = "";

                    // Y reunir los cachitos
                    progressBar.setProgress(70);
                    for (Header header: cookieHeaders) {
                        String[] slices = header.getValue().split(";");

                        if (slices.length > 0) {
                            if (! cookies.contains(slices[0])) {
                                cookies.add(slices[0]);
                                if (this.cookie != "") {
                                    this.cookie += ";";
                                }
                                this.cookie += slices[0];
                            }
                        }
                    }
                    progressBar.setProgress(90);
                } else {
                    this.cookie = null;
                }

                // Excepciones que se pueden dar, simplemente las ignoramos
            } catch (UnsupportedEncodingException encodingException) {
            } catch (IOException ioException) {

            } finally {
                // Cerramos la conexión para asegurarnos de no malgastar recursos
                httpclient.getConnectionManager().shutdown();
            }

            progressBar.setProgress(100);

            return correctUserLogin;
        }

        /**
         * Se ejecutará despues de doInBackground
         **/
        @Override
        protected void onPostExecute(Integer correctUserPass){
            progressBar.setVisibility(View.GONE);
            progressBar = null;

            if ( correctUserPass == 0 ){
                showPopupInfo(me,"Bienvenido! " + this.userName);
                touchCallback();
            }else
                showPopupInfo(me,"Error!, Usuario o contraseña incorrectos");
        }

        /** Devuelve la cookie. */
        public String getCookie(){
            return this.cookie;
        }


        /** Muestra si está logeado. */
        public boolean isLogged(){
            return this.loggedIn;
        }


        /** Devuelve el nick del usuario. */
        public String getUserName(){
            return this.userName;
        }


        /**
         * Descripción: Genera una nueva cookie.
         *
         * @return String La nueva cookie o null si no fué posible.
         * @throws IOException
         */
        private String getNewCookie(){
            String cookie = null;

            HttpClient httpclient = new DefaultHttpClient();
            try {
                HttpGet httpget = new HttpGet(GET_NEW_COOKIE_URL);

                HttpResponse response = httpclient.execute(httpget);
                Header cookieHeader = response.getLastHeader("set-cookie");
                if (cookieHeader != null) {
                    String[] tmp = cookieHeader.getValue().split(";");
                    if (tmp.length > 0){
                        cookie = tmp[0];
                    }
                }
            }catch ( IOException e ){
                /* no hacemos nada */
            }finally {
                // Cerramos la conexión para asegurarnos de no malgastar recursos
                httpclient.getConnectionManager().shutdown();
            }

            return cookie;
        }

        /**
         * Descripción: Cierra la sesión.
         *
         * @return boolean Cierto si se ha cerrado satisfactoriamente la sesión.
         * @todo Cerrar la sesión en la web.
         *
         */
        public boolean logout(){
            if (! this.loggedIn) {
                return false;
            }

            this.loggedIn = false;
            return true;
        }

    }

}
