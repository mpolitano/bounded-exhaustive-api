
## Command Line Options

*  ``-cp=<folder>``  /   ``--classpath=<folder>``:   (**required**) Path to the folder containing the binaries of the target classes.
  
*  ``-c=<name>`` / ``--class=<name>``:  (**required**) The fully-qualified name of a target class. This flag can be used many times with different classes to generate tests for all of them. 

* ``-l=<file>`` / ``--literals=<file>``:  (**required**) A file containing primitive-typed values (literals) to be used as inputs to methods with primitive-typed parameters. See the [tutorial](tutorial.md) for details. 

* ``-b=<file>``  / ``--bounds=<file>``:  (**required**)  A file defining the bounds for the generation, most importantly the maximum allowed size of objects. See the [tutorial](tutorial.md) section for details.    

* ``-m=<file>`` / ``--methods=<file>``: A file containing the signatures of the (builder) methods of the target classes that will be used for generation. See the [tutorial](tutorial.md) for details. [**Default**: All public API methods of the target classes.]

* ``-s=<file>`` / ``--serialize=<file>``: Seralize the generated objects to the specified file. [**Default**: no] 

* ``-d=<folder>`` / ``--output-dir=<folder>``: Directory name to store the generated JUnit test suites. [**Default**: current directory] 

* ``-p=<name>`` / ``--package=<name>``: Package name for the generated JUnit test suite. [Default: empty]

* ``-a=<boolean>`` ``/ --list-api=<boolean>``: List the public methods of the target classes and terminate. [**Default**: false]


## Fine-tunning the search limits

**Note**: **BEAPI** is preconfigured in such way that it will try to generate objects in a bounded-exhaustive manner. However, due to the large number of objects to be generated it might not always terminate fast enough (and it might event not terminate for large scopes and/or complex case studies). Hence, if you want to limit the search so it generates a *partial set of objects* (i.e., not bounded-exhaustive) you can use the options below.


* -t=\<int\> / --time=\<int\>: Maximum number of seconds to spend generating tests. [**Default**: 10 minutes]

* -io=\<boolean\> / --instance-objects-int=\<boolean\>: Instance object-typed parameters only with integer values. [Default: true|

* -i=\<int\>  / --iterations=\<int\>: Limit on the maximum number of iterations. Iteration numbers are displayed throughout the execution. [**Default**: 100] 

* -tl=\<int\> / --test-length=\<int\>: Limit on the maximum number of statements in generated tests. [**Default**: 100] 

* -sm=NO / --matching=NO: Do not use the *state matching* optimization. See [[0]](README.md#references) in [**Default**: YES]				  

* * *

Go back to [Table of Contents](README.md)
