package de.malkusch.whoisServerList.compiler.list.iana;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import de.malkusch.whoisServerList.compiler.exception.WhoisServerListException;
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
	public static final String PROPERTY_WHOIS = "iana.whois";
	
	private Properties properties;
	
	public IanaDomainListFactory(Properties properties) {
		this.properties = properties;
	}
	
	@Override
	public List<TopLevelDomain> buildList() throws BuildListException {
		CloseableHttpClient httpclient = HttpClients.createDefault();
		try {
			HttpGet httpGet = new HttpGet(properties.getProperty(PROPERTY_LIST_URI));
			
			CloseableHttpResponse response = httpclient.execute(httpGet);
			HttpEntity entity = response.getEntity();
			
			EntityToDocumentConverter documentConverter = new EntityToDocumentConverter(properties.getProperty(PROPERTY_LIST_CHARSET));
			DocumentToStringIteratorConvertor<HttpEntity> tldConverter = new DocumentToStringIteratorConvertor<>(properties.getProperty(PROPERTY_LIST_TLD_XPATH), documentConverter);
			
			TopLevelDomainFactory factory = new TopLevelDomainFactory(properties);
			List<TopLevelDomain> domains = new ArrayList<>();
			for (String name : tldConverter.convert(entity)) {
				TopLevelDomain domain = factory.build(name);
				domains.add(domain);
				
			}
			
			EntityUtils.consume(entity);
			
			return domains;
			
		} catch (IOException e) {
			throw new BuildListException(e);
			
		} catch (WhoisServerListException e) {
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
