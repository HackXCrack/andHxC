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
import android.content.SharedPreferences;
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


public class Login extends Activity{

    private PopupWindow popupInfo = null;
    private PopupWindow popupLogin = null;

    private Activity me = null;

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

                            UserManager loginUser = new ProgressBarUserManager();
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

        // Seleccionar tema
        SharedPreferences sp = getApplication().getSharedPreferences("global", 0);
        int themeId = 0;
        if (sp.contains("themeId")){
            themeId = sp.getInt("themeId", 0);
        }

        if (themeId != 0){
            setTheme(themeId);
        }



        setContentView(R.layout.login);
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
    public class ProgressBarUserManager extends UserManager{

        ProgressBar progressBar = null;

        /**
         * Se ejecutará antes de doInBackground
         **/
        @Override
        protected void onPreExecute(){
            progressBar = (ProgressBar) me.findViewById(R.id.progress_login);
            progressBar.setVisibility(View.VISIBLE);
        }

        /**
         * Se ejecutará cuando haya una actualización sobre el estado de la tarea.
         *
         */
        @Override
        protected void onProgressUpdate(Integer... progress) {
            progressBar.setProgress(progress[0]);
        }


        /**
         * Se ejecutará despues de doInBackground
         **/
        @Override
        protected void onPostExecute(Boolean correctUserPass){
            progressBar.setVisibility(View.GONE);
            progressBar = null;

            if (correctUserPass){
                ForumManager.setSessionCookie(this.getCookie());
                touchCallback();
            }
            else {
                showPopupInfo(me, "Error!, Usuario o contraseña incorrectos");
            }
        }
    }

}
