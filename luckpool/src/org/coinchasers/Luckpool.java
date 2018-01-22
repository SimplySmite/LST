package org.coinchasers;

import java.util.ArrayList;
import java.util.Arrays;

public class Luckpool {
	
	private ArrayList<String> stats;
	
	public Luckpool() {
		stats = new ArrayList<String>();
		
		loadStatsAsString();
	}
	
	public void loadStatsAsString() {
		stats.addAll(Arrays.asList("pool","miner","totalHash","totalShares","immature","balance","paid"));
	}
	
	public ArrayList<String> getStats() {
		return this.stats;
	}
}