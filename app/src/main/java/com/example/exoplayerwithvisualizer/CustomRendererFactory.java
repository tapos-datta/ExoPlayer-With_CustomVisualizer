/*
   using a TeeAudioProcessor with AudioBufferSink interface we can get raw PCM data from input buffer.
 */


package com.example.exoplayerwithvisualizer;

import android.content.Context;

import androidx.annotation.Nullable;

import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.audio.AudioCapabilities;
import com.google.android.exoplayer2.audio.AudioProcessor;
import com.google.android.exoplayer2.audio.AudioSink;
import com.google.android.exoplayer2.audio.DefaultAudioSink;
import com.google.android.exoplayer2.audio.TeeAudioProcessor;
import com.google.android.exoplayer2.audio.TeeAudioProcessor.*;


public class CustomRendererFactory extends DefaultRenderersFactory implements DefaultAudioSink.AudioProcessorChain {

    private TeeAudioProcessor teeAudioProcessor;

    public CustomRendererFactory(Context context, AudioBufferSink listener) {
        super(context);
        teeAudioProcessor = new TeeAudioProcessor(listener);
    }

    @Nullable
    @Override
    protected AudioSink buildAudioSink(Context context, boolean enableFloatOutput, boolean enableAudioTrackPlaybackParams, boolean enableOffload) {
        //return a custom audioSink
        return new DefaultAudioSink(AudioCapabilities.getCapabilities(context), this, enableFloatOutput, enableAudioTrackPlaybackParams, enableOffload);
    }

    @Override
    public AudioProcessor[] getAudioProcessors() {
        //return audioProcessor with custom teeAudioProcessor
        return new AudioProcessor[]{teeAudioProcessor};
    }

    @Override
    public PlaybackParameters applyPlaybackParameters(PlaybackParameters playbackParameters) {
        return playbackParameters;
    }

    @Override
    public boolean applySkipSilenceEnabled(boolean skipSilenceEnabled) {
        return skipSilenceEnabled;
    }

    @Override
    public long getMediaDuration(long playoutDuration) {
        return playoutDuration;
    }

    @Override
    public long getSkippedOutputFrameCount() {
        return 0;
    }
}
