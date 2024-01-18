
## Command Line Options


| Option    | Description |
| -----------------------------------------|  ------- |
| -cp=\<classpath\>  / --classpath=\<classpath\> |   (**required**) Path to  binary classes to test. |
| -c=\<test class\> / --class=\<test class\> | (**required**) The fully-qualified name of a class to test. |
| -l=\<literals file\> / --literales=\<literals file\> | (**required**) A file containing literal values to be used as inputs to methods invoked during generation. |
| -b=\<objects bounds file\>  / --bounds=\<objects bounds file\>  |  (**required**)  A file containing the maximum allowed size for objects.      |
| -m=\<builder methods file\> / --methods=\<builder methods file\>   | A file containing the signatures of the builder methods that BEAPI will use for generation. When this is not passed, all public API methods are used.        |
| -s=\<serialize objects file\> / --serialize=\<serialize object file\>| Seralize the bounded exhaustive objects to the specified file.        |
| -d=\<test output directory\> / --output-dir=\<test output directory\>|Name of the directory in which JUnit files should be written. |
| -p=\<package name\> / --package=\<package name\>| Name of the package for the generated JUnit files.|


## Fine-tune options



| Option    | Description |
| -----------------------------------------|  ------- |
| --timelimit=\<time is seconds\>   		 |         |
| --instance-generics-integer=\<true / false\>    |          |
| --instance-object-integer=\<true / false\>    |          |
| --max_BE_iterations = \<number of iterations\ |         |
| --BEmaxsize = 100								  ||
| --only-list-methods				|			|



* * *

Go back to [Table of Contents](README.md)
