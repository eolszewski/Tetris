package Sequential_History;

public class Event {
	private String Action;
	private Integer Value;
	
	public Event(String Action, Integer Value) { 
		this.Action = Action;
		this.Value = Value;
	}
	public String getAction() { return Action; }
	public void setAction(String action) { Action = action; }
	public Integer getValue() { return Value; }
	public void setValue(Integer value) { Value = value; }
	
}
