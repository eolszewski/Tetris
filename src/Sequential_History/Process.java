package Sequential_History;

import java.util.ArrayList;

public class Process {
	private int processID;
	ArrayList<Event> Events = new ArrayList<Event>();
	
	public Process(ArrayList<Event> Events, int processID) { 
		this.Events = Events;
		this.setProcessID(processID);
	}

	public ArrayList<Event> getEvents() { return Events; }	
	public void setEvents(ArrayList<Event> events) { Events = events; }
	public int getProcessID() { return processID; }
	public void setProcessID(int processID) { this.processID = processID; }
}
