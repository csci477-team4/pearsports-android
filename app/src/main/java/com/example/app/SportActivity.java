package com.example.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

public class SportActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sport);

        ImageView pic1 = (ImageView) findViewById(R.id.right_arrow1);
        pic1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SportActivity.this, DifficultyActivity.class);
                startActivity(i);
            }
        });

        ImageView pic2 = (ImageView) findViewById(R.id.right_arrow2);
        pic2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SportActivity.this, DifficultyActivity.class);
                startActivity(i);
            }
        });

        ImageView pic3 = (ImageView) findViewById(R.id.right_arrow3);
        pic3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SportActivity.this, DifficultyActivity.class);
                startActivity(i);
            }
        });

        ImageView pic4 = (ImageView) findViewById(R.id.right_arrow4);
        pic4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SportActivity.this, DifficultyActivity.class);
                startActivity(i);
            }
        });

        ImageView pic5 = (ImageView) findViewById(R.id.right_arrow5);
        pic5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SportActivity.this, DifficultyActivity.class);
                startActivity(i);
            }
        });

        ImageView pic6 = (ImageView) findViewById(R.id.right_arrow6);
        pic6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SportActivity.this, DifficultyActivity.class);
                startActivity(i);
            }
        });

        ImageView pic7 = (ImageView) findViewById(R.id.right_arrow7);
        pic7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SportActivity.this, DifficultyActivity.class);
                startActivity(i);
            }
        });

        ImageView pic8 = (ImageView) findViewById(R.id.right_arrow8);
        pic8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SportActivity.this, DifficultyActivity.class);
                startActivity(i);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.sport, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
