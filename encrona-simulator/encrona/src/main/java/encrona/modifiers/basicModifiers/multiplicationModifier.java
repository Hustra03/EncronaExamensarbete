package encrona.modifiers.basicModifiers;

import encrona.domain.systems.heatingSystem;
import encrona.modifiers.modifierAbstract;

public class multiplicationModifier extends modifierAbstract<Double>{

    private Double multiplyBy;
    /**
     * This is the constructor for changeHeatingSystem 
     * @param name The name for this improvement
     * @param description A description of what the multiplication represents
     * @param multiplyBy The value to multiply by
     */
    public multiplicationModifier(String name, String description,Double multiplyBy)
    {   
        setName(name);
        setDescription(description);
        this.multiplyBy=multiplyBy;
    }

    /**
     * This is the modify implementation for multiplication
     * @param value The value to multiply
     * @return The value multiplied by multiplyBy
     * @throws Exception Any exception which occurs
     */
    @Override
    public Double modify(Double value) throws Exception {
        return value*multiplyBy;
    }
    
}
