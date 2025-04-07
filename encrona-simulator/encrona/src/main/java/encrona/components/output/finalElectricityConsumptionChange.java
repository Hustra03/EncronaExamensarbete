package encrona.components.output;

import java.util.List;
import java.util.Map;

import encrona.components.componentAbstract;
import encrona.modifiers.modifierAbstract;

public class finalElectricityConsumptionChange extends componentAbstract<Double>{


    /**
     * This is a constructor for finalElectricityConsumptionChange 
     * @param name The name of this output
     * @param unit The unit of this output
     * @param dependsOn the components this component depends on
     * @param modifiers the modifiers which should be applied to this component
     */
    public finalElectricityConsumptionChange(String name, String unit, Map<String,componentAbstract> dependsOn, List<modifierAbstract<Double>> modifiers)
    {   this.setName(name);
        this.setUnit(unit);
        this.setDependsOn(dependsOn);
        this.setModifiers(modifiers);
    }

    @Override
    /**
     * This method implements the calculate functionality for finalElectricityConsumptionChange
     * TODO update the calculation, is currently just input consumption + modifiers
     */
    public void calculate() throws Exception {

        Map<String,componentAbstract> dependsOn = getDependsOn();

        Double baseValue = (Double)dependsOn.get("electricityConsumptionInput").getValue();

        this.setValue(baseValue);

        this.applyModifiers();
        this.complete();
    }

}
