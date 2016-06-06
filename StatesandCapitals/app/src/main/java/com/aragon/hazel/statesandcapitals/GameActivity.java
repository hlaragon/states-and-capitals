/* Author: Hazel Aragon
   Due Date: Apr 27th, 2016
   Homework #5
   GameActivity.java
*/

package com.aragon.hazel.statesandcapitals;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Arrays;
import java.util.Random;

public class GameActivity extends AppCompatActivity
{
    Random rand = new Random();
    TextView tv, tv2, tvScore, tvq1;
    EditText et, etName;
    Button btnState, btnCapital, btnSubmit, btnNameSubmit, btnExit;

    int[] storedNums = new int[50];
    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); // disables title
        setContentView(R.layout.activity_game);

        // Variables
        final String[] stateOrCap = new String[1];
        final String[] stateOrCapName = new String[1];
        final String[] corrStateOrCap = new String[1];
        final int[] randNum = new int[1];
        final int[] scoreCounter = {0};
        btnState = (Button)findViewById(R.id.buttonState);
        btnCapital = (Button)findViewById(R.id.buttonCapital);
        btnSubmit = (Button)findViewById(R.id.buttonSubmit);
        btnNameSubmit = (Button)findViewById(R.id.buttonNameSubmit);
        btnExit = (Button)findViewById(R.id.buttonEndGame);

        tv = (TextView)findViewById(R.id.stateOrCapital);
        tv2 = (TextView)findViewById(R.id.q2);
        tvScore = (TextView)findViewById(R.id.textViewScoreNum);
        tvq1 = (TextView)findViewById(R.id.firstQ);
        et = (EditText)findViewById(R.id.editText);
        etName = (EditText)findViewById(R.id.nameEditText);

        final SQLiteDatabase db;
        final SQLiteDatabase dbScores;

        db = openOrCreateDatabase("gameDB.db",
                SQLiteDatabase.CREATE_IF_NECESSARY, null);
        dbScores = openOrCreateDatabase("scoresDB.db",
                SQLiteDatabase.CREATE_IF_NECESSARY, null);

        stateOrCap[0] = getRandomStateOrCapitalBinary();
        randNum[0] = getRandomNumber();

        // Finds the random state/capital.
        // Eg. stateOrCapName[0] = "Sacramento"
        stateOrCapName[0] = queryData(stateOrCap[0], randNum[0], db);

        // Finds the corresponding capital/state.
        // Eg. corrStateOrCap[0] = "California"
        corrStateOrCap[0] = correspondingQueryData(stateOrCap[0],
                randNum[0], db);

        // Displays initial state/capital name on screen. Eg. "Sacramento"
        tv.setText(stateOrCapName[0]);


        /*********************************************************************/
        /*************************** BUTTONS *********************************/
        /*********************************************************************/


        // ******* STATE BUTTON *****
        btnState.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (stateOrCap[0].equals("state"))   // The correct answer
                {
                    // Displays next part of the question
                    tv2.setText(getResources().getString(R.string.q2capital));
                    tv2.setVisibility(View.VISIBLE);
                    et.setVisibility(View.VISIBLE);
                    et.setHint(R.string.capital);
                    btnSubmit.setVisibility(View.VISIBLE);

                    // +10 points awarded, score is updated
                    scoreCounter[0] +=10;
                    tvScore.setText(Integer.toString(scoreCounter[0]));

                    // Prevents user from clicking "State" or "Capital"
                    // buttons while they answer the 2nd question
                    enableButtons(false);
                }

                else if (stateOrCap[0].equals("capital")) // The wrong answer
                {
                    // Keeps 2nd part of the question hidden
                    et.setVisibility(View.INVISIBLE);
                    tv2.setVisibility(View.INVISIBLE);
                    btnSubmit.setVisibility(View.INVISIBLE);

                    // Gives user a new capital or state question to answer
                    stateOrCap[0] = getRandomStateOrCapitalBinary();
                    randNum[0] = getRandomNumber();

                    if (randNum[0] == 51)
                    {
                        gameOver();
                    }
                    else
                    {
                        tv.setText(queryData(stateOrCap[0], randNum[0], db));
                        corrStateOrCap[0]=correspondingQueryData(stateOrCap[0],
                                randNum[0], db);
                    }
                }
            }
        });


        // ******* CAPITAL BUTTON *****
        btnCapital.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (stateOrCap[0].equals("capital")) // The correct answer
                {
                    // Displays next part of the question
                    tv2.setText(getResources().getString(R.string.q2state));
                    tv2.setVisibility(View.VISIBLE);
                    et.setVisibility(View.VISIBLE);
                    et.setHint(R.string.state);
                    btnSubmit.setVisibility(View.VISIBLE);

                    // +10 points awarded, score is updated
                    scoreCounter[0] += 10;
                    tvScore.setText(Integer.toString(scoreCounter[0]));

                    // Prevents user from clicking "State" or "Capital" buttons
                    // while they answer the 2nd question
                    enableButtons(false);
                }
                else if (stateOrCap[0].equals("state"))  // The incorrect answer
                {
                    // Keeps 2nd part of the question hidden
                    et.setVisibility(View.INVISIBLE);
                    tv2.setVisibility(View.INVISIBLE);
                    btnSubmit.setVisibility(View.INVISIBLE);

                    // Gets the user a new capital or state question to answer
                    stateOrCap[0] = getRandomStateOrCapitalBinary();
                    randNum[0] = getRandomNumber();
                    if (randNum[0] == 51)
                    {
                       gameOver();
                    }
                    else
                    {
                        tv.setText(queryData(stateOrCap[0], randNum[0], db));
                        corrStateOrCap[0] = correspondingQueryData(
                                stateOrCap[0], randNum[0], db);
                    }
                }
            }
        });

        // ******* ANSWER SUBMIT BUTTON *****
        btnSubmit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String ans = et.getText().toString();

                // Check's if user's answer is correct
                // If so, +10 points awarded & score is updated
                if (ans.equals(corrStateOrCap[0]))
                {
                    scoreCounter[0] += 10;
                    tvScore.setText(Integer.toString(scoreCounter[0]));
                }

                // 2nd part of question becomes hidden to user
                // "State" & "Capital" buttons become available again for user
                et.setVisibility(View.INVISIBLE);
                et.setText("");
                tv2.setVisibility(View.INVISIBLE);
                btnSubmit.setVisibility(View.INVISIBLE);
                enableButtons(true);

                // Generates new state or capital
                stateOrCap[0] = getRandomStateOrCapitalBinary();
                randNum[0] = getRandomNumber();
                if (randNum[0] == 51)
                {
                    gameOver();
                }
                else
                {
                    tv.setText(queryData(stateOrCap[0], randNum[0], db));
                    corrStateOrCap[0] = correspondingQueryData(
                            stateOrCap[0], randNum[0], db);
                }
            }
        });

        // ******* NAME SUBMIT BUTTON *****
        btnNameSubmit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String nameInput = etName.getText().toString();
                Long recNum;

                if (nameInput.equals(""))
                {
                    dbScores.close();
                    Intent i = new Intent(getApplicationContext(),
                            ScoreActivity.class);
                    startActivity(i);
                }

                else
                {
                    ContentValues cv = new ContentValues();

                    cv.put("name", nameInput);
                    cv.put("score", scoreCounter[0]);
                    recNum=dbScores.insert("show_score",null,cv);
                    dbScores.close();

                    Intent i = new Intent(getApplicationContext(),
                            ScoreActivity.class);
                    startActivity(i);
                }
            }
        });

        // ******* EXIT BUTTON *****
        // (If the player wants to end the game early)
        btnExit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                gameOver();
            }
        });
    }

    public String queryData(String stateOrCap, int randNum, SQLiteDatabase db)
    {
        // Determines if a random state or random capital appears on screen
        Cursor c = db.rawQuery("select " + stateOrCap +
                " from info where id = " + randNum, null);
        c.moveToFirst();
        return c.getString(0);
    }

    public String correspondingQueryData(String stateOrCap, int randNum,
                                         SQLiteDatabase db)
    {
        // Gets the random state's capital or vice versa
        if (stateOrCap.equals("state"))
        {
            Cursor c = db.rawQuery("select " + "capital" +
                    " from info where id = " + randNum, null);
            c.moveToFirst();
            return c.getString(0);
        }

        else
        {
            Cursor c = db.rawQuery("select " + "state" +
                    " from info where id = " + randNum, null);
            c.moveToFirst();
            return c.getString(0);
        }
    }

    public String getRandomStateOrCapitalBinary()
    {
        // Generates random number: either 0 or 1
        // 0 = state, 1 = capital
        int randomBinary;

        randomBinary = rand.nextInt(2); // 0 or 1

        if (randomBinary == 0) {return ("state");}
        else {return ("capital");}
    }

    public int getRandomNumber()
    {
        // Generates & returns random integer between 1 and 50 inclusive
        // Specific number shows up only once throughout the game
        // Eg. If California shows up, neither California nor Sacramento
        // will appear again as a question throughout the game.
        // Game is over by default when all 50 states/capital combinations
        // have been exhausted.

        int randomNumber = rand.nextInt(50)+1; // 1 ... 50
        boolean flag = false;

        if (count < 50)
        {
            for(int i = 0; i<count+1; i++)
            {
                if (randomNumber == storedNums[i])
                {
                    flag = true;
                    getRandomNumber();
                }
            }

            if (!flag)
            {
                storedNums[count]=randomNumber;
                count++;
            }

            return storedNums[count-1];
        }

        else
        {
            // Goes here when all 50 state/capital combinations have been used
            return 51;
        }
    }

    public void enableButtons(Boolean setStatus)
    {
        btnCapital.setEnabled(setStatus);
        btnCapital.setClickable(setStatus);
        btnState.setEnabled(setStatus);
        btnState.setClickable(setStatus);
    }

    public void gameOver()
    {
        // GAME OVER...
        // "Removes" Game textviews, edittexts, and buttons from screen
        enableButtons(false);

        btnState.setVisibility(View.INVISIBLE);
        btnCapital.setVisibility(View.INVISIBLE);
        et.setVisibility(View.INVISIBLE);
        tv2.setVisibility(View.INVISIBLE);
        btnSubmit.setVisibility(View.INVISIBLE);
        tvq1.setVisibility(View.INVISIBLE);

        // Brings up Game Over screen information
        btnNameSubmit.setVisibility(View.VISIBLE);
        etName.setVisibility(View.VISIBLE);
        tv.setText(getResources().getString(R.string.gameOver));
        tv.setTextColor(Color.RED);
    }
}
