package encrona.GUI;

// Link to example used for this https://blog.idrsolutions.com/tutorial-copy-text-javafx-swing/#Copying_Text_in_Swing 
import java.awt.*;
import java.awt.event.*;
import java.awt.datatransfer.StringSelection;//
import java.util.Map;

import javax.swing.*;

import encrona.DataLoader;
import encrona.Model;
import encrona.domain.heatingEnergySource;
import encrona.domain.improvement;
import encrona.expertSystem.ReasoningEngine;
import encrona.expertSystem.Rule;

public class GUIMain extends JPanel {

    private static final String runString = "Run Simulation";
    private static final String outputTabName = "Output";
    private static final String expertString = "Run Expert System";
    private static final String expertSystemTabName = "Expert System Output";

    private JButton runButton;
    private JButton expertButton;
    private static JButton clipboardButton;
    private static String clipboardString;

    private static JFrame theMainFrame;
    private static JTabbedPane tabbedPane;
    private static JPanel outputPage;

    public GUIMain() {
        super(new BorderLayout());

        runButton = new JButton(runString);
        runButton.setActionCommand(runString);
        runButton.addActionListener(new RunListener());

        expertButton = new JButton(expertString);
        expertButton.setActionCommand(expertString);
        expertButton.addActionListener(new ExpertListner());

        clipboardButton=new JButton("Copy to clipboard");
        clipboardButton.addActionListener(new clipboardListener());

        // Create a panel that uses BoxLayout.
        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
        buttonPane.add(runButton);
        buttonPane.add(expertButton);

        tabbedPane = new JTabbedPane();

        // This adds the start tab
        String toolTip = "<html>This is where start information is written</html>";
        JPanel startPage = new JPanel();
        startPage.add(new JLabel("Start page text"));
        tabbedPane.addTab("Start", null, startPage, toolTip);

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
        theMainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create and set up the content pane.
        JComponent newContentPane = new GUIMain();
        newContentPane.setOpaque(true); // content panes must be opaque
        theMainFrame.setContentPane(newContentPane);

        // Display the window.
        theMainFrame.pack();
        theMainFrame.setVisible(true);
    }

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

    // This method creates the output tab, along with its contents, based on the
    // input and the results from the simulation
    public static void createOutputTab(Map<Map.Entry<String, String>, Double> mapOfNumericalVariables,
            java.util.List<heatingEnergySource> heatingEnergySources,
            java.util.List<improvement> improvementsCollected) {
        // This is the tooltip for the heat source page
        String toolTip = new String("<html>This is where the output is shown</html>");

        outputPage = new JPanel();

        outputPage.setLayout(new BoxLayout(outputPage, BoxLayout.PAGE_AXIS));

        JPanel inputSectionPage = new JPanel();

        inputSectionPage.add(new JLabel("Provided input"));

        DefaultListModel<String> listModel = new DefaultListModel<String>();

        for (Map.Entry<String, String> entry : mapOfNumericalVariables.keySet()) {
            listModel.addElement(
                    entry.getKey() + " was set to " + mapOfNumericalVariables.get(entry) + " " + entry.getValue());
        }

        for (heatingEnergySource heatingEnergySource : heatingEnergySources) {
            listModel.addElement(heatingEnergySource.toString());
        }

        for (improvement imp : improvementsCollected) {
            listModel.addElement(imp.toString());
        }

        // Create the list and put it in a scroll pane.
        JList<String> list = new JList<String>(listModel);
        list.setSelectedIndex(0);
        list.setVisibleRowCount(5);
        JScrollPane listScrollPane = new JScrollPane(list);

        inputSectionPage.add(listScrollPane);

        outputPage.add(new JLabel("Output results are shown here"));
        outputPage.add(inputSectionPage);
        JScrollPane scrollableOutputPage = new JScrollPane(outputPage);

        tabbedPane.addTab(outputTabName, null, scrollableOutputPage, toolTip);
    }

    // This class is used to handle the runButton, with its method called when the
    // runButton is clicked, and should collect the relevant information and start
    // the simulation with it
    class RunListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            removeTab(outputTabName);

