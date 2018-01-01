package com.example.sukrit.blogapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private EditText mNameField;
    private EditText mEmailField;
    private EditText mPasswordField;
    private Button mRegisterBtn;
    private ProgressDialog mProgress;
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener mListener;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mProgress=new ProgressDialog(RegisterActivity.this);
        auth=FirebaseAuth.getInstance();
        mDatabase= FirebaseDatabase.getInstance().getReference().child("Users");

        mNameField= (EditText) findViewById(R.id.nameField);
        mEmailField= (EditText) findViewById(R.id.emailField);
        mPasswordField= (EditText) findViewById(R.id.passwordField);
        mRegisterBtn= (Button) findViewById(R.id.registerBtn);

        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRegister();
            }
        });
    }

    private void startRegister() {
        final String name=mNameField.getText().toString().trim();
        final String email=mEmailField.getText().toString().trim();
        final String password=mPasswordField.getText().toString().trim();

        Log.i("TAG","Name:"+name);
        Log.i("TAG","Email:"+email);
        Log.i("TAG","Password:"+password);
        if(!TextUtils.isEmpty(name)&&!TextUtils.isEmpty(email)&&!TextUtils.isEmpty(password))
        {
            Log.i("TAG","Name:"+name);
            Log.i("TAG","Email:"+email);
            Log.i("TAG","Password:"+password);
            mProgress.setMessage("Signing...");
            mProgress.show();
            auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()) {
                        String user_id = auth.getCurrentUser().getUid();

                        DatabaseReference current_user_db = mDatabase.child(user_id);
                        current_user_db.child("name").setValue(name);
                        current_user_db.child("image").setValue("default");

                        Toast.makeText(RegisterActivity.this, "Completed signup...", Toast.LENGTH_LONG).show();
                        mProgress.dismiss();
                        Toast.makeText(RegisterActivity.this, "You need to setup an account", Toast.LENGTH_SHORT).show();
                        Intent setupIntent = new Intent(RegisterActivity.this, SetupActivity.class);
                        setupIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        finish();
                        startActivity(setupIntent);

//                        Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
//                        startActivity(mainIntent);
                    }
                    else {
                        mProgress.dismiss();
                        Toast.makeText(RegisterActivity.this,"Error........",Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }
}
