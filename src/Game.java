import java.util.*;
import ecs100.*;

/**
 * My TowerDefense Game
 * 
 * @author tomo1_000
 * @info Next steps: Implement a "glue tower" Get splash tower working fully Add a "hover to see range" instead of a
 *       "click to see range" of towers. Make some towers take up 4/2 blocks. Add some more interesting enemies.
 * 
 *       Latest updates: Added Sell method, Added more interesting upgrade paths for towers
 * 
 */

public class Game {

	public static final double left = 20;
	public static final double top = 20;
	public static final double size = 20;// size of each block
	public static final double blockSize = 30;// size of the blocks array(number
												// of blocks)
	public static final int startPoint = 21;// where the bugs spawn at

	private final double speed = 80;// Game Speed (higher speeds are slower)

	private Block[][] blocks;
	private ArrayList<Tower> towers;
	private ArrayList<Bug> bugs;
	private String towerToMake;
	private int count, lives, round;
	private double money;
	private Tower selectedTower;
	private boolean gg;

	public static void main(String[] args) {
		UI.setWindowSize(1300, 800);
		UI.setDivider(0.0);
		new Game();
		// new MapCreator();
	}

	/**
	 * starts a new game by resetting variables and calling the main runGame method
	 */
	public Game() {
		blocks = new Block[(int) blockSize][(int) blockSize];
		towers = new ArrayList<Tower>();
		bugs = new ArrayList<Bug>();
		this.count = 0;
		this.round = 0;
		this.money = 300;
		this.lives = 100;
		this.gg = false;
		this.setBlocks();
		UI.setImmediateRepaint(false);// will draw the entire board only once every tick
		UI.setMouseMotionListener(this::doMouse);
		UI.addButton("Start Round", this::newRound);
		UI.addButton("Upgrade", this::upgradeTower);
		UI.addButton("Sell", this::sellTower);
		UI.addButton("BASIC Tower", () -> towerToMake = "BasicTower");
		UI.addButton("SPLASH Tower", () -> towerToMake = "SplashTower");
		this.runGame();
	}

	public void runGame() {// main game
		while (!this.gg) {
			this.drawBoard();
			this.moveBugs();
			this.makeBugs();
			this.fireTowers();
			UI.sleep(speed);
			this.count++;
		}
	}

	/**
	 * Clears everything, Draws the blocks and calls the draw menu method
	 */
	public void drawBoard() {// draws the board by going through the array
		UI.clearGraphics();
		for (int i = 0; i < blocks.length; i++) {
			for (int j = 0; j < blocks[0].length; j++) {
				Block bl = blocks[i][j];
				bl.drawBlock(left + j * left, top + i * top);
			}
		}
		for (int j = 0; j < towers.size(); j++) {
			Tower t1 = towers.get(j);
			t1.drawTower(t1.getX(), t1.getY());
		}
		this.drawMenu();
		UI.repaintGraphics();
	}

	/**
	 * Draws the side menus and fills in the text, also highlights selected tower
	 */
	public void drawMenu() {
		int boxWidth = 200;
		UI.setLineWidth(3);
		UI.drawRect(left + size * blockSize, top, boxWidth, size * blockSize);
		UI.drawRect(left + size * blockSize + boxWidth, top, boxWidth / 2, 30);
		UI.drawRect(left + size * blockSize + boxWidth, top + 30, boxWidth / 2, 30);
		UI.drawLine(left + size * blockSize, top + 30, left + size * blockSize + boxWidth, top + 30);
		UI.drawLine(left + size * blockSize, top + 60, left + size * blockSize + boxWidth, top + 60);
		UI.drawLine(left + size * blockSize, top + 180, left + size * blockSize + boxWidth, top + 180);
		UI.drawLine(left + size * blockSize, top + 210, left + size * blockSize + boxWidth, top + 210);
		UI.drawLine(left + size * blockSize + boxWidth / 2, top + 180, left + size * blockSize + boxWidth / 2,
				top + 210);
		UI.setFontSize(15);
		UI.drawString("Lives: " + String.valueOf(this.lives), left + size * blockSize + boxWidth + 10, top + 20);
		UI.drawString("$" + String.valueOf(this.money), left + size * blockSize + boxWidth + 10, top + 50);
		UI.setFontSize(20);
		UI.drawString("ROUND " + String.valueOf(this.round), left + size * blockSize + 50, top + 22);
		if (this.towerToMake != null) {// calls the public static void methods
										// of each tower
			if (this.towerToMake.equals("BasicTower")) {
				UI.drawString("New Basic Tower", left + size * blockSize + 25, top + 52);
				BasicTower.drawBasicTowerSymbol(left + size * blockSize + 40, top + 60, 120);
				String[] s = BasicTower.displayBasic().split("\n");
				int lin = 0;
				for (String a : s) {
					UI.drawString(a, left + size * blockSize + 10, top + 235 + lin);
					lin += 25;
				}
			} else if (this.towerToMake.equals("SplashTower")) {
				UI.drawString("New Splash Tower", left + size * blockSize + 20, top + 52);
				SplashTower.drawSplashTowerSymbol(left + size * blockSize + 40, top + 60, 120);
				String[] s = SplashTower.displayBasic().split("\n");
				int lin = 0;
				for (String a : s) {
					UI.drawString(a, left + size * blockSize + 10, top + 235 + lin);
					lin += 25;
				}
			}
		} else if (selectedTower != null) {
			UI.drawString(selectedTower.TowerType(), left + size * blockSize + 50, top + 52);
			selectedTower.drawTowerSymbol(left + size * blockSize + 40, top + 60, 120);
			String[] s = selectedTower.getDescription().split("\n");
			int lin = 0;
			for (String a : s) {
				UI.drawString(a, left + size * blockSize + 10, top + 235 + lin);
				lin += 25;
			}
		}
		UI.setFontSize(12);
		UI.setLineWidth(1);

	}

	/**
	 * Controls the effects of the mouse to select and place towers
	 */
	public void doMouse(String s, double x, double y) {
		if (s.equals("released") && towerToMake != null && x > left && y > top && x < left + blockSize * size
				&& y < top + blockSize * size) {
			int remainderx = (int) (x % size);
			int remaindery = (int) (y % size);
			x = (x - remainderx) / size - 1;
			y = (y - remaindery) / size - 1;
			this.makeTower(x, y);
			towerToMake = null;
			this.drawBoard();
		} else if (s.equals("released") && towerToMake == null && x > left && y > top && x < left + blockSize * size
				&& y < top + blockSize * size) {
			for (int i = 0; i < towers.size(); i++) {
				if (towers.get(i).on(x, y)) {
					if (selectedTower != null) {
						selectedTower.swapSelected();
					}
					selectedTower = towers.get(i);
					selectedTower.swapSelected();
					this.drawBoard();
					return;
				} else {
					if (selectedTower != null) {
						selectedTower.swapSelected();
					}
					selectedTower = null;
				}
			}
		}
	}

