The SAGA adaptors are dynamically loaded by the SAGA engine, which
inspects the jar files, examining their manifest.
For instance an attribute named "NSDirectorySpi-class" with value
"org.ogf.saga.adaptors.javaGAT.namespace.NSDirectoryAdaptor" indicates
that the jar file contains a class
"org.ogf.saga.adaptors.javaGAT.namespace.NSDirectoryAdaptor"
which implements NSDirectorySpi (which is an interface provided by the
engine).

Each adaptor has a name, for instance JavaGat, or Gridsam. These names
are used to create paths and names for the adaptor jars. For instance,
the Gridsam adaptor will live in
$SAGA_LOCATION/lib/adaptors/GridsamAdaptor/GridsamAdaptor.jar.

Currently, the are JavaGat adaptor implements the following Saga packages:
namespace, logicalfile, file, job, stream. In addition, the Gridsam adaptor 
implements the job package, and the Socket adaptor implements the stream
package.
