package com.example.sukrit.blogapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {

    RecyclerView mBlogList;
    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseUsers;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabaseLikes;
    private boolean isLiked=false;
    private DatabaseReference mDatabaseCurrentUser;
    private DatabaseReference mDatabaseLikesCount;
    private Query mQueryCurrentUser;
    boolean variable;
    private Button doubleBtn;
    boolean gone=false;
    private Toolbar mToolbar;
    int i=0;
    int ctx=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth=FirebaseAuth.getInstance();

        mToolbar= (Toolbar) findViewById(R.id.main_app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Main Activity");

//            clkBtn= (Button) findViewById(R.id.clkBtn);
//            clkBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mBlogList.smoothScrollToPosition(13);
//            }
//        });


//        scrollView= (ScrollView) findViewById(R.id.scrollView);
        //scrollView.fullScroll(ScrollView.FOCUS_UP);
        mAuthListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()==null)
                {
                    Intent loginIntent=new Intent(MainActivity.this,LoginActivity.class);
                   // loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                   // finish();
                    startActivity(loginIntent);
                }
            }
        };
//        Log.i("TAG","auth: "+mAuth.getCurrentUser().getUid().toString());

        /*For profile ....*/
//        if(mAuth.getCurrentUser()!=null) {
//            String currentUserId = mAuth.getCurrentUser().getUid().toString();
//            mDatabaseCurrentUser = FirebaseDatabase.getInstance().getReference().child("Blog");
//            mQueryCurrentUser = mDatabaseCurrentUser.orderByChild("uid").equalTo(currentUserId);
//        }

        mDatabaseLikesCount=FirebaseDatabase.getInstance().getReference().child("Blog");
        mDatabase= FirebaseDatabase.getInstance().getReference().child("Blog");
        mDatabaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        mDatabaseLikes=FirebaseDatabase.getInstance().getReference().child("Likes");

        mDatabase.keepSynced(true);
        mDatabaseUsers.keepSynced(true);
        mDatabaseLikes.keepSynced(true);
        mDatabaseLikesCount.keepSynced(true);
        mBlogList= (RecyclerView) findViewById(R.id.blog_post);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);

        //mBlogList.smoothScrollToPosition(0);

        /*To reverse the list use the bottom two lines*/
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

        mBlogList.setHasFixedSize(true);
        mBlogList.setLayoutManager(layoutManager);
        checkUserExists();

        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                int count=mBlogList.getAdapter().getItemCount();
                //Toast.makeText(MainActivity.this, "Moved"+String.valueOf(count), Toast.LENGTH_SHORT).show();
               // mBlogList.smoothScrollToPosition(count-1);

            }
        },2000);
        Log.i("WWWW",String.valueOf(SingleBlogActivity.constant));
        if(PostActivity.constant2!=2000)
        {
            ScrollToPosition2();
            PostActivity.constant2=2000;
        }
        else if(SingleBlogActivity.constant!=1000)
        {
            ScrollToPosition();
        }
        else
        {

        }

    }


    void ScrollToPosition()
    {
        Handler handler1=new Handler();
        handler1.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Toast.makeText(MainActivity.this, "Moved"+String.valueOf(count), Toast.LENGTH_SHORT).show();
                mBlogList.smoothScrollToPosition(SingleBlogActivity.constant);

            }
        },2000);
    }

    void ScrollToPosition2()
    {
        Handler handler2=new Handler();
        handler2.postDelayed(new Runnable() {
            @Override
            public void run() {
                int count2=mBlogList.getAdapter().getItemCount();
                //Toast.makeText(MainActivity.this, "Moved"+String.valueOf(count), Toast.LENGTH_SHORT).show();
               // mBlogList.smoothScrollToPosition(count2-1);

            }
        },1000);
    }

    @Override
    public void onBackPressed() {
        Log.i("WWWW","Method chal gya: Main");
        SingleBlogActivity.constant=1000;
        PostActivity.constant2=2000;
        System.exit(0);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthListener);
        FirebaseRecyclerAdapter<Blog,BlogViewHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<Blog, BlogViewHolder>(
                Blog.class,
                R.layout.blog_row,
                BlogViewHolder.class,
                mDatabase

        ) {
            @Override
            protected void populateViewHolder(final BlogViewHolder viewHolder, Blog model, final int position) {
                final String post_key=getRef(position).getKey();
                viewHolder.setTitle(model.getTitle());
                viewHolder.setDesc(model.getDesc());
                viewHolder.setImage(getApplicationContext(),model.getImage());
                viewHolder.setUsername(model.getUsername());
                viewHolder.setLikesCount(model.getLikesCount());
                ctx++;
                Log.i("RRRR", "populateViewHolder: "+String.valueOf(ctx));
                mDatabaseLikes.keepSynced(true);
//                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Intent singleBlogIntent=new Intent(MainActivity.this,SingleBlogActivity.class);
//                        singleBlogIntent.putExtra("blog_id",post_key);
//                        startActivity(singleBlogIntent);
//                    }
//                });
                viewHolder.setLikeBtn(post_key);



                viewHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        i++;
                        isLiked=true;
                        Handler handler=new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if(i==2) {
                                    mDatabaseLikes.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                            if (isLiked) {
                                                if (dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())) {
                                                    mDatabaseLikes.child(post_key).child(mAuth.getCurrentUser().getUid()).removeValue();
                                                    onClickBtn(post_key, 0, true);
                                                    viewHolder.heart.setVisibility(View.VISIBLE);
                                                    isLiked = false;
                                                } else {
                                                    mDatabaseLikes.child(post_key).child(mAuth.getCurrentUser().getUid()).setValue("Random Value");
                                                    onClickBtn(post_key, 1, true);
                                                    viewHolder.heart.setImageResource(R.mipmap.heart);
                                                    viewHolder.heart.setVisibility(View.VISIBLE);

                                             /* To blur the image
                                                viewHolder.mImageView.buildDrawingCache();
                                                Bitmap bitmap=viewHolder.mImageView.getDrawingCache();
                                                Bitmap blurredBitmap = BlurBuilder.blur( MainActivity.this, bitmap );
                                                viewHolder.mImageView.setBackgroundDrawable( new BitmapDrawable
                                                        ( getResources(), blurredBitmap ) );
                                                        */
                                                    isLiked = false;
                                                }
                                                gone=true;
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                }
                                else {
                                            if(gone==false){
                                            Intent singleBlogIntent=new Intent(MainActivity.this,SingleBlogActivity.class);
                                            singleBlogIntent.putExtra("blog_id",post_key);
                                                singleBlogIntent.putExtra("position",position);
                                                singleBlogIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                finish();
                                            startActivity(singleBlogIntent);
                                            }

                                }
                                i=0;gone=false;
                                viewHolder.heart.setVisibility(View.INVISIBLE);
                            }
                        },500);
                        viewHolder.heart.setVisibility(View.INVISIBLE);

                    }
                });


                viewHolder.mLikeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        isLiked=true;
                        mDatabaseLikes.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (isLiked) {
                                            if (dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())) {
                                                mDatabaseLikes.child(post_key).child(mAuth.getCurrentUser().getUid()).removeValue();
                                                onClickBtn(post_key,0,true);
                                                isLiked = false;
                                            } else {
                                                mDatabaseLikes.child(post_key).child(mAuth.getCurrentUser().getUid()).setValue("Random Value");
                                                onClickBtn(post_key,1,true);

                                                isLiked = false;
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                                i=0;


                    }
                });


            }
        };
        mBlogList.setAdapter(firebaseRecyclerAdapter);

    }

    private void onClickBtn(String post_key, int k , final boolean var)
    {
        final int t;final String key;
        t=k;key=post_key;variable=var;
        mDatabaseLikesCount.child(post_key).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(variable && dataSnapshot!=null) {
                    if (t == 0) {
                        Long val = 0L;//
                        val = (Long) dataSnapshot.child("likesCount").getValue();
                        val = val - 1;
                        Log.i("GHG", "Unliked:" + String.valueOf(val));
                        //Log.i("GHG","Unliked:"+String.valueOf(val));

                        mDatabaseLikesCount.child(key).child("likesCount").setValue(val);
                        variable = false;
                        return;
                    } else {
                        Long val = 0L;
                        if(dataSnapshot.hasChild("likesCount")) {
                            val = (Long) dataSnapshot.child("likesCount").getValue();
                            val = val + 1;

                            Log.i("GHG", "Liked:" + String.valueOf(val));
                            mDatabaseLikesCount.child(key).child("likesCount").setValue(val);

                            // mDatabaseLikesCount.child(key).child("likesCount").setValue(val+1);
                            variable = false;
                            return;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        variable=true;
    }

    private void checkUserExists() {
        if (mAuth.getCurrentUser() != null) {
            final String user_id = mAuth.getCurrentUser().getUid();

            mDatabaseUsers.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.hasChild(user_id)) {
                        Intent setupIntent = new Intent(MainActivity.this, SetupActivity.class);
                        setupIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        finish();
                        startActivity(setupIntent);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

    }



    public static class BlogViewHolder extends RecyclerView.ViewHolder{
        View mView;
        ImageButton mLikeBtn;
        ImageView mImageView;
        DatabaseReference mDatabaseLikes;
        FirebaseAuth mAuth;
        LinearLayout linearLayout;
        ImageView heart;

        public BlogViewHolder(View itemView) {
            super(itemView);
            mView=itemView;
            heart= (ImageView) itemView.findViewById(R.id.heart);
            mLikeBtn= (ImageButton) itemView.findViewById(R.id.likeBtn);
            mImageView= (ImageView) itemView.findViewById(R.id.post_image);
            linearLayout= (LinearLayout) itemView.findViewById(R.id.linearLayout);
            mDatabaseLikes=FirebaseDatabase.getInstance().getReference().child("Likes");
            mAuth=FirebaseAuth.getInstance();
        }
        public void setLikeBtn(final String post_key) {
            if (mAuth.getCurrentUser() != null) {

                mDatabaseLikes.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child(post_key).hasChild(mAuth.getCurrentUser().getUid())) {
                            mLikeBtn.setImageResource(R.mipmap.heart);
                        } else {
                            mLikeBtn.setImageResource(R.mipmap.unlike_image);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }
        public void setTitle(String title)
        {
            TextView post_title=(TextView)mView.findViewById(R.id.post_title);
            post_title.setText(title);
        }


        public void setDesc(String desc)
        {
            TextView post_desc=(TextView) mView.findViewById(R.id.post_desc);
            post_desc.setText(desc);
        }
        public void setUsername(String username) {
            TextView post_username = (TextView) mView.findViewById(R.id.post_username);
            post_username.setText(username);
        }

        public void setLikesCount(Long likes)
        {
            TextView post_likes= (TextView) mView.findViewById(R.id.post_likes_count);
            if(likes==0)
            {
                post_likes.setText("");
            }
            else if(likes==1)
            {
                post_likes.setText(likes.toString()+" like");
            }else {
                post_likes.setText(likes.toString()+" likes");
            }
            post_likes.setTextSize(16);
        }

        public void setImage(final Context ctx, final String image)
        {
            final ImageView post_image= (ImageView) mView.findViewById(R.id.post_image);
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



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.action_add)
        {
            Intent post_intent= new Intent(MainActivity.this , PostActivity.class);
            startActivity(post_intent);
           // post_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
          //  finish();
        }
        if(item.getItemId()==R.id.action_logout)
        {
            mAuth.signOut();
        }
        if(item.getItemId()==R.id.profileBtn)
        {
            Intent profile_intent= new Intent(MainActivity.this , ProfileActivity.class);
            startActivity(profile_intent);
            //profile_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
           // finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