	/**
	 * Makes a new Tower if you have the money to do so and the tower isnt in an invalid area
	 */
	public void makeTower(double x, double y) {// makes a new tower
		if (blocks[(int) y][(int) x].blockType().equals("Wall")) {
			if (towerToMake.equals("BasicTower")) {
				Tower t1 = new BasicTower(x, y);
				if (this.money >= t1.cost()) {
					towers.add(t1);
					this.money -= t1.cost();
					blocks[(int) y][(int) x] = new TowerBlock(x, y);
				}
			} else if (towerToMake.equals("SplashTower")) {
				Tower t1 = new SplashTower(x, y);
				if (this.money >= t1.cost()) {
					towers.add(t1);
					this.money -= t1.cost();
					blocks[(int) y][(int) x] = new TowerBlock(x, y);
				}
			}
		}
	}

	/**
	 * Upgrades the current selected tower if you have the money
	 */
	public void upgradeTower() {
		if (selectedTower != null) {
			if (selectedTower.getUpgradeCost() < this.money) {
				this.money = this.money - selectedTower.getUpgradeCost();
				selectedTower.upgrade();
			}
		}
	}

	public void sellTower() {
		if (selectedTower != null) {
			this.money = this.money + (int) selectedTower.getSellPrice();
			blocks[(int) selectedTower.getY()][(int) selectedTower.getX()] = new Wall(selectedTower.getY(),
					selectedTower.getX());
			this.towers.remove(this.selectedTower);
			this.selectedTower = null;
			this.drawBoard();
		}
	}

	/**
	 * Moves all bugs along by 1 along the path. If the bugs get to the end, they are removed and lives are subtracted
	 */
	public void moveBugs() {
		for (int i = 0; i < bugs.size(); i++) {
			Bug bg = bugs.get(i);
			bg.upCount();
			if (bg.getCounter() % bg.getSpeed() == 0) {
				int x = (int) bg.getX();
				int y = (int) bg.getY();
				if (y + 1 == blockSize) {
					bugs.remove(i);
					i--;
					lives = lives - bg.getLives();
					if (lives < 0) {
						gg = true;
					}
				} else if (blocks[y + 1][x].blockType().equals("Path") && !bg.getPast().equals("up")) {
					bg.moveDown();
					bg.drawBug();
				} else if (blocks[y][x + 1].blockType().equals("Path") && !bg.getPast().equals("left")) {
					bg.moveRight();
					bg.drawBug();
				} else if (blocks[y][x - 1].blockType().equals("Path") && !bg.getPast().equals("right")) {
					bg.moveLeft();
					bg.drawBug();
				} else if (blocks[y - 1][x].blockType().equals("Path") && !bg.getPast().equals("down")) {
					bg.moveUp();
					bg.drawBug();
				}
			}
			bg.drawBug();
		}
		UI.repaintGraphics();
	}

	/**
	 * Initialises the next round
	 */
	public void newRound() {
		if (bugs.size() == 0) {
			this.count = 0;
			this.round++;
		} else {
			UI.println("Havent finished current round");
		}
	}

	/**
	 * Makes new bugs, according to each level's template of bug making
	 */
	public void makeBugs() {
		if (round == 1) {
			this.levelTemplate1();
		} else if (round == 2) {
			this.levelTemplate2();
		} else if (round == 3) {
			this.levelTemplate3();
		} else if (round == 4) {
			this.levelTemplate4();
		} else if (round == 5) {
			this.levelTemplate5();
		} else if (round == 6) {
			this.levelTemplate6();
		} else if (round == 7) {
			this.levelTemplate7();
		}
	}

	/**
	 * Attempts to fire each tower, if the tower is ready to fire it will damage bugs and reset the ready to fire
	 * counter, else it will countdown. "I" Loop loops through all of my towers "J" Loop damages a target bug if they
	 * are in range "K" Loop damage target bugs based on splash damage.
	 */
	public void fireTowers() {
		for (int i = 0; i < towers.size(); i++) {// I loop
			Tower t1 = towers.get(i);
			if (t1.toFire()) {
				double tx = t1.getX() * 20 + left;
				double ty = t1.getY() * 20 + top;
				int range = t1.getRange();
				int splash = t1.getSplash();
				for (int j = 0; j < bugs.size(); j++) {// J loop
					Bug b1 = bugs.get(j);
					double bx = b1.getX() * 20 + left;
					double by = b1.getY() * 20 + top;
					if (Math.hypot(bx - tx, by - ty) <= range) {
						b1.removelife(t1.getDamage());
						if (b1.getLives() <= 0) {
							money = money + 2 * b1.bounty();
							bugs.remove(j);
							j--;
						}
						for (int k = 0; k < bugs.size(); k++) {// K loop
							Bug b2 = bugs.get(k);
							double b2x = b2.getX() * 20 + left;
							double b2y = b2.getY() * 20 + top;
							if (Math.hypot(bx - b2x, by - b2y) < splash) {
								b2.removelife(t1.getSplashDamage());
								if (b2.getLives() <= 0) {
									money = money + 2 * b2.bounty();
									bugs.remove(k);
									k--;
								}
								UI.drawOval(bx + 10 - splash, by + 10 - splash, splash * 2, splash * 2);
							}
						}
						t1.resetFire();
						UI.drawLine(tx + 10, ty + 10, bx + 10, by + 10);
						break;
					}
				}
			}
		}

	}

	/**
	 * These templates are for the levels, each time the makeBugs method is called, one of these templates is run.
	 * Templates should get harder each level, balancing needs to be done.
	 */
	public void levelTemplate1() {
		if (this.count % 4 == 0 && this.count < 100) {
			Bug bg = new Bug(startPoint, 0, "normal");
			bugs.add(bg);
			bg.drawBug();
		}
	}

	public void levelTemplate2() {
		if (this.count < 100) {
			Bug bg = new Bug(startPoint, 0, "normal");
			bugs.add(bg);
			bg.drawBug();
		}
	}

	public void levelTemplate3() {
		if (this.count % 2 == 0 && this.count < 100) {
			Bug bg = new Bug(startPoint, 0, "medium");
			bugs.add(bg);
			bg.drawBug();
		}
	}

	public void levelTemplate4() {
		if (this.count % 4 == 0 && this.count < 100) {
			Bug bg = new Bug(startPoint, 0, "medium");
			bugs.add(bg);
			bg.drawBug();
		} else if (this.count % 4 == 2 && this.count < 100) {
			Bug bg = new Bug(startPoint, 0, "hard");
			bugs.add(bg);
			bg.drawBug();
		}
	}

	public void levelTemplate5() {
		if (this.count % 2 == 0 && this.count < 100) {
			Bug bg = new Bug(startPoint, 0, "hard");
			bugs.add(bg);
			bg.drawBug();
		}
	}

	public void levelTemplate6() {
		if (this.count % 2 == 0 && this.count < 100) {
			Bug bg = new Bug(startPoint, 0, "hard");
			bugs.add(bg);
			bg.drawBug();
		} else if (this.count < 100) {
			Bug bg = new Bug(startPoint, 0, "easy");
			bugs.add(bg);
			bg.drawBug();
		}
	}

