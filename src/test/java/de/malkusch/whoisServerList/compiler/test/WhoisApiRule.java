package de.malkusch.whoisServerList.compiler.test;

import static org.junit.Assume.assumeNotNull;

import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

import de.malkusch.whoisApi.WhoisApi;

public class WhoisApiRule implements MethodRule {

    private final WhoisApi whoisApi;

    public WhoisApiRule() {
        String apiKey = System.getenv("API_KEY");
        if (apiKey != null) {
            whoisApi = new WhoisApi(apiKey);

        } else {
            whoisApi = null;
        }
    }

    public WhoisApi whoisApi() {
        assumeNotNull(whoisApi);
        return whoisApi;
    }

    @Override
    public Statement apply(final Statement base, final FrameworkMethod method, final Object target) {
        return base;
    }

}
