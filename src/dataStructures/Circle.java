package dataStructures;

import java.util.ArrayList;

public class Circle {
	private ArrayList<Integer> friends = new ArrayList<Integer>();

	public void add(int x) {
		this.friends.add(x);
	}

	public ArrayList<Integer> getCircleMembers() {
		return friends;
	}

	public boolean contains(Integer id) {
		return this.friends.contains(id);
	}
}
