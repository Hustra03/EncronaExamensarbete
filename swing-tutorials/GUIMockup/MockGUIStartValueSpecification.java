import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;


public class MockGUIStartValueSpecification extends JPanel{
    
    public MockGUIStartValueSpecification() {
        super(new GridBagLayout());

        //We add some basic description text
        JPanel infoPage = new JPanel();
        infoPage.add(new JLabel("Here you give the values for a number of numeric variables"));

        JPanel aTempRow = new JPanel();
        aTempRow.add(new JLabel("Atemp, m^2 to heat"));
        JTextField aTempField = new JTextField(20);
        aTempRow.add(aTempField);

        JPanel electrictyPriceRow = new JPanel();
        electrictyPriceRow.add(new JLabel("Electricty price, kr/kwh"));
        JTextField electrictyPriceField = new JTextField(20);
        electrictyPriceRow.add(electrictyPriceField);

        JPanel electricityConsumptionRow = new JPanel();
        electricityConsumptionRow.add(new JLabel("Electricty consumption, kwh/year"));
        JTextField electricityConsumptionField = new JTextField(20);
        electricityConsumptionRow.add(electricityConsumptionField);


        //Add Components to this panel.
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

}