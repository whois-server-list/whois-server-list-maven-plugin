package de.malkusch.whoisServerList.compiler.helper.existingDomain.google;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.malkusch.whoisServerList.api.v1.model.domain.Domain;
import de.malkusch.whoisServerList.compiler.helper.existingDomain.FindExistingDomainService;

public final class GoogleSearch implements FindExistingDomainService {

    private final String apiKey;

    private final String engineId;

    private static final Logger LOGGER = LoggerFactory.getLogger(GoogleSearch.class);

    public GoogleSearch(String apiKey, String engineId) {
        if (apiKey == null || engineId == null) {
            throw new NullPointerException();
        }

        this.apiKey = apiKey;
        this.engineId = engineId;
    }

    @Override
    public Optional<String> findExistingDomainName(Domain domain) {
        try {
            String query = String.format("site:.%s", domain.getName());
            URL url = new URL("https://www.googleapis.com/customsearch/v1?key=" + apiKey + "&cx=" + engineId + "&q="
                    + query + "&alt=json");

            ObjectMapper mapper = new ObjectMapper();
            Response response = mapper.readValue(url.openStream(), Response.class);

            return response.items().stream().map(Item::link).map(URL::getHost)
                    .filter(host -> StringUtils.endsWithIgnoreCase(host, domain.getName())).findAny();

        } catch (MalformedURLException e) {
            throw new IllegalStateException(e);

        } catch (JsonParseException | JsonMappingException e) {
            throw new RuntimeException(e);

        } catch (IOException e) {
            LOGGER.error(String.format("Failed to search google for %s", domain), e);
            return Optional.empty();
        }
    }

}
