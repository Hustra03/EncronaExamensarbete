import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentListener;

import encrona.domain.heatingEnergySource;

public class MockGUIHeatingSources extends JPanel{

    List<heatingEnergySource> energySources;
    static JPanel heatSourceSpecificationPage;

    public MockGUIHeatingSources(List<heatingEnergySource> initialHeatingEnergySources) {
        super(new GridBagLayout());

        JPanel infoPage = new JPanel();
        infoPage.add(new JLabel("Here you give the values for heat sources for the building"));
        energySources=initialHeatingEnergySources;

        heatSourceSpecificationPage = new JPanel();
        JScrollPane scrollheatSourceSpecificationPage = new JScrollPane(heatSourceSpecificationPage);
        heatSourceSpecificationPage.setLayout(new BoxLayout(heatSourceSpecificationPage, BoxLayout.PAGE_AXIS));

        for (heatingEnergySource heatingEnergySource : initialHeatingEnergySources) {
            JPanel heatSourcePage = createHeatSourcePage(heatingEnergySource);
            heatSourceSpecificationPage.add(heatSourcePage);
        }

        JPanel createNewHeatSourcePage =new JPanel();

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
        c.weighty=0.10;
        add(createNewHeatSourcePage, c);
        c.weighty=0.90;

        add(scrollheatSourceSpecificationPage, c);
    }
    

    private JPanel createHeatSourcePage(heatingEnergySource source)
    {            
        JPanel heatSourcePage = new JPanel();
        heatSourcePage.add(new JLabel(source.getName()));

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

        heatSourcePage.add(costPerKWHPage);
        heatSourcePage.add(kwhPerYearHeatingPage);
        heatSourcePage.add(kwhPerYearHeatingWaterPage);
        return heatSourcePage;
    }

    /**
     * This handles if the button to create a new 
     */
     class CreateNewListener implements ActionListener {
         private JTextField createNewNameField;
  
         public CreateNewListener(JTextField createNewNameField) {
             this.createNewNameField=createNewNameField;
         }
      
        @Override
         public void actionPerformed(ActionEvent e) {

            if ("createNew".equals(e.getActionCommand())) {
                String name = createNewNameField.getText();
                if (name.equals("")) {
                    
                    JOptionPane.showMessageDialog(heatSourceSpecificationPage,"No name was given for the new heating source","The provided input is invalid",JOptionPane.PLAIN_MESSAGE);
                   return;
                }
    
                for (heatingEnergySource heatingEnergySource : energySources) {
                    if (heatingEnergySource.getName().equals(name)) {
                        createNewNameField.setText("");
                        JOptionPane.showMessageDialog(heatSourceSpecificationPage,"The heating source already exists","The provided input is invalid",JOptionPane.PLAIN_MESSAGE);

                        return;
                    }
                }
                heatingEnergySource newHeatingEnergySource =new heatingEnergySource(name, 0.0, 0.0, 0.0, 0.0);
                energySources.add(newHeatingEnergySource);
                JPanel heatSourcePage = createHeatSourcePage(newHeatingEnergySource);
                heatSourceSpecificationPage.add(heatSourcePage);
            } 
            heatSourceSpecificationPage.updateUI();
        }
  
     }

    //This method collects the current values from the heat sources
    public static java.util.List<heatingEnergySource> collectFieldValues() throws Exception
    {
        List<heatingEnergySource> heatSources = new ArrayList<heatingEnergySource>();

        //This iterates over all of the heat source pages, and all of their components, and records
        for (Component heatSourcePage : heatSourceSpecificationPage.getComponents()) {

            Double costPerKWH;
            Double kwhHeatingPerYear;
            Double kwhHeatingWaterPerYear;

            JPanel heatSourceJPanel=(JPanel)heatSourcePage;

            JLabel nameField = (JLabel)heatSourceJPanel.getComponent(0);
            String name =nameField.getText();


            JPanel pricePerKwhPanel=(JPanel)heatSourceJPanel.getComponent(1);
            try {
                costPerKWH=Double.parseDouble(((JTextField)pricePerKwhPanel.getComponent(0)).getText());
            } catch (Exception e) {
                throw new Exception("Kr/kwh not a valid number for"+name);
            }

            JPanel kwhPerYearPanel=(JPanel)heatSourceJPanel.getComponent(2);
            try {
                kwhHeatingPerYear=Double.parseDouble(((JTextField)kwhPerYearPanel.getComponent(0)).getText());
            } catch (Exception e) {
                throw new Exception("Kr/kwh not a valid number for"+name);
            }

            JPanel kwhPerYearWaterPanel=(JPanel)heatSourceJPanel.getComponent(3);
            try {
                kwhHeatingWaterPerYear=Double.parseDouble(((JTextField)kwhPerYearWaterPanel.getComponent(0)).getText());
            } catch (Exception e) {
                throw new Exception("Kr/kwh not a valid number for"+name);
            }

            heatSources.add(new heatingEnergySource(nameField.getText(), kwhHeatingPerYear, kwhHeatingWaterPerYear, 0.0, costPerKWH));

        }

        return heatSources;
    }

}
