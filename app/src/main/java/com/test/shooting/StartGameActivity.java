package com.test.shooting;

import android.content.Intent;
import android.os.Bundle;


import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class StartGameActivity extends AppCompatActivity {

    private Button btnLogOut;
    private Button easy,normal,hard;
    private Button btnLeaderboard;
    FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_game);

//        //delete main activity
//        MainActivity.main.finish();

        btnLogOut=findViewById(R.id.logout);
        btnLeaderboard=findViewById(R.id.leaderboard);
        easy = findViewById(R.id.easy);
        normal = findViewById(R.id.normal);
        hard = findViewById(R.id.hard);


        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent inToMain=new Intent(StartGameActivity.this,LoginActivity.class);
                startActivity(inToMain);
                finish();
            }
        });

        btnLeaderboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(StartGameActivity.this,"The game started", Toast.LENGTH_LONG).show();
                Intent i=new Intent(StartGameActivity.this,LeaderboardActivity.class);
                startActivity(i);
                //finish();
            }
        });

        easy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(StartGameActivity.this,"The game started", Toast.LENGTH_LONG).show();
                Intent inToGame=new Intent(StartGameActivity.this, ShootingActivity_easy.class);
                startActivity(inToGame);
                //finish();
            }
        });


        normal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(StartGameActivity.this,"The game started", Toast.LENGTH_LONG).show();
                Intent inToGame=new Intent(StartGameActivity.this, ShootingActivity_normal.class);
                startActivity(inToGame);
                //finish();
            }
        });

        hard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(StartGameActivity.this,"The game started", Toast.LENGTH_LONG).show();
                Intent inToGame=new Intent(StartGameActivity.this, ShootingActivity_hard.class);
                startActivity(inToGame);
                //finish();
            }
        });


    }

}
