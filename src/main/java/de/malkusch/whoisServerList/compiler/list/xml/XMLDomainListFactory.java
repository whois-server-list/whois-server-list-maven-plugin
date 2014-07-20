package de.malkusch.whoisServerList.compiler.list.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBException;

import net.jcip.annotations.NotThreadSafe;
import de.malkusch.whoisServerList.api.v0.ServerListFactory;
import de.malkusch.whoisServerList.api.v0.model.Domain;
import de.malkusch.whoisServerList.api.v0.model.Server;
import de.malkusch.whoisServerList.api.v0.model.Serverlist;
import de.malkusch.whoisServerList.compiler.helper.DomainUtil;
import de.malkusch.whoisServerList.compiler.list.DomainListFactory;
import de.malkusch.whoisServerList.compiler.list.exception.BuildListException;
import de.malkusch.whoisServerList.compiler.model.DomainList;
import de.malkusch.whoisServerList.compiler.model.Source;
import de.malkusch.whoisServerList.compiler.model.WhoisServer;
import de.malkusch.whoisServerList.compiler.model.domain.TopLevelDomain;

/**
 * Builds the domain list from the existing whois server list.
 *
 * @author markus@malkusch.de
 *
 * @see <a href="https://github.com/whois-server-list/whois-server-list">Whois
 *      Server List</a>
 * @see <a href="bitcoin:1335STSwu9hST4vcMRppEPgENMHD2r1REK">Donations</a>
 */
@NotThreadSafe
public final class XMLDomainListFactory implements DomainListFactory {

    /**
     * The builded Top Level Domains.
     */
    private final Map<String, TopLevelDomain> topLevelDomains = new HashMap<>();

    /**
     * The top level domain converter.
     */
    private final XMLDomainToDomainConverter domainConverter
            = new XMLDomainToDomainConverter();

    /**
     * The server converter.
     */
    private final XMLServerToServerConverter serverConverter
            = new XMLServerToServerConverter();

    @Override
    public Source getSource() {
        return Source.XML;
    }

    @Override
    public DomainList buildList() throws BuildListException {
        try {
            this.topLevelDomains.clear();

            ServerListFactory factory = new ServerListFactory();
            Serverlist serverlist = factory.download();

            for (Server xmlServer : serverlist.getServer()) {

                // skip server for no domains
                if (xmlServer.getDomain().isEmpty()) {
                    continue;
                }

                WhoisServer server = this.serverConverter.convert(xmlServer);

                for (Domain domain : xmlServer.getDomain()) {
                    TopLevelDomain tld = getTopLevelDomain(domain);
                    tld.getWhoisServers().add(server);

                }
            }

            DomainList list = new DomainList();
            list.setDomains(new ArrayList<>(this.topLevelDomains.values()));
            list.setVersion(parseVersion(serverlist.getNotes()));
            list.setDescription(parseDescription(serverlist.getNotes()));

            return list;

        } catch (JAXBException e) {
            throw new BuildListException(e);
        }
    }

    /**
     * Parses the description from the notes.
     *
     * @param notes  the notes
     * @return the parsed description
     */
    private String parseDescription(final String notes) {
        Pattern pattern
            = Pattern.compile("(^.+?)\\s*Version:\\s[\\S]+", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(notes);

        if (!matcher.find()) {
            return null;

        }
        return matcher.group(1).trim();
    }

    /**
     * Parses the version from the notes.
     *
     * @param notes  the notes
     * @return the version
     */
    private String parseVersion(final String notes) {
        Pattern pattern = Pattern.compile("Version:\\s([\\S]+)");
        Matcher matcher = pattern.matcher(notes);

        if (!matcher.find()) {
            return null;

        }
        return matcher.group(1);
    }

    /**
     * Returns the top level domain.
     *
     * If the top level domain does not exist in {@link #topLevelDomains} it
     * will be created and stored and that map.
     *
     * @param domain  the domain, not null
     * @return the top level domain, not null
     */
    private TopLevelDomain getTopLevelDomain(final Domain domain) {
        String name = DomainUtil.normalize(domain.getName());

        TopLevelDomain tld = this.topLevelDomains.get(name);
        if (tld == null) {
            tld = this.domainConverter.convert(domain);
            this.topLevelDomains.put(name, tld);
        }

        return tld;
    }

}
