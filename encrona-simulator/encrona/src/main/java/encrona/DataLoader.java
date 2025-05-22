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
import encrona.domain.heatingEnergySource;
import encrona.domain.improvement;
import encrona.components.output.*;

/**
 * This class is responsible for instantiating the components, domains and
 * modifiers
 */
public class DataLoader {

    private Map<String,componentAbstract> components;

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
        components = new HashMap<>();

        // We then fill the maps with relevant data
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

        for (Entry<Map.Entry<String, String>, Double> entry : mapOfNumericalVariables.entrySet()) {
            switch (entry.getKey().getKey()) {
                case "Atemp":
                    aTempInput = new input<>("Atemp", entry.getKey().getValue(), entry.getValue());
                    break;
                case "Electricty consumption":
                    electricityInput = new input<>("Electricty consumption", entry.getKey().getValue(),entry.getValue());
                    break;
                case "Electricty price":
                    electrictyPriceInput = new input<>("Electricty price", entry.getKey().getValue(),entry.getValue());
                    break;
                case "Water consumption":
                    waterConsumptionInput = new input<>("Water consumption", entry.getKey().getValue(),entry.getValue());
                    break;
                case "Water price":
                    waterPriceInput = new input<>("Water price", entry.getKey().getValue(),entry.getValue());
                    break;
                default:
                    break;
            }

        }

        // We then add the improvements which were selected

        input<List<improvement>> improvementsToImplement = new input<>("improvements", "",
                improvement);

        // We then add the different sources of heating energy
        input<List<heatingEnergySource>> heatingSourcesInput = new input<>("heatingSources",
                "", heatingEnergySources);

        //We then define the intermediate values, as in the calculated values which are not shown to the user directly

        Map<String, componentAbstract<?>> originalElectricityConsumptionDependsOn = new HashMap<>();
        originalElectricityConsumptionDependsOn.put(electricityInput.getName(), electricityInput);
        originalElectricityConsumptionDependsOn.put(heatingSourcesInput.getName(), heatingSourcesInput);
        fullOriginalElectricityConsumption originalElectricityConsumption = new fullOriginalElectricityConsumption(
                "originalElectricityConsumption", "kwh", originalElectricityConsumptionDependsOn);

        // We then define the different outputs
        Map<String, componentAbstract<?>> improvementImpactDependsOn = new HashMap<>();
        improvementImpactDependsOn.put(aTempInput.getName(), aTempInput);
        improvementImpactDependsOn.put(improvementsToImplement.getName(), improvementsToImplement);
        improvementImpact improvementImpact = new improvementImpact("improvementImpact", "kwh",
                improvementImpactDependsOn);

        Map<String, componentAbstract<?>> waterConsumptionDependsOn = new HashMap<>();
        waterConsumptionDependsOn.put(waterConsumptionInput.getName(), waterConsumptionInput);
        waterConsumptionDependsOn.put(improvementImpact.getName(), improvementImpact);
        finalYearlyWaterConsumption waterConsumption = new finalYearlyWaterConsumption(
                "waterConsumption", "m^3", waterConsumptionDependsOn);

        Map<String, componentAbstract<?>> waterSavingsDependsOn = new HashMap<>();
        waterSavingsDependsOn.put(waterConsumption.getName(), waterConsumption);
        waterSavingsDependsOn.put(waterPriceInput.getName(), waterPriceInput);
        finalYearlySavingsFromWater waterSavings = new finalYearlySavingsFromWater(
                        "waterSavings", "kr/year", waterSavingsDependsOn);
        
        Map<String, componentAbstract<?>> improvementReturnOnInvestementDependsOn = new HashMap<>();
        improvementReturnOnInvestementDependsOn.put(aTempInput.getName(), aTempInput);
        improvementReturnOnInvestementDependsOn.put(electrictyPriceInput.getName(), electrictyPriceInput);
        improvementReturnOnInvestementDependsOn.put(waterPriceInput.getName(), waterPriceInput);
        improvementReturnOnInvestementDependsOn.put(improvementImpact.getName(), improvementImpact);
        improvementReturnOnInvestementDependsOn.put(heatingSourcesInput.getName(), heatingSourcesInput);
        improvementReturnOnInvestement improvementReturnOnInvestement = new improvementReturnOnInvestement(
                "improvementReturnOnInvestement", "years", improvementReturnOnInvestementDependsOn);

