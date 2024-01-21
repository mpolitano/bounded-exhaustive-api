## Tutorial

To perform test generation BEAPI requires:

**Binaries:** (.class files) of the target class (or classes).

**Literals:** A file containing the literals that will be used to instantiate primitive-typed parameters of API methods. The format of the file is inherited from Randoop. For example, to use integer literals 0, 1, and 2 (i.e., to define a scope of 3) set your literals file (using the -l parameter) as follows:

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

When an API method takes an integer BEAPI will invoke the method once with each primitive value above. You can also specify literals for other primitive types like int, floats, doubles and strings. We provide additional examples in folder `literals` of this repo.


**Scope:** A file defining the scopes for the non-primitive typed values (objects). For example, to generate objects with a maximum size of 3, with a maximum array size of 3 set the -b parameter to a file with the following format:

```
max.objects=3
max.array.objects=3
omit.fields=modCount
```

For instance, for the `NodeCachingLinkedList` [example](README.md#running-a-simple-example) the scopes above will limit the generation to lists with up to 3 nodes (larger objects will be discarded, together with the tests that created them). The `omit.fields` option defines a Java regular expression that allows one to control the BEAPI's state matching procedure, by discarding fields with names that match the expression in object canonicalization. See [[0]](README.md#references) for details.

**Builder methods:** A file containing the signatures of the API methods that BEAPI will use for generation. For instance, the contents of the file for the `NodeCachingLinkedList` [example](README.md#running-a-simple-example) is:
     
```
ncl.NodeCachingLinkedList.<init>\(\)
ncl.NodeCachingLinkedList.addLast\(int\)
ncl.NodeCachingLinkedList.removeIndex\(int\)
```
   
To list the signatures of the public methods in the API of a class we can run BEAPI with `-a=true`. For example:
```
./run-beapi.sh -cp=./examples/bin/ -c=org.apache.commons.collections4.list.NodeCachingLinkedList -l=literals/literals3.txt -b=properties/scope3.all.canonicalizer.properties -a=true
```

BEAPI will list the methods and terminate. From the results we can manually pick the signatures of the builders methods. We have also defined algorithms to automatically identify builders methods. To run these algorithms refer to the [replication package of BEAPI's paper](https://github.com/mpolitano/bounded-exhaustive-api-testgen/blob/main/RUN_BEAPI.md). 



* * *

Go back to [Table of Contents](README.md)

