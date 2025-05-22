package encrona.GUI;

import java.awt.*;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;

import javax.swing.*;


/**
 * This class is responsible for the start value (or numerical input) GUI page
 */
public class GUIStartValueSpecification extends JPanel {

    public static final String atemp="Atemp";
    static JTextField aTempField=new JTextField("1",20);
    static JTextField electrictyPriceField=new JTextField("1",20);
    static JTextField electricityConsumptionField=new JTextField("1",20);
    static JTextField waterConsumptionField=new JTextField("1",20);
    static JTextField waterPriceField = new JTextField("1",20);

    /**
     * This creates the start value (or numerical input) GUI page
     */
    public GUIStartValueSpecification() {

        this.setLayout(new GridBagLayout());


        // We add some basic description text
        JPanel infoPage = new JPanel();
        infoPage.add(new JLabel("Here you give the values for a number of numeric variables"));

        //Note that the label values must not be modified without changing those in the data loader, since the name is the key for the map!
        JPanel aTempRow = new JPanel();
        aTempRow.add(new JLabel(atemp));
        aTempRow.add(aTempField);
        aTempRow.add(new JLabel("m^2"));

        JPanel electrictyPriceRow = new JPanel();
        electrictyPriceRow.add(new JLabel("Electricty price"));
        electrictyPriceRow.add(electrictyPriceField);
        electrictyPriceRow.add(new JLabel("kr/kwh"));

        JPanel electricityConsumptionRow = new JPanel();
        electricityConsumptionRow.add(new JLabel("Electricty consumption"));
        electricityConsumptionRow.add(electricityConsumptionField);
        electricityConsumptionRow.add(new JLabel("kwh/year"));

        JPanel waterConsumptionRow = new JPanel();
        waterConsumptionRow.add(new JLabel("Water consumption"));
        waterConsumptionRow.add(waterConsumptionField);
        waterConsumptionRow.add(new JLabel("m^3/year"));

        JPanel waterPriceRow = new JPanel();
        waterPriceRow.add(new JLabel("Water price"));
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
        add(electricityConsumptionRow, c);
        add(electrictyPriceRow, c);
        add(waterConsumptionRow, c);
        add(waterPriceRow, c);

    }

    /**
     * This collects the current numerical values
     * @return A map, where each entry consists of <<name,unit>,value>
     * @throws CustomUIException If something goes wrong, this exception is thrown (to be handled by the main class)
     */
    public static Map<Map.Entry<String,String>,Double> collectFieldValues() throws CustomUIException {
        Double aTempValue;
        try {

            aTempValue = Double.parseDouble(aTempField.getText());
        } catch (Exception e) {
            throw new CustomUIException("Atemp is not a number!");
        }
        if (aTempValue<=0.0) {
            throw new CustomUIException("Atemp must be more than 0!");
        }
        
        Double electricityPriceValue;
        try {

            electricityPriceValue = Double.parseDouble(electrictyPriceField.getText());
        } catch (Exception e) {
            throw new CustomUIException("Electricity price is not a number!");
        }
        if (electricityPriceValue<=0.0) {
            throw new CustomUIException("Electricity price must be greater than 0!");
        }

        Double electrictyConsumptionValue;
        try {

            electrictyConsumptionValue = Double.parseDouble(electricityConsumptionField.getText());
        } catch (Exception e) {
            throw new CustomUIException("Electricity consumption is not a number!");
        }
        if (electrictyConsumptionValue<=0.0) {
            throw new CustomUIException("Electricity consumption must be greater than 0!");
        }

        Double waterConsumptionValue;
        try {

            waterConsumptionValue = Double.parseDouble(waterConsumptionField.getText());
        } catch (Exception e) {
            throw new CustomUIException("Water consumption is not a number!");
        }
        if (electrictyConsumptionValue<=0.0) {
            throw new CustomUIException("Water consumption must be greater than 0!");
        }

        Double waterPriceValue;
        try {

            waterPriceValue = Double.parseDouble(waterPriceField.getText());
        } catch (Exception e) {
            throw new CustomUIException("Water price is not a number!");
        }
        if (electrictyConsumptionValue<=0.0) {
            throw new CustomUIException("Water price must be greater than 0!");
        }

        new AbstractMap.SimpleEntry<String, String>(atemp,"m^2");
        Map<Map.Entry<String,String>,Double> map = new HashMap<>();
        map.put(new AbstractMap.SimpleEntry<>(atemp,"m^2"), aTempValue);
        map.put(new AbstractMap.SimpleEntry<>("Electricty price","kr/kwh"), electricityPriceValue);
        map.put(new AbstractMap.SimpleEntry<>("Electricty consumption","kwh/year"), electrictyConsumptionValue);
        map.put(new AbstractMap.SimpleEntry<>("Water consumption","m^3/year"), waterConsumptionValue);
        map.put(new AbstractMap.SimpleEntry<>("Water price","kr/m^3"), waterPriceValue);

        return map;
    }

}