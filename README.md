# BEAPI Tool: Efficient Bounded Exhaustive Input Generation from Program APIs

*BEAPI* is an efficient bounded exhaustive test generation tool that employs routines from the **API** of the software under test for generation. *BEAPI* solely employs the public methods in the **API** of a class in order to perform bounded exhaustive input generation for the class, **without** the need for a formal specification of valid inputs. 


*BEAPI* takes as inputs the target classes for test case generation, a configuration files defining the scopes, and the builder methods for the target class (a sufficient set of builder methods that is, methods from the **API** that by themselves are enough to produce all the feasible objects for the target classes). As outputs, *BEAPI* tool yields:
- a bounded exhaustive set of objects,
- a **JUnit**  test suite with the method sequences produced by *BEAPI* to create each object in the result set, and
- a separate **Junit** test suite with tests revealing errors (if these have been found) in the methods used for generation.

The BEAPI tool is a command line tool for UNIX-based operating systems. The tool is implemented on top of **Randoop**’s infrastructure, replacing random test sequence generation by bounded exhaustive generation.

# Table of Contents

- [Getting Started](#getting-started)
    - Installing BEAPI
    - Running example
    - Command Line Options
- BEAPI Tutorial
    - Using BEAPI 
- [How to build BEAPI from source code](#how-to-build-beapi-from-source-code)


## Getting Started

To compile and use the BEAPI please use Java 8.


### Installing BEAPI

To use  *BEAPI* clone the repository:

```
git clone https://github.com/mpolitano/bounded-exhaustive-api
```
Move to the folder:

```
cd bounded-exhaustive-api
```
For your convenience we provide a  binary distribution of beapi tool ready to use in ```lib``` folder (```beapi.jar```). To compile and generate  jar file from source code, also provided in this repository, see section [How to build BEAPI from source code](#compile) below.


### Running example

. . .
### Command Line Options
. . .
## BEAPI Tutorial

### Using BEAPI

Before running your own examples and start generating new objects and test, keep in mind that BEAPI requires:

- 
-
-
<a name="compile"></a>
## How to build BEAPI from source code

To compile and generate .jar (libs/randoop-all-3.0.6.jar)  we provide a ```Gradle``` wrapper script, gradlew:

```
./gradlew singleJar
```

