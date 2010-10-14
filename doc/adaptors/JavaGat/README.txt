JavaGAT adaptor README
----------------------

Introduction.
  The JavaGAT adaptor actually consists of several adaptors:
  it has implementations for streams, namespace, file, job.
  The JavaGAT version supplied is version 2.0.5.

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
    for which various adaptors are available: ssh, globus, gridsam, glite,
    unicore, to name a few.
    The following methods are not implemented: getSelf(),  signal(),
    checkpoint(), migrate().
    The following JobDescription attributes are not implemented:
    SPMDVARIATION, THREADSPERPROCESS, JOBCONTACT, JOBSTARTTIME.
    Also, post-stage append and pre-stage append is not supported.
    The following Job attributes are not supported: TERMSIG.

  Namespace:
    the SAGA namespace package is built on JavaGAT File, for which various
    adaptors are available: ssh, globus, sftp, glite, to name a few.
    Links are not supported.

  File:
    the File methods modesE(), readE(), writeE(), sizeE() are not implemented.
    In addition, readP and writeP only have the implementation inherited
    from the base implementation, which translates the patterns into
    seeks and contiguous reads/writes, which probably is very slow.
    The File adaptor is built on the JavaGAT RandomAccessFile. Unfortunately,
    currently only a local adaptor is available for that. If a RandomAccessFile
    cannot be used, the adaptor creates a FileInputStream or FileOutputStream
    and uses their methods to implement its own. BUT: FileInputStream only
    supports forward seeks, and FileOutputStream does not support seeks at all.
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
  It is also possible to use preferences to select specific javagat adaptors.
  Below is a list of preference names and possible values:

  resourcebroker.adaptor.name: local, globus, wsgt4new, gt42, sshtrilead, glite,
    gridsam, sge, localq, commandlinessh, unicore
  file.adaptor.name: local, gridftp, sftptrilead, commandlinessh, sshtrilead,
    gliteguid, glitesrm, glitelfn, ftp, gt4gridftp, rftgt4, gt42, rftgt42
  fileinputstream.adaptor.name: local, gridftp, sftptrilead, ftp, http, https
  fileoutputstream.adaptor.name: local, gridftp, sftptrilead, ftp, http, https

JavaGAT schemes.
  It is also possible to use URI schemes to trigger specific javagat adaptors:
  below is a list of schemes and matching adaptors:
  Resourcebroker (Jobs):
      any: local, globus, wsgt4new, gt42, sshtrilead, glite, gridsam, sge, localq,
           commandlinessh, unicore
      ssh: commandlinessh, sshtrilead
      globus: globus
      gram: globus
      commandlinessh: commandlinessh
      sshtrilead: sshtrilead
      ldap: glite
      ldaps: glite
      http: glite, globus
      https: glite, globus, wsgt4new, gt42, gridsam
      glite: glite
      gt42: gt42
      wsgt4new: wsgt4new
      local: local
      localq: localq
      sge: sge
      unicore6: unicore
      gridsam: gridsam

  File:
      (Note: most file adaptors deal with the file: scheme combined with
      no host or local host, because they must be able to copy them
      to remote locations from their own scheme).
      any: local, gridftp, sftptrilead, commandlinessh, sshtrilead,
	   gliteguid, glitesrm, glitelfn, ftp, gt4gridftp, rftgt4, gt42, rftgt42
      ssh: commandlinessh, sshtrilead
      commandlinessh: commandlinessh
      sshtrilead: sshtrilead
      file: commandlinessh, gliteguid, glitelfn, glitesrm, ftp, gridftp, gt42,
            gt4gridftp, rftgt4, rftgt42, sftptrilead, sshtrilead, local
      guid: gliteguid
      gliteguid: gliteguid
      lfn: glitelfn
      glitelfn: glitelfn
      srm: glitesrm
      glitesrm: glitesrm
      ftp: ftp
      sftp: sftptrilead
      sftptrilead: sftptrilead
      gsiftp: gridftp, rftgt4, gt42
      gridftp: gt4gridftp, rftgt4, gt42
      gt4gridftp: gt4gridftp
      gt42: gt42
      rftgt4: rftgt4
      rftgt42: rftgt42

  FileInputStream:
      any: local, ftp, gridftp, http, https, sftptrilead, sshtrilead
      ftp: ftp
      gsiftp: gridftp
      gridftp: gridftp
      http: http
      https: https
      file: local, http, https, gridftp, ftp, sftptrilead, sshtrilead
      sftp: sftptrilead
      sftptrilead: sftptrilead
      sshtrilead: sshtrilead
      ssh: sshtrilead

  FileOutputStream:
      any: local, ftp, gridftp, http, https, sftptrilead, sshtrilead
      ftp: ftp
      gsiftp: gridftp
      gridftp: gridftp
      http: http
      https: https
      file: local, http, https, gridftp, ftp, sftptrilead, sshtrilead
      sftp: sftptrilead
      sftptrilead: sftptrilead
      ssh: sshtrilead
      
Security contexts
  The JavaGAT recognizes the following context types:
    ftp
	with attributes
	  USERID (default: "anonymous")
	  USERPASS (default: "anonymous@localhost")
    ssh
        with attributes
	  USERID
	  USERPASS
	  USERKEY
    sftp
        with attributes
	  USERID
	  USERPASS
	  USERKEY
    globus
	with attributes
	  USERPROXY (default: if the environment variable X509_USER_PROXY is set,
	     then the value of that, else if the System property x509.user.proxy
	     is set, then the value of that, otherwise "")
	  USERKEY (default: if USERPROXY is not set, userkey.pem inside the user's
	     .globus directory, if that file exists)
	  USERCERT (default: if USERPROXY is not set, userkey.cert inside the user's
	     .globus directory, if that file exists)
	  USERPASS
    gridftp
	with attributes
	  USERPROXY (default: if the environment variable X509_USER_PROXY is set,
	     then the value of that, else if the System property x509.user.proxy
	     is set, then the value of that)
	  USERKEY (default: if USERPROXY is not set, userkey.pem inside the user's
	     .globus directory, if that file exists)
	  USERCERT (default: if USERPROXY is not set, userkey.cert inside the user's
	     .globus directory, if that file exists)
	  USERPASS
    glite
	with attributes
	  USERPROXY (default: if the environment variable X509_USER_PROXY is set,
	     then the value of that, else if the System property x509.user.proxy
	     is set, then the value of that, otherwise "")
	  USERKEY (default: if USERPROXY is not set, userkey.pem inside the user's
	     .globus directory, if that file exists)
	  USERCERT (default: if USERPROXY is not set, userkey.cert inside the user's
	     .globus directory, if that file exists)
	  USERPASS
	  USERVO
	  SERVER
	     Note: format of SERVER value is an URL, for instance
	           "voms://voms.grid.sara.nl:30000/O=dutchgrid/O=hosts/OU=sara.nl/CN=voms.grid.sara.nl"

