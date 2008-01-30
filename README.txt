SagaEngine README
-----------------

This is a snapshot of the engine part of the SAGA Java implementation.
The engine takes care of dynamically selecting and loading SAGA adaptors,
and contains base classes for adaptors, and default implementations
for SAGA attributes, SAGA tasks, SAGA monitorable, SAGA buffer,
SAGA session, SAGA context.

Installation.
  To install, you need a Java 1.5 and you need "ant".
  Just call "ant" to install.

Running applications.
  To run applications, you need SagaAdaptors as well.
  Point the SAGA_ADAPTORS_HOME environment variable to it,
  and point the SAGA_HOME environment variable to the SagaEngine tree.
  Some small test applications can be found in the "test" directory.
  Scripts to run them can be found in the "demo" directory.
