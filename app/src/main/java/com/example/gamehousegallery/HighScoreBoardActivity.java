package com.example.gamehousegallery;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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

public class HighScoreBoardActivity extends FragmentActivity {


    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private List<String> score_keys_post_list = null;
    private List<String> game_list = null;
    private HashMap<String, String> key_to_Post = null;

    HighScoreBoardFragmentAdapter higScoreBoardFragmentAdapter;
    ViewPager2 mViewPager;
    Button toggleHSBoard;

    String hsBoardSetting="USER";//GLOBAL

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference allGameData = database.getReference("GameData");
    DatabaseReference allHighScoreData = database.getReference("HighScores");
    SimpleDateFormat localDateFormat= new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

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

    private void backgroundThread() {

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                HighScoreBoardActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                    }
                });
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_score_board);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        game_list = new ArrayList<>();
        score_keys_post_list = new ArrayList<>();
        key_to_Post=new HashMap<>();

        mViewPager = (ViewPager2) findViewById(R.id.pager);
        higScoreBoardFragmentAdapter = new HighScoreBoardFragmentAdapter(this);
        allGameData.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                key_to_Post.put(dataSnapshot.getKey(),dataSnapshot.getKey());
                game_list.add(dataSnapshot.getKey());
                higScoreBoardFragmentAdapter.notifyItemInserted(game_list.size()-1);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                for (int i = 0; i < game_list.size(); i++) {
                    if(game_list.get(i).equals(snapshot.getKey()))
                    {
                        key_to_Post.put(game_list.get(i),snapshot.getKey());
                        game_list.set(i, game_list.get(i));
                        higScoreBoardFragmentAdapter.notifyItemChanged(i);
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
                        higScoreBoardFragmentAdapter.notifyItemRemoved(i);
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

        // Setup Views for High Score grid
        toggleHSBoard = findViewById(R.id.toggle_highscore_grid);
        toggleHSBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Changing Score Scoreboard", Toast.LENGTH_SHORT).show();
                if (hsBoardSetting.equals("USER")){

                }
                else{
                    hsBoardSetting="USER";

                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mViewPager.setAdapter(higScoreBoardFragmentAdapter);
        mViewPager.setPageTransformer((ViewPager2.PageTransformer) (new ZoomOutPageTransformer()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.homebar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.signout:
                mAuth.signOut();
                finish();
                return true;
            case R.id.search_action:

                // IMPLEMENT FILETERING
                return true;
            case R.id.editProfile:
                startActivity(new Intent(this, EditProfile.class));
                return true;

            case R.id.homeOption:
                startActivity(new Intent(this, HomeActivity.class));
                return true;
            case R.id.gamesOption:
                //startActivity(new Intent(this, EditProfile.class));
                return true;

            case R.id.highscoresOption:
                Bundle args = new Bundle();

                Intent hsBoardIntent = new Intent(this, HighScoreBoardActivity.class);
                hsBoardIntent.putExtra("game_name",game_list.get(0));
                startActivity(hsBoardIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public class HighScoreBoardFragmentAdapter extends FragmentStateAdapter {


        public HighScoreBoardFragmentAdapter(FragmentActivity fa) {
            super(fa);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            Fragment fragment = new HighScoreBoardFragment();
            Bundle args = new Bundle();
            args.putString("game_name",game_list.get(position));
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getItemCount() {
            return game_list.size();
        }
    }
}