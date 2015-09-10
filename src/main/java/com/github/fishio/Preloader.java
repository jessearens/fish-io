package com.github.fishio;

import java.io.IOException;
import java.util.HashMap;

import javafx.animation.FadeTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import com.github.fishio.view.ScreenController;

/**
 * Class to preload sprites.
 */
public final class Preloader {
	private Preloader() { }
	
	/**
	 * A map which holds the loaded screens.
	 */
	public static final HashMap<String, Scene> SCREENS = new HashMap<String, Scene>();
	
	/**
	 * A map which holds the loaded images.
	 */
	public static final HashMap<String, Image> IMAGES = new HashMap<String, Image>();
	
	/**
	 * An empty scene for indicating that a screen is still being loaded.
	 */
	private static final Scene EMPTY_SCENE = new Scene(new HBox());
	
	/**
	 * Preload all the screens.
	 */
	public static void preloadScreens() {
		//Create a new thread to load the screens.
		Thread thread = new Thread(() -> {
			loadScreen("mainMenu");
			loadScreen("singlePlayer");
			
			//We don't load the splash screen, because it is shown immediately.
		});
		
		thread.start();
	}
	
	/**
	 * Preload all images.
	 */
	public static void preloadImages() {
		Thread thread = new Thread(() -> {
			tryPreLoad("background.png");
			tryPreLoad("logo.png");
			
			//Load fish sprites
			tryPreLoad("sprites/fish/playerFish.png");
			for (int i = 0; i < 29; i++) {
				tryPreLoad("sprites/fish/fish" + i + ".png");
			}
			
			tryPreLoad("sprites/fish/special/barrelFish.png");
			tryPreLoad("sprites/fish/special/clownFish1.png");
			tryPreLoad("sprites/fish/special/clownFish2.png");
			tryPreLoad("sprites/fish/special/jellyfish.png");
			tryPreLoad("sprites/fish/special/submarine.png");
			tryPreLoad("sprites/fish/special/swordfish.png");
			tryPreLoad("sprites/fish/special/turtle.png");
			
			tryPreLoad("sprites/anchor1.png");
			tryPreLoad("sprites/anchor2.png");
			tryPreLoad("sprites/fishingPole.png");
			tryPreLoad("sprites/float.png");
			tryPreLoad("sprites/seaweed1.png");
			tryPreLoad("sprites/starfish.png");
		});
		
		thread.start();
	}
	
	/**
	 * Attempts to preload an image.<br>
	 * <br>
	 * If loading the image causes an Exception, an error message is output to System.err.
	 * 
	 * @param file
	 * 		the file of the image.
	 */
	private static void tryPreLoad(String file) {
		synchronized (IMAGES) {
			if (IMAGES.containsKey(file)) {
				return;
			}
		}
		
		Image image;
		try {
			image = new Image(file);
		} catch (Exception ex) {
			System.err.println("Error while trying to load image " + file);
			return;
		}
		
		synchronized (IMAGES) {
			IMAGES.put(file, image);
		}
	}
	
	/**
	 * Gets an Image for the given filepath.<br>
	 * If it is not loaded, it loads the Image.
	 * 
	 * @param file
	 * 		the file of the Image.
	 * 
	 * @return
	 * 		the image
	 */
	public static Image getImageOrLoad(String file) {
		synchronized (IMAGES) {
			Image image = IMAGES.get(file);
			if (image != null) {
				return image;
			}
		}
		
		Image image = new Image(file);
		synchronized (IMAGES) {
			IMAGES.put(file, image);
		}
		return image;
	}
	
	/**
	 * Gets the preloaded image from the given file, if it is loaded.<br>
	 * If not, this method throws an IllegalArgumentException.
	 * 
	 * @param file
	 * 		the filepath of the image to get.
	 * 
	 * @return
	 * 		the image at the given filepath.
	 * 
	 * @throws IllegalArgumentException
	 * 		if the image for this filepath is not yet loaded.
	 */
	public static Image getImage(String file) {
		synchronized (IMAGES) {
			Image image = IMAGES.get(file);
			if (image != null) {
				return image;
			} else {
				throw new IllegalArgumentException("No image loaded for " + file + "!");
			}
		}
	}
	
