package com.example.sukrit.blogapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class PostActivity extends AppCompatActivity {

    public static final int GALLERY_REQUEST=1;
    private ImageButton mImageSelect;
    private EditText mPostTitle;
    private EditText mPostDesc;
    private Button mSubmitBtn;
    public Uri mImageUri=null;
    private StorageReference mStorage;
    private ProgressDialog mProgressDialog;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private DatabaseReference mDatabaseUser;
    public static int constant2=2000;
    private Toolbar mToolbar;
    boolean var=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        var=false;

        //Toolbar
        mToolbar= (Toolbar) findViewById(R.id.post_appBar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Post Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        constant2=1;
        mAuth=FirebaseAuth.getInstance();
        mCurrentUser=mAuth.getCurrentUser();
        mDatabase= FirebaseDatabase.getInstance().getReference().child("Blog");
        mDatabaseUser=FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid());

        mDatabase.keepSynced(true);
        mDatabaseUser.keepSynced(true);

        mProgressDialog=new ProgressDialog(this);
        mStorage= FirebaseStorage.getInstance().getReference();

        mPostTitle= (EditText) findViewById(R.id.titleField);
        mPostDesc= (EditText) findViewById(R.id.descField);
        mSubmitBtn= (Button) findViewById(R.id.submitBtn);

        mImageSelect= (ImageButton) findViewById(R.id.ImageSelect);
        mImageSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent=new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,GALLERY_REQUEST);
                galleryIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                Log.d("GGGG","IMMMAAGGE");
                //finish();
            }
        });

        mSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPosting();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GALLERY_REQUEST && resultCode==RESULT_OK)
        {
            mImageUri=data.getData();
           // mImageSelect.setImageURI(mImageUri);
            CropImage.activity(mImageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(this);
            Log.d("GGGG",mImageUri.toString());
            Log.d("GGGG","RESULT_OK");
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                mImageSelect.setImageURI(resultUri);
                Log.d("GGGG","CROP_IMAGE");
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    @Override
    public void onBackPressed() {
        Log.i("WWWW","Method chal gya: Post");
        Intent back_intent=new Intent(PostActivity.this,MainActivity.class);
        startActivity(back_intent);
        back_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finish();
    }

    private void startPosting()
    {
        mProgressDialog.setMessage("Uploading...");

        final String title_val=mPostTitle.getText().toString().trim();
        final String desc_val=mPostDesc.getText().toString().trim();
        Log.i("TAG", "startPosting: title: "+title_val);
        Log.i("TAG", "startPosting: Description: "+desc_val);
        if(!TextUtils.isEmpty(title_val) && !TextUtils.isEmpty(desc_val) && (mImageUri!=null))
        {
            mProgressDialog.show();
            StorageReference filepath=mStorage.child("Blog_Images").child(mImageUri.getLastPathSegment());
            Log.i("TAG", "startPosting: Filepath: "+filepath.toString());

            filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    @SuppressWarnings("VisibleForTests") final
                    Uri downloadURL=taskSnapshot.getDownloadUrl();
                    Log.d("TAGS", "onSuccess: "+downloadURL.toString());
                    final DatabaseReference newPost=mDatabase.push();
                    final DatabaseReference newUserPost=FirebaseDatabase.getInstance()
                                                    .getReference().child("Users")
                                                    .child(mCurrentUser.getUid()).child("Posts").push();

                    /*to retrieve the names of Users*/

                    /*to send the data to server*/
                    mDatabaseUser.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            newPost.child("likesCount").setValue(0);
                            newPost.child("title").setValue(title_val);
                            newPost.child("desc").setValue(desc_val);
                            newPost.child("image").setValue(downloadURL.toString());
                            newPost.child("uid").setValue(mCurrentUser.getUid());
                            newPost.child("username").setValue(dataSnapshot.child("name").getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                    {
                                        Intent main_intent=new Intent(PostActivity.this, MainActivity.class);
                                        startActivity(main_intent);
                                        main_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        finish();
                                    }
                                    else {
                                        Toast.makeText(PostActivity.this, "Error in posting....", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            if(dataSnapshot.hasChild("postsCount") && Long.parseLong(dataSnapshot.child("postsCount").getValue().toString())>0)
                            {
                                if(var==false){
                                    mDatabaseUser.child("postsCount").setValue((Long.parseLong(dataSnapshot.child("postsCount").getValue().toString()))+1);
                                    var=true;
                                }
                                var=true;
                            }
                            else {
                                mDatabaseUser.child("postsCount").setValue(1);
                                var=false;
                            }
                            newUserPost.child("likesCount").setValue(0);
                            newUserPost.child("title").setValue(title_val);
                            newUserPost.child("desc").setValue(desc_val);
                            newUserPost.child("image").setValue(downloadURL.toString());
                            newUserPost.child("uid").setValue(mCurrentUser.getUid());
                            newUserPost.child("username").setValue(dataSnapshot.child("name").getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                    {
                                        Intent main_intent=new Intent(PostActivity.this, MainActivity.class);
                                        startActivity(main_intent);
                                        main_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        finish();
                                    }
                                    else {
                                        Toast.makeText(PostActivity.this, "Error in posting....", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }


                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    mProgressDialog.dismiss();
                    Toast.makeText(PostActivity.this,"Done Uploading...!",Toast.LENGTH_LONG).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    mProgressDialog.dismiss();
                    Toast.makeText(PostActivity.this,"Error occured while uploading",Toast.LENGTH_LONG).show();
                }
            });
        }
        else {
            mProgressDialog.dismiss();
            Toast.makeText(PostActivity.this,"Some details missing..!",Toast.LENGTH_LONG).show();
        }
    }


}