        Map<String, componentAbstract<?>> electricityOutputDependsOn = new HashMap<>();
        electricityOutputDependsOn.put(originalElectricityConsumption.getName(),originalElectricityConsumption);
        electricityOutputDependsOn.put(improvementImpact.getName(), improvementImpact);
        finalYearlyElectricityConsumption electricityOutput = new finalYearlyElectricityConsumption("electricityOutput",
                "kwh/year", electricityOutputDependsOn);

        Map<String, componentAbstract<?>> electricitySavingsDependsOn = new HashMap<>();
        electricitySavingsDependsOn.put(electricityOutput.getName(), electricityOutput);
        electricitySavingsDependsOn.put(electrictyPriceInput.getName(), electrictyPriceInput);
        finalYearlySavingsFromElectricity electricitySavings = new finalYearlySavingsFromElectricity(
                "electricitySavings", "kr/year", electricitySavingsDependsOn);

        Map<String, componentAbstract<?>> heatingOutputDependsOn = new HashMap<>();
        heatingOutputDependsOn.put(improvementImpact.getName(), improvementImpact);
        heatingOutputDependsOn.put(heatingSourcesInput.getName(), heatingSourcesInput);
        finalYearlyHeatingConsumption heatingOutput = new finalYearlyHeatingConsumption("heatingOutput",
                "kwh/year", heatingOutputDependsOn);

        Map<String, componentAbstract<?>> heatingSavingsDependsOn = new HashMap<>();
        heatingSavingsDependsOn.put(heatingOutput.getName(), heatingOutput);
        heatingSavingsDependsOn.put(heatingSourcesInput.getName(), heatingSourcesInput);
        finalYearlyBuildingHeatingSavings heatingSavings = new finalYearlyBuildingHeatingSavings("heatingSavings",
                "kr/year", heatingSavingsDependsOn);

        Map<String, componentAbstract<?>> waterHeatingSavingsDependsOn = new HashMap<>();
        waterHeatingSavingsDependsOn.put(heatingOutput.getName(), heatingOutput);
        waterHeatingSavingsDependsOn.put(heatingSourcesInput.getName(), heatingSourcesInput);
        finalYearlyWaterHeatingSavings waterHeatingSavings = new finalYearlyWaterHeatingSavings("waterHeatingSavings",
                "kr/year", waterHeatingSavingsDependsOn);

        // We then finally add all of the defined outputs to components
        components.put(waterConsumption.getName(), waterConsumption);
        components.put(waterSavings.getName(), waterSavings);
        components.put(improvementImpact.getName(), improvementImpact);
        components.put(improvementReturnOnInvestement.getName(), improvementReturnOnInvestement);
        components.put(electricityOutput.getName(), electricityOutput);
        components.put(electricitySavings.getName(), electricitySavings);
        components.put(heatingOutput.getName(), heatingOutput);
        components.put(heatingSavings.getName(), heatingSavings);
        components.put(waterHeatingSavings.getName(), waterHeatingSavings);

        // TODO note below is where the hard-coded ranges are defined, and will need to
        // be removed if it should be changed
        //Electricity curve is sourced from Sveby https://www.sveby.org/wp-content/uploads/2024/11/Bakgrund_Brukarindata_bostadshus_241108.pdf
        Double[] electricityCurve = { (1.43 / 14.7), (1.31 / 14.7), (1.34 / 14.7), (1.18 / 14.7), (1.11 / 14.7),
                (1.02 / 14.7), (.98 / 14.7), (1.08 / 14.7), (1.13 / 14.7), (1.27 / 14.7), (1.34 / 14.7), (1.5 / 14.7) };
        input<Double[]> electricityCurveInput = new input<>("electricityCurve", "%", electricityCurve);
        components.put(electricityCurveInput.getName(), electricityCurveInput);

