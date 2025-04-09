package encrona.components;

import java.util.List;
import java.util.Map;

import encrona.modifiers.modifierAbstract;

/**
 * This abstract class represents a component, which is a part of the system
 * which is calculated or used to calculate something else.
 * The calculation is done by running the interface function calculate, with the
 * implementation depending on the component
 */
public abstract class componentAbstract<T> implements componentInterface<T>, Runnable {

    private String name;
    private T value;
    private Boolean complete = false; // By default complete is false
    private String unit;

    private Map<String, componentAbstract> dependsOn; // TODO test if this works, does give a warning but if
                                                      // parameterized then only those with the same value as the
                                                      // instance can be added

    // https://docs.oracle.com/javase/8/docs/api/java/util/Map.html
    private List<modifierAbstract<T>> modifiers; // This should be parameterized, since only modifiers which are for the
                                                 // same data type as value should be accepted

    /**
     * This is a getter for the name attribute
     * 
     * @return the instances name
     */
    public String getName() {
        return this.name;
    }

    /**
     * A setter for the name attribute
     * 
     * @param newName The new name
     */
    public void setName(String newName) {
        this.name = newName;
    }

    /**
     * This is a getter for the value attribute
     * 
     * @return the instances value value
     */
    public T getValue() {
        return this.value;
    }

    /**
     * A setter for the value attribute
     * 
     * @param newName The new value
     */
    public void setValue(T newValue) {
        this.value = newValue;
    }

    /**
     * This is a getter for the complete attribute
     * 
     * @return the instances complete value
     */
    public Boolean getComplete() {
        return this.complete;
    }

    /**
     * This sets the complete value to true
     */
    public void complete() {
        this.complete = true;
    }

    /**
     * This is a getter for the unit attribute
     * 
     * @return the instances unit
     */
    public String getUnit() {
        return this.unit;
    }

    /**
     * A setter for the unit attribute
     * 
     * @param newUnit The new unit
     */
    public void setUnit(String newUnit) {
        this.unit = newUnit;
    }

    /**
     * This is a getter for dependsOn
     * 
     * @return the instances dependsOn
     */
    public Map<String, componentAbstract> getDependsOn() {
        return dependsOn;
    }

    /**
     * This is a setter for dependsOn
     * 
     * @param newDependsOn the new dependsOn
     */
    public void setDependsOn(Map<String, componentAbstract> newDependsOn) {
        this.dependsOn = newDependsOn;
    }

    /**
     * This is a getter for modifiers
     * 
     * @return the instances modifiers
     */
    public List<modifierAbstract<T>> getModifiers() {
        return modifiers;
    }

    /**
     * This is a setter for modifiers
     * 
     * @param newModifiers the new modifiers
     */
    public void setModifiers(List<modifierAbstract<T>> newModifiers) {
        this.modifiers = newModifiers;
    }

    /**
     * This method applies each of the modifiers to the current value of the
     * component
     * 
     * @throws Exception If something goes wrong
     */
    public void applyModifiers() throws Exception {
        if (modifiers != null) {
            for (modifierAbstract<T> mod : modifiers) {
                //TODO below is for testing, remove before using in production
                System.out.println(" Modifier " + mod.getName() + " Applied to " + this.getName());
                setValue(mod.modify(value));
            }
        }
    }

    @Override
    /**
     * This transforms the method into a runnable, so that it can be run using a
     * thread pool
     */
    public void run() {
        try {
            this.calculate();
            this.applyModifiers();
            this.complete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
