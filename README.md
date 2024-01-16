# BEAPI Tool: Efficient Bounded Exhaustive Input Generation from Program APIs

`BEAPI` is an efficient bounded exhaustive test generation tool that employs routines from the API of the software under test for generation. BEAPI solely employs the public methods in the API of a class in order to perform bounded exhaustive input generation for the class, without the need for a formal specification of valid inputs. 


`BEAPI` takes as inputs the target class (or classes) for test case generation, a configuration files defining the bounds (or scopes, as these are also typically called in this context), and a file with the signatures of the builder methods for the target class (a sufficient set of builder methods
that is, methods from the API that by themselves are enough to produce all the feasible objects for the target class). As outputs, BEAPI tool yields a bounded exhaustive set of objects, a JUnit  test suite with the method sequences produced by BEAPI to create each object in the result set, and a separate test suite with
tests revealing errors (if these have been found) in the (subset of) methods used for generation. 


