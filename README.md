# BEAPI Tool: Efficient Bounded Exhaustive Input Generation from Program APIs

*BEAPI* is an efficient bounded exhaustive test generation tool that employs routines from the **API** of the software under test for generation. *BEAPI* solely employs the public methods in the **API** of a class in order to perform bounded exhaustive input generation for the class, **without** the need for a formal specification of valid inputs. 


*BEAPI* takes as inputs the target classes for test case generation, a configuration files defining the scopes, and the builder methods for the target class (a sufficient set of builder methods that is, methods from the **API** that by themselves are enough to produce all the feasible objects for the target classes). As outputs, *BEAPI* tool yields:
- a bounded exhaustive set of objects,
- a **JUnit**  test suite with the method sequences produced by *BEAPI* to create each object in the result set, and
- a separate **Junit** test suite with tests revealing errors (if these have been found) in the methods used for generation.

The BEAPI tool is a command line tool for UNIX-based operating systems. The tool is implemented on top of **Randoop**’s infrastructure [0], replacing random test sequence generation by bounded exhaustive generation.

# Table of Contents

- [Getting Started](#gettingstarted)
    - [Installing BEAPI](#installingbeapi)
    - [Running example](#example)
- [Command Line Options](commandsLine.md)
- [Tutorial](tutorial.md)
    - [Using BEAPI](tutorial.md) 
- [How to build BEAPI from source code](buildBEAPI.md)


<a name="gettingStarted"></a>
## Getting Started

<a name="installingbeapi"></a>
### Installing BEAPI

To install  *BEAPI* using docker:

1. Clone the repository:

```
git clone https://github.com/mpolitano/bounded-exhaustive-api
```
2. Move to the root folder:

```bash
cd bounded-exhaustive-api
```

3. Build a docker container:

```
docker build -t beapi .

```

4. Run the docker container:

```
docker run -it beapi:latest /bin/bash
```

For Install in macOS running on Apple CPU follow the instructions [here](appleCPUInstall.md)


**Note**: To make a local installation and compile from sources follow the instructions in 
[How to build BEAPI from source code](buildBEAPI.md) section. 





<a name="example"></a>
### Running example

We provide, as an example, an implementation of ``NodeCachingLinkedList`` in folder ```example```. In order to generate objects and test for a class  we must to compile it:


```bash
cd examples
mkdir -p ./bin
javac -d ./bin/ org/apache/commons/collections4/list/NodeCachingLinkedList.java
```
To run *BEAPI* for ```NodeCachingLinkedList``` with scope 3:


```bash
cd ..
./run-beapi.sh -cp=./examples/bin/ -c=org.apache.commons.collections4.list.NodeCachingLinkedList -l=literals/literals3.txt -b=properties/scope3.all.canonicalizer.properties -m="org.apache.commons.collections4.list.NodeCachingLinkedList.<init>\(int\)|org.apache.commons.collections4.list.AbstractLinkedList.add\(java.lang.Integer\)|org.apache.commons.collections4.list.AbstractLinkedList.clear\(\)|org.apache.commons.collections4.list.AbstractLinkedList.remove\(int\)" -s=objects.ser -d=beapi-tests -p=org.apache.commons.collections4.list
```

The scope (3  in this example) is defined via two provided configuration files: 

- ```literals/literals3.txt```
- ```properties/scope3.all.canonicalizer.properties```

See [Tutorial](tutorial.md) section for more details on these files


Generated tests will be saved in ```./beapi-tests/org/apache/commons/collections4/list```

Generated objects will be saved in the specified file: ```./objects.ser```


* * *
[0] https://randoop.github.io/randoop/
* * *

Go back to [Table of Contents](#table-of-contents)



