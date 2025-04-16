package encrona;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
     * @param mapOfNumericalVariables A map of numerical variables, in the format <<name,unit>,value>
     * @param improvement          The list of improvements to implement
     * @param heatingEnergySources The list of heat sources
     * @return A list of the different components final values in a standard string
     *         format
     */
    public static List<String> runSimulation(Map<Map.Entry<String,String>,Double> mapOfNumericalVariables,List<improvement> improvement,
            List<heatingEnergySource> heatingEnergySources) {
        // We instantiate the data
        // TODO change it so it only loads data relevant for the current simulation, if
        // the data grows sufficently large

        dataLoader = new DataLoader(mapOfNumericalVariables,improvement, heatingEnergySources);

        Collection<componentAbstract> componentsToCalculate = dataLoader.getAllComponentAbstract();

        for (componentAbstract component : componentsToCalculate) {
            recursiveRun(component);
        }

        List<String> outputList = new ArrayList<String>();

        // This gives the values, for testing purposes
        for (componentAbstract componentAbstract : componentsToCalculate) {

            String printString = toStringFunction(componentAbstract);
            if (printString!="") {
            outputList.add(printString);
            System.out.println(printString);
            }
        }
        return outputList;
    }

    /**
     * This method creates a string for the value of an componentAbstract, whose value consists of a list or values to a string
     * @param componentAbstract The component to generate a value string for
     * @return The components value string
     */
    public static String toStringFunction(componentAbstract componentAbstract) {
        String printString = "";

        if (componentAbstract.getValue() instanceof java.util.List) {
            if (((List)componentAbstract.getValue()).size()!=0) {
            printString+=(componentAbstract.getName() + " equals : [ ");

            ((List) componentAbstract.getValue()).toString();

            for (Object a : (List) componentAbstract.getValue()) {
                printString+=(a.toString()+", ");
            }
            printString+=(" ]");
        }
        } else {

            if (componentAbstract.getValue() instanceof Object[]) {
                printString+=(componentAbstract.getName() + " equals : [ ");

                for (int i = 0; i < ((Object[])componentAbstract.getValue()).length; i++) {
                    printString+=(((Object[])componentAbstract.getValue())[i].toString()+", ");
                }
                printString+=(" ]");

            }
            else
            {

            
            printString+=(componentAbstract.getName() + " equals ");
            printString+=(componentAbstract.getValue().toString());

            if (!(componentAbstract.getUnit().equals("") || componentAbstract.getUnit() == null)) {
                printString+=(" " + componentAbstract.getUnit());
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