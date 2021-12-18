package com.example.gamehousegallery;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;


import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeHighScoreRecyclerAdapter extends RecyclerView.Adapter<HomeHighScoreRecyclerAdapter.ViewHolder> {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private RecyclerView r;
    private String highscore_game_name;
    private HashMap<String, HighScoresDataModel.UserGameData> highscores_to_Post = null;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference allHighScoresRef = database.getReference("HighScores");
    SimpleDateFormat localDateFormat= new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

    public HomeHighScoreRecyclerAdapter(RecyclerView fa, HashMap<String, HighScoresDataModel.UserGameData> highscores_to_Post) {

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        this.r = fa;
        this.highscores_to_Post = highscores_to_Post;
    }

    @NonNull
    @Override
    public HomeHighScoreRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_high_score_entry, parent,false);

        Log.d("Recyclerview","ON CREATEA VIEW HOLDER");

        final HomeHighScoreRecyclerAdapter.ViewHolder vh = new HomeHighScoreRecyclerAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull HomeHighScoreRecyclerAdapter.ViewHolder holder, int position) {
        try
        {
            final HighScoresDataModel.UserGameData u =highscores_to_Post.get(position);
            final String uid=u.gamedata_uid;

//            public TextView rank;
//            public TextView score;
//            public TextView date_of_score;
//            public TextView difficulty;
            if(holder.ref!=null && holder.refListener!=null)
            {
                holder.ref.removeEventListener(holder.refListener);
            }

//            if(currentUser.getUid().equals(uid))
//            {
//
//            }
            Log.d("Recyclerview","testing the data ONBIND VIEW HOLDER");

            holder.ref = allHighScoresRef.child(currentUser.getUid());
            holder.ref.getParent().addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        holder.entryKey=dataSnapshot.getKey();
                        holder.rank.setText(dataSnapshot.child("rank").getValue().toString());
                        holder.score.setText(dataSnapshot.child("score").getValue().toString());
                        holder.date_of_score.setText(localDateFormat.format(new Date(Long.parseLong(dataSnapshot.child("date_of_score").getValue().toString()))));
                        holder.difficulty.setText(dataSnapshot.child("difficulty").getValue().toString());
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            // holder.rank.setText();

            Log.d("BindRecView","In recycler view");
        } catch (Exception e) {
            // p did not contain a valid double
            // Toast.makeText(getApplicationContext(),"Rating did not contain a valid double - "+ e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        return highscores_to_Post.size();
    }

    public void removeListener(){
//        if(allMovieDataRef !=null && usersRefListener!=null)
//            allMovieDataRef.removeEventListener(usersRefListener);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private String entryKey;
        public TextView rank;
        public TextView score;
        public TextView date_of_score;
        public TextView difficulty;
        //        public ImageView movie_image;

        DatabaseReference ref;
        ValueEventListener refListener;

        public ViewHolder(View v) {
            super(v);
            rank = (TextView) v.findViewById(R.id.rank);
            difficulty = (TextView) v.findViewById(R.id.difficulty);
            score = (TextView) v.findViewById(R.id.score);
            date_of_score = (TextView) v.findViewById(R.id.date_of_score);
            entryKey="";
//            movie_image = (ImageView) v.findViewById(R.id.movie_image);

        }
    }
}