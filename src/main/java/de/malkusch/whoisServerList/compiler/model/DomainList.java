package de.malkusch.whoisServerList.compiler.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import de.malkusch.whoisServerList.compiler.model.domain.TopLevelDomain;

/**
 * List of all top level domains.
 *
 * This is the root element of the data structure.
 *
 * @author markus@malkusch.de
 * @see <a href="bitcoin:1335STSwu9hST4vcMRppEPgENMHD2r1REK">Donations</a>
 */
public final class DomainList implements Cloneable {

    /**
     * The top level domains.
     */
    private List<TopLevelDomain> domains = new ArrayList<>();

    /**
     * The date.
     */
    private Date date;

    /**
     * The version.
     */
    private String version;

    /**
     * The description.
     */
    private String description;

    /**
     * Returns the top level domains.
     *
     * @return the top level domains
     */
    public List<TopLevelDomain> getDomains() {
        return domains;
    }

    /**
     * Set the top level domains.
     *
     * @param domains  the top level domains
     */
    public void setDomains(final List<TopLevelDomain> domains) {
        this.domains = domains;
    }

    /**
     * Return the date of this list.
     *
     * @return the date
     */
    public Date getDate() {
        return date;
    }

    /**
     * Set the date of this list.
     *
     * @param date  the date
     */
    public void setDate(final Date date) {
        this.date = date;
    }

    /**
     * Return the version.
     *
     * @return the version
     */
    public String getVersion() {
        return version;
    }

    /**
     * Set the version.
     *
     * @param version  the version
     */
    public void setVersion(final String version) {
        this.version = version;
    }

    /**
     * Return the description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set the description.
     *
     * @param description  the description
     */
    public void setDescription(final String description) {
        this.description = description;
    }

    @Override
    public DomainList clone() {
        try {
            return (DomainList) super.clone();

        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);

        }
    }

    @Override
    public final boolean equals(final Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public final int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

}
