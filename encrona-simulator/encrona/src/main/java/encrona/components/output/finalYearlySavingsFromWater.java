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


public class finalYearlySavingsFromWater extends componentAbstract<List<Map.Entry<Integer,Double>>>{

    /**
     * This is a constructor for finalYearlySavingsFromWater 
     * @param name The name of this output
     * @param unit The unit of this output
     * @param dependsOn the components this component depends on
     * @param modifiers the modifiers which should be applied to this component
     */
    public finalYearlySavingsFromWater(String name, String unit, Map<String,componentAbstract> dependsOn, List<modifierAbstract<List<Map.Entry<Integer,Double>>>> modifiers)
    {   this.setName(name);
        this.setUnit(unit);
        this.setDependsOn(dependsOn);
        this.setModifiers(modifiers);
    }

    @Override
    public void calculate() throws Exception {
        Map<String,componentAbstract> dependsOnMap = this.getDependsOn();

        Double waterPrice = (Double)dependsOnMap.get("Water price").getValue();
        List<Map.Entry<Integer,Double>> waterConsumptionList = (List<Map.Entry<Integer,Double>>)dependsOnMap.get("waterConsumption").getValue();

        List<Map.Entry<Integer,Double>> finalSavings=new ArrayList<Map.Entry<Integer,Double>>();

        for (Map.Entry e : waterConsumptionList) {
            if ((Integer) e.getKey() != -1) {
            Double savedWaterM3=waterConsumptionList.get(waterConsumptionList.size() - 1).getValue()-(Double)e.getValue();
            Entry<Integer,Double> entry = new AbstractMap.SimpleEntry<Integer, Double>((Integer)e.getKey(), savedWaterM3*waterPrice);
            finalSavings.add(entry);
            }
        }

        this.setValue(finalSavings);
    }
    
}
