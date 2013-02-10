package es.hackxcrack.andHxC;

import android.text.Spanned;

public class MessageInfo {

    private String author;
    private Spanned message;

    MessageInfo (String author, Spanned message){
        this.author = author;
        this.message = message;
    }

    public String getAuthor(){
        return author;
    }

    public Spanned getMessage(){
        return message;
    }
}
