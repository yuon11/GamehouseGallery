package com.example.gamehousegallery;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.transition.Fade;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
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

    MediaPlayer mp;
    ImageView logoImageView;
    TextView titleTextView;
    ImageView backgroundImageView;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // User Auth
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        logoImageView = (ImageView) findViewById(R.id.gameGallLogo);
        titleTextView = (TextView) findViewById(R.id.gameGallTitle);
        backgroundImageView = (ImageView) findViewById(R.id.background_imageview);
        backgroundImageView.setScaleType(ImageView.ScaleType.FIT_XY);
        backgroundImageView.setAlpha(0.4f);

        // Fade In, as test
        titleTextView.setText(getString(R.string.app_name));
        titleTextView.setTextSize(40);
        // titleTextView.setBackground();

        new CountDownTimer(2000, 2000) {
            public void onTick(long millisUntilFinished) {

                scaleView(titleTextView, .1f,1);
                strobeEffect(titleTextView);

                final int random = new Random().nextInt(3)+1; // [0, 60] + 20 => [20, 80]
                StorageReference pathReference = FirebaseStorage.getInstance().getReference("images/"+"highscore_background"+ random +".JPG");
                pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
//                        Picasso.get().load(uri).transform(new CircleTransform()).into(logoImageView);
                        Picasso.get().load(uri).into(backgroundImageView);
                        scaleView(backgroundImageView, .1f,1);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        backgroundImageView.setImageResource(R.drawable.not_signed_in);
                    }
                });

                // Opening Game announced
                mp = MediaPlayer.create(getBaseContext(), R.raw.gamehouse_gallery_start_tune);
                mp.start();
                // Get Title Logo w/ Picasso

                pathReference = FirebaseStorage.getInstance().getReference(getString(R.string.gamehouse_gallery_logo));
                pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
//                        Picasso.get().load(uri).transform(new CircleTransform()).into(logoImageView);
                        Picasso.get().load(uri).into(logoImageView);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            public void onFinish() {
                spinView(titleTextView);
                mp =MediaPlayer.create(getBaseContext(), R.raw.victory_trumpet);
                mp.start();
            }
        }.start();

    }

    public void scaleView(View v, float startScale, float endScale) {
        Animation anim = new ScaleAnimation(
                1f, 1f, // Start and end values for the X axis scaling
                startScale, endScale, // Start and end values for the Y axis scaling
                Animation.RELATIVE_TO_SELF, 1f, // Pivot point of X scaling
                Animation.RELATIVE_TO_SELF, 1f); // Pivot point of Y scaling
        anim.setFillAfter(true); // Needed to keep the result of the animation
        anim.setDuration(2000);
        v.startAnimation(anim);
    }
    private void strobeEffect(TextView thisView){
        // Set to mk
        new CountDownTimer(7000, 5) {

            public void onTick(long millisUntilFinished) {
                // Change Opening Title Color at random
                Random rnd = new Random();
                int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                thisView.setTextColor(color);
            }

            public void onFinish() {
                thisView.setTextColor(getResources().getColor(R.color.bright_red));
                mp.release();
            }
        }.start();
    }

    private void spinView(View thisView){
        thisView.animate();
        RotateAnimation rotate = new RotateAnimation(0, 1080, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(3000);
        rotate.setInterpolator(new LinearInterpolator());

        thisView.startAnimation(rotate);
    }
    private void fadeInView(View thisView){
        thisView.animate();
        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator());
        fadeIn.setDuration(5000);
        fadeIn.setFillAfter(true);

        thisView.startAnimation(fadeIn);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new CountDownTimer(6500, 1000) {

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