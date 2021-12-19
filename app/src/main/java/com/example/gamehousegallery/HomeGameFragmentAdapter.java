package com.example.gamehousegallery;

import android.os.Bundle;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeGameFragmentAdapter extends FragmentStateAdapter {
    private List<String> game_list = null;
    private HashMap<String, GameDataModel> key_to_Post = null;

    public HomeGameFragmentAdapter(HomeGameFragment fa, List<String> game_list, HashMap<String, GameDataModel> key_to_Post) {
        super(fa);
        this.game_list=game_list;
        this.key_to_Post=key_to_Post;
    }

    @NonNull
    @Override
    public Fragment createFragment(int i) {
        Fragment fragment = new GameDetailFragment();
        Bundle args = new Bundle();

        args.putString("game_name", key_to_Post.get(game_list.get(i)).game_name);
        args.putString("game_image", key_to_Post.get(game_list.get(i)).game_image);
        args.putString("description", key_to_Post.get(game_list.get(i)).description);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getItemCount() {
        return game_list.size();
    }
}