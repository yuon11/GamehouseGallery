package com.example.gamehousegallery;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class WinFragment extends Fragment {

    Integer scoreText = 0;
    Integer difficulty;

    ImageView win_imageview;
    TextView game_name;
    TextView score;
    TextView difficulty_textview;
    Button play_again_button;
    Button home_button;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference allHighScoreData = database.getReference("HighScores");

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View rootView =inflater.inflate(R.layout.fragment_win, container, false);

        Bundle args = getArguments();

        // Picasso liad in image, randomly select img 1 2 or 3
        win_imageview = rootView.findViewById(R.id.win_imageview);
        final int random = new Random().nextInt(3)+1; // [0, 60] + 20 => [20, 80]
        StorageReference pathReference = FirebaseStorage.getInstance().getReference("images/"+"you_win"+ random +".JPG");

        pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(win_imageview);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(rootView.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();

                // JUST DEFAULT OY YOU_WIN1.JPG
                StorageReference pathReference = FirebaseStorage.getInstance().getReference("images/"+"you_win1.JPG");
                pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(win_imageview);
                    }
                });
            }
        });

        game_name=rootView.findViewById(R.id.game_name);
        game_name.setText(args.getString("game_name"));

        score=rootView.findViewById(R.id.score);
        score.setText("YOUR SCORE: "+args.getInt("score"));

        difficulty_textview=rootView.findViewById(R.id.difficulty);
        difficulty_textview.setText("DIFFICULTY: "+args.getString("difficulty"));

        play_again_button = rootView.findViewById(R.id.playAgain);
        play_again_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playAgain(v);
            }
        });
        home_button = rootView.findViewById(R.id.homeButton);
        home_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnHome(v);
            }
        });


        DatabaseReference newScoreRef = allHighScoreData.child(currentUser.getUid())
                .child(args.getString("game_name"))
                .push();

        ScoreData thisRoundScore = new ScoreData(args.get("score").toString(),
                args.getString("difficulty"));

        newScoreRef.setValue(thisRoundScore).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getContext(),"Score Updated", Toast.LENGTH_SHORT).show();
                // Clear space after sending text
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });

        // Win Chime
        final MediaPlayer mp = MediaPlayer.create(getContext(), R.raw.fireworks1);
        mp.start();
        mp.start();
        mp.release();

        return rootView;
    }

    public void playAgain(View v) {

        Intent intent=new Intent(v.getContext(),GameHostActivity.class);
        intent.putExtra("game_name",game_name.getText().toString());
        v.getContext().startActivity(intent);
    }

    public void returnHome(View view) {
        Intent intent = new Intent(getContext(), HomeActivity.class);
        startActivity(intent);
    }
}