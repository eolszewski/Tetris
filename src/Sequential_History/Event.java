package Sequential_History;

public class Event {
	private String Action;
	private Integer Value;
	private int eventID;
	
	public Event(String Action, Integer Value, int eventID) { 
		this.Action = Action;
		this.Value = Value;
		this.setEventID(eventID);
	}
	public String getAction() { return Action; }
	public void setAction(String action) { Action = action; }
	public Integer getValue() { return Value; }
	public void setValue(Integer value) { Value = value; }
	public int getEventID() {return eventID; }
	public void setEventID(int eventID) {this.eventID = eventID; }
}
