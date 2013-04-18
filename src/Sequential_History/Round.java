package Sequential_History;

import java.util.ArrayList;
import java.util.HashMap;

import org.paukov.combinatorics.Factory;
import org.paukov.combinatorics.ICombinatoricsVector;
import org.paukov.combinatorics.Generator;


public class Round {
	ArrayList<Process> Processes = new ArrayList<Process>();
	ArrayList<Event> UserAnswer = new ArrayList<Event>();
	
	//constructor for when "Submit" is pressed
	public Round(ArrayList<Process> Processes, ArrayList<Event> UserAnswer) {
		this.Processes = Processes;
		this.UserAnswer = UserAnswer;
	}
	
	//constructor for when "No Answer" is pressed
	public Round(ArrayList<Process> Processes) {
		this.Processes = Processes;
	}

	public ArrayList<Process> getProcesses() { return Processes; }
	public void setProcesses(ArrayList<Process> processes) { Processes = processes; }
	
	//preconditions: assume event IDs start at 0,1,2...
	public boolean checkUserAnswer() { 
		int[] eventID = new int[Processes.size()];  		//initially everything 0
		for (Event e : UserAnswer) {
			if (e.getEventID() == eventID[e.getProcessID()]) {
				eventID[e.getProcessID()]++;
			} else {
				return false;
			}
		}
		//System.out.println("Hello");
		HashMap <String, ArrayList<Integer>> cache = new HashMap<String, ArrayList<Integer>> ();
		//cache.
		for(Event e: UserAnswer){
			//System.out.println(e);
			if(e.getAction().equals("Write")){
				if(cache.containsKey(e.getVariable() + e.getValue())){
					ArrayList<Integer> temp = cache.get(e.getVariable() + e.getValue());
					temp.add(e.getValue());
					cache.put(e.getVariable() + e.getValue(), temp);
				}
				else{
					ArrayList<Integer> values = new ArrayList<Integer>();
					values.add(e.getValue());
					cache.put(e.getVariable() + e.getValue(), values);
				}
			}
			else{						//read
				if(cache.containsKey(e.getVariable() + e.getValue())){
					if(cache.get(e.getVariable() + e.getValue()).size() > 1){
						ArrayList<Integer> temp = cache.get(e.getVariable() + e.getValue());
						temp.remove(0);
						cache.put(e.getVariable() + e.getValue(), temp);
					}
					else{
						cache.remove(e.getVariable() + e.getValue());
					}
				}
				else {
					return false;
				}
			}
		}
		
		return true;

	}
	
	public boolean checkSCPossible() {
		//save previous state of userAnswer into tempAnswer (IF NEEDED)
		ArrayList<Event> tempAnswer = new ArrayList<Event>();
		for(Event e: this.UserAnswer){
			tempAnswer.add(e);
		}
		
		ArrayList<Event> Answer = new ArrayList<Event>();
		for (int i = 0; i < Processes.size(); i++){
			for(int j = 0; j < Processes.get(i).Events.size(); j++){
				Answer.add(Processes.get(i).Events.get(j));
			}
		}
		
		ICombinatoricsVector<Event> seq = Factory.createVector(Answer);
		Generator<Event> gen = Factory.createPermutationGenerator(seq);

		for (ICombinatoricsVector<Event> perm : gen){
			this.UserAnswer = (ArrayList<Event>) perm.getVector();
			if(this.checkUserAnswer() == true)
				return true;
		}

		//return previous state of UserAnswer (IF NEEDED)
		this.UserAnswer.clear();
		for(Event e: tempAnswer){
			this.UserAnswer.add(e);
		}

		return false;
	}
	public void setUserAnswer(ArrayList<Event> userAnswer) { UserAnswer = userAnswer; }
}
