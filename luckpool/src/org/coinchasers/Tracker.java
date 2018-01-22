package org.coinchasers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

public class Tracker {
	
	private Luckpool luckpool;
	private static HashMap<String, Boolean> options;
	
	public Tracker() {
		setLuckpool(Dock.getLuckpool());
		setOptions(new HashMap<String, Boolean>());
		loadOptions();
	}
	
	public void loadOptions() {
		File file = new File("tracking.json");
		if(file.exists()) {
			loadTrackingOptions();
		} else {
			for(String option : getLuckpool().getStats()) {
				options.put(option, true);
			}
			try {
				saveTrackingOptions();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public void loadTrackingOptions() {
		try {
			FileInputStream saveFile = new FileInputStream("tracking.json");
			ObjectInputStream save = new ObjectInputStream(saveFile);
			options = (HashMap<String, Boolean>) save.readObject();
			save.close();
			}
		catch(Exception exc) {
			exc.printStackTrace();
			}
	}
	
	public void saveTrackingOptions() throws IOException {
		// Create some data objects for us to save.
		try {
			FileOutputStream saveFile = new FileOutputStream("tracking.json");
			ObjectOutputStream save = new ObjectOutputStream(saveFile);

			//		Saves the HashMap rather than the boolean value, sorted the values being put with wrong keys.
			save.writeObject(options);

			// Close the file.
			save.close();
			}
		catch(Exception exc){
			exc.printStackTrace();
			}
	}

	public Luckpool getLuckpool() {
		return luckpool;
	}

	public void setLuckpool(Luckpool luckpool) {
		this.luckpool = luckpool;
	}

	public static HashMap<String, Boolean> getOptions() {
		return options;
	}

	public static void setOptions(HashMap<String, Boolean> options) {
		Tracker.options = options;
	}
}