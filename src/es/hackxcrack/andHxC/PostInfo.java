package es.hackxcrack.andHxC;

public class PostInfo {
    private String name;
    private int responseNum;
    private String id;

    PostInfo (String name, int responseNum, String id){
        this.name = name;
        this.responseNum = responseNum;
        this.id = id;
    }

    public String getName(){
        return name;
    }

    public int getResponseNum(){
        return responseNum;
    }

    public int getResponseNumber(){
        return responseNum;
    }

    public String getId(){
        return id;
    }

}
