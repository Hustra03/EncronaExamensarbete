package encrona;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import encrona.components.componentAbstract;

/**
 * The Model class is reponsible holding the simulation itself, and is the
 * "main" class responsible for the simulation
 */
public class Model {
    static ExecutorService service;
    static DataLoader dataLoader;
    static LinkedHashSet<componentAbstract> executionSet;

    /**
     * This method is responsible for running the simulation, which consists of
     * instantiating relevant data, scheduling tasks, executing tasks and then
     * presenting results
     * Relevant links:
     * https://stackoverflow.com/questions/8767527/how-to-use-thread-pool-concept-in-java
     * https://docs.oracle.com/javase/8/docs/api/java/lang/Runnable.html
     * https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ThreadPoolExecutor.html
     */
    public static void runSimulation() {
        // We instantiate the data
        // TODO change it so it only loads data relevant for the current simulation, if
        // the data grows sufficently large

        dataLoader = new DataLoader();

        // We create the thread pool
        // TODO change to paralell execution if that is faster, issues with dependencies
        // however since we would need to order execution, single-threaded may be faster
        // depending on the size of the tasks
        // service = Executors.newFixedThreadPool(4); //This creates the thread pool,
        // which is responsible for handling the execution

        // We then create the execution ordering
        // Note that we use a LinkedHashSet to both ensure insertion order and that only
        // unique elements are added
        // This allows us to recursivly add elements, adding dependencies when needed,
        // without risking running elements twice

        Collection<componentAbstract> componentsToCalculate = dataLoader.getAllComponentAbstract();

        executionSet = new LinkedHashSet<componentAbstract>();
        for (componentAbstract component : componentsToCalculate) {
            recursiveRun(component);
        }

        // This gives the values, for testing purposes
        for (componentAbstract componentAbstract : componentsToCalculate) {
            System.out.println(componentAbstract.getName() + " equals " + componentAbstract.getValue() + " " + componentAbstract.getUnit());
        }
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
        Map<String, componentAbstract> dependsOnMap = component.getDependsOn();

        if (dependsOnMap != null) {

            Collection<componentAbstract> dependsOnCollection = dependsOnMap.values();

            for (componentAbstract dependency : dependsOnCollection) {
                if (!dependency.getComplete()) {
                    recursiveRun(dependency);
                }
            }
        }
        component.run();
    }

}