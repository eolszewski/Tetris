package Sequential_History;

import java.util.ArrayList;

public class Round {
	ArrayList<Process> Processes = new ArrayList<Process>();
	ArrayList<Event> UserAnswer = new ArrayList<Event>();
	
	public Round(ArrayList<Process> Processes, ArrayList<Event> UserAnswer) {
		this.Processes = Processes;
		this.UserAnswer = UserAnswer;
	}

	public ArrayList<Process> getProcesses() { return Processes; }
	public void setProcesses(ArrayList<Process> processes) { Processes = processes; }
	
	//need to write
	public boolean checkUserAnswer() { return true; }
	public void setUserAnswer(ArrayList<Event> userAnswer) { UserAnswer = userAnswer; }
}
