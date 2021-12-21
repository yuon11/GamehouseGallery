package com.example.gamehousegallery;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 *
 */
public class HighScoreBoardFragment extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private List<String> score_keys_post_list;
    private List<String> game_list;
    private HashMap<String, String> key_to_Post;

    HomeHighScoreRecyclerAdapter highScoreRecyclerFragmentAdapter;
    TabLayout tabLayout;
    RecyclerView highscoreRecyclerView;
    TextView gridTextView;
//    LinearLayout mainLinearLayout;

    Button toggleHSBoard;
    String hsBoardSetting="USER";//GLOBAL
    String viewPagerGame;
    String hsBanner;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference allGameData = database.getReference("GameData");
    DatabaseReference allHighScoreData = database.getReference("HighScores");


    private void backgroundThread() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run()
                    {
                    }
                });
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        game_list = new ArrayList<>();
        key_to_Post = new HashMap<>();
        score_keys_post_list = new ArrayList<>();

        tabLayout = (TabLayout) getActivity().findViewById(R.id.game_tabs);
        gridTextView = (TextView) getActivity().findViewById(R.id.grid_name);

    }

    public void setHighScoreBanner(){
        hsBanner="YOUR "+viewPagerGame+ " HIGHSCORE BOARD";
        if (currentUser.getDisplayName()!=null ||!currentUser.getDisplayName().equals(""))
            hsBanner=currentUser.getDisplayName()+"'s "+viewPagerGame+ " HIGHSCORE BOARD";
        if (gridTextView!=null)
            gridTextView.setText(hsBanner);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_high_score_board, container, false);

        viewPagerGame = getArguments().getString("game_name");
        tabLayout = (TabLayout) rootView.findViewById(R.id.game_tabs);
        gridTextView = (TextView) rootView.findViewById(R.id.grid_name);

        // Setup Views to show games In Viewholder
        LinearLayoutManager layoutManager=new LinearLayoutManager(rootView.getContext());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        layoutManager.scrollToPosition(0);

        highscoreRecyclerView = (RecyclerView) rootView.findViewById(R.id.score_recycler_view);
        highscoreRecyclerView.setLayoutManager(layoutManager);
        highscoreRecyclerView.setItemAnimator(new DefaultItemAnimator());
        highScoreRecyclerFragmentAdapter = new HomeHighScoreRecyclerAdapter(highscoreRecyclerView, score_keys_post_list, viewPagerGame);
        highscoreRecyclerView.setAdapter(highScoreRecyclerFragmentAdapter);

        // mainLinearLayout = (LinearLayout) rootView.findViewById(R.id.main_home_game_layout);
        Log.d("onCreateView","IN HSBOARD FRAGMENT ITEM COUNT" + highScoreRecyclerFragmentAdapter.getItemCount());
        Log.d("onCreateView","IN HSBOARD FRAGMENT RECYC" + highscoreRecyclerView.toString());

//        String hs_banner = currentUser.getDisplayName().toUpperCase()+"'s "+viewPagerGame+ " HIGHSCORE BOARD";
//        gridTextView.setText(hs_banner);
        setHighScoreBanner();

        allGameData.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                key_to_Post.put(dataSnapshot.getKey(),dataSnapshot.child("game_name").getValue().toString());
                game_list.add(dataSnapshot.getKey());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                for (int i = 0; i < game_list.size(); i++) {
                    if(game_list.get(i).equals(snapshot.getKey()))
                    {
                        key_to_Post.put(game_list.get(i),snapshot.child("game_name").getValue().toString());
                        game_list.set(i, game_list.get(i));
                        break;
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                for (int i = 0; i < game_list.size(); i++) {
                    if(game_list.get(i).equals(snapshot.getKey()))
                    {
                        key_to_Post.remove(snapshot.getKey());
                        game_list.remove(i);
                        break;
                    }
                }
            }


            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        allHighScoreData.child(currentUser.getUid()).child(viewPagerGame).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                if (!score_keys_post_list.contains(dataSnapshot.getKey())){
                    score_keys_post_list.add(dataSnapshot.getKey());
                    highScoreRecyclerFragmentAdapter.notifyItemInserted(score_keys_post_list.size());
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                for (int i = 0; i < score_keys_post_list.size(); i++) {
                    Log.d("HSFragment", "scorelist size - " + score_keys_post_list.size());
                    if(score_keys_post_list.get(i).equals(snapshot.getKey()))
                    {
                        highScoreRecyclerFragmentAdapter.notifyItemChanged(i);
                        break;
                    }
                }

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                for (int i = 0; i < score_keys_post_list.size(); i++) {
                    if(score_keys_post_list.get(i).equals(snapshot.getKey()))
                    {
                        score_keys_post_list.remove(i);
                        highScoreRecyclerFragmentAdapter.notifyItemRemoved(i);
                        break;
                    }
                }
            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return rootView;
    }
}