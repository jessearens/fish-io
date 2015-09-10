package com.github.fishio.gui;

import static org.junit.Assert.assertSame;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

import org.junit.After;
import org.testfx.framework.junit.ApplicationTest;

import com.github.fishio.FishIO;
import com.github.fishio.Preloader;
import com.github.fishio.view.MainMenuController;
import com.github.fishio.view.SinglePlayerController;
import com.github.fishio.view.SplashScreenController;

/**
 * Base class for GUI Tests.
 */
public class GuiTest extends ApplicationTest {
	private static final FishIO FISH_IO = new FishIO();
	private Stage stage;
	
	private MainMenuController mainMenuController;
	private SinglePlayerController singleController;
	private SplashScreenController splashController;
	
	private Scene mainScene, singleScene, splashScene;
	
	private volatile boolean loaded = false;
	
	@Override
	public void start(Stage stage) throws Exception {
		this.stage = stage;
		
		FISH_IO.start(stage);
		
		splashScene = Preloader.loadScreen("splashScreen");
		splashController = (SplashScreenController) splashScene.getProperties().get("Controller");
		
		mainScene = Preloader.loadScreen("mainMenu");
		mainMenuController = (MainMenuController) mainScene.getProperties().get("Controller");
		
		singleScene = Preloader.loadScreen("singlePlayer");
		singleController = (SinglePlayerController) singleScene.getProperties().get("Controller");
	}
	
	/**
	 * Called after every test.<br>
	 * <br>
	 * Allows the splash screen to be re skipped.
	 * 
	 * @throws Exception
	 * 		if an Exception occurs.
	 */
	@After
	public void breakDown() throws Exception {
		//Allow the splash to be rerun
		splashController.allowRerun();
		
		//Switch back to the splash screen.
		Platform.runLater(() -> {
			Preloader.switchTo("splashScreen", 0);
			
			loaded = true;
		});
		
		//Wait for the task to be executed.
		while (!loaded) {
			Thread.sleep(50L);
		}
		
		loaded = false;
	}
	
	/**
	 * @return
	 * 		the scene that is currently displayed.
	 */
	public Scene getCurrentScene() {
		return stage.getScene();
	}
	
	/**
	 * Checks if the given scene is the currently displayed scene.
	 * 
	 * @param scene
	 * 		the scene to check
	 * 
	 * @return
	 * 		if the given scene is the currently displayed scene.
	 */
	public boolean isCurrentScene(Scene scene) {
		return scene == getCurrentScene();
	}
	
	/**
	 * Skips the splash screen.
	 */
	public void skipSplash() {
		//Press a key to skip the splash
		press(KeyCode.SPACE);
		
		//Assert that we are now on the main screen.
		assertSame(mainScene, getCurrentScene());
	}
	
	/**
	 * @return
	 * 		the primary stage.
	 */
	public Stage getStage() {
		return stage;
	}
	
	/**
	 * @return
	 * 		the ScreenController for the Main Menu.
	 */
	public MainMenuController getMainMenuController() {
		return mainMenuController;
	}
	
	/**
	 * @return
	 * 		the ScreenController for the Single Player screen.
	 */
	public SinglePlayerController getSinglePlayerController() {
		return singleController;
	}
	
	/**
	 * @return
	 * 		the ScreenController for the splash screen.
	 */
	public SplashScreenController getSplashScreenController() {
		return splashController;
	}
	
	/**
	 * @return
	 * 		the scene for the Main Menu Screen.
	 */
	public Scene getMainMenuScene() {
		return mainScene;
	}
	
	/**
	 * @return
	 * 		the scene for the Single Player Screen.
	 */
	public Scene getSinglePlayerScene() {
		return singleScene;
	}
	
	/**
	 * @return
	 * 		the scene for the Splash Screen.
	 */
	public Scene getSplashScene() {
		return splashScene;
	}
}
