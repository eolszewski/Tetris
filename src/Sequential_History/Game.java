package Sequential_History;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import javax.swing.*;

public class Game extends JLayeredPane {
    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;
    private static final int GRID_ROWS = 7;
    private static final int GRID_COLS = 10;
    private static final int GAP = 0;
    private static final Dimension LAYERED_PANE_SIZE = new Dimension(WIDTH, HEIGHT);
    private static final Dimension LABEL_SIZE = new Dimension(60, 40);
    private GridLayout gridlayout = new GridLayout(GRID_ROWS, GRID_COLS, GAP, GAP);
    private JPanel backingPanel = new JPanel(gridlayout);
    private JPanel[][] panelGrid = new JPanel[GRID_ROWS][GRID_COLS];
    private JLabel Answer = new JLabel("Answer", SwingConstants.CENTER);
    private JButton Submit = new JButton("Submit");
    private JButton NoAnswer = new JButton("No History");

    public Game(ArrayList<Process> Game) {
        JLabel redLabel = new JLabel("Write 1", SwingConstants.CENTER);
        JLabel blueLabel = new JLabel("Read 3", SwingConstants.CENTER);

        backingPanel.setSize(LAYERED_PANE_SIZE);
        backingPanel.setLocation(2 * GAP, 2 * GAP);
        backingPanel.setBackground(Color.black);
        for (int row = 0; row < GRID_ROWS; row++) {
            for (int col = 0; col < GRID_COLS; col++) {
            	if(row == 5 && col > 1 && col < 9)
            	{
            		JPanel temp = new JPanel(new GridBagLayout());
            		temp.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            		panelGrid[row][col] = temp;
            	}
            	else
            		panelGrid[row][col] = new JPanel(new GridBagLayout());
                backingPanel.add(panelGrid[row][col]);
            }
        }

        for(int i = 0; i < Game.size(); i++){
        	JLabel Process = new JLabel("Process " + (i+1), SwingConstants.CENTER);
            Process.setOpaque(true);
            Process.setPreferredSize(LABEL_SIZE);
            panelGrid[i+1][1].add(Process); 
            for(int j = 0; j < Game.get(i).Events.size(); j++)
            {	
            	JLabel Event = new JLabel(Game.get(i).getEvents().get(j).getAction() + " " + Game.get(i).getEvents().get(j).getVariable() + " " + Game.get(i).getEvents().get(j).getValue(), SwingConstants.CENTER);
            	Event.setOpaque(true);
            	Event.setBackground(Color.gray.brighter());
            	panelGrid[i+1][j+2].add(Event);
            }
        }
        
        Answer.setOpaque(true);
        Answer.setPreferredSize(LABEL_SIZE);
        panelGrid[5][1].add(Answer);

        Submit.setOpaque(true);
        Submit.setPreferredSize(LABEL_SIZE);
        panelGrid[5][9].add(Submit);
        
        Font f = new Font("Dialog", Font.PLAIN, 8);
        NoAnswer.setOpaque(true);
        NoAnswer.setPreferredSize(LABEL_SIZE);
        NoAnswer.setFont(f); 
        panelGrid[6][9].add(NoAnswer);
        
        backingPanel.setBorder(BorderFactory.createEmptyBorder(GAP, GAP, GAP, GAP));
        setPreferredSize(LAYERED_PANE_SIZE);
        add(backingPanel, JLayeredPane.DEFAULT_LAYER);
        MyMouseAdapter myMouseAdapter = new MyMouseAdapter();
        addMouseListener(myMouseAdapter);
        addMouseMotionListener(myMouseAdapter);
    }

    private class MyMouseAdapter extends MouseAdapter {
        private JLabel dragLabel = null;
        private int dragLabelWidthDiv2;
        private int dragLabelHeightDiv2;
        private JPanel clickedPanel = null;

        @Override
        public void mousePressed(MouseEvent me) {
            clickedPanel = (JPanel) backingPanel.getComponentAt(me.getPoint());
            Component[] components = clickedPanel.getComponents();
            if (components.length == 0) {
                return;
            }
            // if we click on jpanel that holds a jlabel
            if (components[0] instanceof JLabel) {

                // remove label from panel
                dragLabel = (JLabel) components[0];
                clickedPanel.remove(dragLabel);
                clickedPanel.revalidate();
                clickedPanel.repaint();

                dragLabelWidthDiv2 = dragLabel.getWidth() / 2;
                dragLabelHeightDiv2 = dragLabel.getHeight() / 2;

                int x = me.getPoint().x - dragLabelWidthDiv2;
                int y = me.getPoint().y - dragLabelHeightDiv2;
                dragLabel.setLocation(x, y);
                add(dragLabel, JLayeredPane.DRAG_LAYER);
                repaint();
            }
        }

