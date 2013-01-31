package es.hackxcrack.andHxC;

public class PostInfo {
    private String name;
    private int responseNum;
    private String id;
    private String author;

    PostInfo (String name, int responseNum, String id, String author){
        this.name = name;
        this.responseNum = responseNum;
        this.id = id;
        this.author = author;
    }

    public String getName(){
        return name;
    }

    public int getResponseNumber(){
        return responseNum;
    }

    public String getAuthor(){
        return author;
    }

    public String getId(){
        return id;
    }

}
