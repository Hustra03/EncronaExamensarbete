package encrona.expertSystem;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import encrona.DataLoader;
import encrona.components.input;
import encrona.domain.heatingEnergySource;
import encrona.domain.improvement;

/**
 * This class is used to contain the relevant objects for the expert system system state
 */
public class ExpertSystemModel {

    private Map<Map.Entry<String, String>, Double> numericalValues;
    private java.util.List<heatingEnergySource> heatingEnergySources;
    private Map<String,input<?>> expertSystemInput;
    private List<Map.Entry<improvement,Integer>> sortedListOfImprovementsToConsider; //Note that the list is not always sorted, but it will be sorted when it is retrived using the getter
    private Comparator<Map.Entry<improvement,Integer>> comparator = (p1, p2) ->  p2.getValue()-p1.getValue();
    
    public ExpertSystemModel(Map<Map.Entry<String, String>, Double> numericalValues,java.util.List<heatingEnergySource> heatingEnergySources,Map<String,input<?>> expertSystemInput)
    {
        //This is a simple comparator, which switches elements if the second is larger (Note that a priority queue sorts smallest to largests by default)
        sortedListOfImprovementsToConsider=new ArrayList<>();
        this.numericalValues=numericalValues;
        this.heatingEnergySources=heatingEnergySources;
        this.expertSystemInput=expertSystemInput;
        populateImprovementList();
    }

    /**
     * This is a getter for the priority list, which also sorts the list before returning it
     * @return The current priority list
     */
    public List<Map.Entry<improvement,Integer>> getSortedListOfImprovementsToConsider()
    {        
        sortedListOfImprovementsToConsider.sort(comparator);
        return this.sortedListOfImprovementsToConsider;
    }

    /**
     * This is a getter for the numericalValues attribute
     * @return The current numericalValues
     */
    public Map<Map.Entry<String, String>, Double> getNumericalValues()
    {        
        return this.numericalValues;
    }

    /**
     * This is a getter for the heatingEnergySources attribute
     * @return The current heatingEnergySources
     */
    public java.util.List<heatingEnergySource> getHeatingEnergySources()
    {        
        return this.heatingEnergySources;
    }

    /**
     * This is a getter for the expertSystemInput attribute
     * @return The current expertSystemInput
     */
    public Map<String,input<?>> getExpertSystemInput()
    {        
        return this.expertSystemInput;
    }

    /**
     * This function is used to generate a list of improvements for the expert system to consider, along with their initial priority
     */
    public void populateImprovementList()
    {
        List<improvement> improvements=DataLoader.createInitialListOfImprovements();
        
        for (improvement improvement : improvements) {
            sortedListOfImprovementsToConsider.add(new AbstractMap.SimpleEntry<>(improvement,0));
        }
    }


}
