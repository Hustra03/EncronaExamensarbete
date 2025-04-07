package encrona;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import encrona.components.componentAbstract;
import encrona.components.input;
import encrona.components.output.finalElectricityConsumptionChange;
import encrona.modifiers.modifierAbstract;
import encrona.modifiers.basicModifiers.multiplicationModifier;
import encrona.domain.objectAbstract;

/**
 * This class is responsible for instantiating the components, domains and
 * modifiers
 */
public class DataLoader {

    private Map<String, componentAbstract> components;
    private Map<String, objectAbstract> objects;
    private Map<String, modifierAbstract> modifiers;

    /**
     * This instantiates the database loader
     */
    public DataLoader()
    {
        instantiate();
    }

    /**
     * This method is responsible for instantiating the relevant data
     * TODO add some parameter, ex output, to determine what to include, currently
     * loads an example
     */
    public void instantiate() {
        // We first instantiate the map
        // https://docs.oracle.com/javase/8/docs/api/java/util/HashMap.html
        components = new HashMap<String, componentAbstract>();
        objects = new HashMap<String, objectAbstract>();
        modifiers = new HashMap<String, modifierAbstract>();

        // We then fill the maps with relevant data
        createModifiers();
        createComponents();
        createObjects();
    }

    /**
     * This creates the components objects, and adds them to the map
     */
    public void createComponents() {
        input<Double> electricityInput = new input<Double>("electricityConsumptionInput", "kwh", 100.0);

        Map<String, componentAbstract> electricityOutputDependsOn = new HashMap<String, componentAbstract>();
        electricityOutputDependsOn.put(electricityInput.getName(), electricityInput);
        List<modifierAbstract<Double>> electricityOutputModifiers= new ArrayList<modifierAbstract<Double>>();
        electricityOutputModifiers.add(modifiers.get("multiplyBy3"));
        finalElectricityConsumptionChange electricityOutput = new finalElectricityConsumptionChange("electricityOutput", "kwh", electricityOutputDependsOn,electricityOutputModifiers );

        components.put(electricityInput.getName(), electricityInput);
        components.put(electricityOutput.getName(), electricityOutput);
    }

    /**
     * This creates the domain objects, and adds them to the map
     */
    public void createObjects() {
    }

    /**
     * This creates the modifier objects, and adds them to the map
     */
    public void createModifiers() {
        multiplicationModifier multiplicationModifier = new multiplicationModifier("multiplyBy3", "Multiply by 3", 3.0);
        modifiers.put(multiplicationModifier.getName(), multiplicationModifier);
    }

    /**
     * Retrives a componentAbstract from the database loader
     * @param key The key/name of the componentAbstract to retrive
     * @return The componentAbstract, or null if it does not exist
     */
    public componentAbstract getComponentAbstract(String key)
    {
        return components.get(key);
    }

        /**
     * Retrives a componentAbstract from the database loader
     * @return A collection which contains all of the 
     */
    public Collection<componentAbstract> getAllComponentAbstract()
    {
        return components.values();
    }
}
