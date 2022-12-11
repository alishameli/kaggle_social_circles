package classifying;

public class Pair {
	private int firstCircle, secondCircle;
	
	public Pair(){
		
	}
	public Pair(int firstCircle, int secondCircle) {
		this.setFirstCircle(firstCircle);
		this.setSecondCircle(secondCircle);
	}
	public int getSecondCircle() {
		return secondCircle;
	}
	public void setSecondCircle(int secondCircle) {
		this.secondCircle = secondCircle;
	}
	public int getFirstCircle() {
		return firstCircle;
	}
	public void setFirstCircle(int firstCircle) {
		this.firstCircle = firstCircle;
	}
}
