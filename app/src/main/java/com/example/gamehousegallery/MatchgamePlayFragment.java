package com.example.gamehousegallery;

import android.content.Intent;
import android.os.Bundle;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**

 */
public class MatchgamePlayFragment extends Fragment {

    private static final String ARG_PARAM1 = "difficulty";

    Bundle gameBoard = new Bundle();
    Bundle selectedCard = new Bundle();
    List<Integer> gameArray;
    int clickCnt;
    int matchCnt;
    int columnCnt=3;

    // TODO: Rename and change types of parameters
    private String difficulty;
    GridLayout gridLayout;
    TextView textView;

    WinFragment winFragment;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            difficulty = getArguments().getString(ARG_PARAM1);

            if (difficulty.toLowerCase().equals("easy")){
                columnCnt=3;
                Log.d("DIFFICULTY","Easy " + columnCnt);
            }else if (difficulty.toLowerCase().equals("med")){

                columnCnt=4;
                Log.d("DIFFICULTY","Med " + columnCnt);
            }else if (difficulty.toLowerCase().equals("hard")){

                columnCnt=5;
                Log.d("DIFFICULTY","Hard " + columnCnt);
            }else if (difficulty.toLowerCase().equals("master")){
                columnCnt=6;
                //columnCnt=7;
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_matchgame_play, container, false);

        gridLayout = rootView.findViewById(R.id.rooLayout);
        textView = rootView.findViewById(R.id.clickCnt);

        generateMatchArray();
        clickCnt = 0;
        matchCnt = 0;

//        setContentView(R.layout.fragment_matchgame_play);

        gridLayout.setColumnCount(columnCnt);

        for (int i=0; i<columnCnt*columnCnt; i++){
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    1.0f
            );

