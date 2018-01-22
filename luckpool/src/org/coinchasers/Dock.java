package org.coinchasers;

import java.awt.AWTException;
import java.awt.CheckboxMenuItem;
import java.awt.Image;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class Dock {
	
	private static Luckpool luckpool;
	private static Tracker tracker;
	private static HashMap<CheckboxMenuItem, Boolean> tracking_options;
	private static Webscraper scraper;
	private static double id;
	public static TrayIcon trayIcon;
	private static String address;

	public static void main(String[] args) {
		id = 0.01;
		
		setLuckpool(new Luckpool());
		setTrackingOptions(new HashMap<CheckboxMenuItem, Boolean>());
		
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                setTracker(new Tracker());
                createAndShowGUI();
                updateOptions();
                beginnerAddress();
                scraper = new Webscraper(address);
                statUpdaterTimer(trayIcon);
            }
        });
    }
     
    private static void createAndShowGUI() {
        //Check the SystemTray support
        if (!SystemTray.isSupported()) {
            System.out.println("SystemTray is not supported");
            return;
        }
        final PopupMenu popup = new PopupMenu();
        trayIcon = new TrayIcon(createImage("four_leaf_clover.png", "tray icon"));
        trayIcon.setImageAutoSize(true);
        final SystemTray tray = SystemTray.getSystemTray();
        
        MenuItem aboutItem = new MenuItem("Info");
        aboutItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null,
                        "Luckpool Statistic Tracker\nVersion " + id + "\nCreated by CoinChasers");
            }
        });
        
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	System.exit(0);
            }
        });
        
        MenuItem addressItem = new MenuItem("Switch Address");
        addressItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	try {
                	JFrame frame = new JFrame("Luckpool Mining Address");
                	String name;
                	name = JOptionPane.showInputDialog(frame, "What's your worker address?", "Luckpool Mining Address", JOptionPane.QUESTION_MESSAGE);
                	while(name == null) {
                		name = JOptionPane.showInputDialog(frame, "What's your worker address?", "Luckpool Mining Address", JOptionPane.QUESTION_MESSAGE);
                	}
                	address = name;
                	scraper = new Webscraper(address);
    				saveAddress();
                	updateOptions();
    				statUpdater(trayIcon);
    			} catch (IOException ex) {
    				ex.printStackTrace();
    			}
            }
        });
        
        Menu displayMenu = new Menu("Track");
        // Create a popup menu components
        for(String key : Tracker.getOptions().keySet()) {
        	boolean contains = false;
        	for(CheckboxMenuItem menu : tracking_options.keySet()) {
        		if(menu.getName().equals(key)) {
        			contains = true;
        		}
        	}
        	if(!contains) {
        		tracking_options.put(new CheckboxMenuItem(key), null);
        	}
        }
        HashMap<String, Boolean> unsorted;
        unsorted = new HashMap<String, Boolean>();
        for(CheckboxMenuItem menu : tracking_options.keySet()) {
        	for(String key : Tracker.getOptions().keySet()) {
        		if(menu.getLabel().equals(key)) {
        			menu.setState(Tracker.getOptions().get(key));
        			unsorted.put(menu.getLabel(), menu.getState());
        			}
        		}
        	}
        List<String> list = new ArrayList<String>(unsorted.keySet());
        Collections.sort(list);
        for(String string : list) {
        	for(CheckboxMenuItem menu : tracking_options.keySet()) {
        		if(menu.getLabel() == string) {
        			menu.setLabel(Utilities.capitiliseString(menu.getLabel()));
        			menu.addItemListener(new ItemListener()  {

						@Override
						public void itemStateChanged(ItemEvent ie) {
							try {
								updateOptions();
								statUpdater(trayIcon);
								tracker.saveTrackingOptions();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
        				
        			});
        			displayMenu.add(menu);
        		}
        	}
        }
        
        popup.add(aboutItem);
        popup.addSeparator();
        popup.add(addressItem);
        popup.addSeparator();
        popup.add(displayMenu);
        popup.addSeparator();
        popup.add(exitItem);
         
        trayIcon.setPopupMenu(popup);
         
        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            System.out.println("TrayIcon could not be added.");
            return;
        }
        
    }
     
    //Obtain the image URL
    protected static Image createImage(String path, String description) {
        URL imageURL = Dock.class.getResource(path);
         
        if (imageURL == null) {
            System.err.println("Resource not found: " + path);
            return null;
        } else {
            return (new ImageIcon(imageURL, description)).getImage();
        }
    }

	public static Luckpool getLuckpool() {
		return luckpool;
	}

	public static void setLuckpool(Luckpool luckpool) {
		Dock.luckpool = luckpool;
	}

	public HashMap<CheckboxMenuItem, Boolean> getTrackingoptions() {
		return tracking_options;
	}

	public static void setTrackingOptions(HashMap<CheckboxMenuItem, Boolean> tracking_options) {
		Dock.tracking_options = tracking_options;
	}

	public static Tracker getTracker() {
		return tracker;
	}

	public static void setTracker(Tracker tracker) {
		Dock.tracker = tracker;
	}

	private static void updateOptions() {
    	for(CheckboxMenuItem menu : tracking_options.keySet()) {
			if(Tracker.getOptions().containsKey(Utilities.decapitiliseString(menu.getLabel()))) {
    			if(!menu.getState() == Tracker.getOptions().get(Utilities.decapitiliseString(menu.getLabel()))) {
    				Tracker.getOptions().replace(Utilities.decapitiliseString(menu.getLabel()), menu.getState());
    			}
    		}
    	}
	}

	public static String tooltip() {
		String tip = "";
		String br = "\n";
		
		for(String key : scraper.getTextValues().keySet()) {
			if(Tracker.getOptions().containsKey(key)) {
				if(Tracker.getOptions().get(key).equals(true)) {
					if(!key.equalsIgnoreCase("pool")) {
						String[] chars = scraper.getTextValues().get(key).split("");
						String start = "";
						String end = "";
						for(int x = 0; x < chars.length; x++) {
							if(x == 1 || x == 2) {
								start += chars[x];
							} else if(x >= chars.length - 3){
								end += chars[x];
							}
						}
						tip += formatKey(key) + start + "..." + end + br;
					} else {
						tip += scraper.getTextValues().get(key).toUpperCase() + br;
					}
				}
			}
			
		}
		ArrayList<String> order = new ArrayList<String>(); {
			order.addAll(Arrays.asList("totalHash","totalShares","balance", "immature", "paid"));
		}
		for (String key : order) {
			if(Tracker.getOptions().containsKey(key)) {
				if(Tracker.getOptions().get(key).equals(true)) {
					tip += formatKey(key) + scraper.getDoubleValues().get(key) + br;
				}
			}
		}
		return tip;
	}
	
	public static String formatKey(String string) {
		switch(string) {
		case "miner" : {
			return  "Address: ";
		}
		case "totalHash" : {
			return  "Hashrate: ";
		}
		case "totalShares": {
			return  "Shares: ";
		}
		case "immature" : {
			return  "Immature: ";
		}
		case "balance" : {
			return  "Balance: ";
		}
		case "paid" : {
			return  "Paid: ";
		}
		default : {
			return "null";
		}
		}
	}
	
	public static void statUpdaterTimer(TrayIcon icon) {
		int delay = 0;
//		int interval = 60001;
		int interval = 5000;
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				scraper.scrape();
				icon.setToolTip(tooltip());
				}
			}, delay, interval);
	}

	public static void statUpdater(TrayIcon icon) {
		scraper.scrape();
		icon.setToolTip(tooltip());
	}
	
	public static void loadAddress() {
		try {
			FileInputStream saveFile = new FileInputStream("address.json");
			ObjectInputStream save = new ObjectInputStream(saveFile);
			address = (String) save.readObject();
			save.close();
			}
		catch(Exception exc) {
			exc.printStackTrace();
			}
	}
	
	public static void saveAddress() throws IOException {
		try {
			FileOutputStream saveFile = new FileOutputStream("address.json");
			ObjectOutputStream save = new ObjectOutputStream(saveFile);
			save.writeObject(address);
			save.close();
			}
		catch(Exception exc){
			exc.printStackTrace();
			}
	}

	public static void beginnerAddress() {
		File file = new File("address.json");
		if(file.exists()) {
			loadAddress();
		} else {
        	try {
            	JFrame frame = new JFrame("Luckpool Mining Address");
            	String name;
            	name = JOptionPane.showInputDialog(frame, "What's your worker address?", "Luckpool Mining Address", JOptionPane.QUESTION_MESSAGE);
            	while(name == null) {
            		name = JOptionPane.showInputDialog(frame, "What's your worker address?", "Luckpool Mining Address", JOptionPane.QUESTION_MESSAGE);
            	}
            	address = name;
				saveAddress();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
