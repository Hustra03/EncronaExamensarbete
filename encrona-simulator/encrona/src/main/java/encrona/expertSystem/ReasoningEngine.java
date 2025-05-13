package encrona.expertSystem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import encrona.domain.heatingEnergySource;
import encrona.domain.improvement;

/**
 * This is the "main" class of the expert system, and is what is called, and what is responsible for setting up the system environment
 */
public class ReasoningEngine {
    
    private ExpertSystemModel model;
    private List<Rule> rules;
    private List<Rule> triggeredRules;

    public ReasoningEngine(Map<Map.Entry<String, String>, Double> numericalValues,java.util.List<heatingEnergySource> heatingEnergySources )
    {
        this.rules=new ArrayList<>();
        this.model = new ExpertSystemModel(numericalValues,heatingEnergySources);
        generateRules();
    }

    /**
     * This method generates the rules the expert system will use
     * How to use lambda expressions https://stackoverflow.com/questions/13604703/how-do-i-define-a-method-which-takes-a-lambda-as-a-parameter-in-java-8
     */
    private void generateRules()
    {
        Condition firstCondition = (lambdaModel) -> {
            return true;
        };
        PostCondition firstPostCondition = (lambdaModel)->
        {
            lambdaModel.getSortedListOfImprovementsToConsider().forEach((item)->{

                if (item.getKey().getName().equals("Berg Or Mark värme")) {
                    item.setValue(item.getValue()-100);
                }

            });
        };
        Rule exampleRule = new Rule("Basic static rule","It decreases the priority of Berg or Mark Heating", firstCondition, firstPostCondition, null);
        rules.add(exampleRule);
    }

    /**
     * This method is called to start the expert system execution, and will return a sorted list of improvements (which is the current recommendation)
     * @return A a sorted list of improvements as a list
     */
    public List<String> recommendations()
    {

        List<String> recommendationStringList=new ArrayList<>();

        triggeredRules=new ArrayList<>();
        for (Rule rule : rules) {
            if (rule.testRule(model)) {
                triggeredRules.add(rule);
            }
        }
        for (Entry<improvement,Integer> queueValue : model.getSortedListOfImprovementsToConsider()) {
            recommendationStringList.add(" " +queueValue.getKey().toString()+" = " +queueValue.getValue() +" score."); 
            
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
