/*
   using a TeeAudioProcessor with AudioBufferSink interface we can get raw PCM data from input buffer.
 */


package com.example.exoplayerwithvisualizer;

import android.content.Context;
import android.os.Handler;

import androidx.annotation.Nullable;

import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.Renderer;
import com.google.android.exoplayer2.audio.AudioProcessor;
import com.google.android.exoplayer2.audio.AudioRendererEventListener;
import com.google.android.exoplayer2.audio.TeeAudioProcessor;
import com.google.android.exoplayer2.audio.TeeAudioProcessor.*;
import com.google.android.exoplayer2.drm.DrmSessionManager;
import com.google.android.exoplayer2.drm.FrameworkMediaCrypto;
import com.google.android.exoplayer2.mediacodec.MediaCodecSelector;

import java.util.ArrayList;



public class CustomRendererFactory extends DefaultRenderersFactory {

    private AudioBufferSink listener;
    private AudioProcessor[] aMProcessors;

    public CustomRendererFactory(Context context, AudioBufferSink listener) {
        super(context);
        this.listener = listener;
    }

    @Override
    protected void buildAudioRenderers(Context context, int extensionRendererMode, MediaCodecSelector mediaCodecSelector, @Nullable DrmSessionManager<FrameworkMediaCrypto> drmSessionManager, boolean playClearSamplesWithoutKeys, boolean enableDecoderFallback, AudioProcessor[] audioProcessors, Handler eventHandler, AudioRendererEventListener eventListener, ArrayList<Renderer> out) {

        aMProcessors = new AudioProcessor[]{new TeeAudioProcessor(listener)};

        super.buildAudioRenderers(context, extensionRendererMode, mediaCodecSelector, drmSessionManager, playClearSamplesWithoutKeys, enableDecoderFallback, aMProcessors, eventHandler, eventListener, out);
    }


}
