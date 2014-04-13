package com.arretadogames.pilot.screens;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Color;
import android.opengl.GLES11;
import android.support.v4.view.GestureDetectorCompat;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenAccessor;
import aurelienribon.tweenengine.TweenEquations;

import com.arretadogames.pilot.MainActivity;
import com.arretadogames.pilot.R;
import com.arretadogames.pilot.config.GameSettings;
import com.arretadogames.pilot.database.GameDatabase;
import com.arretadogames.pilot.game.Game;
import com.arretadogames.pilot.game.GameState;
import com.arretadogames.pilot.levels.LevelManager;
import com.arretadogames.pilot.levels.LevelTable;
import com.arretadogames.pilot.loading.FontLoader;
import com.arretadogames.pilot.loading.FontLoader.FontTypeFace;
import com.arretadogames.pilot.loading.FontSpecification;
import com.arretadogames.pilot.loading.LoadManager;
import com.arretadogames.pilot.loading.LoadableGLObject;
import com.arretadogames.pilot.loading.LoadableType;
import com.arretadogames.pilot.render.opengl.GLCanvas;
import com.arretadogames.pilot.tournaments.Tournament;
import com.arretadogames.pilot.tournaments.TournamentType;
import com.arretadogames.pilot.ui.AnimationManager;
import com.arretadogames.pilot.ui.GameButtonListener;
import com.arretadogames.pilot.ui.ImageButton;
import com.arretadogames.pilot.world.GameWorld;

public class TournamentSelectionScreen extends GameScreen implements GameButtonListener, OnGestureListener, TweenAccessor<TournamentSelectionScreen> {
	
    private static final int LOCK_SCREEN_MAX_ALPHA = 100;
    private static final int MAX_ALPHA = 255;
	private static final float TRANSITION_ANIM_DURATION = 0.25f;
	
	private final float SCREEN_WIDTH = getDimension(R.dimen.screen_width);
	private final float SCREEN_HEIGHT = getDimension(R.dimen.screen_height);
	
	private ImageButton leftArrow;
	private ImageButton rightArrow;
	private InputEventHandler event;
	private FontSpecification titleFont;
	private GestureDetectorCompat mDetector;
	
	private Timeline currentAnimation;
	
	private class TournamentData {
		public String tournamentName;
		private int tournamentBg;
		private boolean isLocked;
		public ImageButton button;
		public TournamentType type;
		
		public void renderTrophy(GLCanvas canvas, float timeElapsed) {
			button.render(canvas, timeElapsed);
		}
	}
	
	private float xOffset;
	private TournamentData[] tournamentsInfo;
	private List<Tournament> tournaments;
	private boolean isScrolling; /* Needed to handle stop scroll event */
    private float auxOffset; // Use this Aux variable will be used to avoid flickering later on...
	
	private int getCurrentTournamentIndex() {
	    int currentIndex = (int) Math.floor((xOffset+1) / SCREEN_WIDTH) + (xOffset % SCREEN_WIDTH > SCREEN_WIDTH / 2 ? 1 : 0);
	    if (currentIndex < 0)
	        return 0;
	    else if (currentIndex >= tournamentsInfo.length)
	        return tournamentsInfo.length - 1;
		return currentIndex;
	}
	
	public TournamentSelectionScreen() {
		
		tournaments = GameDatabase.getInstance().getAllTournaments();
		
		tournamentsInfo = new TournamentData[tournaments.size()]; // Mock Info
		for (int i = 0 ; i < tournaments.size() ; i++) {
			tournamentsInfo[i] = createTournamentInfo(i, tournaments.get(i));
		}
		
		// Create First Tournament Info
		leftArrow = new ImageButton(-1, 17, 242, 60, 60,
		        this, R.drawable.level_arrow_left_selected, R.drawable.level_arrow_left_unselected);
		rightArrow = new ImageButton(-2, 723, 242, 60, 60,
		        this, R.drawable.level_arrow_right_selected, R.drawable.level_arrow_right_unselected);
		
		titleFont = FontLoader.getInstance().getFont(FontTypeFace.ARIAN);
		
		mDetector = new GestureDetectorCompat(MainActivity.getContext(), this);
		event = new InputEventHandler(null); // MotionEvent will be set later
		
        leftArrow.setVisible(false);
        if (tournamentsInfo.length <= 1) {
            rightArrow.setVisible(false);
        }
	}
	
