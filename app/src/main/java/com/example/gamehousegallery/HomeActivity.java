package com.example.gamehousegallery;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.gamehousegallery.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    HomeGameFragment homeGameFragment;

    private ActivityMainBinding binding;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    FirebaseDatabase database = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_home);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        homeGameFragment = new HomeGameFragment();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);

        transaction.replace(R.id.nav_host_fragment_home,homeGameFragment,"home_game_fragment");
        transaction.addToBackStack("home_game_fragment");
        transaction.commit();

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
                args.putString("game_name","pureluck");
                Intent hsBoardIntent = new Intent(this, HighScoreBoardActivity.class);
                hsBoardIntent.putExtra("game_name","matchmaker");
                startActivity(hsBoardIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}