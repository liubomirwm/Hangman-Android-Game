package com.example.liubo.hangman;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DownloadWordTask extends AsyncTask<Void, Void, String> {

    RelativeLayout editTextsLayout;
    Context context;
    OkHttpClient client = new OkHttpClient();
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    private static final String GET_TOKEN_URL = "http://hangman-api.herokuapp.com/hangman";

    public DownloadWordTask(RelativeLayout editTextsLayout, Context context) {
        super();
        this.editTextsLayout = editTextsLayout;
        this.context = context;
    }

    @Override
    protected String doInBackground(Void... voids) {
        String newWord;
        do {
            newWord = getNewWord();
        } while (newWord.length() > 16);

        return newWord;
    }

    @Override
    protected void onPostExecute(String newWord) {
        super.onPostExecute(newWord);
        this.editTextsLayout.removeAllViews();
        char[] newWordLetters = newWord.toUpperCase().toCharArray();
        for (int i = 0, id = -1; i < newWordLetters.length; id++, i++) {
            TextView textView = new TextView(context);
            RelativeLayout.LayoutParams params = new RelativeLayout
                    .LayoutParams(50
                    , RelativeLayout.LayoutParams.WRAP_CONTENT);
            textView.setId(TextViewsIdsHolder.Ids[i]);
            params.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
            if (i != 0) {
                params.addRule(RelativeLayout.ALIGN_BASELINE, TextViewsIdsHolder.Ids[id]);
                params.addRule(RelativeLayout.RIGHT_OF, TextViewsIdsHolder.Ids[id]);
            }
            textView.setLayoutParams(params);
            textView.setText("_");
            textView.setTextSize(30);
            editTextsLayout.addView(textView);
        }
        CurrentWordHolder.currentWord = newWord;
        for (int i = 0; i < newWordLetters.length; i++) {
            CurrentWordHolder.letters.add(newWordLetters[i] + "");
        }
    }

    private String getNewWord() {
        String JSONToken = "";
        String JSONWord = "";
        String parsedToken = "";
        String parsedWord = "";

        try {
            JSONToken = post(GET_TOKEN_URL, "");
        } catch (IOException ie) {
            Log.wtf(this.getClass().toString(), ie.toString());
        }

        try {
            JSONObject jsonObject = new JSONObject(JSONToken);
            parsedToken = jsonObject.getString("token");
        } catch (JSONException je) {
            Log.wtf(getClass().toString(), je.toString());
        }

        String wordRequestUrl = GET_TOKEN_URL + "?token=" + parsedToken;
        try {
            JSONWord = get(wordRequestUrl);
        } catch (IOException ie) {
            Log.wtf(this.getClass().toString(), ie.toString());
        }

        try {
            JSONObject jsonObject = new JSONObject(JSONWord);
            parsedWord = jsonObject.getString("solution");
        } catch (JSONException je) {
            Log.wtf(getClass().toString(), je.toString());
        }

        return parsedWord;
    }

    private String get(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    private String post(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }
}
