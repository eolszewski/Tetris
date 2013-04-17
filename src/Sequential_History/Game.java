package Sequential_History;

import java.util.ArrayList;

public class Game {
	public Game()
	{
		ArrayList<Event> EventsArr = new ArrayList<Event>();
		Event tempEve1 = new Event("Read", 1);
		Event tempEve2 = new Event("Write", 2);
		Event tempEve3 = new Event("Read", 3);
		EventsArr.add(tempEve1);
		EventsArr.add(tempEve2);
		EventsArr.add(tempEve3);
		Process P1 = new Process(EventsArr);
		ArrayList<Event> EventsArr2 = new ArrayList<Event>();
		Event tempEve4 = new Event("Read", 2);
		Event tempEve5 = new Event("Write", 3);
		EventsArr2.add(tempEve4);
		EventsArr2.add(tempEve5);
		Process P2 = new Process(EventsArr2);
		ArrayList<Event> EventsArr3 = new ArrayList<Event>();
		Event tempEve6 = new Event("Write", 1);
		Event tempEve7 = new Event("Read", 2);
		Event tempEve8 = new Event("Write", 5);
		EventsArr3.add(tempEve6);
		EventsArr3.add(tempEve7);
		EventsArr3.add(tempEve8);
		Process P3 = new Process(EventsArr3);
		ArrayList<Process> ProcessesArr = new ArrayList<Process>();
		ProcessesArr.add(P1);
		ProcessesArr.add(P2);
		ProcessesArr.add(P3);
		MakeGUI(ProcessesArr);
	}

	private void MakeGUI(ArrayList<Process> processesArr) {
		// TODO Auto-generated method stub
		
	}
}
