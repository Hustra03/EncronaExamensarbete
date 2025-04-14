package encrona.GUIMockup;

import java.awt.*;
import java.awt.event.*;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.*;

import encrona.DataLoader;
import encrona.Model;
import encrona.domain.heatingEnergySource;
import encrona.domain.improvement;

public class MockGUIMain extends JPanel{
    
     private static final String runString = "Run Simulation";
     private JButton runButton;
     private static JFrame theMainFrame;
     private static JTabbedPane tabbedPane;
     private static JPanel outputPage;


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

        //This adds the start tab
        String toolTip = "<html>This is where start information is written</html>";
        JPanel startPage = new JPanel();
        startPage.add(new JLabel("Start page text"));
        tabbedPane.addTab("Start", null, startPage, toolTip);       

        //This adds the numerical value specification tab
        String toolTip2 = "<html>This is where you specify numeric values</html>";
        tabbedPane.addTab("Numeric variables", null, new MockGUIStartValueSpecification(), toolTip2);

        //This adds the the heat source tab
        String toolTip3 = "<html>This is where you specify heat sources</html>";
        tabbedPane.addTab("Heat sources", null, new MockGUIHeatingSources(DataLoader.createInitialListOfHeatSources()), toolTip3);

        //This adds the improvement tab
        String tooltip4 = "<html>This is where you specify improvements</html>";
        tabbedPane.addTab("Improvements", null, new MockGUIImprovements(DataLoader.createInitialListOfImprovements()), tooltip4);

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
    public static void createOutputTab(java.util.List<Map.Entry<Map.Entry<String,String>,Double>> listOfNumericalVariables,java.util.List<heatingEnergySource> heatingEnergySources,java.util.List<improvement> improvementsCollected)
    {
        //This is the tooltip for the heat source page
        String toolTip = new String("<html>This is where the output is shown</html>");

        outputPage = new JPanel();
        
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

        for (improvement imp : improvementsCollected) {
            listModel.addElement(imp.toString());
        }
 
        //Create the list and put it in a scroll pane.
        JList<String> list = new JList<String>(listModel);
        list.setSelectedIndex(0);
        list.setVisibleRowCount(5);
        JScrollPane listScrollPane = new JScrollPane(list);

        inputSectionPage.add(listScrollPane);

        outputPage.add(new JLabel("Output results are shown here"));
        outputPage.add(inputSectionPage);
        JScrollPane scrollableOutputPage = new JScrollPane(outputPage);

        tabbedPane.addTab("Output",null, scrollableOutputPage,toolTip);
    }

    //This class is used to handle the runButton, with its method called when the runButton is clicked, and should collect the relevant information and start the simulation with it
    class RunListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            removeOutputTab();

            final java.util.List<Map.Entry<Map.Entry<String,String>,Double>> listOfNumericalVariables;
            final java.util.List<heatingEnergySource> heatingEnergySources;
            final java.util.List<improvement> improvements;

            try {
                listOfNumericalVariables =MockGUIStartValueSpecification.collectFieldValues();
                heatingEnergySources=MockGUIHeatingSources.collectFieldValues();
                improvements=MockGUIImprovements.collectFieldValues();
            } catch (Exception error) {
                JOptionPane.showMessageDialog(theMainFrame,error.getMessage(),"The provided input is invalid",JOptionPane.PLAIN_MESSAGE);
                System.out.println(error);
                return;
            }

            for (Map.Entry<Map.Entry<String,String>,Double> numericalEntry : listOfNumericalVariables) {
                System.out.println(numericalEntry.getKey().getKey() + " equals " + numericalEntry.getValue() + " " + numericalEntry.getKey().getValue());
            }
            for (heatingEnergySource heatingEnergySource : heatingEnergySources) {
                System.out.println(heatingEnergySource.toString());
            }

            createOutputTab(listOfNumericalVariables,heatingEnergySources,improvements);

            //Schedule a job for the event-dispatching thread:
            //running the simulation specifically in this case
            javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                java.util.List<String> outputList=Model.runSimulation(improvements, heatingEnergySources);
                addSimulatorOutputToOutput(outputList);
            }
            });
        }
    }

    /**
     * This adds a section to the output tab with the output from the simulator 
     * @param outputList
     */
    public static void addSimulatorOutputToOutput(java.util.List<String> outputList)
    {
        JPanel simulatorOutputPage = new JPanel();
        
        DefaultListModel<String> listModel = new DefaultListModel<String>();
        for (String list : outputList) {
            listModel.addElement(list);
        }
        JList<String> list = new JList<String>(listModel);
        list.setSelectedIndex(0);
        list.setVisibleRowCount(5);
        JScrollPane listScrollPane = new JScrollPane(list);

        simulatorOutputPage.add(new JLabel("Simultor Output"));

        simulatorOutputPage.add(listScrollPane);


        outputPage.add(simulatorOutputPage);
        outputPage.updateUI();

    }


    
}