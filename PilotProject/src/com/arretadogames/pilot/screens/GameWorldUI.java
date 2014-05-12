package com.arretadogames.pilot.screens;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.RectF;
import android.opengl.GLES11;
import android.view.MotionEvent;
import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenAccessor;
import aurelienribon.tweenengine.TweenCallback;

import com.arretadogames.pilot.R;
import com.arretadogames.pilot.entities.Player;
import com.arretadogames.pilot.entities.PlayerNumber;
import com.arretadogames.pilot.items.BoxItem;
import com.arretadogames.pilot.items.Item;
import com.arretadogames.pilot.render.AnimationManager;
import com.arretadogames.pilot.render.AnimationSwitcher;
import com.arretadogames.pilot.render.opengl.GLCanvas;
import com.arretadogames.pilot.ui.GameButtonListener;
import com.arretadogames.pilot.ui.GameHUDButton;
import com.arretadogames.pilot.ui.ImageButton;
import com.arretadogames.pilot.world.GameWorld;

public class GameWorldUI extends GameScreen implements GameButtonListener {

	public static final int BT_PLAYER_1_JUMP = 1;
	public static final int BT_PLAYER_1_ACT = 2;
	public static final int BT_PLAYER_1_ITEM = 4;
	public static final int BT_PLAYER_2_JUMP = 5;
	public static final int BT_PLAYER_2_ACT = 6;
	public static final int BT_PLAYER_2_ITEM = 8;

	private static final int PAUSE_BT = 9;

	private final float COOLDOWN_IMAGE_SIZE = getDimension(R.dimen.cooldown_image_size);
	private final int INIT_OF_STATUS_INTERVAL = 270;
	private final int END_OF_STATUS_INTERVAL = 487 - INIT_OF_STATUS_INTERVAL;

	// These two first rects will determine the position of the others
	private final RectF PLAYER_1_ITEM_FRAME_RECT = new RectF(
	        40 - COOLDOWN_IMAGE_SIZE / 2, 40 - COOLDOWN_IMAGE_SIZE / 2,
	        40 + COOLDOWN_IMAGE_SIZE / 2, 40 + COOLDOWN_IMAGE_SIZE / 2);
	
	private final RectF PLAYER_2_ITEM_FRAME_RECT = new RectF(
            590 - COOLDOWN_IMAGE_SIZE / 2, 40 - COOLDOWN_IMAGE_SIZE / 2,
            590 + COOLDOWN_IMAGE_SIZE / 2, 40 + COOLDOWN_IMAGE_SIZE / 2);
	
	private final RectF PLAYER_1_ITEM_RECT = new RectF(PLAYER_1_ITEM_FRAME_RECT);
	{
	    PLAYER_1_ITEM_RECT.inset(5, 5); // Move 10 px inwards
	}
	
	private final RectF PLAYER_2_ITEM_RECT = new RectF(PLAYER_2_ITEM_FRAME_RECT);
    {
        PLAYER_2_ITEM_RECT.inset(5, 5); // Move 10 px inwards
    }
	
    private final RectF PLAYER_1_EFFECT_ACTIVE_RECT = new RectF(PLAYER_1_ITEM_FRAME_RECT);
    {
        PLAYER_1_EFFECT_ACTIVE_RECT.inset(-70, -70); // Move 50 px outwards
    }
    
    private final RectF PLAYER_2_EFFECT_ACTIVE_RECT = new RectF(PLAYER_2_ITEM_FRAME_RECT);
    {
        PLAYER_2_EFFECT_ACTIVE_RECT.inset(-70, -70); // Move 50 px outwards
    }
    
    private class ItemImage implements TweenAccessor<ItemImage> {
        protected float x, y;
        protected float width, height;
        protected int imageId;
        
        @Override
        public int getValues(ItemImage itemimg, int type, float[] getValues) {
            getValues[0] = itemimg.x;
            getValues[1] = itemimg.y;
            return 2;
        }
        @Override
        public void setValues(ItemImage itemimg, int type, float[] returnValues) {
            itemimg.x = returnValues[0];
            itemimg.y = returnValues[1];
        }
    }
    
