package de.malkusch.whoisServerList.compiler.list;

import de.malkusch.whoisServerList.compiler.list.exception.BuildListException;
import de.malkusch.whoisServerList.compiler.model.DomainList;
import de.malkusch.whoisServerList.compiler.model.Source;

/**
 * Domain list factory.
 *
 * @author markus@malkusch.de
 * @see <a href="bitcoin:1335STSwu9hST4vcMRppEPgENMHD2r1REK">Donations</a>
 */
public interface DomainListFactory {

    /**
     * Builds the domain list.
     *
     * @return the top level domain list
     *
     * @throws BuildListException If building failed
     * @throws InterruptedException If the thread was interrupted
     */
    DomainList buildList() throws BuildListException, InterruptedException;

    /**
     * Returns the source of this factory.
     *
     * @return the source
     */
    Source getSource();
}
