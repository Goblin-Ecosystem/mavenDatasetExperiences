# Maven Dataset And Weaver Experiences
[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](LICENSE.txt)

This project shows an example of usage of the Maven Central combined with its query enrichers called "Weaver".

The Maven Central miner used to generate the Maven dependency graph is available here: https://github.com/Goblin-Ecosystem/goblinDependencyMiner.  
The weaver used to enrich queries is available here: https://github.com/Goblin-Ecosystem/goblinWeaver.  
A Zenodo archive that contains the associated dataset dump and the Weaver jar is available here: https://zenodo.org/records/10291589.

## Contents
- **experiences** folder contains the "exp1.pom.xml" used for the experiments and the "executionOutput.txt" contains the outputs of the experiments.
  - **experiences/compareLib** folder contains the data (.csv) created with the "LIBRARY COMPARE" experiment, the Python scripts used to analyze the results and the images generated with these scripts.
  - **experiences/ecosystem** folder contains the data (.csv) created with the "ECOSYSTEM" experiment, the Python scripts used to analyze the results and the images generated with these scripts.
- **src/java/experiences** contains the three experiments:
  - ExpArtifactCompare: Compares two libraries on the trend and number of CVEs (including transitive) in their last twenty versions.
  - ExpEcosystem: Answer these two questions: "How many of the whole releases on Maven Central contain at least one direct CVEs" and "How many of the latest releases of each artifact contain at least one CVE".
  - ExpSpeedClient: Speed client side, determine the average, max and min release speed of my dependencies so that I know the best time to update everything.
  - ExpSpeedProvider: Speed provider side, determine the average, max and min release speed of artifacts who depend on me so that I know the best time to provide my updates.
  - ExpUpdate: Retrieve a project's ego-centric dependency tree with CVEs information and for packages with CVEs, retrieve later versions without CVEs.

## Requirements
- Java 17
- Maven, with MAVEN_HOME defines
- A Neo4j database containing the Maven Central dependency graph (dump for Neo4j 4.x here: https://zenodo.org/records/10291589).
- The weaver jar available here: https://zenodo.org/records/10291589

## Execution
As the ecosystem experiences are run on the whole Maven ecosystem we need to increase the RAM allocated to Neo4j to 3GB.

1. Edit the neo4j.conf file of your Neo4j database containing the Maven Central graph to increase the heap max size:
> dbms.memory.heap.max_size=3G
2. Launch the Neo4j database containing the Maven Central graph.
3. Launch the Weaver jar with the command:
> java -Dneo4jUri="bolt://localhost:7687/" -Dneo4jUser="neo4j" -Dneo4jPassword="password" -jar /path/to/goblinWeaver-1.0.0.jar
4. Run the project:
> mvn clean install exec:java

## Licensing
Copyright 2023 SAP SE or an SAP affiliate company and Maven Dataset And Weaver Experience. Please see our [LICENSE](LICENSE) for copyright and license information.