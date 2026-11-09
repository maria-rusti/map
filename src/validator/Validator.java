package validator;

/**
 * Functional interface for validating entities (Lab 4 requirement).
 * @param <T> the type of entity to validate
 */
@FunctionalInterface
public interface Validator<T> {
    /**
     * Validates the given entity.
     * @param entity the entity to validate
     * @throws ValidationException if the entity is invalid
     */
    void validate(T entity) throws ValidationException;

    /**
     * Combines this validator with another validator sequentially.
     * @param other the next validator to run
     * @return a composed Validator that performs both validations
     */
    default Validator<T> andThen(Validator<T> other) {
        return entity -> {
            this.validate(entity);
            other.validate(entity);
        };
    }
}