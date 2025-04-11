import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.*;
import javax.swing.event.*;

import encrona.domain.heatingEnergySource;

public class MockGUIMain extends JPanel{
    
     private static final String runString = "Run Simulation";
     private JButton runButton;
     private static JFrame theMainFrame;
     private static JTabbedPane tabbedPane;

     public MockGUIMain() {
        super(new BorderLayout());
  
        runButton = new JButton(runString);
        runButton.setActionCommand(runString);
        runButton.addActionListener(new RunListener());                
        //Create a panel that uses BoxLayout.
        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new BoxLayout(buttonPane,BoxLayout.LINE_AXIS));
        buttonPane.add(runButton);

        tabbedPane = new JTabbedPane();

        //This adds the start page
        String toolTip = new String("<html>This is where start information is written</html>");
        JPanel startPage = new JPanel();
        startPage.add(new JLabel("Start page text"));
        tabbedPane.addTab("Start", null, startPage, toolTip);       

        //This adds the numerical value specification page
        String toolTip2 = new String("<html>This is where you specify numeric values</html>");
        tabbedPane.addTab("Numeric variables", null, new MockGUIStartValueSpecification(), toolTip2);

        //This adds the value specification page
        String toolTip3 = new String("<html>This is where you specify heat sources</html>");

        heatingEnergySource districtHeating = new heatingEnergySource("districtHeating", 174812.0, 26850.0,0.0,1.25);
        heatingEnergySource gasHeating = new heatingEnergySource("gasHeating", 2000.0, 0.0,0.0,30.0);
        java.util.List<heatingEnergySource> heatingEnergySources= new ArrayList<heatingEnergySource>();
        heatingEnergySources.add(districtHeating);
        heatingEnergySources.add(gasHeating);

        tabbedPane.addTab("Heat sources", null, new MockGUIHeatingSources(heatingEnergySources), toolTip3);

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
        theMainFrame = new JFrame("MockGUI");
        theMainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
 
        //Create and set up the content pane.
        JComponent newContentPane = new MockGUIMain();
        newContentPane.setOpaque(true); //content panes must be opaque
        theMainFrame.setContentPane(newContentPane);
 
        //Display the window.
        theMainFrame.pack();
        theMainFrame.setVisible(true);
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

    //This removes the output tab, if it does exist, to be used when it is being re-generated during simulation
    public static void removeOutputTab()
    {
        if (-1!=tabbedPane.indexOfTab("Output")) {
            tabbedPane.remove(tabbedPane.indexOfTab("Output"));
        }
    }

    //This method creates the output tab, along with its contents, based on the input and the results from the simulation
    public static void createOutputTab(java.util.List<Map.Entry<Map.Entry<String,String>,Double>> listOfNumericalVariables,java.util.List<heatingEnergySource> heatingEnergySources)
    {
        JPanel outputPage = new JPanel();
        outputPage.setLayout(new BoxLayout(outputPage, BoxLayout.PAGE_AXIS));

        JPanel inputSectionPage = new JPanel();

        inputSectionPage.add(new JLabel("Provided input"));

        DefaultListModel<String> listModel = new DefaultListModel<String>();
        for (Entry<Map.Entry<String,String>,Double> entry : listOfNumericalVariables) {
            listModel.addElement(entry.getKey().getKey() + " was set to " + entry.getValue() + " " + entry.getKey().getValue());
        }

        for (heatingEnergySource heatingEnergySource : heatingEnergySources) {
            listModel.addElement(heatingEnergySource.toString());

        }
 
        //Create the list and put it in a scroll pane.
        JList<String> list = new JList<String>(listModel);
        list.setSelectedIndex(0);
        list.setVisibleRowCount(5);
        JScrollPane listScrollPane = new JScrollPane(list);

        inputSectionPage.add(listScrollPane);

        outputPage.add(new JLabel("Output results are shown here"));
        outputPage.add(inputSectionPage);

        tabbedPane.addTab("Output", outputPage);
    }

    //This class is used to handle the runButton, with its method called when the runButton is clicked, and should collect the relevant information and start the simulation with it
    class RunListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            removeOutputTab();

            java.util.List<Map.Entry<Map.Entry<String,String>,Double>> listOfNumericalVariables=null;
            java.util.List<heatingEnergySource> heatingEnergySources =null;
            try {
                listOfNumericalVariables =MockGUIStartValueSpecification.collectFieldValues();
                heatingEnergySources=MockGUIHeatingSources.collectFieldValues();
            } catch (Exception error) {
                JOptionPane.showMessageDialog(theMainFrame,error.getMessage(),"The provided input is invalid",JOptionPane.PLAIN_MESSAGE);
                return;
            }

            for (Map.Entry<Map.Entry<String,String>,Double> numericalEntry : listOfNumericalVariables) {
                System.out.println(numericalEntry.getKey().getKey() + " equals " + numericalEntry.getValue() + " " + numericalEntry.getKey().getValue());
            }
            for (heatingEnergySource heatingEnergySource : heatingEnergySources) {
                System.out.println(heatingEnergySource.toString());
            }
            createOutputTab(listOfNumericalVariables,heatingEnergySources);
        }
    }

    
}