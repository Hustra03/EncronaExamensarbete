import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

public class MockGUIMain extends JPanel{
    
     private static final String runString = "Run Simulation";
     private JButton runButton;

     public MockGUIMain() {
        super(new BorderLayout());
  
        runButton = new JButton(runString);
        runButton.setActionCommand(runString);
        runButton.addActionListener(new RunListener());                
        //Create a panel that uses BoxLayout.
        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new BoxLayout(buttonPane,BoxLayout.LINE_AXIS));
        buttonPane.add(runButton);


        JTabbedPane tabbedPane = new JTabbedPane();

        //This adds the start page
        String toolTip = new String("<html>This is where start information is written</html>");
        JPanel startPage = new JPanel();
        startPage.add(new JLabel("Start page text "));
        tabbedPane.addTab("Start", null, startPage, toolTip);       

        //This adds the value specification page
        String toolTip2 = new String("<html>This is where you specify numeric values</html>");
        tabbedPane.addTab("Numeric variables", null, new MockGUIStartValueSpecification(), toolTip2);


        add(tabbedPane);
        add(tabbedPane);

        add(buttonPane, BorderLayout.PAGE_END);
     }

     /**
      * Create the GUI and show it.  For thread safety,
      * this method should be invoked from the
      * event-dispatching thread.
      */
      private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("ListDemo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
 
        //Create and set up the content pane.
        JComponent newContentPane = new MockGUIMain();
        newContentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);
 
        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }
 
    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
}

class RunListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {
        System.out.println("This will start the simulation");
    }
}
}