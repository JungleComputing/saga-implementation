SagaAdaptors README
-----------------

This is a snapshot of the adaptors of the SAGA Java implementation.
These adaptors are dynamically loaded by the SAGA engine, which inspects
the jar files, examining their manifest.
For instance an attribute named "NSDirectorySpi-class" with value
"org.ogf.saga.adaptors.javaGAT.namespace.NSDirectory" indicates that
the jar file contains a class
"org.ogf.saga.adaptors.javaGAT.namespace.NSDirectory"
which implements NSDirectorySpi (which is an interface provided by the
engine).

Currently, there are javaGAT adaptors for the following Saga packages:
namespace, logicalfile, file, job, stream. In addition, there is a
gridSAM adaptor for the job package, and a tcp adaptor for the stream package.

Installation.
  To install, you need a Java 1.5 and you need "ant".
  Just call "ant" to install.

Running applications.
  To run applications, you need SagaEngine as well.
  See the README file there.
