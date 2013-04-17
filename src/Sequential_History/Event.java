package Sequential_History;

public class Event {
	private String action;
	private String variable;
	private Integer value;
	private int eventID;
	private int processID;
	
	//preconditions: assume event IDs start at 0,1,2...
	//assume processIDs start at 0
	//action can either be "write" or "read"
	public Event(String action, Integer value, int eventID, String variable, int processID) {
		this.variable = variable;
		this.action = action;
		this.value = value;
		this.eventID = eventID;
		this.processID = processID;
	}
	
	
	public String getAction() { return action; }
	public void setAction(String action) { this.action = action; }
	public Integer getValue() { return value; }
	public void setValue(Integer value) { this.value = value; }
	public int getEventID() {return eventID; }
	public void setEventID(int eventID) {this.eventID = eventID; }


	public int getProcessID() {
		return this.processID;
	}


	public void setProcessID(int processID) {
		this.processID = processID;
	}


	public String getVariable() {
		return this.variable;
	}


	public void setVariable(String variable) {
		this.variable = variable;
	}
}
