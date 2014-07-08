package de.malkusch.whoisServerList.compiler.list.iana;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import de.malkusch.whoisServerList.compiler.exception.WhoisServerListException;
import de.malkusch.whoisServerList.compiler.helper.ConcurrencyService;
import de.malkusch.whoisServerList.compiler.helper.converter.DocumentToStringIteratorConvertor;
import de.malkusch.whoisServerList.compiler.helper.converter.EntityToDocumentConverter;
import de.malkusch.whoisServerList.compiler.list.DomainListFactory;
import de.malkusch.whoisServerList.compiler.list.exception.BuildListException;
import de.malkusch.whoisServerList.compiler.model.domain.TopLevelDomain;

/**
 * Domain list factory.
 * 
 * @author markus@malkusch.de
 * @see <a href="bitcoin:1335STSwu9hST4vcMRppEPgENMHD2r1REK">Donations</a>
 */
public class IanaDomainListFactory extends DomainListFactory {
	
	public static final String PROPERTY_LIST_URI = "iana.list.uri";
	public static final String PROPERTY_LIST_CHARSET = "iana.list.charset";
	public static final String PROPERTY_LIST_TLD_XPATH = "iana.list.tld.xpath";
	public static final String PROPERTY_WHOIS_HOST = "iana.whois.host";
	public static final String PROPERTY_WHOIS_TIMEOUT_SECONDS = "iana.whois.timeout.seconds";
	
	private Properties properties;
	
	public IanaDomainListFactory(Properties properties) {
		this.properties = properties;
	}
	
	@Override
	public List<TopLevelDomain> buildList() throws BuildListException, InterruptedException {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		try {
			HttpGet httpGet = new HttpGet(properties.getProperty(PROPERTY_LIST_URI));
			
			CloseableHttpResponse response = httpclient.execute(httpGet);
			HttpEntity entity = response.getEntity();
			
			EntityToDocumentConverter documentConverter = new EntityToDocumentConverter(properties.getProperty(PROPERTY_LIST_CHARSET));
			DocumentToStringIteratorConvertor<HttpEntity> tldConverter = new DocumentToStringIteratorConvertor<>(properties.getProperty(PROPERTY_LIST_TLD_XPATH), documentConverter);
			
			final TopLevelDomainFactory factory = new TopLevelDomainFactory(properties);
			ConcurrencyService concurrencyService = new ConcurrencyService(properties);
			List<FutureTask<TopLevelDomain>> tasks = new ArrayList<>();
			for (final String name : tldConverter.convert(entity)) {
				FutureTask<TopLevelDomain> task = new FutureTask<>(new Callable<TopLevelDomain>() {

					@Override
					public TopLevelDomain call() throws Exception {
						return factory.build(name);
					}
					
				});
				tasks.add(task);
				concurrencyService.getExecutor().execute(task);
				
			}
			
			List<TopLevelDomain> domains = new ArrayList<>();
			int timeout = Integer.parseInt(properties.getProperty(PROPERTY_WHOIS_TIMEOUT_SECONDS));
			for (Future<TopLevelDomain> task : tasks) {
				domains.add(task.get(timeout, TimeUnit.SECONDS));
				
			}
			
			EntityUtils.consume(entity);
			
			return domains;
			
		} catch (IOException | WhoisServerListException | ExecutionException | TimeoutException e) {
			throw new BuildListException(e);
			
		} finally {
			try {
				httpclient.close();
				
			} catch (IOException e) {
				throw new BuildListException(e);
				
			}
		}
	}

}
