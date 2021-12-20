package com.example.gamehousegallery;

public class UserGameDataModel {

    public String gamedata_uid;
    public String game_name;
    public String score;
    public String difficulty;
    public String date_of_score;
    public String score_rank;

    public UserGameDataModel(String gamedata_key, String game_name, String score, String difficulty, String date_scored, String score_rank){
        this.gamedata_uid=gamedata_key;
        this.game_name=game_name;
        this.score=score;
        this.difficulty=difficulty;
        this.date_of_score=date_scored;
        this.score_rank = score_rank;
    }

}