        Double[] heatingCurve = { 0.14, 0.14, 0.14, (3.0 / 70.0), (3.0 / 70.0), (3.0 / 70.0), (3.0 / 70.0), (3.0 / 70.0),
                (3.0 / 70.0), (3.0 / 70.0), 0.14, 0.14 };
        input<Double[]> heatingCurveInput = new input<>("heatingCurve", "%", heatingCurve);
        components.put(heatingCurveInput.getName(), heatingCurveInput);

        Double[] waterCurve = { (1.0 / 12.0), (1.0 / 12.0), (1.0 / 12.0), (1.0 / 12.0), (1.0 / 12.0), (1.0 / 12.0),
                (1.0 / 12.0), (1.0 / 12.0), (1.0 / 12.0), (1.0 / 12.0), (1.0 / 12.0), (1.0 / 12.0) };
        input<Double[]> waterCurveInput = new input<>("waterCurve", "%", waterCurve);
        components.put(waterCurveInput.getName(), waterCurveInput);

        Map<String, componentAbstract<?>> dashboardDependsOn = new HashMap<>();
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
        List<heatingEnergySource> initialListOfHeatSources = new ArrayList<>();
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
        // We first create the representation for those improvements
        //Note that the names here must also be updated in ReasoningEngine if they are modified, since that uses the literal names to find the correct ones
        improvement BergOrMarkHeating = new improvement("Berg Or Mark värme", 940.0,0.0,0.0,0.0, 570.0, 15 );
        improvement FTX = new improvement("FTX", 860.0,0.0,0.0,0.0, 620.0, 15 );
        improvement ChangeWindows = new improvement("Byt fönster", 770.0,0.0,0.0,0.0, 445.0, 40 );
        improvement InsulateFacade = new improvement("Fasadisolering", 620.0,0.0,0.0,0.0, 630.0, 40 );
        improvement FVP = new improvement("FVP", 600.00,0.0,0.0,0.0, 290.0, 20 );
        improvement InsulateAttic = new improvement("Vindisolering", 330.0,0.0,0.0,0.0, 100.0, 40 );
        improvement SolarPanels = new improvement("Solpaneler", 0.0,0.0,220.0,0.0, 210.0, 15 );
        improvement IMDWarmWater = new improvement("IMD Varmvatten", 0.0,140.0,0.0,0.0, 85.0, 10 );
        improvement ControlSystem = new improvement("Regel och Styr", 0.0,100.0,0.0,0.0, 45.0, 10 );
        improvement ThermometerReconfiguration = new improvement("Termostat+Inljustering", 90.0,0.0,0.0,0.0, 50.0, 10 );
        improvement EconomicalFlush = new improvement("Snålpolande armatur", 0.0,100.0,0.0,0.0, 45.0, 15 );
        improvement EfficentLighting = new improvement("Belysning", 0.0,0.0,30.0,0.0, 15.0, 15 );
        improvement IMDEl = new improvement("IMD El", 0.0,0.0,0.0,0.0, 85.0, 10 );
        improvement roofReplacement = new improvement("Takbyte", 0.0,0.0,0.0,0.0, 85.0, 10 );


        List<improvement> initialListOfImprovements = new ArrayList<>();
        initialListOfImprovements.add(BergOrMarkHeating);
        initialListOfImprovements.add(FTX);
        initialListOfImprovements.add(ChangeWindows);
        initialListOfImprovements.add(InsulateFacade);
        initialListOfImprovements.add(FVP);
        initialListOfImprovements.add(InsulateAttic);
        initialListOfImprovements.add(SolarPanels);
        initialListOfImprovements.add(IMDWarmWater);
        initialListOfImprovements.add(ControlSystem);
        initialListOfImprovements.add(ThermometerReconfiguration);
        initialListOfImprovements.add(EconomicalFlush);
        initialListOfImprovements.add(EfficentLighting);
        initialListOfImprovements.add(IMDEl);
        initialListOfImprovements.add(roofReplacement);
        return initialListOfImprovements;
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