	@Override
	public void onLoading() {
	    // Load a few objects
	    List<LoadableGLObject> objs = new ArrayList<LoadableGLObject>();
	    objs.add(new LoadableGLObject(R.drawable.swamp_tournament_bg, LoadableType.TEXTURE));
        objs.add(new LoadableGLObject(R.drawable.desert_tournament_bg, LoadableType.TEXTURE));
        objs.add(new LoadableGLObject(R.drawable.jungle_tournament_bg, LoadableType.TEXTURE));
        objs.add(new LoadableGLObject(R.drawable.lockpad, LoadableType.TEXTURE));
	    LoadManager.getInstance().addExtraObjects(objs);
	}
	
	private TournamentData createTournamentInfo(int index, Tournament tournament) {
		TournamentData ti = new TournamentData();
		int imgPressed = 0;
		int imgUnpressed = 0;
		
		ti.tournamentName = tournament.getTournamentType().toString();
		
		ti.tournamentBg = LevelTable.TOURNAMENT_BACKGROUNDS.get(tournament.getTournamentType());
		
		ti.isLocked = !tournament.getEnable();
		
		ti.type = tournament.getTournamentType();
		
		switch (tournament.getTournamentType()) {
			case JUNGLE://cacau
				if (ti.isLocked){
					imgPressed = R.drawable.trophy_cacau_blocked;
					imgUnpressed = R.drawable.trophy_cacau_blocked;
				} else {
					imgPressed = R.drawable.trophy_cacau_selected;
					imgUnpressed = R.drawable.trophy_cacau_unselected;
				}
				break;
			case DESERT://mirage
				if (ti.isLocked){
					imgPressed = R.drawable.trophy_mirage_blocked;
					imgUnpressed = R.drawable.trophy_mirage_blocked;
				} else {
					imgPressed = R.drawable.trophy_mirage_selected;
					imgUnpressed = R.drawable.trophy_mirage_unselected;
				}			
				break;
			case SWAMP://victoria
				if (ti.isLocked){
					imgPressed = R.drawable.trophy_victoria_blocked;
					imgUnpressed = R.drawable.trophy_victoria_blocked;
				} else {
					imgPressed = R.drawable.trophy_victoria_selected;
					imgUnpressed = R.drawable.trophy_victoria_unselected;
				}	
				break;
			default:
				break;
		}
		
		ti.button = createButton(index, getDimension(R.dimen.screen_width)/2 - 82 + index * 800,// 82 is half of the button
									getDimension(R.dimen.screen_height)/2 - 82,// 82 is half of the button
									imgPressed, imgUnpressed);
		
		return ti;
	}
	
	private ImageButton createButton(int id, float x, float y, int imgPressed, int imgUnpressed) {
		return new ImageButton(id, x, y, 165, 165, this, imgPressed, imgUnpressed);
	}

	private boolean collidesWithScreen(int tournamentIndex) {
		return diffFromCenterScreen(tournamentIndex) < 1f;
	}

	/* This Function is parametized with SCREEN_WIDTH */
	private float diffFromCenterScreen(int tournamentIndex) {
		return Math.abs(SCREEN_WIDTH / 2 + tournamentIndex * SCREEN_WIDTH - (xOffset + SCREEN_WIDTH / 2)) / SCREEN_WIDTH;
	}

