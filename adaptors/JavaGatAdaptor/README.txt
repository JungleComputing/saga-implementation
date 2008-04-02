JavaGAT adaptor README
----------------------

Introduction.
  The JavaGAT adaptor actually consists of several adaptors:
  it has implementations for streams, namespace, file, logicalfile, job.

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
    advertService. Unfortunately, at the moment only a local advertService
    is available for JavaGAT, which means that this streams adaptor only
    works locally. This will probably change in the future.
    The StreamService adaptor is complete, exept for permissions.
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
  LogicalFile:
    Needs to be partly rewritten since GAT logicalfiles don't do what
    this adaptor expects. I thought that the GAT logicalfile adaptor
    would create a file or some non-volatile storage for it, but it actually
    depends on the user to do so. TODO!!!

JavaGAT Preferences.
  JavaGAT has a Preferences object type that allows users to pass information
  on to specific adaptors. SAGA does not have such a mechanism. Therefore,
  a "preferences" context type has been added for the SAGA JavaGAT adaptor.


