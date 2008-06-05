Java SAGA README
----------------

Introduction.
  This is release 1.0rc1 of the SAGA Java implementation, which implements
  release 1.0rc1 of the Java SAGA language bindings.
  A SAGA engine takes care of dynamically selecting and loading SAGA adaptors,
  and contains base classes for adaptors, and default implementations
  for SAGA attributes, SAGA tasks, SAGA monitorable, SAGA buffer,
  SAGA session, SAGA context.

Environment variables.
  There is one important environment variable, used by the SAGA scripts,
  and that is SAGA_LOCATION. This should be set and point to the root
  directory of your SAGA installation. So, if you installed the SAGA
  implementation in $HOME/workspace/saga-impl-1.0rc1, then you should have
  
  SAGA_LOCATION=$HOME/workspace/saga-impl-1.0rc1.
  
  On MacOS-X, Linux, Solaris, and other Unix systems, this can be accomplished
  by adding the line
      export SAGA_LOCATION=$HOME/workspace/saga-impl-1.0rc1
  or
      set SAGA_LOCATION=$HOME/workspace/saga-impl-1.0rc1
  for CSH users to your .bashrc, .profile, or .cshrc file (whichever gets
  executed when you log in to your system).
  
  On Windows 2000 or Windows XP you may have installed it in, for instance,
      c:\Program Files\saga-impl-1.0rc1
  You can set the SAGA_LOCATION variable to this path by going to the
  Control Panel, System, the "Advanced" tab, Environment variables,
  add it there and reboot your system.

Writing a Java SAGA application.
  Since there is no Java SAGA programmers manual yet, for now you will have to
  find out how to do this mostly by yourself. However, the "demo" directory
  has some small applications that will help you to get a feel for it. And,
  of course, there is the javadoc. Point your favorite browser to
  $SAGA_LOCATION/doc/saga-api-1.0rc1/index.html. This documentation is also
  on-line at http://saga.cct.lsu.edu/java/apidoc/index.html.

Compiling and running a Java SAGA application.
  See doc/usersguide.pdf for information on how to compile and run a Java
  SAGA application.
