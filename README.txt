SagaEngine README
-----------------

This is release 0.1 of the SAGA Java implementation.
It includes release 0.9 of the Java SAGA language bindings.
A SAGA engine takes care of dynamically selecting and loading SAGA adaptors,
and contains base classes for adaptors, and default implementations
for SAGA attributes, SAGA tasks, SAGA monitorable, SAGA buffer,
SAGA session, SAGA context.

Installation.
  To install, you need a Java 1.5 and you need "ant".
  Just call "ant" to install.

Running applications.
  Point the SAGA_LOCATION environment variable to the Saga tree.
  Some small test applications can be found in the "test" directory.
  Scripts to run them can be found in the "demo" directory.
