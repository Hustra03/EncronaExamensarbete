package encrona.GUI;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import javax.swing.*;

import encrona.components.input;
public class GUIExpertSystemInput extends JPanel{
    
    static JCheckBox hasFTXCheckbox=new JCheckBox();
    static JCheckBox hasFVPCheckbox=new JCheckBox();
    static JCheckBox existingUnusedGarbageDisposalChuteCheckbox=new JCheckBox();
    static JCheckBox naturalVentilationCheckbox=new JCheckBox();
    static JCheckBox hasFromAndSupplyAirCheckbox=new JCheckBox();
    static JCheckBox lessThan3LayersOfWindowsCheckbox=new JCheckBox();
    static JCheckBox isPreservationOrderedCheckbox=new JCheckBox();
    static JCheckBox notFulfillingIMDWarmWaterEnergyRequiermentCheckbox=new JCheckBox();
    static JCheckBox newlyBuiltCheckbox=new JCheckBox();
    static JCheckBox replumbingNeededCheckbox=new JCheckBox();

    static JTextField heatingControlSystemAgeField=new JTextField("1",20);
    static JTextField lightingInstallationAgeField=new JTextField("1",20);
    static JTextField facadeInsulationAgeField=new JTextField("1",20);
    static JTextField atticInsulationAgeField=new JTextField("1",20);
    static JTextField roofAgeField=new JTextField("1",20);

    public GUIExpertSystemInput() {

        this.setLayout(new GridBagLayout());

        // We add create a panel with basic description text
        JPanel infoPage = new JPanel();
        infoPage.add(new JLabel("Here you give the input specifically for the expert system"));

        //Here we create the panels for the checkboxes
        JPanel ftxRow = new JPanel();
        ftxRow.add(new JLabel("Does the building have FTX installed already?"));
        ftxRow.add(hasFTXCheckbox);

        JPanel fvpRow = new JPanel();
        fvpRow.add(new JLabel("Does the building have FVP installed already?"));
        fvpRow.add(hasFVPCheckbox);

        JPanel garbageRow = new JPanel();
        garbageRow.add(new JLabel("Does the building an unused garbage chute or something similar?"));
        garbageRow.add(existingUnusedGarbageDisposalChuteCheckbox);

        JPanel naturalVentilationRow = new JPanel();
        naturalVentilationRow.add(new JLabel("Does the building have natural ventilation?"));
        naturalVentilationRow.add(naturalVentilationCheckbox);

        JPanel fromAndSupplyAirRow = new JPanel();
        fromAndSupplyAirRow.add(new JLabel("Does the building have from and supply air (Från och tillluft)?"));
        fromAndSupplyAirRow.add(hasFromAndSupplyAirCheckbox);

        JPanel windowLayersRow = new JPanel();
        windowLayersRow.add(new JLabel("Does the buildings windows have less than 3 layers?"));
        windowLayersRow.add(lessThan3LayersOfWindowsCheckbox);

        JPanel preservationOrderedRow = new JPanel();
        preservationOrderedRow.add(new JLabel("Is the building preservation ordered (K-märkt)?"));
        preservationOrderedRow.add(isPreservationOrderedCheckbox);

        JPanel imdWarmWaterEnergyRequiermentRow = new JPanel();
        imdWarmWaterEnergyRequiermentRow.add(new JLabel("Does the building not fulfill the energy requierment to avoid IMD warm water?"));
        imdWarmWaterEnergyRequiermentRow.add(notFulfillingIMDWarmWaterEnergyRequiermentCheckbox);

        JPanel newlyBuiltRow = new JPanel();
        newlyBuiltRow.add(new JLabel("Is the building newly built?"));
        newlyBuiltRow.add(newlyBuiltCheckbox);

        JPanel reblumbingRow = new JPanel();
        reblumbingRow.add(new JLabel("Does the building need replumbing soon?"));
        reblumbingRow.add(replumbingNeededCheckbox);

        //Here we create the panels for the 

        JPanel heatCentralAgeRow = new JPanel();
        heatCentralAgeRow.add(new JLabel("Heating control system age"));
        heatCentralAgeRow.add(heatingControlSystemAgeField);
        heatCentralAgeRow.add(new JLabel("years"));

        JPanel lightingInstallationAgeRow = new JPanel();
        lightingInstallationAgeRow.add(new JLabel("Lighting installation age"));
        lightingInstallationAgeRow.add(lightingInstallationAgeField);
        lightingInstallationAgeRow.add(new JLabel("years"));

        JPanel facadeInsulationAgeRow = new JPanel();
        facadeInsulationAgeRow.add(new JLabel("Facade insulation age"));
        facadeInsulationAgeRow.add(facadeInsulationAgeField);
        facadeInsulationAgeRow.add(new JLabel("years"));

        JPanel atticInsulationRow = new JPanel();
        atticInsulationRow.add(new JLabel("Attic insulation age"));
        atticInsulationRow.add(atticInsulationAgeField);
        atticInsulationRow.add(new JLabel("years"));

        JPanel roofAgeRow = new JPanel();
        roofAgeRow.add(new JLabel("Roof age"));
        roofAgeRow.add(roofAgeField);
        roofAgeRow.add(new JLabel("years"));


        // Add Components to this panel.
        GridBagConstraints c = new GridBagConstraints();
        c.gridwidth = GridBagConstraints.REMAINDER;

        c.fill = GridBagConstraints.HORIZONTAL;
        add(infoPage, c);

        c.fill = GridBagConstraints.BOTH;
        c.weightx = 0.25;
        c.weighty = 0;
        //Here are the checkboxes
        add(ftxRow, c);
        add(fvpRow, c);
        add(garbageRow, c);
        add(naturalVentilationRow, c);
        add(fromAndSupplyAirRow, c);
        add(windowLayersRow, c);
        add(preservationOrderedRow, c);
        add(imdWarmWaterEnergyRequiermentRow, c);
        add(newlyBuiltRow, c);
        add(reblumbingRow, c);

        //Here are the text fields
        add(heatCentralAgeRow, c);
        add(lightingInstallationAgeRow, c);
        add(facadeInsulationAgeRow, c);
        add(atticInsulationRow, c);
        add(roofAgeRow, c);
    }

