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
	public int WIDTH = 0, HEIGHT = 0;
	private int NUM_EVENTS = 0, MAX_P_EVENTS = 0, NUM_PROCESSES = 0;
	private JPanel[][] processGrid;
	private JPanel[][] answerGrid;
	private JPanel gridPanel;
	private JPanel answerPanel;
	private JButton submitButton = new JButton("Submit");
	private JButton resetButton = new JButton("Reset");
	private JButton noAnswerButton = new JButton("No History");
	private JButton newButton = new JButton("New Round");
	private JButton solveButton = new JButton("Solve");
	static JFrame frame = null;
	private ArrayList<Process> processes;
	private HashMap<JLabel, Event> home;
    
	public Game(final ArrayList<Process> p) {
		processes = p;
		initComponents();
		addEvents();
		addListeners();
		MyMouseAdapter myMouseAdapter = new MyMouseAdapter();
		frame.getContentPane().addMouseListener(myMouseAdapter);
		frame.getContentPane().addMouseMotionListener(myMouseAdapter);
		
		
	}

	private void addEvents() {
		home = new HashMap<JLabel, Event>();
		for (int i = 0; i < NUM_PROCESSES; i++) {
			Process p = processes.get(i);
			ArrayList<Event> events = p.getEvents();
			for (int j = 0; j < events.size(); j++) {
				Event e = events.get(j);
				JLabel label = new JLabel(e.getAction() + " " + e.getVariable() + " " + e.getValue(), SwingConstants.CENTER);
				label.setOpaque(true);
				label.setBackground(new Color(255,255,255));
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
				if(answer.size() != NUM_EVENTS)
					JOptionPane.showMessageDialog(null,
							"Please add all events to this history", "Error",
							JOptionPane.ERROR_MESSAGE);
				else
				{
					Round r = new Round(processes, answer);
					if (!r.checkUserAnswer())
						JOptionPane.showMessageDialog(null,
								"This is not a sequential history", "Wrong!",
								JOptionPane.ERROR_MESSAGE);
					else
						JOptionPane.showMessageDialog(null,
								"This is a valid sequential history", "Correct!",
								JOptionPane.INFORMATION_MESSAGE);
				}
			}
		});
		
		noAnswerButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Round r = new Round(processes);
				if (r.checkSCPossible())
					JOptionPane.showMessageDialog(null,
							"There exists a sequential history", "Wrong!",
							JOptionPane.ERROR_MESSAGE);
				else
					JOptionPane.showMessageDialog(null,
							"There does not exist a sequential history",
							"Correct!", JOptionPane.INFORMATION_MESSAGE);
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
        gridPanel = new javax.swing.JPanel();
        JPanel jPanel11 = new javax.swing.JPanel();
        answerPanel = new javax.swing.JPanel();
        
        
        frame = new JFrame("Sequential History Finder!");
        frame.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setContentPane(new JLayeredPane());
        NUM_PROCESSES = processes.size();
        
        jPanel11.setLayout(new java.awt.GridLayout(NUM_PROCESSES, 0));
        
        for (Process p : processes) {
        	if (p.getEvents().size() > MAX_P_EVENTS) {
        		MAX_P_EVENTS = p.getEvents().size();
        	}
        	NUM_EVENTS += p.getEvents().size();
        }
        
        gridPanel.setLayout(new java.awt.GridLayout(NUM_PROCESSES, MAX_P_EVENTS));
        processGrid = new JPanel[NUM_PROCESSES][MAX_P_EVENTS];
        
        for (int i = 0; i< NUM_PROCESSES; i++) {
        	jPanel11.add(new JLabel("Process " + (i + 1), SwingConstants.CENTER));
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
        	temp.setPreferredSize(new Dimension(70,70));
        	temp.setBorder(BorderFactory.createMatteBorder(1, left, 1, 1, new Color(0, 0, 0)));
        	answerGrid[0][i] = temp;
        	answerPanel.add(answerGrid[0][i]);
        }
        
        JLabel jLabel5 = new JLabel();
        JLabel jLabel6 = new JLabel("Score: ");
        JLabel jLabel7 = new JLabel("Round: ");
        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("Sequential History Finder");
        jLabel5.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

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
                                .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                            .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING))))
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
                            .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel6)
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
    }// </editor-fold> 

	public static ArrayList<Process> makeGame() {
		ArrayList<Event> EventsArr = new ArrayList<Event>();

		Event tempEve1 = new Event("Write", 3, 0, "x", 0);
		Event tempEve2 = new Event("Write", 4, 1, "x", 0);
		Event tempEve3 = new Event("Write", 5, 2, "y", 0);
		EventsArr.add(tempEve1);
		EventsArr.add(tempEve2);
		EventsArr.add(tempEve3);
		Process P1 = new Process(EventsArr, 0);

		ArrayList<Event> EventsArr2 = new ArrayList<Event>();
		Event tempEve4 = new Event("Read", 4, 0, "x", 1);
		EventsArr2.add(tempEve4);
		Process P2 = new Process(EventsArr2, 1);

		ArrayList<Event> EventsArr3 = new ArrayList<Event>();
		Event tempEve6 = new Event("Read", 3, 0, "x", 2);
		EventsArr3.add(tempEve6);
		Process P3 = new Process(EventsArr3, 2);
		ArrayList<Process> ProcessesArr = new ArrayList<Process>();
		ProcessesArr.add(P1);
		ProcessesArr.add(P2);
		ProcessesArr.add(P3);

		return ProcessesArr;
	}

	public static void main(String[] args) {
		final ArrayList<Process> game = makeGame();
		Game g = new Game(game);
		Round r = new Round(game);
		System.out.println(r.checkSCPossible());

	}
}