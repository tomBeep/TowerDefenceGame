import java.awt.Color;
import ecs100.*;

/**
 * 
 * @info Bugs are moddeled off the enemies in bloons. Harder enemies have more layers to them and require more hits to
 *       take down. As an enemy is hit, it changes color and changes speeds.
 * 
 * @type normal, medium, hard, vhard,ehard have 1,2,3,4,5 lives each respectfully
 * @type kilo has 40 lives
 * @type mega has 80 lives
 * @type boss has 200 lives
 */
public class Bug {

	private double x, y;// matrix coordinates of bug
	private double left = 20;
	private double top = 20;
	private String type;
	private String past = "down";
	private int lives;
	private double size = 20;// do not change, instead change extra size to make
								// bugs bigger/smaller
	private double extraSize = 4;
	private int speed;// larger numbers mean slower speeds
	private int counter = 0;

	public Bug(double x, double y, String type) {
		this.x = x;
		this.y = y;
		this.type = type;
		if (type.equals("normal")) {
			this.lives = 1;
		} else if (type.equals("medium")) {
			this.lives = 2;
		} else if (type.equals("hard")) {
			this.lives = 3;
		} else if (type.equals("vhard")) {//very-hard
			this.lives = 4;
		} else if (type.equals("ehard")) {//extremely-hard
			this.lives = 5;
		} else if (type.equals("kilo")) {
			this.lives = 40;
		} else if (type.equals("mega")) {
			this.lives = 80;
		} else if (type.equals("boss")) {
			this.lives = 200;
		}
		this.setSpeed();
	}

	public void upCount() {
		this.counter++;
	}

	public int getCounter() {
		return this.counter;
	}

	public int getSpeed() {
		return this.speed;
	}

	/**
	 * ATM, the speed of each bug is based on how many lives they currently have with.
	 */
	public void setSpeed() {
		if (lives == 1) {
			this.speed = 2;
		} else if (lives == 2) {
			this.speed = 2;
		} else if (lives == 3) {
			this.speed = 2;
		} else if (lives == 4) {
			this.speed = 3;
		} else if (lives == 5) {
			this.speed = 3;
		} else {
			this.speed = 5;
		}
	}

	public double getX() {
		return this.x;
	}

	public double getY() {
		return this.y;
	}

	public void removelife(int num) {
		this.lives = this.lives - num;
		this.setSpeed();
	}

	/**
	 * Returns the current life value of the bug
	 */
	public int getLives() {
		return this.lives;
	}

	public void moveDown() {
		this.y = this.y + 1;
		this.past = "down";
	}

	public void moveLeft() {
		this.x = this.x - 1;
		this.past = "left";
	}

	public void moveRight() {
		this.x = this.x + 1;
		this.past = "right";
	}

	public void moveUp() {
		this.y = this.y - 1;
		this.past = "up";
	}

	public String getPast() {
		return this.past;
	}

	public String getType() {
		return this.type;
	}

	/**
	 * @return how much money is gained from killing the bug
	 */
	public int bounty() {
		if (type.equals("normal")) {
			return 1;
		} else if (type.equals("medium")) {
			return 2;
		} else if (type.equals("hard")) {
			return 3;
		} else if (type.equals("vhard")) {
			return 4;
		} else if (type.equals("ehard")) {
			return 5;
		} else if (type.equals("kilo")) {
			return 40;
		} else if (type.equals("mega")) {
			return 80;
		} else if (type.equals("boss")) {
			return 200;
		} else {
			return 0;
		}
	}

	public void drawBug() {
		if (lives == 1) {
			UI.setColor(Color.red);
		} else if (lives == 2) {
			UI.setColor(Color.blue);
		} else if (lives == 3) {
			UI.setColor(Color.green);
		} else if (lives == 4) {
			UI.setColor(Color.yellow.darker());
		} else if (lives == 5) {
			UI.setColor(Color.magenta);
		} else {
			UI.setColor(Color.black);
		}
		UI.fillOval(left + x * left - extraSize / 2, top + top * y - extraSize / 2, size + extraSize, size + extraSize);
		UI.setColor(Color.black);// following should be special boss design
									// effects
		if (type.equals("kilo")) {

		} else if (type.equals("mega")) {

		} else if (type.equals("boss")) {

		}
	}
}
