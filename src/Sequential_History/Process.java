package Sequential_History;

import java.util.ArrayList;

public class Process {
	private int processID;
	ArrayList<Event> events = new ArrayList<Event>();
	
	public Process(int processID) {
		this.processID = processID;
	}
	
	public Process(ArrayList<Event> events, int processID) { 
		this.events = events;
		this.processID = processID;
	}

	public ArrayList<Event> getEvents() { return events; }	
	public void setEvents(ArrayList<Event> events) { this.events = events; }
	public int getProcessID() { return processID; }
	public void setProcessID(int processID) { this.processID = processID; }
	
	public void addEvent(String action, String variable, int value) {
		Event e = new Event(action, value, events.size(), variable, processID);
		events.add(e);
	}
}
