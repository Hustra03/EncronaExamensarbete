package encrona;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import encrona.components.componentAbstract;
import encrona.components.input;
import encrona.components.intermediate.fullOriginalElectricityConsumption;
import encrona.modifiers.modifierAbstract;
import encrona.modifiers.basicModifiers.multiplicationModifier;
import encrona.domain.heatingEnergySource;
import encrona.domain.improvement;
import encrona.domain.improvementImpactEnum;
import encrona.components.output.*;

/**
 * This class is responsible for instantiating the components, domains and
 * modifiers
 */
public class DataLoader {

    private Map<String, componentAbstract> components;
    private Map<String, modifierAbstract> modifiers;

    /**
     * This instantiates the database loader
     * 
     * @param mapOfNumericalVariables A map of numerical variables, in the format
     *                                <<name,unit>,value>
     * @param improvement             The list of improvements to implement
     * @param heatingEnergySources    The list of heat sources
     */
    public DataLoader(Map<Map.Entry<String, String>, Double> mapOfNumericalVariables, List<improvement> improvement,
            List<heatingEnergySource> heatingEnergySources) {
        instantiate(mapOfNumericalVariables, improvement, heatingEnergySources);
    }

    /**
     * This method is responsible for instantiating the relevant data
     * 
     * @param listOfNumericalVariables A map of numerical variables, in the format
     *                                 <<name,unit>,value>
     * @param improvement              The list of improvements to implement
     * @param heatingEnergySources     The list of heat sources
     */
    public void instantiate(Map<Map.Entry<String, String>, Double> mapOfNumericalVariables,
            List<improvement> improvement, List<heatingEnergySource> heatingEnergySources) {
        // We first instantiate the map
        // https://docs.oracle.com/javase/8/docs/api/java/util/HashMap.html
        components = new HashMap<String, componentAbstract>();
        modifiers = new HashMap<String, modifierAbstract>();

        // We then fill the maps with relevant data
        createModifiers();
        createComponents(mapOfNumericalVariables, improvement, heatingEnergySources);
    }

