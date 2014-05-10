package com.arretadogames.pilot.util;

import com.arretadogames.pilot.MainActivity;
import com.arretadogames.pilot.audio.MusicI;
import com.arretadogames.pilot.audio.SoundI;

public class Assets {
	// Music
	public static MusicI mainMenuMusic;
	
	// Sound Effects
	public static SoundI clickSound;
	public static SoundI jumpSound;
	public static SoundI pickupSound;
	public static SoundI swipeSound;
	
	public static void load() {
		clickSound = MainActivity.getActivity().getAudio().newSound("button_click.wav");
		jumpSound = MainActivity.getActivity().getAudio().newSound("jump.wav");
		pickupSound = MainActivity.getActivity().getAudio().newSound("pickup_box.wav");
		swipeSound = MainActivity.getActivity().getAudio().newSound("swipe.flac");
		
		mainMenuMusic = MainActivity.getActivity().getAudio().newMusic("main_menu.mp3");
		mainMenuMusic.setLooping(true);
		mainMenuMusic.setVolume(0.5f);
	}
	
	public static void playSound(SoundI sound) {
		if (Settings.soundEnabled)
			sound.play(0.6f);
	}
	
	public static void playSound(SoundI sound, float volume) {
		if (Settings.soundEnabled)
			sound.play(volume);
	}

}
