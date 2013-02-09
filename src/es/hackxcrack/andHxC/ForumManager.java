package es.hackxcrack.andHxC;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import org.htmlcleaner.*;

import android.util.Log;

public class ForumManager {

    private final static String MAIN_FORUM = "http://www.hackxcrack.es/forum/?";

    /**
     * Esto busca en la página principal y saca los foros y las ID.
     *  Forma tres grupos, url, id y nombre.
     *
     */
    private final static Pattern FORUM_REGEX = Pattern.compile("<a class=\"subject\" href=\"(http://(www\\.)?hackxcrack.es/forum/index.php[?]board=\\d+).0\" name=\"b(\\d+)\">([^<]+)</a>");

    /**
     * Esto busca en una página de una categoría y saca el nombre, ID y autor de los post.
     *
     * @TODO cambiarlo por un parser SGML, esto no es bueno para la cordura de nadie.
     */
    private final static Pattern POST_REGEX = Pattern.compile(
        "\\s*<td class=\"subject[^\"]*\">" +
          "\\s*<div\\s*>" +
            "\\s*(?:<strong>\\s*)?<span class=\"subject_title\" id=\"msg_\\d+\">" +
              "<a href=\"http://www.hackxcrack.es/forum/index.php\\?topic=(\\d+).0\">([^<]*)</a>" +
            "</span>(?:\\s*</strong>)?" +
            "\\s*<p>[^<]*<a[^>]*>([^<]*)</a>\\s*<small id=\"[^\"]*\">" +
             "(?:[^<]*<a[^>]*>\\d*</a>[^<]*)*" +
            "</small>\\s*</p>" +
          "\\s*</div>" +
        "\\s*</td>" +
        "\\s*<td class=\"stats[^\"]*\">" +
          "\\s*(\\d*) Respuestas?");


    /**
     * Descripción: Busca las ID del tablón en una URL.
     *
     */
    private final static Pattern BOARD_ID_MATCHER = Pattern.compile(
        "(?:\\?|&)board=(\\d+)(\\.\\d+)");


    /**
     * Descripción: Lee los datos en una url. Usa una cookie si se especifica.
     *
     * @param url String La dirección de donde leer los datos.
     * @param cookie String La cookie que usar al acceder a la dirección.
     *
     * @return String Los datos que devuelve la URL.
     * @throws IOException
     *
     */
    public static String fetchUrl(String url, String cookie) throws IOException{
        String result = null;

        HttpClient httpclient = new DefaultHttpClient();
        try {
            HttpGet request = new HttpGet(url);
            if (cookie != null){
                request.addHeader("Cookie", cookie);
            }

            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            result = httpclient.execute(request, responseHandler);

        } finally {
            // Cerramos la conexión para asegurarnos de no malgastar recursos
            httpclient.getConnectionManager().shutdown();
        }

        return result;
    }


    /**
     * Descripcion: Devuelve la lista de foros como una tupla de (nombre, id).
     *
     * @return List<Pair<String, String>>
     *
     * @note No implementado, por ahora se usará la lista estática.
     */
    public static List<PostInfo> getForumList(){

        try {
            System.out.println(fetchUrl(MAIN_FORUM, null));
        } catch (IOException ioException) {}

        return null;
    }

    /**
     * Descripción: Devuelve la lista de posts de una categoría como una tupla de (nombre, id).
     *
     * @return  List<PostInfo>
     */
    public static List<PostInfo> getItemsFromCategory(int categoryId, int page){
        String url = MAIN_FORUM + "board=" + categoryId + "." + page * 10;

        List <PostInfo> postList = new ArrayList<PostInfo>();
        String data;
        try {
            data = fetchUrl(url, null);
        } catch (IOException ioException) {
            //Log.e("andHxC getPostsFromCategory", ioException + "");
            return null;
        }

        HtmlCleaner cleaner = new HtmlCleaner();
        TagNode doc = cleaner.clean(data);

        try{
            Object[] subjects = doc.evaluateXPath("//a[@class=\"subject\"]");
            for (int i = 0;i < subjects.length; i++){
                TagNode subject = (TagNode) subjects[i];
                Matcher idMatch = BOARD_ID_MATCHER.matcher(subject.getAttributeByName("href"));
                if (idMatch.find()){

                    int id = Integer.parseInt(idMatch.group(1));
                    String name = subject.getText().toString();

                    postList.add(new PostInfo(name, null, id, null, true));
                }
                else{
                    Log.e("andHxC", "Board ID not found on url “" + subject.getAttributeByName("href") + "”");
                }
            }
        }
        catch(XPatherException xpe){
            Log.e("andHxC", xpe.toString());
        }


        Matcher match = POST_REGEX.matcher(data);

        while (match.find()){
            int id = Integer.parseInt(match.group(1));
            String name = match.group(2);
            String author = match.group(3);
            int responseNum = Integer.parseInt(match.group(4));

            postList.add(new PostInfo(name, responseNum, id, author, false));
        }

        return postList;
   }

}
