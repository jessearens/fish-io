package com.github.fishio;

/**
 * This class represents an object that can be collided with.
 */
public interface ICollidable {
	/**
	 * Called when this collidable collides with another collidable.
	 * 
	 * @param other
	 * 		The collidable that we have collided with.
	 */
	void onCollide(ICollidable other);
	
	/**
	 * The Collision Box is used to determine when one object collides with another object.
	 * 
	 * @return
	 * 		the collision box of this ICollidable.
	 */
	ICollisionArea getBoundingArea();
	
	/**
	 * This method checks whether the collidable object collides with the given
	 * collidable.
	 * 
	 * @param other
	 *            the Collidable to check.
	 * @return if this collidable collides with the given collidable.
	 */
	default boolean doesCollides(ICollidable other) {
		if (other == null) {
			return false;
		}
		
		return getBoundingArea().intersects(other.getBoundingArea());
	}
}
