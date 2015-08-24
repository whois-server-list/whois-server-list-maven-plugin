grammar IanaWhois;

response
    :   line+ EOF
    ;

line
    :   (COMMENT | whois | created | changed | setState | keyValuePair) EOL
    ;

whois
    :   WHOIS COLON WS whoisHost
    ;

whoisHost
    :   value
    ;

created
    :   CREATED COLON WS createdDate
    ;
    
createdDate
    :   value
    ;

changed
    :   CHANGED COLON WS changedDate
    ;

changedDate
    :   value
    ;
    
setState
    :   STATE COLON WS state
    ;

state
    :   (NEW | ACTIVE | INACTIVE)
    ;

keyValuePair
    :   key COLON WS value
    ;
    
key
    :   ANY+
    ;
    
value
    :   ~EOL+
    ;

EOL      : [\r\n]+ ;
COMMENT  : '%' ~[\r\n]+ ;
COLON    : ':' ;
WHOIS    : 'whois' ;
CREATED  : 'created' ;
CHANGED  : 'changed' ;
STATE    : 'status' ;
ACTIVE   : 'ACTIVE' ;
NEW      : 'NEW' ;
INACTIVE : 'INACTIVE' ;
WS       : [ \t]+ ;
ANY      : . ;
