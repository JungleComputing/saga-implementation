Local adaptor README
---------------------

Introduction.
  The Local adaptor implements the SAGA packages 'namespace' and (parts of) 
  'file'. It is built on top of Java's local file API (i.e. java.io.File) and
  uses java.nio to copy files. For the 'file' package, only the 'getSize()'
  methods are implemented.

Using the Local adaptor.
  The adaptor recognizes the schemes 'local', 'file', and 'any'. It also accepts
  URLs without a scheme. Examples of recognized URLs:
  
  - file://localhost/etc/passwd
  - local://localhost/usr/bin
  - any://localhost/tmp
  - dir/foo.txt
