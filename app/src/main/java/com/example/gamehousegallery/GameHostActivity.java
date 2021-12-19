package com.example.gamehousegallery;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.gamehousegallery.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class GameHostActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    MatchgameMainFragment matchgameMainFragment;
    PureLuckFragment pureLuckFragment;
    QuizBowlFragment quizBowlFragment;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    String game_name;

//    DatabaseReference allMovieDataRef = database.getReference("GameData");
//    SimpleDateFormat localDateFormat= new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("GameHostactivity","in on create method");

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_game_host);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        // Open bundle for Game extra to open
        matchgameMainFragment = new MatchgameMainFragment();
        pureLuckFragment = new PureLuckFragment();
        quizBowlFragment = new  QuizBowlFragment();
    }

    //    @Override
    protected void onStart() {
        super.onStart();

        String game_name = getIntent().getStringExtra("game_name");

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);

        if (game_name.toLowerCase().equals("matchmaker")){
            transaction.replace(R.id.game_host_fragment,matchgameMainFragment,"home_game_fragment");
            transaction.addToBackStack(null);
            transaction.commit();

        } else if (game_name.toLowerCase().equals("pureluck")){
            Toast.makeText(getParent(), "Opening Game " + game_name, Toast.LENGTH_SHORT).show();

        } else if (game_name.toLowerCase().equals("quizbowl!")){
            Toast.makeText(getParent(), "Opening Game " + game_name, Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getParent(), "No Implementation Found For " + game_name, Toast.LENGTH_SHORT).show();
        }
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}