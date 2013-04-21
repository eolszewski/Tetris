package Sequential_History;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

import javax.swing.*;

public class Game extends JLayeredPane {
    public int WIDTH = 0, HEIGHT = 0;
    private int GRID_ROWS = 0, GRID_COLS = 3;
    private static final int GAP = 0;
    private Dimension LAYERED_PANE_SIZE;
    private Dimension LABEL_SIZE = new Dimension(60, 40);
    private GridLayout gridlayout;
    private JPanel backingPanel;
    private JPanel[][] panelGrid;
    private JLabel Answer = new JLabel("Answer", SwingConstants.CENTER);
    private JButton Submit = new JButton("Submit");
    private JButton Refresh = new JButton("Restart");
    private JButton NoAnswer = new JButton("No History");

    public Game(final ArrayList<Process> Game) {
    	makeBoard(Game);
    	addEvents(Game);
        makeButtons();
        addListeners(Game);
        
        backingPanel.setBorder(BorderFactory.createEmptyBorder(GAP, GAP, GAP, GAP));
        setPreferredSize(LAYERED_PANE_SIZE);
        add(backingPanel, JLayeredPane.DEFAULT_LAYER);
        MyMouseAdapter myMouseAdapter = new MyMouseAdapter();
        addMouseListener(myMouseAdapter);
        addMouseMotionListener(myMouseAdapter);
    }

    private void addEvents(ArrayList<Process> Game) {
        for(int i = 0; i < Game.size(); i++){
        	JLabel Process = new JLabel("Process " + (i+1), SwingConstants.CENTER);
            Process.setOpaque(true);
            Process.setPreferredSize(LABEL_SIZE);
            panelGrid[i+1][1].add(Process); 
            for(int j = 0; j < Game.get(i).Events.size(); j++)
            {	
            	JLabel Event = new JLabel(Game.get(i).getEvents().get(j).getAction() + " " + Game.get(i).getEvents().get(j).getVariable() + " " + Game.get(i).getEvents().get(j).getValue(), SwingConstants.CENTER);
            	Event.setOpaque(true);
            	Event.setDisplayedMnemonic(Game.get(i).getEvents().get(j).getProcessID()*1000 + Game.get(i).getEvents().get(j).getEventID());
            	Event.setBackground(Color.gray.brighter().brighter());
            	panelGrid[i+1][j+2].add(Event);
            	panelGrid[i+1][j+2].setBorder(BorderFactory.createLineBorder(Color.BLACK));
            }
        }
	}

	private void makeBoard(final ArrayList<Process> Game) {
	    GRID_ROWS = 4+Game.size();
	    HEIGHT = 60*GRID_ROWS;
	    for(Process p : Game)
	    	GRID_COLS += p.getEvents().size();
	    WIDTH = 60*GRID_COLS;
	    LAYERED_PANE_SIZE = new Dimension(WIDTH, HEIGHT);
	    gridlayout = new GridLayout(GRID_ROWS, GRID_COLS, GAP, GAP);
	    backingPanel = new JPanel(gridlayout);
	    panelGrid = new JPanel[GRID_ROWS][GRID_COLS];
	    
        backingPanel.setSize(LAYERED_PANE_SIZE);
        backingPanel.setLocation(2 * GAP, 2 * GAP);
        backingPanel.setBackground(Color.black);
        for (int row = 0; row < GRID_ROWS; row++) {
            for (int col = 0; col < GRID_COLS; col++) {
            	if(row == (GRID_ROWS-2) && col > 1 && col < (GRID_COLS-1))
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
	}
	
	private void makeButtons() {
        Answer.setPreferredSize(LABEL_SIZE);
        panelGrid[GRID_ROWS-2][1].add(Answer);
        
        Refresh.setPreferredSize(LABEL_SIZE);
        panelGrid[GRID_ROWS-3][GRID_COLS-1].add(Refresh);
        
        Submit.setPreferredSize(LABEL_SIZE);
        panelGrid[GRID_ROWS-2][GRID_COLS-1].add(Submit);
        
        Font f = new Font("Dialog", Font.PLAIN, 8);
        NoAnswer.setPreferredSize(LABEL_SIZE);
        NoAnswer.setFont(f); 
        panelGrid[GRID_ROWS-1][GRID_COLS-1].add(NoAnswer);
	}

	private void addListeners(final ArrayList<Process> Game) {
        Submit.addActionListener(new ActionListener()
        {
          public void actionPerformed(ActionEvent e)
          {
      		ArrayList<Event> answer = new ArrayList<Event>();
    		
            for(int i = 0; i < 4; i++)
            {
                Component[] components = panelGrid[5][2+i].getComponents();
                JLabel temp = (JLabel) components[0];
                answer.add(Game.get(temp.getDisplayedMnemonic()/1000).getEvents().get(temp.getDisplayedMnemonic()%10));
                System.out.println(answer.toString());
            }
    		Round r = new Round(Game, answer);
    		if(!r.checkUserAnswer())
    			JOptionPane.showMessageDialog (null, "This is not a sequential history", "Wrong!", JOptionPane.INFORMATION_MESSAGE);
    		else
    			JOptionPane.showMessageDialog (null, "This is a valid sequential history", "Correct!", JOptionPane.INFORMATION_MESSAGE);
          }
        });
        
        NoAnswer.addActionListener(new ActionListener()
        {
          public void actionPerformed(ActionEvent e)
          {
    		Round r = new Round(Game);
    		if(r.checkSCPossible())
    			JOptionPane.showMessageDialog (null, "There exists a sequential history", "Wrong!", JOptionPane.INFORMATION_MESSAGE);
    		else
    			JOptionPane.showMessageDialog (null, "There does not exist a sequential history", "Correct!", JOptionPane.INFORMATION_MESSAGE);
          }
        });
        
        Refresh.addActionListener(new ActionListener()
        {
          public void actionPerformed(ActionEvent e)
          {
        	  createAndShowUI(Game);
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
            clickedPanel = (JPanel) backingPanel.getComponentAt(me.getPoint());
            Component[] components = clickedPanel.getComponents();
            if (components.length == 0) {
                return;
            }
            if (components[0] instanceof JLabel) {
                dragLabel = (JLabel) components[0];
                if(dragLabel.getText().charAt(0) != 'P' && dragLabel.getText().charAt(0) != 'A')
                {
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
        }

        @Override
        public void mouseDragged(MouseEvent me) {
            if (dragLabel == null || dragLabel.getText().charAt(0) == 'P' || dragLabel.getText().charAt(0) == 'A') {
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
            if (dragLabel == null || dragLabel.getText().charAt(0) == 'P' || dragLabel.getText().charAt(0) == 'A') {
            	dragLabel.add(dragLabel);
            	dragLabel.revalidate();
                return;
            }
            remove(dragLabel); 
            JPanel droppedPanel = (JPanel) backingPanel.getComponentAt(me.getPoint());
            if (droppedPanel == null) {
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
                    if (r == 5 || ((r-1) == dragLabel.getDisplayedMnemonic()/1000 && (c-2) == dragLabel.getDisplayedMnemonic()%10)){
                        droppedPanel.add(dragLabel);
                        droppedPanel.revalidate();
                    }else {
                        clickedPanel.add(dragLabel);
                        clickedPanel.revalidate();
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
    	final ArrayList<Process> game = makeGame();
    	
    	Round r = new Round(game);
    	System.out.println(r.checkSCPossible());
		java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                createAndShowUI(game);
            }
        });
    }
}