	@Override
	public void render(GLCanvas canvas, float timeElapsed) {
		
		GLES11.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		
		canvas.saveState();
		auxOffset = xOffset; // Use this Aux variable to store the offset and avoid flickering (xOffset may be modified while the render is executing)
		canvas.translate(-auxOffset, 0);
		
		for (int i = 0 ; i < tournamentsInfo.length ; i++) {
			if (collidesWithScreen(i)) {
				
				float diff = diffFromCenterScreen(i);
				
				canvas.drawBitmap(tournamentsInfo[i].tournamentBg,
				        auxOffset, 0, SCREEN_WIDTH, SCREEN_HEIGHT,
						0, getDimension(R.dimen.tournament_bg_extra_height),
						MAX_ALPHA - diff * MAX_ALPHA);
				
				canvas.drawText(tournamentsInfo[i].type.name(),
				        auxOffset + SCREEN_WIDTH / 2f, 45,
						titleFont, 1.2f, true, MAX_ALPHA - diff * MAX_ALPHA);
				
				tournamentsInfo[i].renderTrophy(canvas, timeElapsed);
				
				if (tournamentsInfo[i].isLocked) {
				    int alpha = (int) (LOCK_SCREEN_MAX_ALPHA - LOCK_SCREEN_MAX_ALPHA * diff);
				    canvas.drawRect(auxOffset, 0, auxOffset + SCREEN_WIDTH, SCREEN_HEIGHT, Color.argb(alpha, 0, 0, 0));
	                
	                canvas.drawText("Tournament Locked",
	                        auxOffset + SCREEN_WIDTH / 2f, 435,
	                        titleFont, 1.2f, true, MAX_ALPHA - diff * MAX_ALPHA);
				} else {
					canvas.drawText(tournamentsInfo[i].tournamentName + " trophy",
	                        auxOffset + SCREEN_WIDTH / 2f, 435,
	                        titleFont, 1.2f, true, MAX_ALPHA - diff * MAX_ALPHA);
				}
			}
		}
		
		canvas.restoreState();
		
		leftArrow.render(canvas, timeElapsed);
		rightArrow.render(canvas, timeElapsed);
		
        GLES11.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);
		
	}

	@Override
	public void step(float timeElapsed) {
	}

	@Override
	public void input(InputEventHandler event) {
		mDetector.onTouchEvent(event.getEvent());
		
		if (event.getAction() == MotionEvent.ACTION_UP
		        && isScrolling) {
		    isScrolling = false;
		    if (xOffset % SCREEN_WIDTH != 0) {
		        startAnimationTo(getCurrentTournamentIndex());
		    }
		}
	}

	@Override
	public void onPause() {
	}
	
	@Override
	public void onBackPressed() {
		Game.getInstance().goTo(GameState.MAIN_MENU);
	}

	@Override
	public void onClick(int stageId) {
		int currentIndex = getCurrentTournamentIndex();
		if (stageId == -1 && currentIndex > 0) {
		    startAnimationTo(getCurrentTournamentIndex() - 1);
		} else if (stageId == -2 && currentIndex < tournamentsInfo.length - 1) {
            startAnimationTo(getCurrentTournamentIndex() + 1);
		} else if (stageId > -1) {
			int stageTableId = tournaments.get(getCurrentTournamentIndex()).getIdsLevels()[stageId];
			// Sets the level
			((GameWorld)Game.getInstance().getScreen(GameState.RUNNING_GAME)).setLevel(LevelManager.getLevels().get(stageTableId));
			// Start Loading ?
			Game.getInstance().goTo(GameState.CHARACTER_SELECTION);
		}
	}

	@Override
	public boolean onDown(MotionEvent e) {
	    return sendMotionEvent(e);
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		
		int targetIndex;
		if (velocityX < 0) {
			targetIndex = getCurrentTournamentIndex() + 1;
		} else {
			targetIndex = getCurrentTournamentIndex() - 1;
		}
		
		if (targetIndex < 0 || targetIndex >= tournamentsInfo.length) {
			return false;
		}
	
		isScrolling = false;
		
		startAnimationTo(targetIndex);
		
		return true;
	}
	
	private void startAnimationTo(int tournamentIndex) {
		if (currentAnimation != null && currentAnimation.isFinished()) {
		    return;
		}
		
		if (tournamentIndex <= 0) {
		    tournamentIndex = 0;
		    leftArrow.setVisible(false);
        } else {
            leftArrow.setVisible(true);
        }
		
		if (tournamentIndex >= tournamentsInfo.length - 1) {
            tournamentIndex = tournamentsInfo.length - 1;
            rightArrow.setVisible(false);
		} else {
            rightArrow.setVisible(true);
		}
		    
	    float targetX = SCREEN_WIDTH * tournamentIndex;
		
		currentAnimation = Timeline.createSequence()
			.push(Tween.to(this, 0, TRANSITION_ANIM_DURATION).target(targetX).ease(TweenEquations.easeOutBack))
			.start(AnimationManager.getInstance());
	}
	
	@Override
	public void onLongPress(MotionEvent e) {
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {

		xOffset += distanceX / GameSettings.WidthRatio;
		isScrolling = true;
		
		return true;
	}

	@Override
	public void onShowPress(MotionEvent e) {
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
	    return sendMotionEvent(e);
	}
	
	private boolean sendMotionEvent(MotionEvent e) {
	    event.setMotionEvent(e);
        
        boolean pressed = false;
        
        event.setOffsetX((int)xOffset);
        int currentTournament = getCurrentTournamentIndex();
        if (!tournamentsInfo[currentTournament].isLocked) {
        	 pressed |= tournamentsInfo[currentTournament].button.input(event);
        }
        event.setOffsetX(0);
        
        pressed |= leftArrow.input(event);
        pressed |= rightArrow.input(event);
        
        return pressed;
	}
	
	@Override
	public int getValues(TournamentSelectionScreen s, int type, float[] values) {
		values[0] = xOffset;
		return 1;
	}

	@Override
	public void setValues(TournamentSelectionScreen s, int type, float[] values) {
		xOffset = values[0];
	}
}
