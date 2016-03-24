package de.malkusch.whoisServerList.compiler.list.whoisrb;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
final class Zone {
    
    private String host;
    
    void setHost(String host) {
        this.host = host;
    }
    
    public String host() {
        return host;
    }

}
