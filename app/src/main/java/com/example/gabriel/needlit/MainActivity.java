package com.example.gabriel.needlit;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import org.apache.http.util.ByteArrayBuffer;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import android.content.Intent;
import android.speech.RecognizerIntent;
import android.widget.Toast;

import java.util.Locale;

import android.view.Menu;
import android.view.MenuInflater;

/**
 * Main Activity
 */
public class MainActivity extends Activity {
    private TextView tvData;
    private String searchInput = "";
    private ImageButton btnPlay;
    private ImageButton btnStop;
    private StringBuilder webTextBuilder;
    private TtsManager tts;
    private final int REQ_CODE_SPEECH_INPUT = 100;

    /**
     * start program
     * @param savedInstanceState
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvData = (TextView) findViewById(R.id.tvData);
        btnPlay = (ImageButton) findViewById(R.id.btnDownload);
        btnStop = (ImageButton) findViewById(R.id.btnStop);
        btnStop.setVisibility(View.GONE);
        tts = new TtsManager(this);
        tts.initOrInstallTts();
    }

        /**
         * when click the play button
         * @param view
         */
    public void clickDownload(View view){
        promptSpeechInput();
    }

    /**
     * when click stop button
     * @param view
     */
    public void clickStop(View view){
        tts.stopTts();
        btnStop.setVisibility(View.GONE);
        btnPlay.setVisibility(View.VISIBLE);
    }

    /**
     * Showing google speech input dialog
     * */
    public void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Receiving speech input
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    tvData.setText(result.get(0));
                    searchInput = result.get(0);
                    new downloader().execute();
                    btnPlay.setVisibility(View.GONE);
                    btnStop.setVisibility(View.VISIBLE);
                }
                break;
            }

        }
    }

    /**
     * class to run things in background
     */
    class downloader extends AsyncTask<Void, Void, String> {

        /**
         * download data from internet as background activity
         * @param params
         * @return
         */
        protected String doInBackground(Void... params) {
            final int APPROX_MAX_PAGE_SIZE = 300;
            String webTextHtml = "";
            try {
                URL urlToDownload = new URL(
                        "https://en.wikipedia.org/wiki/"+searchInput);//+searchInput);
                URLConnection ucon = urlToDownload.openConnection();
                ucon.setRequestProperty("Connection", "keep-alive"); // (1)
                InputStream inputStream = ucon.getInputStream();
                BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
                ByteArrayBuffer byteArrayBuffer = new ByteArrayBuffer(APPROX_MAX_PAGE_SIZE); // (2)
                int current = 0;
                byte[] buf = new byte[APPROX_MAX_PAGE_SIZE];
                int read;
                do {
                    read = bufferedInputStream.read(buf, 0, buf.length); // (3)
                    if (read > 0) byteArrayBuffer.append(buf, 0, read);
                } while (read >= 0);
                webTextHtml = new String(byteArrayBuffer.toByteArray());

            } catch (Exception e) {
                Log.i("Error", e.toString());
            }

            return webTextHtml;
        }

        /**
         * convert html into text, then speak the text
         * @param webTextHtml
         */
        protected void onPostExecute(String webTextHtml) {
            super.onPostExecute(webTextHtml);
            webTextBuilder = new StringBuilder(webTextHtml);
            String result = HtmlPrccssr.htmlParagraph(webTextBuilder).toString();
            tts.speak(result);
        }


    }
}
