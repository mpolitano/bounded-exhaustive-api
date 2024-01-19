# BEAPI Tool: Efficient Bounded Exhaustive Input Generation from Program APIs

**BEAPI** is an efficient bounded exhaustive test generation tool that employs routines from the *API* of the software under test for generation. **BEAPI** solely employs the public methods in the *API* of a class in order to perform bounded exhaustive input generation for the class, **without** the need for a formal specification of valid inputs. 


**BEAPI** takes as inputs the target classes for test case generation, configuration files defining the scopes, and the methods from the *API* that will be used for generation (*builder methods* in the paper describing **BEAPI** [0]). As outputs, **BEAPI** yields:
- a bounded exhaustive set of objects,
- a **JUnit**  test suite with the method sequences produced by **BEAPI** to create each object in the result set, and
- a separate **Junit** test suite with tests revealing errors (if these have been found) in the methods used for generation.

**BEAPI** is a command line tool that runs on a Docker container. The tool is implemented on top of **Randoop**’s infrastructure [1], replacing random test sequence generation by bounded exhaustive generation. For a detailed explanation of the algorithms underlying **BEAPI** read the scientific paper [0].


# Table of Contents

- [Getting Started](#getting-started)
    - [Installing BEAPI](#installing-beapi)
    - [Running a simple example](#running-a-simple-example)
- [Tutorial](TUTORIAL.md)
- [Command Line Options](OPTIONS.md)
- [References](#references)

## Getting Started

### Installing BEAPI

To install **BEAPI** using docker:

1. Clone the repository:

```
git clone https://github.com/mpolitano/bounded-exhaustive-api
cd bounded-exhaustive-api
```

2. Build **BEAPI**'s docker container:

```
docker build -t beapi .
```

3. Run the container:

```
docker run -it beapi:latest /bin/bash
```

**Note**: To make a local installation and compile from sources follow the instructions [here](ALTERNATIVE_INSTALL.md).


### Running a simple example

1. Compile the `NodeCachingLinkedList` (taken from Apache Commons [2]) java class provided in the `examples` folder.

    ```
    cd examples
    mkdir -p ./bin
    javac -d ./bin/ org/apache/commons/collections4/list/NodeCachingLinkedList.java
    cd ..
    ```
2. Run **BEAPI** to generate tests for the ```NodeCachingLinkedList``` class, using a scope of 3:

    ```
    ./run-beapi.sh -cp=./examples/bin/ -c=org.apache.commons.collections4.list.NodeCachingLinkedList -l=literals/literals3.txt -b=properties/scope3.all.canonicalizer.properties -m=examples/config_builders/org.apache.commons.collections4.list.NodeCachingLinkedList -s=objects.ser
    ```

    where, `-cp` defines the classpath, `-c` the target class for test generation, `-l` and `-b` define the scopes (a scope of 3 is defined via the two provided configuration files above), `-m` is a file containing regular expressions for the methods used in the generation, and `-s` is the file where the generated objects will be stored.

    The generated tests will be saved in files `RegressionTest0.java` and `RegressionTest.java`, and the objects in `objects.ser`, within the current directory.

    See the [tutorial](TUTORIAL.md) for an in depth explanation of how to use **BEAPI**, and [this section](OPTIONS.md) for the available configuration options.



* * *
## References 

[0] M. Politano, V. Bengolea, F. Molina, N. Aguirre, M. Frias, P. Ponzio: Efficient Bounded Exhaustive Input Generation from Program APIs. FASE 2023. 111-132.

[1] https://randoop.github.io/randoop/

[2] https://github.com/apache/commons-collections
* * *
Go back to [Table of Contents](#table-of-contents)



