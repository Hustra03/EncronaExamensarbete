package encrona.GUI;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import encrona.domain.improvement;
import encrona.domain.improvementImpactEnum;

public class GUIImprovements extends JPanel {

    List<improvement> improvements;
    static JPanel improvementSpecificationPage;

    public GUIImprovements(List<improvement> initialImprovements) {
        super(new GridBagLayout());

        JPanel infoPage = new JPanel();
        infoPage.add(new JLabel("Here you give the values for improvements for the building"));
        improvements = initialImprovements;

        improvementSpecificationPage = new JPanel();
        JScrollPane scrollimprovementSpecificationPage = new JScrollPane(improvementSpecificationPage);
        improvementSpecificationPage.setLayout(new BoxLayout(improvementSpecificationPage, BoxLayout.PAGE_AXIS));
        scrollimprovementSpecificationPage.getVerticalScrollBar().setUnitIncrement(10);

        for (improvement improvement : initialImprovements) {
            JPanel improvementPage = createImprovementPage(improvement);
            improvementPage.setAlignmentX(Component.LEFT_ALIGNMENT);
            improvementSpecificationPage.add(improvementPage);
        }

        JPanel createNewImprovementPage = new JPanel();

        JButton createNewButton = new JButton("Create new improvement");
        JTextField createNewNameField = new JTextField(20);
        CreateNewListener CreateNewListener = new CreateNewListener(createNewNameField);
        createNewButton.addActionListener(CreateNewListener);
        createNewButton.setActionCommand("createNew");

        createNewImprovementPage.add(createNewButton);
        createNewImprovementPage.add(createNewNameField);

        // Add Components to this panel with specific constraints on their layout
        GridBagConstraints c = new GridBagConstraints();
        c.gridwidth = GridBagConstraints.REMAINDER;

        c.fill = GridBagConstraints.HORIZONTAL;
        add(infoPage, c);

        c.fill = GridBagConstraints.BOTH;
        c.weighty = 0.10;
        c.weightx=1.0;
        add(createNewImprovementPage, c);
        c.weighty = 0.90;
        c.weightx=0.9;
        add(scrollimprovementSpecificationPage, c);
    }

    /**
     * This creates an improvement page, based on a provided improvement
     * @param improvement The improvement this page is for
     * @return The improvement page component
     */
    private JPanel createImprovementPage(improvement improvement) {
        JPanel improvementPage = new JPanel();

        JPanel selectButtonPage = new JPanel();
        JCheckBox selectCheckBox = new JCheckBox("Select");
        selectButtonPage.add(selectCheckBox);

        JPanel krPerM2Page = new JPanel();
        JTextField kwhPerYearHeatingField = new JTextField(improvement.getCostPerM2().toString(), 10);
        krPerM2Page.add(kwhPerYearHeatingField);
        krPerM2Page.add(new JLabel("kr/m^2 over the improvements lifetime"));

        JPanel kwhPerM2Page = new JPanel();
        JTextField kwhPerM2TextField = new JTextField(improvement.getKwhPerM2().toString(), 10);
        kwhPerM2Page.add(kwhPerM2TextField);
        String[] unitOptions={"kwh/m^2 over the improvements lifetime","kwh per year"};
        JComboBox<String> unitSelection = new JComboBox<String>(unitOptions);
        unitSelection.setSelectedIndex(0);
        kwhPerM2Page.add(unitSelection);

        JPanel yearsOfServicePage = new JPanel();
        JTextField yearsOfServiceField = new JTextField(improvement.getYearsOfService().toString(), 10);
        yearsOfServicePage.add(yearsOfServiceField);
        yearsOfServicePage.add(new JLabel("years the improvement is efficent"));

        JPanel impactTypePage = new JPanel(new GridLayout(0, 1));
        ButtonGroup impactTypeGroup = new ButtonGroup();

        for (improvementImpactEnum impactEnum : improvementImpactEnum.values()) {
            JRadioButton radioButton = new JRadioButton(impactEnum.toString());
            radioButton.setActionCommand(impactEnum.toString());
            impactTypeGroup.add(radioButton);
            if (impactEnum.equals(improvement.getImpactType())) {
                impactTypeGroup.setSelected(radioButton.getModel(), true);
            }
            impactTypePage.add(radioButton);
        }

        improvementPage.add(selectButtonPage);
        improvementPage.add(new JLabel(improvement.getName()));
        improvementPage.add(krPerM2Page);
        improvementPage.add(kwhPerM2Page);
        improvementPage.add(yearsOfServicePage);
        improvementPage.add(impactTypePage);

        return improvementPage;
    }

    /**
     * This handles if the button to create a new improvement is pressed
     */
    class CreateNewListener implements ActionListener {
        private JTextField createNewNameField;

        public CreateNewListener(JTextField createNewNameField) {
            this.createNewNameField = createNewNameField;
        }