            final Map<Map.Entry<String, String>, Double> mapOfNumericalVariables;
            final java.util.List<heatingEnergySource> heatingEnergySources;
            final java.util.List<improvement> improvements;

            try {
                mapOfNumericalVariables = GUIStartValueSpecification.collectFieldValues();

                Double aTemp = 1.0;
                for (Map.Entry<String, String> entry : mapOfNumericalVariables.keySet()) {

                    if (entry.getKey().equals("Atemp")) {
                        aTemp = mapOfNumericalVariables.get(entry);
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

        // This class is used to handle the toClipboard button
        class clipboardListener implements ActionListener {
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
     * @param outputList
     */
    public static void addSimulatorOutputToOutput(Map<String, java.util.List<String>> outputLists) {
        JPanel simulatorOutputPage = new JPanel();

        // Link to example used for this
        // https://blog.idrsolutions.com/tutorial-copy-text-javafx-swing/#Copying_Text_in_Swing

        clipboardString="";
        for (String listItem : outputLists.get("clipboard")) {
            clipboardString += listItem;
        }

        DefaultListModel<String> listModel = new DefaultListModel<String>();
        for (String listItem : outputLists.get("output")) {
            listModel.addElement(listItem);
        }
        JList<String> list = new JList<String>(listModel);
        list.setSelectedIndex(0);
        list.setVisibleRowCount(5);
        JScrollPane listScrollPane = new JScrollPane(list);

        simulatorOutputPage.add(new JLabel("Simultor Output"));
        simulatorOutputPage.add(clipboardButton);

        simulatorOutputPage.add(listScrollPane);

        outputPage.add(simulatorOutputPage);
        outputPage.updateUI();

    }

    // This is used to handle the run expert system button
    class ExpertListner implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            removeTab(expertSystemTabName);
            final Map<Map.Entry<String, String>, Double> mapOfNumericalVariables;
            final java.util.List<heatingEnergySource> heatingEnergySources;

            try {
                mapOfNumericalVariables = GUIStartValueSpecification.collectFieldValues();
                heatingEnergySources = GUIHeatingSources.collectFieldValues();
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
                            heatingEnergySources);
                    java.util.List<String> resultList = reasoningEngine.recommendations();
                    addExpertSystemOutputTab(resultList, reasoningEngine.getTriggeredRules());
                }
            });
        }
    }

    public static void addExpertSystemOutputTab(java.util.List<String> resultList,
            java.util.List<Rule> triggeredRules) {

        // This is the tooltip for the heat source page
        String toolTip = new String("<html>This is where the expert system output is shown</html>");

        JPanel expertPage = new JPanel();

        expertPage.setLayout(new BoxLayout(expertPage, BoxLayout.PAGE_AXIS));

        JPanel resultPage = new JPanel();
        resultPage.setLayout(new BoxLayout(resultPage, BoxLayout.PAGE_AXIS));

        Integer index = 1;
        for (String resultString : resultList) {
            resultPage.add(new JLabel(index + " : " + resultString));
            index += 1;
        }
        Dimension minSize = new Dimension(5, 100);
        Dimension prefSize = new Dimension(5, 100);
        Dimension maxSize = new Dimension(Short.MAX_VALUE, 100);
        expertPage.add(new Box.Filler(minSize, prefSize, maxSize));

        expertPage.add(resultPage);

        expertPage.add(new Box.Filler(minSize, prefSize, maxSize));

        DefaultListModel<String> listModel = new DefaultListModel<String>();

        index = 1;
        for (Rule rule : triggeredRules) {
            listModel.addElement(index + " : " + rule.toString());
            index += 1;
        }

        // Create the list and put it in a scroll pane.
        JList<String> list = new JList<String>(listModel);
        list.setSelectedIndex(0);
        list.setVisibleRowCount(5);
        JScrollPane listScrollPane = new JScrollPane(list);

        expertPage.add(listScrollPane);
        expertPage.add(new Box.Filler(minSize, prefSize, maxSize));

        JScrollPane scrollableExpert = new JScrollPane(expertPage);

        tabbedPane.addTab(expertSystemTabName, null, scrollableExpert, toolTip);

    }

}