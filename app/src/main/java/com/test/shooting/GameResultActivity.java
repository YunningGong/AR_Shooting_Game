package com.test.shooting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.reflect.Array;
import java.util.List;
import java.util.Locale;

public class GameResultActivity extends AppCompatActivity {

    private TextView timeDisplay,BesttimeDisplay,ModeDisplay,LocationDisplay;
    private Button back;
    private Button restart;
    private FirebaseFirestore db;
    private static final String TAG = "GameResultActivity";
    FirebaseAuth mFirebaseAuth;
    private String best_time;
    private String best_city;

    double latitude = 0.0;
    double longtitude = 0.0;
    private String location_city = null;
    Location gps_loc = null, network_loc = null, final_loc = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_result);

        db = FirebaseFirestore.getInstance();
        mFirebaseAuth=FirebaseAuth.getInstance();

        timeDisplay=findViewById(R.id.time);
        BesttimeDisplay = findViewById(R.id.besttime);
        restart=findViewById(R.id.restart);
        back=findViewById(R.id.back);
        ModeDisplay=findViewById(R.id.mode);
        LocationDisplay=findViewById(R.id.location);

        //get the time value from shooting activity
        Intent intent=getIntent();
        String gametime=intent.getStringExtra("GameTime");
        String gamemode=intent.getStringExtra("Mode");
        timeDisplay.setText(transformTime(gametime));
        ModeDisplay.setText(gamemode);

        //gps, get the location
        String city = GPS_location();

        //read the time from user database and get the best time. Then write the best time
        readvalue(gametime,gamemode,city);


        back.setOnClickListener(view -> {
//            Intent inToMain=new Intent(GameResultActivity.this,StartGameActivity.class);
//            startActivity(inToMain);
            finish();
        });

        restart.setOnClickListener(view -> {
            if (gamemode.equals("Easy")){
                Toast.makeText(GameResultActivity.this,"The game started", Toast.LENGTH_LONG).show();
                Intent inToGame=new Intent(GameResultActivity.this,ShootingActivity_easy.class);
                startActivity(inToGame);
                finish();
            }
            else if (gamemode.equals("Normal")){
                Toast.makeText(GameResultActivity.this,"The game started", Toast.LENGTH_LONG).show();
                Intent inToGame=new Intent(GameResultActivity.this,ShootingActivity_normal.class);
                startActivity(inToGame);
                finish();
            }
            else if (gamemode.equals("Hard")){
                Toast.makeText(GameResultActivity.this,"The game started", Toast.LENGTH_LONG).show();
                Intent inToGame=new Intent(GameResultActivity.this,ShootingActivity_hard.class);
                startActivity(inToGame);
                finish();
            }
        });
    }

    public String GPS_location(){
        String city = "";
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_NETWORK_STATE)!= PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this,"not granted",Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this,"granted",Toast.LENGTH_SHORT).show();

        }
        try{
            gps_loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            network_loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }catch (Exception e){
            e.printStackTrace();
        }
        if (gps_loc!=null){
            final_loc = gps_loc;
            latitude = final_loc.getLatitude();
            longtitude = final_loc.getLongitude();
        }
        else if (network_loc!=null){
            final_loc = network_loc;
            latitude = final_loc.getLatitude();
            longtitude = final_loc.getLongitude();
        }
        else {
            latitude = 0.0;
            longtitude = 0.0;
        }
        try{
            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude,longtitude,1);
            if (addresses!=null && addresses.size()>0){
                city = addresses.get(0).getLocality();
//                String address = addresses.get(0).getAddressLine(0);
//                String state = addresses.get(0).getAdminArea();
//                String country = addresses.get(0).getCountryName();
//                String postal_code = addresses.get(0).getPostalCode();
//                String knownName = addresses.get(0).getFeatureName();
                LocationDisplay.setText(city);
                return city;
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return city;
    }

    public void readvalue(String gametime,String gamemode,String location_city){
        //read the time from user database
        db.collection("Users")
                .document(mFirebaseAuth.getCurrentUser().getEmail())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        UserList userList = documentSnapshot.toObject(UserList.class);

                        String Easy = userList.getEasy();
                        String EasyCity = userList.getEasycity();
                        String Hard = userList.getHard();
                        String HardCity = userList.getHardcity();
                        String Normal = userList.getNormal();
                        String NormalCity = userList.getNormalcity();
                        String mode_time = null;
                        String mode_city = null;

                        if (gamemode.equals("Easy")){
                            mode_time = Easy;
                            mode_city = EasyCity;
                        }else if (gamemode.equals("Hard")){
                            mode_time = Hard;
                            mode_city = HardCity;

                        }else if (gamemode.equals("Normal")){
                            mode_time = Normal;
                            mode_city = NormalCity;
                        }


                        if (mode_time.isEmpty()){
                            best_time = gametime;
                            best_city = location_city;
//                            Log.d(TAG, "besttime0: "+ best_time);
                            if (gamemode.equals("Easy")){
                                writevalue(best_time,best_city,Hard,HardCity,Normal,NormalCity);
                            }else if (gamemode.equals("Hard")){
                                writevalue(Easy,EasyCity,best_time,best_city,Normal,NormalCity);
                            }else if (gamemode.equals("Normal")){
                                writevalue(Easy,EasyCity,Hard,HardCity,best_time,best_city);
                            }
                            BesttimeDisplay.setText(transformTime(best_time));


                        }else {
                            best_time = getBestTime(mode_time,gametime,mode_city,location_city)[0];
                            best_city = getBestTime(mode_time,gametime,mode_city,location_city)[1];

                            Log.d(TAG, "TESTTEST besttime1: "+ best_time);
                            Log.d(TAG, "TESTTEST bestcity1: "+ best_city);


                            Log.d(TAG, "TESTTEST Easy: "+ Easy);
                            Log.d(TAG, "TESTTEST EasyCity: "+ EasyCity);

                            Log.d(TAG, "TESTTEST Hard: "+ Hard);
                            Log.d(TAG, "TESTTEST HardCity: "+ HardCity);

                            Log.d(TAG, "TESTTEST Normal: "+ Normal);
                            Log.d(TAG, "TESTTEST NormalCity: "+ NormalCity);

                            if (gamemode.equals("Easy")){
                                writevalue(best_time,best_city,Hard,HardCity,Normal,NormalCity);
                            }else if (gamemode.equals("Hard")){
                                writevalue(Easy,EasyCity,best_time,best_city,Normal,NormalCity);
                            }else if (gamemode.equals("Normal")){
                                writevalue(Easy,EasyCity,Hard,HardCity,best_time,best_city);
                            }
                            BesttimeDisplay.setText(transformTime(best_time));

                        }
                    }
                });
    }

    public void writevalue(String Easy,String EasyCity,String Hard,String HardCity, String Normal, String NormalCity){
//        Log.d(TAG, "besttime2: "+ best_time);
        UserList userList = new UserList(Easy,EasyCity,Hard,HardCity,Normal,NormalCity);
        db.collection("Users")
                .document(mFirebaseAuth.getCurrentUser().getEmail())
                .set(userList)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(GameResultActivity.this, "Successful!", Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String error = e.getMessage();
                        Toast.makeText(GameResultActivity.this, "Error"+error, Toast.LENGTH_SHORT).show();

                    }
                });
    }

    public String[] getBestTime(String best_time, String thistime,String best_City,String thiscity){
        String result_time = "";
        String result_city = "";
        String[] result = new String[2];

        String[] best_arrary = best_time.split(":");
        String[] thistime_arrary = thistime.split(":");

//        Log.d(TAG, "best_arrary0: "+ Integer.parseInt(best_arrary[0]));
//        Log.d(TAG, "best_arrary1: "+ Integer.parseInt(best_arrary[1]));
//
//        Log.d(TAG, "thisgame_arrary0: "+ Integer.parseInt(thisgame_arrary[0]));
//        Log.d(TAG, "thisgame_arrary1: "+ Integer.parseInt(thisgame_arrary[1]));


        if (Integer.parseInt(best_arrary[0]) < Integer.parseInt(thistime_arrary[0])){
            result_time = best_time;
            result_city = best_City;


        }else if (Integer.parseInt(best_arrary[0]) > Integer.parseInt(thistime_arrary[0])){
            result_time = thistime;
            result_city = thiscity;

        }else if (Integer.parseInt(best_arrary[0]) == Integer.parseInt(thistime_arrary[0])){
            if (Integer.parseInt(best_arrary[1]) <= Integer.parseInt(thistime_arrary[1])){
                result_time = best_time;
                result_city = best_City;

            }else if (Integer.parseInt(best_arrary[1]) > Integer.parseInt(thistime_arrary[1])){
                result_time = thistime;
                result_city = thiscity;

            }
        }
//        Log.d(TAG, "result: "+ result);
        result[0] = result_time;
        result[1] = result_city;
        return result;
    }

    public String transformTime(String time){
        int m,s;

        if (time.isEmpty()){
            return "error";
        }else {
            String[] time_arrary = time.split(":");
            m = Integer.parseInt(time_arrary[0]);
            s = Integer.parseInt(time_arrary[1]);

            return m+" min "+ s +"s";
        }

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




