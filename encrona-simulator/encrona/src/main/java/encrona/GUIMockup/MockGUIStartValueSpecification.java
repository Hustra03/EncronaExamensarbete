package encrona.GUIMockup;

import java.awt.*;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.*;

public class MockGUIStartValueSpecification extends JPanel {

    static JTextField aTempField;
    static JTextField electrictyPriceField;
    static JTextField electricityConsumptionField;
    static JTextField waterConsumptionField;
    static JTextField waterPriceField;

    public MockGUIStartValueSpecification() {

        this.setLayout(new GridBagLayout());


        // We add some basic description text
        JPanel infoPage = new JPanel();
        infoPage.add(new JLabel("Here you give the values for a number of numeric variables"));

        //Note that the label values must not be modified without changing those in the data loader, since the name is the key for the map!
        JPanel aTempRow = new JPanel();
        aTempRow.add(new JLabel("Atemp"));
        aTempField = new JTextField("1",20);
        aTempRow.add(aTempField);
        aTempRow.add(new JLabel("m^2"));

        JPanel electrictyPriceRow = new JPanel();
        electrictyPriceRow.add(new JLabel("Electricty price"));
        electrictyPriceField = new JTextField("1",20);
        electrictyPriceRow.add(electrictyPriceField);
        electrictyPriceRow.add(new JLabel("kr/kwh"));

        JPanel electricityConsumptionRow = new JPanel();
        electricityConsumptionRow.add(new JLabel("Electricty consumption"));
        electricityConsumptionField = new JTextField("1",20);
        electricityConsumptionRow.add(electricityConsumptionField);
        electricityConsumptionRow.add(new JLabel("kwh/year"));

        JPanel waterConsumptionRow = new JPanel();
        waterConsumptionRow.add(new JLabel("Water consumption"));
        waterConsumptionField = new JTextField("1",20);
        waterConsumptionRow.add(waterConsumptionField);
        waterConsumptionRow.add(new JLabel("m^3/year"));

        JPanel waterPriceRow = new JPanel();
        waterPriceRow.add(new JLabel("Water price"));
        waterPriceField = new JTextField("1",20);
        waterPriceRow.add(waterPriceField);
        waterPriceRow.add(new JLabel("kr/m^3"));

        // Add Components to this panel.
        GridBagConstraints c = new GridBagConstraints();
        c.gridwidth = GridBagConstraints.REMAINDER;

        c.fill = GridBagConstraints.HORIZONTAL;
        add(infoPage, c);

        c.fill = GridBagConstraints.BOTH;
        c.weightx = 0.25;
        c.weighty = 0;
        add(aTempRow, c);
        add(electrictyPriceRow, c);
        add(electricityConsumptionRow, c);
        add(waterConsumptionRow, c);
        add(waterPriceRow, c);

    }

    // This method collects the current values of the field
    public static Map<Map.Entry<String,String>,Double> collectFieldValues() throws Exception {
        Double aTempValue;
        try {

            aTempValue = Double.parseDouble(aTempField.getText());
        } catch (Exception e) {
            throw new Exception("Atemp is not a number!");
        }
        if (aTempValue<=0.0) {
            throw new Exception("Atemp must be more than 0!");
        }
        
        Double electricityPriceValue;
        try {

            electricityPriceValue = Double.parseDouble(electrictyPriceField.getText());
        } catch (Exception e) {
            throw new Exception("Electricity price is not a number!");
        }
        if (electricityPriceValue<=0.0) {
            throw new Exception("Electricity price must be greater than 0!");
        }

        Double electrictyConsumptionValue;
        try {

            electrictyConsumptionValue = Double.parseDouble(electricityConsumptionField.getText());
        } catch (Exception e) {
            throw new Exception("Electricity consumption is not a number!");
        }
        if (electrictyConsumptionValue<=0.0) {
            throw new Exception("Electricity consumption must be greater than 0!");
        }

        Double waterConsumptionValue;
        try {

            waterConsumptionValue = Double.parseDouble(waterConsumptionField.getText());
        } catch (Exception e) {
            throw new Exception("Water consumption is not a number!");
        }
        if (electrictyConsumptionValue<=0.0) {
            throw new Exception("Water consumption must be greater than 0!");
        }

        Double waterPriceValue;
        try {

            waterPriceValue = Double.parseDouble(waterPriceField.getText());
        } catch (Exception e) {
            throw new Exception("Water price is not a number!");
        }
        if (electrictyConsumptionValue<=0.0) {
            throw new Exception("Water price must be greater than 0!");
        }

        new AbstractMap.SimpleEntry<String, String>("Atemp","m^2");
        Map<Map.Entry<String,String>,Double> map = new HashMap<Map.Entry<String,String>,Double>();
        map.put(new AbstractMap.SimpleEntry<String, String>("Atemp","m^2"), aTempValue);
        map.put(new AbstractMap.SimpleEntry<String, String>("Electricty price","kr/kwh"), electricityPriceValue);
        map.put(new AbstractMap.SimpleEntry<String, String>("Electricty consumption","kwh/year"), electrictyConsumptionValue);
        map.put(new AbstractMap.SimpleEntry<String, String>("Water consumption","m^3/year"), waterConsumptionValue);
        map.put(new AbstractMap.SimpleEntry<String, String>("Water price","kr/m^3"), waterPriceValue);

        return map;
    }

}