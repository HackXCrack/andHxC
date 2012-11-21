package es.hackxcrack.andHxC;

import java.util.Vector;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.Header;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.entity.StringEntity;


/**
 * Gestiona la sesión del usuario, implementa la toma de cookie, el login y el logout.
 */
public class UserManager {

    /** Url del que tomar la cookie. */
    private final static String GET_NEW_COOKIE_URL = "http://www.hackxcrack.es/forum/";

    /** Url a la que se debe enviar la petición de login. */
    private final static String LOGIN_URL =
        "http://www.hackxcrack.es/forum/index.php?action=login2";



    private boolean loggedIn = false; // El usuario está logueado
    private String userName = null;   // Nick del usuario
    private String cookie = null;     // Cookie de la sesión


    public UserManager(){}


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
    private static String getNewCookie() throws IOException{
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
        } finally {
            // Cerramos la conexión para asegurarnos de no malgastar recursos
            httpclient.getConnectionManager().shutdown();
        }

        return cookie;
    }


    /**
     * Descripción: Hace login con un usuario y una password concretos.
     *
     * @param username String El nombre del usuario.
     * @param password String La contraseña del usuario.
     *
     * @return boolean True si el login ha sido exitoso.
     *
     */
    public boolean login(String username, String password){
        // Se hace necesaria una cookie
        //  ...o quizá no, no lo probé :P
        try {
            this.cookie = this.getNewCookie();
        } catch(IOException e) {
            return false;
        }

        if (this.cookie == null) {
            return false;
        }


        // Se hace la petición al sistema de login
        boolean correctUserPass = false;
        HttpClient httpclient = new DefaultHttpClient();
        try {
            HttpPost request = new HttpPost(LOGIN_URL);

            request.addHeader("Cookie", this.cookie);
            request.addHeader("Content-type", "application/x-www-form-urlencoded");

            request.setEntity(new StringEntity("user=" + username
                                               + "&passwrd=" + password
                                               + "&openid_identifier=&cookielength=600&hash_password="));

            HttpResponse response = httpclient.execute(request);

            // Si responde redireccionando es que fue bien ^^
            Header locationHeader = response.getLastHeader("location");
            if (locationHeader != null) {
                correctUserPass = true;
                this.userName = username;

                // Y solo queda cojer las cookies
                Header[] cookieHeaders = response.getHeaders("set-cookie");
                Vector<String> cookies = new Vector<String>();
                this.cookie = "";

                // Y reunir los cachitos
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

        return correctUserPass;
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
