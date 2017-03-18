
public interface Block {

	public String blockType();// returns a string of what type of block it is

	public void drawBlock(double x, double y);// draws the block in the
												// specified spot

	public boolean on(double x, double y);// returns true if the specified spot
											// is on the block

}
