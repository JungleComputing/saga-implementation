Archive adaptor README
----------------------
  
Introduction.
  The Archive adaptor implements the SAGA packages 'namespace' and (partially)
  'file'. The adaptor is built on top of the TrueZip library, and provides 
  access to archive files as if they were directories. Archives can be nested.

  The adaptor recognizes the following archive types:

  - ZIP files (.zip), including self-executable ZIP files (.exe)

  - TAR archives (.tar), possibly compressed with Gzip (.tar.gz, .tgz) 
    or Bzip2 (.tar.bz2, .tbz2)

  - Java archives (.jar, .war, .ear)

  - OpenDocument files (.odb, .odf, .odg, .odm, .odp, .ods, .odt, .otg, .oth,
    .otp, .ots, .ott)
  
Limitations.  
  File contents can only be accessed via streams (e.g. a FileInputStream and
  FileOutputStream). Random read and write access to files inside an archive
  is not supported by TrueZip.
    
Using the Archive adaptor.
  The adaptor recognizes the schemes 'archive', 'file', and 'any'. It also 
  accepts URLs without a scheme.
  
  Examples of recognized URLs:
  
  - any://localhost/tmp/test.zip
  - file://localhost/home/you/file.jar/src/Test.java
  - archive://localhost/tmp/file.tar.gz/dir/foo.jar/META-INF/LICENSE.txt

  The archive adaptor does not require any specific security contexts.
