package encrona.components.output;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import encrona.components.componentAbstract;
import encrona.domain.heatingEnergySource;
import encrona.domain.improvement;
import encrona.domain.improvementImpactEnum;
import encrona.modifiers.modifierAbstract;

public class finalYearlyBuildingHeatingConsumption extends componentAbstract<List<Map.Entry<Integer,List<heatingEnergySource>>>>{
        /**
     * This is a constructor for finalElectricityConsumptionChange 
     * @param name The name of this output
     * @param unit The unit of this output
     * @param dependsOn the components this component depends on
     * @param modifiers the modifiers which should be applied to this component
     */
    public finalYearlyBuildingHeatingConsumption(String name, String unit, Map<String,componentAbstract> dependsOn, List<modifierAbstract<List<Entry<Integer, List<heatingEnergySource>>>>> modifiers)
    {   this.setName(name);
        this.setUnit(unit);
        this.setDependsOn(dependsOn);
        this.setModifiers(modifiers);
    }

    @Override
    /**
     * This method implements the calculate functionality for finalElectricityConsumptionChange
     * TODO confirm the calculation with Laszlo
     */
    public void calculate() throws Exception {

        Map<String,componentAbstract> dependsOnMap = getDependsOn();

        List<heatingEnergySource> baseValues = (List<heatingEnergySource>)dependsOnMap.get("heatingSources").getValue();
        //We sort heat sources based on their kwh price, since we want to minimize costs
        baseValues.sort((heatingEnergySource h1, heatingEnergySource h2) -> h1.getCostPerKwh().compareTo(h2.getCostPerKwh()));

        List<Entry<improvement, Double>> improvementImpacts=(List<Entry<improvement, Double>>)dependsOnMap.get("improvementImpact").getValue();
        //Here we create a shallow copy of improvementImpacts, so the list is cloned but the objects are the same instances as in the origninal
        improvementImpacts=(List<Entry<improvement, Double>>) ((ArrayList)improvementImpacts).clone();

        //https://www.w3schools.com/java/java_lambda.asp 
        //This removes all improvements which do not impact Electricity
        //TODO update this if type structure is changed
        improvementImpacts.removeIf((improvement)->{return !(improvement.getKey().getImpactType().equals(improvementImpactEnum.BuildingHeating));});

        //This creates a set of the unique years of service, aka the unique values we need to find electricity for
        Set<Integer> uniqueYearsOfService=new HashSet<Integer>();

        for (Entry<improvement, Double> entry : improvementImpacts) {
            uniqueYearsOfService.add(entry.getKey().getYearsOfService());
        }
        int yearsOfService[]=new int[uniqueYearsOfService.size()];



        //Note that we use Map.Entry<Double,Double> to represent a pair of doubles, in this case years of service and yearly consumption
        List<Map.Entry<Integer,List<heatingEnergySource>>> heatingConsumptionList = new ArrayList<Map.Entry<Integer,List<heatingEnergySource>>>();

        for (int i = 0; i < yearsOfService.length; i++) {
            
            Integer min=0;
            Integer currentMin=Integer.MAX_VALUE;
            Double improvementImpact=0.0;
            List<Entry<improvement, Double>> improvementsStillActive = new ArrayList<Entry<improvement, Double>>();
            if (i>1) {
                min=yearsOfService[i-1];
            }

            for (Entry<improvement, Double> entry : improvementImpacts) {
                if (entry.getKey().getYearsOfService()>min) {
                    if (entry.getKey().getYearsOfService()<currentMin) {
                        currentMin=entry.getKey().getYearsOfService();
                    }                        
                    improvementImpact+=(entry.getValue());
                    improvementsStillActive.add(entry);
                }
            }
            yearsOfService[i]=currentMin;
            List<heatingEnergySource> updatedHeatingSources=distributeImpact(baseValues,improvementsStillActive);
            Entry<Integer,List<heatingEnergySource>> entry = new AbstractMap.SimpleEntry<Integer, List<heatingEnergySource>>(yearsOfService[i], updatedHeatingSources);
            heatingConsumptionList.add(entry);
        }


        this.setValue(heatingConsumptionList);
    }

    /**
     * This function will distribute the impact from the measure between the different sources of heat
     * TODO potentially update this to only modify certain sources for certain improvements(?)
     * @return A modifier list of heating sources, with the impact distributed between them
     */
    private List<heatingEnergySource> distributeImpact(List<heatingEnergySource> heatSources, List<Entry<improvement, Double>> improvementsStillActive)
    {


        //We first combine the impact of the relevant improvements, to get the total reduction
        Double sumOfHeatingNeedReduction=0.0;
        for (Entry<improvement,Double> entry : improvementsStillActive) {
            sumOfHeatingNeedReduction+=entry.getValue();
        }

        //We then create a copy of heatSources, which we can modify to represent this specific year ranges values
        List<heatingEnergySource> copyOfHeatSources = new ArrayList<heatingEnergySource>();
        for (heatingEnergySource heatingEnergySource : heatSources) {
            copyOfHeatSources.add(heatingEnergySource);
        }

        int index=0;
        //We then iterate over the above list, and remove building heating by sumOfHeatingNeedReduction until it is fully used
        while (sumOfHeatingNeedReduction>0.0) {
            
            heatingEnergySource source=copyOfHeatSources.get(index);

            Double reduceBy=Double.min(sumOfHeatingNeedReduction, source.getKwhPerYearHeating());
            source.setKwhPerYearHeating(source.getKwhPerYearHeating()-reduceBy);
            sumOfHeatingNeedReduction-=reduceBy;
            index+=1;
        }

        return copyOfHeatSources;

    }

}
