package encrona.components.intermediate;
import java.util.List;
import java.util.Map;

import encrona.domain.heatingEnergySource;
import encrona.components.componentAbstract;

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
     */
    public fullOriginalElectricityConsumption(String name, String unit, Map<String, componentAbstract<?>> dependsOn) {
        this.setName(name);
        this.setUnit(unit);
        this.setDependsOn(dependsOn);
    }
    @Override
    public void calculate() throws Exception {
        Map<String, componentAbstract<?>> dependsOnMap = getDependsOn();


        Double baseValue = (Double) dependsOnMap.get("Electricty consumption").getValue();
        List<heatingEnergySource> heatSources = (List<heatingEnergySource>) dependsOnMap.get("heatingSources").getValue();

        // We add the electricity from heating sources
        for (heatingEnergySource heatingEnergySource : heatSources) {
            baseValue += heatingEnergySource.getKwhPerYearInElectricity();
        }

        setValue(baseValue);
    }
    
}
