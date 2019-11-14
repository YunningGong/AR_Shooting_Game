package com.test.shooting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.FadingCircle;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    private EditText emailId, password;
    private Button btnSignUp;
    private TextView tvLogin;
    FirebaseAuth mFirebaseAuth;
    private FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //delete login activity
        db = FirebaseFirestore.getInstance();

        mFirebaseAuth=FirebaseAuth.getInstance();
        emailId=findViewById(R.id.signup_email);
        password=findViewById(R.id.signup_password);
        btnSignUp=findViewById(R.id.btnSignUp);
        tvLogin=findViewById(R.id.tvLogin);
        ProgressBar progressBar;

        //loading view
        progressBar = (ProgressBar)findViewById(R.id.progress);
        Sprite fadingCircle = new FadingCircle();
        progressBar.setIndeterminateDrawable(fadingCircle);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email=emailId.getText().toString();
                String pwd=password.getText().toString();
                if (email.isEmpty()){
                    emailId.setError("Please enter email");
                    emailId.requestFocus();
                }
                else if (pwd.isEmpty()){
                    password.setError("Please enter password");
                    password.requestFocus();

                }
                else if (email.isEmpty()&&pwd.isEmpty()){
                    Toast.makeText(SignupActivity.this,"Fields Are Empty!",Toast.LENGTH_SHORT).show();

                }
                else if (!(email.isEmpty()&&pwd.isEmpty())){
                    progressBar.setVisibility(View.VISIBLE);
                    mFirebaseAuth.createUserWithEmailAndPassword(email,pwd).addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()){
                                Toast.makeText(SignupActivity.this,"SignUp Unsuccessful, Please Try Again!",Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.INVISIBLE);

                            }else {
                                // after create the account, add the profile value to database
                                Map<String, Object> user = new HashMap<>();
                                user.put("easy", "99:60");
                                user.put("easycity", "None");
                                user.put("hard", "99:60");
                                user.put("hardcity", "None");
                                user.put("normal", "99:60");
                                user.put("normalcity", "None");

                                // Add a new document with a generated ID
                                db.collection("Users")
                                        .document(email)
                                        .set(user)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(SignupActivity.this, "Successful!", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(SignupActivity.this,StartGameActivity.class));
                                                finish();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                String error = e.getMessage();
                                                Toast.makeText(SignupActivity.this, "Error"+error, Toast.LENGTH_SHORT).show();

                                            }
                                        });
                            }
                        }
                    });
                }
                else{
                    Toast.makeText(SignupActivity.this,"Error!",Toast.LENGTH_SHORT).show();
                }
            }
        });


        tvLogin.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v){
                Intent i=new Intent(SignupActivity.this,LoginActivity.class);
                startActivity(i);
                finish();
            }
        });



    }
}
