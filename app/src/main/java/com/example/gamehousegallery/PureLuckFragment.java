package com.example.gamehousegallery;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.CountDownTimer;
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

import java.util.Random;


public class PureLuckFragment extends Fragment {

    public static final Random RANDOM = new Random();
    private Button btn;
    private Button toss_btn;
    private Button heads_btn;
    private Button tails_btn;
    private Button log_score_btn;
    private Button clear_btn;
    private TextView toss_cnt_txtview;
    private TextView score_txtview;
    private TextView difficulty_txtview;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    FlippableFragment headFlippable;
    FlippableFragment tailsFlipable;
    boolean mShowingBack=false;


    private int heads_guess_cnt;
    private int tails_guess_cnt;
    private int guess_cnt;

    private int crrct_heads_guess_cnt;
    private int crrct_tails_guess_cnt;
    private int crrct_guess_cnt;

    private int toss_cnt;
    private int flip_cnt;
    private float percentage;
    private String current_guess;
    private String difficulty_reached="Unranked";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        toss_cnt=0;     // num of times toss is selected in general
        current_guess = ""; // if current_guess.equals.("")

        if (savedInstanceState == null) {

            Bundle args1 = new Bundle();
            args1.putInt("flip_image", R.drawable.tails);
            tailsFlipable = new FlippableFragment();
            tailsFlipable.setArguments(args1);

            // Heads
            Bundle args = new Bundle();
            args.putInt("flip_image", R.drawable.heads);
            headFlippable = new FlippableFragment();
            headFlippable.setArguments(args);

            getFragmentManager().beginTransaction()
                    .add(R.id.container, headFlippable)
                    .commit();
        }
    }

    private void flipCard() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // DO your work here
                // get the data
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // update UI
                        try {
                            if (mShowingBack) {
                                getFragmentManager().popBackStack();
                                mShowingBack = false;
                                return;
                            }

                            // Flip to the back.

                            mShowingBack = true;

                            // Create and commit a new fragment transaction that adds the fragment for the back of
                            // the card, uses custom animations, and is part of the fragment manager's back stack.

                            getFragmentManager()
                                    .beginTransaction()

                                    // Replace the default fragment animations with animator resources representing
                                    // rotations when switching to the back of the card, as well as animator
                                    // resources representing rotations when flipping back to the front (e.g. when
                                    // the system Back button is pressed).

                                    // FLIP FROM RIGHT
//                    .setCustomAnimations(
//                            R.animator.card_flip_right_in,
//                            R.animator.card_flip_right_out,
//                            R.animator.card_flip_left_in,
//                            R.animator.card_flip_left_out)

                                    // FLIP FROM LEFT
                                    .setCustomAnimations(
                                            R.animator.card_flip_left_in,
                                            R.animator.card_flip_left_out,
                                            R.animator.card_flip_right_in,
                                            R.animator.card_flip_right_out)

                                    // Replace any fragments currently in the container view with a fragment
                                    // representing the next page (indicated by the just-incremented currentPage
                                    // variable).
                                    .replace(R.id.container, tailsFlipable)

                                    // Add this transaction to the back stack, allowing users to press Back
                                    // to get to the front of the card.
                                    .addToBackStack(null)

                                    // Commit the transaction.
                                    .commit();

                        } catch (IllegalStateException e){
                            // Reset state of game fragment - refresh fragment stack
                            Log.d("FlipCoinErr: ","IllegalStateException caught during coin flip - "+e.getMessage());
                        }
                    }
                });
            }
        }).start();

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview =inflater.inflate(R.layout.fragment_pureluck, container, false);

        // Text Views
        toss_cnt_txtview = (TextView) rootview.findViewById(R.id.toss_cnt_textview);
        score_txtview = (TextView) rootview.findViewById(R.id.score_textview);
        difficulty_txtview = (TextView) rootview.findViewById(R.id.difficulty_textview);

        btn = (Button) rootview.findViewById(R.id.btn);
        toss_btn = (Button) rootview.findViewById(R.id.toss_btn);
        heads_btn= (Button) rootview.findViewById(R.id.heads);
        tails_btn= (Button) rootview.findViewById(R.id.tails);
        log_score_btn= (Button) rootview.findViewById(R.id.log_score_btn);
        clear_btn= (Button) rootview.findViewById(R.id.clear_btn);

        resetScoreData();
        resetGameUI();

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flip_cnt+=1;
                if(RANDOM.nextFloat() > 0.5f )
                {
                    Toast.makeText(getContext(), "Is THIS your lucky side?", Toast.LENGTH_SHORT).show();
                }
                else if(RANDOM.nextFloat() < 0.5f )
                {
                    Toast.makeText(getContext(), "OR, Is THIS your lucky side?", Toast.LENGTH_SHORT).show();
                }
                flipCard();
                updateGameUI();
            }
        });

        // TOSS
        toss_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // UPDATE GAME COUNTS

                if(RANDOM.nextFloat() > 0.5f )
                {
                    // LAND HEADS
                    if(mShowingBack)
                       flipCard();
                }
                else
                {
                    //LAND TAILS
                    if(!mShowingBack)
                        flipCard();
                }
                new CountDownTimer(3000, 100) {
                    public void onTick(long millisUntilFinished) {
                        flipCard();
                    }
                    public void onFinish() {
                        updateGameState();
                        // Reset both buttons to blue
                        updateGameUI();
                    }
                }.start();
            }
        });
        heads_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!current_guess.equals("heads")){
                    current_guess="heads";
                    Toast.makeText(getContext(), "Current Guess: "+ current_guess, Toast.LENGTH_SHORT).show();
                    view.setBackgroundColor( getResources().getColor(R.color.maroon));
                    tails_btn.setBackgroundColor( getResources().getColor(R.color.duke_blue));
                }else{
                    current_guess="";
                    Toast.makeText(getContext(), "No Current Guess", Toast.LENGTH_SHORT).show();
                    view.setBackgroundColor( getResources().getColor(R.color.duke_blue));
                }

            }
        });
        tails_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!current_guess.equals("tails")){
                    current_guess="tails";
                    Toast.makeText(getContext(), "Current Guess: "+ current_guess, Toast.LENGTH_SHORT).show();
                    view.setBackgroundColor( getResources().getColor(R.color.maroon));
                    heads_btn.setBackgroundColor( getResources().getColor(R.color.duke_blue));
                }else{
                    current_guess="";
                    Toast.makeText(getContext(), "No Current Guess", Toast.LENGTH_SHORT).show();
                    view.setBackgroundColor( getResources().getColor(R.color.duke_blue));
                }
            }
        });

        log_score_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(difficulty_reached.toLowerCase().equals("unranked")){
                    Toast.makeText(getContext(), "Guess Attempts Too Low To Save.", Toast.LENGTH_SHORT).show();
                }else
                    {
                    Toast.makeText(getContext(), "Logging Score Data.", Toast.LENGTH_SHORT).show();
                    logScoreData();
                }
                resetScoreData();
                resetGameUI();
            }
        });

        clear_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "This Will Not Save Score Data", Toast.LENGTH_SHORT).show();
                resetScoreData();
                resetGameUI();

            }
        });
        return rootview;
    }

    public void logScoreData()
    {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference newScoreRef = database.getReference("HighScores").child(currentUser.getUid())
                .child("pureluck")
                .push();

        String score=(String) Float.toString(percentage);
        ScoreData thisRoundScore = new ScoreData(score, difficulty_reached);
        thisRoundScore.score_details.put("guessed_heads",Float.toString(heads_guess_cnt));
        thisRoundScore.score_details.put("guessed_tails",Float.toString(tails_guess_cnt));
        thisRoundScore.score_details.put("heads_hit",Float.toString(crrct_heads_guess_cnt));
        thisRoundScore.score_details.put("tail_hit",Float.toString(crrct_tails_guess_cnt));
        thisRoundScore.score_details.put("guess_total",Float.toString(guess_cnt));
        thisRoundScore.score_details.put("hits_total",Float.toString(crrct_guess_cnt));

        newScoreRef.setValue(thisRoundScore).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getContext(),"Score Updated", Toast.LENGTH_SHORT).show();
                // Clear space after sending text
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void resetScoreData(){
        difficulty_reached="Unranked";
        percentage=0;
        heads_guess_cnt=0;
        tails_guess_cnt=0;
        guess_cnt=tails_guess_cnt +
                heads_guess_cnt;    // num of times toss is selected WHILE either HEAD or TAILS is selected

        crrct_heads_guess_cnt=0;
        crrct_tails_guess_cnt=0;
        crrct_guess_cnt=crrct_tails_guess_cnt +
                crrct_heads_guess_cnt;  // num of TOTAL CORRECT GUESSES of heads plus tails
    }

    // Rese
    public void resetGameUI(){
        resetScoreData();
        updateGameUI();
        determineDifficultyLevel();
    }

    public void determineDifficultyLevel(){
        if (guess_cnt<10)
        {
            difficulty_reached="Unranked";
        }
        if (guess_cnt>10 && guess_cnt<25)
        {
            difficulty_reached="Beginner";
        }
        else if (guess_cnt<40 && guess_cnt>25)
        {
            difficulty_reached="Intermediate";
        }
        else if (guess_cnt<60 && guess_cnt>40)
        {
            difficulty_reached="Adept";
        }
        else if (guess_cnt>100)
        {
            difficulty_reached="Master";
        }
        setDifficultyColor();
    }

    public void setDifficultyColor(){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                //TODO your background code
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // update UI
                        String current_difficulty=difficulty_reached.toLowerCase();
                        if (current_difficulty.equals("beginner"))
                        {
                            score_txtview.setBackgroundColor(getResources().getColor(R.color.bronze));
                        }
                        else if (current_difficulty.equals("intermediate"))
                        {
                            score_txtview.setBackgroundColor(getResources().getColor(R.color.silver));
                        }
                        else if (current_difficulty.equals("adept"))
                        {
                            score_txtview.setBackgroundColor(getResources().getColor(R.color.gold));
                        }
                        else if (current_difficulty.equals("master"))
                        {
                            score_txtview.setBackgroundColor(getResources().getColor(R.color.platinum));
                        }else
                        {
                            score_txtview.setBackgroundColor(getResources().getColor(R.color.soft_blue));
                        }
                    }
                });
            }
        });
    }

    public void updateGameState(){
        updateGameCounts();
        determineDifficultyLevel();
    }

    public void updateGameCounts(){
        // When user hits toss, what should we update
        toss_cnt +=1;

        if (current_guess.equals("heads"))
        {
            heads_guess_cnt+=1;
            if (!mShowingBack)
            {
                Toast.makeText(getContext(), "CORRECT GUESS ON " + current_guess, Toast.LENGTH_SHORT).show();
                crrct_heads_guess_cnt+=1;
            }

        }else if (current_guess.equals("tails"))
        {
            tails_guess_cnt+=1;
            if (mShowingBack){
                Toast.makeText(getContext(), "CORRECT GUESS ON " + current_guess, Toast.LENGTH_SHORT).show();
                crrct_tails_guess_cnt+=1;
            }
        }
        // reset current guess
        guess_cnt=tails_guess_cnt+heads_guess_cnt;    // num of times toss is selected WHILE either HEAD or TAILS is selected
        crrct_guess_cnt=crrct_tails_guess_cnt +crrct_heads_guess_cnt;
        current_guess="";
        try {

            float luck =(float) crrct_guess_cnt/guess_cnt;
            Log.d("CalcLuck-","Luck - " + percentage);
            Log.d("CalcLuck-","Luck - " + crrct_guess_cnt);
            Log.d("CalcLuck-","Luck - " + guess_cnt);

            if (!Double.isNaN(luck)) {
                percentage = luck * (float) 100.0;
            }
        }catch (ArithmeticException e){

            Log.d("","Arith Error " + e.getMessage() + " Guess count is "+guess_cnt);
        }
    }

    public void updateGameUI(){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                //TODO your background code
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // update UI
                        // When user hits toss, what should we update
                        heads_btn.setBackgroundColor( getResources().getColor(R.color.duke_blue));
                        tails_btn.setBackgroundColor( getResources().getColor(R.color.duke_blue));

                        String tossUpdate = "Toss: "+ toss_cnt+ "\n" +
                                "Flips: "+ flip_cnt;
                        toss_cnt_txtview.setText(tossUpdate);

                        String scoreUpdate="Hits: "+ crrct_guess_cnt + "\n" +
                                "Luck: "+ percentage+ "\n" +
                                "Level: "+ difficulty_reached;
                        score_txtview.setText(scoreUpdate);

                        String difficultyUpdate ="Heads: "+crrct_heads_guess_cnt +"|"+heads_guess_cnt+"\n"
                                +"Tails: "+crrct_tails_guess_cnt +"|"+tails_guess_cnt+"\n"
                                +"Guesses: "+ guess_cnt;
                        difficulty_txtview.setText(difficultyUpdate);
                    }
                });
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();

        //Store scorce to bundle
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        //SAVE SCORE
    }
}