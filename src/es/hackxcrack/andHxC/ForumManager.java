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
import android.text.Html;
import android.text.Spanned;
import android.text.Editable;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.xml.sax.Attributes;
import org.xml.sax.XMLReader;
import org.xml.sax.SAXException;

public class ForumManager {

    private static String sessionCookie = null;

    private final static String MAIN_FORUM = "http://www.hackxcrack.es/forum/?theme=6&";

    /**
     * Maneja los tags de imágenes reemplazandolos por el title o el alt (en este orden).
     *
     */
    private static Html.TagHandler customTagHandler = new Html.TagHandler() {
            private void handleImg(XMLReader xmlReader, Editable output) {
                // Elimina
                output.delete(output.length() - 1, output.length());
                String alt = "";
                String title = "";
                try{
                    alt = xmlReader.getProperty("alt").toString();
                } catch(SAXException saxe) {}
                try{
                    title = xmlReader.getProperty("title").toString();
                } catch(SAXException saxe) {}

                if (!title.equals("")){
                    output.append(" "+ title +" ");
                }
                else if (!alt.equals("")){
                    output.append(" "+ alt +" ");
                }

            }


            @Override
            public void handleTag(boolean opening, String tag,
                                  Editable output, XMLReader xmlReader) {

                if (tag.equalsIgnoreCase("img")) {
                    handleImg(xmlReader, output);
                }
            }
        };

    /**
     * Esto busca en la página principal y saca los foros y las ID.
     *  Forma tres grupos, url, id y nombre.
     *
     */
    private final static Pattern FORUM_REGEX = Pattern.compile("<a class=\"subject\" href=\"(http://(www\\.)?hackxcrack.es/forum/index.php[?]board=\\d+).0\" name=\"b(\\d+)\">([^<]+)</a>");

    /**
     * Descripción: Busca las ID del tablón en una URL.
     *
     */
    private final static Pattern BOARD_ID_MATCHER = Pattern.compile(
        "(?:\\?|&)board=(\\d+)(\\.\\d+)?");


    /**
     * Descripción: Busca las ID del tema en una URL.
     *
     */
    private final static Pattern TOPIC_ID_MATCHER = Pattern.compile(
        "(?:\\?|&)topic=(\\d+)(\\.\\d+)?");



    /**
     * Descripción: Marca una cookie para usar en la sesión.
     *  En caso de ser `null' no se utilizará.
     *
     * @param cookie String Valor de la cookie a usar.
     *
     */
    public static void setSessionCookie(String cookie){
        Log.d("andHxC", "SessionCookie: " + sessionCookie + " -> " + cookie);
        sessionCookie = cookie;
    }


    /**
     * Descripción: Devuelve la cookie marcada como la de la sesión.
     *  Se considerará borrada en caso de ser `null'.
     *
     * @return La cookie de sesión.
     */
    public static String getSessionCookie(){
        return sessionCookie;
    }


