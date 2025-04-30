package encrona.expertSystem;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import encrona.DataLoader;
import encrona.domain.heatingEnergySource;
import encrona.domain.improvement;

/**
 * This class is used to contain the relevant objects for the expert system system state
 */
public class ExpertSystemModel {

    private Map<Map.Entry<String, String>, Double> numericalValues;
    private java.util.List<heatingEnergySource> heatingEnergySources;
    private List<Map.Entry<improvement,Integer>> sortedListOfImprovementsToConsider; //Note that the list is not sorted during operations, but it will be sorted when it is retrived using get
    private Comparator<Map.Entry<improvement,Integer>> comparator = (p1, p2) ->  p2.getValue()-p1.getValue();
    
    public ExpertSystemModel(Map<Map.Entry<String, String>, Double> numericalValues,java.util.List<heatingEnergySource> heatingEnergySources)
    {
        //This is a simple comparator, which switches elements if the second is larger (Note that a priority queue sorts smallest to largests by default)
        sortedListOfImprovementsToConsider=new ArrayList<Map.Entry<improvement,Integer>>();
        this.numericalValues=numericalValues;
        this.heatingEnergySources=heatingEnergySources;
        populateImprovementList();
    }

    /**
     * This is a getter for the priority list
     * @return The current priority list
     */
    public List<Map.Entry<improvement,Integer>> getSortedListOfImprovementsToConsider()
    {        
        sortedListOfImprovementsToConsider.sort(comparator);
        return this.sortedListOfImprovementsToConsider;
    }

    public void populateImprovementList()
    {
        List<improvement> improvements=DataLoader.createInitialListOfImprovements();
        
        Entry<improvement,Integer> entry = new AbstractMap.SimpleEntry<improvement, Integer>((improvement)improvements.get(0),100 );
        Entry<improvement,Integer> entry2 = new AbstractMap.SimpleEntry<improvement, Integer>((improvement)improvements.get(1),50 );

        sortedListOfImprovementsToConsider.add(entry);
        sortedListOfImprovementsToConsider.add(entry2);
    }

}
