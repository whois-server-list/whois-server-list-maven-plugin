package de.malkusch.whoisServerList.compiler.helper.existingDomain.bing;

import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
final class Results {

    private Collection<Result> results;

    Collection<Result> results() {
        return results;
    }

    void setResults(Collection<Result> results) {
        this.results = results;
    }

}
