# Whois server list compiler

This is the code which generates the [whois-server-list](https://github.com/whois-server-list/whois-server-list).
The generated list is a merged compilation of these sources:

* The existing whois-server-list
* [IANA](http://www.iana.org/domains/root/db)
* [Public Suffix List](https://publicsuffix.org/list/effective_tld_names.dat)
* [Ruby Whois](https://github.com/weppos/whois/blob/master/data/tld.json)


# Usage

you can use the compiler as a maven plugin `whois-server-list`:

```xml
<build>
    <plugins>
        <plugin>
            <groupId>de.malkusch.whois-server-list</groupId>
            <artifactId>whois-server-list-maven-plugin</artifactId>
            <version>0.0.1</version>
            <configuration>
                <schema>whois-server-list.xsd</schema>
                <file>whois-server-list.xml</file>
            </configuration>
        </plugin>
    </plugins>
</build>
```

## Goals

* `mvn whois-server-list:schema`: Builds the schema. The configuration parameter
  `schema` is the schema location.

* `mvn whois-server-list:xml`: Builds the whois server list. The configuration
  parameter `file` is the list location.


# License and author

Markus Malkusch <markus@malkusch.de> is the author of this project. This project is free and under the WTFPL.

## Donations

If you like this project and feel generous donate a few Bitcoins here:
[1335STSwu9hST4vcMRppEPgENMHD2r1REK](bitcoin:1335STSwu9hST4vcMRppEPgENMHD2r1REK)

[![Build Status](https://travis-ci.org/whois-server-list/whois-server-list-compiler.svg)](https://travis-ci.org/whois-server-list/whois-server-list-compiler)
