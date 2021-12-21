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

    public HashMap<String, HashMap<String, UserGameDataModel>> game_name_to_score_map;

    public HighScoresDataModel(String highscore_data_uid, String user_uid){

        this.highscores_uid=highscore_data_uid;
        this.user_uid=user_uid;

        this.matchmaker_highscore_data= new HashMap<>();
        this.pureluck_highscore_data= new HashMap<>();
        this.quizbowl_highscore_data= new HashMap<>();
        this.game_name_to_highscore_gamedata= new HashMap<>();
        this.game_name_to_score_map= new HashMap<>();

        game_name_to_score_map.put("matchmaker",matchmaker_highscore_data);
        game_name_to_score_map.put("pureluck",pureluck_highscore_data);
        game_name_to_score_map.put("quizbowl!",quizbowl_highscore_data);
    }

    public void setGameScoreData(String game_name, UserGameDataModel dataInstance){
        game_name_to_score_map.get(game_name.toLowerCase())
                .put(dataInstance.gamedata_uid, dataInstance);
    }

    public HashMap<String, UserGameDataModel> getGameScoreData(String game_name){

        if(game_name_to_score_map.containsKey(game_name))
            return game_name_to_score_map.get(game_name);
        else{
            HashMap<String, UserGameDataModel> new_game_data=new HashMap<>();
            game_name_to_score_map.put(game_name,new_game_data);
            return new_game_data;
        }
    }
}