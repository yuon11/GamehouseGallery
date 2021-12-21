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

import java.util.ArrayList;
import java.util.List;
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
    List<String> current_equation;

    String difficulty="beginner";

    List<String> arithmertic_operations; //= {" + "," - "," x "," / "};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        arithmertic_operations = new ArrayList<String>();
        arithmertic_operations.add("+");
        arithmertic_operations.add("-");
        arithmertic_operations.add("x");
        arithmertic_operations.add("/");
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
                Picasso.get().load(uri).into(quiz_backgrd_imgview);
                quiz_backgrd_imgview.setScaleType(ImageView.ScaleType.FIT_XY);
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

    private String pickOp(){
        Random randomGenerator = new Random();
        int randIndex = randomGenerator.nextInt(arithmertic_operations.size());
        return arithmertic_operations.get(randIndex);
    }
    public void generateQuestion(){

        current_equation.clear();
        ArrayList<String> generated_question = new ArrayList<>();
//        array<std::string, 4> arithmertic_operations = {" + "," - "," x "," / "};
        Random randomGenerator = new Random();

        int randomLeftInt = randomGenerator.nextInt(100);
        generated_question.add(Double.toString(randomLeftInt));

        String operation = pickOp();
        generated_question.add(operation);

        int randomRightInt = randomGenerator.nextInt(100);
        generated_question.add(Double.toString(randomLeftInt));
        // equation_right.setText(randomRightInt);
        // log("Generated : " + randomInt);

        current_equation=generated_question;
    }

    public void setQuizUI(){
//        lin_layout=(LinearLayout) findViewById(R.id.quiz_bowl_layout);
//        quiz_backgrd_imgview=(ImageView) findViewById(R.id.quiz_bowl_imageview);
//        equation_left=(TextView) findViewById(R.id.equation_left);
//        equation_op=(TextView) findViewById(R.id.equation_operator);
//        equation_right=(TextView) findViewById(R.id.equation_right);
        equation_left.setText(current_equation.get(0));
        equation_op.setText(current_equation.get(1));
        equation_right.setText(current_equation.get(2));
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