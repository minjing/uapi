package uapi;

/**
 * Implement the interface will indicate that the object can be identifiable.
 * In general the getId will return the object id which will be put in a map
 * which means the id object should rewrite the hash and equals method carefully.
 */
public interface IIdentifiable<T> {

    T getId();
}
