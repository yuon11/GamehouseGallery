package com.example.gamehousegallery;

public class GameDataModel {

    public String uid;
    public String description;
    public String game_image;
    public String game_name;

    public GameDataModel(String uid, String name, String image, String description){
//        if(image=="" || image==null)
//            this.image="default_poster.jpg";
//        else
        this.uid=uid;
        this.game_image=image;
        this.game_name = name;
        this.description=description;
    }
}