	public void levelTemplate7() {
		if (this.count < 100) {
			Bug bg = new Bug(startPoint, 0, "hard");
			bugs.add(bg);
			bg.drawBug();
		}
	}

	public void setBlocks() {// Should load a map into the block array
		this.template1();// draws the map of template 2
	}

	/**
	 * These are the template maps to use. Different templates can be created using the MapCreator class and
	 * copy-pasting the output. Note. Templates should start paths at 0,15 position in order to not cause fuck ups. As
	 * this is where bugs spawn at. Recommended to start by making the entire array a certain block type with a loop.
	 */
	public void template1() {
		for (int i = 0; i < blocks.length; i++) { // makes all blocks in the
													// array walls.
			for (int j = 0; j < blocks[0].length; j++) {
				blocks[i][j] = new Wall(left + left * 0, top + top * 0);
			}
			blocks[0][22] = new Unavailable(left + left * 22, top + top * 0);
			blocks[1][22] = new Unavailable(left + left * 22, top + top * 1);
			blocks[2][22] = new Unavailable(left + left * 22, top + top * 2);
			blocks[3][22] = new Unavailable(left + left * 22, top + top * 3);
			blocks[3][21] = new Path(left + left * 21, top + top * 3);
			blocks[3][20] = new Path(left + left * 20, top + top * 3);
			blocks[3][19] = new Path(left + left * 19, top + top * 3);
			blocks[3][18] = new Path(left + left * 18, top + top * 3);
			blocks[3][17] = new Path(left + left * 17, top + top * 3);
			blocks[3][16] = new Path(left + left * 16, top + top * 3);
			blocks[3][15] = new Path(left + left * 15, top + top * 3);
			blocks[3][14] = new Path(left + left * 14, top + top * 3);
			blocks[3][13] = new Path(left + left * 13, top + top * 3);
			blocks[3][12] = new Path(left + left * 12, top + top * 3);
			blocks[3][11] = new Path(left + left * 11, top + top * 3);
			blocks[3][10] = new Path(left + left * 10, top + top * 3);
			blocks[3][9] = new Path(left + left * 9, top + top * 3);
			blocks[3][8] = new Path(left + left * 8, top + top * 3);
			blocks[3][7] = new Path(left + left * 7, top + top * 3);
			blocks[3][6] = new Path(left + left * 6, top + top * 3);
			blocks[3][5] = new Path(left + left * 5, top + top * 3);
			blocks[3][4] = new Path(left + left * 4, top + top * 3);
			blocks[3][3] = new Path(left + left * 3, top + top * 3);
			blocks[4][3] = new Path(left + left * 3, top + top * 4);
			blocks[5][3] = new Path(left + left * 3, top + top * 5);
			blocks[6][3] = new Path(left + left * 3, top + top * 6);
			blocks[7][3] = new Path(left + left * 3, top + top * 7);
			blocks[8][3] = new Path(left + left * 3, top + top * 8);
			blocks[9][3] = new Path(left + left * 3, top + top * 9);
			blocks[10][3] = new Path(left + left * 3, top + top * 10);
			blocks[11][3] = new Path(left + left * 3, top + top * 11);
			blocks[11][2] = new Path(left + left * 2, top + top * 11);
			blocks[12][2] = new Path(left + left * 2, top + top * 12);
			blocks[11][3] = new Path(left + left * 3, top + top * 11);
			blocks[12][3] = new Path(left + left * 3, top + top * 12);
			blocks[13][3] = new Path(left + left * 3, top + top * 13);
			blocks[14][3] = new Path(left + left * 3, top + top * 14);
			blocks[15][3] = new Path(left + left * 3, top + top * 15);
			blocks[16][3] = new Path(left + left * 3, top + top * 16);
			blocks[17][3] = new Path(left + left * 3, top + top * 17);
			blocks[18][3] = new Path(left + left * 3, top + top * 18);
			blocks[19][3] = new Path(left + left * 3, top + top * 19);
			blocks[20][3] = new Path(left + left * 3, top + top * 20);
			blocks[21][3] = new Path(left + left * 3, top + top * 21);
			blocks[22][3] = new Path(left + left * 3, top + top * 22);
			blocks[22][4] = new Path(left + left * 4, top + top * 22);
			blocks[22][5] = new Path(left + left * 5, top + top * 22);
			blocks[22][6] = new Path(left + left * 6, top + top * 22);
			blocks[22][7] = new Path(left + left * 7, top + top * 22);
			blocks[22][8] = new Path(left + left * 8, top + top * 22);
			blocks[22][9] = new Path(left + left * 9, top + top * 22);
			blocks[22][10] = new Path(left + left * 10, top + top * 22);
			blocks[22][11] = new Path(left + left * 11, top + top * 22);
			blocks[21][11] = new Path(left + left * 11, top + top * 21);
			blocks[20][11] = new Path(left + left * 11, top + top * 20);
			blocks[19][11] = new Path(left + left * 11, top + top * 19);
			blocks[18][11] = new Path(left + left * 11, top + top * 18);
			blocks[17][11] = new Path(left + left * 11, top + top * 17);
			blocks[16][11] = new Path(left + left * 11, top + top * 16);
			blocks[15][11] = new Path(left + left * 11, top + top * 15);
			blocks[14][11] = new Path(left + left * 11, top + top * 14);
			blocks[13][11] = new Path(left + left * 11, top + top * 13);
			blocks[12][11] = new Path(left + left * 11, top + top * 12);
			blocks[11][11] = new Path(left + left * 11, top + top * 11);
			blocks[10][11] = new Path(left + left * 11, top + top * 10);
			blocks[9][11] = new Path(left + left * 11, top + top * 9);
			blocks[8][11] = new Path(left + left * 11, top + top * 8);
			blocks[7][11] = new Path(left + left * 11, top + top * 7);
			blocks[7][12] = new Path(left + left * 12, top + top * 7);
			blocks[7][13] = new Path(left + left * 13, top + top * 7);
			blocks[7][14] = new Path(left + left * 14, top + top * 7);
			blocks[7][15] = new Path(left + left * 15, top + top * 7);
			blocks[7][16] = new Path(left + left * 16, top + top * 7);
			blocks[7][17] = new Path(left + left * 17, top + top * 7);
			blocks[7][18] = new Path(left + left * 18, top + top * 7);
			blocks[8][18] = new Path(left + left * 18, top + top * 8);
			blocks[9][18] = new Path(left + left * 18, top + top * 9);
			blocks[10][18] = new Path(left + left * 18, top + top * 10);
			blocks[11][18] = new Path(left + left * 18, top + top * 11);
			blocks[12][18] = new Path(left + left * 18, top + top * 12);
			blocks[13][18] = new Path(left + left * 18, top + top * 13);
			blocks[14][18] = new Path(left + left * 18, top + top * 14);
			blocks[15][18] = new Path(left + left * 18, top + top * 15);
			blocks[16][18] = new Path(left + left * 18, top + top * 16);
			blocks[17][18] = new Path(left + left * 18, top + top * 17);
			blocks[18][18] = new Path(left + left * 18, top + top * 18);
			blocks[19][18] = new Path(left + left * 18, top + top * 19);
			blocks[20][18] = new Path(left + left * 18, top + top * 20);
			blocks[20][19] = new Path(left + left * 19, top + top * 20);
			blocks[21][19] = new Path(left + left * 19, top + top * 21);
			blocks[21][18] = new Path(left + left * 18, top + top * 21);
			blocks[22][18] = new Path(left + left * 18, top + top * 22);
			blocks[22][19] = new Path(left + left * 19, top + top * 22);
			blocks[22][20] = new Path(left + left * 20, top + top * 22);
			blocks[22][21] = new Path(left + left * 21, top + top * 22);
			blocks[22][22] = new Path(left + left * 22, top + top * 22);
			blocks[22][23] = new Path(left + left * 23, top + top * 22);
			blocks[22][24] = new Path(left + left * 24, top + top * 22);
			blocks[23][24] = new Path(left + left * 24, top + top * 23);
			blocks[24][24] = new Path(left + left * 24, top + top * 24);
			blocks[25][24] = new Path(left + left * 24, top + top * 25);
			blocks[26][24] = new Path(left + left * 24, top + top * 26);
			blocks[27][24] = new Path(left + left * 24, top + top * 27);
			blocks[27][23] = new Path(left + left * 23, top + top * 27);
			blocks[27][22] = new Path(left + left * 22, top + top * 27);
			blocks[27][21] = new Path(left + left * 21, top + top * 27);
			blocks[27][20] = new Path(left + left * 20, top + top * 27);
			blocks[27][19] = new Path(left + left * 19, top + top * 27);
			blocks[27][18] = new Path(left + left * 18, top + top * 27);
			blocks[27][17] = new Path(left + left * 17, top + top * 27);
			blocks[27][16] = new Path(left + left * 16, top + top * 27);
			blocks[27][15] = new Path(left + left * 15, top + top * 27);
			blocks[28][15] = new Path(left + left * 15, top + top * 28);
			blocks[28][14] = new Path(left + left * 14, top + top * 28);
			blocks[28][13] = new Path(left + left * 13, top + top * 28);
			blocks[28][12] = new Path(left + left * 12, top + top * 28);
			blocks[28][11] = new Path(left + left * 11, top + top * 28);
			blocks[28][10] = new Path(left + left * 10, top + top * 28);
			blocks[28][9] = new Path(left + left * 9, top + top * 28);
			blocks[28][8] = new Path(left + left * 8, top + top * 28);
			blocks[28][7] = new Path(left + left * 7, top + top * 28);
			blocks[28][6] = new Path(left + left * 6, top + top * 28);
			blocks[28][5] = new Path(left + left * 5, top + top * 28);
			blocks[28][4] = new Path(left + left * 4, top + top * 28);
			blocks[28][3] = new Path(left + left * 3, top + top * 28);
			blocks[28][2] = new Path(left + left * 2, top + top * 28);
			blocks[29][2] = new Path(left + left * 2, top + top * 29);
			blocks[28][16] = new Path(left + left * 16, top + top * 28);
			blocks[27][16] = new Path(left + left * 16, top + top * 27);
			blocks[27][15] = new Wall(left + left * 15, top + top * 27);
			blocks[20][19] = new Wall(left + left * 19, top + top * 20);
			blocks[21][19] = new Wall(left + left * 19, top + top * 21);
			blocks[12][2] = new Wall(left + left * 2, top + top * 12);
			blocks[11][2] = new Wall(left + left * 2, top + top * 11);
			blocks[10][25] = new Grass(left + left * 25, top + top * 10);
			blocks[11][25] = new Grass(left + left * 25, top + top * 11);
			blocks[11][24] = new Grass(left + left * 24, top + top * 11);
			blocks[12][24] = new Grass(left + left * 24, top + top * 12);
			blocks[13][24] = new Grass(left + left * 24, top + top * 13);
			blocks[14][24] = new Grass(left + left * 24, top + top * 14);
			blocks[14][25] = new Grass(left + left * 25, top + top * 14);
			blocks[13][25] = new Grass(left + left * 25, top + top * 13);
			blocks[13][24] = new Grass(left + left * 24, top + top * 13);
			blocks[14][24] = new Grass(left + left * 24, top + top * 14);
			blocks[15][23] = new Grass(left + left * 23, top + top * 15);
			blocks[16][22] = new Grass(left + left * 22, top + top * 16);
			blocks[15][21] = new Grass(left + left * 21, top + top * 15);
			blocks[14][21] = new Grass(left + left * 21, top + top * 14);
			blocks[13][21] = new Grass(left + left * 21, top + top * 13);
			blocks[13][22] = new Grass(left + left * 22, top + top * 13);
			blocks[12][22] = new Grass(left + left * 22, top + top * 12);
			blocks[12][23] = new Grass(left + left * 23, top + top * 12);
			blocks[11][23] = new Grass(left + left * 23, top + top * 11);
			blocks[11][24] = new Grass(left + left * 24, top + top * 11);
			blocks[11][25] = new Grass(left + left * 25, top + top * 11);
			blocks[11][26] = new Grass(left + left * 26, top + top * 11);
			blocks[12][26] = new Grass(left + left * 26, top + top * 12);
			blocks[13][26] = new Grass(left + left * 26, top + top * 13);
			blocks[14][26] = new Grass(left + left * 26, top + top * 14);
			blocks[15][26] = new Grass(left + left * 26, top + top * 15);
			blocks[16][26] = new Grass(left + left * 26, top + top * 16);
			blocks[16][25] = new Grass(left + left * 25, top + top * 16);
			blocks[17][25] = new Grass(left + left * 25, top + top * 17);
			blocks[16][25] = new Grass(left + left * 25, top + top * 16);
			blocks[16][24] = new Grass(left + left * 24, top + top * 16);
			blocks[16][23] = new Grass(left + left * 23, top + top * 16);
			blocks[15][23] = new Grass(left + left * 23, top + top * 15);
			blocks[15][22] = new Grass(left + left * 22, top + top * 15);
			blocks[14][22] = new Grass(left + left * 22, top + top * 14);
			blocks[13][22] = new Grass(left + left * 22, top + top * 13);
			blocks[12][22] = new Grass(left + left * 22, top + top * 12);
			blocks[11][22] = new Grass(left + left * 22, top + top * 11);
			blocks[10][22] = new Grass(left + left * 22, top + top * 10);
			blocks[10][23] = new Grass(left + left * 23, top + top * 10);
			blocks[9][23] = new Grass(left + left * 23, top + top * 9);
			blocks[9][24] = new Grass(left + left * 24, top + top * 9);
			blocks[8][24] = new Grass(left + left * 24, top + top * 8);
			blocks[8][25] = new Grass(left + left * 25, top + top * 8);
			blocks[7][25] = new Grass(left + left * 25, top + top * 7);
			blocks[6][25] = new Grass(left + left * 25, top + top * 6);
			blocks[7][25] = new Grass(left + left * 25, top + top * 7);
			blocks[8][24] = new Grass(left + left * 24, top + top * 8);
			blocks[9][24] = new Grass(left + left * 24, top + top * 9);
			blocks[10][24] = new Grass(left + left * 24, top + top * 10);
			blocks[11][24] = new Grass(left + left * 24, top + top * 11);
			blocks[12][24] = new Grass(left + left * 24, top + top * 12);
			blocks[13][24] = new Grass(left + left * 24, top + top * 13);
			blocks[14][24] = new Grass(left + left * 24, top + top * 14);
			blocks[15][25] = new Grass(left + left * 25, top + top * 15);
			blocks[16][26] = new Grass(left + left * 26, top + top * 16);
			blocks[17][26] = new Grass(left + left * 26, top + top * 17);
			blocks[18][26] = new Grass(left + left * 26, top + top * 18);
			blocks[15][7] = new Grass(left + left * 7, top + top * 15);
			blocks[16][7] = new Grass(left + left * 7, top + top * 16);
			blocks[17][7] = new Grass(left + left * 7, top + top * 17);
			blocks[17][6] = new Grass(left + left * 6, top + top * 17);
			blocks[16][6] = new Grass(left + left * 6, top + top * 16);
			blocks[16][5] = new Grass(left + left * 5, top + top * 16);
			blocks[15][5] = new Grass(left + left * 5, top + top * 15);
			blocks[14][5] = new Grass(left + left * 5, top + top * 14);
			blocks[13][5] = new Grass(left + left * 5, top + top * 13);
			blocks[12][5] = new Grass(left + left * 5, top + top * 12);
			blocks[12][6] = new Grass(left + left * 6, top + top * 12);
			blocks[11][6] = new Grass(left + left * 6, top + top * 11);
			blocks[10][7] = new Grass(left + left * 7, top + top * 10);
			blocks[9][7] = new Grass(left + left * 7, top + top * 9);
			blocks[10][7] = new Grass(left + left * 7, top + top * 10);
			blocks[14][7] = new Grass(left + left * 7, top + top * 14);
			blocks[15][7] = new Grass(left + left * 7, top + top * 15);
			blocks[16][7] = new Grass(left + left * 7, top + top * 16);
			blocks[17][7] = new Grass(left + left * 7, top + top * 17);
			blocks[18][7] = new Grass(left + left * 7, top + top * 18);
			blocks[17][7] = new Grass(left + left * 7, top + top * 17);
			blocks[16][7] = new Grass(left + left * 7, top + top * 16);
			blocks[15][7] = new Grass(left + left * 7, top + top * 15);
			blocks[14][7] = new Grass(left + left * 7, top + top * 14);
			blocks[13][7] = new Grass(left + left * 7, top + top * 13);
			blocks[12][7] = new Grass(left + left * 7, top + top * 12);
			blocks[11][7] = new Grass(left + left * 7, top + top * 11);
			blocks[14][7] = new Grass(left + left * 7, top + top * 14);
			blocks[15][7] = new Grass(left + left * 7, top + top * 15);
			blocks[16][7] = new Grass(left + left * 7, top + top * 16);
			blocks[16][6] = new Grass(left + left * 6, top + top * 16);
			blocks[15][6] = new Grass(left + left * 6, top + top * 15);
			blocks[14][6] = new Grass(left + left * 6, top + top * 14);
			blocks[12][6] = new Grass(left + left * 6, top + top * 12);
			blocks[12][7] = new Grass(left + left * 7, top + top * 12);
			blocks[12][15] = new Grass(left + left * 15, top + top * 12);
			blocks[13][15] = new Grass(left + left * 15, top + top * 13);
			blocks[13][14] = new Grass(left + left * 14, top + top * 13);
			blocks[14][14] = new Grass(left + left * 14, top + top * 14);
			blocks[15][14] = new Grass(left + left * 14, top + top * 15);
			blocks[16][14] = new Grass(left + left * 14, top + top * 16);
			blocks[17][14] = new Grass(left + left * 14, top + top * 17);
			blocks[17][15] = new Grass(left + left * 15, top + top * 17);
			blocks[18][15] = new Grass(left + left * 15, top + top * 18);
			blocks[17][15] = new Grass(left + left * 15, top + top * 17);
			blocks[16][15] = new Grass(left + left * 15, top + top * 16);
			blocks[15][15] = new Grass(left + left * 15, top + top * 15);
			blocks[14][15] = new Grass(left + left * 15, top + top * 14);
			blocks[13][15] = new Grass(left + left * 15, top + top * 13);
			blocks[14][15] = new Grass(left + left * 15, top + top * 14);
			blocks[15][15] = new Grass(left + left * 15, top + top * 15);
			blocks[16][15] = new Grass(left + left * 15, top + top * 16);
			blocks[24][10] = new Grass(left + left * 10, top + top * 24);
			blocks[25][9] = new Grass(left + left * 9, top + top * 25);
			blocks[25][8] = new Grass(left + left * 8, top + top * 25);
			blocks[25][7] = new Grass(left + left * 7, top + top * 25);
			blocks[25][6] = new Grass(left + left * 6, top + top * 25);
			blocks[25][7] = new Grass(left + left * 7, top + top * 25);
			blocks[25][8] = new Grass(left + left * 8, top + top * 25);
			blocks[25][9] = new Grass(left + left * 9, top + top * 25);
			blocks[25][10] = new Grass(left + left * 10, top + top * 25);
			blocks[25][11] = new Grass(left + left * 11, top + top * 25);
			blocks[25][12] = new Grass(left + left * 12, top + top * 25);
			blocks[25][11] = new Grass(left + left * 11, top + top * 25);
			blocks[25][10] = new Grass(left + left * 10, top + top * 25);
			blocks[24][9] = new Grass(left + left * 9, top + top * 24);
			blocks[24][8] = new Grass(left + left * 8, top + top * 24);
			blocks[25][8] = new Grass(left + left * 8, top + top * 25);
			blocks[25][9] = new Grass(left + left * 9, top + top * 25);
			blocks[25][10] = new Grass(left + left * 10, top + top * 25);
			blocks[25][11] = new Grass(left + left * 11, top + top * 25);
			blocks[24][11] = new Grass(left + left * 11, top + top * 24);
			blocks[24][10] = new Grass(left + left * 10, top + top * 24);
			blocks[25][10] = new Grass(left + left * 10, top + top * 25);
			blocks[25][9] = new Grass(left + left * 9, top + top * 25);
			blocks[25][8] = new Grass(left + left * 8, top + top * 25);
			blocks[25][7] = new Grass(left + left * 7, top + top * 25);
			blocks[25][8] = new Grass(left + left * 8, top + top * 25);
			blocks[13][6] = new Grass(left + left * 6, top + top * 13);
			blocks[15][24] = new Grass(left + left * 24, top + top * 15);
			blocks[14][24] = new Grass(left + left * 24, top + top * 14);
			blocks[14][23] = new Grass(left + left * 23, top + top * 14);
			blocks[13][23] = new Grass(left + left * 23, top + top * 13);
			blocks[12][25] = new Grass(left + left * 25, top + top * 12);
			blocks[9][25] = new Grass(left + left * 25, top + top * 9);
			blocks[10][25] = new Grass(left + left * 25, top + top * 10);
			blocks[10][26] = new Grass(left + left * 26, top + top * 10);
			blocks[9][26] = new Grass(left + left * 26, top + top * 9);
			blocks[8][26] = new Grass(left + left * 26, top + top * 8);
			blocks[7][26] = new Grass(left + left * 26, top + top * 7);
			blocks[8][27] = new Grass(left + left * 27, top + top * 8);
			blocks[9][27] = new Grass(left + left * 27, top + top * 9);
			blocks[10][27] = new Grass(left + left * 27, top + top * 10);
			blocks[10][28] = new Grass(left + left * 28, top + top * 10);
			blocks[11][28] = new Grass(left + left * 28, top + top * 11);
			blocks[10][28] = new Grass(left + left * 28, top + top * 10);
			blocks[9][28] = new Grass(left + left * 28, top + top * 9);
			blocks[10][28] = new Grass(left + left * 28, top + top * 10);
			blocks[11][28] = new Grass(left + left * 28, top + top * 11);
			blocks[12][28] = new Grass(left + left * 28, top + top * 12);
			blocks[12][27] = new Grass(left + left * 27, top + top * 12);
			blocks[13][27] = new Grass(left + left * 27, top + top * 13);
			blocks[14][27] = new Grass(left + left * 27, top + top * 14);
			blocks[13][27] = new Grass(left + left * 27, top + top * 13);
			blocks[12][27] = new Grass(left + left * 27, top + top * 12);
			blocks[11][27] = new Grass(left + left * 27, top + top * 11);
			blocks[13][28] = new Grass(left + left * 28, top + top * 13);
			blocks[14][28] = new Grass(left + left * 28, top + top * 14);
			blocks[14][27] = new Grass(left + left * 27, top + top * 14);
			blocks[15][27] = new Grass(left + left * 27, top + top * 15);
			blocks[16][27] = new Grass(left + left * 27, top + top * 16);
			blocks[17][27] = new Grass(left + left * 27, top + top * 17);
			blocks[17][28] = new Grass(left + left * 28, top + top * 17);
			blocks[16][28] = new Grass(left + left * 28, top + top * 16);
			blocks[15][28] = new Grass(left + left * 28, top + top * 15);
			blocks[2][21] = new Unavailable(left + left * 21, top + top * 2);
			blocks[2][20] = new Unavailable(left + left * 20, top + top * 2);
			blocks[1][20] = new Unavailable(left + left * 20, top + top * 1);
			blocks[1][19] = new Unavailable(left + left * 19, top + top * 1);
			blocks[0][19] = new Unavailable(left + left * 19, top + top * 0);
			blocks[0][18] = new Unavailable(left + left * 18, top + top * 0);
			blocks[2][23] = new Unavailable(left + left * 23, top + top * 2);
			blocks[2][24] = new Unavailable(left + left * 24, top + top * 2);
			blocks[1][24] = new Unavailable(left + left * 24, top + top * 1);
			blocks[1][25] = new Unavailable(left + left * 25, top + top * 1);
			blocks[0][25] = new Unavailable(left + left * 25, top + top * 0);
			blocks[0][26] = new Unavailable(left + left * 26, top + top * 0);
			blocks[0][24] = new Unavailable(left + left * 24, top + top * 0);
			blocks[0][23] = new Unavailable(left + left * 23, top + top * 0);
			blocks[1][23] = new Unavailable(left + left * 23, top + top * 1);
			blocks[1][21] = new Unavailable(left + left * 21, top + top * 1);
			blocks[0][21] = new Unavailable(left + left * 21, top + top * 0);
			blocks[0][20] = new Unavailable(left + left * 20, top + top * 0);
			blocks[3][23] = new Unavailable(left + left * 23, top + top * 3);
			blocks[1][9] = new Grass(left + left * 9, top + top * 1);
			blocks[1][10] = new Grass(left + left * 10, top + top * 1);
			blocks[1][9] = new Grass(left + left * 9, top + top * 1);
			blocks[1][8] = new Grass(left + left * 8, top + top * 1);
			blocks[1][11] = new Grass(left + left * 11, top + top * 1);
			blocks[1][10] = new Grass(left + left * 10, top + top * 1);
			blocks[1][9] = new Grass(left + left * 9, top + top * 1);
			blocks[1][8] = new Grass(left + left * 8, top + top * 1);
			blocks[1][7] = new Grass(left + left * 7, top + top * 1);
			blocks[1][6] = new Grass(left + left * 6, top + top * 1);
			blocks[1][5] = new Grass(left + left * 5, top + top * 1);
			blocks[0][5] = new Grass(left + left * 5, top + top * 0);
			blocks[0][4] = new Grass(left + left * 4, top + top * 0);
			blocks[0][3] = new Grass(left + left * 3, top + top * 0);
			blocks[0][2] = new Grass(left + left * 2, top + top * 0);
			blocks[0][6] = new Grass(left + left * 6, top + top * 0);
			blocks[0][7] = new Grass(left + left * 7, top + top * 0);
			blocks[0][8] = new Grass(left + left * 8, top + top * 0);
			blocks[0][9] = new Grass(left + left * 9, top + top * 0);
			blocks[0][10] = new Grass(left + left * 10, top + top * 0);
			blocks[0][11] = new Grass(left + left * 11, top + top * 0);
			blocks[0][12] = new Grass(left + left * 12, top + top * 0);
			blocks[0][13] = new Grass(left + left * 13, top + top * 0);
			blocks[0][14] = new Grass(left + left * 14, top + top * 0);
			blocks[0][15] = new Grass(left + left * 15, top + top * 0);
			blocks[0][16] = new Grass(left + left * 16, top + top * 0);
			blocks[0][17] = new Grass(left + left * 17, top + top * 0);
			blocks[9][29] = new Grass(left + left * 29, top + top * 9);
			blocks[10][29] = new Grass(left + left * 29, top + top * 10);
			blocks[11][29] = new Grass(left + left * 29, top + top * 11);
			blocks[12][29] = new Grass(left + left * 29, top + top * 12);
			blocks[13][29] = new Grass(left + left * 29, top + top * 13);
			blocks[14][29] = new Grass(left + left * 29, top + top * 14);
			blocks[14][28] = new Grass(left + left * 28, top + top * 14);
			blocks[15][28] = new Grass(left + left * 28, top + top * 15);
			blocks[16][28] = new Grass(left + left * 28, top + top * 16);
			blocks[17][28] = new Grass(left + left * 28, top + top * 17);
			blocks[17][29] = new Grass(left + left * 29, top + top * 17);
			blocks[16][29] = new Grass(left + left * 29, top + top * 16);
			blocks[15][29] = new Grass(left + left * 29, top + top * 15);
			blocks[14][29] = new Grass(left + left * 29, top + top * 14);
			blocks[15][29] = new Grass(left + left * 29, top + top * 15);
			blocks[16][29] = new Grass(left + left * 29, top + top * 16);
			blocks[17][29] = new Grass(left + left * 29, top + top * 17);
			blocks[18][29] = new Grass(left + left * 29, top + top * 18);
			blocks[19][29] = new Grass(left + left * 29, top + top * 19);
			blocks[22][29] = new Grass(left + left * 29, top + top * 22);
			blocks[23][29] = new Grass(left + left * 29, top + top * 23);
			blocks[22][29] = new Grass(left + left * 29, top + top * 22);
			blocks[20][29] = new Grass(left + left * 29, top + top * 20);
			blocks[8][29] = new Grass(left + left * 29, top + top * 8);
			blocks[7][29] = new Grass(left + left * 29, top + top * 7);
			blocks[6][29] = new Grass(left + left * 29, top + top * 6);
			blocks[5][29] = new Grass(left + left * 29, top + top * 5);
			blocks[6][28] = new Grass(left + left * 28, top + top * 6);
			blocks[28][28] = new Grass(left + left * 28, top + top * 28);
			blocks[29][28] = new Grass(left + left * 28, top + top * 29);
			blocks[29][29] = new Grass(left + left * 29, top + top * 29);
			blocks[28][29] = new Grass(left + left * 29, top + top * 28);
			blocks[0][21] = new Path(left + left * 22, top + top * 0);
			blocks[1][21] = new Path(left + left * 22, top + top * 1);
			blocks[2][21] = new Path(left + left * 22, top + top * 2);
			blocks[3][21] = new Path(left + left * 22, top + top * 3);
		}
	}

