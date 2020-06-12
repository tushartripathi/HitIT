package com.vubird.hitit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class viewPost extends AppCompatActivity {

    private ListView listView;
    private ArrayList<String> username;
    private ArrayAdapter arrayAdapter;
    private FirebaseAuth mAuth;
    private ImageView disimageView;
    private ArrayList<DataSnapshot> dataSnapshotsArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_post);

        mAuth = FirebaseAuth.getInstance();
        dataSnapshotsArrayList = new ArrayList<>();
        listView = findViewById(R.id.sentListview);
        disimageView = findViewById(R.id.Displayimageview);
        username = new ArrayList<>();
        arrayAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,username);
        listView.setAdapter(arrayAdapter);
        getSentImage();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                disimageView.setVisibility(View.VISIBLE);


                DataSnapshot myDataSnapShot = dataSnapshotsArrayList.get(i);
                String dLink = myDataSnapShot.child("imageLink").getValue().toString();

                Picasso.get().load(dLink).into(disimageView);






            }
        });

    }

    public void getSentImage()
    {

        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Getting Data");
        progressDialog.show();
        FirebaseDatabase.getInstance().getReference().child("my_users").child(mAuth.getCurrentUser().getUid()).child("received_posts").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
            {
                if(dataSnapshot.hasChildren()) {
                    dataSnapshotsArrayList.add(dataSnapshot);
                    String fromWhomeUsername = (String) dataSnapshot.child("fromWhom").getValue();
                    username.add(fromWhomeUsername);
                    arrayAdapter.notifyDataSetChanged();
                }
                else
                {
                    Toast.makeText(viewPost.this,"found", Toast.LENGTH_SHORT).show();


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

            }


        });
        progressDialog.dismiss();

    }
}
