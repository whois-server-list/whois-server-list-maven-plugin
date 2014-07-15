package de.malkusch.whoisServerList.compiler.list;

import java.util.Collection;

import de.malkusch.whoisServerList.compiler.list.exception.BuildListException;
import de.malkusch.whoisServerList.compiler.model.domain.TopLevelDomain;

/**
 * Domain list factory.
 * 
 * @author markus@malkusch.de
 * @see <a href="bitcoin:1335STSwu9hST4vcMRppEPgENMHD2r1REK">Donations</a>
 */
public abstract class DomainListFactory {
	
	/**
	 * Builds the domain list.
	 * @throws InterruptedException 
	 */
	abstract public Collection<TopLevelDomain> buildList() throws BuildListException, InterruptedException;

}
