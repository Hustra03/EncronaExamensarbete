package encrona.components.output;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import encrona.components.componentAbstract;
import encrona.domain.heatingEnergySource;
import encrona.domain.improvement;

public class finalYearlySavingsFromElectricity extends componentAbstract<List<Map.Entry<Integer, Double>>> {

    /**
     * This is a constructor for finalYearlySavingsFromElectricity
     * 
     * @param name      The name of this output
     * @param unit      The unit of this output
     * @param dependsOn the components this component depends on
     */
    public finalYearlySavingsFromElectricity(String name, String unit, Map<String, componentAbstract> dependsOn) {
        this.setName(name);
        this.setUnit(unit);
        this.setDependsOn(dependsOn);
    }

    @Override
    public void calculate() throws Exception {
        Map<String, componentAbstract> dependsOnMap = this.getDependsOn();

        Double electricityPrice = (Double) dependsOnMap.get("Electricty price").getValue();
        List<Map.Entry<Integer, Double>> electricityConsumptionList = (List<Map.Entry<Integer, Double>>) dependsOnMap.get("electricityOutput").getValue();

        List<Map.Entry<Integer, Double>> finalSavings = new ArrayList<Map.Entry<Integer, Double>>();

        for (Map.Entry e : electricityConsumptionList) {
            if ((Integer) e.getKey() != -1) {
                Entry<Integer, Double> entry = new AbstractMap.SimpleEntry<Integer, Double>((Integer) e.getKey(), (electricityConsumptionList.get(electricityConsumptionList.size() -1).getValue() - (Double) e.getValue()) * electricityPrice);
                finalSavings.add(entry);
            }
        }

        this.setValue(finalSavings);
    }

}
