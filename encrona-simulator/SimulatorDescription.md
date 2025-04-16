# Encrona Simulator

This is a Java-based simulation platform, with a modular architecture, since the intention is that specific aspects can be further developed when they become relevant. 

Below is a description of the different parts of the system

## Misc

These are the parts of the system which are unique, such as the Main class, responsible for starting the program, or the Model class, which contains the entire simulation and is responsible for handling it. 

## Components

Component classes represent the parts of the system which are calculated, and/or used to calculate another component. This includes the input values, the intermediate values and the output values. 

Their common values and those common methods whose implementation is static are are defined in the abstract class componentAbstract. The methods whose implementation varies is defined in the interface componentInterface. 

To be specific, the current value is calculated using the specific classes overwritten 'calculate' method, which will use the list of components it is dependent on (which is empty for input variables) to, in some way, set the current value. Afterwards any applicable modifiers will be applied, and then the complete function is called and this component is finished executing. 

## Domain

Domain classes represent the domain-specific data types, as in the specific things which have certain specific properties. 

These generally extend the abstract class objectAbstract, which provides some common values and methods. 

## GUI

This will store the classes related to the simulator GUI (Graphic User Interface), with this using the Swing library to implement a basic user interface to allow a user of the simulator to specify relevant parameters for the simulation, and to view the simulation results in a more user-friendly manner (when compared with the raw output generated for use by the real time estimation system).

## Modifiers

Modifier classes represent the part of the system that impacts another part, specifically one or more components. 

Their common values and those common methods whose implementation is static are are defined in the abstract class modifierAbstract. The methods whose implementation varies is defined in the interface modifierAbstract. 

Each modifier will implement the 'modify' function, which will accept a value of a specific data type and then return a value of that same data type, with the details being implementation specific. 