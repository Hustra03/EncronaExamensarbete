package encrona.components.output;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import encrona.components.componentAbstract;
import encrona.modifiers.modifierAbstract;

public class finalElectricityConsumptionChange extends componentAbstract<Integer>{


    /**
     * This is a constructor for finalElectricityConsumptionChange 
     * @param name The name of this output
     * @param unit The unit of this output
     * @param dependsOn the components this component depends on
     * @param modifiers the modifiers which should be applied to this component
     */
    finalElectricityConsumptionChange(String name, String unit, Map<String,? extends componentAbstract> dependsOn, List<? extends modifierAbstract<Integer>> modifiers)
    {   this.setName(name);
        this.setUnit(unit);
        this.setDependsOn(dependsOn);
        this.setModifiers(modifiers);
    }

    @Override
    /**
     * This method implements the calculate functionality for finalElectricityConsumptionChange
     */
    public void calculate() throws Exception {

        Map<String,? extends componentAbstract> dependsOn = getDependsOn();

        Integer baseValue = (Integer)dependsOn.get("electricityConsumption").getValue();

        this.setValue(baseValue);

        this.applyModifiers();
        this.complete();
    }
    
}
