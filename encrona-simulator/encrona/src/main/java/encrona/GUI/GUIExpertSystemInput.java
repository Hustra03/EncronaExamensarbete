package encrona.GUI;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import javax.swing.*;

import encrona.components.input;
public class GUIExpertSystemInput extends JPanel{
    
    static JTextField heatCentralAgeField=new JTextField("1",20);
    static JCheckBox naturalVentilationCheckbox=new JCheckBox();



    public GUIExpertSystemInput() {

        this.setLayout(new GridBagLayout());

        // We add some basic description text
        JPanel infoPage = new JPanel();
        infoPage.add(new JLabel("Here you give the input specifically for the expert system"));

        //Note that the label values must not be modified without changing those in the data loader, since the name is the key for the map!
        JPanel heatCentralAgeRow = new JPanel();
        heatCentralAgeRow.add(new JLabel("Heating control system age"));
        heatCentralAgeRow.add(heatCentralAgeField);
        heatCentralAgeRow.add(new JLabel("years"));

        JPanel naturalVentilationRow = new JPanel();
        naturalVentilationRow.add(new JLabel("Does the building have natural ventilation?"));
        naturalVentilationRow.add(naturalVentilationCheckbox);

        // Add Components to this panel.
        GridBagConstraints c = new GridBagConstraints();
        c.gridwidth = GridBagConstraints.REMAINDER;

        c.fill = GridBagConstraints.HORIZONTAL;
        add(infoPage, c);

        c.fill = GridBagConstraints.BOTH;
        c.weightx = 0.25;
        c.weighty = 0;
        add(heatCentralAgeRow, c);
        add(naturalVentilationRow, c);

    }

    // This method collects the current values of the field
    public static Map<String,input<?>> collectFieldValues() throws CustomUIException {
        Map<String,input<?>> map = new HashMap<>();       

        Double heatCentralAge;
        try {

            heatCentralAge = Double.parseDouble(heatCentralAgeField.getText());
        } catch (Exception _) {
            throw new CustomUIException("heatCentralAge is not a number!");
        }
        if (heatCentralAge<=0.0) {
            throw new CustomUIException("heatCentralAge must be greater than 0!");
        }
        map.put("heatCentralAge", new input<>("heatCentralAge", "year", heatCentralAge));

        map.put("naturalVentilation", new input<>("naturalVentilation", "N/A", (naturalVentilationCheckbox.isSelected())));

        return map;
    }

}