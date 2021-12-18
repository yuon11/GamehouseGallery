package com.example.gamehousegallery;

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
import android.widget.GridLayout;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class HomeGameFragment extends Fragment {
    public class ZoomOutPageTransformer implements ViewPager2.PageTransformer {
        private static final float MIN_SCALE = 0.85f;
        private static final float MIN_ALPHA = 0.5f;

        private void backgroundThread(View view, float position) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    view.post(new Runnable() {
                        @Override
                        public void run() {
                            int pageWidth = view.getWidth();
                            int pageHeight = view.getHeight();

                            if (position < -1) { // [-Infinity,-1)
                                // This page is way off-screen to the left.
                                view.setAlpha(0f);

                            } else if (position <= 1) { // [-1,1]
                                // Modify the default slide transition to shrink the page as well
                                float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
                                float vertMargin = pageHeight * (1 - scaleFactor) / 2;
                                float horzMargin = pageWidth * (1 - scaleFactor) / 2;
                                if (position < 0) {
                                    view.setTranslationX(horzMargin - vertMargin / 2);
                                } else {
                                    view.setTranslationX(-horzMargin + vertMargin / 2);
                                }

                                // Scale the page down (between MIN_SCALE and 1)
                                view.setScaleX(scaleFactor);
                                view.setScaleY(scaleFactor);

                                // Fade the page relative to its size.
                                view.setAlpha(MIN_ALPHA +
                                        (scaleFactor - MIN_SCALE) /
                                                (1 - MIN_SCALE) * (1 - MIN_ALPHA));

                            } else { // (1,+Infinity]
                                // This page is way off-screen to the right.
                                view.setAlpha(0f);
                            }
                        }
                    });
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        Log.d("Error", e.getLocalizedMessage());
                    }
                }
            }).start();
        }

        public void transformPage(View view, float position) {
            backgroundThread(view, position);
        }
    }

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    //private List<String> highscores_list = null;
    private HashMap<String, HighScoresDataModel> highscores_to_Post = null;
    private HashMap<String, HighScoresDataModel.UserGameData> gamescores_to_Post = null;
    private List<String> game_list = null;
    private HashMap<String, GameDataModel> key_to_Post = null;

    HomeGameFragmentAdapter gameCollectionFragmentAdapter;
    HomeHighScoreRecyclerAdapter highScoreFragmentAdapter;
    ViewPager2 mViewPager;
    TabLayout tabLayout;
    RecyclerView highscoreRecyclerView;
    GridLayout gridLayout;

    Button toggleHSBoard;
    String hsBoardSetting="USER";//GLOBAL
    String viewPagerGame="matchmaker";

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference allGameData = database.getReference("GameData");
    DatabaseReference allHighScoreData = database.getReference("HighScores");

    private void backgroundThread(View rootView) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                rootView.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("BACK Thread", "View Page");

                        // gameCollectionFragmentAdapter = new HomeGameFragmentAdapter(HomeGameFragment.this, game_list, key_to_Post);
                        mViewPager = (ViewPager2) rootView.findViewById(R.id.pager);
                        gridLayout = (GridLayout) rootView.findViewById(R.id.high_score_grid);
                        tabLayout = (TabLayout) rootView.findViewById(R.id.game_tabs);

                        mViewPager.setAdapter(gameCollectionFragmentAdapter);
                        mViewPager.setPageTransformer((ViewPager2.PageTransformer) (new ZoomOutPageTransformer()));
                        // Recycler View Setup of scoreboard

                        LinearLayoutManager layoutManager=new LinearLayoutManager(rootView.getContext());
                        layoutManager.setOrientation(RecyclerView.VERTICAL);
                        // layoutManager.scrollToPosition(0);

                        Log.d("THREAD", "BEFORE ");
                        highscoreRecyclerView.setLayoutManager(layoutManager);
                        highscoreRecyclerView.setAdapter(highScoreFragmentAdapter);
                        highscoreRecyclerView.setItemAnimator(new DefaultItemAnimator());

                        if (mViewPager.getAdapter() != null) {
                            new TabLayoutMediator(tabLayout, mViewPager,
                                    (tab, position) -> tab.setText(key_to_Post.get(game_list.get(position)).game_name.toString())
                            ).attach();
                        }

                        // gridLayout.setAdapter(highScoreFragmentAdapter);
                    }
                });
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Log.d("Error", e.getLocalizedMessage());
                }
            }
        }).start();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        game_list = new ArrayList<>();
        key_to_Post = new HashMap<>();
        highscores_to_Post = new HashMap<>();
        gamescores_to_Post = new HashMap<>();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_home_game, container, false);

        highscoreRecyclerView = (RecyclerView) rootView.findViewById(R.id.score_recycler_view);
        highScoreFragmentAdapter = new HomeHighScoreRecyclerAdapter(highscoreRecyclerView,gamescores_to_Post);

        // Setup Views to show games In Viewholder
        gameCollectionFragmentAdapter = new HomeGameFragmentAdapter(HomeGameFragment.this, game_list, key_to_Post);

        // background thread for view pager adapting and animation
        backgroundThread(rootView);

        // Setup Views for High Score grid
        gridLayout = rootView.findViewById(R.id.high_score_grid);
        toggleHSBoard = rootView.findViewById(R.id.toggle_highscore_grid);
        toggleHSBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(rootView.getContext(), "Changing Score Scoreboard", Toast.LENGTH_SHORT).show();
                if (hsBoardSetting.equals("USER")){
                    hsBoardSetting="Global";
                }
            }
        });

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        allGameData.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {

                GameDataModel gameModel=new GameDataModel(
                        dataSnapshot.getKey(),
                        dataSnapshot.child("game_name").getValue().toString(),
                        dataSnapshot.child("game_image").getValue().toString(),
                        dataSnapshot.child("description").getValue().toString()
                );

                key_to_Post.put(dataSnapshot.getKey(),gameModel);
                game_list.add(dataSnapshot.getKey());
                gameCollectionFragmentAdapter.notifyDataSetChanged();
                gameCollectionFragmentAdapter.notifyItemInserted(game_list.size()-1);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                for (int i = 0; i < game_list.size(); i++) {
                    if(game_list.get(i).equals(snapshot.getKey()))
                    {
                        GameDataModel gameModel=new GameDataModel(
                                snapshot.getKey(),
                                snapshot.child("game_name").getValue().toString(),
                                snapshot.child("game_image").getValue().toString(),
                                snapshot.child("description").getValue().toString()
                        );

                        key_to_Post.put(game_list.get(i),gameModel);
                        game_list.set(i, game_list.get(i));
                        gameCollectionFragmentAdapter.notifyItemChanged(i);
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
                        gameCollectionFragmentAdapter.notifyItemRemoved(i);
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

        Log.d("OnViewCreate", "AllHighScoreData ");
        allHighScoreData.child(currentUser.getUid()).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {

                Log.d("HighScoresChildAdded", "Values of datasnapshot " + dataSnapshot.toString());

                HighScoresDataModel gameModel=new HighScoresDataModel(dataSnapshot.getKey(),currentUser.getUid());

                if (dataSnapshot.getKey().toLowerCase().equals("matchmaker"))
                {
                    Log.d("HighScoresChildAdded", "Values of matchmaker datasnapshot " + dataSnapshot.toString());// iterate through, adding to hashmap
                    gameModel.game_name_to_highscore_gamedata.put("matchmaker",dataSnapshot.getRef());
                }
                if (dataSnapshot.getKey().toLowerCase().equals("pureluck"))
                {
                    Log.d("HighScoresChildAdded", "Values of pureluck datasnapshot " + dataSnapshot.toString());// iterate through, adding to hashmap
                    //gameModel.setGameScoreData("pureluck");
                    gameModel.game_name_to_highscore_gamedata.put("pureluck",dataSnapshot.getRef());
                }
                if (dataSnapshot.getKey().toLowerCase().equals("quizbowl!"))
                {
                    Log.d("HighScoresChildAdded", "Values of quizbowl datasnapshot " + dataSnapshot.toString());// iterate through, adding to hashmap
                    //gameModel.setGameScoreData("quizbowl!");
                    gameModel.game_name_to_highscore_gamedata.put("quizbowl!",dataSnapshot.getRef());
                }

                highscores_to_Post.put(dataSnapshot.getKey(),gameModel);
                highScoreFragmentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                for (int i = 0; i < highscores_to_Post.size(); i++) {
                    if(highscores_to_Post.get(i).equals(snapshot.getKey()))
                    {

                        HighScoresDataModel gameModel=new HighScoresDataModel(snapshot.getKey(),currentUser.getUid());

                        if (snapshot.getKey().toLowerCase().equals("matchmaker"))
                        {
                            Log.d("HighScoresChildAdded", "Values of matchmaker datasnapshot " + snapshot.toString());// iterate through, adding to hashmap
                            gameModel.game_name_to_highscore_gamedata.put("matchmaker",snapshot.getRef());
                        }
                        if (snapshot.getKey().toLowerCase().equals("pureluck"))
                        {
                            Log.d("HighScoresChildAdded", "Values of pureluck datasnapshot " + snapshot.toString());// iterate through, adding to hashmap
                            //gameModel.setGameScoreData("pureluck");
                            gameModel.game_name_to_highscore_gamedata.put("pureluck",snapshot.getRef());
                        }
                        if (snapshot.getKey().toLowerCase().equals("quizbowl!"))
                        {
                            Log.d("HighScoresChildAdded", "Values of quizbowl datasnapshot " + snapshot.toString());// iterate through, adding to hashmap
                            //gameModel.setGameScoreData("quizbowl!");
                            gameModel.game_name_to_highscore_gamedata.put("quizbowl!",snapshot.getRef());
                        }

                        highscores_to_Post.put(snapshot.getKey(),gameModel);
                        highScoreFragmentAdapter.notifyItemChanged(i);
                        // iterate through, adding to hashmap

                        break;
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                for (int i = 0; i < highscores_to_Post.size(); i++) {
                    if(highscores_to_Post.get(i).equals(snapshot.getKey()))
                    {
                        highscores_to_Post.remove(snapshot.getKey());
                        highScoreFragmentAdapter.notifyItemRemoved(i);
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

        Log.d("HighScoreData", "Highscore Data Size - " + highscores_to_Post.size());
    }
}