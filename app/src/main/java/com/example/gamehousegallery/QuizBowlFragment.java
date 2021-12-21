package com.example.gamehousegallery;

import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class QuizBowlFragment extends Fragment {

    TextView equation_left;
    TextView equation_op;
    TextView equation_right;
    LinearLayout lin_layout;
    ImageView quiz_backgrd_imgview;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        mp = MediaPlayer.create(getBaseContext(), R.raw.victory_trumpet);
//        mp.start();
//        mp.release();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =inflater.inflate(R.layout.fragment_quiz_bowl, container, false);
//        Chronometer simpleChronometer = (Chronometer) rootView.findViewById(R.id.simpleChronometer); // initiate a chronometer

        lin_layout=(LinearLayout)rootView.findViewById(R.id.quiz_bowl_layout);
        quiz_backgrd_imgview=(ImageView) rootView.findViewById(R.id.quiz_bowl_imageview);
        equation_left=(TextView) rootView.findViewById(R.id.equation_left);
        equation_op=(TextView)rootView.findViewById(R.id.equation_operator);
        equation_right=(TextView)rootView.findViewById(R.id.equation_right);
        StorageReference pathReference = FirebaseStorage.getInstance().getReference(getString(R.string.quizbowl_background));
        pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
//                Picasso.get().load(uri).transform(new CircleTransform()).into(quiz_backgrd_imgview);
                Picasso.get().load(uri).into(quiz_backgrd_imgview);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

//        simpleChronometer.start();
        return rootView;
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

}