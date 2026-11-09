package repository;

import domain.Entity;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * CRUD operations repository interface.
 * @param <ID> type E must have an attribute of type ID
 * @param <E> type of entities saved in repository
 */
public interface Repository<ID, E extends Entity<ID>> {

    /**
     * @param id - the id of the entity to be returned
     *           id must not be null
     * @return the entity with the specified id or null - if there is no entity with the given id
     * @throws IllegalArgumentException if id is null.
     */
    E findOne(ID id);

    /**
     * @return all entities
     */
    Iterable<E> findAll();

    /**
     * Returns all entities matching the given filter predicate (Lab 4 requirement).
     * @param filter predicate to filter entities
     * @return List of filtered entities
     */
    default List<E> findAll(Predicate<E> filter) {
        return StreamSupport.stream(findAll().spliterator(), false)
                .filter(filter)
                .collect(Collectors.toList());
    }

    /**
     * @param entity entity must be not null
     * @return null - if the given entity is saved
     *         otherwise returns the entity (id already exists)
     * @throws validator.ValidationException if the entity is not valid
     * @throws IllegalArgumentException if the given entity is null.
     */
    E save(E entity);

    /**
     * removes the entity with the specified id
     * @param id id must be not null
     * @return the removed entity or null if there is no entity with the given id
     * @throws IllegalArgumentException if the given entity is null.
     */
    E delete(ID id);

    /**
     * @param entity entity must not be null
     * @return null - if the entity is updated,
     *         otherwise returns the entity (e.g. id does not exist)
     * @throws IllegalArgumentException if the given entity is null.
     * @throws validator.ValidationException if the entity is not valid.
     */
    E update(E entity);
}