    /**
     * This creates the components objects, and adds them to the map
     * 
     * @param listOfNumericalVariables A list of numerical variables, in the format
     *                                 <<name,unit>,value>
     * @param improvement              The list of improvements to implement
     * @param heatingEnergySources     The list of heat sources
     */
    public void createComponents(Map<Map.Entry<String, String>, Double> mapOfNumericalVariables,
            List<improvement> improvement, List<heatingEnergySource> heatingEnergySources) {
        // We first define the numerical inputs which are read from input and used for
        // creating the output

        input<Double> electricityInput = null;
        input<Double> aTempInput = null;
        input<Double> electrictyPriceInput = null;
        input<Double> waterConsumptionInput = null;
        input<Double> waterPriceInput = null;

        for (Entry<String, String> entry : mapOfNumericalVariables.keySet()) {
            switch (entry.getKey()) {
                case "Atemp":
                    aTempInput = new input<Double>("Atemp", entry.getValue(), mapOfNumericalVariables.get(entry));
                    break;
                case "Electricty consumption":
                    electricityInput = new input<Double>("Electricty consumption", entry.getValue(),
                            mapOfNumericalVariables.get(entry));
                    break;
                case "Electricty price":
                    electrictyPriceInput = new input<Double>("Electricty price", entry.getValue(),
                            mapOfNumericalVariables.get(entry));
                    break;
                case "Water consumption":
                    waterConsumptionInput = new input<Double>("Water consumption", entry.getValue(),
                            mapOfNumericalVariables.get(entry));
                    break;
                case "Water price":
                    waterPriceInput = new input<Double>("Water price", entry.getValue(),
                            mapOfNumericalVariables.get(entry));
                    break;
                default:
                    break;
            }

        }

        // We then add the improvements which were selected

        input<List<improvement>> improvementsToImplement = new input<List<improvement>>("improvements", "",
                improvement);

        components.put(improvementsToImplement.getName(), improvementsToImplement);

        // We then add the different sources of heating energy
        input<List<heatingEnergySource>> heatingSourcesInput = new input<List<heatingEnergySource>>("heatingSources",
                "", heatingEnergySources);

        // TODO define intermediate values here, perhaps use a function to create unique
        // ones for all of the heating sources
        // We then define the intermediate values

        Map<String, componentAbstract> waterConsumptionDependsOn = new HashMap<String, componentAbstract>();
        waterConsumptionDependsOn.put(waterConsumptionInput.getName(), waterConsumptionInput);
        waterConsumptionDependsOn.put(improvementsToImplement.getName(), improvementsToImplement);
        finalYearlyWaterConsumption waterConsumption = new finalYearlyWaterConsumption(
                "waterConsumption", "m^3", waterConsumptionDependsOn, null);

        Map<String, componentAbstract> originalElectricityConsumptionDependsOn = new HashMap<String, componentAbstract>();
        originalElectricityConsumptionDependsOn.put(electricityInput.getName(), electricityInput);
        originalElectricityConsumptionDependsOn.put(heatingSourcesInput.getName(), heatingSourcesInput);
        fullOriginalElectricityConsumption originalElectricityConsumption = new fullOriginalElectricityConsumption(
                "originalElectricityConsumption", "kwh", originalElectricityConsumptionDependsOn, null);

        // We then define the different outputs
        Map<String, componentAbstract> improvementImpactDependsOn = new HashMap<String, componentAbstract>();
        improvementImpactDependsOn.put(aTempInput.getName(), aTempInput);
        improvementImpactDependsOn.put(improvementsToImplement.getName(), improvementsToImplement);
        improvementImpact improvementImpact = new improvementImpact("improvementImpact", "kwh",
                improvementImpactDependsOn, null);

        // TODO add remaining types of cost for the different energy sources
        Map<String, componentAbstract> improvementReturnOnInvestementDependsOn = new HashMap<String, componentAbstract>();
        improvementReturnOnInvestementDependsOn.put(aTempInput.getName(), aTempInput);
        improvementReturnOnInvestementDependsOn.put(electrictyPriceInput.getName(), electrictyPriceInput);
        improvementReturnOnInvestementDependsOn.put(improvementImpact.getName(), improvementImpact);
        improvementReturnOnInvestementDependsOn.put(heatingSourcesInput.getName(), heatingSourcesInput);
        improvementReturnOnInvestement improvementReturnOnInvestement = new improvementReturnOnInvestement(
                "improvementReturnOnInvestement", "years", improvementReturnOnInvestementDependsOn, null);

        Map<String, componentAbstract> electricityOutputDependsOn = new HashMap<String, componentAbstract>();
        electricityOutputDependsOn.put(improvementImpact.getName(), improvementImpact);
        electricityOutputDependsOn.put(originalElectricityConsumption.getName(), originalElectricityConsumption);
        finalYearlyElectricityConsumption electricityOutput = new finalYearlyElectricityConsumption("electricityOutput",
                "kwh/year", electricityOutputDependsOn, null);

        Map<String, componentAbstract> electricitySavingsDependsOn = new HashMap<String, componentAbstract>();
        electricitySavingsDependsOn.put(electricityInput.getName(), electricityInput);
        electricitySavingsDependsOn.put(electricityOutput.getName(), electricityOutput);
        electricitySavingsDependsOn.put(electrictyPriceInput.getName(), electrictyPriceInput);
        electricitySavingsDependsOn.put(heatingSourcesInput.getName(), heatingSourcesInput);
        finalYearlySavingsFromElectricity electricitySavings = new finalYearlySavingsFromElectricity(
                "electricitySavings", "kr/year", electricitySavingsDependsOn, null);

        Map<String, componentAbstract> heatingOutputDependsOn = new HashMap<String, componentAbstract>();
        heatingOutputDependsOn.put(improvementImpact.getName(), improvementImpact);
        heatingOutputDependsOn.put(heatingSourcesInput.getName(), heatingSourcesInput);
        finalYearlyHeatingConsumption heatingOutput = new finalYearlyHeatingConsumption("heatingOutput",
                "kwh/year", heatingOutputDependsOn, null);

        Map<String, componentAbstract> heatingSavingsDependsOn = new HashMap<String, componentAbstract>();
        heatingSavingsDependsOn.put(heatingOutput.getName(), heatingOutput);
        heatingSavingsDependsOn.put(heatingSourcesInput.getName(), heatingSourcesInput);
        finalYearlyBuildingHeatingSavings heatingSavings = new finalYearlyBuildingHeatingSavings("heatingSavings",
                "kr/year", heatingSavingsDependsOn, null);

        Map<String, componentAbstract> waterHeatingSavingsDependsOn = new HashMap<String, componentAbstract>();
        waterHeatingSavingsDependsOn.put(heatingOutput.getName(), heatingOutput);
        waterHeatingSavingsDependsOn.put(heatingSourcesInput.getName(), heatingSourcesInput);
        finalYearlyWaterHeatingSavings waterHeatingSavings = new finalYearlyWaterHeatingSavings("waterHeatingSavings",
                "kr/year", waterHeatingSavingsDependsOn, null);

        // We then finally add all of the defined outputs to components
        components.put(waterConsumption.getName(), waterConsumption);
        components.put(improvementImpact.getName(), improvementImpact);
        components.put(improvementReturnOnInvestement.getName(), improvementReturnOnInvestement);
        components.put(electricityOutput.getName(), electricityOutput);
        components.put(electricitySavings.getName(), electricitySavings);
        components.put(heatingOutput.getName(), heatingOutput);
        components.put(heatingSavings.getName(), heatingSavings);
        components.put(waterHeatingSavings.getName(), waterHeatingSavings);

        // TODO note below is where the hard-coded ranges are defined, and will need to
        // be removed if it should be changed
        Double[] electricityCurve = { (1.0 / 12.0), (1.0 / 12.0), (1.0 / 12.0), (1.0 / 12.0), (1.0 / 12.0),
                (1.0 / 12.0), (1.0 / 12.0), (1.0 / 12.0), (1.0 / 12.0), (1.0 / 12.0), (1.0 / 12.0), (1.0 / 12.0) };
        input<Double[]> electricityCurveInput = new input<Double[]>("electricityCurve", "%", electricityCurve);
        components.put(electricityCurveInput.getName(), electricityCurveInput);

        Double[] heatingCurve = { 0.14, 0.14, 0.14, (3.0 / 70.0), (3.0 / 70.0), (3.0 / 70.0), (3.0 / 70.0), (3.0 / 70.0),
                (3.0 / 70.0), (3.0 / 70.0), 0.14, 0.14 };
        input<Double[]> heatingCurveInput = new input<Double[]>("heatingCurve", "%", heatingCurve);
        components.put(heatingCurveInput.getName(), heatingCurveInput);

        Double[] waterCurve = { (1.0 / 12.0), (1.0 / 12.0), (1.0 / 12.0), (1.0 / 12.0), (1.0 / 12.0), (1.0 / 12.0),
                (1.0 / 12.0), (1.0 / 12.0), (1.0 / 12.0), (1.0 / 12.0), (1.0 / 12.0), (1.0 / 12.0) };
        input<Double[]> waterCurveInput = new input<Double[]>("waterCurve", "%", waterCurve);
        components.put(waterCurveInput.getName(), waterCurveInput);

        Map<String, componentAbstract> dashboardDependsOn = new HashMap<String, componentAbstract>();
        dashboardDependsOn.put(electricityCurveInput.getName(), electricityCurveInput);
        dashboardDependsOn.put(heatingCurveInput.getName(), heatingCurveInput);
        dashboardDependsOn.put(waterCurveInput.getName(), waterCurveInput);
        dashboardDependsOn.put(originalElectricityConsumption.getName(), originalElectricityConsumption);
        dashboardDependsOn.put(electricityOutput.getName(), electricityOutput);
        dashboardDependsOn.put(waterConsumption.getName(), waterConsumption);
        dashboardDependsOn.put(waterConsumptionInput.getName(), waterConsumptionInput);
        dashboardDependsOn.put(heatingSourcesInput.getName(), heatingSourcesInput);
        dashboardDependsOn.put(heatingOutput.getName(), heatingOutput);
        dashboardFormattedString dashboardOutput = new dashboardFormattedString("dashboardString", "",dashboardDependsOn);
        components.put(dashboardOutput.getName(), dashboardOutput);
    }

