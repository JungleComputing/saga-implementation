SagaEngine README
-----------------

Introduction.
  This is release 0.9 of the SAGA Java implementation, which implements
  release 0.9 of the Java SAGA language bindings.
  A SAGA engine takes care of dynamically selecting and loading SAGA adaptors,
  and contains base classes for adaptors, and default implementations
  for SAGA attributes, SAGA tasks, SAGA monitorable, SAGA buffer,
  SAGA session, SAGA context.

Installation.
  To install, you need a Java 1.5 and you need "ant".
  Just call "ant" to install.

Environment variables.
  There is one important environment variable, used by the SAGA scripts,
  and that is SAGA_LOCATION. This should be set and point to the root
  directory of your SAGA installation. So, if you installed the SAGA
  implementation in $HOME/workspace/saga-impl-0.9, then you should have
  
  SAGA_LOCATION=$HOME/workspace/saga-impl-0.9.
  
  On MacOS-X, Linux, Solaris, and other Unix systems, this can be accomplished
  by adding the line
      export SAGA_LOCATION=$HOME/workspace/saga-impl-0.9
  or
      set SAGA_LOCATION=$HOME/workspace/saga-impl-0.9
  for CSH users to your .bashrc, .profile, or .cshrc file (whichever gets
  executed when you log in to your system).
  
  On Windows 2000 or Windows XP you may have installed it in, for instance,
      c:\Program Files\saga-impl-0.9
  You can set the SAGA_LOCATION variable to this path by going to the
  Control Panel, System, the "Advanced" tab, Environment variables,
  add it there and reboot your system.

Writing a Java SAGA application.
  Since there is no Java SAGA programmers manual yet, for now you will have to
  find out how to do this mostly by yourself. However, the "demo" directory
  has some small applications that will help you to get a feel for it. And,
  of course, there is the javadoc. Point your favorite browser to
  $SAGA_LOCATION/doc/saga-api-0.9/index.html. This documentation is also
  on-line at http://saga.cct.lsu.edu/java/apidoc/index.html.

Compiling a Java SAGA application.
  To compile your Java SAGA application, you need to have saga-api-0.9.jar in
  your classpath. So:
  
      javac -classpath $SAGA_LOCATION/lib/saga-api-0.9.jar <yourappl>.java
  
  Of course, your application may have other dependencies,
  or more than one .java file.

Running a Java SAGA application.
  A simple script to run a SAGA application lives in bin/run_saga_app.
  This script creates a list of jar-files to put in the classpath (the
  ones in $SAGA_LOCATION/lib), and then calls java.
  
  SAGA uses some properties, which may be set in a file saga.properties,
  which must be on your classpath or in the current directory. Any
  settings in a user-defined saga.properties file override the defaults,
  and in addition, any system property specified with the -D flag to java
  overrides ones provided in a saga.properties file.
  
  The most important such property is probably the
    saga.adaptor.path
  property, which is used to determine where the SAGA engine is to find the
  SAGA adaptors. saga.adaptor.path should be set to a path, a list of
  directories, which each contain directories for the adaptors.
  The default value of saga.adaptor.path (specified in the saga.properties
  file) is $SAGA_LOCATION/lib/adaptors.
  For the structure of an adaptor directory see the lib/adaptors directory.
  In short, each adaptor has its own subdirectory, named <adaptorname>Adaptor,
  in which a jar-file <adaptorname>Adaptor.jar exists, and which also contains
  all supporting jar-files.
  The manifest of <adaptorname>Adaptor.jar specifies which adaptors actually
  are implemented by this adaptor. For instance, GridsamAdaptor.jar has:
  
      JobServiceSpi-class: org.ogf.saga.adaptors.gridsam.job.JobServiceAdaptor
  
  which indicates that it contains a class
  org.ogf.saga.adaptors.gridsam.job.JobServiceAdaptor
  which is a JobService adaptor.

Selecting/deselecting SAGA adaptors.
  By default, the SAGA engine tries all adaptors that it can find on the
  saga.adaptor.path list. It is, however, possible to select a specific
  adaptor, or to not select a specific adaptor. This is accomplished
  with properties, which can be specified in a saga.properties file or
  on the run_saga_app command line. Below are some examples:

  StreamService.adaptor.name = socket,javagat
	this loads both the socket and the javagat adaptor for
	the StreamService SPI, but no others. Also, the adaptors will
	be tried in the specified order.

  StreamService.adaptor.name = !socket
	this will not load the socket adaptor for the StreamService SPI.
  
Adaptor-specific stuff.
  SAGA offers an API for accessing the Grid, and its implementation(s) use
  various grid middleware to realize this. In an ideal world, SAGA would hide
  all the gruesome details from the user. Unfortunately, the world is not
  ideal, and some middleware may need user-defined settings.
  Adaptor-specific information can be found in the corresponding adaptor
  directory, for instance adaptors/GridsamAdaptor/README.txt.

