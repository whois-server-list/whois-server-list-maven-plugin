grammar MDWhois;

doc: line+ ;

line: tld? WS? COMMENT? EOL ;

tld: suffix WS arguments ;

suffix: SUFFIX ;

arguments: web | whois | FLAG ;

whois : FLAG WS host | host ;

host : HOST ;

web: WEB WS URI ;

COMMENT : '#' ~[\r\n]* ;
SUFFIX: '.' ~[ \t\r\n]+ ;
WEB: 'WEB' ;
URI: [a-z]+':'~[ \t\r\n]+ ;
HOST: [a-z0-9-\.]+ ;
FLAG: [A-Z]+ ;
WS: [\t ]+ ;
EOL: [\r\n]+ ;
