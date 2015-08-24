grammar IanaWhois;

response
    :   line+ EOF
    ;

line
    :   (COMMENT | whois | created | changed | state | keyValuePair) EOL
    ;

whois
    :   WHOIS COLON WS value
    ;

created
    :   CREATED COLON WS value
    ;

changed
    :   CHANGED COLON WS value
    ;

state
    :   STATE COLON WS value
    ;

keyValuePair
    :   key COLON WS value
    ;
    
key
    :   ANY+
    ;
    
value
    :   (ANY | WS | COLON | WHOIS | CREATED | CHANGED | STATE | COMMENT)+
    ;

EOL     : [\r\n]+ ;
COMMENT : '%' ~[\r\n]+ ;
COLON   : ':' ;
WHOIS   : 'whois' ;
CREATED : 'created' ;
CHANGED : 'changed' ;
STATE   : 'status' ;
WS      : [ \t]+ ;
ANY     : . ;
