package com.example.gamehousegallery;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;

public class ScoreData {

    public Object date_of_score;
    public String difficulty;
    public String score_rank;
    public String score;

    public ScoreData(String score, String difficulty) {

        this.difficulty = difficulty;
        this.score = score;
        this.score_rank="unranked";
        this.date_of_score = ServerValue.TIMESTAMP;
    }

    public class UserGameData {

        public String gamedata_uid;
        public String game_name;
        public String score;
        public String difficulty;
        public String date_of_score;

        public UserGameData(String gamedata_key, String game_name, String score, String difficulty, String date_scored) {
            this.gamedata_uid = gamedata_key;
            this.game_name = game_name;
            this.score = score;
            this.difficulty = difficulty;
            this.date_of_score = date_scored;
        }

    }
}