        @Override
        public void actionPerformed(ActionEvent e) {

            if ("createNew".equals(e.getActionCommand())) {
                String name = createNewNameField.getText();
                if (name.equals("")) {

                    JOptionPane.showMessageDialog(improvementSpecificationPage,
                            "No name was given for the improvement", "The provided input is invalid",
                            JOptionPane.PLAIN_MESSAGE);
                    return;
                }

                for (improvement improvement : improvements) {
                    if (improvement.getName().equals(name)) {
                        createNewNameField.setText("");
                        JOptionPane.showMessageDialog(improvementSpecificationPage, "The improvement already exists",
                                "The provided input is invalid", JOptionPane.PLAIN_MESSAGE);

                        return;
                    }
                }
                improvement newImprovement = new improvement(name, 0.0, 0.0, 0, improvementImpactEnum.Electricity);
                improvements.add(newImprovement);
                JPanel improvementPage = createImprovementPage(newImprovement);
                improvementSpecificationPage.add(improvementPage);
            }
            improvementSpecificationPage.updateUI();
        }

    }

    /**
     * This method collects the current values from the improvement which currently
     * exist in the improvement component
     * <p>
     * Note that this depends on the structure of the improvement page, and the
     * ordering of its components, so any changes made there will likely break this method
     * <p>
     * 
     * @param aTemp the area of the building 
     * @return A list of improvement with the provided values
     * @throws Exception If something goes wrong, with the only expected case being
     *                   if the values provided are invalid
     */
    public static java.util.List<improvement> collectFieldValues(Double aTemp) throws Exception {
        List<improvement> improvementsCollected = new ArrayList<improvement>();

        // This iterates over all of the heat source pages, and all of their components, to retrive the user provided input
        for (Component improvementPage : improvementSpecificationPage.getComponents()) {

            Double krPerM2;
            Double kwhPerM2;
            Integer yearsOfService;            
            improvementImpactEnum impactType;

            JPanel improvementJPanel = (JPanel) improvementPage;

            JCheckBox selectBox = (JCheckBox) ((JPanel)improvementJPanel.getComponent(0)).getComponent(0);
            Boolean selected = selectBox.isSelected();

            // This confirms that the user selected this specific heat source 
            if (selected) {

                JLabel nameLabel = (JLabel) improvementJPanel.getComponent(1);
                String name = nameLabel.getText();

                JPanel krPerM2Page = (JPanel) improvementJPanel.getComponent(2);
                try {
                    krPerM2 = Double.parseDouble(((JTextField) krPerM2Page.getComponent(0)).getText());
                } catch (Exception e) {
                    throw new Exception("kr/m^2 not a valid number for " + name);
                }
                if (krPerM2<=0.0) {
                    throw new Exception("kr/m^2 for " + name + " must be greater than 0");
                }


                JPanel yearsOfServicePage = (JPanel) improvementJPanel.getComponent(4);
                try {
                    yearsOfService = Integer
                            .parseInt(((JTextField) yearsOfServicePage.getComponent(0)).getText());
                } catch (Exception e) {
                    throw new Exception("years of service is not a valid number for " + name);
                }
                if (yearsOfService<=0) {
                    throw new Exception("years of service for " + name + " must be greater than 0");
                }

                
                JPanel kwhPerM2Page = (JPanel) improvementJPanel.getComponent(3);
                try {
                    kwhPerM2 = Double.parseDouble(((JTextField) kwhPerM2Page.getComponent(0)).getText());
                    switch ((String)((JComboBox<String>) kwhPerM2Page.getComponent(1)).getSelectedItem()) {
                        case "kwh/m^2 over the improvements lifetime":
                            break;
                        case "kwh per year":
                        kwhPerM2=(kwhPerM2*yearsOfService)/aTemp;
                            break;
                        default:
                            throw new Exception("no unit selected for improvement kwh " + name);
                    }
                } catch (Exception e) {
                    throw new Exception("improvement kwh not a valid number for " + name);
                }
                if (kwhPerM2<=0.0) {
                    throw new Exception("kwh for " + name + " must be greater than 0");
                }

                JPanel impactTypePage = (JPanel) improvementJPanel.getComponent(5);
                try {
                    String currentlySelected=((JRadioButton)impactTypePage.getComponent(0)).getModel().getGroup().getSelection().getActionCommand();
                    System.out.println(currentlySelected);
                    impactType = improvementImpactEnum.valueOf(currentlySelected);
                } catch (Exception e) {
                    throw new Exception("No impact type selected for " + name);
                }

                improvementsCollected.add(new improvement(name, kwhPerM2,krPerM2,
                yearsOfService, impactType));
            }
        }

        if (improvementsCollected.isEmpty()) {
            throw new Exception("No improvements selected, please select one or more");
        }

        return improvementsCollected;
    }

}