	private Player p1;
	private Player p2;
	private GameWorld gWorld;
	
	private ItemImage p1ItemImg;
	private AnimationSwitcher itemActiveAnim;
	private float angleStrippedSunP1;

    private ItemImage p2ItemImg;
	private AnimationSwitcher itemActiveAnim2;
    private float angleStrippedSunP2;
	
    private GameHUDButton p1JumpListener;
    private GameHUDButton p1ActListener;
    private GameHUDButton p1ItemListener;
    
    private GameHUDButton p2JumpListener;
    private GameHUDButton p2ActListener;
    private GameHUDButton p2ItemListener;
    
    private ImageButton pauseBt;
    private PauseScreen pauseScreen;
	private float totalDistance = Float.MIN_VALUE;

	public GameWorldUI(GameWorld gameWorld) {
		this.gWorld = gameWorld;
		
		p1 = gWorld.getPlayers().get(PlayerNumber.ONE);
		p2 = gWorld.getPlayers().get(PlayerNumber.TWO);

		itemActiveAnim = AnimationManager.getInstance().getSprite("Spinner");
		itemActiveAnim2 = AnimationManager.getInstance().getSprite("Spinner");
		
		pauseScreen = new PauseScreen();
		pauseBt = new ImageButton(PAUSE_BT, 375, 10, 
				getDimension(R.dimen.main_menu_button_size) - 30, 
				getDimension(R.dimen.main_menu_button_size) - 30,
				this, 
				R.drawable.pause_selected, 
				R.drawable.pause_unselected);
		
	}
	public void setAllButtonListeners(GameHUDButton listener) {
		setP1ButtonListeners(listener);
		setP2ButtonListeners(listener);
	}

	// We may need to split this for each listener... For now, we don't need
	// this
	public void setP1ButtonListeners(GameHUDButton listener) {
		p1JumpListener = listener;
		p1ActListener = listener;
		p1ItemListener = listener;
	}

	public void setP2ButtonListeners(GameHUDButton listener) {
		p2JumpListener = listener;
		p2ActListener = listener;
		p2ItemListener = listener;
	}

