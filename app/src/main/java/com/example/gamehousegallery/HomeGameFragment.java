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
import android.widget.TextView;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
    private List<String> score_keys_post_list = null;
    private List<String> game_list = null;
    private HashMap<String, GameDataModel> key_to_Post = null;

    HomeGameFragmentAdapter gameCollectionFragmentAdapter;
    HomeHighScoreRecyclerAdapter highScoreRecyclerFragmentAdapter;
    ViewPager2 mViewPager;
    TabLayout tabLayout;
    RecyclerView highscoreRecyclerView;
    TextView gridTextView;
//    LinearLayout mainLinearLayout;

    Button toggleHSBoard;
    String hsBoardSetting="USER";//GLOBAL
    String viewPagerGame;
    String hsBanner;
    HighScoresDataModel highScoresDataModel;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference allGameData = database.getReference("GameData");
    DatabaseReference allHighScoreData = database.getReference("HighScores");
    SimpleDateFormat localDateFormat= new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");


    private void backgroundThread(View rootView) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                rootView.post(new Runnable() {
                    @Override
                    public void run() {

                        mViewPager.setAdapter(gameCollectionFragmentAdapter);
                        mViewPager.setPageTransformer((ViewPager2.PageTransformer) (new ZoomOutPageTransformer()));
                        // Recycler View Setup of scoreboard
                        if (mViewPager.getAdapter() != null) {
                            new TabLayoutMediator(tabLayout, mViewPager,
                                    (tab, position) -> tab.setText(key_to_Post.get(game_list.get(position)).game_name.toString())
                            ).attach();
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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        game_list = new ArrayList<>();
        key_to_Post = new HashMap<>();
        score_keys_post_list = new ArrayList<>();
        highScoresDataModel=new HighScoresDataModel(
                currentUser.getUid(),
                currentUser.getUid());

        viewPagerGame="matchmaker";
        setHsBanner();
    }

    public void setHsBanner(){
        hsBanner="YOUR "+viewPagerGame+ " HIGHSCORE BOARD";
        if (currentUser.getDisplayName()!=null ||!currentUser.getDisplayName().equals(""))
            hsBanner=currentUser.getDisplayName()+"'s "+viewPagerGame+ " HIGHSCORE BOARD";
        if (gridTextView!=null)
            gridTextView.setText(hsBanner);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_home_game, container, false);

        highscoreRecyclerView = (RecyclerView) rootView.findViewById(R.id.score_recycler_view);
        mViewPager = (ViewPager2) rootView.findViewById(R.id.pager);
        tabLayout = (TabLayout) rootView.findViewById(R.id.game_tabs);
        gridTextView = (TextView) rootView.findViewById(R.id.grid_name);

        LinearLayoutManager layoutManager=new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        layoutManager.scrollToPosition(0);

        highscoreRecyclerView.setLayoutManager(layoutManager);
        highscoreRecyclerView.setItemAnimator(new DefaultItemAnimator());

        // Setup Views to show games In Viewholder
        gameCollectionFragmentAdapter = new HomeGameFragmentAdapter(HomeGameFragment.this, game_list, key_to_Post);
        highScoreRecyclerFragmentAdapter = new HomeHighScoreRecyclerAdapter(highscoreRecyclerView, score_keys_post_list, viewPagerGame);
        highscoreRecyclerView.setAdapter(highScoreRecyclerFragmentAdapter);

        String hs_banner = currentUser.getDisplayName().toUpperCase()+"'s "+viewPagerGame+ " HIGHSCORE BOARD";
        gridTextView.setText(hs_banner);
        // mainLinearLayout = (LinearLayout) rootView.findViewById(R.id.main_home_game_layout);



        // descriptionText.setMovementMethod(new ScrollingMovementMethod());
        // background thread for view pager adapting and animation
        backgroundThread(rootView);

        // Setup Views for High Score grid
        toggleHSBoard = rootView.findViewById(R.id.toggle_highscore_grid);
        toggleHSBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(rootView.getContext(), "Changing Score Scoreboard", Toast.LENGTH_SHORT).show();
                if (hsBoardSetting.equals("USER")){
                    hsBoardSetting="GLOBAL";
                    String banner = "GLOBAL "+viewPagerGame+" HIGHSCORE BOARD";
                    gridTextView.setText(banner);
                }
                else{
                    hsBoardSetting="USER";
                    setHsBanner();
                }
            }
        });

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

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener(){
            @Override
            public void onTabSelected(TabLayout.Tab tab){
                int position = tab.getPosition();
                viewPagerGame=key_to_Post.get(game_list.get(position)).game_name.toString();
                setHsBanner();
                score_keys_post_list.clear();
                allHighScoreData.child(currentUser.getUid()).child(viewPagerGame).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {

                        //Log.d("onChildAdded", "Datasnapshot - \n" + dataSnapshot.toString());
                        if (!score_keys_post_list.contains(dataSnapshot.getKey())){

                            UserGameDataModel newData = new UserGameDataModel(
                                    dataSnapshot.getKey(),
                                    viewPagerGame,
                                    dataSnapshot.child("score").getValue().toString(),
                                    dataSnapshot.child("difficulty").getValue().toString(),
                                    localDateFormat.format(new Date(Long.parseLong(dataSnapshot.child("date_of_score").getValue().toString()))),
                                    dataSnapshot.child("score_rank").getValue().toString()
                            );

                            score_keys_post_list.add(dataSnapshot.getKey());
                            highScoresDataModel.setGameScoreData(viewPagerGame, newData);
                             highScoreRecyclerFragmentAdapter.notifyDataSetChanged();

                            Log.d("onChildAdded", "Score To Post List 2- \n" + score_keys_post_list.toString());
                            Log.d("onChildAdded", "Score To Post List 2- \n" + score_keys_post_list.size());

                        }
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        for (int i = 0; i < score_keys_post_list.size(); i++) {
                            Log.d("onChildChanged", "scorelist size - " + score_keys_post_list.size());
                            if(score_keys_post_list.get(i).equals(snapshot.getKey()))
                            {
                                UserGameDataModel newData = new UserGameDataModel(
                                        snapshot.getKey(),
                                        viewPagerGame,
                                        snapshot.child("score").getValue().toString(),
                                        snapshot.child("difficulty").getValue().toString(),
                                        localDateFormat.format(new Date(Long.parseLong(snapshot.child("date_of_score").getValue().toString()))),
                                        snapshot.child("score_rank").getValue().toString()
                                );

                                highScoresDataModel.setGameScoreData(viewPagerGame,newData);
                                highScoreRecyclerFragmentAdapter.notifyItemChanged(i);
                                highScoreRecyclerFragmentAdapter.notifyDataSetChanged();
//                                .notifyItemInserted(keyList.size() - 1);
//                                recyclerView.scrollToPosition(keyList.size() - 1);
                                break;
                            }
                        }

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                        for (int i = 0; i < score_keys_post_list.size(); i++) {
                            if(score_keys_post_list.get(i).equals(snapshot.getKey()))
                            {
                                highScoresDataModel.game_name_to_score_map.remove(snapshot.getKey());
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
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}