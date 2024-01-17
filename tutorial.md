## Tutorial

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
* * *

Go back to [Table of Contents](README.md)

