package encrona.GUIMockup;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import encrona.domain.heatingEnergySource;


public class MockGUIHeatingSources extends JPanel {

    List<heatingEnergySource> energySources;
    static JPanel heatSourceSpecificationPage;

    public MockGUIHeatingSources(List<heatingEnergySource> initialHeatingEnergySources) {
        super(new GridBagLayout());

        JPanel infoPage = new JPanel();
        infoPage.add(new JLabel("Here you give the values for heat sources for the building"));
        energySources = initialHeatingEnergySources;

        heatSourceSpecificationPage = new JPanel();
        JScrollPane scrollheatSourceSpecificationPage = new JScrollPane(heatSourceSpecificationPage);
        heatSourceSpecificationPage.setLayout(new BoxLayout(heatSourceSpecificationPage, BoxLayout.PAGE_AXIS));

        for (heatingEnergySource heatingEnergySource : initialHeatingEnergySources) {
            JPanel heatSourcePage = createHeatSourcePage(heatingEnergySource);
            heatSourceSpecificationPage.add(heatSourcePage);
        }

        JPanel createNewHeatSourcePage = new JPanel();

        JButton createNewButton = new JButton("Create new heat source");
        JTextField createNewNameField = new JTextField(20);
        CreateNewListener CreateNewListener = new CreateNewListener(createNewNameField);
        createNewButton.addActionListener(CreateNewListener);
        createNewButton.setActionCommand("createNew");

        createNewHeatSourcePage.add(createNewButton);
        createNewHeatSourcePage.add(createNewNameField);

        // Add Components to this panel.
        GridBagConstraints c = new GridBagConstraints();
        c.gridwidth = GridBagConstraints.REMAINDER;

        c.fill = GridBagConstraints.HORIZONTAL;
        add(infoPage, c);

        c.fill = GridBagConstraints.BOTH;
        c.weighty = 0.10;
        add(createNewHeatSourcePage, c);
        c.weighty = 0.90;

        add(scrollheatSourceSpecificationPage, c);
    }

    private JPanel createHeatSourcePage(heatingEnergySource source) {
        JPanel heatSourcePage = new JPanel();

        JPanel selectButtonPage = new JPanel();
        JCheckBox selectCheckBox = new JCheckBox("Select");
        selectButtonPage.add(selectCheckBox);

        JPanel costPerKWHPage = new JPanel();
        JTextField costPerKWHField = new JTextField(source.getCostPerKwh().toString(), 10);
        costPerKWHPage.add(costPerKWHField);
        costPerKWHPage.add(new JLabel("kr/kwh"));

        JPanel kwhPerYearHeatingPage = new JPanel();
        JTextField kwhPerYearHeatingField = new JTextField(source.getKwhPerYearHeating().toString(), 10);
        kwhPerYearHeatingPage.add(kwhPerYearHeatingField);
        kwhPerYearHeatingPage.add(new JLabel("kwh/year for heating the building"));

        JPanel kwhPerYearHeatingWaterPage = new JPanel();
        JTextField kwhPerYearHeatingWaterField = new JTextField(source.getKwhPerYearHeatingWater().toString(), 10);
        kwhPerYearHeatingWaterPage.add(kwhPerYearHeatingWaterField);
        kwhPerYearHeatingWaterPage.add(new JLabel("kwh/year for heating water"));

        JPanel electricityNeededPanel = new JPanel();
        JTextField electricityNeededField = new JTextField(source.getKwhPerYearInElectricity().toString(), 10);
        electricityNeededPanel.add(electricityNeededField);
        electricityNeededPanel.add(new JLabel("kwh in electricity needed for the source to operate"));

        heatSourcePage.add(selectButtonPage);
        heatSourcePage.add(new JLabel(source.getName()));
        heatSourcePage.add(costPerKWHPage);
        heatSourcePage.add(kwhPerYearHeatingPage);
        heatSourcePage.add(kwhPerYearHeatingWaterPage);
        heatSourcePage.add(electricityNeededPanel);

        return heatSourcePage;
    }

