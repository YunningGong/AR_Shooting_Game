package com.test.shooting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.sql.Array;

public class LeaderboardActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private static final String TAG = "LeaderboardActivity";
    TextView top[] = new TextView[10];
    String[] data_hard = new String[10];
    String[] data_normal = new String[10];
    String[] data_easy = new String[10];



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        // Setting for Navigation Bar
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        showeasy();


//        ButterKnife.bind(this);


    }
    // Click listener for choosing different navigation tabs
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_easy: {
                    Log.d(TAG, "TESTTEST easy: ");
                    showeasy();

                    return true;
                }
                case R.id.navigation_normal: {
                    Log.d(TAG, "TESTTEST normal: ");
                    shownormal();
                    return true;
                }
                case R.id.navigation_hard: {
                    Log.d(TAG, "TESTTEST hard: ");
                    showhard();
                    return true;
                }
            }
            return false;
        }
    };

    public void showeasy(){
        db = FirebaseFirestore.getInstance();

        db.collection("Users")
                .orderBy("easy")
                .limit(10)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int i = 0;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                UserList userList = document.toObject(UserList.class);
                                if (i == 9){
                                    data_easy[i] = "Top"+(i+1)+":   "+document.getId() + "    " + userList.getEasy()+"    "+userList.getEasycity();
                                }else {
                                    data_easy[i] = "Top"+(i+1)+"  :   "+document.getId() + "    " + userList.getEasy()+"    "+userList.getEasycity();
                                }
                                i++;
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(LeaderboardActivity.this, android.R.layout.simple_list_item_1, data_easy);
                        ListView listView = (ListView)findViewById(R.id.list_view);
                        listView.setAdapter(adapter);

                    }
                });
    }

    public void shownormal(){
        db = FirebaseFirestore.getInstance();

        db.collection("Users")
                .orderBy("normal")
                .limit(10)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int i = 0;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                UserList userList = document.toObject(UserList.class);
                                if (i == 9){
                                    data_normal[i] = "Top"+(i+1)+":   "+document.getId() + "    " + userList.getNormal()+"    "+userList.getNormalcity();
                                }else {
                                    data_normal[i] = "Top"+(i+1)+"  :   "+document.getId() + "    " + userList.getNormal()+"    "+userList.getNormalcity();
                                }
                                i++;
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(LeaderboardActivity.this, android.R.layout.simple_list_item_1, data_normal);
                        ListView listView = (ListView)findViewById(R.id.list_view);
                        listView.setAdapter(adapter);

                    }
                });
    }

    public void showhard(){
        db = FirebaseFirestore.getInstance();

        db.collection("Users")
                .orderBy("hard")
                .limit(10)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int i = 0;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                UserList userList = document.toObject(UserList.class);
                                if (i == 9){
                                    data_hard[i] = "Top"+(i+1)+":   "+document.getId() + "    " + userList.getHard()+"    "+userList.getHardcity();
                                }else {
                                    data_hard[i] = "Top"+(i+1)+"  :   "+document.getId() + "    " + userList.getHard()+"    "+userList.getHardcity();
                                }
                                i++;
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(LeaderboardActivity.this, android.R.layout.simple_list_item_1, data_hard);
                        ListView listView = (ListView)findViewById(R.id.list_view);
                        listView.setAdapter(adapter);

                    }
                });
    }


    /**
     * user to write and read data with firebase
     */
    public static class UserList {
        private String easy, easycity,hard,hardcity,normal,normalcity;

        public UserList() {}

        public UserList(String easy, String easycity, String hard,String hardcity,String normal,String normalcity) {
            this.easy = easy;
            this.easycity = easycity;
            this.hard = hard;
            this.hardcity = hardcity;
            this.normal = normal;
            this.normalcity = normalcity;


        }
        public String getEasy() {return easy;}
        public String getEasycity() {return easycity;}

        public String getHard() {return hard;}
        public String getHardcity() {return hardcity;}

        public String getNormal() {return normal;}
        public String getNormalcity() {return normalcity;}


    }
}
