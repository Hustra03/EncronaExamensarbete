package encrona.expertSystem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import encrona.domain.improvement;

/**
 * This is the "main" class of the expert system, and is what is called, and what is responsible for setting up the system environment
 */
public class ReasoningEngine {
    
    private ExpertSystemModel model;
    private List<Rule> rules;

    public ReasoningEngine()
    {
        this.rules=new ArrayList<Rule>();
        this.model = new ExpertSystemModel();
        generateRules();
    }

    /**
     * This method generates the rules the expert system will use
     * How to use lambda expressions https://stackoverflow.com/questions/13604703/how-do-i-define-a-method-which-takes-a-lambda-as-a-parameter-in-java-8
     */
    private void generateRules()
    {
        Condition firstCondition = (model) -> {
            return true;
        };
        PostCondition firstPostCondition =(model)->
        {
            model.sortedListOfImprovementsToConsider.forEach((item)->{

                if (item.getKey().getName().equals("Berg Or Mark Heating")) {
                    item.setValue(item.getValue()-100);
                }

            });
        };
        Rule exampleRule = new Rule("First condition", firstCondition, firstPostCondition, null);
        rules.add(exampleRule);
    }

    public String recommendations()
    {

        String recommendationString="";

        for (Rule rule : rules) {
            rule.testRule(model);
        }

        for (Entry<improvement,Integer> queueValue : model.getSortedListOfImprovementsToConsider()) {
            recommendationString+=" " +queueValue.getKey().toString()+" = " +queueValue.getValue() +" score." +System.lineSeparator(); 
        }        
        System.out.println(recommendationString);

        return recommendationString;
    }
}
