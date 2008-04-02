Socket adaptor README
---------------------

Introduction.
  The Socket adaptor solely consists of a Stream and StreamService adaptor.
  It is built on top of java.net.Socket.

Not implemented.
  The StreamService adaptor is complete, exept for permissions.
  The Stream adaptor does not support the STREAM_WRITE metric and
  waitFor(Activity.WRITE). Also, Stream attributes are not supported yet.

Using the Socket adaptor.
  The socket adaptor recognizes the "tcp" and the "any" scheme.
  The "any" scheme is translated into a "tcp" scheme.
