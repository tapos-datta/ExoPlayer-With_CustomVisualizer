package com.example.exoplayerwithvisualizer;

import android.content.Context;
import android.util.Log;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.audio.TeeAudioProcessor;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.RawResourceDataSource;
import com.google.android.exoplayer2.util.Util;

import java.nio.ByteBuffer;

public class AudioPlayer {

    DefaultTrackSelector trackSelector;
    DefaultLoadControl loadControl;
    public AudioDataReceiver audioDataReceiver;
    public static AudioDataFetch audioDataFetch;
    public static int sampleRate;
    public static int channels;
    private SimpleExoPlayer player;

    public AudioPlayer(SimpleExoPlayer player){

        this.player = player;
        trackSelector  = new DefaultTrackSelector();
        loadControl = new DefaultLoadControl();
        //set a callback to receive audio data
         audioDataReceiver = new AudioDataReceiver();
        setAudioDataFetch((AudioDataFetch) audioDataReceiver);
    }



    public void play(Context context, int resId){

        if(player!=null)
        {
            stop();
        }

        final CustomRendererFactory rendererFactory = new CustomRendererFactory(context, new TeeAudioProcessor.AudioBufferSink() {

            int counter = 0;
            @Override
            public void flush(int sampleRateHz, int channelCount, int encoding) {

                sampleRate = sampleRateHz;
                channels = channelCount;
            }

            @Override
            public void handleBuffer(ByteBuffer buffer) {

                counter++;
                if(!audioDataReceiver.isLocked()){
                    audioDataReceiver.setLocked(true);
                    audioDataFetch.setAudioDataAsByteBuffer(buffer.duplicate(),sampleRate,channels);

                }
                else{
                    Log.d("main_Activity", "handleBuffer: skipped no"+ counter);
                }
            }
        });

        player = ExoPlayerFactory.newSimpleInstance(context, rendererFactory, trackSelector, loadControl);

        //create datasource from resource
        DataSpec dataSpec = new DataSpec(RawResourceDataSource.buildRawResourceUri(resId));
        final RawResourceDataSource rawResourceDataSource = new RawResourceDataSource(context);
        try {
            rawResourceDataSource.open(dataSpec);
        } catch (RawResourceDataSource.RawResourceDataSourceException e) {
            e.printStackTrace();
        }

        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(context, Util.getUserAgent(context, "Visualizer"));
        final ProgressiveMediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(rawResourceDataSource.getUri());

        //load datasource into player
        player.setPlayWhenReady(true);
        player.prepare(mediaSource);
    }


    public void stop(){

        if(player!=null)
        {
            player.stop();
            player.setPlayWhenReady(false);
            player.seekTo(0);
            player.release();
        }
    }


    public void setAudioDataFetch(AudioDataFetch audioDataFetch) {
        this.audioDataFetch = audioDataFetch;
    }


    public interface AudioDataFetch{
        void setAudioDataAsByteBuffer(ByteBuffer buffer, int sampleRate, int channelCount);
    }
}
