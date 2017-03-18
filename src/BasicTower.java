import java.awt.Color;

import ecs100.UI;

public class BasicTower implements Tower {

	private double x, y;// matrix coordinates
	private int baseFireRate = 6;// fires once every 6 moves
	private int fireRate = 6; // current cooldown on fire rate
	private int range;
	private int cost = 60;
	private int splash = 0;
	private int splashDamage = 0;
	private int damage = 1;
	private int level = 1;// level for upgrades
	private double sellPrice;
	private boolean selected = false;
	
	private static int baseRange = 90;

	public BasicTower(double x, double y) {
		this.x = x;
		this.y = y;
		this.sellPrice = 0.5 * this.cost;
		this.range = baseRange;
	}

	public String TowerType() {
		return "BasicTower";
	}

	public static String displayBasic() {
		return ("Basic Tower\n" + "Damage: 1\n" + "Splash: 0\n" + "Cost: $60\n");
	}

	public static void drawBasicTowerSymbol(double spotX, double spotY, int size) {
		UI.setLineWidth((int) size / 10);
		double[] xcoords = { spotX + size / 2, spotX + size / 2, spotX + size / 4, spotX + size / 2,
				spotX + size * 3 / 4, spotX + size / 2 };
		double[] ycoords = { spotY + size * 5 / 6, spotY + size / 5, spotY + size / 2, spotY + size / 6,
				spotY + size / 2, spotY + size / 5 };
		UI.drawPolygon(xcoords, ycoords, 6);
		UI.setLineWidth(1);
	}

	public String getDescription() {
		String upgradeDescription;
		if (level == 1) {
			upgradeDescription = "$50\n\nIncreases the \nrange by 30";
		} else if (level == 2) {
			upgradeDescription = "$80\n\nIncreases the \nfireRate by 1";
		} else if (level == 3) {
			upgradeDescription = "$300\n\nIncreases range\nand fireRate";
		} else if (level == 4) {
			upgradeDescription = "$1000\n\nIncreases fireRate\nand damage";
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
			this.range = this.range + 30;
		} else if (level == 2) {
			this.baseFireRate = this.baseFireRate - 1;
		} else if (level == 3) {
			this.baseFireRate = this.baseFireRate - 2;
			this.range = this.range + 20;
		} else if (level == 4) {
			this.baseFireRate = this.baseFireRate - 2;
			this.damage += 1;
		}
		this.sellPrice += 0.5 * getUpgradeCost();
		level++;
	}

	public int getUpgradeCost() {
		if (level == 1) {
			return 50;
		} else if (level == 2) {
			return 80;
		} else if (level == 3) {
			return 300;
		} else if (level == 4) {
			return 1000;
		} else {
			return 0;
		}
	}

	public void swapSelected() {
		this.selected = !this.selected;
	}

	public void drawTower(double x, double y) {
		UI.setLineWidth(2);
		double spotX = x * Game.size + Game.left;
		double spotY = y * Game.size + Game.top;
		double[] xcoords = { spotX + Game.size / 2, spotX + Game.size / 2, spotX + Game.size / 4, spotX + Game.size / 2,
				spotX + Game.size * 3 / 4, spotX + Game.size / 2 };
		double[] ycoords = { spotY + Game.size * 5 / 6, spotY + Game.size / 5, spotY + Game.size / 2,
				spotY + Game.size / 6, spotY + Game.size / 2, spotY + Game.size / 5 };
		UI.drawPolygon(xcoords, ycoords, 6);
		if (selected) {
			UI.setColor(Color.red);
			UI.drawOval(spotX + 10 - range, spotY + 10 - range, this.range * 2, this.range * 2);
			UI.drawRect(spotX, spotY, 20, 20);
			UI.setColor(Color.black);
		}
		UI.setLineWidth(1);
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

	public int cost() {
		return this.cost;
	}

	public double getSellPrice() {
		return this.sellPrice;
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
		UI.setLineWidth((int) size / 10);
		double[] xcoords = { spotX + size / 2, spotX + size / 2, spotX + size / 4, spotX + size / 2,
				spotX + size * 3 / 4, spotX + size / 2 };
		double[] ycoords = { spotY + size * 5 / 6, spotY + size / 5, spotY + size / 2, spotY + size / 6,
				spotY + size / 2, spotY + size / 5 };
		UI.drawPolygon(xcoords, ycoords, 6);
		UI.setLineWidth(1);
	}

	public void resetFire() {
		this.fireRate = baseFireRate;
	}

}
