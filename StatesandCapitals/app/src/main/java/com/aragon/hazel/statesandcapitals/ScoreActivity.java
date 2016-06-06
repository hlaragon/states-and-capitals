/* Author: Hazel Aragon
   Due Date: Apr 27th, 2016
   Homework #5
   ScoreActivity.java
*/

package com.aragon.hazel.statesandcapitals;

import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import java.lang.String;

public class ScoreActivity extends AppCompatActivity
{
    TextView playerList, scoreList;
    Button btnExitToMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); // disables title
        setContentView(R.layout.activity_score);

        btnExitToMenu = (Button)findViewById(R.id.buttonBack);

        playerList = (TextView)findViewById(R.id.textViewNamesList);
        scoreList = (TextView)findViewById(R.id.textViewScoreList);

        int counter = 0;
        int i = 1;
        final SQLiteDatabase dbScores;
        dbScores = openOrCreateDatabase("scoresDB.db",
                SQLiteDatabase.CREATE_IF_NECESSARY, null);

        Cursor c;
        c = dbScores.rawQuery("select * from show_score order by score desc;",
                null);

        String nameText = "";
        String scoreText = "";

        for(c.moveToFirst(); !c.isAfterLast(); c.moveToNext())
        {
            String name = c.getString(1);
            int score = c.getInt(2);

            nameText += name + "\n";
            scoreText += Integer.toString(score) + "\n";
            counter += 1;
            if (counter==10) // Limits to top 10 high scores
            {
                break;
            }
        }

        playerList.setText(nameText);
        scoreList.setText(scoreText);

        // ******* EXIT TO MENU BUTTON *****
        btnExitToMenu.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(getApplicationContext(),
                        SplooshActivity.class);
                startActivity(i);
            }
        });
    }
}
