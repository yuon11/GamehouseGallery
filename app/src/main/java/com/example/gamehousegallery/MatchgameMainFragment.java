package com.example.gamehousegallery;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Random;

public class MatchgameMainFragment extends Fragment {

    MatchgamePlayFragment matchgamePlayFragment;
    ImageView game_image;
    Button easyButton;
    Button medButton;
    Button hardButton;
    Button masterButton;
    ImageView backgroundImageView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        matchgamePlayFragment = new MatchgamePlayFragment();
//        Bundle args = new Bundle();
//        args.putInt("someInt", someInt);
//        myFragment.setArguments(args);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle args = new Bundle();

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_matchgame_main, container, false);
        game_image = rootView.findViewById(R.id.game_image);
        backgroundImageView = (ImageView) rootView.findViewById(R.id.background_imageview);

        StorageReference pathReference = FirebaseStorage.getInstance().getReference("images/matchmaker.JPG");
        pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(game_image);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(rootView.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();

                // Have picasso load in a default image
                // Picasso.get().load(uri).into(game_image_imageview);
            }
        });


        final int random = new Random().nextInt(3)+1; // [0, 60] + 20 => [20, 80]
        pathReference = FirebaseStorage.getInstance().getReference("images/"+"highscore_background"+ random +".JPG");
        pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
//                        Picasso.get().load(uri).transform(new CircleTransform()).into(logoImageView);
                Picasso.get().load(uri).into(backgroundImageView);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "No Background Image Loaded "+e.getMessage(), Toast.LENGTH_SHORT).show();
//                backgroundImageView.setImageResource();
//                scaleView(backgroundImageView, .1f,1);
            }
        });

        easyButton = rootView.findViewById(R.id.easyButton);
        medButton= rootView.findViewById(R.id.medButton);
        hardButton= rootView.findViewById(R.id.hardButton);
        masterButton= rootView.findViewById(R.id.masterButton);

        easyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "Starting Beginner Game", Toast.LENGTH_SHORT).show();
                args.putString("difficulty","easy");
                startGameFragment(args);
            }
        });

        medButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "Starting Intermediate Game", Toast.LENGTH_SHORT).show();
                args.putString("difficulty","med");
                startGameFragment(args);
            }
        });

        hardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "Starting Adept Game", Toast.LENGTH_SHORT).show();
                args.putString("difficulty","hard");
                startGameFragment(args);
            }
        });

        masterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "Starting Expert Game", Toast.LENGTH_SHORT).show();
                args.putString("difficulty","master");
                startGameFragment(args);
            }
        });

        return rootView;
    }

    public void startGameFragment(Bundle args) {

        FragmentManager manager = getParentFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);

        matchgamePlayFragment.setArguments(args);

        transaction.replace(R.id.game_host_fragment,matchgamePlayFragment,"matchgame_play_fragment");
        transaction.addToBackStack(null);
        transaction.commit();

//        Intent intent = new Intent(view.getContext(), MatchgameActivity.class);
//        intent.putExtra("column", 3);
//        startActivity(intent);
    }

}