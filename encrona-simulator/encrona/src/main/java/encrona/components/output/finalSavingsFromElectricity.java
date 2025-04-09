package encrona.components.output;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import encrona.components.componentAbstract;
import encrona.domain.improvement;
import encrona.modifiers.modifierAbstract;

public class finalSavingsFromElectricity extends componentAbstract<List<Double>>{

    /**
     * This is a constructor for finalElectricityConsumptionChange 
     * @param name The name of this output
     * @param unit The unit of this output
     * @param dependsOn the components this component depends on
     * @param modifiers the modifiers which should be applied to this component
     */
    public finalSavingsFromElectricity(String name, String unit, Map<String,componentAbstract> dependsOn, List<modifierAbstract<List<Double>>> modifiers)
    {   this.setName(name);
        this.setUnit(unit);
        this.setDependsOn(dependsOn);
        this.setModifiers(modifiers);
    }

    @Override
    public void calculate() throws Exception {
        Map<String,componentAbstract> dependsOnMap = this.getDependsOn();

        Double baseValue = (Double)dependsOnMap.get("electricityConsumptionInput").getValue();
        Double electricityPrice = (Double)dependsOnMap.get("electricityPrice").getValue();
        List<Double> improvements=(List<Double>)dependsOnMap.get("electricityOutput").getValue();

        List<Double> finalSavings=new ArrayList<>();

        for (Double double1 : improvements) {
            finalSavings.add((baseValue-double1)*electricityPrice);
        }

        this.setValue(finalSavings);
    }
    
}