	public void template2() {// map 2, new maps can be created by running the
								// map creator and copypasting the output

		for (int i = 0; i < blocks.length; i++) { // makes all blocks in the
													// array walls.
			for (int j = 0; j < blocks[0].length; j++) {
				blocks[i][j] = new Wall(left + left * 0, top + top * 0);
			}
		}
		blocks[0][21] = new Path(left + left * 21, top + top * 0);
		blocks[1][21] = new Path(left + left * 21, top + top * 1);
		blocks[2][21] = new Path(left + left * 21, top + top * 2);
		blocks[3][21] = new Path(left + left * 21, top + top * 3);
		blocks[4][21] = new Path(left + left * 21, top + top * 4);
		blocks[5][21] = new Path(left + left * 21, top + top * 5);
		blocks[6][21] = new Path(left + left * 21, top + top * 6);
		blocks[7][21] = new Path(left + left * 21, top + top * 7);
		blocks[7][20] = new Path(left + left * 20, top + top * 7);
		blocks[7][19] = new Path(left + left * 19, top + top * 7);
		blocks[7][18] = new Path(left + left * 18, top + top * 7);
		blocks[7][17] = new Path(left + left * 17, top + top * 7);
		blocks[7][16] = new Path(left + left * 16, top + top * 7);
		blocks[7][15] = new Path(left + left * 15, top + top * 7);
		blocks[7][14] = new Path(left + left * 14, top + top * 7);
		blocks[7][13] = new Path(left + left * 13, top + top * 7);
		blocks[7][12] = new Path(left + left * 12, top + top * 7);
		blocks[7][11] = new Path(left + left * 11, top + top * 7);
		blocks[7][10] = new Path(left + left * 10, top + top * 7);
		blocks[7][9] = new Path(left + left * 9, top + top * 7);
		blocks[7][8] = new Path(left + left * 8, top + top * 7);
		blocks[7][7] = new Path(left + left * 7, top + top * 7);
		blocks[7][6] = new Path(left + left * 6, top + top * 7);
		blocks[7][5] = new Path(left + left * 5, top + top * 7);
		blocks[7][4] = new Path(left + left * 4, top + top * 7);
		blocks[7][3] = new Path(left + left * 3, top + top * 7);
		blocks[8][3] = new Path(left + left * 3, top + top * 8);
		blocks[9][3] = new Path(left + left * 3, top + top * 9);
		blocks[10][3] = new Path(left + left * 3, top + top * 10);
		blocks[11][3] = new Path(left + left * 3, top + top * 11);
		blocks[11][4] = new Path(left + left * 4, top + top * 11);
		blocks[11][5] = new Path(left + left * 5, top + top * 11);
		blocks[11][6] = new Path(left + left * 6, top + top * 11);
		blocks[11][7] = new Path(left + left * 7, top + top * 11);
		blocks[11][8] = new Path(left + left * 8, top + top * 11);
		blocks[11][9] = new Path(left + left * 9, top + top * 11);
		blocks[11][10] = new Path(left + left * 10, top + top * 11);
		blocks[11][11] = new Path(left + left * 11, top + top * 11);
		blocks[11][12] = new Path(left + left * 12, top + top * 11);
		blocks[11][13] = new Path(left + left * 13, top + top * 11);
		blocks[11][14] = new Path(left + left * 14, top + top * 11);
		blocks[11][15] = new Path(left + left * 15, top + top * 11);
		blocks[11][16] = new Path(left + left * 16, top + top * 11);
		blocks[11][17] = new Path(left + left * 17, top + top * 11);
		blocks[11][18] = new Path(left + left * 18, top + top * 11);
		blocks[11][19] = new Path(left + left * 19, top + top * 11);
		blocks[11][20] = new Path(left + left * 20, top + top * 11);
		blocks[11][21] = new Path(left + left * 21, top + top * 11);
		blocks[11][22] = new Path(left + left * 22, top + top * 11);
		blocks[11][23] = new Path(left + left * 23, top + top * 11);
		blocks[11][24] = new Path(left + left * 24, top + top * 11);
		blocks[11][25] = new Path(left + left * 25, top + top * 11);
		blocks[12][25] = new Path(left + left * 25, top + top * 12);
		blocks[13][25] = new Path(left + left * 25, top + top * 13);
		blocks[14][25] = new Path(left + left * 25, top + top * 14);
		blocks[15][25] = new Path(left + left * 25, top + top * 15);
		blocks[16][25] = new Path(left + left * 25, top + top * 16);
		blocks[17][25] = new Path(left + left * 25, top + top * 17);
		blocks[18][25] = new Path(left + left * 25, top + top * 18);
		blocks[19][25] = new Path(left + left * 25, top + top * 19);
		blocks[20][25] = new Path(left + left * 25, top + top * 20);
		blocks[21][25] = new Path(left + left * 25, top + top * 21);
		blocks[22][25] = new Path(left + left * 25, top + top * 22);
		blocks[23][24] = new Path(left + left * 24, top + top * 23);
		blocks[23][23] = new Path(left + left * 23, top + top * 23);
		blocks[23][24] = new Path(left + left * 24, top + top * 23);
		blocks[23][25] = new Path(left + left * 25, top + top * 23);
		blocks[23][24] = new Path(left + left * 24, top + top * 23);
		blocks[23][23] = new Path(left + left * 23, top + top * 23);
		blocks[23][22] = new Path(left + left * 22, top + top * 23);
		blocks[23][21] = new Path(left + left * 21, top + top * 23);
		blocks[23][20] = new Path(left + left * 20, top + top * 23);
		blocks[22][20] = new Path(left + left * 20, top + top * 22);
		blocks[22][19] = new Path(left + left * 19, top + top * 22);
		blocks[21][19] = new Path(left + left * 19, top + top * 21);
		blocks[20][19] = new Path(left + left * 19, top + top * 20);
		blocks[19][19] = new Path(left + left * 19, top + top * 19);
		blocks[18][19] = new Path(left + left * 19, top + top * 18);
		blocks[17][19] = new Path(left + left * 19, top + top * 17);
		blocks[16][19] = new Path(left + left * 19, top + top * 16);
		blocks[15][19] = new Path(left + left * 19, top + top * 15);
		blocks[15][18] = new Path(left + left * 18, top + top * 15);
		blocks[15][17] = new Path(left + left * 17, top + top * 15);
		blocks[15][16] = new Path(left + left * 16, top + top * 15);
		blocks[15][15] = new Path(left + left * 15, top + top * 15);
		blocks[15][14] = new Path(left + left * 14, top + top * 15);
		blocks[15][13] = new Path(left + left * 13, top + top * 15);
		blocks[15][12] = new Path(left + left * 12, top + top * 15);
		blocks[15][11] = new Path(left + left * 11, top + top * 15);
		blocks[15][10] = new Path(left + left * 10, top + top * 15);
		blocks[15][9] = new Path(left + left * 9, top + top * 15);
		blocks[15][8] = new Path(left + left * 8, top + top * 15);
		blocks[15][7] = new Path(left + left * 7, top + top * 15);
		blocks[16][7] = new Path(left + left * 7, top + top * 16);
		blocks[17][7] = new Path(left + left * 7, top + top * 17);
		blocks[18][7] = new Path(left + left * 7, top + top * 18);
		blocks[19][7] = new Path(left + left * 7, top + top * 19);
		blocks[20][7] = new Path(left + left * 7, top + top * 20);
		blocks[21][7] = new Path(left + left * 7, top + top * 21);
		blocks[22][7] = new Path(left + left * 7, top + top * 22);
		blocks[22][6] = new Path(left + left * 6, top + top * 22);
		blocks[22][7] = new Path(left + left * 7, top + top * 22);
		blocks[22][8] = new Path(left + left * 8, top + top * 22);
		blocks[22][9] = new Path(left + left * 9, top + top * 22);
		blocks[22][10] = new Path(left + left * 10, top + top * 22);
		blocks[22][11] = new Path(left + left * 11, top + top * 22);
		blocks[22][12] = new Path(left + left * 12, top + top * 22);
		blocks[22][13] = new Path(left + left * 13, top + top * 22);
		blocks[22][14] = new Path(left + left * 14, top + top * 22);
		blocks[23][14] = new Path(left + left * 14, top + top * 23);
		blocks[24][14] = new Path(left + left * 14, top + top * 24);
		blocks[25][14] = new Path(left + left * 14, top + top * 25);
		blocks[26][14] = new Path(left + left * 14, top + top * 26);
		blocks[27][14] = new Path(left + left * 14, top + top * 27);
		blocks[28][14] = new Path(left + left * 14, top + top * 28);
		blocks[29][14] = new Path(left + left * 14, top + top * 29);
		blocks[23][19] = new Path(left + left * 19, top + top * 23);
		blocks[22][20] = new Wall(left + left * 20, top + top * 22);
		blocks[22][6] = new Wall(left + left * 6, top + top * 22);
		blocks[0][20] = new Unavailable(left + left * 20, top + top * 0);
		blocks[1][20] = new Unavailable(left + left * 20, top + top * 1);
		blocks[2][20] = new Unavailable(left + left * 20, top + top * 2);
		blocks[3][20] = new Unavailable(left + left * 20, top + top * 3);
		blocks[3][19] = new Unavailable(left + left * 19, top + top * 3);
		blocks[2][19] = new Unavailable(left + left * 19, top + top * 2);
		blocks[2][18] = new Unavailable(left + left * 18, top + top * 2);
		blocks[1][18] = new Unavailable(left + left * 18, top + top * 1);
		blocks[1][17] = new Unavailable(left + left * 17, top + top * 1);
		blocks[0][17] = new Unavailable(left + left * 17, top + top * 0);
		blocks[0][16] = new Unavailable(left + left * 16, top + top * 0);
		blocks[4][20] = new Unavailable(left + left * 20, top + top * 4);
		blocks[4][22] = new Unavailable(left + left * 22, top + top * 4);
		blocks[3][22] = new Unavailable(left + left * 22, top + top * 3);
		blocks[3][23] = new Unavailable(left + left * 23, top + top * 3);
		blocks[3][24] = new Unavailable(left + left * 24, top + top * 3);
		blocks[2][24] = new Unavailable(left + left * 24, top + top * 2);
		blocks[2][25] = new Unavailable(left + left * 25, top + top * 2);
		blocks[1][25] = new Unavailable(left + left * 25, top + top * 1);
		blocks[1][26] = new Unavailable(left + left * 26, top + top * 1);
		blocks[0][26] = new Unavailable(left + left * 26, top + top * 0);
		blocks[0][27] = new Unavailable(left + left * 27, top + top * 0);
		blocks[0][26] = new Unavailable(left + left * 26, top + top * 0);
		blocks[0][25] = new Unavailable(left + left * 25, top + top * 0);
		blocks[0][24] = new Unavailable(left + left * 24, top + top * 0);
		blocks[0][23] = new Unavailable(left + left * 23, top + top * 0);
		blocks[0][22] = new Unavailable(left + left * 22, top + top * 0);
		blocks[1][22] = new Unavailable(left + left * 22, top + top * 1);
		blocks[2][22] = new Unavailable(left + left * 22, top + top * 2);
		blocks[2][23] = new Unavailable(left + left * 23, top + top * 2);
		blocks[2][24] = new Unavailable(left + left * 24, top + top * 2);
		blocks[1][24] = new Unavailable(left + left * 24, top + top * 1);
		blocks[1][23] = new Unavailable(left + left * 23, top + top * 1);
		blocks[1][19] = new Unavailable(left + left * 19, top + top * 1);
		blocks[0][19] = new Unavailable(left + left * 19, top + top * 0);
		blocks[0][18] = new Unavailable(left + left * 18, top + top * 0);
		blocks[3][18] = new Unavailable(left + left * 18, top + top * 3);
		blocks[2][17] = new Unavailable(left + left * 17, top + top * 2);
		blocks[1][16] = new Unavailable(left + left * 16, top + top * 1);
		blocks[0][15] = new Unavailable(left + left * 15, top + top * 0);
		blocks[2][26] = new Unavailable(left + left * 26, top + top * 2);
		blocks[1][27] = new Unavailable(left + left * 27, top + top * 1);
		blocks[0][28] = new Unavailable(left + left * 28, top + top * 0);
		blocks[2][16] = new Unavailable(left + left * 16, top + top * 2);
		blocks[1][15] = new Unavailable(left + left * 15, top + top * 1);
		blocks[0][14] = new Unavailable(left + left * 14, top + top * 0);

	}
}