	/**
	 * Load the screen from the given file, and store it for later use.
	 * 
	 * @param filename
	 * 		the name of the file to load (without extension).
	 * 
	 * @return
	 * 		the scene that has been loaded.
	 */
	public static Scene loadScreen(String filename) {
		
		synchronized (SCREENS) {
			Scene scene = SCREENS.get(filename);
			if (scene != null) {
				return scene;
			}
			
			//Indicate that we are loading the screen by putting the EMPTY_SCENE in the map.
			SCREENS.put(filename, EMPTY_SCENE);
		}
		
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(Preloader.class.getResource("view/" + filename + ".fxml"));

		try {
			Pane rootLayout = (Pane) loader.load();
			ScreenController controller = ((ScreenController) loader.getController());
			if (controller == null) {
				System.err.println("Screen controller not found for " + filename);
				return null;
			}

			Scene scene = new Scene(rootLayout);
			
			//Set the controller as userdata for the scene.
			scene.getProperties().put("Controller", controller);
			
			//Initialize the controller
			try {
				controller.init(scene);
			} catch (Exception ex) {
				System.err.println("Error while initializing controller for " + filename);
			}
			
			synchronized (SCREENS) {
				SCREENS.put(filename, scene);
			}
			
			return scene;
		} catch (IOException e) {
			System.err.println("Error loading screen:" + filename);
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Changes the screen to the screen with the given filename (without extension),
	 * with a FadeTransition of length milliseconds.
	 * 
	 * @param filename
	 * 		The filename of the screen to switch to.
	 * @param length
	 * 		The length in milliseconds of the FadeTransition.
	 * 
	 * @throws LoaderException
	 * 		if a screen is still being loaded, and while waiting for it to be done loaded,
	 * 		we get interrupted.
	 */
	public static void switchTo(String filename, int length) {
		Scene scene;
		synchronized (SCREENS) {
			scene = SCREENS.get(filename);
		}
		
		if (scene == null) {
			throw new IllegalArgumentException("No screen with name " + filename + " is loaded!");
		} else if (scene == EMPTY_SCENE) {
			//Screen is being loaded, so sleep for a bit and try again
			while (scene == EMPTY_SCENE) {
				try {
					Thread.sleep(50L);
					
					synchronized (SCREENS) {
						scene = SCREENS.get(filename);
					}
				} catch (InterruptedException ex) {
					throw new LoaderException("Interrupted while waiting for screen to get loaded!", ex);
				}
			}
		}
		
		showScreen(scene, length);
	}
	
	/**
	 * Load a screen from a fxml-file and show it.
	 * 
	 * @param filename
	 * 			filename of the fxml file.
	 * @param length
	 * 			If &gt; 0, fade in the new screen, else just show it.
	 */
	public static void loadAndShowScreen(String filename, int length) {
		Scene scene;
		synchronized (SCREENS) {
			scene = SCREENS.get(filename);
		}
		
		if (scene == null) {
			scene = loadScreen(filename);
		}
		
		showScreen(scene, length);
	}
	
	/**
	 * Shows the given scene on the screen.
	 * 
	 * @param scene
	 * 		the scene to show.
	 * @param length
	 * 		the length of the FadeTransition for this screen.
	 */
	private static void showScreen(Scene scene, int length) {
		ScreenController controller = (ScreenController) scene.getProperties().get("Controller");
		controller.onSwitchTo();
		
		//DoubleProperty opacity = scene.getRoot().opacityProperty();
		
		if (length > 0) {
			scene.getRoot().setOpacity(0);
			FishIO.getInstance().getPrimaryStage().setScene(scene);
			
			FadeTransition fade = new FadeTransition(Duration.millis(length), scene.getRoot());
			fade.setFromValue(0.0);
			fade.setToValue(1.0);
			fade.play();
		} else {
			FishIO.getInstance().getPrimaryStage().setScene(scene);
		}
	}
}