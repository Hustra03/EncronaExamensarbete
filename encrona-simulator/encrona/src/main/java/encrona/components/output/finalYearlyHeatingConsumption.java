package encrona.components.output;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import encrona.components.componentAbstract;
import encrona.domain.heatingEnergySource;
import encrona.domain.improvement;

public class finalYearlyHeatingConsumption extends componentAbstract<List<Map.Entry<Integer, List<heatingEnergySource>>>> {
    /**
     * This is a constructor for finalElectricityConsumptionChange
     * 
     * @param name      The name of this output
     * @param unit      The unit of this output
     * @param dependsOn the components this component depends on
     */
    public finalYearlyHeatingConsumption(String name, String unit, Map<String, componentAbstract> dependsOn) {
        this.setName(name);
        this.setUnit(unit);
        this.setDependsOn(dependsOn);
    }

    @Override
    /**
     * This method implements the calculate functionality for
     * finalElectricityConsumptionChange
     * TODO confirm the calculation with Laszlo
     */
    public void calculate() throws Exception {

        Map<String, componentAbstract> dependsOnMap = getDependsOn();

        List<heatingEnergySource> baseValues = (List<heatingEnergySource>) dependsOnMap.get("heatingSources").getValue();
        // We sort heat sources based on their kwh price, since we want to minimize
        // costs
        baseValues.sort((heatingEnergySource h1, heatingEnergySource h2) -> Double.compare(h2.getCostPerKwh(), h1.getCostPerKwh()));

        List<Map.Entry<improvement,Map<String,Double>>> improvementImpacts = (List<Map.Entry<improvement,Map<String,Double>>>) dependsOnMap.get("improvementImpact").getValue();


        // Note that we use Map.Entry<Integer,List<heatingEnergySource>> to represent a ranges and heat sources
        List<Map.Entry<Integer, List<heatingEnergySource>>> heatingConsumptionList = new ArrayList<Map.Entry<Integer, List<heatingEnergySource>>>();

        // We check if there are any improvements affecting heating, if so we calculate
        // the impact of improvements in ranges in the format <year this range ends,heat
        // source list after impact is distributed>
        // We also re-use the original values with <-1,heat source list>, for the dashboard
        if (improvementImpacts.size() > 0) {

        // This creates a set of the unique years of service, aka the unique values we
        // need to find electricity for
        Set<Integer> uniqueYearsOfService = new HashSet<Integer>();

        for (Map.Entry<improvement,Map<String,Double>> entry : improvementImpacts) {
            uniqueYearsOfService.add(entry.getKey().getYearsOfService());
        }
        int yearsOfService[] = new int[uniqueYearsOfService.size()];

            for (int i = 0; i < yearsOfService.length; i++) {

                Integer min = 0;
                Integer currentMin = Integer.MAX_VALUE;
                List<Entry<improvement, Map<String,Double>>> improvementsStillActive = new ArrayList<Entry<improvement, Map<String,Double>>>();
                if (i > 0) {
                    min = yearsOfService[i - 1];
                }

                for (Map.Entry<improvement,Map<String,Double>> entry : improvementImpacts) {
                    if (entry.getKey().getYearsOfService() > min) {
                        if (entry.getKey().getYearsOfService() < currentMin) {
                            currentMin = entry.getKey().getYearsOfService();
                        }
                        improvementsStillActive.add(new AbstractMap.SimpleEntry<improvement,Map<String,Double>>(entry.getKey(),entry.getValue()));
                    }
                }
                yearsOfService[i] = currentMin;
                List<heatingEnergySource> updatedHeatingSources = distributeImpact(baseValues, improvementsStillActive);
                Entry<Integer, List<heatingEnergySource>> entry = new AbstractMap.SimpleEntry<Integer, List<heatingEnergySource>>(
                        yearsOfService[i], updatedHeatingSources);
                heatingConsumptionList.add(entry);
            }
        }
        Entry<Integer, List<heatingEnergySource>> entry = new AbstractMap.SimpleEntry<Integer, List<heatingEnergySource>>(
            -1, baseValues);
    heatingConsumptionList.add(entry);
        this.setValue(heatingConsumptionList);
    }

    /**
     * This function will distribute the impact from the measure between the
     * different sources of heat
     * TODO potentially update this to only modify certain sources for certain
     * improvements(?)
     * 
     * @return A modifier list of heating sources, with the impact distributed
     *         between them
     */
    private List<heatingEnergySource> distributeImpact(List<heatingEnergySource> heatSources,
    List<Entry<improvement, Map<String,Double>>> improvementsStillActive) {

        // We first combine the impact of the relevant improvements, to get the total
        // reduction for building and water heating
        Double sumOfHeatingNeedReduction = 0.0;        
        Double sumOfWaterHeatingNeedReduction = 0.0;

        for (Entry<improvement, Map<String,Double>> entry : improvementsStillActive) {
            sumOfHeatingNeedReduction += entry.getValue().get("buildingHeating");
            sumOfWaterHeatingNeedReduction += entry.getValue().get("waterHeating");
        }

        // We then create a copy of heatSources, which we can modify to represent this
        // specific year ranges values
        List<heatingEnergySource> copyOfHeatSources = new ArrayList<heatingEnergySource>();
        for (heatingEnergySource heatingEnergySource : heatSources) {
            copyOfHeatSources.add(new heatingEnergySource(heatingEnergySource));
        }

        int index = 0;
        // We then iterate over the above list, and remove building heating by
        // sumOfHeatingNeedReduction until it is fully used, or reduced each to 0 if the impact is greater than the sum of energy currently used
        while (sumOfHeatingNeedReduction > 0.0 && index<copyOfHeatSources.size()) {

            heatingEnergySource source = copyOfHeatSources.get(index);

            Double reduceBy = Double.min(sumOfHeatingNeedReduction, source.getKwhPerYearHeating());
            source.setKwhPerYearHeating(source.getKwhPerYearHeating() - reduceBy);
            sumOfHeatingNeedReduction -= reduceBy;
            index += 1;
        }

        //We then do the same for sumOfWaterHeatingNeedReduction
        index = 0;
        while (sumOfWaterHeatingNeedReduction > 0.0 && index<copyOfHeatSources.size()) {
            heatingEnergySource source = copyOfHeatSources.get(index);
            Double reduceBy = Double.min(sumOfWaterHeatingNeedReduction, source.getKwhPerYearHeatingWater());
            source.setKwhPerYearHeatingWater(source.getKwhPerYearHeatingWater() - reduceBy);
            sumOfWaterHeatingNeedReduction -= reduceBy;
            index += 1;
        }       

        return copyOfHeatSources;

    }

}
