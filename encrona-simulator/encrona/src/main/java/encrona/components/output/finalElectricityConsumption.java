package encrona.components.output;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import encrona.components.componentAbstract;
import encrona.domain.improvement;
import encrona.domain.improvementImpactEnum;
import encrona.modifiers.modifierAbstract;

public class finalElectricityConsumption extends componentAbstract<List<Double>>{

    /**
     * This is a constructor for finalElectricityConsumptionChange 
     * @param name The name of this output
     * @param unit The unit of this output
     * @param dependsOn the components this component depends on
     * @param modifiers the modifiers which should be applied to this component
     */
    public finalElectricityConsumption(String name, String unit, Map<String,componentAbstract> dependsOn, List<modifierAbstract<List<Double>>> modifiers)
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
        System.out.println("Running output "+getName());

        System.out.println(dependsOnMap.size());

        for (componentAbstract iterable_element : dependsOnMap.values()) {
            System.out.println(iterable_element.getName() + " "+ iterable_element.getValue());
        }
        
        Integer aTemp = (Integer)dependsOnMap.get("aTempInput").getValue();
        Double baseValue = (Double)dependsOnMap.get("electricityConsumptionInput").getValue();

        List<improvement> improvements=(List<improvement>)dependsOnMap.get("improvements").getValue();

        //https://www.w3schools.com/java/java_lambda.asp 
        //This removes all improvements which do not impact Electricity

        improvements.removeIf((improvement)->{return !(improvement.getImpactType().equals(improvementImpactEnum.Electricity));});

        //This creates a set of the unique years of service, aka the unique values we need to find electricity for
        Set<Integer> uniqueYearsOfService=new HashSet<Integer>();
        for (improvement i : improvements) {
            uniqueYearsOfService.add(i.getYearsOfService());
        }

        int yearsOfService[]=new int[uniqueYearsOfService.size()];
        Set<Double> improvementImpactList= new LinkedHashSet<Double>();

        for (int i = 0; i < yearsOfService.length; i++) {
            
            Integer min=0;
            Integer currentMin=Integer.MAX_VALUE;
            Double improvementImpact=0.0;
            if (i>1) {
                min=yearsOfService[i-1];
            }

            for (improvement imp : improvements) {
                if (imp.getYearsOfService()>min) {
                    if (imp.getYearsOfService()<currentMin) {
                        currentMin=imp.getYearsOfService();
                    }                        
                    improvementImpact+=(imp.getKwhPerM2()*aTemp)/imp.getYearsOfService();

                }
            }
            yearsOfService[i]=currentMin;
            improvementImpactList.add(improvementImpact);

        }

        List<Double> electricityConsumptionList = new ArrayList<Double>();

        for (Double impact : improvementImpactList) {
            electricityConsumptionList.add(baseValue-impact);
        }
        this.setValue(electricityConsumptionList);
    }


}
