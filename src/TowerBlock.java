import java.awt.Color;
import ecs100.*;

public class TowerBlock implements Block {

	private double size = 20;// size of each block
	private double x, y;// location of block

	public TowerBlock(double x, double y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public String blockType() {
		// TODO Auto-generated method stub
		return "TowerBlock";
	}

	@Override
	public void drawBlock(double x, double y) {
		UI.setColor(Color.pink.darker());
		UI.fillRect(x, y, size, size);
		UI.setColor(Color.black);
		UI.drawRect(x, y, size, size);
	}

	public boolean on(double x, double y) {
		if (x > this.x && x < this.x + this.size && y > this.y && y < this.y + size) {
			return true;
		} else {
			return false;
		}
	}

}