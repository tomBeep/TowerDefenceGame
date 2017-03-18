import java.awt.Color;
import ecs100.UI;

public class SplashTower implements Tower {

	private double x, y;// matrix coordinates
	private int baseFireRate = 20;// fires once every 6 moves
	private int fireRate = 20; // current cooldown on fire rate
	private int range;
	private int cost = 120;
	private int splash = 60;// don't make it perfect as it looks bad
	private int splashDamage = 1;
	private int damage = 0;
	private double sellPrice;
	private int level = 1;// level for upgrades
	private boolean selected = false;
	
	private static int baseRange = 80;

	public SplashTower(double x, double y) {
		this.x = x;
		this.y = y;
		this.sellPrice = 0.5 * this.cost;
		this.range = baseRange;
	}

	public static String displayBasic() {//displays the basic information about the tower
		return ("Splash Tower\n" + "Damage: 1\n" + "Splash: 60\n" + "Cost: $120\n");
	}
	
	public static void drawSplashTowerSymbol(double spotX, double spotY, int size) {
		double[] xcoords = { spotX + size / 2, spotX + size * 5 / 8, spotX + size, spotX + size * 6 / 8,
				spotX + size * 7 / 8, spotX + size / 2, spotX + size * 1 / 8, spotX + size * 2 / 8, spotX,
				spotX + size * 3 / 8 };
		double[] ycoords = { spotY, spotY + size * 1 / 3, spotY + size * 1 / 3, spotY + size * 2 / 3, spotY + size,
				spotY + size * 2 / 3, spotY + size, spotY + size * 2 / 3, spotY + size * 1 / 3, spotY + size * 1 / 3 };
		UI.fillPolygon(xcoords, ycoords, 10);
	}

	public String TowerType() {
		return "SplashTower";
	}

	public String getDescription() {//gets a far more detailed description about the tower
		String upgradeDescription;
		if (level == 1) {
			upgradeDescription = "$100\n\nIncreases the \nrange by 40";
		} else if (level == 2) {
			upgradeDescription = "$150\n\nIncreases the \nfireRate by 5";
		} else if (level == 3) {
			upgradeDescription = "$500\n\nDoubles Damage";
		} else {
			upgradeDescription = "No further upgrades";
		}
		String rate = "FireRate: " + this.baseFireRate;
		String damage = "Damage: " + this.damage;
		String splash = "Splash: " + this.splash;
		String range = "Range: " + this.range;
		return (rate + "\n" + damage + "\n" + splash + "\n" + range + "\n\n" + "NextUpgrade: " + upgradeDescription);
	}

	public void upgrade() {
		if (level == 1) {
			this.range = this.range + 40;
		} else if (level == 2) {
			this.baseFireRate = this.baseFireRate - 2;
		} else if (level == 3) {
			this.damage += 1;
		}
		this.sellPrice += 0.5 * getUpgradeCost();
		level++;
	}

	public int getUpgradeCost() {
		if (level == 1) {
			return 100;
		} else if (level == 2) {
			return 200;
		} else if (level == 3) {
			return 500;
		} else {
			return 0;
		}
	}

	public void swapSelected() {
		this.selected = !this.selected;
	}

	public void drawTower(double x, double y) {// needs rework of icon
		double spotX = x * Game.size + Game.left;
		double spotY = y * Game.size + Game.top;
		double[] xcoords = { spotX + Game.size / 2, spotX + Game.size * 5 / 8, spotX + Game.size,
				spotX + Game.size * 6 / 8, spotX + Game.size * 7 / 8, spotX + Game.size / 2, spotX + Game.size * 1 / 8,
				spotX + Game.size * 2 / 8, spotX, spotX + Game.size * 3 / 8 };
		double[] ycoords = { spotY, spotY + Game.size * 1 / 3, spotY + Game.size * 1 / 3, spotY + Game.size * 2 / 3,
				spotY + Game.size, spotY + Game.size * 2 / 3, spotY + Game.size, spotY + Game.size * 2 / 3,
				spotY + Game.size * 1 / 3, spotY + Game.size * 1 / 3 };
		UI.fillPolygon(xcoords, ycoords, 10);
		if (selected) {
			UI.setLineWidth(2);
			UI.setColor(Color.red);
			UI.drawOval(spotX + 10 - range, spotY + 10 - range, this.range * 2, this.range * 2);
			UI.drawRect(spotX, spotY, 20, 20);
			UI.setColor(Color.black);
			UI.setLineWidth(1);
		}

	}

	public boolean on(double x, double y) {
		if (this.x * 20 + Game.left <= x && (this.x + 1) * 20 + Game.left >= x && this.y * 20 + Game.top <= y
				&& (this.y + 1) * 20 + Game.top >= y) {
			return true;
		} else {
			return false;
		}
	}

	public int getRange() {
		return this.range;
	}

	public int getSplash() {
		return this.splash;
	}

	public double getX() {
		return this.x;
	}

	public double getY() {
		return this.y;
	}

	public double getSellPrice() {
		return this.sellPrice;
	}

	public int cost() {
		return this.cost;
	}

	public int getDamage() {
		return this.damage;
	}

	public int getSplashDamage() {
		return this.splashDamage;
	}

	public boolean toFire() {
		if (fireRate == 0) {
			return true;
		} else {
			fireRate--;
			return false;
		}
	}

	public void drawTowerSymbol(double spotX, double spotY, int size) {
		double[] xcoords = { spotX + size / 2, spotX + size * 5 / 8, spotX + size, spotX + size * 6 / 8,
				spotX + size * 7 / 8, spotX + size / 2, spotX + size * 1 / 8, spotX + size * 2 / 8, spotX,
				spotX + size * 3 / 8 };
		double[] ycoords = { spotY, spotY + size * 1 / 3, spotY + size * 1 / 3, spotY + size * 2 / 3, spotY + size,
				spotY + size * 2 / 3, spotY + size, spotY + size * 2 / 3, spotY + size * 1 / 3, spotY + size * 1 / 3 };
		UI.fillPolygon(xcoords, ycoords, 10);
	}

	public void resetFire() {
		this.fireRate = baseFireRate;
	}

}
