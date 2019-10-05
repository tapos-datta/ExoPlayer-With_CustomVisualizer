package com.example.exoplayerwithvisualizer;

import java.nio.ByteBuffer;
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
        byte[] bufferArr = new byte[buffer.remaining()];
        buffer.get(bufferArr);
        buffer.rewind();

        //frames par sample
        int numFrames  = bufferArr.length / channelCount;

        byte[] rawData = new byte[numFrames];

        int sum = 0;

        int val1 = 0;

        //took average of all channel data
        for (int i = 0 ; i < numFrames ; i++) {
            sum = 0;
            for (int ch = 0; ch < channelCount; ch++) {

                val1 = (int)bufferArr[channelCount * i + ch];

                sum+= val1;
            }
            rawData[i] = (byte)((sum/channelCount));
        }

        // Log.d("data_receiver", "  "+rawData.length+ " "+ bufferArr.length+"  "+channelCount);

        //send partial data to visualizer
        if(rawData.length>=1024) {

            this.audioDataListener.setRawAudioBytes(Arrays.copyOf(rawData, 1024));
        }
        else{
            this.audioDataListener.setRawAudioBytes(rawData.clone());
        }

        setLocked(false);

    }

    public boolean isLocked() {
        return isLocked;
    }

    public void setLocked(boolean locked) {
        isLocked = locked;
    }

    public interface AudioDataListener{

        void setRawAudioBytes(byte[] bytes);
    }
}
