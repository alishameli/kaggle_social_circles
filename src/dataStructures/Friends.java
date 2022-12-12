package dataStructures;
import java.util.ArrayList;

public class Friends {
	private ArrayList<Integer> friends = new ArrayList<Integer>();
	
	public void add(int x) {
		this.friends.add(x);
	}
	
	public boolean contains(int x){
		return this.friends.contains(x);
	}

	public int get(int index){
		return this.friends.get(index);
	}
	
	public int getSize() {
		return this.friends.size();
	}

	public Integer[] getFriends() {
		return friends.toArray(new Integer[friends.size()]);
	}
}
