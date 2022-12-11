package clustering;
import java.util.ArrayList;

public class Container {
	public ArrayList<Integer> arr;
	public Container() {
		arr = new ArrayList<Integer>();
	}
	public int size() {
		return this.arr.size();
		}

		public boolean contains(Integer i) {
		return this.arr.contains(i);
		}
}