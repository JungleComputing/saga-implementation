Java SAGA README
----------------

Introduction.
  This is release 1.0 of the SAGA Java implementation, which implements
  release 1.0 of the Java SAGA language bindings.
  A SAGA engine takes care of dynamically selecting and loading SAGA adaptors,
  and contains base classes for adaptors, and default implementations
  for SAGA attributes, SAGA tasks, SAGA monitorable, SAGA buffer,
  SAGA session, SAGA context.

Environment variables.
  There is one important environment variable, used by the SAGA scripts,
  and that is JAVA_SAGA_LOCATION. This should be set and point to the root
  directory of your SAGA installation. So, if you installed the SAGA
  implementation in $HOME/workspace/saga-impl-1.0, then you should have
  
  JAVA_SAGA_LOCATION=$HOME/workspace/saga-impl-1.0.
  
  On MacOS-X, Linux, Solaris, and other Unix systems, this can be accomplished
  by adding the line
      export JAVA_SAGA_LOCATION=$HOME/workspace/saga-impl-1.0
  or
      set JAVA_SAGA_LOCATION=$HOME/workspace/saga-impl-1.0
  for CSH users to your .bashrc, .profile, or .cshrc file (whichever gets
  executed when you log in to your system).
  
  On Windows 2000 or Windows XP you may have installed it in, for instance,
      c:\Program Files\saga-impl-1.0
  You can set the JAVA_SAGA_LOCATION variable to this path by going to the
  Control Panel, System, the "Advanced" tab, Environment variables,
  add it there and reboot your system.

  For backwards compatibility with earlier Java SAGA releases, the
  SAGA_LOCATION environment variable is also recognized. However, the
  JAVA_SAGA_LOCATION environment variable takes precedence.

Writing a Java SAGA application.
  Since there is no Java SAGA programmers manual yet, for now you will have to
  find out how to do this mostly by yourself. However, the "demo" directory
  has some small applications that will help you to get a feel for it. And,
  of course, there is the javadoc. Point your favorite browser to
  $JAVA_SAGA_LOCATION/doc/saga-api-1.0/index.html. This documentation is also
  on-line at http://saga.cct.lsu.edu/java/apidoc/index.html.

Compiling and running a Java SAGA application.
  See doc/usersguide.pdf for information on how to compile and run a Java
  SAGA application.
