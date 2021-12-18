package com.example.gamehousegallery;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HighScoreEntryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HighScoreEntryFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HighScoreEntryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HighScoreEntryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HighScoreEntryFragment newInstance(String param1, String param2) {
        HighScoreEntryFragment fragment = new HighScoreEntryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =inflater.inflate(R.layout.fragment_high_score_entry, container, false);
        Bundle args = getArguments();

        //high_score_grid_row

        GridLayout highScoreGridLayout = rootView.findViewById(R.id.high_score_grid_row);

        TextView rank_textview = (TextView) rootView.findViewById(R.id.rank);
        rank_textview.setText(args.getString("rank"));

        TextView difficulty_textview = (TextView) rootView.findViewById(R.id.difficulty);
        difficulty_textview.setText(args.getString("difficulty"));

        TextView score_textview = (TextView) rootView.findViewById(R.id.score);
        score_textview.setText(args.getString("score"));

        TextView date_of_score_textview = (TextView) rootView.findViewById(R.id.date_of_score);
        date_of_score_textview.setText(args.getString("date_of_score"));

        return rootView;
    }
}