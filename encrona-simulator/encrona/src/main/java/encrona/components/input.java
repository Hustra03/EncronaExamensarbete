package encrona.components;

public class input<T> extends componentAbstract<T>{
    
    /**
     * This is a constructor for originalElectricityConsumption 
     * @param currentElectricityValue The current value
     */
    input(String name, String unit, T value)
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
}
