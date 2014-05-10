package com.arretadogames.pilot.screens;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Rect;
import android.graphics.RectF;
import android.support.v4.view.GestureDetectorCompat;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;

import com.arretadogames.pilot.MainActivity;
import com.arretadogames.pilot.R;
import com.arretadogames.pilot.accounts.Account;
import com.arretadogames.pilot.accounts.AccountManager;
import com.arretadogames.pilot.config.GameSettings;
import com.arretadogames.pilot.database.GameDatabase;
import com.arretadogames.pilot.database.descriptors.StoreItemDescriptor;
import com.arretadogames.pilot.game.Game;
import com.arretadogames.pilot.game.GameState;
import com.arretadogames.pilot.loading.FontLoader;
import com.arretadogames.pilot.loading.FontLoader.FontTypeFace;
import com.arretadogames.pilot.render.opengl.GLCanvas;
import com.arretadogames.pilot.ui.GameButtonListener;
import com.arretadogames.pilot.ui.ImageButton;
import com.arretadogames.pilot.ui.ItemWidget;
import com.arretadogames.pilot.ui.Text;
import com.arretadogames.pilot.util.Assets;

public class GameStore extends GameScreen implements GameButtonListener, OnGestureListener {

	private static final int BACK_BT = 2;
	
	private static final int WIDGETS_X_OFFSET = 110;
	private static final int Y_DISTANCE_BETWEEN_WIDGETS = 150;
	private static final int INITIAL_WIDGETS_Y = 135;
	
	private static final int SCROLLING_AREA_WIDTH = 580;
	
	// This constant will tell how much damping the fling will have (higher values will make the fling less effective)
	private static final float FLING_DAMPING = 0.9f;
	// This constant will tell how much initial force the fling will have (higher values will make the fling more powerful)
	private static final float FLING_PUSH_FORCE = 0.02f;

	private Text storeLabel;
	private Text moneyLabel;
	private RectF seedRenderingMoneyRect = new RectF(0, 0, 45, 45);
	private int currentY = INITIAL_WIDGETS_Y;
	private final Rect SCROLLING_AREA_RECT;

	private InputEventHandler event;
	private ArrayList<ItemWidget> storeItems = new ArrayList<ItemWidget>();
	private ImageButton buttonBack;
	private GestureDetectorCompat mDetector;
	
	private final int MAX_CURRENT_Y;

	public GameStore() {
		List<StoreItemDescriptor> items = GameDatabase.getInstance().getStoreItems();

		for (StoreItemDescriptor item : items) {
			ItemWidget widget = new ItemWidget(0, 0, 0, 580, 150, item);
			storeItems.add(widget);
		}
		
		if (storeItems.size() >= 4) {
			MAX_CURRENT_Y = (storeItems.size() - 3) * Y_DISTANCE_BETWEEN_WIDGETS;
		} else {
			MAX_CURRENT_Y = - INITIAL_WIDGETS_Y;
		}
		
		event = new InputEventHandler(null); // MotionEvent will be set later
		
		SCROLLING_AREA_RECT = new Rect(WIDGETS_X_OFFSET, INITIAL_WIDGETS_Y, WIDGETS_X_OFFSET + SCROLLING_AREA_WIDTH,
		        (int) getDimension(R.dimen.screen_height));

		buttonBack = new ImageButton(BACK_BT,
				702, 388,
                getDimension(R.dimen.main_menu_button_size),
                getDimension(R.dimen.main_menu_button_size), this,
				R.drawable.back_bt, //bt_back_selected,
				R.drawable.back_bt);// bt_back_unselected);
		
		mDetector = new GestureDetectorCompat(MainActivity.getContext(), this);
	}

