package com.example.gamehousegallery;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

/**

 * create an instance of this fragment.
 */
public class GameDetailFragment extends Fragment {
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_game_detail, container, false);
        Bundle args = getArguments();

        TextView game_name_textview = (TextView) rootView.findViewById(R.id.game_name_textview);
        game_name_textview.setText(args.getString("game_name"));

        ImageView game_image_imageview = (ImageView) rootView.findViewById(R.id.game_image_imageview);
        StorageReference pathReference = FirebaseStorage.getInstance().getReference("images/"+args.getString("game_image"));
        pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(game_image_imageview);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(rootView.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();

                // Have picasso load in a default image
                // Picasso.get().load(uri).into(game_image_imageview);
            }
        });

        TextView descriptionText = (TextView) rootView.findViewById(R.id.description);
        descriptionText.setText(args.getString("description"));
        descriptionText.setMovementMethod(new ScrollingMovementMethod());

        LinearLayout gameDetailLayout = rootView.findViewById(R.id.game_detail_layout);
        gameDetailLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(v.getContext(),GameHostActivity.class);
                intent.putExtra("game_name",args.getString("game_name"));
                v.getContext().startActivity(intent);
            }
        });

        return rootView;
    }
}
