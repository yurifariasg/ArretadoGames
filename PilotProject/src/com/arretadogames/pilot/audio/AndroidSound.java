package com.arretadogames.pilot.audio;

import android.media.SoundPool;


public class AndroidSound implements SoundI {
    int soundId;
    SoundPool soundPool;
    
    public AndroidSound(SoundPool soundPool,int soundId) {
        this.soundId = soundId;
        this.soundPool = soundPool;
    }

    @Override
    public void play(float volume) {
        soundPool.play(soundId, volume, volume, 0, 0, 1);
    }

    @Override
    public void dispose() {
        soundPool.unload(soundId);
    }

}
