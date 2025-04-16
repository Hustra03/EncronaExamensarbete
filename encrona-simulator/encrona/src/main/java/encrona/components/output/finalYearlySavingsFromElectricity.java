package encrona.components.output;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import encrona.components.componentAbstract;
import encrona.domain.heatingEnergySource;
import encrona.domain.improvement;
import encrona.modifiers.modifierAbstract;

public class finalYearlySavingsFromElectricity extends componentAbstract<List<Map.Entry<Integer,Double>>>{

    /**
     * This is a constructor for finalElectricityConsumptionChange 
     * @param name The name of this output
     * @param unit The unit of this output
     * @param dependsOn the components this component depends on
     * @param modifiers the modifiers which should be applied to this component
     */
    public finalYearlySavingsFromElectricity(String name, String unit, Map<String,componentAbstract> dependsOn, List<modifierAbstract<List<Map.Entry<Integer,Double>>>> modifiers)
    {   this.setName(name);
        this.setUnit(unit);
        this.setDependsOn(dependsOn);
        this.setModifiers(modifiers);
    }

    @Override
    public void calculate() throws Exception {
        Map<String,componentAbstract> dependsOnMap = this.getDependsOn();

        Double baseValue = (Double)dependsOnMap.get("Electricty consumption").getValue();
        Double electricityPrice = (Double)dependsOnMap.get("Electricty price").getValue();
        List<Map.Entry<Integer,Double>> electricityConsumptionList = (List<Map.Entry<Integer,Double>>)dependsOnMap.get("electricityOutput").getValue();
        List<heatingEnergySource> heatSources = (List<heatingEnergySource>)dependsOnMap.get("heatingSources").getValue();

        //We add the electricity from heating sources
        for (heatingEnergySource heatingEnergySource : heatSources) {
            baseValue+=heatingEnergySource.getKwhPerYearInElectricity();
        }
        List<Map.Entry<Integer,Double>> finalSavings=new ArrayList<Map.Entry<Integer,Double>>();

        for (Map.Entry e : electricityConsumptionList) {
            Entry<Integer,Double> entry = new AbstractMap.SimpleEntry<Integer, Double>((Integer)e.getKey(), (baseValue-(Double)e.getValue())*electricityPrice);
            finalSavings.add(entry);
        }

        this.setValue(finalSavings);
    }
    
}