	@Override
	public void render(GLCanvas canvas, float timeElapsed) {
		canvas.drawBitmap(R.drawable.ui_buttons, 0,
				getDimension(R.dimen.screen_height)
						- getDimension(R.dimen.ui_buttons_height),
				getDimension(R.dimen.screen_width),
				getDimension(R.dimen.ui_buttons_height));
		
		pauseBt.render(canvas, timeElapsed);
		if (!p1.isDead()){
			canvas.drawBitmap(p1.getStatusImg(), INIT_OF_STATUS_INTERVAL + calculateMapCompletion(p1.body.getPosition().x), 390,
			        getDimension(R.dimen.progression_character_image_size), getDimension(R.dimen.progression_character_image_size));
			
			if (p1.getItem() != null && p1ItemImg == null) {
    			canvas.drawRect(PLAYER_1_ITEM_RECT, p1.getItem().getColor());
    	        canvas.drawBitmap(p1.getItem().getImageDrawable(), PLAYER_1_ITEM_RECT);
    	        
    	        if (p1.getItem().isActive()) {
    	        	
    	        	GLES11.glBlendFunc(GLES11.GL_SRC_ALPHA, GLES11.GL_ONE); // Additive Blending Effect
    	        	
    	        	itemActiveAnim.render(canvas, PLAYER_1_EFFECT_ACTIVE_RECT, timeElapsed);
    
    	            GLES11.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);
    	        } else {
                    GLES11.glBlendFunc(GLES11.GL_SRC_ALPHA, GLES11.GL_ONE); // Additive Blending Effect
                    
                    canvas.saveState();
                    
                    canvas.translate(PLAYER_1_EFFECT_ACTIVE_RECT.centerX(), PLAYER_1_EFFECT_ACTIVE_RECT.centerY());
                    angleStrippedSunP1 = (angleStrippedSunP1 + timeElapsed * 100) % 360f;
                    canvas.rotate(angleStrippedSunP1);
                    canvas.translate(-PLAYER_1_EFFECT_ACTIVE_RECT.centerX(), -PLAYER_1_EFFECT_ACTIVE_RECT.centerY());
                    canvas.drawBitmap(R.drawable.stripped_sun_small, PLAYER_1_EFFECT_ACTIVE_RECT);
                    canvas.restoreState();
    
                    GLES11.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);
    	        }
			} else if (p1ItemImg != null) {
	            canvas.drawBitmap(p1ItemImg.imageId,
                    p1ItemImg.x, p1ItemImg.y,
                    p1ItemImg.width, p1ItemImg.height);
			}
		}
		
        canvas.drawBitmap(R.drawable.item_frame, PLAYER_1_ITEM_FRAME_RECT);

		if (!p2.isDead()){
			canvas.drawBitmap(p2.getStatusImg(), INIT_OF_STATUS_INTERVAL + calculateMapCompletion(p2.body.getPosition().x), 440,
                    getDimension(R.dimen.progression_character_image_size), getDimension(R.dimen.progression_character_image_size));
			if (p2.getItem() != null && p2ItemImg == null) {
                canvas.drawRect(PLAYER_2_ITEM_RECT, p2.getItem().getColor());
    	        canvas.drawBitmap(p2.getItem().getImageDrawable(), PLAYER_2_ITEM_RECT);
    	        
    	        if (p2.getItem().isActive()) {
                    
                    GLES11.glBlendFunc(GLES11.GL_SRC_ALPHA, GLES11.GL_ONE); // Additive Blending Effect
                    
    	        	itemActiveAnim2.render(canvas, PLAYER_2_EFFECT_ACTIVE_RECT, timeElapsed);
    
                    GLES11.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);
    	        } else {
                    GLES11.glBlendFunc(GLES11.GL_SRC_ALPHA, GLES11.GL_ONE); // Additive Blending Effect
                    
                    canvas.saveState();
                    
                    canvas.translate(PLAYER_2_EFFECT_ACTIVE_RECT.centerX(), PLAYER_2_EFFECT_ACTIVE_RECT.centerY());
                    angleStrippedSunP2 = (angleStrippedSunP2 + timeElapsed * 100) % 360f;
                    canvas.rotate(angleStrippedSunP2);
                    canvas.translate(-PLAYER_2_EFFECT_ACTIVE_RECT.centerX(), -PLAYER_2_EFFECT_ACTIVE_RECT.centerY());
                    canvas.drawBitmap(R.drawable.stripped_sun_small, PLAYER_2_EFFECT_ACTIVE_RECT);
                    canvas.restoreState();
    
                    GLES11.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);
                }
			} else if (p2ItemImg != null) {
                canvas.drawBitmap(p2ItemImg.imageId,
                    p2ItemImg.x, p2ItemImg.y,
                    p2ItemImg.width, p2ItemImg.height);
            }
		}
        
        canvas.drawBitmap(R.drawable.item_frame, PLAYER_2_ITEM_FRAME_RECT);

        pauseScreen.render(canvas, timeElapsed);
	}

	@Override
	public void step(float timeElapsed) {
	}

	private int calculateMapCompletion(float pos) {

		float flagXPosition = gWorld.getFlagPos();

		if (totalDistance == Float.MIN_VALUE) {
			totalDistance = flagXPosition - pos;
		}

		int totalCompletion = END_OF_STATUS_INTERVAL
				- (int) (END_OF_STATUS_INTERVAL * (flagXPosition - pos) / totalDistance);

		if (totalCompletion < 0)
			totalCompletion = 0;
		if (totalCompletion > END_OF_STATUS_INTERVAL)
			totalCompletion = END_OF_STATUS_INTERVAL;

		return totalCompletion;
	}

	
	//pauseScreen.input(event);
