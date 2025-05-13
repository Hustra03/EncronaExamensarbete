package encrona.components;

import java.util.Map;

/**
 * This class represents inputs to the system
 */
public class input<T> extends componentAbstract<T>{
    
    /**
     * This is a constructor for inputs 
     * @param name The name of the input
     * @param unit The unit of the input
     * @param value The value of the input
     */
    public input(String name, String unit, T value)
    {   this.setName(name);
        this.setUnit(unit);
        this.setValue(value);
    }

    /**
     * This is the calculate method for input, it simply completes the input
     * @throws Exception if something goes wrong
     */
    @Override
    public void calculate() throws Exception {
        this.complete();
    }

    @Override
    public Map<String, componentAbstract> getDependsOn() {
        return null;
    }
}
