package encrona.GUI;

// Link to example used for this https://blog.idrsolutions.com/tutorial-copy-text-javafx-swing/#Copying_Text_in_Swing 
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.awt.datatransfer.StringSelection;//
import java.util.Map;
import java.util.Map.Entry;

import javax.imageio.ImageIO;
import javax.swing.*;

import encrona.DataLoader;
import encrona.Model;
import encrona.components.input;
import encrona.domain.heatingEnergySource;
import encrona.domain.improvement;
import encrona.expertSystem.ReasoningEngine;
import encrona.expertSystem.Rule;

/**
 * This class is the main class for the GUI, and handles some of the basic portions and placing the other components
 */
public class GUIMain extends JPanel {

    private static final String runString = "Run Simulation";
    private static final String outputTabName = "Output";
    private static final String expertString = "Run Expert System";
    private static final String expertSystemTabName = "Expert System Output";

    private JButton runButton;
    private JButton expertButton;
    private static JButton clipboardButton=new JButton("Copy to clipboard");;
    private static String clipboardString;

    private static JFrame theMainFrame;
    private static JTabbedPane tabbedPane=new JTabbedPane();
    private static JPanel outputPage;

    /**
     * This creates the GUI, and is responsible for handling the GUI classes
     */
    public GUIMain() {
        super(new BorderLayout());

        runButton = new JButton(runString);
        runButton.setActionCommand(runString);
        runButton.addActionListener(new RunListener());

        expertButton = new JButton(expertString);
        expertButton.setActionCommand(expertString);
        expertButton.addActionListener(new ExpertListner());

        clipboardButton.addActionListener(new clipboardListener());

        // Create a panel that uses BoxLayout.
        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
        buttonPane.add(runButton);
        buttonPane.add(expertButton);

        // This adds the numerical value specification tab
        String toolTip2 = "<html>This is where you specify numeric values</html>";
        tabbedPane.addTab("Numeric variables", null, new GUIStartValueSpecification(), toolTip2);

        // This adds the the heat source tab
        String toolTip3 = "<html>This is where you specify heat sources</html>";
        tabbedPane.addTab("Heat sources", null, new GUIHeatingSources(DataLoader.createInitialListOfHeatSources()),
                toolTip3);

        // This adds the improvement tab
        String tooltip4 = "<html>This is where you specify improvements</html>";
        tabbedPane.addTab("Improvements", null, new GUIImprovements(DataLoader.createInitialListOfImprovements()),
                tooltip4);

        // This adds the improvement tab
        String tooltip5 = "<html>This is where you specify the additional inputs to the expert system</html>";
        tabbedPane.addTab("Expert system inputs", null, new GUIExpertSystemInput(),
        tooltip5);

        add(tabbedPane);

        add(buttonPane, BorderLayout.PAGE_END);
    }

