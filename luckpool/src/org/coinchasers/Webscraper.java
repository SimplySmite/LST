package org.coinchasers;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.swing.JOptionPane;

import com.jaunt.JNode;
import com.jaunt.NotFound;
import com.jaunt.ResponseException;
import com.jaunt.UserAgent;

public class Webscraper {
	
	private UserAgent userAgent = new UserAgent();
	private String address;
	private HashMap<String, String> text;
	private HashMap<String, String> doubles;
	private JNode jnode;
	private boolean hasNotified;
	private boolean minerNotFound;
	
	public Webscraper(String address) {
		this.address = address;
	}
	
	public void scrape() {
		try {
			this.text = new HashMap<String, String>();
			this.doubles = new HashMap<String, String>();
			userAgent.sendGET("https://luckpool.org/api/worker_stats?" + this.address);
			if(userAgent.response.getStatus() == 200) {
		        if(hasNotified) {
		        	hasNotified = false;
		        }
		        List<String> list = new ArrayList<String>(Tracker.getOptions().keySet());
		        Collections.sort(list);
		        DecimalFormat df = new DecimalFormat("0.###");
				for(String string : list) {
					jnode = userAgent.json.findFirst(string);
					
					if(!jnode.getType().equals(JNode.Type.NUMBER)) {
						text.put(string, jnode.toString());
					} else {
						if(string.equalsIgnoreCase("totalHash")) {
							double formatHash = jnode.toDouble();
							double q = formatHash*2;
							double w = q/1000;
							double e = w/1000;
							df = new DecimalFormat("0");
							doubles.put(string, df.format(e));
						} else {
							doubles.put(string, df.format(jnode.toDouble()));
						}
					}
				}
		        Dock.trayIcon.setImage(Dock.createImage("four_leaf_clover.png", "tray icon"));
		        Dock.trayIcon.setImageAutoSize(true);
				minerNotFound = false;
			} else {
		        if(hasNotified != true) {
			        Dock.trayIcon.setImage(Dock.createImage("red_clover.png", "tray icon"));
			        Dock.trayIcon.setImageAutoSize(true);
		        	JOptionPane.showMessageDialog(null, "Error 404: Luckpool offline");
		        	this.hasNotified = true;
		        }
			}
		} catch (ResponseException e) {
			e.printStackTrace();
		} catch (NotFound e) {
			if(minerNotFound != true) {
		        Dock.trayIcon.setImage(Dock.createImage("red_clover.png", "tray icon"));
		        Dock.trayIcon.setImageAutoSize(true);
	        	JOptionPane.showMessageDialog(null, "Error 404: Miner not found");
				minerNotFound = true;
			}
		}
	}
	
	public HashMap<String, String> getTextValues() {
		return this.text;
	}
	
	public HashMap<String, String> getDoubleValues() {
		return this.doubles;
	}
}