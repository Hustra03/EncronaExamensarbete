package encrona.components.output;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import encrona.components.componentAbstract;
import encrona.domain.improvement;
import encrona.modifiers.modifierAbstract;

/**
 * This creates a list for the impact every individual improvement will have per year in kwh
 */
public class improvementImpact extends componentAbstract<List<Map.Entry<improvement,Double>>>{

    /**
     * This is a constructor for improvementImpact 
     * @param name The name of this output
     * @param unit The unit of this output
     * @param dependsOn the components this component depends on
     * @param modifiers the modifiers which should be applied to this component
     */
    public improvementImpact(String name, String unit, Map<String,componentAbstract> dependsOn, List<modifierAbstract<List<Entry<improvement, Double>>>> modifiers)
    {   this.setName(name);
        this.setUnit(unit);
        this.setDependsOn(dependsOn);
        this.setModifiers(modifiers);
    }

    @Override
    public void calculate() throws Exception {

        Map<String,componentAbstract> dependsOnMap = getDependsOn();

        Integer aTemp = (Integer)dependsOnMap.get("aTempInput").getValue();
        List<improvement> improvements=(List<improvement>)dependsOnMap.get("improvements").getValue();

        List<Map.Entry<improvement,Double>> improvmentImpact=new ArrayList<Map.Entry<improvement,Double>>();

        for (improvement improvement : improvements) {
            //We get kwh per m2 * m2 / years of service = yearly kwh impact
            Double impact = (improvement.getKwhPerM2()*aTemp)/improvement.getYearsOfService();
            Entry<improvement,Double> entry = new AbstractMap.SimpleEntry<improvement, Double>((improvement)improvement,impact );
            improvmentImpact.add(entry);
        }

        this.setValue(improvmentImpact);
    }
    
}
