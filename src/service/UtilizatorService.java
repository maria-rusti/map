package service;

import domain.Utilizator;
import repository.Repository;
import validator.Validator;

import java.util.Optional;

/**
 * Service for managing users, using Optional and stream-compatible patterns (Lab 4).
 */
public class UtilizatorService {
    private final Repository<Long, Utilizator> repoUtilizator;

    public UtilizatorService(Repository<Long, Utilizator> repoUtilizator) {
        this.repoUtilizator = repoUtilizator;
    }

    /**
     * Adds a new user.
     */
    public Optional<Utilizator> addUtilizator(String firstName, String lastName, Long id) {
        Utilizator utilizator = new Utilizator(firstName, lastName);
        utilizator.setId(id);
        return Optional.ofNullable(repoUtilizator.save(utilizator));
    }

    /**
     * Removes a user by ID, returning an Optional of the removed user.
     */
    public Optional<Utilizator> removeUtilizator(Long id) {
        return Optional.ofNullable(repoUtilizator.delete(id));
    }

    /**
     * Finds a user by ID using Optional.
     */
    public Optional<Utilizator> findUtilizator(Long id) {
        return Optional.ofNullable(repoUtilizator.findOne(id));
    }

    /**
     * Returns all users.
     */
    public Iterable<Utilizator> getAllUtilizatori() {
        return repoUtilizator.findAll();
    }
}