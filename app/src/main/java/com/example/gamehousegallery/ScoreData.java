package com.example.gamehousegallery;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;

public class ScoreData {

    public Object date_of_score;
    public String difficulty;
    public String score_rank;
    public String score;
    public HashMap<String,String> score_details;

    public ScoreData(String score, String difficulty) {

//        String game_name, String score, String difficulty, String date_scored, String score_rank)
        this.difficulty = difficulty;
        this.score = score;
        this.score_rank="unranked";
        this.date_of_score = ServerValue.TIMESTAMP;
        this.score_details=new HashMap<>();
    }
}
