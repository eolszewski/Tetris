package Sequential_History;

import java.util.ArrayList;

public class Process {
	ArrayList<Event> Events = new ArrayList<Event>();

	public Process() { }
	
	public ArrayList<Event> getEvents() { return Events; }
	public void setEvents(ArrayList<Event> events) { Events = events; }
}
