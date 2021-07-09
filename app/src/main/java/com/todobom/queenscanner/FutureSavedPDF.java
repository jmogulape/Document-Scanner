package com.todobom.queenscanner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.todobom.queenscanner.Data.BlogRecyclerAdapter;
import com.todobom.queenscanner.Data.PdfAdapter;
import com.todobom.queenscanner.Model.FutureModel;

import java.util.ArrayList;
import java.util.List;

public class FutureSavedPDF extends AppCompatActivity implements PdfAdapter.OnPDFclickListener{

    private DatabaseReference mDatabaseReference;
    private FirebaseDatabase mDatabase;
    private RecyclerView recyclerView;
    private FirebaseUser mUser;
    private FirebaseAuth mAuth;
    private PdfAdapter blogRecyclerAdapter;
    List<String> mylist = new ArrayList<String>();
    List<String> mylist2  = new ArrayList<String>();
    List<String> dateList  = new ArrayList<String>();
    String str;
    String []  array;
    PdfAdapter.OnPDFclickListener onPDFclickListener = this;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_future_saved_p_d_f);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        String mEmail;
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        mEmail = ""+currentUser.getEmail();
        int i = mEmail.indexOf(".");


        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mDatabase.getReference(""+mEmail.substring(0, i)).child("pdf");
        mDatabaseReference.keepSynced(true);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewEd_pdf);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        onStartt();

    }



    protected void onStartt() {

        mDatabaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                str = dataSnapshot.getValue().toString();
                String date = dataSnapshot.getKey().toString();

                dateList.add(date.substring(0,10));

                str = dataSnapshot.getValue().toString();
                Log.d("data","dataaa: snapshot "+str);

                str = str.replace('{',' ');
                str = str.replace('}',' ');
                Log.d("data","dataaa: snapshot "+str);

                array = str.split(",");
                Log.d("data","dataaa: snapshot "+array.length);
                Log.d("data","dataaa: snapshot "+str);

                String pdfName;

                for (int i=0 ; i<array.length; i++){

//                    Log.d("data","dataaa: "+array[i]);
//                    Log.d("data","dat length: "+array[i].length());
//                    Log.d("data","dat index: "+array[i].indexOf('='));
                    int first = array[i].indexOf('=');
                    int last = array[i].length();
                    String string = array[i];
                    String string2 = array[i];

                    string = string.substring(++first,last);
                    pdfName = string2.substring(0,--first);

                    mylist.add(string);
                    mylist2.add(pdfName);

                    Log.d("data","dat index: "+ array[i]);
                    Log.d("data","dat name: "+ pdfName);

                }

                blogRecyclerAdapter = new PdfAdapter(FutureSavedPDF.this, dateList, mylist2, onPDFclickListener  );
//                Toast.makeText(PostListActivity.this,""+blogRecyclerAdapter,Toast.LENGTH_LONG).show();
                recyclerView.setAdapter(blogRecyclerAdapter);
                blogRecyclerAdapter.notifyDataSetChanged();

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
    }


    @Override
    public void onPdfclick(int position) {

        Log.d("position","position"+position);

        String s = mylist.get(position);
        String pdfName = mylist2.get(position);

        Toast.makeText(this, "Diverting to Download... ", Toast.LENGTH_SHORT).show();

        Log.d("position","position"+ s);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(""+s));
        startActivity(intent);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                blogRecyclerAdapter.getFilter().filter(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
}

