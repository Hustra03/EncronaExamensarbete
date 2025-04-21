package encrona.components.output;

import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import encrona.domain.heatingEnergySource;

import encrona.components.componentAbstract;

/**
 * This is used to genererate the json string which is used to transfer simulation data to the frontend
 */
public class dashboardFormattedString extends componentAbstract<String>{

    /**
     * This is a constructor for dashboardFormattedString
     * 
     * @param name      The name of this output
     * @param unit      The unit of this output
     * @param dependsOn the components this component depends on
     */
    public dashboardFormattedString(String name, String unit, Map<String, componentAbstract> dependsOn) {
        this.setName(name);
        this.setUnit(unit);
        this.setDependsOn(dependsOn);
        this.setModifiers(null);
    }

    @Override
    public void calculate() throws Exception {

        Map<String, componentAbstract> dependsOnMap = getDependsOn();

        Double[] electricityCurve=(Double[]) dependsOnMap.get("electricityCurve").getValue();
        Double[] heatingCurve=(Double[]) dependsOnMap.get("heatingCurve").getValue();
        Double[] waterCurve=(Double[]) dependsOnMap.get("waterCurve").getValue();

        Double originalElectricityConsumption=(Double)dependsOnMap.get("originalElectricityConsumption").getValue();
        List<Map.Entry<Integer, Double>> electricityConsumptionAfterImprovements= (List<Map.Entry<Integer, Double>>) dependsOnMap.get("electricityOutput").getValue();
        
        Double waterConsumption=(Double)dependsOnMap.get("Water consumption").getValue();
        List<Map.Entry<Integer, Double>> waterConsumptionAfterImprovements= (List<Map.Entry<Integer, Double>>) dependsOnMap.get("waterConsumption").getValue();

        List<heatingEnergySource> originalHeatingEnergySources = (List<heatingEnergySource>) dependsOnMap.get("heatingSources").getValue();
        List<Map.Entry<Integer, List<heatingEnergySource>>> heatSourcesAfterConsumption= (List<Map.Entry<Integer, List<heatingEnergySource>>>) dependsOnMap.get("heatingOutput").getValue();

        JSONObject object = new JSONObject();
        object.put("electricityCurve", electricityCurve);
        object.put("heatingCurve", heatingCurve);
        object.put("waterCurve", waterCurve);
        JSONArray electricityConsumptionArray = new JSONArray();
            for (Map.Entry<Integer, Double> position : electricityConsumptionAfterImprovements) {
                JSONObject electricityConsumptionPosition = new JSONObject();
                electricityConsumptionPosition.put("year",position.getKey());
                electricityConsumptionPosition.put("electricityValue", position.getValue());
                electricityConsumptionArray.put(electricityConsumptionPosition);
            }
        object.put("electricityConsumption", electricityConsumptionArray);
        JSONArray electricitySavingsArray = new JSONArray();
        for (Map.Entry<Integer, Double> position : electricityConsumptionAfterImprovements) {
            JSONObject electricityConsumptionPosition = new JSONObject();
            electricityConsumptionPosition.put("year",position.getKey());
            electricityConsumptionPosition.put("electricityValue", originalElectricityConsumption-position.getValue());
            electricitySavingsArray.put(electricityConsumptionPosition);
        }
        object.put("electricitySavings", electricitySavingsArray);
        
        JSONArray waterConsumptionArray = new JSONArray();
        for (Map.Entry<Integer, Double> position : waterConsumptionAfterImprovements) {
            JSONObject waterConsumptionPosition = new JSONObject();
            waterConsumptionPosition.put("year",position.getKey());
            waterConsumptionPosition.put("waterValue", position.getValue());
            waterConsumptionArray.put(waterConsumptionPosition);
        }
        object.put("waterConsumption", waterConsumptionArray);

        JSONArray waterSavingsArray = new JSONArray();
        for (Map.Entry<Integer, Double> position : waterConsumptionAfterImprovements) {
            JSONObject waterSavingsPosition = new JSONObject();
            waterSavingsPosition.put("year",position.getKey());
            waterSavingsPosition.put("waterValue", waterConsumption-position.getValue());
            waterSavingsArray.put(waterSavingsPosition);
        }
        object.put("waterSavings", waterSavingsArray);
        
        

        JSONArray heatSourcesOverTimeArray=new JSONArray();

        for (Map.Entry<Integer, List<heatingEnergySource>> sourceEntriesAtPointInTime : heatSourcesAfterConsumption) {
            
            JSONObject heatSourceAtPointInTime=new JSONObject();

            JSONArray heatSourceInfoAtPointInTimeArray = new JSONArray();
            for (heatingEnergySource source : sourceEntriesAtPointInTime.getValue()) {

                heatingEnergySource originalSource=null;
                for (heatingEnergySource source2 : originalHeatingEnergySources) {
                    if (source2.getName().equals(source.getName())) {
                        originalSource=source2;
                    }
                }

                JSONObject heatSource=new JSONObject();
                heatSource.put("name", source.getName());
                heatSource.put("buildingHeatingConsumption", source.getKwhPerYearHeating());
                heatSource.put("buildingHeatingSavings", originalSource.getKwhPerYearHeating()-source.getKwhPerYearHeating());
                heatSource.put("waterHeatingConsumption", source.getKwhPerYearHeatingWater());
                heatSource.put("waterHeatingSavings", originalSource.getKwhPerYearHeatingWater()-source.getKwhPerYearHeatingWater());
                heatSourceInfoAtPointInTimeArray.put(heatSource);
            }
            heatSourceAtPointInTime.put("year",sourceEntriesAtPointInTime.getKey());
            heatSourceAtPointInTime.put("heatSource", heatSourceInfoAtPointInTimeArray);
            heatSourcesOverTimeArray.put(heatSourceAtPointInTime);
        }
        object.put("heatSources", heatSourcesOverTimeArray);

        setValue(object.toString());
        System.out.println(object.toString());
    }
    
}
