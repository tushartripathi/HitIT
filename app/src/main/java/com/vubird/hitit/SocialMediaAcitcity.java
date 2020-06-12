package com.vubird.hitit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ThrowOnExtraProperties;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class SocialMediaAcitcity extends AppCompatActivity
{
    FirebaseAuth mAuth;
    ImageView imageView;
    EditText descText;
    ListView usersListView;
    Bitmap bitmap;
    String ImageIdentifier;
    ArrayList<String> usernames;
    ArrayAdapter arrayAdapter;
    String imageDownloadLink;
    String CurrentUserName;
    private  ArrayList<String> uids;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_media_acitcity);
        mAuth = FirebaseAuth.getInstance();
        imageView = findViewById(R.id.imageView);
        descText = findViewById(R.id.descEditText);
        usersListView = findViewById(R.id.AllusersListView);
        usernames = new ArrayList();
        arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, usernames);
        usersListView.setAdapter(arrayAdapter);
        uids =new ArrayList();
        Toast.makeText(this,mAuth.getCurrentUser().getDisplayName(),Toast.LENGTH_SHORT).show();

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                selectImage();
            }
        });

        usersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                Toast.makeText(SocialMediaAcitcity.this ,"User = " + FirebaseAuth.getInstance().getCurrentUser().getDisplayName(),Toast.LENGTH_SHORT).show();
                HashMap<String,String> dataMap = new HashMap<>();
                dataMap.put("fromWhom", FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                dataMap.put("imageIdentifier",ImageIdentifier);
                dataMap.put("imageLink",imageDownloadLink);
                dataMap.put("des",descText.getText().toString());
                FirebaseDatabase.getInstance().getReference().child("my_users").child(uids.get(i)).child("received_posts").push().setValue(dataMap);
                Toast.makeText(SocialMediaAcitcity.this,"done............",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void CreatePost(View view)
    {
        shareImageToserver();
    }

    public void selectImage()
    {
        if(Build.VERSION.SDK_INT >= 23 && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},1000);
        }
        else
        {
            getChoosenImage();
        }
    }

    private void getChoosenImage()
    {
        Intent i = new Intent(Intent.ACTION_PICK , MediaStore.Images.Media.EXTERNAL_CONTENT_URI );
        startActivityForResult(i,2000);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==1000)
        {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                getChoosenImage();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==2000)
        {
            if(resultCode == Activity.RESULT_OK )
            {
                Uri choosenImge = data.getData();
                try
                {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),choosenImge);
                    imageView.setImageBitmap(bitmap);
                }
                catch (Exception e)
                { }
            }
        }
    }

    void shareImageToserver()
    {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploding Image");
        progressDialog.show();
        if (bitmap != null)
        {
            imageView.setDrawingCacheEnabled(true);
            imageView.buildDrawingCache();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] data = baos.toByteArray();
            ImageIdentifier = UUID.randomUUID().toString()+".png";
            UploadTask uploadTask = FirebaseStorage.getInstance().getReference().child("mu_imgages").child(ImageIdentifier).putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener()
            {
                @Override
                public void onFailure(@NonNull Exception exception)
                {
                    // Handle unsuccessful uploads
                    Toast.makeText(SocialMediaAcitcity.this,exception.toString(),Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>()
            {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                    Toast.makeText(SocialMediaAcitcity.this,"Success",Toast.LENGTH_SHORT).show();
                    descText.setVisibility(View.VISIBLE);
                    FirebaseDatabase.getInstance().getReference().child("my_users").addChildEventListener(new ChildEventListener()
                    {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
                        {
                            uids.add(dataSnapshot.getKey());
                            String username =(String)dataSnapshot.child("username").getValue();
                            Toast.makeText(SocialMediaAcitcity.this, username , Toast.LENGTH_SHORT).show();
                            usernames.add(username);
                            arrayAdapter.notifyDataSetChanged();
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

                    taskSnapshot.getMetadata().getReference().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if(task.isSuccessful())
                            {
                                imageDownloadLink = task.getResult().toString();
                            }
                        }
                    });
                }
            });
            progressDialog.dismiss();
        }

    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.usermenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.logoutItem:
                 logOut();
                 break;

            case R.id.viewPost:
                Intent i = new Intent(this, viewPost.class);
                startActivity(i);
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();

    }

    private void logOut()
    {
        mAuth.signOut();
        finish();
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);

    }


}
