package domain;

import java.util.Objects;

/**
 * Base generic entity class.
 *
 * @param <ID> the type of identifier
 */
public class Entity<ID> {
    private ID id;

    /**
     * Gets the identifier.
     *
     * @return the entity id
     */
    public ID getId() {
        return id;
    }

    /**
     * Sets the identifier.
     *
     * @param id the entity id to set
     */
    public void setId(ID id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Entity<?> entity = (Entity<?>) o;
        return Objects.equals(id, entity.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}