        @Override
        public void mouseDragged(MouseEvent me) {
            if (dragLabel == null || dragLabel.getText().charAt(0) == 'P') {
                dragLabel.setLocation(dragLabel.getX(), dragLabel.getY());
            	repaint();
                return;
            }
            int x = me.getPoint().x - dragLabelWidthDiv2;
            int y = me.getPoint().y - dragLabelHeightDiv2;
            dragLabel.setLocation(x, y);
            repaint();
        }

        @Override
        public void mouseReleased(MouseEvent me) {
            if (dragLabel == null || dragLabel.getText().charAt(0) == 'P') {
            	dragLabel.add(dragLabel);
            	dragLabel.revalidate();
                return;
            }
            remove(dragLabel); // remove dragLabel for drag layer of JLayeredPane
            JPanel droppedPanel = (JPanel) backingPanel.getComponentAt(me.getPoint());
            if (droppedPanel == null) {
                // if off the grid, return label to home
                clickedPanel.add(dragLabel);
                clickedPanel.revalidate();
            }
            else {
            	if(droppedPanel.getX() < 85 || droppedPanel.getX() > 700 || (droppedPanel.getY() < 400 && droppedPanel.getY() > 300) || droppedPanel.getY() > 500 || droppedPanel.getY() < 80) {
                    clickedPanel.add(dragLabel);
                    clickedPanel.revalidate();
            	}
            	else{
                    int r = -1;
                    int c = -1;
                    searchPanelGrid: for (int row = 0; row < panelGrid.length; row++) {
                        for (int col = 0; col < panelGrid[row].length; col++) {
                            if (panelGrid[row][col] == droppedPanel) {
                                r = row;
                                c = col;
                                break searchPanelGrid;
                            }
                        }
                    }

                    if (r == -1 || c == -1) {
                        // if off the grid, return label to home
                        clickedPanel.add(dragLabel);
                        clickedPanel.revalidate();
                    } else {
                        droppedPanel.add(dragLabel);
                        droppedPanel.revalidate();
                    }
            	}
            }

            repaint();
            dragLabel = null;
        }
    }

    private static void createAndShowUI(ArrayList<Process> game) {
        JFrame frame = new JFrame("DragLabelOnLayeredPane");
        frame.getContentPane().add(new Game(game));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

	public static ArrayList<Process> makeGame()
	{
		ArrayList<Event> EventsArr = new ArrayList<Event>();
		//public Event(String action, Integer value, int eventID, String variable, int processID) {

		Event tempEve1 = new Event("Write", 3, 0, "x", 0);
		Event tempEve2 = new Event("Write", 4, 1, "x", 0);
		EventsArr.add(tempEve1);
		EventsArr.add(tempEve2);
		Process P1 = new Process(EventsArr, 0);
		
		
		ArrayList<Event> EventsArr2 = new ArrayList<Event>();
		Event tempEve4 = new Event("Read", 4,0,"x",1);
		EventsArr2.add(tempEve4);
		Process P2 = new Process(EventsArr2,1);
		
		
		ArrayList<Event> EventsArr3 = new ArrayList<Event>();
		Event tempEve6 = new Event("Read", 3,0,"x",2);
		EventsArr3.add(tempEve6);
		Process P3 = new Process(EventsArr3,2);
		ArrayList<Process> ProcessesArr = new ArrayList<Process>();
		ProcessesArr.add(P1);
		ProcessesArr.add(P2);
		ProcessesArr.add(P3);
		
		return ProcessesArr;
	}
	
    public static void main(String[] args) {
		//Event(String action, Integer value, int eventID, String variable, int processID) {

    	final ArrayList<Process> game = makeGame();
		ArrayList<Event> answer = new ArrayList<Event>();
		
		answer.add(new Event("Write", 3, 0, "x", 0));
		answer.add(new Event("Read", 3, 0, "x", 2));
		answer.add(new Event("Write", 4, 1, "x", 0));
		answer.add(new Event("Read", 4, 0, "x", 1));
		Round r = new Round(game, answer);
		
		System.out.print(r.checkUserAnswer());

		java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                createAndShowUI(game);
            }
        });
    }
}