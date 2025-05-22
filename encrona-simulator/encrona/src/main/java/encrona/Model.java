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

    private Model() {
        throw new IllegalStateException("The model should never be instantiated, only used to run the simulator");
    }

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
    public static Map<String, List<String>> runSimulation(
            Map<Map.Entry<String, String>, Double> mapOfNumericalVariables,
            List<improvement> improvement,
            List<heatingEnergySource> heatingEnergySources) {
        // We instantiate the data

        dataLoader = new DataLoader(mapOfNumericalVariables, improvement, heatingEnergySources);

        Collection<componentAbstract> componentsToCalculate = dataLoader.getAllComponentAbstract();

        for (componentAbstract<?> component : componentsToCalculate) {
            recursiveRun(component);
        }

        Map<String, List<String>> outputLists = new HashMap<>();
        List<String> outputList = new ArrayList<>();
        List<String> clipboardList = new ArrayList<>();

        // This prints the values, for testing purposes
        for (componentAbstract<?> componentAbstract : componentsToCalculate) {

            if (componentAbstract.getName().equals("dashboardString")) {
                clipboardList.add((String) componentAbstract.getValue());
            } else {
                String printString = toStringFunction(componentAbstract);
                if (!printString.equals("")) {
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
    public static String toStringFunction(componentAbstract<?> componentAbstract) {

        StringBuilder stringBuilder = new StringBuilder();

        if (componentAbstract.getValue() instanceof List l) {
            if (!l.isEmpty()) {
                stringBuilder.append(componentAbstract.getName() + " equals : [ ");
                for (Object a : l) {
                    stringBuilder.append(a.toString() + ", ");
                }
                stringBuilder.append(" ]");
            }
        } else {

            if (componentAbstract.getValue() instanceof Object[] o) {
                stringBuilder.append(componentAbstract.getName() + " equals : [ ");
                for (int i = 0; i < (o).length; i++) {
                    stringBuilder.append((o)[i].toString() + ", ");
                }
                stringBuilder.append(" ]");

            } else {
                stringBuilder.append(componentAbstract.getName() + " equals ");
                stringBuilder.append(componentAbstract.getValue().toString());

                if (!(componentAbstract.getUnit().equals("") || componentAbstract.getUnit() == null)) {
                    stringBuilder.append(" " + componentAbstract.getUnit());
                }
            }
        }

        return stringBuilder.toString();
    }

    /**
     * This method recursivly runs components, first running its (not yet completed)
     * dependencies before running itself
     * 
     * @param component The component to run
     */
    public static void recursiveRun(componentAbstract<?> component) {
        Map<String, componentAbstract<?>> dependsOnMap = (Map<String, componentAbstract<?>>) component.getDependsOn();

        if (dependsOnMap != null) {
            for (componentAbstract<?> dependency : dependsOnMap.values()) {
                if (!dependency.getComplete()) {
                    recursiveRun(dependency);
                }
            }
        }
        if (!component.getComplete()) {
            component.run();
        }
    }

}