    // This method collects the current values of the field
    public static Map<String,input<?>> collectFieldValues() throws CustomUIException {
        Map<String,input<?>> map = new HashMap<>();       

        Double heatingControlSystemAge;
        try {

            heatingControlSystemAge = Double.parseDouble(heatingControlSystemAgeField.getText());
        } catch (Exception _) {
            throw new CustomUIException("heatingControlSystemAge is not a number!");
        }
        if (heatingControlSystemAge<=0.0) {
            throw new CustomUIException("heatingControlSystemAge must be greater than 0!");
        }
        map.put("heatingControlSystemAge", new input<>("heatingControlSystemAge", "year", heatingControlSystemAge));

        Double lightingInstallationAge;
        try {

            lightingInstallationAge = Double.parseDouble(lightingInstallationAgeField.getText());
        } catch (Exception _) {
            throw new CustomUIException("lightingInstallationAge is not a number!");
        }
        if (lightingInstallationAge<=0.0) {
            throw new CustomUIException("lightingInstallationAge must be greater than 0!");
        }
        map.put("lightingInstallationAge", new input<>("lightingInstallationAge", "year", lightingInstallationAge));

        Double facadeInsulationAge;
        try {

            facadeInsulationAge = Double.parseDouble(facadeInsulationAgeField.getText());
        } catch (Exception _) {
            throw new CustomUIException("facadeInsulationAge is not a number!");
        }
        if (facadeInsulationAge<=0.0) {
            throw new CustomUIException("facadeInsulationAge must be greater than 0!");
        }
        map.put("facadeInsulationAge", new input<>("facadeInsulationAge", "year", facadeInsulationAge));
        
        Double atticInsulationAge;
        try {

            atticInsulationAge = Double.parseDouble(atticInsulationAgeField.getText());
        } catch (Exception _) {
            throw new CustomUIException("atticInsulationAge is not a number!");
        }
        if (atticInsulationAge<=0.0) {
            throw new CustomUIException("atticInsulationAge must be greater than 0!");
        }
        map.put("atticInsulationAge", new input<>("atticInsulationAge", "year", atticInsulationAge));

        Double roofAge;
        try {

            roofAge = Double.parseDouble(roofAgeField.getText());
        } catch (Exception _) {
            throw new CustomUIException("roofAge is not a number!");
        }
        if (roofAge<=0.0) {
            throw new CustomUIException("roofAge must be greater than 0!");
        }
        map.put("roofAge", new input<>("roofAge", "year", roofAge));

        map.put("hasFTX", new input<>("hasFTX", "N/A", (hasFTXCheckbox.isSelected())));
        map.put("hasFVP", new input<>("hasFVP", "N/A", (hasFVPCheckbox.isSelected())));
        map.put("existingUnusedChute", new input<>("existingUnusedChute", "N/A", (existingUnusedGarbageDisposalChuteCheckbox.isSelected())));
        map.put("naturalVentilation", new input<>("naturalVentilation", "N/A", (naturalVentilationCheckbox.isSelected())));
        map.put("hasFromAndSupplyAir", new input<>("hasFromAndSupplyAir", "N/A", (hasFromAndSupplyAirCheckbox.isSelected())));
        map.put("lessThan3LayersOfWindows", new input<>("lessThan3LayersOfWindows", "N/A", (lessThan3LayersOfWindowsCheckbox.isSelected())));
        map.put("isPreservationOrdered", new input<>("isPreservationOrdered", "N/A", (isPreservationOrderedCheckbox.isSelected())));
        map.put("notFulfillingIMDWarmWaterEnergyRequierment", new input<>("notFulfillingIMDWarmWaterEnergyRequierment", "N/A", (notFulfillingIMDWarmWaterEnergyRequiermentCheckbox.isSelected())));
        map.put("newlyBuilt", new input<>("newlyBuilt", "N/A", (newlyBuiltCheckbox.isSelected())));
        map.put("replumbingNeeded", new input<>("replumbingNeeded", "N/A", (replumbingNeededCheckbox.isSelected())));

        return map;
    }

}