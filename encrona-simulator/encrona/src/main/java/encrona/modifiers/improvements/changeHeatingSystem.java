package encrona.modifiers.improvements;

import encrona.domain.systems.heatingSystem;
import encrona.modifiers.modifierAbstract;

/**
 * This is an improvement which represents changing the current heating system
 */
public class changeHeatingSystem extends modifierAbstract<heatingSystem>{

    private heatingSystem newHeatingSystem;

    /**
     * This is the constructor for changeHeatingSystem 
     * @param name The name for this improvement
     * @param description A description of how the heating system is changed
     * @param newHeatingSystem The heating system to change to
     */
    public changeHeatingSystem(String name, String description,heatingSystem newHeatingSystem)
    {   
        setName(name);
        setDescription(description);
        this.newHeatingSystem=newHeatingSystem;
    }


    @Override
    /**
     * This is the modify method for the changeHeatingSystem method
     * @param value The current heating system
     * @throws Exception If the current and new system are the same
     * @return The new heating system
     */
    public heatingSystem modify(heatingSystem value) throws Exception {

        if (value.getName().equals(newHeatingSystem.getName())) {
            throw new Exception("");
        }

        return newHeatingSystem;
    }
    
}
