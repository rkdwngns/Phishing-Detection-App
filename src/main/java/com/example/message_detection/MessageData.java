package com.example.message_detection;

public class MessageData {
    private String title;
    private String text;

    private String category;

    private int type;

    private String Package;
    public MessageData(String nbr, String text,String category , int type , String Package){
        this.title = nbr;
        this.text = text;
        this.category = category;
        this.type = type;
        this.Package = Package;
    }
    public MessageData(String nbr, String text , int type , String Package){
        this.title = nbr;
        this.text = text;
        this.type = type;
        this.Package = Package;
    }



    public String getTitle() {
        return title;
    }

    public String getText() {
        return text;
    }

    public String getCategory() {
        return category;
    }
    public int getType() {return type;}

    public String getPackage(){return Package;}

}
