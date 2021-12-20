package com.example.gamehousegallery;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


public class FlippableFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_flippable, container, false);
        Bundle args = getArguments();

        ImageView thisSide = (ImageView) rootView.findViewById(R.id.flipImage);
        thisSide.setImageResource(args.getInt("flip_image",R.drawable.heads));

        return rootView;
    }
}