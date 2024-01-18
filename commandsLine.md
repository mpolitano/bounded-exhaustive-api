
## Command Line Options


| Option    | Description |
| -----------------------------------------|  ------- |
| -cp=\<classpath\>  / --classpath=\<classpath\> |   (**required**) Path to  binary classes to test. |
| -c=\<test class\> / --class=\<test class\> | (**required**) The fully-qualified name of a class to test. |
| -l=\<file name\> / --literales=\<file name\> | (**required**) A file containing literal values to be used as inputs to methods invoked during generation. See [Tutorial](tutorial.md) section for format details. |
| -b=\<file name\>  / --bounds=\<file name\>  |  (**required**)  A file containing the maximum allowed size for objects.  See [Tutorial](tutorial.md) section for format details.    |
| -m=\<file name\> / --methods=\<file name\>| A file containing the signatures of the builder methods that BEAPI will use for generation. When this is not passed, all public API methods are used. See [Tutorial](tutorial.md) section for format details.|
| -s=\<file name\> / --serialize=\<file name\>| Seralize the bounded exhaustive objects to the specified file.        |
| -d=\<directory name\> / --output-dir=\<directory name\>|Name of the directory in which JUnit files should be written. |
| -p=\<package name\> / --package=\<package name\>| Name of the package for the generated JUnit files.|
| -a=\<boolean\> / --list-api=\<boolean\>| List public methods and terminate [default false]|


## Fine-tune options



| Option   									        | Description |
| --------------------------------------------- |  ---------- |
| --timelimit=\<int\>   					        | Maximum number of seconds to spend generating tests|
| --instance-generics-integer=\<boolean\>.      | Instance generic type parameters only with Integer class [default true]|
| --instance-object-integer=\<boolean\>         | Instance Object type parameters only with integer values [default true||
| --max_BE_iterations=\<int\>                 | Maximum number of iterations for inputs generation in BE [default 100] |
| --BEmaxsize=\<int\>   			               | Do not generate BE tests with more than <int> statements [default 100] |				  


**Note**: 

* * *

Go back to [Table of Contents](README.md)
