package encrona;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import encrona.components.componentAbstract;
import encrona.components.input;
import encrona.components.output.finalYearlyElectricityConsumption;
import encrona.components.output.finalYearlySavingsFromElectricity;
import encrona.modifiers.modifierAbstract;
import encrona.modifiers.basicModifiers.multiplicationModifier;
import encrona.domain.heatingEnergySource;
import encrona.domain.improvement;
import encrona.domain.improvementImpactEnum;
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
        createObjects();
        createModifiers();        
        createComponents();
    }

    /**
     * This creates the components objects, and adds them to the map
     */
    public void createComponents() {
        //We first define the simple value inputs which are read from input and used for creating the output
        //TODO make the inputs read values from user input
        input<Double> electricityInput = new input<Double>("electricityConsumptionInput", "kwh", 5703.0);
        input<Integer> aTempInput = new input<Integer>("aTempInput", "m^2", 1074);
        input<Double> rentValueInput = new input<Double>("yearlyRent", "%", 2.0);
        input<Double> varianceInput = new input<Double>("variance", "%", 2.0);
        input<Double> electrictyPriceInput=new input<Double>("electricityPrice","kr/kwh",1.1);


        //We then add the improvements which were selected
        //TODO only add the improvements which are a part of input

        List<improvement> improvement = new ArrayList<improvement>();
        improvement.add((improvement)objects.get("EfficentLighting"));
        input<List<improvement>> improvementsToImplement= new input<List<improvement>>("improvements", "N/A",improvement); 


        components.put(improvementsToImplement.getName(), improvementsToImplement);

        //We then add the different sources of heating energy
        //TODO change to creating energy sources based on their input values
        List<heatingEnergySource> heatingEnergySources= new ArrayList<heatingEnergySource>();
        heatingEnergySources.add((heatingEnergySource)objects.get("districtHeating"));
        input<List<heatingEnergySource>> heatingSourcesInput= new input<List<heatingEnergySource>>("heatingSources", "N/A",heatingEnergySources); 

        //TODO define intermediate values here, perhaps use a function to create unique ones for all of the heating sources
        //We then define the intermediate values


        //We then define the different outputs
        
        Map<String, componentAbstract> electricityOutputDependsOn = new HashMap<String, componentAbstract>();
        electricityOutputDependsOn.put(electricityInput.getName(), electricityInput);
        electricityOutputDependsOn.put(improvementsToImplement.getName(), improvementsToImplement);
        electricityOutputDependsOn.put(aTempInput.getName(), aTempInput);
        finalYearlyElectricityConsumption electricityOutput = new finalYearlyElectricityConsumption("electricityOutput", "kwh", electricityOutputDependsOn,new ArrayList<modifierAbstract<List<Entry<Integer, Double>>>>());

        Map<String, componentAbstract> electricitySavingsDependsOn = new HashMap<String, componentAbstract>();
        electricitySavingsDependsOn.put(electricityInput.getName(), electricityInput);
        electricitySavingsDependsOn.put(electricityOutput.getName(), electricityOutput);
        electricitySavingsDependsOn.put(electrictyPriceInput.getName(), electrictyPriceInput);
        finalYearlySavingsFromElectricity electricitySavings = new finalYearlySavingsFromElectricity("electricitySavings", "kr", electricitySavingsDependsOn,new ArrayList<modifierAbstract<List<Entry<Integer, Double>>>>());

        //We then finally add all of the defined outputs to components
        components.put(electricityOutput.getName(), electricityOutput);
        components.put(electricitySavings.getName(), electricitySavings);
    }

    /**
     * This creates the domain objects, and adds them to the map
     */
    public void createObjects() {
        //We first create the representation for those improvements which are treated in a "binary" manner, and which are applied, and then add them to the data loader for objects
        improvement BergOrMarkHeating = new improvement("BergOrMarkHeating", 940.0, 570.0, 15, improvementImpactEnum.BuildingHeating);
        improvement FTX = new improvement("FTX", 860.0, 620.0, 15, improvementImpactEnum.BuildingHeating);
        improvement ChangeWindows=new improvement("ChangeWindows", 770.0, 445.0, 40, improvementImpactEnum.BuildingHeating);
        improvement InsulateFacade=new improvement("InsulateFacade", 620.0, 630.0, 40, improvementImpactEnum.BuildingHeating);
        improvement FVP = new improvement("FVP", 600.0, 290.0, 20, improvementImpactEnum.BuildingHeating);
        improvement InsulateAttic = new improvement("InsulateAttic", 330.0, 100.0, 40, improvementImpactEnum.BuildingHeating);
        improvement SolarPanels = new improvement("SolarPanels", 220.0, 210.0, 15, improvementImpactEnum.Electricity);
        improvement IMDWater = new improvement("IMDWater", 140.0, 85.0, 10, improvementImpactEnum.Water);
        improvement ControlSystem = new improvement("ControlSystem", 100.0, 45.0, 10, improvementImpactEnum.BuildingHeating);
        improvement ThermometerReconfiguration = new improvement("ThermometerReconfiguration", 90.0, 50.0, 10, improvementImpactEnum.BuildingHeating);
        improvement EconomicalFlush = new improvement("EconomicalFlush", 100.0, 45.0, 15, improvementImpactEnum.Water);
        improvement EfficentLighting = new improvement("EfficentLighting", 30.0, 15.0, 15, improvementImpactEnum.Electricity);
    
        objects.put(BergOrMarkHeating.getName(), BergOrMarkHeating);
        objects.put(FTX.getName(), FTX);
        objects.put(ChangeWindows.getName(), ChangeWindows);
        objects.put(InsulateFacade.getName(), InsulateFacade);
        objects.put(FVP.getName(), FVP);
        objects.put(InsulateAttic.getName(), InsulateAttic);
        objects.put(SolarPanels.getName(), SolarPanels);
        objects.put(IMDWater.getName(), IMDWater);
        objects.put(ControlSystem.getName(), ControlSystem);
        objects.put(ThermometerReconfiguration.getName(), ThermometerReconfiguration);
        objects.put(EconomicalFlush.getName(), EconomicalFlush);
        objects.put(EfficentLighting.getName(), EfficentLighting);

        //We then add the relevant heating sources (currently just district heating)
        //TODO get realistic cost for district heating
        heatingEnergySource districtHeating = new heatingEnergySource("districtHeating", 174812.0, 26850.0,0.0,1.25);
        objects.put(districtHeating.getName(), districtHeating);
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
