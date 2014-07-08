package de.malkusch.whoisServerList.compiler.helper;

import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ConcurrencyService {

	public static final String PROPERTY_LEVEL = "concurrency.level";
	
	private Executor executor;
	
	public ConcurrencyService(Properties properties) {
		this.executor = Executors.newFixedThreadPool(Integer.parseInt(properties.getProperty(PROPERTY_LEVEL)));
	}
	
	public Executor getExecutor() {
		return executor;
	}

}
