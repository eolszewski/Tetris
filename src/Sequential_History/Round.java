package Sequential_History;

import java.util.ArrayList;

public class Round {
	ArrayList<Process> Processes = new ArrayList<Process>();
	ArrayList<Process> CorrectAnswer = new ArrayList<Process>();
	
	public Round(ArrayList<Process> Processes, ArrayList<Process> CorrectAnswer) {
		this.Processes = Processes;
		this.CorrectAnswer = CorrectAnswer;
	}

	public ArrayList<Process> getProcesses() { return Processes; }
	public void setProcesses(ArrayList<Process> processes) { Processes = processes; }
	public ArrayList<Process> getCorrectAnswer() { return CorrectAnswer; }
	public void setCorrectAnswer(ArrayList<Process> correctAnswer) { CorrectAnswer = correctAnswer; }
}
