package com.example.gamehousegallery;

import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;

public class HighScoresDataModel {

    public String user_uid;
    public String highscores_uid;

    public HashMap<String,DatabaseReference> game_name_to_highscore_gamedata;

    public HashMap<String, UserGameDataModel> matchmaker_highscore_data;
    public HashMap<String, UserGameDataModel> pureluck_highscore_data;
    public HashMap<String, UserGameDataModel> quizbowl_highscore_data;

    public HighScoresDataModel(String highscore_data_uid, String user_uid){

        this.highscores_uid=highscore_data_uid;
        this.user_uid=user_uid;

        this.matchmaker_highscore_data= new HashMap<>();
        this.pureluck_highscore_data= new HashMap<>();
        this.quizbowl_highscore_data= new HashMap<>();
        this.game_name_to_highscore_gamedata= new HashMap<>();

    }

    public void setGameScoreData(String game_name, HashMap<String, UserGameDataModel> dataInstance){
        if (game_name.toLowerCase().equals("matchmaker")){
            matchmaker_highscore_data=dataInstance;
        }
        else if (game_name.toLowerCase().equals("pureluck")){
            pureluck_highscore_data=dataInstance;
        }
        else if (game_name.toLowerCase().equals("quizbowl!")){
            quizbowl_highscore_data=dataInstance;
        }
    }

    public void addGameScoreData(String game_name, UserGameDataModel dataInstance){

        if (game_name.toLowerCase().equals("matchmaker")){
            matchmaker_highscore_data.put(dataInstance.gamedata_uid, dataInstance);
        }
        else if (game_name.toLowerCase().equals("pureluck")){
            pureluck_highscore_data.put(dataInstance.gamedata_uid, dataInstance);
        }
        else if (game_name.toLowerCase().equals("quizbowl!")){
            quizbowl_highscore_data.put(dataInstance.gamedata_uid, dataInstance);
        }

    }

    public HashMap<String, UserGameDataModel> getGameScoreData(String game_name){

        if (game_name.toLowerCase().equals("matchmaker")){
            return matchmaker_highscore_data;
        }
        else if (game_name.toLowerCase().equals("pureluck")){
            return pureluck_highscore_data;
        }
        else if (game_name.toLowerCase().equals("quizbowl!")){
            return quizbowl_highscore_data;
        }
        else {
            HashMap<String, UserGameDataModel> emptyResult = new HashMap<>();
            return emptyResult;
        }
    }
}