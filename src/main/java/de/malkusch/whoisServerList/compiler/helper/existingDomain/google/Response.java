package de.malkusch.whoisServerList.compiler.helper.existingDomain.google;

import java.util.ArrayList;
import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
final class Response {

    private Collection<Item> items = new ArrayList<>();

    void setItems(Collection<Item> items) {
        this.items = items;
    }

    Collection<Item> items() {
        return items;
    }

}
