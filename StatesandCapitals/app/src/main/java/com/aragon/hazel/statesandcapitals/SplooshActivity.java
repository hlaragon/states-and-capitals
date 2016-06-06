/* Author: Hazel Aragon
   Due Date: Apr 27th, 2016
   Homework #5
   Objective: A simple game designed to test your knowledge on
   determining a state's capital.
*/

package com.aragon.hazel.statesandcapitals;

import android.content.Intent;
import android.database.DatabaseUtils;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.database.sqlite.SQLiteDatabase;
import android.content.ContentValues;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import java.util.*;
import java.io.InputStream;
import java.util.Scanner;

public class SplooshActivity extends AppCompatActivity
{
    Button btnPlayGame, btnShowScores, btnExit;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); // disables title
        setContentView(R.layout.activity_sploosh);

        btnPlayGame = (Button)findViewById(R.id.play_game);
        btnShowScores = (Button)findViewById(R.id.show_scores);
        btnExit = (Button)findViewById(R.id.exit);

        String states[] = new String[50];
        String capitals[] = new String[50];

        parseUSStates(states, capitals);

        /************************* DATABASE **********************************/
        SQLiteDatabase db;          // States & Capitals information found here
        SQLiteDatabase dbScores;    // Players' names and scores found here

        deleteDatabase("gameDB.db");
        db = openOrCreateDatabase("gameDB.db",
                SQLiteDatabase.CREATE_IF_NECESSARY, null);
        dbScores = openOrCreateDatabase("scoresDB.db",
                SQLiteDatabase.CREATE_IF_NECESSARY, null);
        db.setVersion(1); //v1.0
        dbScores.setVersion(1); //v1.0

        // Creates "info" table: id | state | capital
        String dbStatesCapitals = "create table if not exists info(id " +
                "integer primary key autoincrement, state text not null, " +
                "capital text not null);";
        db.execSQL(dbStatesCapitals);

        // Creates "show_scores" table: id | name | score
        String dbScoreInstructions = "create table if not exists " +
                "show_score(id integer primary key autoincrement, " +
                "name text, score integer);";
        dbScores.execSQL(dbScoreInstructions);

        ContentValues cv = new ContentValues();
        long recNum = 0;

        for(int i=0; i<50; i++)
        {
            cv.put("state", states[i]);
            cv.put("capital", capitals[i]);
            recNum = db.insert("info", null, cv);
        }

        Log.d("recNum", Long.toString(recNum));
        Long halp = DatabaseUtils.queryNumEntries(db, "info");
        Log.d("queryrecNum", Long.toString(halp));

        db.close();
        dbScores.close();

        /************************* BUTTONS ***********************************/

        // "Play Game" button
        btnPlayGame.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(getApplicationContext(),
                        GameActivity.class);
                startActivity(i);

            }
        });

        // "Show Scores" button
        btnShowScores.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(getApplicationContext(),
                        ScoreActivity.class);
                startActivity(i);
            }
        });

        // "Exit" Button
        btnExit.setOnClickListener(new View.OnClickListener()
         {
             @Override
             public void onClick(View v)
             {
                 finishAffinity();
             }
         });
    }

    // Read in file
    private void parseUSStates(String states[], String capitals[]) {
        try {

            InputStream is = getResources().openRawResource(R.raw.us_states);
            Scanner sc = new Scanner(is);

            String line;

            int i = 0;

            sc.nextLine();
            sc.nextLine(); // skips headers

            while (sc.hasNext())
            {
                line = sc.nextLine();
                String temp[] = line.split("\\s\\s+");
                if(temp.length >= 2)
                {
                    if(temp.length == 2)
                    {
                        states[i] = temp[0];
                        capitals[i++] = temp[1];
                    }
                    else
                    {
                        states[i] = temp[0] + " " + temp[1];
                        capitals[i++] = temp[2];
                    }
                }
            }
        }catch (Exception e){System.err.println(e);}
    }
}
