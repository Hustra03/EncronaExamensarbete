package encrona.components.output;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import encrona.components.componentAbstract;
import encrona.domain.heatingEnergySource;
import encrona.modifiers.modifierAbstract;

public class finalYearlyBuildingHeatingSavings extends componentAbstract<List<Map.Entry<Integer, List<Map.Entry<String, Double>>>>> {
    
    /** 
     * This is a constructor for finalYearlyBuildingHeatingSavings
     * 
     * @param name      The name of this output
     * @param unit      The unit of this output
     * @param dependsOn the components this component depends on
     * @param modifiers the modifiers which should be applied to this component
     */
    public finalYearlyBuildingHeatingSavings(String name, String unit, Map<String, componentAbstract> dependsOn,
            List<modifierAbstract<List<Map.Entry<Integer, List<Map.Entry<String, Double>>>>>> modifiers) {
        this.setName(name);
        this.setUnit(unit);
        this.setDependsOn(dependsOn);
        this.setModifiers(modifiers);
    }

    @Override
    /**
     * This is the calculate method for finalYearlyBuildingHeatingSavings
     */
    public void calculate() throws Exception {

        Map<String, componentAbstract> dependsOnMap = this.getDependsOn();

        List<heatingEnergySource> baseValues = (List<heatingEnergySource>)dependsOnMap.get("heatingSources").getValue();

        List<Map.Entry<Integer, List<heatingEnergySource>>> heatingConsumptionList = (List<Map.Entry<Integer, List<heatingEnergySource>>>) dependsOnMap.get("heatingOutput").getValue();

        List<Map.Entry<Integer, List<Map.Entry<String, Double>>>> finalSavings = new ArrayList<Map.Entry<Integer, List<Map.Entry<String, Double>>>>();

        Map<String,heatingEnergySource> originalSourceMap=new HashMap<String,heatingEnergySource>();

        for (heatingEnergySource source : baseValues) {
            originalSourceMap.put(source.getName(), source);
        }

        for (Map.Entry<Integer, List<heatingEnergySource>> e : heatingConsumptionList) {

            List<Map.Entry<String, Double>> sourceSavings = new ArrayList<Map.Entry<String, Double>>();

            for (heatingEnergySource sources : e.getValue()) {

                Double savingsGenerated =  (originalSourceMap.get(sources.getName()).getKwhPerYearHeating()-sources.getKwhPerYearHeating()) * sources.getCostPerKwh();

                Entry<String, Double> listEntryToSave = new AbstractMap.SimpleEntry<String, Double>(sources.getName(),savingsGenerated);
                sourceSavings.add(listEntryToSave);
            }

            Entry<Integer, List<Map.Entry<String, Double>>> listEntryToSave = new AbstractMap.SimpleEntry<Integer, List<Map.Entry<String, Double>>>((Integer) e.getKey(),sourceSavings);
            finalSavings.add(listEntryToSave);
        }

        this.setValue(finalSavings);
    }
}
