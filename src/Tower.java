
public interface Tower {

	public String TowerType();// returns a string of what type of block it is

	public void drawTower(double x, double y);// draws the tower on the
												// specified spot

	public boolean on(double x, double y);// returns true if the specified spot
											// is on the tower

	public int getUpgradeCost();// returns the cost of upgrading

	public void upgrade();// upgrades the tower, also contains the upgrade path of the tower

	/**
	 * Swaps whether or not the tower is the "selected" tower. Only one tower is ever selected at any time.
	 */
	public void swapSelected();

	public int cost();// returns cost of tower

	/**
	 *True if the gun is ready to fire, else it counts down.
	 */
	public boolean toFire();// when called checks whether tower is ready

	public int getSplash();// range of splash

	public int getRange();

	public int getDamage();

	public int getSplashDamage();

	public double getSellPrice();

	public double getX();// returns matrix cordinates of the tower

	public double getY();

	/**
	 * Draws a symbol of the selected tower based on location and size given
	 */
	public void drawTowerSymbol(double spotX, double spotY, int size);

	/**
	 * Resets the cooldown on fireing
	 */
	public void resetFire();

	/**
	 * Describes the tower and the next upgrade
	 */
	public String getDescription();
}
