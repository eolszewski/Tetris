package Sequential_History;

import java.util.ArrayList;

public class Process {
	ArrayList<Event> Events = new ArrayList<Event>();

	public Process(ArrayList<Event> Events) { 
		this.Events = Events;
	}
	
	public ArrayList<Event> getEvents() { return Events; }
	public void setEvents(ArrayList<Event> events) { Events = events; }
}
