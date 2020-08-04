/*
 * Generic cration of a HuffmanNode class, with each node having a respective character it rerepresents
 * and the frequency of that character. Also includes getters and setters for most instance variables.
 */

public class HuffmanNode {
	private Character inChar;
	private HuffmanNode left;
	private HuffmanNode right;
	private Integer frequency;
	
	public HuffmanNode(Character inChar, Integer frequency, HuffmanNode left, HuffmanNode right) {
		this.inChar = inChar;
		this.frequency = frequency;
		this.left = left;
		this.right = right;
	}
	
	public int getFrequency() {
		return frequency;
	}
	
	public HuffmanNode getLeft() {
		return left;
	}
	
	public HuffmanNode getRight() {
		return right;
	}
	
	public Character getChar() {
		return inChar;
	}
	public boolean hasChar() {
		return (inChar != null);
	}
	
	public void setFrequency(Integer frequency) {
		this.frequency = frequency;
	}
}
