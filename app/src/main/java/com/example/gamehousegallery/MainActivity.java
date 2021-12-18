package com.example.gamehousegallery;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Random;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    ImageView logoImageView;
    TextView titleTextView;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MediaPlayer mp = MediaPlayer.create(getBaseContext(), R.raw.gamehouse_gallery_start_tune);
        mp.start();

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        StorageReference pathReference = FirebaseStorage.getInstance().getReference(getString(R.string.gamehouse_gallery_logo));
        pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).transform(new CircleTransform()).into(logoImageView);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        logoImageView = (ImageView) findViewById(R.id.gameGallLogo);
        titleTextView = (TextView) findViewById(R.id.gameGallTitle);
        titleTextView.setText(getString(R.string.app_name));

        animateView(titleTextView);

        new CountDownTimer(7000, 10) {

            public void onTick(long millisUntilFinished) {
                Random rnd = new Random();
                int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                titleTextView.setTextColor(color);
            }

            public void onFinish() {
                titleTextView.setTextColor(getResources().getColor(R.color.red_orange));
                mp.release();
                // Initialize Firebase Auth
          /* mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                }
            });*/
            }
        }.start();

    }

    private void animateView(View thisView){
        thisView.animate();
        RotateAnimation rotate = new RotateAnimation(0, 1080, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(1000);
        rotate.setInterpolator(new LinearInterpolator());
        thisView.startAnimation(rotate);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new CountDownTimer(3000, 1000) {

            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                if(currentUser==null){
                    Toast.makeText(MainActivity.this, "No user found", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MainActivity.this,SignupLogin.class));
                    finish();
                }
                else{
                    if(currentUser.isEmailVerified()) {
                        Toast.makeText(MainActivity.this, "User already signed in", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(MainActivity.this, HomeActivity.class));
                        finish();
                    }
                    else{
                        Toast.makeText(MainActivity.this, "Please verify your email and login.", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(MainActivity.this, SignupLogin.class));
                        finish();
                    }
                }

            }
        }.start();
    }
}