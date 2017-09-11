package me.didik.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import me.didik.jugger.Jugger;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Jugger.with(this)
                .setPositiveTextColor("#3F51B5")
                .setNegativeTextColor("#FF4081")
                .check();
    }
}