    /**
     * Create the GUI and show it. For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private static void createAndShowGUI() {
        // Create and set up the window.
        theMainFrame = new JFrame("Encrona Simulator");
        theMainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // Create and set up the content pane.
        JComponent newContentPane = new GUIMain();
        newContentPane.setOpaque(true); // content panes must be opaque
        theMainFrame.setContentPane(newContentPane);
        try {
            theMainFrame.setIconImage(ImageIO.read(new File("encrona-simulator\\encrona\\src\\main\\resources\\Encrona.png")));
        } catch (IOException _) {
            System.out.println("Unable to set icon");
        }

        // Display the window.
        theMainFrame.pack();
        theMainFrame.setVisible(true);
    }

    /**
     * This is the method used to invoke the GUI
     * @param args
     */
    public static void main(String[] args) {
        // Schedule a job for the event-dispatching thread:
        // creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

    /**
     * This removes a tab with the specified String name, if it does exist, to be
     * used when it is being re-generated
     * 
     * @param name The name of the tab to remove if it exists
     */
    public static void removeTab(String name) {
        if (-1 != tabbedPane.indexOfTab(name)) {
            tabbedPane.remove(tabbedPane.indexOfTab(name));
        }
    }

    /**
     * This method creates the output tab, along with its contents, based on the input and the results from the simulation
     * @param mapOfNumericalVariables A map of numerical values
     * @param heatingEnergySources A list of heat sources
     * @param improvementsCollected A list of improvements
     */
    public static void createOutputTab(Map<Map.Entry<String, String>, Double> mapOfNumericalVariables,
            java.util.List<heatingEnergySource> heatingEnergySources,
            java.util.List<improvement> improvementsCollected) {
        // This is the tooltip for the heat source page
        String toolTip = "<html>This is where the output is shown</html>";

        outputPage = new JPanel();

        outputPage.setLayout(new BoxLayout(outputPage, BoxLayout.PAGE_AXIS));

        JPanel inputSectionPage = new JPanel();

        inputSectionPage.add(new JLabel("Provided input"));

        DefaultListModel<String> listModel = new DefaultListModel<>();

        for (Map.Entry<Map.Entry<String,String>,Double> entry : mapOfNumericalVariables.entrySet()) {
            listModel.addElement(entry.getKey().getKey() + " was set to " + entry.getValue() + " " + entry.getKey().getValue());
        }

        for (heatingEnergySource heatingEnergySource : heatingEnergySources) {
            listModel.addElement(heatingEnergySource.toString());
        }

        for (improvement imp : improvementsCollected) {
            listModel.addElement(imp.toString());
        }

        // Create the list and put it in a scroll pane.
        JList<String> list = new JList<>(listModel);
        list.setSelectedIndex(0);
        list.setVisibleRowCount(5);
        JScrollPane listScrollPane = new JScrollPane(list);

        inputSectionPage.add(listScrollPane);

        outputPage.add(new JLabel("Output results are shown here"));
        outputPage.add(inputSectionPage);
        JScrollPane scrollableOutputPage = new JScrollPane(outputPage);

        tabbedPane.addTab(outputTabName, null, scrollableOutputPage, toolTip);
        tabbedPane.setSelectedComponent(scrollableOutputPage);

    }

    /**
     * This class is used to handle the runButton, 
     * with its method called when the runButton is clicked, 
     * and should collect the relevant information and startthe simulation with it
     */
    class RunListener implements ActionListener {
        /**
        * This method is called once the action, in this case a button being clicked, is triggered
        * @param e The event which caused this
        */
        public void actionPerformed(ActionEvent e) {
            removeTab(outputTabName);

            final Map<Map.Entry<String, String>, Double> mapOfNumericalVariables;
            final java.util.List<heatingEnergySource> heatingEnergySources;
            final java.util.List<improvement> improvements;

            try {
                mapOfNumericalVariables = GUIStartValueSpecification.collectFieldValues();

                Double aTemp = 1.0;
                for (Map.Entry<Map.Entry<String,String>,Double> entry : mapOfNumericalVariables.entrySet()) {

                    if (entry.getKey().getKey().equals("Atemp")) {
                        aTemp = entry.getValue();
                    }

                }

                heatingEnergySources = GUIHeatingSources.collectFieldValues();
                improvements = GUIImprovements.collectFieldValues(aTemp);
            } catch (Exception error) {
                JOptionPane.showMessageDialog(theMainFrame, error.getMessage(), "The provided input is invalid",
                        JOptionPane.PLAIN_MESSAGE);
                System.out.println(error);
                return;
            }

            createOutputTab(mapOfNumericalVariables, heatingEnergySources, improvements);

            // Schedule a job for the event-dispatching thread:
            // running the simulation specifically in this case
            javax.swing.SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    Map<String, java.util.List<String>> outputLists = Model.runSimulation(mapOfNumericalVariables,
                            improvements,
                            heatingEnergySources);
                    addSimulatorOutputToOutput(outputLists);
                }
            });
        }
    }

    /**
    * This class is used to handle the toClipboard button
    */ 
    class clipboardListener implements ActionListener {
        /**
        * This method is called once the action, in this case a button being clicked, is triggered
        * @param e The event which caused this
        */
        public void actionPerformed(ActionEvent e) {
            StringSelection clipboardOutput = new StringSelection(clipboardString);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(clipboardOutput, null);
               JOptionPane.showMessageDialog(theMainFrame, "Copied dashboard string to clipboard", "Copied to clipboard",
               JOptionPane.PLAIN_MESSAGE);
        }
    }

    /**
     * This adds a section to the output tab with the output from the simulator
     * 
     * @param outputList The list of outputs to display to the user
     */
    public static void addSimulatorOutputToOutput(Map<String, java.util.List<String>> outputLists) {
        JPanel simulatorOutputPage = new JPanel();

        // Link to example used for this
        // https://blog.idrsolutions.com/tutorial-copy-text-javafx-swing/#Copying_Text_in_Swing

        StringBuilder bld = new StringBuilder();

        for (String listItem : outputLists.get("clipboard")) {
            bld.append(listItem);
        }
        clipboardString=bld.toString();

        DefaultListModel<String> listModel = new DefaultListModel<>();
        for (String listItem : outputLists.get("output")) {
            listModel.addElement(listItem);
        }
        JList<String> list = new JList<>(listModel);
        list.setSelectedIndex(0);
        list.setVisibleRowCount(5);
        JScrollPane listScrollPane = new JScrollPane(list);

        simulatorOutputPage.add(new JLabel("Simultor Output"));
        simulatorOutputPage.add(clipboardButton);

        simulatorOutputPage.add(listScrollPane);

        outputPage.add(simulatorOutputPage);
        outputPage.updateUI();
    }

    /**
     * This is used to handle the run expert system button
     */
    class ExpertListner implements ActionListener {
        /**
        * This method is called once the action, in this case a button being clicked, is triggered
        * @param e The event which caused this
        */
        public void actionPerformed(ActionEvent e) {
            removeTab(expertSystemTabName);
            final Map<Map.Entry<String, String>, Double> mapOfNumericalVariables;
            final java.util.List<heatingEnergySource> heatingEnergySources;
            final Map<String,input<?>> expertSystemInput;

            try {
                mapOfNumericalVariables = GUIStartValueSpecification.collectFieldValues();
                heatingEnergySources = GUIHeatingSources.collectFieldValues();
                expertSystemInput=GUIExpertSystemInput.collectFieldValues();
            } catch (Exception error) {
                JOptionPane.showMessageDialog(theMainFrame, error.getMessage(), "The provided input is invalid",
                        JOptionPane.PLAIN_MESSAGE);
                System.out.println(error);
                return;
            }

            // Schedule a job for the event-dispatching thread:
            // running the simulation specifically in this case
            javax.swing.SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    ReasoningEngine reasoningEngine = new ReasoningEngine(mapOfNumericalVariables,
                            heatingEnergySources,expertSystemInput);
                    java.util.List<Entry<String,Integer>> resultList = reasoningEngine.recommendations();
                    addExpertSystemOutputTab(resultList, reasoningEngine.getTriggeredRules());
                }
            });
        }
    }

    /**
     * This method creates the expert system output page
     * @param resultList A list of results, in this case a sorted list of improvements
     * @param triggeredRules A list of the rules which were triggered
     */
    public static void addExpertSystemOutputTab(java.util.List<Entry<String,Integer>> resultList,
            java.util.List<Rule> triggeredRules) {

        // This is the tooltip for the heat source page
        String toolTip = "<html>This is where the expert system output is shown</html>";

        JPanel expertPage = new JPanel();

        expertPage.setLayout(new BoxLayout(expertPage, BoxLayout.PAGE_AXIS));

        JPanel resultPage = new JPanel();
        resultPage.setLayout(new BoxLayout(resultPage, BoxLayout.PAGE_AXIS));

        Integer index=1;
        for (int i = 1; i < resultList.size()+1; i++) {
            Entry<String,Integer> result = resultList.get(i-1);
            resultPage.add(new JLabel(index + " : " + result.getKey() +" = " + result.getValue()));
            if(i<resultList.size() && resultList.get(i).getValue()<result.getValue())
            {index+=1;}
        }

        Dimension minSize = new Dimension(5, 100);
        Dimension prefSize = new Dimension(5, 100);
        Dimension maxSize = new Dimension(Short.MAX_VALUE, 100);
        expertPage.add(new Box.Filler(minSize, prefSize, maxSize));

        expertPage.add(resultPage);

        expertPage.add(new Box.Filler(minSize, prefSize, maxSize));

        DefaultListModel<String> listModel = new DefaultListModel<>();

        index = 1;
        for (Rule rule : triggeredRules) {
            listModel.addElement(index + " : " + rule.toString());
            index += 1;
        }

        // Create the list and put it in a scroll pane.
        JList<String> list = new JList<>(listModel);
        list.setSelectedIndex(0);
        list.setVisibleRowCount(5);
        JScrollPane listScrollPane = new JScrollPane(list);

        expertPage.add(listScrollPane);
        expertPage.add(new Box.Filler(minSize, prefSize, maxSize));

        JScrollPane scrollableExpert = new JScrollPane(expertPage);

        tabbedPane.addTab(expertSystemTabName, null, scrollableExpert, toolTip);
        tabbedPane.setSelectedComponent(scrollableExpert);
    }   

}