package com.example.exoplayerwithvisualizer;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.exoplayer2.SimpleExoPlayer;

public class BarActivity extends AppCompatActivity {

    private SimpleExoPlayer player;
    private AudioPlayer audioPlayer;
    private BarVisualizer mVisualizer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar);
        mVisualizer = findViewById(R.id.bar);
        player = null;
        mVisualizer.show();
        audioPlayer = new AudioPlayer(player);
        audioPlayer.audioDataReceiver.setAudioDataListener(mVisualizer);
    }

    @Override
    protected void onStart() {
        super.onStart();
        startPlayingAudio(R.raw.sample_music);
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopPlayingAudio();
    }

    private void startPlayingAudio(int resId) {
        audioPlayer.play(this, resId);
    }

    private void stopPlayingAudio() {
        if (audioPlayer != null)
            audioPlayer.stop();
    }


}
