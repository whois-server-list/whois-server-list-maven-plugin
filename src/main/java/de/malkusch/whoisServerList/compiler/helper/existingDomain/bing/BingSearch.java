package de.malkusch.whoisServerList.compiler.helper.existingDomain.bing;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.malkusch.whoisServerList.api.v1.model.domain.Domain;
import de.malkusch.whoisServerList.compiler.helper.existingDomain.FindExistingDomainService;

public final class BingSearch implements FindExistingDomainService {

    private final String apiKey;

    private static final Logger LOGGER = LoggerFactory.getLogger(BingSearch.class);

    public BingSearch(String apiKey) {
        if (apiKey == null) {
            throw new NullPointerException();
        }
        this.apiKey = apiKey;
    }

    @Override
    public Optional<String> findExistingDomainName(Domain domain) {
        try {
            String query = URLEncoder.encode(String.format("'site:.%s'", domain.getName()), "UTF-8");
            URL url = new URL("https://api.datamarket.azure.com/Bing/Search/v1/Web?$format=json&Query=" + query);

            Results results;
            try (InputStream stream = search(url)) {
                ObjectMapper mapper = new ObjectMapper();
                TypeReference<HashMap<String, Results>> typeRef = new TypeReference<HashMap<String, Results>>() {
                };
                Map<String, Results> resultsMap = mapper.readValue(stream, typeRef);
                results = resultsMap.values().stream().findAny().orElse(null);
            }
            if (results == null) {
                return Optional.empty();
            }

            return results.results().stream().map(Result::url).map(URL::getHost)
                    .filter(host -> StringUtils.endsWithIgnoreCase(host, domain.getName())).findAny();

        } catch (UnsupportedEncodingException | MalformedURLException e) {
            throw new IllegalStateException(e);

        } catch (IOException e) {
            LOGGER.error(String.format("Failed to search bing for %s", domain), e);
            return Optional.empty();
        }
    }

    private InputStream search(URL url) throws IOException {
        URLConnection connection = url.openConnection();
        String userpass = ":" + apiKey;
        String encoded = new String(new Base64().encode(userpass.getBytes())).replaceAll("[\r\n]", "");
        connection.setRequestProperty("Authorization", "Basic " + encoded);
        return connection.getInputStream();
    }

}
