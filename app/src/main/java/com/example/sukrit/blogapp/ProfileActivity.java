package com.example.sukrit.blogapp;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    Button mProfileBtn;
    TextView mPostsCount;
    TextView mFriendsCount;
    RecyclerView mProfilePostsList;
    CircleImageView mProfileImage;
    DatabaseReference mUsersDatabase;
    DatabaseReference mUsersPosts;
    FirebaseUser mCurrentUser;
    TextView mProfileName;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    private Toolbar mToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //Database Reference
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String uid = mCurrentUser.getUid();
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
        mUsersPosts = FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("Posts");
        mUsersPosts.keepSynced(true);
        mUsersDatabase.keepSynced(true);
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    Intent loginIntent = new Intent(ProfileActivity.this, LoginActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    finish();
                    startActivity(loginIntent);
                }
            }
        };

        //Toolbar
        mToolbar= (Toolbar) findViewById(R.id.profile_appBar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("My Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mUsersDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    Long count = Long.parseLong(dataSnapshot.child("postsCount").getValue().toString()) - 1;
                    mPostsCount.setText(count.toString());
                }catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //Android Fields
        mProfileBtn = (Button) findViewById(R.id.profile_edit_profileBtn);
        mPostsCount = (TextView) findViewById(R.id.profile_posts_count);
        mFriendsCount = (TextView) findViewById(R.id.profile_friends_count);
        mProfilePostsList = (RecyclerView) findViewById(R.id.profile_posts_recyclerView);
        mProfileImage = (CircleImageView) findViewById(R.id.profile_image);
        mProfileName = (TextView) findViewById(R.id.profile_user_name);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        /*To reverse the list use the bottom two lines*/
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        mProfilePostsList.setHasFixedSize(true);
        mProfilePostsList.setLayoutManager(linearLayoutManager);
        Picasso.with(ProfileActivity.this).load(R.mipmap.pic).into(mProfileImage);

        mUsersDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();
                mProfileName.setText(name);
                if (image.equals("default")) {
                    Picasso.with(ProfileActivity.this).load(R.mipmap.pic).into(mProfileImage);
                } else {
                    Picasso.with(ProfileActivity.this).load(image).into(mProfileImage);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);
        FirebaseRecyclerAdapter<Blog, ProfileActivity.PostViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Blog, ProfileActivity.PostViewHolder>(
                Blog.class,
                R.layout.post_row,
                ProfileActivity.PostViewHolder.class,
                mUsersPosts

        ) {
            @Override
            protected void populateViewHolder(final ProfileActivity.PostViewHolder viewHolder, Blog model, final int position) {
                viewHolder.setTitle(model.getTitle());
                viewHolder.setDesc(model.getDesc());
                viewHolder.setImage(getApplicationContext(), model.getImage());
                viewHolder.setUsername(model.getUsername());
                mUsersPosts.keepSynced(true);
            }
        };
        mProfilePostsList.setAdapter(firebaseRecyclerAdapter);

    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        View mView;
        ImageButton mLikeBtn;
        ImageView mImageView;
        DatabaseReference mDatabaseLikes;
        FirebaseAuth mAuth;
        LinearLayout linearLayout;
        ImageView heart;

        public PostViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            heart = (ImageView) itemView.findViewById(R.id.heart);
            mLikeBtn = (ImageButton) itemView.findViewById(R.id.likeBtn);
            mImageView = (ImageView) itemView.findViewById(R.id.post_image);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.linearLayout);
            mDatabaseLikes = FirebaseDatabase.getInstance().getReference().child("Likes");
            mAuth = FirebaseAuth.getInstance();
        }


        public void setTitle(String title) {
            TextView post_title = (TextView) mView.findViewById(R.id.post_title);
            post_title.setText(title);
        }


        public void setDesc(String desc) {
            TextView post_desc = (TextView) mView.findViewById(R.id.post_desc);
            post_desc.setText(desc);
        }

        public void setUsername(String username) {
            TextView post_username = (TextView) mView.findViewById(R.id.post_username);
            post_username.setText(username);
        }



        public void setImage(final Context ctx, final String image) {
            final ImageView post_image = (ImageView) mView.findViewById(R.id.post_image);
            //Picasso.with(ctx).load(image).into(post_image);
            Picasso.with(ctx).load(image).networkPolicy(NetworkPolicy.OFFLINE).into(post_image, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    Picasso.with(ctx).load(image).into(post_image);
                }
            });
        }

    }
}