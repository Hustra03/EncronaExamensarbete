package encrona.components.output;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import encrona.components.componentAbstract;
import encrona.domain.heatingEnergySource;

public class finalYearlyBuildingHeatingSavings extends componentAbstract<List<Map.Entry<Integer, List<Map.Entry<String, Double>>>>> {
    
    /** 
     * This is a constructor for finalYearlyBuildingHeatingSavings
     * 
     * @param name      The name of this output
     * @param unit      The unit of this output
     * @param dependsOn the components this component depends on
     */
    public finalYearlyBuildingHeatingSavings(String name, String unit, Map<String, componentAbstract<?>> dependsOn) {
        this.setName(name);
        this.setUnit(unit);
        this.setDependsOn(dependsOn);
    }

    @Override
    /**
     * This is the calculate method for finalYearlyBuildingHeatingSavings
     */
    public void calculate() throws Exception {

        Map<String, componentAbstract<?>> dependsOnMap = this.getDependsOn();

        List<heatingEnergySource> baseValues = (List<heatingEnergySource>)dependsOnMap.get("heatingSources").getValue();

        List<Map.Entry<Integer, List<heatingEnergySource>>> heatingConsumptionList = (List<Map.Entry<Integer, List<heatingEnergySource>>>) dependsOnMap.get("heatingOutput").getValue();

        List<Map.Entry<Integer, List<Map.Entry<String, Double>>>> finalSavings = new ArrayList<>();

        Map<String,heatingEnergySource> originalSourceMap=new HashMap<>();

        for (heatingEnergySource source : baseValues) {
            originalSourceMap.put(source.getName(), source);
        }

        for (Map.Entry<Integer, List<heatingEnergySource>> e : heatingConsumptionList) {

            List<Map.Entry<String, Double>> sourceSavings = new ArrayList<>();

            for (heatingEnergySource sources : e.getValue()) {

                Double savingsGenerated =  (originalSourceMap.get(sources.getName()).getKwhPerYearHeating()-sources.getKwhPerYearHeating()) * sources.getCostPerKwh();

                Entry<String, Double> listEntryToSave = new AbstractMap.SimpleEntry<>(sources.getName(),savingsGenerated);
                sourceSavings.add(listEntryToSave);
            }

            Entry<Integer, List<Map.Entry<String, Double>>> listEntryToSave = new AbstractMap.SimpleEntry<>(e.getKey(),sourceSavings);
            finalSavings.add(listEntryToSave);
        }

        this.setValue(finalSavings);
    }
}
