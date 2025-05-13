package encrona.components.output;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import encrona.components.componentAbstract;
import encrona.domain.improvement;

/**
 * This creates a list for the impact every individual improvement will have per year in kwh
 */
public class improvementImpact extends componentAbstract<List<Map.Entry<improvement,Map<String,Double>>>>{

    /**
     * This is a constructor for improvementImpact 
     * @param name The name of this output
     * @param unit The unit of this output
     * @param dependsOn the components this component depends on
     */
    public improvementImpact(String name, String unit, Map<String,componentAbstract> dependsOn)
    {   this.setName(name);
        this.setUnit(unit);
        this.setDependsOn(dependsOn);
    }

    @Override
    public void calculate() throws Exception {

        Map<String,componentAbstract> dependsOnMap = getDependsOn();

        Double aTemp = (Double)dependsOnMap.get("Atemp").getValue();
        List<improvement> improvements=(List<improvement>)dependsOnMap.get("improvements").getValue();

        List<Map.Entry<improvement,Map<String,Double>>> improvementImpact=new ArrayList<Map.Entry<improvement,Map<String,Double>>>();

        for (improvement improvement : improvements) {
            //We get m2 / years of service = yearly kwh impact
            Double impactMultiplication = (aTemp)/improvement.getYearsOfService();
            Map<String,Double> improvementImpactMap = new HashMap<String,Double>();

            improvementImpactMap.put("buildingHeating", improvement.getKwhPerM2BuildingHeating()*impactMultiplication);
            improvementImpactMap.put("waterHeating", improvement.getKwhPerM2WaterHeating()*impactMultiplication);
            improvementImpactMap.put("electricity", improvement.getKwhPerM2Electricity()*impactMultiplication);
            improvementImpactMap.put("water", improvement.getM3PerM2Water()*impactMultiplication);

            Entry<improvement,Map<String,Double>> entry = new AbstractMap.SimpleEntry<improvement, Map<String,Double>>((improvement)improvement,improvementImpactMap );
            improvementImpact.add(entry);
        }
        this.setValue(improvementImpact);
    }
    
}
