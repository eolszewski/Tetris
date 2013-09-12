//gtws
package Sequential_History;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.border.*;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class Game {
	private int NUM_EVENTS, MAX_P_EVENTS, NUM_PROCESSES;
	private JPanel[][] processGrid;
	private JPanel[][] answerGrid;
	private JPanel gridPanel;
	private JPanel answerPanel;
	private JPanel labelPanel;
	private JLabel scoreLabel;
	private JButton submitButton = new JButton("Submit");
	private JButton resetButton = new JButton("Reset");
	private JButton noAnswerButton = new JButton("No History");
	private JButton newButton = new JButton("New Round");
	private JButton solveButton = new JButton("Solve");
	private Color[] colors = {new Color(150, 200, 255), new Color (255, 200, 150), new Color (255, 150, 200)};
	static JFrame frame = null;
	private ArrayList<Process> processes;
	private ArrayList<Round> rounds;
	private HashMap<JLabel, Event> home;
	private int roundNum, score;
	private boolean solved;
    
	public Game(final ArrayList<Round> r) {
		rounds = r;
        frame = new JFrame("Sequential History Finder!");
        frame.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        roundNum = 0;
        score = 0;
        startRound(0);
		addListeners();
	}
	
	private void startRound(int i) {
		processes = rounds.get(i).getProcesses();
        gridPanel = new javax.swing.JPanel();
        labelPanel = new javax.swing.JPanel();
        answerPanel = new javax.swing.JPanel();
        solved = false;
		initComponents();
		addEvents();
		frame.getContentPane().repaint();
		MyMouseAdapter myMouseAdapter = new MyMouseAdapter();
		frame.getContentPane().addMouseListener(myMouseAdapter);
		frame.getContentPane().addMouseMotionListener(myMouseAdapter);
	}

	private JLabel eventLabel(Event e) {
		String s = "";
		if (e.getAction().equals("Read")) {
			s = "<html>Read(" + e.getVariable() + ") = " + e.getValue() + "<br><br> event " + e.getEventID() + "</html>";
		} else {
			s = "<html>Write(" + e.getVariable() + ", " + e.getValue() + ")<br><br> event " + e.getEventID() + "</html>";
		}
		JLabel label = new JLabel(s, SwingConstants.CENTER);
		//JLabel label = new JLabel(e.getAction() + " " + e.getVariable() + " " + e.getValue(), SwingConstants.CENTER);
		//label.setPreferredSize(new Dimension(100,100));
		label.setOpaque(true);
		label.setBackground(colors[e.getProcessID()]);
		return label;
	}
	
	private void addEvents() {
		home = new HashMap<JLabel, Event>();
		for (int i = 0; i < NUM_PROCESSES; i++) {
			Process p = processes.get(i);
			ArrayList<Event> events = p.getEvents();
			for (int j = 0; j < events.size(); j++) {
				Event e = events.get(j);
				JLabel label = eventLabel(e);
				home.put(label, e);
				processGrid[i][j].add(label);
			}
		}
	}
	
	private ArrayList<Event> getAnswer() {
		ArrayList<Event> answer = new ArrayList<Event>();
		for (int i = 0; i < NUM_EVENTS; i++) {				
			Component[] components = answerGrid[0][i].getComponents();
			if (components.length > 0) {
				if (components[0] instanceof JLabel) {
					answer.add(home.get((JLabel) components[0]));
				}
			}
		}
		return answer;
	}
	
	private void addListeners() {
		submitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ArrayList<Event> answer = getAnswer();
				if (answer.size() != NUM_EVENTS) {
					JOptionPane.showMessageDialog(null,
							"Please add all events to this history", "Error",
							JOptionPane.ERROR_MESSAGE);
				} else {
					if (!rounds.get(roundNum).checkUserAnswer(answer)) {
						JOptionPane.showMessageDialog(null,
								"This is not a sequential history", "Wrong!",
								JOptionPane.ERROR_MESSAGE);
					} else {
						if (solved == false) {
							score++;
							scoreLabel.setText("Score: " + score);
							solved = true;
						}
						JOptionPane.showMessageDialog(null,
								"This is a valid sequential history", "Correct!",
								JOptionPane.INFORMATION_MESSAGE);
					}
				}
			}
		});
		
		noAnswerButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Round r = new Round(processes);
				if (r.checkSCPossible()) {
					JOptionPane.showMessageDialog(null,
							"There exists a sequential history", "Wrong!",
							JOptionPane.ERROR_MESSAGE);
				} else {
					if (solved == false) {
						score++;
						scoreLabel.setText("Score: " + score);
						solved = true;
					}
					JOptionPane.showMessageDialog(null,
							"There does not exist a sequential history",
							"Correct!", JOptionPane.INFORMATION_MESSAGE);
				}
			}
		});
		
		resetButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				for (int i = 0; i < NUM_EVENTS; i++) {
					if (answerGrid[0][i].getComponentCount() > 0) {
						JLabel label = (JLabel) answerGrid[0][i].getComponent(0);
						answerGrid[0][i].remove(label);
						answerGrid[0][i].revalidate();
						answerGrid[0][i].repaint();
						Event event = home.get(label);
						processGrid[event.getProcessID()][event.getEventID()].add(label);
						processGrid[event.getProcessID()][event.getEventID()].revalidate();
					}
				}
			}
		});
		
		newButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				roundNum++;
				if (roundNum < rounds.size()) {
					startRound(roundNum);
				} else {
					Object[] options = { "Restart game", "Return", "Quit" };
					int selectedValue = JOptionPane.showOptionDialog(null,
							"Done!  Your score was: " + score + "/5",
							"Done", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[2]);
					if (selectedValue == 0) {
						score = 0;
						roundNum = 0;
						startRound(0);
					} else if (selectedValue == 2) {
						System.exit(0);
					}
				}
			}
		});
		
		solveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				solved = true;
				ArrayList<Event> answer = rounds.get(roundNum).findSequentialHistory();
				if (answer == null) {
					JOptionPane.showMessageDialog(null,
							"There is no sequential history possible",
							"No Solution", JOptionPane.INFORMATION_MESSAGE);
				} else {
					//System.out.println(answer);
					clearGrid();
					paintSolution(answer);
				}
			}
		});
	}
	
	private void clearGrid() {
		for (int i = 0; i < NUM_PROCESSES; i++) {
			for (int j = 0; j < MAX_P_EVENTS; j++) {
				processGrid[i][j].removeAll();
			}
		}
		gridPanel.revalidate();
		frame.getContentPane().repaint();
		
	}
	
	private void paintSolution(ArrayList<Event> answer) {
		for (int i = 0; i < NUM_EVENTS; i++) {
			Event e = answer.get(i);
			JLabel label = eventLabel(e);
			home.put(label, e);
			answerGrid[0][i].removeAll();
			answerGrid[0][i].add(label);
		}
		answerPanel.revalidate();
	}

	private class MyMouseAdapter extends MouseAdapter {
		private JLabel dragLabel = null;
		private int dragLabelWidthDiv2;
		private int dragLabelHeightDiv2;
		private JPanel clickedPanel = null;

		@Override
		public void mousePressed(MouseEvent me) {
			Component container = me.getComponent().getComponentAt(me.getPoint());
			if (!(container.equals(gridPanel) || container.equals(answerPanel))) {
				return;
			}
			clickedPanel = (JPanel) container.getComponentAt(new Point(me.getX() - container.getX(), me.getY() - container.getY()));
			Component[] components = clickedPanel.getComponents();
			if (components.length == 0) {
				return;
			}
			if (components[0] instanceof JLabel) {
				dragLabel = (JLabel) components[0];
				clickedPanel.remove(dragLabel);
				clickedPanel.revalidate();
				clickedPanel.repaint();

				dragLabelWidthDiv2 = dragLabel.getWidth() / 2;
				dragLabelHeightDiv2 = dragLabel.getHeight() / 2;

				int x = me.getX() - dragLabelWidthDiv2 - me.getComponent().getX();
				int y = me.getY() - dragLabelHeightDiv2 - me.getComponent().getY();
				dragLabel.setLocation(x, y);
				frame.getContentPane().add(dragLabel, JLayeredPane.DRAG_LAYER);
				frame.getContentPane().repaint();
			}
		}

		@Override
		public void mouseDragged(MouseEvent me) {
			if (dragLabel == null)
				return;
			Component container = me.getComponent();
			int x = me.getX() - dragLabelWidthDiv2 + container.getX();
			int y = me.getY() - dragLabelHeightDiv2 + container.getY();
			dragLabel.setLocation(x, y);
			frame.getContentPane().repaint();
		}

		@Override
		public void mouseReleased(MouseEvent me) {
			if (dragLabel == null) {
				return;
			}
			frame.getContentPane().remove(dragLabel);
			Component container = me.getComponent().getComponentAt(me.getPoint());
			if (container == null) {
				clickedPanel.add(dragLabel);
				clickedPanel.revalidate();
				frame.getContentPane().repaint();
				dragLabel = null;
				return;
			}
			if (!container.equals(answerPanel) && !container.equals(gridPanel)) {
				clickedPanel.add(dragLabel);
				clickedPanel.revalidate();
				frame.getContentPane().repaint();
				dragLabel = null;
				return;
			}
			JPanel droppedPanel = (JPanel) container.getComponentAt(new Point(me.getX() - container.getX(), me.getY() - container.getY()));
			if (droppedPanel == null) {
				clickedPanel.add(dragLabel);
				clickedPanel.revalidate();
			} else {
				if (container.equals(gridPanel)) {
					Event event = home.get(dragLabel);
					processGrid[event.getProcessID()][event.getEventID()].add(dragLabel);
					processGrid[event.getProcessID()][event.getEventID()].revalidate();
				}
				
				if (container.equals(answerPanel)) {
					if (droppedPanel.equals(answerPanel)) {
						clickedPanel.add(dragLabel);
						clickedPanel.revalidate();
						frame.getContentPane().repaint();
						dragLabel = null;
						return;
					}
					if (droppedPanel.getComponentCount() > 0) {
							JLabel moveLabel = (JLabel) droppedPanel.getComponent(0);
							droppedPanel.remove(moveLabel);
							Event event = home.get(moveLabel);
							processGrid[event.getProcessID()][event.getEventID()].add(moveLabel);
							processGrid[event.getProcessID()][event.getEventID()].revalidate();
					}
					droppedPanel.add(dragLabel);
					droppedPanel.revalidate();
				}
			}
			frame.getContentPane().repaint();
			dragLabel = null;
		}
	}
	
	private void initComponents() {
		NUM_PROCESSES = processes.size();
        MAX_P_EVENTS = 0;
        NUM_EVENTS = 0;
        frame.setContentPane(new JLayeredPane());
        
        JLabel jLabel5 = new JLabel();
        scoreLabel = new JLabel("Score: " + score);
        JLabel jLabel7 = new JLabel("Round: " + (roundNum + 1));
        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("Sequential History Finder");
        jLabel5.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        
        labelPanel.setLayout(new java.awt.GridLayout(NUM_PROCESSES, 0));
        
        for (Process p : processes) {
        	if (p.getEvents().size() > MAX_P_EVENTS) {
        		MAX_P_EVENTS = p.getEvents().size();
        	}
        	NUM_EVENTS += p.getEvents().size();
        }
        
        gridPanel.setLayout(new java.awt.GridLayout(NUM_PROCESSES, MAX_P_EVENTS));
        processGrid = new JPanel[NUM_PROCESSES][MAX_P_EVENTS];
        
        for (int i = 0; i< NUM_PROCESSES; i++) {
        	labelPanel.add(new JLabel("Process " + (i + 1), SwingConstants.CENTER));
        	for (int j = 0; j < MAX_P_EVENTS; j++) {
        		JPanel temp = new JPanel(new GridBagLayout());
        		temp.setPreferredSize(new Dimension(70,70));
        		if (j < processes.get(i).getEvents().size()) {
        			int top = 1, left = 1;
            		if (j > 0) {
            			left = 0;
            		}
            		if (i > 0) {
            			if (j < processes.get(i-1).getEvents().size()) {
            				top = 0;
            			}
            		}
            		temp.setBorder(BorderFactory.createMatteBorder(top, left, 1, 1, new Color(0, 0, 0)));
        		} 
        		processGrid[i][j] = temp;
        		gridPanel.add(processGrid[i][j]);
        		
        	}
        }
      

        answerPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Answer", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));
        answerPanel.setLayout(new java.awt.GridLayout(1, NUM_EVENTS));
        
        answerGrid = new JPanel[1][NUM_EVENTS];
        
        for (int i = 0; i < NUM_EVENTS; i++) {
        	JPanel temp = new JPanel(new GridBagLayout());
        	int left = 0;
        	if (i == 0) {
        		left = 1;
        	}
        	temp.setPreferredSize(new Dimension(85,70));
        	temp.setBorder(BorderFactory.createMatteBorder(1, left, 1, 1, new Color(0, 0, 0)));
        	answerGrid[0][i] = temp;
        	answerPanel.add(answerGrid[0][i]);
        }
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(frame.getContentPane());
        frame.getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(38, 38, 38)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(labelPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(gridPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(answerPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(resetButton, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(submitButton, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(noAnswerButton, javax.swing.GroupLayout.Alignment.TRAILING))
                                .addComponent(newButton, javax.swing.GroupLayout.Alignment.TRAILING))
                            .addComponent(solveButton, javax.swing.GroupLayout.Alignment.TRAILING)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(127, 127, 127)
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 245, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(scoreLabel, javax.swing.GroupLayout.Alignment.TRAILING))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(28, 28, 28)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(gridPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 179, Short.MAX_VALUE)
                            .addComponent(labelPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(scoreLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(solveButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(newButton)
                        .addGap(56, 56, 56)))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(resetButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(submitButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(noAnswerButton))
                    .addComponent(answerPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(22, 22, 22))
        );

        frame.pack();
        frame.setVisible(true);
	}
	

	public static ArrayList<Round> makeGame() {		
		ArrayList<Round> rounds = new ArrayList<Round>();
		Process p1 = new Process(0);
		p1.addEvent("Write", "x", 3);
		p1.addEvent("Write", "x", 4);
		p1.addEvent("Write", "y", 5);
		
		Process p2 = new Process(1);
		p2.addEvent("Read", "x", 4);
		p2.addEvent("Read", "y", 5);

		Process p3 = new Process(2);
		p3.addEvent("Read", "x", 3);
		p3.addEvent("Write", "x", 10);
		ArrayList<Process> processesArr = new ArrayList<Process>();
		processesArr.add(p1);
		processesArr.add(p2);
		processesArr.add(p3);

		rounds.add(new Round(processesArr));
		
		p1 = new Process(0);
		p1.addEvent("Write", "x", 4);
		p1.addEvent("Write", "x", 3);
		
		p2 = new Process(1);
		p2.addEvent("Read", "x", 3);
		p2.addEvent("Read", "x", 4);
		processesArr = new ArrayList<Process>();
		processesArr.add(p1);
		processesArr.add(p2);
		rounds.add(new Round(processesArr));
		
		return rounds;
	}

	public static void main(String[] args) {
		final ArrayList<Round> rounds = makeGame();
		Game g = new Game(rounds);

	}
}