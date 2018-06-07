package com.example.liubo.hangman;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class GameActivity extends AppCompatActivity {
    int lifes = 7;
    Resources resources;
    ArrayList<String> guessedLetters = new ArrayList<String>();
    ArrayList<String> alreadyTriedLetters = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        this.resources = getResources();

        Context context = this;
        RelativeLayout editTextsLayout = findViewById(R.id.TextViewsLayout);
        new DownloadWordTask(editTextsLayout, context).execute();

        final EditText editText = findViewById(R.id.EditTextBox);
        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int iLen = s.length();
                if (iLen > 0 && !Character.isLetter((s.charAt(iLen - 1)))) {
                    s.delete(iLen - 1, iLen);
                    return;
                }
                if (iLen > 1) {
                    s.delete(0, 1);
                }
            }
        });
    }

    public void checkLetter(View view) {
        String currentWord = CurrentWordHolder.currentWord.toLowerCase();
        EditText editTextBox = findViewById(R.id.EditTextBox);
        String letter = editTextBox.getText().toString().toLowerCase();

        int index = currentWord.indexOf(letter);
        if (index == -1) {
            if (alreadyTriedLetters.contains(letter)) {
                Toast.makeText(this, "You already tried that letter", Toast.LENGTH_LONG).show();
            } else {
                reduceLifes();
                alreadyTriedLetters.add(letter);
            }
        }
        int matchLength = letter.length();
        while (index >= 0) {  // indexOf returns -1 if no match found
            guessedLetters.add(letter);
            revealLetter(index, letter);
            index = currentWord.indexOf(letter, index + matchLength);
        }
    }

    private void revealLetter(int index, String letter) {
        TextView textView = findViewById(TextViewsIdsHolder.Ids[index]);
        textView.setText(letter);
    }

    private void reduceLifes() {
        this.lifes--;
        String imageToShow = "image_" + this.lifes;
        int imageResourceId = resources.getIdentifier(imageToShow, "drawable", getPackageName());
        ImageView imageView = findViewById(R.id.image);
        imageView.setImageResource(imageResourceId);
        if (this.lifes == 0) {
            gameOver();
        }
    }

    private void gameOver() {
        revealAllLetters();
        Toast.makeText(this, "You died. :(", Toast.LENGTH_LONG).show();
    }

    private void revealAllLetters() {
        for (int i = 0; i < CurrentWordHolder.letters.size(); i++) {
            revealLetter(i, CurrentWordHolder.letters.get(i));
        }

        Button guessLetterButton = findViewById(R.id.guessLetterButton);
        guessLetterButton.setVisibility(View.GONE);

        Button restartButton = findViewById(R.id.restartButton);
        restartButton.setVisibility(View.VISIBLE);
    }

    public void restartGame(View view) {

    }
}
