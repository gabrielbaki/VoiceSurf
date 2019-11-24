package com.example.gabriel.needlit;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import java.util.Locale;
import android.content.Intent;
import android.os.Build;
import java.util.HashMap;
import android.annotation.TargetApi;

/**
 * Manages Text to Speech libraries
 * Created by gabrielbaki on 12/29/15.
 */
public class TtsManager
{
    private TextToSpeech myTTS;
    private Context context;

    /**
     * constructor
     * @param baseContext
     */
    public TtsManager(Context baseContext)
    {
        this.context = baseContext;
        initOrInstallTts();
    }

    /**
     * initializes or installs Text to Speech
     */
    public void initOrInstallTts()
    {
        myTTS = new TextToSpeech(context, new OnInitListener()
        {
            public void onInit(int status)
            {
                if (status == TextToSpeech.SUCCESS)
                {
                    myTTS.setLanguage(Locale.UK);
                }
                else
                    installTts();
            }
        });
    }

    /**
     * installs Text to speech
     */
    private void installTts()
    {
        Intent installIntent = new Intent();
        installIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
        context.startActivity(installIntent);
    }

    /**
     * stops text to speech
     */
    public void stopTts(){
        myTTS.stop();
    }

    /**
     * speaks out a text
     * @param text
     */
    public void speak(String text)
    {
        String textLimit = text;
        if(text.length() > 3000){
            textLimit = text.substring(0, 2998);//max string 4k but this to enable my api 17
        } else if(text.length() < 30){
            textLimit = "Nothing found, please search something else";
        }
        //check API to decide action
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ttsGreater21(textLimit);//BIG TEXT DONT WORK
        } else {
            ttsUnder20(textLimit);
        }
    }

    @SuppressWarnings("deprecation")
    private void ttsUnder20(String text) {
        HashMap<String, String> map = new HashMap<>();
        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "MessageId");
        myTTS.speak(text, TextToSpeech.QUEUE_FLUSH, map);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void ttsGreater21(String text) {
        String utteranceId=this.hashCode() + "";
        myTTS.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId);
    }


}