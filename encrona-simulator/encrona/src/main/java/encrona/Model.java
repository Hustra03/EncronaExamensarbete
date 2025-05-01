package encrona;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import encrona.components.componentAbstract;
import encrona.domain.heatingEnergySource;
import encrona.domain.improvement;

/**
 * The Model class is reponsible holding the simulation itself, and is the
 * "main" class responsible for the simulation
 */
public class Model {
    static DataLoader dataLoader;

    /**
     * This method is responsible for running the simulation, which consists of
     * instantiating relevant data, scheduling tasks, executing tasks and then
     * presenting results
     * 
     * @param mapOfNumericalVariables A map of numerical variables, in the format
     *                                <<name,unit>,value>
     * @param improvement             The list of improvements to implement
     * @param heatingEnergySources    The list of heat sources
     * @return A map of string lists, which contains different types of output
     */
    public static Map<String, List<String>> runSimulation(Map<Map.Entry<String, String>, Double> mapOfNumericalVariables,
            List<improvement> improvement,
            List<heatingEnergySource> heatingEnergySources) {
        // We instantiate the data
        // TODO change it so it only loads data relevant for the current simulation, if
        // the data grows sufficently large

        dataLoader = new DataLoader(mapOfNumericalVariables, improvement, heatingEnergySources);

        Collection<componentAbstract> componentsToCalculate = dataLoader.getAllComponentAbstract();

        for (componentAbstract component : componentsToCalculate) {
            recursiveRun(component);
        }

        Map<String, List<String>> outputLists = new HashMap<String, List<String>>();
        List<String> outputList = new ArrayList<String>();
        List<String> clipboardList = new ArrayList<String>();

        // This prints the values, for testing purposes
        for (componentAbstract componentAbstract : componentsToCalculate) {

            if (componentAbstract.getName().equals("dashboardString")) {
                clipboardList.add((String)componentAbstract.getValue());
            } else {
                String printString = toStringFunction(componentAbstract);
                if (printString != "") {
                    outputList.add(printString);
                }
            }
        }
        outputLists.put("clipboard", clipboardList);
        outputLists.put("output", outputList);
        return outputLists;
    }

    /**
     * This method creates a string for the value of an componentAbstract, whose
     * value consists of a list or values to a string
     * 
     * @param componentAbstract The component to generate a value string for
     * @return The components value string
     */
    public static String toStringFunction(componentAbstract componentAbstract) {
        String printString = "";

        if (componentAbstract.getValue() instanceof java.util.List) {
            if (((List) componentAbstract.getValue()).size() != 0) {
                printString += (componentAbstract.getName() + " equals : [ ");

                ((List) componentAbstract.getValue()).toString();

                for (Object a : (List) componentAbstract.getValue()) {
                    printString += (a.toString() + ", ");
                }
                printString += (" ]");
            }
        } else {

            if (componentAbstract.getValue() instanceof Object[]) {
                printString += (componentAbstract.getName() + " equals : [ ");

                for (int i = 0; i < ((Object[]) componentAbstract.getValue()).length; i++) {
                    printString += (((Object[]) componentAbstract.getValue())[i].toString() + ", ");
                }
                printString += (" ]");

            } else {

                printString += (componentAbstract.getName() + " equals ");
                printString += (componentAbstract.getValue().toString());

                if (!(componentAbstract.getUnit().equals("") || componentAbstract.getUnit() == null)) {
                    printString += (" " + componentAbstract.getUnit());
                }
            }
        }

        return printString;
    }

    /**
     * This method recursivly adds components to the execution set, first adding its
     * dependencies before adding itself
     * TODO Note that we here recursivly run the dependencies, which we may want to
     * change to multi-threaded?
     * 
     * @param component The component to add
     */
    public static void recursiveRun(componentAbstract component) {
        Map<String, componentAbstract> dependsOnMap = (Map<String, componentAbstract>) component.getDependsOn();

        if (dependsOnMap != null) {
            for (componentAbstract dependency : dependsOnMap.values()) {
                if (!dependency.getComplete()) {
                    recursiveRun(dependency);
                }
            }
        }
        if (!component.getComplete()) {
            component.run();
            return;
        }
    }

}