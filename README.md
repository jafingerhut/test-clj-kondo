# Introduction

This project's only purpose is to be a small test program for features
of some Clojure IDEs and/or LSP server implementations.  It does not
have any errors, but it does have some warnings according to the
2022-April latest versions of clj-kondo with its default
configuration.


# Running using Clojure CLI tools

The Clojure program does not do very much: accept a file name as a
command line parameter, and try to read that file as EDN, if the file
name has the suffix `.edn`.

```bash
$ clojure -M -m test-clj-kondo.main foo.edn
Read edn file with return type class clojure.lang.PersistentArrayMap from file 'foo.edn'
```

# License

Copyright © 2022 Andy Fingerhut

Distributed under the EPL License, same as Clojure. See LICENSE.
