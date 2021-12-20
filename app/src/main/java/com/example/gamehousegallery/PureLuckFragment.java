package com.example.gamehousegallery;

import android.os.Bundle;

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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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
    private TextView toss_txtview;


    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    FlippableFragment headFlippable;
    FlippableFragment tailsFlipable;
    FragmentManager parentFragManager;
    boolean mShowingBack;


    private int heads_guess_cnt;
    private int tails_guess_cnt;
    private int guess_cnt;

    private int crrct_heads_guess_cnt;
    private int crrct_tails_guess_cnt;
    private int crrct_guess_cnt;

    private int toss_cnt;
    private String current_guess;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        parentFragManager = getParentFragmentManager();

        resetScoreData();


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

            parentFragManager.beginTransaction()
                    .add(R.id.container, headFlippable)
                    .commit();
        }
    }



    private void flipCard() {
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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview =inflater.inflate(R.layout.fragment_pureluck, container, false);


//        TextView toss_cnt_txtview;
//        TextView score_txtview;
//        TextView toss_txtview;

        // Text Views
        toss_cnt_txtview = (TextView) rootview.findViewById(R.id.toss_cnt_textview);
        score_txtview = (TextView) rootview.findViewById(R.id.score_textview);
        difficulty_txtview = (TextView) rootview.findViewById(R.id.difficulty_textview);

        resetGameUI();

        btn = (Button) rootview.findViewById(R.id.btn);
        toss_btn = (Button) rootview.findViewById(R.id.toss_btn);
        heads_btn= (Button) rootview.findViewById(R.id.heads);
        tails_btn= (Button) rootview.findViewById(R.id.tails);
        log_score_btn= (Button) rootview.findViewById(R.id.log_score_btn);
        clear_btn= (Button) rootview.findViewById(R.id.clear_btn);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(RANDOM.nextFloat() > 0.5f )
                {
                    Toast.makeText(getContext(), "Is THIS your lucky side?", Toast.LENGTH_SHORT).show();
                }
                else if(RANDOM.nextFloat() < 0.5f )
                {
                    Toast.makeText(getContext(), "OR, Is THIS your lucky side?", Toast.LENGTH_SHORT).show();
                }

                flipCard();
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(RANDOM.nextFloat() > 0.5f )
                {
                    Toast.makeText(getContext(), "Is THIS your lucky side?", Toast.LENGTH_SHORT).show();
                }
                else if(RANDOM.nextFloat() < 0.5f )
                {
                    Toast.makeText(getContext(), "OR, Is THIS your lucky side?", Toast.LENGTH_SHORT).show();
                }

                flipCard();
            }
        });
        // TOSS
        toss_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // UPDATE GAME COUNTS
                updateGameState();

                new CountDownTimer(3000, 100) {
                    public void onTick(long millisUntilFinished) {
                        flipCard();
                    }

                    public void onFinish() {
                        if(RANDOM.nextFloat() > 0.5f ){
                            if(!mShowingBack)
                                flipCard();
                        }else {
                            if(mShowingBack)
                                flipCard();

                        }

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
        return rootview;
    }

    public void logScoreData(){

    }

    public void resetScoreData(){
        heads_guess_cnt=0;
        tails_guess_cnt=0;
        guess_cnt=tails_guess_cnt +
                heads_guess_cnt;    // num of times toss is selected WHILE either HEAD or TAILS is selected

        crrct_heads_guess_cnt=0;
        crrct_tails_guess_cnt=0;
        crrct_guess_cnt=crrct_tails_guess_cnt +
                crrct_heads_guess_cnt;  // num of TOTAL CORRECT GUESSES of heads plus tails
    }

    public void resetGameUI(){
        String tossUpdate = "Toss"+ toss_cnt;
        toss_cnt_txtview.setText(tossUpdate);

        String scoreUpdate="Score:"+ crrct_guess_cnt + " / " + guess_cnt;
        score_txtview.setText(scoreUpdate);

        String difficultyUpdate ="Heads: "+heads_guess_cnt+"|"+crrct_heads_guess_cnt+"\n"
                +"Tails: "+tails_guess_cnt+"|"+crrct_tails_guess_cnt;
        difficulty_txtview.setText(difficultyUpdate);
    }

    public void updateGameState(){
        // When user hits toss, what should we update
        toss_cnt +=1;

        if (current_guess.equals("heads"))
        {
            heads_guess_cnt+=1;
            if (!mShowingBack){
                Toast.makeText(getContext(), "CORRECT GUESS ON " + current_guess, Toast.LENGTH_SHORT).show();
                crrct_heads_guess_cnt+=1;
            }

        }else if (current_guess.equals("tails"))
        {
            tails_guess_cnt+=1;
            if (mShowingBack){
                Toast.makeText(getContext(), "CORRECT GUESS ON " + current_guess, Toast.LENGTH_SHORT).show();
                tails_guess_cnt+=1;
            }
        }
        // reset current guess
        guess_cnt=tails_guess_cnt +heads_guess_cnt;    // num of times toss is selected WHILE either HEAD or TAILS is selected
        crrct_guess_cnt=crrct_tails_guess_cnt +crrct_heads_guess_cnt;
        current_guess="";
    }

    public void updateGameUI(){
        // When user hits toss, what should we update
        heads_btn.setBackgroundColor( getResources().getColor(R.color.duke_blue));
        tails_btn.setBackgroundColor( getResources().getColor(R.color.duke_blue));

        String tossUpdate = "Toss: "+ toss_cnt;
        toss_cnt_txtview.setText(tossUpdate);

        String scoreUpdate="Score: "+ crrct_guess_cnt + " / " + guess_cnt;
        score_txtview.setText(scoreUpdate);

        String difficultyUpdate ="Heads: "+heads_guess_cnt+"|"+crrct_heads_guess_cnt+"\n"
                +"Tails: "+tails_guess_cnt+"|"+crrct_tails_guess_cnt;
        difficulty_txtview.setText(difficultyUpdate);
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