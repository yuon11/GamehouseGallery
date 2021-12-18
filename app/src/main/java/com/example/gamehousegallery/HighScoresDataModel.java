package com.example.gamehousegallery;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HighScoresDataModel {

    public String user_uid;
    public String highscores_uid;

    public HashMap<String,DatabaseReference> game_name_to_highscore_gamedata;

    private HashMap<String,UserGameData> matchmaker_highscore_data;
    private HashMap<String,UserGameData> pureluck_highscore_data;
    private HashMap<String,UserGameData> quizbowl_highscore_data;

    public HighScoresDataModel(String highscore_data_uid, String user_uid){

        this.highscores_uid=highscore_data_uid;
        this.user_uid=user_uid;

        this.matchmaker_highscore_data= new HashMap<>();
        this.pureluck_highscore_data= new HashMap<>();
        this.quizbowl_highscore_data= new HashMap<>();
        this.game_name_to_highscore_gamedata= new HashMap<>();

    }

    public class UserGameData{

        public String gamedata_uid;
        public String game_name;
        public String score;
        public String difficulty;
        public String date_of_score;

        public UserGameData(String gamedata_key, String game_name, String score, String difficulty, String date_scored){
            this.gamedata_uid=gamedata_key;
            this.game_name=game_name;
            this.score=score;
            this.difficulty=difficulty;
            this.date_of_score=date_scored;
        }

    }
    public void setGameScoreData(String game_name, HashMap<String, UserGameData> dataInstance){
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
    public HashMap<String,UserGameData> getGameScoreData(String game_name){

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
            HashMap<String,UserGameData> emptyResult = new HashMap<>();
            return emptyResult;
        }
    }
}