package com.example.gamehousegallery;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class HomeHighScoreRecyclerAdapter extends RecyclerView.Adapter<HomeHighScoreRecyclerAdapter.ViewHolder> {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private RecyclerView r;
    private List<String> score_to_post;
    private HashMap<String, UserGameDataModel> filtered_highscores_to_Post;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference allHighScoresRef = database.getReference("HighScores");
    SimpleDateFormat localDateFormat= new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    ChildEventListener usersRefListener;
    String game_name;

    public HomeHighScoreRecyclerAdapter(RecyclerView fa,List<String> highscores_to_post_key_list,String game_name) {
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        this.r = fa;
        this.score_to_post=highscores_to_post_key_list;
        this.game_name=game_name;
    }

    @NonNull
    @Override
    public HomeHighScoreRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_high_score_entry, parent,false);
        final HomeHighScoreRecyclerAdapter.ViewHolder vh = new HomeHighScoreRecyclerAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull HomeHighScoreRecyclerAdapter.ViewHolder holder, int position) {

//        Log.d("onBindView","SCORE_TO_POST - " + game_name);
//        Log.d("onBindView","SCORE_TO_POST - " + score_to_post.toString() + " - SIZE " + score_to_post.size());
//        Log.d("onBindView","POSITION- " + position);
        Log.d("onBindView","SCORES TO POST AND POSITION- " + score_to_post.toString() + " POS" + score_to_post.get(position));

        // final String uid=u.gamedata_uid;
        if(holder.ref!=null && holder.refListener!=null)
        {
            holder.ref.removeEventListener(holder.refListener);
        }


        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        holder.ref = database.getReference("HighScores")
                .child(currentUser.getUid())
                .child(game_name)
                .child(score_to_post.get(position))
                .getRef();
        holder.ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("onBindView","B4 Existence Check - "+ dataSnapshot.getKey() +" "+ dataSnapshot.toString() +" " + game_name);
                if (dataSnapshot.exists()){
                    holder.entryKey=dataSnapshot.getKey();
                    holder.rank.setText(dataSnapshot.child("score_rank").getValue().toString());
                    holder.score.setText(dataSnapshot.child("score").getValue().toString());
                    holder.date_of_score.setText(localDateFormat.format(new Date(Long.parseLong(dataSnapshot.child("date_of_score").getValue().toString()))));
                    holder.difficulty.setText(dataSnapshot.child("difficulty").getValue().toString());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return score_to_post.size();
    }

    public void removeListener(){
        if(allHighScoresRef !=null && usersRefListener!=null)
            allHighScoresRef.removeEventListener(usersRefListener);
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

        }
    }
}
//    @Override
//    public Filter getFilter() {
//        return new Filter() {
//
//            @Override
//            protected FilterResults performFiltering(CharSequence charSequence){
//            final FirebaseDatabase database = FirebaseDatabase.getInstance();
//                    holder.ref = database.getReference("HighScores")
//                            .child(currentUser.getUid())
//                            .child(game_name)
//                            .child(score_to_post.get(position))
//                            .getRef();
//
//                String charString = charSequence.toString();
//                Log.d("GetFilter", "get filter, current string: " + charString);
//                if (charString.isEmpty()) {
//                    md_filtered = key_to_Post;
//
//                    Log.d("GetFilterif", "Fileredlist size - " + md_filtered.size() +" Full list size - "+key_to_Post.size());
//
//                } else {
//                    HashMap<String, GameDataModel> filteredGameList = new HashMap<>();
//                    HashMap<String, UserGameDataModel> filteredScoresList = new HashMap<>();

//                    for (UserGameDataModel movie : key_to_Post.values()) {
//
//                        if ()
//                        {
//                        }
//                        else {
//                            try
//                            {
//                                if ()
//                                {
//                                }
//                            } catch (NumberFormatException e) {
//                                // p did not contain a valid double
//                                // Toast.makeText(getApplicationContext(),"Rating did not contain a valid double - "+ e.getMessage(), Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    }
//                    md_filtered = filteredList;
//                }
//                FilterResults filterResults = new FilterResults();
//                filterResults.values = md_filtered;
//                return filterResults;
//            }
//
//            @Override
//            protected void publishResults(CharSequence constraint, FilterResults filterResults) {
//                Log.d("Publish Results", "Constraints =" + constraint);
//                md_filtered = (HashMap<String, MovieDataModel>) filterResults.values;
//                keyList.clear();
//                keyList.addAll(md_filtered.keySet());
//                notifyDataSetChanged();
//            }
//        };
//    }