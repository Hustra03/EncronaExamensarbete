package encrona.GUI;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import encrona.domain.improvement;

public class GUIImprovements extends JPanel {

    List<improvement> improvements;
    static JPanel improvementSpecificationPage;

    final static String kwhHeatingOverLifetimeUnit = "kwh/m^2 over the improvements lifetime for building heating";
    final static String kwhHeatingPerYearUnit = "kwh per year for building heating";
    final static String kwhHeatingWaterOverLifetimeUnit = "kwh/m^2 over the improvements lifetime for heating water";
    final static String kwhHeatingWaterPerYearUnit = "kwh per year for water heating";
    final static String kwhElectricityOverLifetimeUnit = "kwh/m^2 over the improvements lifetime for electricity";
    final static String kwhElectricityPerYearUnit = "kwh per year for electricity";
    final static String m3WaterOverImprovementLifetimeUnit = "m^3 water/m^2 Atemp over the improvements lifetime for water";
    final static String m3WaterPerYearUnit = "m^3 per year for water";
    final static String costKrPerM2LifetimeUnit = "kr/m² over the improvements lifetime";
    final static String costkrPerYearUnit = "kr per year";
    final static String costTotalUnit = "total cost";

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
        c.weightx = 1.0;
        add(createNewImprovementPage, c);
        c.weighty = 0.90;
        c.weightx = 0.9;
        add(scrollimprovementSpecificationPage, c);
    }

    /**
     * This creates an improvement page, based on a provided improvement
     * 
     * @param improvement The improvement this page is for
     * @return The improvement page component
     */
    private JPanel createImprovementPage(improvement improvement) {
        JPanel improvementPage = new JPanel();

        JPanel selectButtonPage = new JPanel();
        JCheckBox selectCheckBox = new JCheckBox("Select");
        selectButtonPage.add(selectCheckBox);

        JLabel label = new JLabel(improvement.getName());

        JPanel krPerM2Page = new JPanel();
        JTextField kwhPerYearHeatingField = new JTextField(improvement.getCostPerM2().toString(), 10);
        krPerM2Page.add(kwhPerYearHeatingField);

        String[] costUnitOptions = {costKrPerM2LifetimeUnit, costTotalUnit, costkrPerYearUnit};
        JComboBox<String> costUnitSelection = new JComboBox<>(costUnitOptions);
        costUnitSelection.setSelectedIndex(0);
        krPerM2Page.add(costUnitSelection);
        krPerM2Page.setBorder(BorderFactory.createLineBorder(Color.black));

        JPanel kwhPerM2BuildingHeatingPage = new JPanel();
        JTextField kwhPerM2TextField = new JTextField(improvement.getKwhPerM2BuildingHeating().toString(), 10);
        kwhPerM2BuildingHeatingPage.add(kwhPerM2TextField);
        String[] unitOptions = { kwhHeatingOverLifetimeUnit, kwhHeatingPerYearUnit };
        JComboBox<String> unitSelection = new JComboBox<String>(unitOptions);
        unitSelection.setSelectedIndex(0);
        kwhPerM2BuildingHeatingPage.add(unitSelection);
        kwhPerM2BuildingHeatingPage.setBorder(BorderFactory.createLineBorder(Color.black));

        JPanel kwhPerM2WaterHeatingPage = new JPanel();
        JTextField kwhPerM2WaterHeatingTextField = new JTextField(improvement.getKwhPerM2WaterHeating().toString(), 10);
        kwhPerM2WaterHeatingPage.add(kwhPerM2WaterHeatingTextField);
        String[] unitOptions2 = { kwhHeatingWaterOverLifetimeUnit,
                kwhHeatingWaterPerYearUnit };
        JComboBox<String> unitSelection2 = new JComboBox<String>(unitOptions2);
        unitSelection2.setSelectedIndex(0);
        kwhPerM2WaterHeatingPage.add(unitSelection2);
        kwhPerM2WaterHeatingPage.setBorder(BorderFactory.createLineBorder(Color.black));

        JPanel kwhPerM2ElectricityPage = new JPanel();
        JTextField kwhPerM2ElectricityTextField = new JTextField(improvement.getKwhPerM2Electricity().toString(), 10);
        kwhPerM2ElectricityPage.add(kwhPerM2ElectricityTextField);
        String[] unitOptions3 = { kwhElectricityOverLifetimeUnit, kwhElectricityPerYearUnit };
        JComboBox<String> unitSelection3 = new JComboBox<String>(unitOptions3);
        unitSelection3.setSelectedIndex(0);
        kwhPerM2ElectricityPage.add(unitSelection3);
        kwhPerM2ElectricityPage.setBorder(BorderFactory.createLineBorder(Color.black));

        JPanel m3WaterPage = new JPanel();
        JTextField m3TextField = new JTextField(improvement.getM3PerM2Water().toString(), 10);
        m3WaterPage.add(m3TextField);
        String[] unitOptions4 = { m3WaterOverImprovementLifetimeUnit, m3WaterPerYearUnit };
        JComboBox<String> unitSelection4 = new JComboBox<String>(unitOptions4);
        unitSelection4.setSelectedIndex(0);
        m3WaterPage.add(unitSelection4);
        m3WaterPage.setBorder(BorderFactory.createLineBorder(Color.black));

        JPanel yearsOfServicePage = new JPanel();
        JTextField yearsOfServiceField = new JTextField(improvement.getYearsOfService().toString(), 10);
        yearsOfServicePage.add(yearsOfServiceField);
        yearsOfServicePage.add(new JLabel("years the improvement is efficent"));

        improvementPage.add(selectButtonPage);
        improvementPage.add(label);
        
        improvementPage.add(krPerM2Page);
        improvementPage.add(kwhPerM2BuildingHeatingPage);
        improvementPage.add(kwhPerM2WaterHeatingPage);
        improvementPage.add(kwhPerM2ElectricityPage);
        improvementPage.add(m3WaterPage);
        improvementPage.add(yearsOfServicePage);
        improvementPage.setLayout(new FlowLayout(FlowLayout.RIGHT));
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
                improvement newImprovement = new improvement(name, 0.0, 0.0, 0.0, 0.0, 0.0, 0);
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
     * ordering of its components, so any changes made there will likely break this
     * method
     * <p>
     * 
     * @param aTemp the area of the building
     * @return A list of improvement with the provided values
     * @throws Exception If something goes wrong, with the only expected case being
     *                   if the values provided are invalid
     */
    public static java.util.List<improvement> collectFieldValues(Double aTemp) throws Exception {
        List<improvement> improvementsCollected = new ArrayList<improvement>();

        // This iterates over all of the heat source pages, and all of their components,
        // to retrive the user provided input
        for (Component improvementPage : improvementSpecificationPage.getComponents()) {

            Double krPerM2;
            Double kwhPerM2HeatingBuilding;
            Double kwhPerM2HeatingWater;
            Double kwhPerM2Electricity;
            Double m3WaterPerM2;

            Integer yearsOfService;

            JPanel improvementJPanel = (JPanel) improvementPage;

            JCheckBox selectBox = (JCheckBox) ((JPanel) improvementJPanel.getComponent(0)).getComponent(0);
            Boolean selected = selectBox.isSelected();

            // This confirms that the user selected this specific improvement
            if (selected) {

                JLabel nameLabel = (JLabel) improvementJPanel.getComponent(1);
                String name = nameLabel.getText();

                JPanel yearsOfServicePage = (JPanel) improvementJPanel.getComponent(7);
                try {
                    yearsOfService = Integer
                            .parseInt(((JTextField) yearsOfServicePage.getComponent(0)).getText());
                } catch (Exception e) {
                    throw new Exception("years of service is not a valid number for " + name);
                }
                if (yearsOfService <= 0) {
                    throw new Exception("years of service for " + name + " must be greater than 0");
                }

                JPanel krPerM2Page = (JPanel) improvementJPanel.getComponent(2);
                try {
                    krPerM2 = Double.parseDouble(((JTextField) krPerM2Page.getComponent(0)).getText());
                    switch ((String) ((JComboBox<String>) krPerM2Page.getComponent(1)).getSelectedItem()) {
                        case costKrPerM2LifetimeUnit:
                            break;
                        case costTotalUnit:
                            krPerM2 = krPerM2 / aTemp;
                            break;
                        case costkrPerYearUnit:
                            krPerM2 = (krPerM2 * yearsOfService) / aTemp;
                            break;
                        default:
                            throw new Exception("No valid unit selected for cost for " + name);
                    }

                } catch (Exception e) {
                    throw new Exception("kr/m^2 not a valid number for " + name);
                }
                if (krPerM2 <= 0.0) {
                    throw new Exception("kr/m^2 for " + name + " must be greater than 0");
                }

                JPanel kwhPerM2HeatingBuildingPage = (JPanel) improvementJPanel.getComponent(3);
                try {
                    kwhPerM2HeatingBuilding = Double
                            .parseDouble(((JTextField) kwhPerM2HeatingBuildingPage.getComponent(0)).getText());
                    switch ((String) ((JComboBox<String>) kwhPerM2HeatingBuildingPage.getComponent(1))
                            .getSelectedItem()) {
                        case kwhHeatingOverLifetimeUnit:
                            break;
                        case kwhHeatingPerYearUnit:
                            kwhPerM2HeatingBuilding = (kwhPerM2HeatingBuilding * yearsOfService) / aTemp;
                            break;
                        default:
                            throw new Exception("no unit selected for improvement kwh " + name);
                    }
                } catch (Exception e) {
                    System.out.println(e);
                    throw new Exception("improvement kwh for heating the building is not a valid number for " + name);
                }
                if (kwhPerM2HeatingBuilding < 0.0) {
                    throw new Exception("kwh heating the building for " + name + " must be non-negative");
                }

                JPanel kwhPerM2HeatingWaterPage = (JPanel) improvementJPanel.getComponent(4);
                try {
                    kwhPerM2HeatingWater = Double
                            .parseDouble(((JTextField) kwhPerM2HeatingWaterPage.getComponent(0)).getText());
                    switch ((String) ((JComboBox<String>) kwhPerM2HeatingWaterPage.getComponent(1)).getSelectedItem()) {
                        case kwhHeatingWaterOverLifetimeUnit:
                            break;
                        case kwhHeatingWaterPerYearUnit:
                            kwhPerM2HeatingWater = (kwhPerM2HeatingWater * yearsOfService) / aTemp;
                            break;
                        default:
                            throw new Exception("no unit selected for improvement kwh " + name);
                    }
                } catch (Exception e) {
                    System.out.println(e);
                    throw new Exception("improvement kwh heating water not a valid number for " + name);
                }
                if (kwhPerM2HeatingWater < 0.0) {
                    throw new Exception("kwh heating water for " + name + " must be non-negative");
                }
                JPanel kwhPerM2ElectricityPage = (JPanel) improvementJPanel.getComponent(5);
                try {
                    kwhPerM2Electricity = Double
                            .parseDouble(((JTextField) kwhPerM2ElectricityPage.getComponent(0)).getText());
                    switch ((String) ((JComboBox<String>) kwhPerM2ElectricityPage.getComponent(1)).getSelectedItem()) {
                        case kwhElectricityOverLifetimeUnit:
                            break;
                        case kwhElectricityPerYearUnit:
                            kwhPerM2Electricity = (kwhPerM2Electricity * yearsOfService) / aTemp;
                            break;
                        default:
                            throw new Exception("no unit selected for improvement kwh " + name);
                    }
                } catch (Exception e) {
                    System.out.println(e);
                    throw new Exception("improvement kwh for electricity not a valid number for " + name);
                }
                if (kwhPerM2Electricity < 0.0) {
                    throw new Exception("kwh for electricity for " + name + " must be non-negative");
                }

                JPanel m3PerM2Page = (JPanel) improvementJPanel.getComponent(6);
                try {
                    m3WaterPerM2 = Double.parseDouble(((JTextField) m3PerM2Page.getComponent(0)).getText());
                    switch ((String) ((JComboBox<String>) m3PerM2Page.getComponent(1)).getSelectedItem()) {
                        case m3WaterOverImprovementLifetimeUnit:
                            break;
                        case m3WaterPerYearUnit:
                            m3WaterPerM2 = (m3WaterPerM2 * yearsOfService) / aTemp;
                            break;
                        default:
                            throw new Exception("no unit selected for improvement kwh " + name);
                    }
                } catch (Exception e) {
                    System.out.println(e);
                    throw new Exception("improvement m^3 not a valid number for " + name);
                }
                if (m3WaterPerM2 < 0.0) {
                    throw new Exception("m^3 for " + name + " must be non-negative");
                }

                improvementsCollected.add(new improvement(name, kwhPerM2HeatingBuilding, kwhPerM2HeatingWater,
                        kwhPerM2Electricity, m3WaterPerM2, krPerM2, yearsOfService));
            }
        }

        if (improvementsCollected.isEmpty()) {
            throw new Exception("No improvements selected, please select one or more");
        }

        return improvementsCollected;
    }

}