            ImageView imageView = new ImageView(getContext());
            imageView.setLayoutParams(param);
            imageView.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.mipmap.cardback, null));
            imageView.setBackground(ResourcesCompat.getDrawable(getResources(), R.mipmap.cardback_round, null));
            // imageView.setBackground(ResourcesCompat.getDrawable(getResources(), R.mipmap.cardback_round, null));
            imageView.setTag(R.mipmap.cardback);
            gridLayout.addView(imageView);
            final int finalI = i;

            if (gameArray.size() <= 0){
                gameBoard.putInt(String.valueOf(i), 0);
            }
            else {
                gameBoard.putInt(String.valueOf(i), getRandomElement());
            }

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Toast.makeText(v.getContext(), "Card Value: "+ getCardValue(finalI), Toast.LENGTH_SHORT).show();
                    ImageView thisView = (ImageView) v;

                    // Card flip animation
                    flipCardImg(thisView, finalI);

                    // UPDATE CLICK COUNT
                    updateClickCnt();

                    new CountDownTimer(450, 450) {
                        public void onTick(long millisUntilFinished) {
                            // WAIT
                        }

                        // UPDATE GAME STATE
                        public void onFinish() {
                            updateGameBoard(finalI, v);
                            checkForWin();
                        }
                    }.start();
                }
            });
        }

        return rootView;
    }

    // Update Click View
    //
    private void updateClickCnt(){

        textView.setText("Clicks: " + Integer.toString(++clickCnt));
    }

    // Run through the step for updating the game board
    //
    private void updateGameBoard(int finalI, View v){

        ImageView thisView = (ImageView) v;
        ImageView matchedView = (ImageView) gridLayout.getChildAt(selectedCard.getInt("CurrentCard"));

        // Set Card as currently selected
        //
        if (selectedCard.get("CurrentCard")==null){
            Log.d("Current Card", "Currently selected card is " + finalI + " With value " + getCardValue(finalI));
            selectedCard.putInt("CurrentCard", finalI);
            return;
        }

        // Check for match since selected and clicked on is different
        //
        if (selectedCard.get("CurrentCard")!=null && selectedCard.getInt("CurrentCard")!=finalI){

            if (checkMatch(finalI)){
                Log.d("Match Made", "Matched Card " + selectedCard.get("CurrentCard") + " With " + finalI);
                thisView.setVisibility(View.INVISIBLE);
                matchedView.setVisibility(View.INVISIBLE);
            }
            else{
                Log.d("Mismatch", "No match made is " + finalI + " Prior card is" + selectedCard.get("CurrentCard"));
                flipCardImg(matchedView, selectedCard.getInt("CurrentCard"));
                flipCardImg(thisView, finalI);
            }
            selectedCard.clear();
        }

        else if (selectedCard.get("CurrentCard")!=null && selectedCard.getInt("CurrentCard")==finalI){
            selectedCard.clear();
        }
        else{
            Log.d("INVALID MOVE", "Something unexpected happened");
        }

    }

    // ADD ANIMATION TO VIEW
    //
    private void animateCard(View thisView){
        thisView.animate();
        RotateAnimation rotate = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(300);
        rotate.setInterpolator(new LinearInterpolator());
        thisView.startAnimation(rotate);
    }

    private void flipCardImg(View v, int viewIndx){
        // If the Cardback is showing
        //
        ImageView thisView = (ImageView) v;
        animateCard(thisView);

        // Check If card is currently showing back logo
        if (((Integer) thisView.getTag()== R.mipmap.cardback)){
            int cardVal = getCardValue(viewIndx);
            Log.d("Flip Image", "Changing drawable in IF statement");
            thisView.setTag(returnObjTag(cardVal));
            thisView.setImageDrawable(ResourcesCompat.getDrawable(getResources(), (Integer) returnObjTag(cardVal), null));
        }
        else{
            Log.d("Flip Image", "Changing drawable in ELSE statement");

            thisView.setTag(R.mipmap.cardback);
            thisView.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.mipmap.cardback, null));
        }
    }

    // Return reference to necessary image number pic
    //
    public Object returnObjTag(int cardVal){
        Object imgTag = R.mipmap.zero;
        switch (cardVal) {
            case 1:  imgTag = R.mipmap.one;
                break;
            case 2:  imgTag = R.mipmap.two;
                break;
            case 3:  imgTag = R.mipmap.three;
                break;
            case 4:  imgTag = R.mipmap.four;
                break;
            case 5:  imgTag = R.mipmap.five;
                break;
            case 6:  imgTag = R.mipmap.six;
                break;
            case 7:  imgTag = R.mipmap.seven;
                break;
            case 8:  imgTag = R.mipmap.eight;
                break;
            case 9:  imgTag = R.mipmap.nine;
                break;
            case 10: imgTag = R.mipmap.ten;
                break;
            case 11: imgTag = R.mipmap.eleven;
                break;
            case 12: imgTag = R.mipmap.twelve;
                break;
            case 13:  imgTag = R.mipmap.thirteen;
                break;
            case 14:  imgTag = R.mipmap.fourteen;
                break;
            case 15:  imgTag = R.mipmap.fifteen;
                break;
            case 16:  imgTag = R.mipmap.sixteen;
                break;
            case 17:  imgTag = R.mipmap.seventeen;
                break;
            case 18: imgTag = R.mipmap.eighteen;
                break;
            case 19: imgTag = R.mipmap.ninteen;
                break;
            case 20: imgTag = R.mipmap.twenty;
                break;
            case 21: imgTag = R.mipmap.twentyone;
                break;
            case 22: imgTag = R.mipmap.twentytwo;
                break;
            case 23:  imgTag = R.mipmap.twentythree;
                break;
            case 24:  imgTag = R.mipmap.twentyfour;
                break;
            case 25: imgTag = R.mipmap.twentyfive;
                break;
            default: imgTag = R.mipmap.zero;
                break;
        }
        return imgTag;
    }

    // Check Game board for win condition
    //
    private void checkForWin(){
        int numPairs = (columnCnt*columnCnt)/2;

        if (matchCnt==numPairs)
        {
            winFragment=new WinFragment();
            Bundle args = new Bundle();
            // WIN FRAGMENT
            FragmentManager manager = getParentFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);

            args.putInt("score",clickCnt);
            args.putString("difficulty",difficulty);
            args.putString("game_name","matchmaker");

            winFragment.setArguments(args);

            transaction.replace(R.id.game_host_fragment,winFragment,"matchgame_play_fragment");
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    // UPDATE MATCH COUNT
    private void updateMatchCnt(){
        textView.setText("Matches: "+Integer.toString(++matchCnt));
    }

    //
    // The int represents the dict key for the card clicked
    //
    private boolean checkMatch(int checkCardIndx){
        int cardVal = getCardValue(checkCardIndx);
        int selectedCardVal = getCardValue(selectedCard.getInt("CurrentCard"));

        if (cardVal == selectedCardVal)
        {
            Log.d("Match Made", "Made match with " + checkCardIndx +" and "+ selectedCard.getInt("CurrentCard"));
            Log.d("Match Values", "Matched on " + cardVal);
            updateMatchCnt();
//            Toast.makeText(getContext(), "Matched Card "
//                    + checkCardIndx + " With " + selectedCard.getInt("CurrentCard")
//                    + " On Number "+cardVal, Toast.LENGTH_SHORT).show();
            return true;
        }
        else
        {
            // clear current selected card, and turn both cards back over
//            Log.d("No Match Made", "Did Not Match Card " + selectedCard.get("CurrentCard") + " With " + checkCardIndx);
//            Log.d("Reset Selected Card", "Cards Reset");
//            Toast.makeText(getContext(), "No Match with " + checkCardIndx + " and " + selectedCard.getInt("CurrentCard") + ". Resetting Pair.", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    // Fill the game array with the number of pairs to be matched
    //
    private void generateMatchArray(){
        int numPairs = (columnCnt*columnCnt)/2;
        gameArray = new ArrayList<>();

        //
        // 0 acts as our 'joker' when row*col is odd
        //
        if ( columnCnt*columnCnt % 2 != 0 )
            gameArray.add(0);

        // Add a Pair of numbers to be taken later for matching
        //
        for (int i=0; i<numPairs; i++){
            gameArray.add(i+1);
            gameArray.add(i+1);
        }
    }

    // Function select an element base on index and return an element
    //
    private int getRandomElement()
    {
        Random rand = new Random();
        Log.d("Game Array Size", "Here: " + gameArray.size());
        int randIndx = rand.nextInt(gameArray.size());
        int value = gameArray.get(randIndx);
        gameArray.remove(randIndx);
        return value;
    }

    // Check the gameboard for the card value assigned at game start
    //
    private int getCardValue(int checkCardIndx){
        return gameBoard.getInt(String.valueOf(checkCardIndx));
    }

}