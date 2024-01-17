# BEAPI Tool: Efficient Bounded Exhaustive Input Generation from Program APIs

*BEAPI* is an efficient bounded exhaustive test generation tool that employs routines from the **API** of the software under test for generation. *BEAPI* solely employs the public methods in the **API** of a class in order to perform bounded exhaustive input generation for the class, **without** the need for a formal specification of valid inputs. 


*BEAPI* takes as inputs the target classes for test case generation, a configuration files defining the scopes, and the builder methods for the target class (a sufficient set of builder methods that is, methods from the **API** that by themselves are enough to produce all the feasible objects for the target classes). As outputs, *BEAPI* tool yields:
- a bounded exhaustive set of objects,
- a **JUnit**  test suite with the method sequences produced by *BEAPI* to create each object in the result set, and
- a separate **Junit** test suite with tests revealing errors (if these have been found) in the methods used for generation.

The BEAPI tool is a command line tool for UNIX-based operating systems. The tool is implemented on top of **Randoop**’s infrastructure, replacing random test sequence generation by bounded exhaustive generation.

# Table of Contents

- [Getting Started](#gettingstarted)
    - [Installing BEAPI](#installingbeapi)
    - [Running example](#example)
- [Command Line Options](commandsLine.md)
- [Tutorial](#tutorial)
    - [Using BEAPI](#usingbeapi) 
- [How to build BEAPI from source code](#compile)


<a name="gettingStarted"></a>
## Getting Started

To compile and use the BEAPI please use Java 8.

<a name="installingbeapi"></a>
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


<a name="example"></a>
### Running example

We provide as an example an implementation of NodeCachingLinkedList in folder ```example```. In order to generate objects and test for a class (or classes) we must to compile it:


```bash
cd examples
mkdir -p ./bin
javac -d ./bin/ org/apache/commons/collections4/list/NodeCachingLinkedList.java
```
To run *BEAPI* for ```NodeCachingLinkedList``` with scope 3:

```
cd ..
```

```
java -cp libs/randoop-all-3.0.6.jar:./examples/bin/ randoop.main.Main gentests --testclass=org.apache.commons.collections4.list.NodeCachingLinkedList --literals-file=literals/literals3.txt --canonicalizer-cfg=properties/scope3.all.canonicalizer.properties --builder-methods="org.apache.commons.collections4.list.NodeCachingLinkedList.<init>\(int\)|org.apache.commons.collections4.list.AbstractLinkedList.add\(java.lang.Integer\)|org.apache.commons.collections4.list.AbstractLinkedList.remove\(int\)" --serialize-objects=objects.ser
```

The scope (3  in this example) is defined via two provided configuration files: 

- ```literals/literals3.txt```
- ```properties/scope3.all.canonicalizer.properties```

See [BEAPI tutorial](#tutorial) section for more details on these files


Generated tests and objects are saved in ```name of folder```




<a name="tutorial"></a>
## BEAPI Tutorial

<a name="usingbeapi"></a>
### Using BEAPI

Before running your own examples and start generating new objects and test, keep in mind that BEAPI requires:

- Compiled target class (or classes) for test case generation,
- A file containing primitives:

	When an API method takes a primitive-typed parameter, BEAPI will invoke the method once with each primitive 	value defined in primitive scopes. One may also specify primitive values for primitive types like int, floats,   doubles and strings, by describing their values by extension. We provide some examples in folder ```literals```
 The format of this file is inherited from Randoop:

    Literals for ```int``` primitive type (scope 3):

``` 
START CLASSLITERALS
CLASSNAME
java.lang.Integer
LITERALS
int:0
int:1
int:2
END CLASSLITERALS
```


- A file containing objects scope:

  defines the maximum allowed size for objects. For instance, for the case of ```NodeCachingLinkedList``` on the running example, it was defined as 3, thus, method sequences that create lists with more than  3 nodes will be discarded (together with the objects created by the execution of the sequence). 
   Additionally, options are provided to define the maximum size of the created arrays (``max.array.objects``) and fields that must be omitted when canonicalizing (``omit.fields`` parameter is a Java regular expression). We provide some examples in folder ```properties```


    Object scopes for 3:


```
max.objects=3
max.array.objects=3
omit.fields=modCount|ALLOWED_IMBALANCE
```


- Builder methods:


    file containing the signatures of the builder methods that BEAPI will use for generation. For instance, the contents of the file with the signatures of the builders for NodeCachingLinkedList is:
     
```
ncl.NodeCachingLinkedList.<init>\(\)
ncl.NodeCachingLinkedList.addLast\(int\)
ncl.NodeCachingLinkedList.removeIndex\(int\)
```

<a name="compile"></a>
## How to build BEAPI from source code
To compile  *BEAPI* clone the repository:

```
git clone https://github.com/mpolitano/bounded-exhaustive-api

```
Move to the folder:

```
cd bounded-exhaustive-api
```


To compile and generate .jar (libs/randoop-all-3.0.6.jar)  we provide a ```Gradle``` wrapper script, gradlew:


```
./gradlew singleJar
```

