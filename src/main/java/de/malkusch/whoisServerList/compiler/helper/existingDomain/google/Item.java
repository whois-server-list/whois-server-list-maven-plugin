package de.malkusch.whoisServerList.compiler.helper.existingDomain.google;

import java.net.URL;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
final class Item {

    private URL link;

    void setLink(URL link) {
        this.link = link;
    }

    URL link() {
        return link;
    }

}
