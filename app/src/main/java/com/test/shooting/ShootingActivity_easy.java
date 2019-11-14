package com.test.shooting;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Point;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.view.Display;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.ar.sceneform.Camera;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.collision.Ray;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.MaterialFactory;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.ShapeFactory;
import com.google.ar.sceneform.rendering.Texture;

import java.util.ArrayList;
import java.util.Random;

public class ShootingActivity_easy extends AppCompatActivity {

    private Scene scene;
    private Camera camera;
    private ModelRenderable bulletRenderable;
    private boolean shouldStartTimer=true;
    private int balloonsLeft=20;
    private Point point;
    private TextView balloonleftTxt;
    private SoundPool soundPool;
    private int sound;
    private int airGun;
    private int birdSound;
    protected String timeInfo;
    //private ModelAnimator modelAnimator;
    private int i=0;
    private ImageView gunImage;
    private String Mode;
    private Button giveUpe;
    private Boolean stopThread = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shooting_easy);

        gunImage=(ImageView)findViewById(R.id.gun);
        //gunImage.setImageResource(R.drawable.gun);
        //gunImage.setImageBitmap(BitmapFactory.decodeFile("drawable/gun.jpg"));
        Display display=getWindowManager().getDefaultDisplay();
        point=new Point();
        display.getRealSize(point);

        loadSoundPool();
        balloonleftTxt=findViewById(R.id.balloonsCntTxt);

        CustomArFragment arFragment=
                (CustomArFragment) getSupportFragmentManager().findFragmentById(R.id.arFragment);

        scene=arFragment.getArSceneView().getScene();
        camera=scene.getCamera();

        addBalloonsToScene();
        //addMoveToScene();
        buildBulletModel();

        Button shoot=findViewById(R.id.shootButton);

        giveUpe=findViewById(R.id.giveUp);
        giveUpe.setOnClickListener(view -> {
            Intent backToMain=new Intent(ShootingActivity_easy.this,StartGameActivity.class);
            stopThread = false;
            startActivity(backToMain);
            finish();
        });





        shoot.setOnClickListener(v-> {

            if(shouldStartTimer){
                startTimer();
                shouldStartTimer=false;
            }

            shoot();

        });



    }

    private void loadSoundPool() {

        AudioAttributes audioAttributes=new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_GAME)
                .build();

        soundPool=new SoundPool.Builder()
                .setMaxStreams(1)
                .setAudioAttributes(audioAttributes)
                .build();

        sound=soundPool.load(this,R.raw.blop_sound,1);
        airGun=soundPool.load(this,R.raw.air_gun_shot,1);
        birdSound=soundPool.load(this,R.raw.plane,1);



    }

    private void shoot(){

        Ray ray=camera.screenPointToRay(point.x/2f,point.y/2f);
        Node node=new Node();
        node.setRenderable(bulletRenderable);
        scene.addChild(node);
        soundPool.play(airGun,1f,1f,1,0,1f);

        new Thread(()->{

            for (int i=0; i<200; i++){

                int finalI1 = i;
                runOnUiThread(()->{

                    Vector3 vector3=ray.getPoint(finalI1 *0.1f);
                    node.setWorldPosition(vector3);

                    Node nodeInContact=scene.overlapTest(node);

                    if (nodeInContact!=null){

                        balloonsLeft--;
                        balloonleftTxt.setText("Monsters Left:"+balloonsLeft);
                        scene.removeChild(nodeInContact);

                        soundPool.play(sound,1f,1f,1,0,1f);


                    }


                });
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            runOnUiThread(()->scene.removeChild(node));


        }).start();

    }

    private void startTimer(){
        TextView timer=findViewById(R.id.timerText);
        new Thread(()->{
            int seconds=0;
            int minitesPassed=0;
            int secondsPassed=0;

            while (balloonsLeft>0){

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                seconds++;
                int currentMinitesPassed = seconds/60;
                int currentSecondsPassed=seconds%60;

                runOnUiThread(()->timer.setText(currentMinitesPassed +":"+currentSecondsPassed));

                minitesPassed=currentMinitesPassed;
                secondsPassed=currentSecondsPassed;
            }
            //Toast.makeText(ShootingActivity.this,"Congrats!",Toast.LENGTH_SHORT).show();
            Intent inToleader=new Intent(ShootingActivity_easy.this,GameResultActivity.class);
            timeInfo = transform(minitesPassed,secondsPassed);
            inToleader.putExtra("GameTime",timeInfo);
            Mode="Easy";
            inToleader.putExtra("Mode",Mode);
            stopThread = false;
            startActivity(inToleader);
            finish();
        }).start();
    }

    private void buildBulletModel() {

        Texture
                .builder()
                .setSource(this, R.drawable.texture)
                .build()
                .thenAccept(texture -> {
                    MaterialFactory
                            .makeOpaqueWithTexture(this, texture)
                            .thenAccept(material -> {
                                bulletRenderable= ShapeFactory
                                        .makeSphere(0.01f,
                                                new Vector3(0f,0f,0f)
                                                ,material);
                            });
                });
    }

    private void addBalloonsToScene() {
        ModelRenderable
                .builder()
                .setSource(this, Uri.parse("plane.sfb"))
                .build()
                .thenAccept(renderable ->{
                    ArrayList<Node> nodes=new ArrayList<>(20);
                    for (int i=0; i<20; i++){
                        nodes.add(i,new Node());


                        nodes.get(i).setRenderable(renderable);
                        scene.addChild(nodes.get(i));


                        Random random=new Random();
                        int x=random.nextInt(10);
                        int z=random.nextInt(10);
                        int y=random.nextInt(20);

                        z=-z;

                        nodes.get(i).setWorldPosition(new Vector3(
                                (float)x,
                                y/10f,
                                (float)z
                        ));
                        nodes.get(i).setWorldRotation(Quaternion.axisAngle(new Vector3(0, 1, 0), 180));



                    }
                    new Thread(()->{
                        while(stopThread){

                            Random time=new Random();
                            try {
                                Thread.sleep(time.nextInt(17000));
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            soundPool.play(birdSound,1f,1f,2,0,1f);


                        }


                    }).start();


                });
    }

    public String transform(int minitesPassed, int secondsPassed){
        int m,s;
        String minites, seconds;
        m = (""+minitesPassed).length();
        s = (""+secondsPassed).length();

        if (m == 1 && s != 1){
            minites = "0"+minitesPassed;
            return minites+":"+secondsPassed;
        }
        else if (m != 1 && s == 1){
            seconds = "0"+secondsPassed;
            return minitesPassed+":"+seconds;
        }
        else if (m == 1 && s == 1){
            minites = "0"+minitesPassed;
            seconds = "0"+secondsPassed;
            return minites+":"+seconds;
        }else {
            return minitesPassed+":"+secondsPassed;

        }


    }

}

