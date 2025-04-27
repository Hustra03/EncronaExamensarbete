package encrona.components.output;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import encrona.domain.heatingEnergySource;
import encrona.components.componentAbstract;
import encrona.modifiers.modifierAbstract;
import encrona.domain.improvement;
import encrona.domain.improvementImpactEnum;

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

        List<improvement> improvements = (List<improvement>) dependsOnMap.get("improvements").getValue();
        // Here we create a shallow copy of improvementImpacts, so the list is cloned
        // but the objects are the same instances as in the origninal
        improvements = (List<improvement>) ((ArrayList) improvements).clone();
        // https://www.w3schools.com/java/java_lambda.asp
        // This removes all improvements which do not impact Water
        improvements.removeIf((improvement) -> {
            return !(improvement.getImpactType().equals(improvementImpactEnum.Water));
        });

        // Note that we use Map.Entry<Double,Double> to represent a pair of doubles, in
        // this case years of service and yearly consumption
        List<Map.Entry<Integer, Double>> waterConsumptionList = new ArrayList<Map.Entry<Integer, Double>>();

        // We check if there are any improvements affecting electricity, if so we
        // calculate the impact of improvements in ranges in the format <year this range
        // ends,impact value>
        // We re-use the original values with <-1,original value>
        Entry<Integer, Double> alwaysIncludedEntry = new AbstractMap.SimpleEntry<Integer, Double>(-1, baseValue);
        waterConsumptionList.add(alwaysIncludedEntry);
        if (improvements.size() > 0) {

            // This creates a set of the unique years of service, aka the unique values we
            // need to find electricity for
            Set<Integer> uniqueYearsOfService = new HashSet<Integer>();

            for (improvement entry : improvements) {
                uniqueYearsOfService.add(entry.getYearsOfService());
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

                for (improvement entry : improvements) {
                    if (entry.getYearsOfService() > min) {
                        if (entry.getYearsOfService() < currentMin) {
                            currentMin = entry.getYearsOfService();
                        }
                        improvementImpact += (1); // TODO add improvement impact for water here, if that is implemented
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
        this.setValue(waterConsumptionList);

    }

}
