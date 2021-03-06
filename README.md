# Whois server list compiler

This is the code which generates the [whois-server-list](https://github.com/whois-server-list/whois-server-list).
The generated list is a merged compilation of these sources:

* The existing whois-server-list
* [IANA](http://www.iana.org/domains/root/db)
* [Public Suffix List](https://publicsuffix.org/list/effective_tld_names.dat)
* [Ruby Whois](https://github.com/weppos/whois/blob/master/data/tld.json)
* [Marco d'Itri's Whois list](https://raw.githubusercontent.com/rfc1036/whois/next/tld_serv_list)
* [php-whois](https://raw.githubusercontent.com/regru/php-whois/master/src/Phois/Whois/whois.servers.json)
* [phpWhois](https://raw.githubusercontent.com/phpWhois/phpWhois/master/src/whois.servers.php)


# Usage

you can use the compiler as a maven plugin `whois-server-list`:

```xml
<build>
    <plugins>
        <plugin>
            <groupId>de.malkusch.whois-server-list</groupId>
            <artifactId>whois-server-list-maven-plugin</artifactId>
            <version>0.1.0</version>
            <configuration>
                <schema>whois-server-list.xsd</schema>
                <file>whois-server-list.xml</file>
            </configuration>
        </plugin>
    </plugins>
</build>
```


## System properties

Please provide these system properties:

* `-Dbing.apiKey` Bing API key to access Bing's web search API
* `-Dgoogle.apiKey` and `-Dgoogle.engine` to use Google's CSE.
* `-DwhoisApi.apiKey` [Whois API](http://whois-api.domaininformation.de/) api key


## Goals

* `mvn whois-server-list:schema`: Builds the schema. The configuration parameter
  `schema` is the schema location.

* `mvn whois-server-list:xml`: Builds the whois server list. The configuration
  parameter `file` is the list location.
  
* `mvn whois-server-list:verify`: Verifies the whois server list.


# License and author

Markus Malkusch <markus@malkusch.de> is the author of this project. This project is free and under the WTFPL.

## Donations

If you like this project and feel generous donate a few Bitcoins here:
[1335STSwu9hST4vcMRppEPgENMHD2r1REK](bitcoin:1335STSwu9hST4vcMRppEPgENMHD2r1REK)

[![Build Status](https://travis-ci.org/whois-server-list/whois-server-list-maven-plugin.svg?branch=master)](https://travis-ci.org/whois-server-list/whois-server-list-maven-plugin)
