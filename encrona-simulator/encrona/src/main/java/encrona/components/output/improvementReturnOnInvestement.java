package encrona.components.output;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import encrona.components.componentAbstract;
import encrona.domain.heatingEnergySource;
import encrona.domain.improvement;
import encrona.modifiers.modifierAbstract;

public class improvementReturnOnInvestement extends componentAbstract<List<Map.Entry<String, Double>>> {

    /**
     * This is a constructor for improvementReturnOnInvestement
     * 
     * @param name      The name of this output
     * @param unit      The unit of this output
     * @param dependsOn the components this component depends on
     * @param modifiers the modifiers which should be applied to this component
     */
    public improvementReturnOnInvestement(String name, String unit, Map<String, componentAbstract> dependsOn,
            List<modifierAbstract<List<Entry<String, Double>>>> modifiers) {
        this.setName(name);
        this.setUnit(unit);
        this.setDependsOn(dependsOn);
        this.setModifiers(modifiers);
    }

    @Override
    public void calculate() throws Exception {

        Map<String, componentAbstract> dependsOnMap = getDependsOn();

        Double aTemp = (Double) dependsOnMap.get("Atemp").getValue();
        Double electricityPrice = (Double) dependsOnMap.get("Electricty price").getValue();
        Double waterPrice = (Double) dependsOnMap.get("Water price").getValue();

        List<Map.Entry<improvement, Map<String, Double>>> improvementImpacts = (List<Map.Entry<improvement, Map<String, Double>>>) dependsOnMap.get("improvementImpact").getValue();
        List<heatingEnergySource> heatSources = (List<heatingEnergySource>)dependsOnMap.get("heatingSources").getValue();

        List<Map.Entry<String, Double>> improvementROI = new ArrayList<Map.Entry<String, Double>>();

        Double heatingBuildingKwhPrice=calculateHeatingSourceKwhPrice(heatSources);
        Double heatingWaterKwhPrice=calculateHeatingWaterSourceKwhPrice(heatSources);

        for (Entry<improvement, Map<String, Double>> entry : improvementImpacts) {

            Double entryROIValue = calculateROI(aTemp, entry.getKey(), entry.getValue(), electricityPrice,waterPrice,heatingBuildingKwhPrice,heatingWaterKwhPrice);
            Entry<String, Double> roiEntry = new AbstractMap.SimpleEntry<String, Double>(entry.getKey().getName(),entryROIValue);
            improvementROI.add(roiEntry);
        }

        setValue(improvementROI);
    }

    /**
     * This calculates the ROI (The number of years needed to repay the initial
     * investment)
     * 
     * @param aTemp The area of the building
     * @param improvement The improvement to calculate for
     * @param improvementImpact The impact of the improvement
     * @param electrictyPrice The price per kwh 
     * @param heatSources A list of heat sources for this building
     * 
     * @return The number of years until investment is repayed (assumes all prices rise at the same rate the investment would)
     * @throws Exception
     */
    private Double calculateROI(Double aTemp, improvement improvement, Map<String, Double> improvementImpact,
            Double electricityPrice,Double waterPrice, Double HeatingSourceKwhPrice, Double HeatingWaterSourceKwhPrice) throws Exception {
        Double totalCost = improvement.getCostPerM2() * aTemp;

        Double totalYearlySavings=(improvementImpact.get("electricity") * electricityPrice)+
        (improvementImpact.get("buildingHeating") * HeatingSourceKwhPrice)
        +
        (improvementImpact.get("waterHeating") * HeatingWaterSourceKwhPrice)
        +
        (improvementImpact.get("water")*waterPrice)
        ;

        return (totalCost/totalYearlySavings);
    }

    /**
     * This method calculates the weighted average price per kwh for the specified heating sources, for heating the building
     * @param heatSources The heating sources for the current building
     * @return The weighted average kwh price for heating the building
     */
    private Double calculateHeatingSourceKwhPrice(List<heatingEnergySource> heatSources)
    {

        Double price=0.0;

        Double sumKWH=0.0;

        for (heatingEnergySource heatingEnergySource : heatSources) {
            price+=heatingEnergySource.getKwhPerYearHeating()*heatingEnergySource.getCostPerKwh();
            sumKWH+=heatingEnergySource.getKwhPerYearHeating();
        }
        if(sumKWH==0.0)
        {return 0.0;}

        return price/sumKWH;
    }
    /**
     * This method calculates the weighted average price per kwh for the specified heating sources, for heating the water
     * @param heatSources The heating sources for the current building
     * @return The weighted average kwh price for water the building
     */
    private Double calculateHeatingWaterSourceKwhPrice(List<heatingEnergySource> heatSources)
    {

        Double price=0.0;

        Double sumKWH=0.0;

        for (heatingEnergySource heatingEnergySource : heatSources) {
            price+=heatingEnergySource.getKwhPerYearHeatingWater()*heatingEnergySource.getCostPerKwh();
            sumKWH+=heatingEnergySource.getKwhPerYearHeatingWater();
        }

        if(sumKWH==0.0)
        {return 0.0;}
        
        return price/sumKWH;
    }
}
