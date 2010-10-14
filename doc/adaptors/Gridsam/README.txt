GridSAM Adaptor README
----------------------

Introduction.
  The GridSAM adaptor currently solely consists of a JobService adaptor.

Not implemented.
  The adaptor does not implement all of SAGA:
    - the following methods are not implemented (will throw a
      NotImplementedException): getSelf(), signal(), suspend(), resume(),
      checkpoint(), migrate().
    - the following JobDescription attributes are not implemented:
      WORKINGDIRECTORY, INTERACTIVE, JOBCONTACT, JOBSTARTTIME.
    - post-stage append and pre-stage append is not supported.
    - the SPMD extensions are not implemented yet in this adaptor.
    - the following job metrics are not implemented:
      JOB_SIGNAL, JOB_CPUTIME, JOB_MEMORYUSE, JOB_VMEMORYUSE, JOB_PERFORMANCE.
    - the following job attributes are not implemented: 
      TERMSIG.

Using a GridSAM service.
  When creating the job service, you need to specify an URL to contact
  the GridSAM service. The adaptor recognizes the following schemes:
  "https", "gridsam", and "any". The "gridsam" and "any" scheme are
  translated into a "https" scheme. The URL usually looks like:
  gridsam://<host>:18443/gridsam/services/gridsam. The preferred scheme
  to use is "gridsam", as otherwise other adaptors may apply as well.

  You need, of course, a certificate for the Gridsam service. Also, you
  need a file crypto.properties on your classpath. An example is included
  in this directory. You will probably need to adapt some values there,
  a.o. to refer to your certificate.
  You also may need a HTTP-proxy.properties file in your classpath, otherwise
  you get some warning about this. However, this does not seem to harm
  the functionality of the Gridsam adaptor for SAGA.
  Also, you need to be aware that the clock of the system on which your
  application runs does not run more than 5 minutes behind or ahead of
  the clock on the server on which the Gridsam service runs, otherwise
  certificate validation will fail.
  If you don't use the run-saga-app script, you need to be aware that the
  Gridsam adaptor depends on some endorsed libraries, which live in
  $JAVA_SAGA_LOCATION/lib/adaptors/GridsamAdaptor/endorsed. You need to set
  the java.endorsed.dirs property to this directory, see the run-saga-app
  script.
