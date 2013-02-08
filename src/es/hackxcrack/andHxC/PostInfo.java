package es.hackxcrack.andHxC;

public class PostInfo {
    private String name;
    private Integer responseNum;
    private int id;
    private String author;
    private boolean subforum;

    PostInfo (String name, Integer responseNum, int id, String author, boolean subforum){
        this.name = name;
        this.responseNum = responseNum;
        this.id = id;
        this.author = author;
        this.subforum = subforum;
    }

    public String getName(){
        return name;
    }

    public Integer getResponseNumber(){
        return responseNum;
    }

    public String getAuthor(){
        return author;
    }

    public int getId(){
        return id;
    }

    public boolean isSubforum(){
        return subforum;
    }
}
