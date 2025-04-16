package encrona.components;

import java.util.Map;

/**
 * This class represents inputs to the system
 */
public class input<T> extends componentAbstract<T>{
    
    /**
     * This is a constructor for originalElectricityConsumption 
     * @param currentElectricityValue The current value
     */
    public input(String name, String unit, T value)
    {   this.setName(name);
        this.setUnit(unit);
        this.setValue(value);
    }

    /**
     * This is the calculate method for input, it applies any modifiers and then completes the input
     * @throws Exception if something goes wrong
     */
    @Override
    public void calculate() throws Exception {
        this.applyModifiers();
        this.complete();
    }



    @Override
    public Map<String, componentAbstract> getDependsOn() {
        return null;
    }
}
