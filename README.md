# BEAPI Tool: Efficient Bounded Exhaustive Input Generation from Program APIs

*BEAPI* is an efficient bounded exhaustive test generation tool that employs routines from the **API** of the software under test for generation. *BEAPI* solely employs the public methods in the **API** of a class in order to perform bounded exhaustive input generation for the class, **without** the need for a formal specification of valid inputs. 


*BEAPI* takes as inputs the target classes for test case generation, a configuration files defining the scopes, and the builder methods for the target class (a sufficient set of builder methods that is, methods from the **API** that by themselves are enough to produce all the feasible objects for the target classes). As outputs, *BEAPI* tool yields:
- a bounded exhaustive set of objects,
- a **JUnit**  test suite with the method sequences produced by *BEAPI* to create each object in the result set, and
- a separate **Junit** test suite with tests revealing errors (if these have been found) in the methods used for generation. 

## Getting Started

### Installing BEAPI

To use  *BEAPI* Clone the repository:

```
git clone https://github.com/mpolitano/bounded-exhaustive-api
```
. . .
#### Running NodeCaching Linked List example

. . .
### Command Line Options
. . .
## BEAPI Tutorial

### Using BEAPI

Before running your own examples and start generating new objects and test, keep in mind that BEAPI requires:

- 
-
-

## How to build BEAPI from source code
