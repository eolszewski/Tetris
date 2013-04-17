package Sequential_History;

import java.util.ArrayList;

public class Process {
	private int processID;
	ArrayList<Event> Events = new ArrayList<Event>();

	
	public Process(ArrayList<Event> Events, int processID) { 
		this.Events = Events;
		this.setProcessID(processID);
	}
	
	//optional getter and setter methods for Events & processiD
	//most likely never used bc won't need to modify
	public ArrayList<Event> getEvents() { 
		return Events; 
	}
	
	public void setEvents(ArrayList<Event> events) { 
		Events = events; 
	}

	public int getProcessID() {
		return processID;
	}

	public void setProcessID(int processID) {
		this.processID = processID;
	}
}
