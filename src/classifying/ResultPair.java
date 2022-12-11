package classifying;

public class ResultPair {
	private double F1, BER;
	
	public ResultPair(){
		
	}
	public ResultPair(double F1, double BER) {
		this.setF1(F1);
		this.setBER(BER);
	}
	public double getF1() {
		return F1;
	}
	public void setF1(double F1) {
		this.F1 = F1;
	}
	public double getBER() {
		return BER;
	}
	public void setBER(double BER) {
		this.BER = BER;
	}
}
