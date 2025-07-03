# Encrona Simulator

This is a Java-based simulation platform, with a modular architecture, since the intention is that specific aspects can be further developed when they become relevant. 

## System description

Below is a description of the different parts of the system.

### Misc

These are the parts of the system which are unique, such as the Main class, responsible for starting the program, or the Model class, which contains the entire simulation and is responsible for handling it. 

### Components

Component classes represent the parts of the system which are calculated, and/or used to calculate another component. This includes the input values, the intermediate values and the output values. 

Their common values and those common methods whose implementation is static are are defined in the abstract class componentAbstract. The methods whose implementation varies is defined in the interface componentInterface. 

To be specific, the current value is calculated using the specific classes overwritten 'calculate' method, which will use the list of components it is dependent on (which is empty for input variables) to, in some way, set the current value. Afterwards any applicable modifiers will be applied, and then the complete function is called and this component is finished executing. 

### Domain

Domain classes represent the domain-specific data types, as in the specific things which have certain specific properties. 

These generally extend the abstract class objectAbstract, which provides some common values and methods. 

### GUI

This will store the classes related to the simulator GUI (Graphic User Interface), with this using the Swing library to implement a basic user interface to allow a user of the simulator to specify relevant parameters for the simulation, and to view the simulation results in a more user-friendly manner (when compared with the raw output generated for use by the real time estimation system).

### Expert system

These classes are responsible for handling the logic of the expert system component, which is used to generate suggestions about which improvements to implement first. This uses a rules-based methodology, with the rules consiting of a condition and one or two post-conditions, with the former being responsible for verifying that the system model state fulfills certain criteria, while the latter is reponsible for modifying the system model, with this modification depending on if the condition was true or not. 

## How to use

This section will discuss how the system is utilized, both for basic use and how to add new things to the different categories. 

### Basic usage

The system can be utilized by simply running the main method of the Main.java class. This should initiate the GUI, which provides access to all of the systems functionality.

Note that to run it from the development environment you would need Java JDK v.23 and something to handle Maven (Maven being a project management tool, which handles dependencies). Most integrated development environments, e.g. Visual Studio Code, can handle Maven, but the JDK does need to be installed. 

.jar files can be created in a variety of ways, with it being possible to do this using the command line or a tool, this is described in detail in the following link: https://dev.to/rohitk570/create-an-executable-jar-file-on-vs-code-n-command-line-154b 

### Adding or modifying data

The data is currently represented in the code, so any changes made must be done there, with the details depending on the type. 

#### New improvements/heat sources

Adding new improvements or heat sources is quite simple, and only consists of adding a new item to the relevant list in DataLoader.java, under the createInitialListOfImprovements or createInitialListOfHeatSources. Modifying an existing item is done much the same way, simply changing the values for a specific entry in those functions.

#### New rules

Rule modification or creation is done by going to the ReasoningEngine.java class, and specifically the generateRules function. 

#### New components/variables in the simulation

This is a bit more complex than the previous examples, since it consists of creating a new class which extends the componentAbstract.java class, much like ones in the output folder. One then needs to define a constructor and calculate method, much like the existing examples. Then one needs to instantiate it in the DataLoader.java class, which includes adding the components the newly one depends on to a new dependsOn map, and then adding it to the components map (if it is to be shown as an output). 

#### New input

A new input depends on if the data type is already covered by the existing types (as of writing this, the types covered are Double, Improvement and Heat Source). If it is not covered, one needs to, much like the current methods, add the ability to provide that input in the GUI, collect the values from the GUI, and then add the input to the methods, such that the data goes from the GUI to the DataLoaders createComponents method. 

A new input is then defined by creating an instance with the relevant type, which can then be used in the dependsOn maps of the different components.