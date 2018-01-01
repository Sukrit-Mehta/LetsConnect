package com.example.sukrit.blogapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class  SingleBlogActivity extends AppCompatActivity {

    private ImageView mSingleBlogImage;
    private TextView mSingleBlogTitle;
    private TextView mSingleBlogDesription;
    private String postUserId;
    private Button mRemovePostBtn;
    private DatabaseReference mDatabaseBlog;
    private DatabaseReference mDatabaseLikes;
    private DatabaseReference mUsersPosts;
    private FirebaseAuth mAuth;
    private int position;
    public static int constant=1000;
    private Toolbar mToolbar;
    FirebaseUser mCurrentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_blog);

        mSingleBlogImage= (ImageView) findViewById(R.id.singleBlogImage);
        mSingleBlogTitle= (TextView) findViewById(R.id.singleBlogTitle);
        mSingleBlogDesription= (TextView) findViewById(R.id.singleBlogDescription);
        mRemovePostBtn= (Button) findViewById(R.id.removePostBtn);

        mCurrentUser=FirebaseAuth.getInstance().getCurrentUser();
        String uid=mCurrentUser.getUid();

        final String post_key=getIntent().getExtras().getString("blog_id");

        mDatabaseBlog= FirebaseDatabase.getInstance().getReference().child("Blog");
        mDatabaseLikes= FirebaseDatabase.getInstance().getReference().child("Likes");
        Log.d("YYYY","post key:" +post_key);
        mUsersPosts=FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Posts");

        mDatabaseBlog.keepSynced(true);
        mDatabaseLikes.keepSynced(true);
        mUsersPosts.keepSynced(true);

        mAuth=FirebaseAuth.getInstance();

        //Toolbar
        mToolbar= (Toolbar) findViewById(R.id.singleBlog_appBar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Single Blog");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        position=getIntent().getIntExtra("position",0);
        constant=position;
      //  Toast.makeText(this, "Position: "+String.valueOf(position), Toast.LENGTH_SHORT).show();
        mDatabaseBlog.child(post_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String postImage= (String) dataSnapshot.child("image").getValue();
                String postTitle= (String) dataSnapshot.child("title").getValue();
                String postDesc= (String) dataSnapshot.child("desc").getValue();
                //Long value=(Long)dataSnapshot.child("likesCount").getValue();
                //Toast.makeText(SingleBlogActivity.this, String.valueOf(value), Toast.LENGTH_SHORT).show();
                postUserId= (String) dataSnapshot.child("uid").getValue();
                mSingleBlogTitle.setText(postTitle);
                mSingleBlogDesription.setText(postDesc);
                Picasso.with(SingleBlogActivity.this).load(postImage).into(mSingleBlogImage);
                if(mAuth.getCurrentUser().getUid().equals(postUserId))
                {
                    mRemovePostBtn.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
            mRemovePostBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mUsersPosts.child(post_key).removeValue();
                    mDatabaseBlog.child(post_key).removeValue();
                    mDatabaseLikes.child(post_key).removeValue();
                    Log.d("RRRR", "Remove: : " +post_key);
                    Intent mainIntent=new Intent(SingleBlogActivity.this, MainActivity.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    finish();
                    startActivity(mainIntent);
                    Toast.makeText(SingleBlogActivity.this, "Post Deleted Successfully..!", Toast.LENGTH_SHORT).show();
                }
            });

        //Toast.makeText(this, post_key, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        Log.i("WWWW","Method chal gya: SingleBlog");
        startActivity(new Intent(SingleBlogActivity.this,MainActivity.class));
    }

}
