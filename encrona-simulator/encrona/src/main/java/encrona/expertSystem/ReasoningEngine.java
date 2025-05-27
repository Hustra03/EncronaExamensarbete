package encrona.expertSystem;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import encrona.components.input;
import encrona.domain.heatingEnergySource;
import encrona.domain.improvement;

/**
 * This is the "main" class of the expert system, and is what is called, and what is responsible for setting up the system environment
 */
public class ReasoningEngine {
    
    private ExpertSystemModel model;
    private List<Rule> rules;
    private List<Rule> triggeredRules;

    /**
     * This instantiates the reasoning engine
     * @param numericalValues A list of numerical variables to include in the system model
     * @param heatingEnergySources A list of heat sources to include in the system model
     * @param expertSystemInput A map of inputs to the expert system to include in the system model
     */
    public ReasoningEngine(Map<Map.Entry<String, String>, Double> numericalValues,java.util.List<heatingEnergySource> heatingEnergySources,Map<String,input<?>> expertSystemInput )
    {
        this.rules=new ArrayList<>();
        this.model = new ExpertSystemModel(numericalValues,heatingEnergySources,expertSystemInput);
        generateRules();
    }

    /**
     * This method generates the rules the expert system will use
     * How to use lambda expressions https://stackoverflow.com/questions/13604703/how-do-i-define-a-method-which-takes-a-lambda-as-a-parameter-in-java-8
     */
    private void generateRules()
    {
        //We first define the rules which depend only on the system input, not on the systems generated suggestions
        //First are the rules which depend on binary input variable
        Condition hasFTXCondition = (lambdaModel) -> {
            return lambdaModel.getExpertSystemInput().get("hasFTX").getValue().equals(true);
        };

        PostCondition hasFTXorFVPPostCondition = (lambdaModel)->
        {
            lambdaModel.getSortedListOfImprovementsToConsider().forEach((item)->{

                if (item.getKey().getName().equals("FTX")) {
                    item.setValue(item.getValue()-5);
                }
                else
                {
                    if (item.getKey().getName().equals("FVP")) {
                    item.setValue(item.getValue()-5);
                }

                }

            });
        };
        Rule hasFTXRule = new Rule("Has FTX","If the building already has FTX, then FTX and FVP have lower priority", hasFTXCondition, hasFTXorFVPPostCondition, null);
        rules.add(hasFTXRule);

        Condition hasFVPCondition = (lambdaModel) -> {
            return lambdaModel.getExpertSystemInput().get("hasFVP").getValue().equals(true);
        };        
        Rule hasFVPRule = new Rule("Has FVP","If the building already has FVP, then FTX and FVP have lower priority", hasFVPCondition, hasFTXorFVPPostCondition, null);
        rules.add(hasFVPRule);

        Condition fvpSuitableCondition = (lambdaModel) -> {
            return ((!lambdaModel.getExpertSystemInput().get("existingUnusedChute").getValue().equals(true))&&(lambdaModel.getExpertSystemInput().get("naturalVentilation").getValue().equals(true)));
        };
        PostCondition fvpSuitablePostCondition = (lambdaModel)->
        {
            lambdaModel.getSortedListOfImprovementsToConsider().forEach((item)->{

                if (item.getKey().getName().equals("FTX")) {
                    item.setValue(item.getValue()-3);
                }
                else
                {
                    if (item.getKey().getName().equals("FVP")) {
                    item.setValue(item.getValue()+3);
                }

                }

            });
        };
        Rule fvpSuitableRule = new Rule("FVP suitability","If the building has natural ventilation, but no existing unused chute, then FVP +3 and FTX -3", fvpSuitableCondition, fvpSuitablePostCondition, null);
        rules.add(fvpSuitableRule);

        Condition ftxSuitableCondition = (lambdaModel) -> {
            return ((lambdaModel.getExpertSystemInput().get("existingUnusedChute").getValue().equals(true))&&(lambdaModel.getExpertSystemInput().get("naturalVentilation").getValue().equals(true))&&(lambdaModel.getExpertSystemInput().get("hasFromAndSupplyAir").getValue().equals(true)));
        };
        PostCondition ftxSuitablePostCondition = (lambdaModel)->
        {
            lambdaModel.getSortedListOfImprovementsToConsider().forEach((item)->{

                if (item.getKey().getName().equals("FTX")) {
                    item.setValue(item.getValue()+5);
                }
                else
                {
                    if (item.getKey().getName().equals("FVP")) {
                    item.setValue(item.getValue()-3);
                }

                }

            });
        };
        Rule ftxSuitableRule = new Rule("FTX suitability","If the building has natural ventilation, an existing unused chute and from and supply air (Från och tillluft) then FTX+5, FVP-3", ftxSuitableCondition, ftxSuitablePostCondition, null);
        rules.add(ftxSuitableRule);

        Condition windowLayersCondition = (lambdaModel) -> {
            return ((lambdaModel.getExpertSystemInput().get("lessThan3LayersOfWindows").getValue().equals(true)));
        };
        PostCondition windowLayersPostCondition = (lambdaModel)->
        {
            lambdaModel.getSortedListOfImprovementsToConsider().forEach((item)->{
                if (item.getKey().getName().equals("Byt fönster")) {
                    item.setValue(item.getValue()+100);
                }
            });
        };
        Rule windowLayersRule = new Rule("Window layers","If the building has windows with less than 3 layers, then changing windows is top priority", windowLayersCondition, windowLayersPostCondition, null);
        rules.add(windowLayersRule);

        Condition isPreservationOrderedCondition = (lambdaModel) -> {
            return ((lambdaModel.getExpertSystemInput().get("isPreservationOrdered").getValue().equals(true)));
        };
        PostCondition isPreservationOrderedPostCondition = (lambdaModel)->
        {
            lambdaModel.getSortedListOfImprovementsToConsider().forEach((item)->{
                if (item.getKey().getName().equals("Solpaneler")) {
                    item.setValue(item.getValue()-100);
                }
            });
        };
        Rule isPreservationOrderedRule = new Rule("Preservation order (K-märkt)","If the building has a preservation order, then certain improvements (ex. solar panels) are forbidden, so they should not be recommended", isPreservationOrderedCondition, isPreservationOrderedPostCondition, null);
        rules.add(isPreservationOrderedRule);

        
        Condition notFulfillingIMDWarmWaterEnergyRequiermentCondition = (lambdaModel) -> {
            return ((lambdaModel.getExpertSystemInput().get("notFulfillingIMDWarmWaterEnergyRequierment").getValue().equals(true)));
        };
        PostCondition notFulfillingIMDWarmWaterEnergyRequiermentPostCondition = (lambdaModel)->
        {
            lambdaModel.getSortedListOfImprovementsToConsider().forEach((item)->{
                if (item.getKey().getName().equals("IMD Varmvatten")) {
                    item.setValue(item.getValue()+100);
                }
            });
        };
        Rule notFulfillingIMDWarmWaterEnergyRequiermentRule = new Rule("IMD Warm water energy performance","If the building does not fulfill the energy performance metrics for IMD warm water, implementing it is a requierment by law, therefore it is a top priority", notFulfillingIMDWarmWaterEnergyRequiermentCondition, notFulfillingIMDWarmWaterEnergyRequiermentPostCondition, null);
        rules.add(notFulfillingIMDWarmWaterEnergyRequiermentRule);


        Condition ifNewlyBuiltCondition = (lambdaModel) -> {
            return ((lambdaModel.getExpertSystemInput().get("newlyBuilt").getValue().equals(true)));
        };
        PostCondition ifNewlyBuiltPostCondition = (lambdaModel)->
        {
            lambdaModel.getSortedListOfImprovementsToConsider().forEach((item)->{
                if (item.getKey().getName().equals("IMD El")) {
                    item.setValue(item.getValue()+100);
                }
            });
        };
        Rule ifNewlyBuiltRule = new Rule("Newly built","If the building was newly built IMD Electricity should be implemented", ifNewlyBuiltCondition, ifNewlyBuiltPostCondition, null);
        rules.add(ifNewlyBuiltRule);


        Condition ifReplumbingCondition = (lambdaModel) -> {
            return ((lambdaModel.getExpertSystemInput().get("replumbingNeeded").getValue().equals(true)));
        };
        PostCondition ifReplumbingPostCondition = (lambdaModel)->
        {
            lambdaModel.getSortedListOfImprovementsToConsider().forEach((item)->{
                if (item.getKey().getName().equals("IMD Varmvatten")) {
                    item.setValue(item.getValue()+100);
                }
            });
        };
        Rule ifReplumbingRule = new Rule("Replumbing","If replumbing needs to be done soon, then IMD warm water also needs to be installed (+100)", ifReplumbingCondition, ifReplumbingPostCondition, null);
        rules.add(ifReplumbingRule);


        Condition ifThermometerConfiguredCondition = (lambdaModel) -> {
            return ((lambdaModel.getExpertSystemInput().get("thermometerConfigured").getValue().equals(false)));
        };
        PostCondition ifThermometerConfiguredPostCondition = (lambdaModel)->
        {
            lambdaModel.getSortedListOfImprovementsToConsider().forEach((item)->{
                if (item.getKey().getName().equals("Regel och Styr")||item.getKey().getName().equals("Termostat+Inljustering")) {
                    item.setValue(item.getValue()+100);
                }
            });
        };
        Rule ifThermometerConfiguredRule = new Rule("Heating misconfiguration","If the heating system is not correctly configured, then Thermometer Reconfiguration and Heating Control System Reconfiguration are a top priority (+100)  ", ifThermometerConfiguredCondition, ifThermometerConfiguredPostCondition, null);
        rules.add(ifThermometerConfiguredRule);
        //Secondly, we define those which depend on numeric inputs

        Condition heatingControlSystemCondition = (lambdaModel) -> {
            return ((Double)(lambdaModel.getExpertSystemInput().get("heatingControlSystemAge").getValue())>=30.0);
        };
        PostCondition heatingControlSystemPostCondition = (lambdaModel)->
        {
            lambdaModel.getSortedListOfImprovementsToConsider().forEach((item)->{
                if (item.getKey().getName().equals("Berg Or Mark värme")) {
                    item.setValue(item.getValue()+4);
                }
            });
        };
        Rule heatingControlSystemRule = new Rule("Old heating control system","If the buildings heating control system is older than 30 years, geo-thermal energy +4", heatingControlSystemCondition, heatingControlSystemPostCondition, null);
        rules.add(heatingControlSystemRule);

        Condition lightingInstallationCondition = (lambdaModel) -> {
            return ((Double)(lambdaModel.getExpertSystemInput().get("lightingInstallationAge").getValue())>=20.0);
        };
        PostCondition lightingInstallationPostCondition = (lambdaModel)->
        {
            lambdaModel.getSortedListOfImprovementsToConsider().forEach((item)->{
                if (item.getKey().getName().equals("Belysning")) {
                    item.setValue(item.getValue()+100);
                }
            });
        };
        Rule lightingInstallationRule = new Rule("Old lighting Installation","If the lighting installation is older than 20 years, it is top priority ", lightingInstallationCondition, lightingInstallationPostCondition, null);
        rules.add(lightingInstallationRule);

                Condition roofAgeCondition = (lambdaModel) -> {
            return ((Double)(lambdaModel.getExpertSystemInput().get("roofAge").getValue())<=30.0);
        };
        PostCondition roofAgePostCondition = (lambdaModel)->
        {
            lambdaModel.getSortedListOfImprovementsToConsider().forEach((item)->{
                if (item.getKey().getName().equals("Takbyte")) {
                    item.setValue(item.getValue()-100);
                }
            });
        };
        Rule roofAgeRule = new Rule("New roof","If the roof is younger than 30 years, replacing it is much less relevant (-100) ", roofAgeCondition, roofAgePostCondition, null);
        rules.add(roofAgeRule);

        Condition facadeInsulationCondition = (lambdaModel) -> {
            return ((Double)(lambdaModel.getExpertSystemInput().get("facadeInsulationAge").getValue())>=40.0);
        };
        PostCondition facadeInsulationPostCondition = (lambdaModel)->
        {
            lambdaModel.getSortedListOfImprovementsToConsider().forEach((item)->{
                if (item.getKey().getName().equals("Fasadisolering")) {
                    item.setValue(item.getValue()+3);
                }
            });
        };
        Rule facadeInsulationRule = new Rule("Old facade insulation","If the facade insulation is older than 40 years, increase its priority by +3 ", facadeInsulationCondition, facadeInsulationPostCondition, null);
        rules.add(facadeInsulationRule);

        Condition atticInsulationCondition = (lambdaModel) -> {
            return ((Double)(lambdaModel.getExpertSystemInput().get("atticInsulationAge").getValue())>=40.0);
        };
        PostCondition atticInsulationPostCondition = (lambdaModel)->
        {
            lambdaModel.getSortedListOfImprovementsToConsider().forEach((item)->{
                if (item.getKey().getName().equals("Vindisolering")) {
                    item.setValue(item.getValue()+3);
                }
            });
        };
        Rule atticInsulationRule = new Rule("Old attic insulation","If the attic insulation is older than 40 years, increase its priority by +3 ", atticInsulationCondition, atticInsulationPostCondition, null);
        rules.add(atticInsulationRule);


        //Thirdly, we define those which depend on the other recommendations

        Condition recommendingGeothermalCondition = (lambdaModel) -> {

            for (Entry<improvement, Integer> entry : lambdaModel.getSortedListOfImprovementsToConsider()) {
                if (entry.getKey().getName().equals("Berg Or Mark värme") && entry.getValue().intValue()>=3)
                {return true;}
            }
            return false;
        };
        PostCondition recommendingGeothermalPostCondition = (lambdaModel)->
        {
            lambdaModel.getSortedListOfImprovementsToConsider().forEach((item)->{
                if (item.getKey().getName().equals("Fasadisolering")) {
                    item.setValue(item.getValue()+3);
                }
            });
        };
        Rule recommendingGeothermalRule = new Rule("Recommending geothermal","If geo-thermal heating is recommended, then facade insulation is given higher priority (+3)", recommendingGeothermalCondition, recommendingGeothermalPostCondition, null);
        rules.add(recommendingGeothermalRule);

        Condition recommendingFTXFVPCondition = (lambdaModel) -> {

            for (Entry<improvement, Integer> entry : lambdaModel.getSortedListOfImprovementsToConsider()) {
                if (((entry.getKey().getName().equals("FTX"))||(entry.getKey().getName().equals("FVP"))) && entry.getValue().intValue()>=3)
                {return true;}
            }
            return false;
        };
        PostCondition recommendingFTXFVPPostCondition = (lambdaModel)->
        {
            lambdaModel.getSortedListOfImprovementsToConsider().forEach((item)->{
                if (item.getKey().getName().equals("Vindisolering")||item.getKey().getName().equals("Takbyte")) {
                    item.setValue(item.getValue()+100);
                }
            });
        };
        Rule recommendingFTXFVPRule = new Rule("Recommending FTX/FVP","If FTX or FVP is recommended, then attic insulation and roof replacement is top priority (+100)", recommendingFTXFVPCondition, recommendingFTXFVPPostCondition, null);
        rules.add(recommendingFTXFVPRule);

        
        Condition recommendingRoofReplacementCondition = (lambdaModel) -> {

            for (Entry<improvement, Integer> entry : lambdaModel.getSortedListOfImprovementsToConsider()) {
                if ((entry.getKey().getName().equals("Takbyte")) && entry.getValue().intValue()>=3)
                {return true;}
            }
            return false;
        };
        PostCondition recommendingRoofReplacementPostCondition = (lambdaModel)->
        {
            lambdaModel.getSortedListOfImprovementsToConsider().forEach((item)->{
                if (item.getKey().getName().equals("Solpaneler")) {
                    item.setValue(item.getValue()+3);
                }
            });
        };
        Rule recommendingRoofReplacementRule = new Rule("Recommending roof replacement","If roof replacement is recommended, then solar panels are a higher priority (+3)", recommendingRoofReplacementCondition, recommendingRoofReplacementPostCondition, null);
        rules.add(recommendingRoofReplacementRule);


        Condition recommendingFVPCondition = (lambdaModel) -> {

            for (Entry<improvement, Integer> entry : lambdaModel.getSortedListOfImprovementsToConsider()) {
                if ((entry.getKey().getName().equals("FVP")) && entry.getValue().intValue()>=3)
                {return true;}
            }
            return false;
        };
        PostCondition recommendingFVPPostCondition = (lambdaModel)->
        {
            lambdaModel.getSortedListOfImprovementsToConsider().forEach((item)->{
                if (item.getKey().getName().equals("Solpaneler")) {
                    item.setValue(item.getValue()+2);
                }
            });
        };
        Rule recommendingFVPRule = new Rule("Recommending FVP","If FVP is recommended, then solar panels are given higher priority (+2)", recommendingFVPCondition, recommendingFVPPostCondition, null);
        rules.add(recommendingFVPRule);

        Condition recommendingSolarPanelsCondition = (lambdaModel) -> {

            for (Entry<improvement, Integer> entry : lambdaModel.getSortedListOfImprovementsToConsider()) {
                if ((entry.getKey().getName().equals("Solpaneler")) && entry.getValue().intValue()>=3)
                {return true;}
            }
            return false;
        };
        PostCondition recommendingSolarPanelsPostCondition = (lambdaModel)->
        {
            lambdaModel.getSortedListOfImprovementsToConsider().forEach((item)->{
                if (item.getKey().getName().equals("FVP")||item.getKey().getName().equals("Takbyte")) {
                    item.setValue(item.getValue()+3);
                }
                else
                {
                if (item.getKey().getName().equals("IMD El")) {
                    item.setValue(item.getValue()+100);
                }
                }

            });
        };
        Rule recommendingSolarPanelsRule = new Rule("Recommending Solar Panels","If Solar Panels are recommended, then FVP and roof replacement is given higher priority (+3), while IMD Electricity is a top priority (+100, to measure who uses what)", recommendingSolarPanelsCondition, recommendingSolarPanelsPostCondition, null);
        rules.add(recommendingSolarPanelsRule);

        Condition recommendingReplaceWindowsCondition = (lambdaModel) -> {

            for (Entry<improvement, Integer> entry : lambdaModel.getSortedListOfImprovementsToConsider()) {
                if ((entry.getKey().getName().equals("Byt fönster")) && entry.getValue().intValue()>=3)
                {return true;}
            }
            return false;
        };
        PostCondition recommendingReplaceWindowsPostCondition = (lambdaModel)->
        {
            lambdaModel.getSortedListOfImprovementsToConsider().forEach((item)->{
                if (item.getKey().getName().equals("Regel och Styr")||item.getKey().getName().equals("Termostat+Inljustering")) {
                    item.setValue(item.getValue()+5);
                }
            });
        };
        Rule recommendingReplaceWindowsRule = new Rule("Window replacement","If window replacement is recommended, then both Thermometer Reconfiguration and Heating Control System Reconfiguration are a high priority (+5)", recommendingReplaceWindowsCondition, recommendingReplaceWindowsPostCondition, null);
        rules.add(recommendingReplaceWindowsRule);

    }

    /**
     * This method is called to start the expert system execution, and will return a sorted list of improvements (which is the current recommendation)
     * @return A a sorted list of improvements as a list
     */
    public List<Entry<String,Integer>> recommendations()
    {

        List<Entry<String,Integer>> recommendationStringList=new ArrayList<>();

        triggeredRules=new ArrayList<>();
        for (Rule rule : rules) {
            if (Boolean.TRUE.equals(rule.testRule(model))) {
                triggeredRules.add(rule);
            }
        }
        for (Entry<improvement,Integer> queueValue : model.getSortedListOfImprovementsToConsider()) {
            recommendationStringList.add(new AbstractMap.SimpleEntry<>(queueValue.getKey().getName(),queueValue.getValue())); 
        }        

        return recommendationStringList;
    }

    /**
     * A basic getter for the triggered rule attribute
     */
    public List<Rule> getTriggeredRules()
    {
        return this.triggeredRules;
    }
}
