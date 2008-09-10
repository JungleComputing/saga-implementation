JavaGAT adaptor README
----------------------

Introduction.
  The JavaGAT adaptor actually consists of several adaptors:
  it has implementations for streams, namespace, file, job.
  The JavaGAT version supplied is version 2.0.1.

Not implemented.
  Permissions are not implemented, there is no support for it in JavaGAT.
  A general note: the adaptors here are built on top of the JavaGAT API.
  However, JavaGAT adaptor implementations may only support this API
  partly (like SAGA adaptors may only support the SAGA API partly).
  It is possible that although there is support for some construct in
  the JavaGAT API, there may be no adaptor that implements it (as could
  be the case with SAGA).

  Streams:
    The JavaGAT adaptor for the stream package is built on JavaGAT EndPoint
    and JavaGAT Pipe. The adaptor implementation also depends on the JavaGAT
    advertService. This advert service relies on a file, whose url may
    be supplied to the Streams adaptor by means of a system property.
    The system property "saga.adaptor.javagat.advertService"
    should be set to the required URL.
    There is a default but that works only locally: "file://$HOME/.GatAdvertDB".
    The StreamService adaptor is complete, except for permissions.
    The Stream adaptor does not support the STREAM_WRITE metric and
    waitFor(Activity.WRITE). Also, Stream attributes are not supported yet.
  Jobs:
    The JavaGAT adaptor for the job package is built on JavaGAT Resources,
    for which various adaptors are available: ssh, globus, gridsam, to name
    a few.
    The following methods are not implemented: getSelf(),  signal(),
    checkpoint(), migrate().
    The following JobDescription attributes are not implemented:
    SPMDVARIATION, INTERACTIVE, THREADSPERPROCESS, JOBCONTACT, JOBSTARTTIME.
    Also, post-stage append and pre-stage append is not supported.
    The following Job attributes are not supported: TERMSIG.
  Namespace:
    the SAGA namespace package is built on JavaGAT File, for which various
    adaptors are available: ssh, globus, sftp, to name a few.
    Links are not supported.
  File:
    the File methods modesE(), readE(), writeE(), sizeE() are not implemented.
    In addition, readP and writeP only have the implementation inherited
    from the base implementation, which translates the patterns into
    seeks and contiguous reads/writes, which probably is very slow.
    The File adaptor is built on the JavaGAT RandomAccessFile. Unfortunately,
    currently only a local adaptor is available for that.
    Permissions are not implemented.
    FileInputStream and FileOutputStream are built in top of the
    corresponding JavaGAT versions, for which several adaptors are available.

JavaGAT Preferences.
  JavaGAT has a Preferences object type that allows users to pass information
  on to specific adaptors. SAGA does not have such a mechanism. Therefore,
  a "preferences" context type has been added for the SAGA JavaGAT adaptor.
  For instance, a JavaGAT application may use preferences to select which
  adaptor to use, very similar to the way a SAGA application can use
  properties to select which SAGA adaptor to use. See for instance the
  code in demo/src/demo/job/TestJob2.java for an example.
  The file JavaGat-doc/javagat.properties describes the available preferences.