    /**
     * Descripción: Lee los datos en una url. Usa una cookie si se especifica.
     *
     * @param url String La dirección de donde leer los datos.
     *
     * @return String Los datos que devuelve la URL.
     * @throws IOException
     *
     */
    public static String fetchUrl(String url) throws IOException{
        String result = null;

        HttpClient httpclient = new DefaultHttpClient();
        try {
            HttpGet request = new HttpGet(url);
            if (sessionCookie != null){
                request.addHeader("Cookie", sessionCookie);
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
            System.out.println(fetchUrl(MAIN_FORUM));
        } catch (IOException ioException) {}

        return null;
    }


    /**
     * Descripción: Añade a la lista los subforos de una categoría.
     *
     */
    public static void getSubforumsFromCategory(TagNode doc, List<PostInfo> subforumList){
        try{
            Object[] subforums = doc.evaluateXPath("//table[@class=\"table_list\"]/tbody/tr");
            for (int i = 0;i < subforums.length; i++){
                TagNode subforum = (TagNode) subforums[i];

                // Búsqueda del nombre/ID
                Object[] subjects = subforum.evaluateXPath("//a[@class=\"subject\"]");
                if (subjects.length != 1){
                    Log.e("andHxC", "Error parsing subforum link");
                    continue;
                }
                TagNode subject = (TagNode)subjects[0];

                Matcher idMatch = BOARD_ID_MATCHER.matcher(subject.getAttributeByName("href"));
                if (!idMatch.find()){
                    Log.e("andHxC", "Board ID not found on url “" + subject.getAttributeByName("href") + "”");
                    continue;
                }

                int id = Integer.parseInt(idMatch.group(1));
                String name = subject.getText().toString();

                // Búsqueda del número de respuestas
                if (subforum.getChildTags().length != 4){
                    Log.e("andHxC", "Parse error looking for subforum response number, found " +
                          subforum.getChildTags().length + " child tags, expected 4");
                    continue;
                }

                TagNode responseNode = subforum.getChildTags()[2];

                // Toma la fila de respuestas, haz trim() y toma de la segunda linea la primera columna
                String[] lines = responseNode.getText().toString().trim().split("\n");
                if (lines.length != 2){
                     // Probablemente sea una redirección, así que ya no se loguea
                    continue;
                }
                String sResponseNum = lines[1].trim().split(" ")[0];
                int responseNum = Integer.parseInt(sResponseNum);

                subforumList.add(new PostInfo(name, responseNum, id, null, null, true));
            }
        }
        catch(XPatherException xpe){
            Log.e("andHxC", xpe.toString());
        }
    }


    /**
     * Descripción: Añade a la lista los posts de una categoría.
     *
     */
    public static void getPostsFromCategory(TagNode doc, List<PostInfo> postList){
        Object[] threads = null;
        try{
            // Localización de la información
             threads = doc.evaluateXPath("//div[@id=\"messageindex\"]/table/tbody/tr");
        }
        catch(XPatherException xpe){
            Log.e("andHxC", xpe.toString());
        }
        if (threads != null){
            for (int i = 0;i < threads.length; i++){
                TagNode thread = (TagNode) threads[i];

                // El evaluador de XPath no soporta not :\
                if (thread.hasAttribute("class")){
                    continue;
                }

                List<TagNode> titleList = thread.getElementListByAttValue("class", "subject_title", true, true);

                // Extracción del título/ID
                if((titleList.size() != 1) || (titleList.get(0).getChildTags().length != 1)){
                    Log.e("andHxC", "Parsing error looking for title");
                    continue;
                }


                TagNode title = titleList.get(0);
                String name = title.getText().toString();
                TagNode link = title.getChildTags()[0];

                Matcher match = TOPIC_ID_MATCHER.matcher(link.getAttributeByName("href"));
                if (!match.find()){
                    Log.e("andHxC", "Topic ID not found on url “" + link.getAttributeByName("href") + "”");
                    continue;
                }

                int id = Integer.parseInt(match.group(1));

                // Extracción de la autoría
                String author;
                Object[] authorLink = null;
                try {
                    authorLink = thread.evaluateXPath("//p/a");
                }
                catch(XPatherException xpe){
                    Log.e("andHxC", xpe.toString());
                    continue;
                }
                if (authorLink.length != 1){
                    // Si no se encuentra el enlace al autor se busca de otra forma
                    try {
                        authorLink = thread.evaluateXPath("//p");
                    }
                    catch(XPatherException xpe){
                        Log.e("andHxC", xpe.toString());
                        continue;
                    }
                    if (authorLink.length != 1){
                        Log.e("andHxC", "Parse error looking for topic author");
                        continue;
                    }

                    TagNode authorTag = (TagNode) authorLink[0];
                    author = authorTag.getText().toString();
                    String[] authorSlices = author.split("\n");
                    author = authorSlices[0].substring(13); // len("Iniciado por ")
                }
                else{
                    TagNode authorTag = (TagNode) authorLink[0];
                    author = authorTag.getText().toString();
                }

                // Extracción del número de respuestas
                if (thread.getChildTags().length != 4){
                    Log.e("andHxC", "Parse error looking for response number, found " +
                          thread.getChildTags().length + " child tags, expected 4");
                    continue;
                }

                TagNode responseNode = thread.getChildTags()[2];
                int responseNum = Integer.parseInt(responseNode.getText().toString().trim().split(" ")[0]);

                postList.add(new PostInfo(name, responseNum, id, author, null, false));
            }
        }
    }


    /**
     * Descripción: Devuelve la lista de posts de una categoría.
     *
     * @return  List<PostInfo>
     */
    public static List<PostInfo> getItemsFromCategory(int categoryId, int page){
        String url = MAIN_FORUM + "board=" + categoryId + "." + page * 25;
        Log.d("andHxC", url);

        List <PostInfo> postList = new ArrayList<PostInfo>();
        String data;
        try {
            data = fetchUrl(url);
        } catch (IOException ioException) {
            Log.e("andHxC getPostsFromCategory", ioException.toString());
            return null;
        }

        HtmlCleaner cleaner = new HtmlCleaner();
        TagNode doc = cleaner.clean(data);

        // Búsqueda de subforos
        if (page == 0){
            getSubforumsFromCategory(doc, postList);
        }


        // Búsqueda de hilos
        getPostsFromCategory(doc, postList);

        return postList;
   }


    /**
     * Descripción: Parsea el Mensaje a partir de un nodo.
     *
     * @return  MessageInfo
     */
    public static MessageInfo getMessageFromNode(HtmlCleaner cleaner, TagNode node){
        Object[] authorNodes = null;
        try{
            authorNodes = node.evaluateXPath("//div[@class=\"poster\"]/h4/a");
        }
        catch(XPatherException xpe){
            Log.e("andHxC", xpe.toString());
            return null;
        }

        if (authorNodes.length != 1){
            Log.e("andHxC", "Error retrieving author from message");
            return null;
        }

        TagNode authorNode = (TagNode) authorNodes[0];
        String author = authorNode.getText().toString();


        Object[] textNodes = null;
        try{
            textNodes = node.evaluateXPath("//div[@class=\"post\"]/div[@class=\"inner\"]");
        }
        catch(XPatherException xpe){
            Log.e("andHxC", xpe.toString());
            return null;
        }
        if (textNodes.length != 1){
            Log.e("andHxC", "Error retrieving text from message");
            return null;
        }

        TagNode textNode = (TagNode) textNodes[0];
        Spanned text = Html.fromHtml(cleaner.getInnerHtml(textNode), null, customTagHandler);

        return new MessageInfo(author, text);
    }


    /**
     * Descripción: Devuelve la lista de mensajes de un hilo.
     *
     * @return  List<MessageInfo>
     */
    public static List<MessageInfo> getItemsFromThread(int threadId, int page){
        List<MessageInfo> msgList = new ArrayList<MessageInfo>();
        String url = MAIN_FORUM + "topic=" + threadId + "." + page * 10;

        String data;
        try {
            data = fetchUrl(url);
        } catch (IOException ioException) {
            Log.e("andHxC getItemsFromThread", ioException.toString());
            return null;
        }

        HtmlCleaner cleaner = new HtmlCleaner();
        TagNode doc = cleaner.clean(data);

        Object[] messages = null;
        try{
            messages = doc.evaluateXPath("//div[@class=\"post_wrapper\"]");
        }
        catch(XPatherException xpe){
            Log.e("andHxC", xpe.toString());
        }
        if (messages != null){
            for(int i = 0; i < messages.length; i++){
                TagNode messageNode = (TagNode) messages[i];
                MessageInfo message = getMessageFromNode(cleaner, messageNode);
                if (message != null){
                    msgList.add(message);
                }
            }
        }

        return msgList;
    }


    /**
     * Descripción: Devuelve la lista de mensajes privados.
     *
     * @return  List<MessageInfo>
     */
    public static List<MessageInfo> getPrivateMessages(int page){
        List<MessageInfo> msgList = new ArrayList<MessageInfo>();
        String url = MAIN_FORUM + "action=pm;f=inbox;sort=date;desc;start=" + (page * 15);

        String data;

        try {
            data = fetchUrl(url);
        } catch (IOException ioException) {
            Log.e("andHxC getPrivateMessages", ioException.toString());
            return null;
        }
        HtmlCleaner cleaner = new HtmlCleaner();
        TagNode doc = cleaner.clean(data);

        Object[] messages = null;
        try{
            messages = doc.evaluateXPath("//div[@id=\"personal_messages\"]/form[@name=\"pmFolder\"]/div");
        }
        catch(XPatherException xpe){
            Log.e("andHxC", xpe.toString());
        }
        if (messages != null){
            for(int i = 0; i < messages.length; i++){
                TagNode messageNode = (TagNode) messages[i];

                if (messageNode.getAttributes().get("class").endsWith(" clear")){
                    MessageInfo message = getMessageFromNode(cleaner, messageNode);
                    if (message != null){
                        msgList.add(message);
                    }
                }
            }
        }

        return msgList;
    }


    /**
     * Descripción: Parsea el Post a partir de un nodo.
     *
     * @return  PostInfo
     */
    public static PostInfo getPostFromNode(Element element){
        String author = null;

        // Título del post
        NodeList titleNodes = element.getElementsByTagName("title");
        if (titleNodes.getLength() != 1){
            Log.e("andHxC", "Error retrieving name from post");
            return null;
        }

        Element titleNode = (Element) titleNodes.item(0);
        NodeList titleChilds = titleNode.getChildNodes();
        if (titleChilds.getLength() != 1){
            Log.e("andHxC", "Error retrieving name from post (no title childs)");
            return null;
        }

        String title = titleChilds.item(0).getNodeValue();


        // Id del post
        NodeList linkNodes = element.getElementsByTagName("link");
        if (linkNodes.getLength() != 1){
            Log.e("andHxC", "Error retrieving id from post");
            return null;
        }

        Element linkNode = (Element) linkNodes.item(0);
        NodeList linkChilds = linkNode.getChildNodes();
        if (linkChilds.getLength() != 1){
            Log.e("andHxC", "Error retrieving id from post (no childs)");
            return null;
        }

        Matcher match = TOPIC_ID_MATCHER.matcher(linkChilds.item(0).getNodeValue());

        if (!match.find()){
            Log.e("andHxC", "Error looking for topic id");
            return null;
        }

        int id = Integer.parseInt(match.group(1));

        // Categoría del post
        NodeList categoryNodes = element.getElementsByTagName("category");
        if (categoryNodes.getLength() != 1){
            Log.e("andHxC", "Error retrieving category from post");
            return null;
        }

        Element categoryNode = (Element) categoryNodes.item(0);
        NodeList categoryChilds = categoryNode.getChildNodes();
        if (linkChilds.getLength() != 1){
            Log.e("andHxC", "Error retrieving id from post (no childs)");
            return null;
        }
        String forum = categoryChilds.item(0).getNodeValue();

        return new PostInfo(title, null, id, author, forum, false);
    }


    /**
     * Descripción: Devuelve la lista de novedades.
     *
     * @return  List<PostInfo>
     */
    public static List<PostInfo> getNews(){
        String url = MAIN_FORUM + "type=rss;action=.xml;sa=news;limit=20";
        List<PostInfo> postList = new ArrayList<PostInfo>();

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        DocumentBuilder db = null;
        try{
            db = dbf.newDocumentBuilder();
        }
        catch(ParserConfigurationException pce){
            Log.e("andHxC", pce.toString());
            return null;
        }

        Document doc = null;
        try{
            doc = db.parse(url);
        }
        catch(SAXException saxe){
            Log.e("andHxC", saxe.toString());
            return null;
        }
        catch(IOException ioe){
            Log.e("andHxC", ioe.toString());
            return null;
        }


        NodeList nodeList = doc.getElementsByTagName("item");
        for(int i = 0; i < nodeList.getLength(); i++){
            Node postNode = nodeList.item(i);
            if (postNode.getNodeType() == Node.ELEMENT_NODE){
                PostInfo post = getPostFromNode((Element) postNode);
                if (post != null){
                    postList.add(post);
                }
            }
        }

        return postList;
    }
}
