package de.malkusch.whoisServerList.compiler.list.listObjectBuilder;

import net.jcip.annotations.NotThreadSafe;
import de.malkusch.whoisServerList.compiler.exception.WhoisServerListException;
import de.malkusch.whoisServerList.compiler.model.ListObject;
import de.malkusch.whoisServerList.compiler.model.Source;

/**
 * Abstract builder for ListObject.
 *
 * @author markus@malkusch.de
 * @param <T> the created list object type.
 * @see <a href="bitcoin:1335STSwu9hST4vcMRppEPgENMHD2r1REK">Donations</a>
 */
@NotThreadSafe
abstract class ListObjectBuilder<T extends ListObject<?>> {

    /**
     * The source for the created objects.
     */
    private final Source source;

    /**
     * Sets the source for this factory.
     *
     * All created objects of this factory will have this source.
     *
     * @param source  the source
     */
    ListObjectBuilder(final Source source) {
        this.source = source;
    }

    /**
     * Builds a new list object.
     *
     * @return the list object
     * @throws WhoisServerListException If building the object failed.
     */
    public final T build() throws WhoisServerListException {
        try {
            T object = getObjectType().newInstance();
            object.setSource(source);
            complete(object);
            return object;

        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);

        }
    }

    /**
     * Returns the class for the created objects.
     *
     * @return the object list type
     */
    protected abstract Class<? extends T> getObjectType();

    /**
     * Completes a created object.
     *
     * @param object  the list object, not null
     * @throws WhoisServerListException If building the object failed.
     */
    protected void complete(final T object) throws WhoisServerListException {
    }

}
