package service;

import domain.Utilizator;
import java.util.function.Predicate;

/**
 * Predicate criteria for filtering users (Lab 4 requirement).
 */
public class CriteriiUtilizator {

    /**
     * Filters users whose last name starts with the given prefix.
     * @param prefix the prefix to search for
     * @return Predicate checking the last name
     */
    public static Predicate<Utilizator> numeIncepeCu(String prefix) {
        return user -> user.getLastName() != null && user.getLastName().startsWith(prefix);
    }

    /**
     * Filters users whose first name starts with the given prefix.
     * @param prefix the prefix to search for
     * @return Predicate checking the first name
     */
    public static Predicate<Utilizator> prenumeIncepeCu(String prefix) {
        return user -> user.getFirstName() != null && user.getFirstName().startsWith(prefix);
    }
}