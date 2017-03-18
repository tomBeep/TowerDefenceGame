import java.awt.Color;
import ecs100.*;

/*#
 * This is a map creator, created maps should start their paths at 0,15.
 * Simply copy-paste the output of this into a new map template in the main game, 
 * then set the game to run that template.
 * @author tomo1_000
 *
 */
public class MapCreator {

	public static final double left = 20;
	public static final double top = 20;
	public static final double size = 20;
	private String selected = "Wall";
	private String current;

	public MapCreator() {
		this.drawDefault();
		UI.setMouseMotionListener(this::doMouse);
		UI.addButton("Wall", () -> selected = "Wall");
		UI.addButton("Path", () -> selected = "Path");
		UI.addButton("Unavailable", () -> selected = "Unavailable");
		UI.addButton("Grass", () -> selected = "Grass");

	}

	public void doMouse(String s, double x, double y) {
		if (x > left && y > top && x < left + size * 30 && y < top + size * 30
				&& (s.equals("released") || s.equals("dragged"))) {
			int remainderx = (int) (x % size);
			int remaindery = (int) (y % size);
			x = (x - remainderx) / size;
			y = (y - remaindery) / size;
			String now = (selected + " " + x + " " + y);
			if (!now.equals(current)) {
				current = now;
				UI.printf("blocks[%.0f][%.0f] = new %s (left+left*%.0f,top+top*%.0f);\n", y - 1, x - 1, selected, x - 1,
						y - 1);
			}
			if (selected.equals("Wall")) {
				UI.setColor(Color.red);
			} else if (selected.equals("Path")) {
				UI.setColor(Color.blue);
			} else if (selected.equals("Unavailable")) {
				UI.setColor(Color.GRAY);
			}else if (selected.equals("Grass")) {
				UI.setColor(Color.green.darker().darker());
			}
			UI.fillRect(x * 20, y * 20, size, size);
			UI.setColor(Color.black);
		}
	}

	public void drawDefault() {
		for (int i = 0; i < 30; i++) {
			for (int j = 0; j < 30; j++) {
				if (j == 0) {
					UI.drawLine(left + j * size, top + i * size, left + j * size, top + (i + 1) * size);
				}
				if (i == 0) {
					UI.drawLine(left + j * size, top + i * top, size + (j + 1) * left, size + (i) * size);
				}
				if (i == 29) {
					UI.drawLine(left + (j) * size, top + (i + 1) * size, left + (j + 1) * size, top + (i + 1) * size);
				}
				if (j == 29) {
					UI.drawLine(left + (j + 1) * size, top + (i) * size, left + (j + 1) * size, top + (i + 1) * size);
				}if(i ==0 && j == 21){
					UI.drawOval(left + (j + 1) * size, top + (i) * size, 20, 20);
				}
			}
		}

	}
}
