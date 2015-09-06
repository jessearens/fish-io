package com.github.fishio;

import java.util.ArrayList;

import com.github.fishio.listeners.TickListener;
import com.github.fishio.view.SinglePlayerController;

import javafx.scene.canvas.Canvas;

/**
 * Represents a playing field designed for single player.
 */
public class SinglePlayerPlayingField extends PlayingField {

	private PlayerFish player;
	
	/**
	 * @param fps
	 * 		the (target) framerate.
	 * @param canvas
	 * 		the canvas to use, can be <code>null</code> to create one.
	 * @param screenController
	 * 		the screenController on which this playing field is located on.
	 */
	public SinglePlayerPlayingField(int fps, Canvas canvas, SinglePlayerController screenController) {
		super(fps, canvas);
		
		//Adding the playerFish
		player = new PlayerFish(new BoundingBox(new Vec2d(640, 335), 100, 64), 
				FishIO.getInstance().getPrimaryStage());
		add(player);
		
		//Checking if the playerFish died
		registerGameListener(new TickListener() {
			@Override
			public void preTick() { }

			@Override
			public void postTick() {
				if (player.isDead()) {
					screenController.showDeathScreen(true);
					stopGame();
					clear();
				}
			}
		});
	}

	@Override
	public ArrayList<PlayerFish> getPlayers() {
		ArrayList<PlayerFish> res = new ArrayList<>();
		res.add(player);
		return res;
	}

}