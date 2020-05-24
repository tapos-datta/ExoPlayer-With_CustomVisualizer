package com.example.exoplayerwithvisualizer;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;

public class AudioDataReceiver implements AudioPlayer.AudioDataFetch {

    public boolean isLocked;
    private AudioDataListener audioDataListener;

    public AudioDataReceiver(){
        isLocked = false;
        audioDataListener = null;
    }

    public void setAudioDataListener(AudioDataListener audioDataListener) {
        this.audioDataListener = audioDataListener;
    }

    @Override
    public void setAudioDataAsByteBuffer(ByteBuffer buffer, int sampleRate, int channelCount) {
        //raw PCM data
        ShortBuffer shortBuffer = buffer.asShortBuffer();
        short[] data = new short[shortBuffer.limit()];
        shortBuffer.get(data);

        //frames par sample
        int numFrames  = data.length / channelCount;

        short[] rawData = new short[numFrames];

        long sum = 0;

        int val1 = 0;

        //took average of all channel data
        for (int i = 0 ; i < numFrames ; i++) {
            sum = 0;
            for (int ch = 0; ch < channelCount; ch++) {
                val1 = (int)(data[channelCount * i + ch]+ 32768);
                sum += val1;
            }
            rawData[i] = (short) ((sum/channelCount));
        }
        
        this.audioDataListener.setRawAudioBytes(rawData.clone());

        setLocked(false);

    }

    public boolean isLocked() {
        return isLocked;
    }

    public void setLocked(boolean locked) {
        isLocked = locked;
    }

    public interface AudioDataListener{

        void setRawAudioBytes(short[] shorts);
    }
}
