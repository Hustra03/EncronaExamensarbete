import java.awt.*;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Map;

import javax.swing.*;

public class MockGUIStartValueSpecification extends JPanel {

    static JTextField aTempField;
    static JTextField electrictyPriceField;
    static JTextField electricityConsumptionField;

    public MockGUIStartValueSpecification() {
        super(new GridBagLayout());

        // We add some basic description text
        JPanel infoPage = new JPanel();
        infoPage.add(new JLabel("Here you give the values for a number of numeric variables"));

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

        // Add Components to this panel.
        GridBagConstraints c = new GridBagConstraints();
        c.gridwidth = GridBagConstraints.REMAINDER;

        c.fill = GridBagConstraints.HORIZONTAL;
        add(infoPage, c);

        c.fill = GridBagConstraints.BOTH;
        c.weightx = 0.25;
        c.weighty = 0.25;
        add(aTempRow, c);
        add(electrictyPriceRow, c);
        add(electricityConsumptionRow, c);
    }

    // This method collects the current values of the field
    public static java.util.List<Map.Entry<Map.Entry<String,String>,Double>> collectFieldValues() throws Exception {
        Double aTempValue;
        try {

            aTempValue = Double.parseDouble(aTempField.getText());
        } catch (Exception e) {
            throw new Exception("Atemp is not a number!");
        }
        
        Double electricityPriceValue;
        try {

            electricityPriceValue = Double.parseDouble(electrictyPriceField.getText());
        } catch (Exception e) {
            throw new Exception("Electricity price is not a number!");
        }

        Double electrictyConsumptionValue;
        try {

            electrictyConsumptionValue = Double.parseDouble(electricityConsumptionField.getText());
        } catch (Exception e) {
            throw new Exception("Electricity consumption is not a number!");
        }
        new AbstractMap.SimpleEntry<String, String>("Atemp","m^2");
        java.util.List<Map.Entry<Map.Entry<String,String>,Double>> listOfNumericalVariables = new ArrayList<Map.Entry<Map.Entry<String,String>,Double>>();
        listOfNumericalVariables.add(new AbstractMap.SimpleEntry<Map.Entry<String,String>,Double>(new AbstractMap.SimpleEntry<String, String>("Atemp","m^2"), aTempValue));
        listOfNumericalVariables.add(new AbstractMap.SimpleEntry<Map.Entry<String,String>,Double>(new AbstractMap.SimpleEntry<String, String>("Electricty price","kr/kwh"), electricityPriceValue));
        listOfNumericalVariables.add(new AbstractMap.SimpleEntry<Map.Entry<String,String>,Double>(new AbstractMap.SimpleEntry<String, String>("Electricty consumption","kwh/year"), electrictyConsumptionValue));
        return listOfNumericalVariables;
    }

}