//	if (pauseScreen.isHidden())
//		ui.input(event);
	
	@Override
	public void input(InputEventHandler event) {
		pauseBt.input(event);
		pauseScreen.input(event);
		if(pauseScreen.isHidden()) {
			int action = event.getAction() & MotionEvent.ACTION_MASK;
			if (action == MotionEvent.ACTION_DOWN
					|| action == MotionEvent.ACTION_POINTER_DOWN) {
				pressButtons(event.getX(), event.getY(), true);
			} else if (action == MotionEvent.ACTION_UP
					|| action == MotionEvent.ACTION_POINTER_UP) {
				pressButtons(event.getX(), event.getY(), false);
			}
		}
		
		
		
	}

	private void pressButtons(float x, float y, boolean pressed) {
		if (y > 340) {
			if (x < 230) {
				if (x < 105) {
					// Jump 1
					if (p1JumpListener != null) {
						p1JumpListener.onClick(BT_PLAYER_1_JUMP, pressed);
					}
				} else {
					// Act 1
					if (p1ActListener != null) {
						p1ActListener.onClick(BT_PLAYER_1_ACT, pressed);
					}
				}
			}

			if (x > 600) {
				if (x < 710) {
					// Jump 2
					if (p2JumpListener != null) {
						p2JumpListener.onClick(BT_PLAYER_2_JUMP, pressed);
					}
				} else {
					// Act 2
					if (p2ActListener != null) {
						p2ActListener.onClick(BT_PLAYER_2_ACT, pressed);
					}
				}
			}
		}

		if (PLAYER_1_ITEM_FRAME_RECT.contains(x, y)) {
		    if (p1ItemListener != null) {
		        p1ItemListener.onClick(BT_PLAYER_1_ITEM, pressed);
		    }
		} else if (PLAYER_2_ITEM_FRAME_RECT.contains(x, y)) {
		    if (p2ItemListener != null) {
		        p2ItemListener.onClick(BT_PLAYER_2_ITEM, pressed);
		    }
		}
	}
	
	public boolean isPauseHidden() {
		return pauseScreen.isHidden();
	}


	// This should be given in Pixels
	public void addItemAnimation(PlayerNumber pNum, float x, float y, Item item) {
	    if (pNum.equals(PlayerNumber.ONE)) {
	        
	        p1ItemImg = new ItemImage();
	        p1ItemImg.x = x;
	        p1ItemImg.y = y;
	        p1ItemImg.imageId = item.getImageDrawable();
            p1ItemImg.width = BoxItem.ITEM_IMAGE_SIZE.width() * GLCanvas.physicsRatio;
            p1ItemImg.height = BoxItem.ITEM_IMAGE_SIZE.height() * GLCanvas.physicsRatio;
	        
	        Tween.to(p1ItemImg, 0, 0.8f)
	        .target(PLAYER_1_ITEM_RECT.centerX(), PLAYER_1_ITEM_RECT.centerY())
	        .setCallback(new TweenCallback() {
                
                @Override
                public void onEvent(int arg0, BaseTween<?> arg1) {
                    p1ItemImg = null;
                }
            })
	        .start(com.arretadogames.pilot.ui.AnimationManager.getInstance());
	        
	    } else if (pNum.equals(PlayerNumber.TWO)) {
	        
	        p2ItemImg = new ItemImage();
            p2ItemImg.x = x;
            p2ItemImg.y = y;
            p2ItemImg.imageId = item.getImageDrawable();
            p2ItemImg.width = BoxItem.ITEM_IMAGE_SIZE.width() * GLCanvas.physicsRatio;
            p2ItemImg.height = BoxItem.ITEM_IMAGE_SIZE.height() * GLCanvas.physicsRatio;
            
            Tween.to(p2ItemImg, 0, 0.8f)
            .target(PLAYER_2_ITEM_RECT.centerX(), PLAYER_2_ITEM_RECT.centerY())
            .setCallback(new TweenCallback() {
                
                @Override
                public void onEvent(int arg0, BaseTween<?> arg1) {
                    p2ItemImg = null;
                }
            })
            .start(com.arretadogames.pilot.ui.AnimationManager.getInstance());
	    }
	}
	
	@Override
	public void onPause() {
		if (pauseScreen.isHidden())
			pauseScreen.show();
	}
	
	@Override
	public void onClick(int buttonId) {
		switch (buttonId) {
		case PAUSE_BT:
			pauseScreen.show();
			break;
		}
		
	}

}