	@Override
	public void render(GLCanvas canvas, float timeElapsed) {

		canvas.drawBitmap(R.drawable.store_background,
		        0, 0, getDimension(R.dimen.screen_width), getDimension(R.dimen.screen_height),
		        0, getDimension(R.dimen.store_bg_extra_height));
		
		if (currentY > INITIAL_WIDGETS_Y) {
			currentY = INITIAL_WIDGETS_Y;
		} else if (currentY <  -MAX_CURRENT_Y) {
			currentY = - MAX_CURRENT_Y;
		}
		
		renderWidgets(canvas, timeElapsed);
		
		canvas.drawBitmap(R.drawable.store_background_header,
		        0, 0, getDimension(R.dimen.screen_width), 100);

		canvas.drawBitmap(R.drawable.store_top, 100, 68, getDimension(R.dimen.store_header_width), getDimension(R.dimen.store_header_height));

		if ( AccountManager.get().getAccount1() != null) { // SyncManager.get().isSignedIn() &&
			if (storeLabel == null || moneyLabel == null ||
					AccountManager.get().getAccount1().getCoins() > 0) {


				createUserInfoLabels();
//				p1Coins = AccountManager.get().getAccount1().getCoins();
//				labelsAreRelatedToAccountProvider = true;
			}

			seedRenderingMoneyRect.right = 10 + seedRenderingMoneyRect.width();
			seedRenderingMoneyRect.left = 10;
			seedRenderingMoneyRect.bottom = 13 + seedRenderingMoneyRect.height();
			seedRenderingMoneyRect.top = 13;
			canvas.drawBitmap(R.drawable.seed1, seedRenderingMoneyRect);

			storeLabel.render(canvas, timeElapsed);
			moneyLabel.render(canvas, timeElapsed);
		}

		buttonBack.render(canvas, timeElapsed);
	}

	private void createUserInfoLabels() {
		Account acc = AccountManager.get().getAccount1();
		storeLabel = new Text(333, 100, "Store",
				FontLoader.getInstance().getFont(FontTypeFace.TRANSMETALS_STORE), 1.3f, false);
		moneyLabel = new Text(70, 34, String.valueOf(acc.getCoins()),
				FontLoader.getInstance().getFont(FontTypeFace.TRANSMETALS_STORE), 0.9f, false);
	}

	@Override
	public void step(float timeElapsed) {
		
		currentY += currentFlingVelocity;
		currentFlingVelocity *= FLING_DAMPING;
	}

	@Override
	public void input(InputEventHandler event) {
		mDetector.onTouchEvent(event.getEvent());
	}

	@Override
	public void onPause() {

	}

	@Override
	public void onClick(int buttonId) {
		switch (buttonId) {
		case BACK_BT:
			Assets.mainMenuMusic.play();
			Game.getInstance().goTo(GameState.MAIN_MENU);
			break;
		}
	}

	private void renderWidgets(GLCanvas canvas, float timeElapsed) {
		
		canvas.saveState();
		canvas.translate(WIDGETS_X_OFFSET,  currentY);
		for (int i = 0; i < storeItems.size() ; i++) {
			storeItems.get(i).render(canvas, timeElapsed);
			canvas.translate(0,  Y_DISTANCE_BETWEEN_WIDGETS);
		}
		
		canvas.restoreState();

	}

	@Override
	public void onBackPressed() {
		Assets.mainMenuMusic.play();
		Game.getInstance().goTo(GameState.MAIN_MENU);
	}

	@Override
	public boolean onDown(MotionEvent e) {
		event.setMotionEvent(e);
		return buttonBack.input(event);
	}
	
	private float currentFlingVelocity = 0;

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		event.setMotionEvent(e1);
		if (SCROLLING_AREA_RECT.contains((int)event.getX(), (int)event.getY())) {
			currentFlingVelocity += (velocityY / GameSettings.HeightRatio) * FLING_PUSH_FORCE;
			return true;
		}
		
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		event.setMotionEvent(e1);
		if (SCROLLING_AREA_RECT.contains((int)event.getX(), (int)event.getY())) {
			currentY -= distanceY / GameSettings.HeightRatio;
			return true;
		}
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		event.setMotionEvent(e);
		
		boolean clicked = false;
		event.setOffsetX(-WIDGETS_X_OFFSET);
		for (int i = 0 ; i < storeItems.size() ; i++) {
			event.setOffsetY( - currentY - Y_DISTANCE_BETWEEN_WIDGETS * i);
			clicked |= storeItems.get(i).input(event);
		}
		event.setOffsetY(0);
		event.setOffsetX(0);
		clicked |= buttonBack.input(event);
		
		return clicked;
	}
}
