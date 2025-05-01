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

        // Note that we use Map.Entry<Double,Double> to represent a pair of doubles, in
        // this case years of service and yearly consumption
        List<Map.Entry<Integer, Double>> waterConsumptionList = new ArrayList<Map.Entry<Integer, Double>>();

        // We check if there are any improvements affecting electricity, if so we
        // calculate the impact of improvements in ranges in the format <year this range
        // ends,impact value>
        // We re-use the original values with <-1,original value>
        Entry<Integer, Double> alwaysIncludedEntry = new AbstractMap.SimpleEntry<Integer, Double>(-1, baseValue);
        waterConsumptionList.add(alwaysIncludedEntry);

        int i = 0;
        for (Map.Entry<improvement, Map<String, Double>> impact : improvementImpacts) {
            // https://docs.oracle.com/javase/8/docs/api/java/util/Map.Entry.html
            Entry<Integer, Double> entry = new AbstractMap.SimpleEntry<Integer, Double>(
                    impact.getKey().getYearsOfService(),
                    Double.min(baseValue - impact.getValue().get("water"), 0.0));
            waterConsumptionList.add(entry);
            i++;
        }

        this.setValue(waterConsumptionList);

    }

}
