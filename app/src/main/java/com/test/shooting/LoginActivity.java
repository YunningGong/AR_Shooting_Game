package com.test.shooting;

import android.content.Intent;
import android.os.Bundle;

import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.DoubleBounce;
import com.github.ybq.android.spinkit.style.FadingCircle;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {


    private EditText emailId, password;
    private Button btnSignIn;
    private TextView tvSignUp;
    FirebaseAuth mFirebaseAuth;
    ProgressBar progressBar;

    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mFirebaseAuth=FirebaseAuth.getInstance();
        emailId=findViewById(R.id.login_email);
        password=findViewById(R.id.login_password);
        btnSignIn=findViewById(R.id.btnSignIn);
        tvSignUp=findViewById(R.id.tvSignUp);

        //loading view
        progressBar = (ProgressBar)findViewById(R.id.progress);
        Sprite fadingCircle = new FadingCircle();
        progressBar.setIndeterminateDrawable(fadingCircle);

        mAuthStateListener=new FirebaseAuth.AuthStateListener() {
            FirebaseUser mFirebaseUser=mFirebaseAuth.getCurrentUser();
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (mFirebaseUser!=null){
                    Toast.makeText(LoginActivity.this,"You are logged in", Toast.LENGTH_LONG).show();
                    Intent i=new Intent(LoginActivity.this, StartGameActivity.class);
                    startActivity(i);

                }
                else{
                    Toast.makeText(LoginActivity.this,"please login", Toast.LENGTH_LONG).show();
                }
            }
        };

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email=emailId.getText().toString();
                String pwd=password.getText().toString();
                if (email.isEmpty()){
                    emailId.setError("please enter email id");
                    emailId.requestFocus();
                }
                else if (pwd.isEmpty()){
                    password.setError("please enter your password");
                    password.requestFocus();

                }
                else if (email.isEmpty()&&pwd.isEmpty()){
                    Toast.makeText(LoginActivity.this,"Fields Are Empty!",Toast.LENGTH_SHORT).show();

                }
                else if (!(email.isEmpty()&&pwd.isEmpty())){
                    progressBar.setVisibility(View.VISIBLE);
                    mFirebaseAuth.signInWithEmailAndPassword(email,pwd).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()){
                                Toast.makeText(LoginActivity.this,"login error, please try again",Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.INVISIBLE);


                            }
                            else{
                                Toast.makeText(LoginActivity.this,"You are logged in", Toast.LENGTH_LONG).show();
                                Intent intToHome=new Intent(LoginActivity.this,StartGameActivity.class);
                                startActivity(intToHome);
                                finish();
                            }
                        }
                    });
                }
                else{
                    Toast.makeText(LoginActivity.this,"Error!",Toast.LENGTH_SHORT).show();
                }
            }
        });

        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intSignUp=new Intent(LoginActivity.this,SignupActivity.class);
                startActivity(intSignUp);
                finish();
            }
        });
    }

    @Override
    protected void onStart(){
        super.onStart();
//        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

}
