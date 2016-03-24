package de.malkusch.whoisServerList.compiler.helper.existingDomain.bing;

import java.net.URL;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
final class Result {

    @JsonProperty("Url")
    private URL url;

    URL url() {
        return url;
    }

}