    /**
     * This creates the domain objects, and adds them to the map
     * 
     * @return A list of initial Heat Sources
     */
    public static List<heatingEnergySource> createInitialListOfHeatSources() {

        // We then add the relevant heating sources (currently just district heating)
        heatingEnergySource districtHeating = new heatingEnergySource("districtHeating", 174812.0, 26850.0, 0.0, 0.9);
        heatingEnergySource gasHeating = new heatingEnergySource("gasHeating", 2000.0, 0.0, 0.0, 30.0);
        List<heatingEnergySource> initialListOfHeatSources = new ArrayList<heatingEnergySource>();
        initialListOfHeatSources.add(districtHeating);
        initialListOfHeatSources.add(gasHeating);

        return initialListOfHeatSources;
    }

    /**
     * This creates the inital list of improvements to display in the GUI
     * 
     * @return A list of improvements to include initially
     */
    public static List<improvement> createInitialListOfImprovements() {
        // We first create the representation for those improvements which are treated
        // in a "binary" manner, and which are applied, and then add them to the data
        // loader for objects
        improvement BergOrMarkHeating = new improvement("Berg Or Mark Heating", 940.0, 570.0, 15,
                improvementImpactEnum.BuildingHeating);
        improvement FTX = new improvement("FTX", 860.0, 620.0, 15, improvementImpactEnum.BuildingHeating);
        improvement ChangeWindows = new improvement("Change Windows", 770.0, 445.0, 40,
                improvementImpactEnum.BuildingHeating);
        improvement InsulateFacade = new improvement("Insulate Facade", 620.0, 630.0, 40,
                improvementImpactEnum.BuildingHeating);
        improvement FVP = new improvement("FVP", 600.0, 290.0, 20, improvementImpactEnum.BuildingHeating);
        improvement InsulateAttic = new improvement("Insulate Attic", 330.0, 100.0, 40,
                improvementImpactEnum.BuildingHeating);
        improvement SolarPanels = new improvement("Solar Panels", 220.0, 210.0, 15, improvementImpactEnum.Electricity);
        improvement IMDWater = new improvement("IMD Water", 140.0, 85.0, 10, improvementImpactEnum.Water);
        improvement ControlSystem = new improvement("Control System", 100.0, 45.0, 10,
                improvementImpactEnum.BuildingHeating);
        improvement ThermometerReconfiguration = new improvement("Thermometer Reconfiguration", 90.0, 50.0, 10,
                improvementImpactEnum.BuildingHeating);
        improvement EconomicalFlush = new improvement("Economical Flush", 100.0, 45.0, 15, improvementImpactEnum.Water);
        improvement EfficentLighting = new improvement("Efficent Lighting", 30.0, 15.0, 15,
                improvementImpactEnum.Electricity);

        List<improvement> initialListOfImprovements = new ArrayList<improvement>();
        initialListOfImprovements.add(BergOrMarkHeating);
        initialListOfImprovements.add(FTX);
        initialListOfImprovements.add(ChangeWindows);
        initialListOfImprovements.add(InsulateFacade);
        initialListOfImprovements.add(FVP);
        initialListOfImprovements.add(InsulateAttic);
        initialListOfImprovements.add(SolarPanels);
        initialListOfImprovements.add(IMDWater);
        initialListOfImprovements.add(ControlSystem);
        initialListOfImprovements.add(ThermometerReconfiguration);
        initialListOfImprovements.add(EconomicalFlush);
        initialListOfImprovements.add(EfficentLighting);
        return initialListOfImprovements;
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
     * 
     * @param key The key/name of the componentAbstract to retrive
     * @return The componentAbstract, or null if it does not exist
     */
    public componentAbstract getComponentAbstract(String key) {
        return components.get(key);
    }

    /**
     * Retrives a componentAbstract from the database loader
     * 
     * @return A collection which contains all of the
     */
    public Collection<componentAbstract> getAllComponentAbstract() {
        return components.values();
    }
}
