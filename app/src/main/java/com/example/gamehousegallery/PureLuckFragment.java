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

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    FlippableFragment headFlippable;
    FlippableFragment tailsFlipable;
    FragmentManager parentFragManager;
    boolean mShowingBack;

    private int toss_cnt;
    private int guess_cnt;
    private int crrct_guess_cnt;
    private String current_guess;
    private boolean isGuessing;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        parentFragManager = getParentFragmentManager();

        toss_cnt=0;
        guess_cnt=0;
        crrct_guess_cnt=0;
        current_guess = "";
        isGuessing=false;

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

        btn = (Button) rootview.findViewById(R.id.btn);
        toss_btn = (Button) rootview.findViewById(R.id.toss_btn);
        heads_btn= (Button) rootview.findViewById(R.id.heads);
        tails_btn= (Button) rootview.findViewById(R.id.tails);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flipCard();
            }
        });
        toss_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toss_cnt +=1;

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
                        heads_btn.setBackgroundColor( getResources().getColor(R.color.duke_blue));
                        tails_btn.setBackgroundColor( getResources().getColor(R.color.duke_blue));
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