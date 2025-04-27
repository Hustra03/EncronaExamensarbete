package encrona.components.intermediate;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import encrona.domain.heatingEnergySource;
import encrona.components.componentAbstract;
import encrona.modifiers.modifierAbstract;

/**
 * This intermediate value is used to create a single value for the current electricity consumption
 */
public class fullOriginalElectricityConsumption extends componentAbstract<Double>{


    /**
     * This is a constructor for fullOriginalElectricityConsumption
     * 
     * @param name      The name of this output
     * @param unit      The unit of this output
     * @param dependsOn the components this component depends on
     * @param modifiers the modifiers which should be applied to this component
     */
    public fullOriginalElectricityConsumption(String name, String unit, Map<String, componentAbstract> dependsOn,
            List<modifierAbstract<Double>> modifiers) {
        this.setName(name);
        this.setUnit(unit);
        this.setDependsOn(dependsOn);
        this.setModifiers(modifiers);
    }
    @Override
    public void calculate() throws Exception {
        Map<String, componentAbstract> dependsOnMap = getDependsOn();


        Double baseValue = (Double) dependsOnMap.get("Electricty consumption").getValue();
        List<heatingEnergySource> heatSources = (List<heatingEnergySource>) dependsOnMap.get("heatingSources").getValue();

        // We add the electricity from heating sources
        for (heatingEnergySource heatingEnergySource : heatSources) {
            baseValue += heatingEnergySource.getKwhPerYearInElectricity();
        }

        setValue(baseValue);
    }
    
}