    /**
     * This handles if the button to create a new
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

                    JOptionPane.showMessageDialog(heatSourceSpecificationPage,
                            "No name was given for the new heating source", "The provided input is invalid",
                            JOptionPane.PLAIN_MESSAGE);
                    return;
                }

                for (heatingEnergySource heatingEnergySource : energySources) {
                    if (heatingEnergySource.getName().equals(name)) {
                        createNewNameField.setText("");
                        JOptionPane.showMessageDialog(heatSourceSpecificationPage, "The heating source already exists",
                                "The provided input is invalid", JOptionPane.PLAIN_MESSAGE);

                        return;
                    }
                }
                heatingEnergySource newHeatingEnergySource = new heatingEnergySource(name, 0.0, 0.0, 0.0, 0.0);
                energySources.add(newHeatingEnergySource);
                JPanel heatSourcePage = createHeatSourcePage(newHeatingEnergySource);
                heatSourceSpecificationPage.add(heatSourcePage);
            }
            heatSourceSpecificationPage.updateUI();
        }

    }

    /**
     * This method collects the current values from the heat sources which currently
     * exist in the heat source component
     * <p>
     * Note that this depends on the structure of the heatSource page, and the
     * ordering of its components, so any changes made there will likely break this method
     * <p>
     * 
     * @return A list of heat sources with the provided values
     * @throws Exception If something goes wrong, with the only expected case being
     *                   if the values provided are invalid
     */
    public static java.util.List<heatingEnergySource> collectFieldValues() throws Exception {
        List<heatingEnergySource> heatSources = new ArrayList<heatingEnergySource>();

        // This iterates over all of the heat source pages, and all of their components, to retrive the user provided input
        for (Component heatSourcePage : heatSourceSpecificationPage.getComponents()) {

            Double costPerKWH;
            Double kwhHeatingPerYear;
            Double kwhHeatingWaterPerYear;
            Double kwhElectricityNeeded;

            JPanel heatSourceJPanel = (JPanel) heatSourcePage;

            JCheckBox selectBox = (JCheckBox) ((JPanel)heatSourceJPanel.getComponent(0)).getComponent(0);
            Boolean selected = selectBox.isSelected();

            // This confirms that the user selected this specific heat source 
            if (selected) {

                JLabel nameField = (JLabel) heatSourceJPanel.getComponent(1);
                String name = nameField.getText();

                JPanel pricePerKwhPanel = (JPanel) heatSourceJPanel.getComponent(2);
                try {
                    costPerKWH = Double.parseDouble(((JTextField) pricePerKwhPanel.getComponent(0)).getText());
                } catch (Exception e) {
                    throw new Exception("Kr/kwh is not a valid number for " + name);
                }
                if (costPerKWH<=0.0) {
                    throw new Exception("Kr/kwh for " + name + " must be greater than 0");
                }

                JPanel kwhPerYearPanel = (JPanel) heatSourceJPanel.getComponent(3);
                try {
                    kwhHeatingPerYear = Double.parseDouble(((JTextField) kwhPerYearPanel.getComponent(0)).getText());
                } catch (Exception e) {
                    throw new Exception("kwh per year for heating is not a valid number for " + name);
                }
                if (kwhHeatingPerYear<0.0) {
                    throw new Exception("kwh per year for heating for " + name + " must not be negative");
                }

                JPanel kwhPerYearWaterPanel = (JPanel) heatSourceJPanel.getComponent(4);
                try {
                    kwhHeatingWaterPerYear = Double
                            .parseDouble(((JTextField) kwhPerYearWaterPanel.getComponent(0)).getText());
                } catch (Exception e) {
                    throw new Exception("kwh per year for heating water is not a valid number for " + name);
                }
                if (kwhHeatingWaterPerYear<0.0) {
                    throw new Exception("kwh per year for heating water for " + name + " must not be negative");
                }

                JPanel kwhInElectricityPerYearPage = (JPanel) heatSourceJPanel.getComponent(5);
                try {
                    kwhElectricityNeeded = Double.parseDouble(((JTextField) kwhInElectricityPerYearPage.getComponent(0)).getText());
                } catch (Exception e) {
                    throw new Exception("Electricity for heat source is not a valid number for " + name);
                }
                if (kwhElectricityNeeded<0) {
                    throw new Exception("Electricity for heat source for " + name + " must be greater than 0");
                }


                heatSources.add(new heatingEnergySource(nameField.getText(), kwhHeatingPerYear, kwhHeatingWaterPerYear,
                kwhElectricityNeeded, costPerKWH));
            }
        }

        if (heatSources.isEmpty()) {
            throw new Exception("No heat source selected, please select one or more");
        }

        return heatSources;
    }

}
