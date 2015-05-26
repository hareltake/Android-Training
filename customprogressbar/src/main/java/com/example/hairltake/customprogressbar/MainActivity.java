package com.example.hairltake.customprogressbar;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;

import java.util.Random;


public class MainActivity extends ActionBarActivity {

    private final Random random = new Random();

    private GoalProgressBar progressBar;
    private Button button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar = (GoalProgressBar) findViewById(R.id.progressBar);
        button = (Button) findViewById(R.id.resetProgressBtn);
        progressBar.setGoal(70);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int prog = random.nextInt(100);
                progressBar.setProgress(prog);
            }
        });
    }


}
