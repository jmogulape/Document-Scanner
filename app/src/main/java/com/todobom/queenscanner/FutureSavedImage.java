package com.todobom.queenscanner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.todobom.queenscanner.Data.BlogRecyclerAdapter;
import com.todobom.queenscanner.Model.FutureModel;

import java.util.ArrayList;
import java.util.List;

public class FutureSavedImage extends AppCompatActivity implements BlogRecyclerAdapter.OnImageclickListener {

    private DatabaseReference mDatabaseReference;
    private FirebaseDatabase mDatabase;
    private RecyclerView recyclerView;
    private FirebaseUser mUser;
    private FirebaseAuth mAuth;
    private List<FutureModel> blogList;
    private BlogRecyclerAdapter blogRecyclerAdapter;
    List<String> mylist= new ArrayList<String>();
    String str;
    String []  array;
    List<String> dateList  = new ArrayList<String>();
    BlogRecyclerAdapter.OnImageclickListener onImageclickListener = this;
    private ShimmerFrameLayout mShimmerViewContainer;
    private final int SPLASH_DISPLAY_LENGTH = 4000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_future_saved_image);

        mShimmerViewContainer = findViewById(R.id.shimmer_view_container);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        String mEmail;
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        mEmail = ""+currentUser.getEmail();
        int i = mEmail.indexOf(".");


        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mDatabase.getReference(""+mEmail.substring(0, i)).child("image");
        mDatabaseReference.keepSynced(true);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewEd);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this,3));

        mShimmerViewContainer.setDuration(500);
        onStartt();
    }


    protected void onStartt() {

        mDatabaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                String date = dataSnapshot.getKey().toString();
//                 date = date.substring(0,10);

                dateList.add(date);

                str = dataSnapshot.getValue().toString();

                str = str.replace('{',' ');
                str = str.replace('}',' ');
                array = str.split(",");


                for (int i=0 ; i<array.length; i++){

//                    Log.d("data","dataaa: "+array[i]);
//                    Log.d("data","dat length: "+array[i].length());
//                    Log.d("data","dat index: "+array[i].indexOf('='));
                    int first = array[i].indexOf('=');
                    int last = array[i].length();
                    String string = array[i];

                    string = string.substring(++first,last);
                    mylist.add(string);
                    Log.d("data","dat index: "+ array[i]);

                    blogRecyclerAdapter = new BlogRecyclerAdapter(FutureSavedImage.this, mylist, dateList , onImageclickListener);
//                Toast.makeText(PostListActivity.this,""+blogRecyclerAdapter,Toast.LENGTH_LONG).show();
                    recyclerView.setAdapter(blogRecyclerAdapter);
                    blogRecyclerAdapter.notifyDataSetChanged();
                }


            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "Error Loading Image"+databaseError, Toast.LENGTH_SHORT).show();
            }
        } );
        mShimmerViewContainer.stopShimmerAnimation();
        mShimmerViewContainer.setVisibility(View.GONE);
    }

    @Override
    public void onImageclick(int position) {

        Toast.makeText(this, "Downloading...", Toast.LENGTH_SHORT).show();
        String s = mylist.get(position);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(""+s));
        startActivity(intent);

    }
}