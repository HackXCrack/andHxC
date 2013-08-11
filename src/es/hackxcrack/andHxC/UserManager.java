package es.hackxcrack.andHxC;

import java.util.List;
import java.util.Vector;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.Header;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;

import android.os.AsyncTask;
import android.util.Log;


/**
 * Gestiona la sesión del usuario, implementa la toma de cookie, el login y el logout.
 */
public class UserManager extends AsyncTask<String, Integer, Boolean>{

    /** Url a la que se debe enviar la petición de login. */
    private final static String LOGIN_URL =
        "http://www.hackxcrack.es/forum/index.php?action=login2";

    /** Url a la que redirige en caso de un login correcto. */
    private final static String SUCCESSFULL_LOGIN_URI = "/forum/index.php";


    private boolean loggedIn = false; // El usuario está logueado
    private String userName = null;   // Nick del usuario
    private static String sessionCookie = null;     // Cookie de la sesión


    public UserManager(){}

    @Override
    protected Boolean doInBackground(String...args) {
        return login(args[0], args[1]);
    }


    /** Devuelve la cookie. */
    public String getCookie(){
        return sessionCookie;
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
     * Descripción: Hace login con un usuario y una password concretos.
     *
     * @param username String El nombre del usuario.
     * @param password String La contraseña del usuario.
     *
     * @return boolean True si el login ha sido exitoso.
     *
     */
    public boolean login(String username, String password){
        publishProgress(10);

        // Se hace la petición al sistema de login

        boolean correctUserPass = false;
        DefaultHttpClient httpclient = new DefaultHttpClient();
        HttpContext localContext = new BasicHttpContext();
        CookieStore cookieStore = new BasicCookieStore();
        httpclient.setCookieStore(cookieStore);
        try {
            HttpPost request = new HttpPost(LOGIN_URL);

            request.addHeader("Content-type", "application/x-www-form-urlencoded");

            request.setEntity(new StringEntity("user=" + username
                                               + "&passwrd=" + password
                                               + "&openid_identifier=&cookieneverexp=on&hash_passwrd="));

            HttpResponse response = httpclient.execute(request, localContext);
            HttpUriRequest uriRequest = (HttpUriRequest)
                localContext.getAttribute(ExecutionContext.HTTP_REQUEST);

            publishProgress(30);

            String uri = uriRequest.getURI().toString();

            // Si responde redireccionando es que fue bien ^^
            if (uri.equals(SUCCESSFULL_LOGIN_URI)) {
                correctUserPass = true;
                this.userName = username;

                // Y solo queda cojer las cookies y reunir los cachitos
                publishProgress(50);

                String cookieString = null;
                List<Cookie> cookies = cookieStore.getCookies();
                for (Cookie cookie: cookies){

                    // Hay que intercalar ';' entre las cookies
                    if (cookieString == null){
                        cookieString = "";
                    } else {
                        cookieString += ";";
                    }
                    cookieString += cookie.getName() + "=" + cookie.getValue();
                }
                this.sessionCookie = cookieString;
                publishProgress(80);
            } else {
                this.sessionCookie = null;
            }

            // Excepciones que se pueden dar, simplemente las ignoramos
        } catch (UnsupportedEncodingException encodingException) {
        } catch (IOException ioException) {
        } finally {
            // Cerramos la conexión para asegurarnos de no malgastar recursos
            httpclient.getConnectionManager().shutdown();
        }

        publishProgress(100);
        return correctUserPass && (!this.sessionCookie.equals(""));
    }


    /**
     * Descripción: Cierra la sesión.
     *
     * @return boolean Cierto si se ha cerrado satisfactoriamente la sesión.
     * @todo Cerrar la sesión en la web.
     *
     */
    public boolean logout(){
        if (!this.loggedIn) {
            return false;
        }

        this.loggedIn = false;
        return true;
    }

}
