package edu.univdhaka.cse2216.myplane.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.univdhaka.cse2216.myplane.R;

public class SplashScreen extends AppCompatActivity {
    private ProgressBar progressBar;
    private int progess ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);
        progressBar=findViewById(R.id.progessBarId);
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                doWork();
                startApp();
            }
        });
        thread.start();
    }
    public void doWork(){
        for(progess=20;progess<=100;progess+=20){
            try {
                Thread.sleep(500);
                progressBar.setProgress(progess);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public void startApp()
    {
            Intent intent=new Intent(SplashScreen.this, MainActivity.class);
            startActivity(intent);
            finish();
    }
}
