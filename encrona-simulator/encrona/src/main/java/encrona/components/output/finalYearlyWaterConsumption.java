package encrona.components.output;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import encrona.components.componentAbstract;
import encrona.modifiers.modifierAbstract;
import encrona.domain.improvement;

public class finalYearlyWaterConsumption extends componentAbstract<List<Map.Entry<Integer, Double>>> {
    /**
     * This is a constructor for fullOriginalElectricityConsumption
     * 
     * @param name      The name of this output
     * @param unit      The unit of this output
     * @param dependsOn the components this component depends on
     * @param modifiers the modifiers which should be applied to this component
     */
    public finalYearlyWaterConsumption(String name, String unit, Map<String, componentAbstract> dependsOn,
            List<modifierAbstract<List<Map.Entry<Integer, Double>>>> modifiers) {
        this.setName(name);
        this.setUnit(unit);
        this.setDependsOn(dependsOn);
        this.setModifiers(modifiers);
    }

    @Override
    public void calculate() throws Exception {
        Map<String, componentAbstract> dependsOnMap = getDependsOn();

        Double baseValue = (Double) dependsOnMap.get("Water consumption").getValue();

        List<Map.Entry<improvement, Map<String, Double>>> improvementImpacts = (List<Map.Entry<improvement, Map<String, Double>>>) dependsOnMap.get("improvementImpact").getValue();

        // Note that we use Map.Entry<Integer,Double> to represent years of service and yearly consumption
        List<Map.Entry<Integer, Double>> waterConsumptionList = new ArrayList<Map.Entry<Integer, Double>>();

        //We check if there are any improvements affecting water, if so we calculate the impact of improvements in ranges in the format <year this range ends,impact value>
        //We always re-use the original values with <-1,original value> (So that we store the orignal values for transfer to the simulator)
        if (improvementImpacts.size() > 0) {

            // This creates a set of the unique years of service, aka the unique values we need to find water for
            Set<Integer> uniqueYearsOfService = new HashSet<Integer>();

            for (Entry<improvement,Map<String,Double>> entryForImprovementImpacts : improvementImpacts) {
                if(entryForImprovementImpacts.getKey().getM3PerM2Water()>0.0)
                {uniqueYearsOfService.add(entryForImprovementImpacts.getKey().getYearsOfService());
            }

            int yearsOfService[] = new int[uniqueYearsOfService.size()];
            Set<Double> improvementImpactList = new LinkedHashSet<Double>();

            for (int i = 0; i < yearsOfService.length; i++) {

                Integer min = 0;
                Integer currentMin = Integer.MAX_VALUE;
                Double improvementImpact = 0.0;
                if (i > 0) {
                    min = yearsOfService[i - 1];
                }

                for (Entry<improvement,Map<String,Double>> entry : improvementImpacts) {
                    if (entry.getKey().getYearsOfService() > min) {
                        if (entry.getKey().getYearsOfService() < currentMin) {
                            currentMin = entry.getKey().getYearsOfService();
                        }
                        improvementImpact += (entry.getValue().get("water"));
                    }
                }
                yearsOfService[i] = currentMin;
                improvementImpactList.add(improvementImpact);
            }

            int i = 0;
            for (Double impact : improvementImpactList) {
                // https://docs.oracle.com/javase/8/docs/api/java/util/Map.Entry.html
                Entry<Integer, Double> entry = new AbstractMap.SimpleEntry<Integer, Double>(yearsOfService[i],
                        baseValue - impact);

                waterConsumptionList.add(entry);
                i++;
            }
        }
        Entry<Integer, Double> entry = new AbstractMap.SimpleEntry<Integer, Double>(-1,baseValue);
        waterConsumptionList.add(entry);

        this.setValue(waterConsumptionList);

    }

}}
