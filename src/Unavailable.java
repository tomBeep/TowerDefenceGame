import java.awt.Color;
import ecs100.*;

public class Unavailable implements Block {

	private double size = 20;// size of each block
	private double x, y;// location of block

	public Unavailable(double x, double y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public String blockType() {
		// TODO Auto-generated method stub
		return "Unavailable";
	}

	@Override
	public void drawBlock(double x, double y) {
		// TODO Auto-generated method stub
		UI.setColor(Color.GRAY);
		UI.fillRect(x, y, size, size);
		UI.setColor(Color.black);
	}

	public boolean on(double x, double y) {
		if (x > this.x && x < this.x + this.size && y > this.y && y < this.y + size) {
			return true;
		} else {
			return false;
		}
	}

}