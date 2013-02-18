package es.hackxcrack.andHxC;

public class PostInfo {
    private String name;
    private Integer responseNum;
    private int id;
    private String author;
    private String forum;
    private boolean subforum;

    PostInfo (String name, Integer responseNum, int id,
              String author, String forum, boolean subforum){

        this.name = name;
        this.responseNum = responseNum;
        this.id = id;
        this.author = author;
        this.forum = forum;
        this.subforum = subforum;
    }

    public String getName(){
        return name;
    }

    public Integer getResponseNumber(){
        return responseNum;
    }

    public int getId(){
        return id;
    }

    public String getAuthor(){
        return author;
    }

    public String getForum(){
        return forum;
    }

    public boolean isSubforum(){
        return subforum;
    }
}
