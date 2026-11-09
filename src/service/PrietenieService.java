package service;

import domain.Prietenie;
import domain.Tuple;
import domain.Utilizator;
import repository.Repository;
import validator.Validator;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.StreamSupport;

/**
 * Service for managing friendships using Stream API and Optional (Lab 4).
 */
public class PrietenieService {
    private final Repository<Tuple<Long, Long>, Prietenie> repoPrietenie;
    private final Repository<Long, Utilizator> repoUtilizator;
    private final Validator<Prietenie> validator;

    public PrietenieService(Repository<Tuple<Long, Long>, Prietenie> repoPrietenie,
                            Repository<Long, Utilizator> repoUtilizator,
                            Validator<Prietenie> validator) {
        this.repoPrietenie = repoPrietenie;
        this.repoUtilizator = repoUtilizator;
        this.validator = validator;
    }

    /**
     * Adds a friendship between two users.
     */
    public void addPrietenie(Long id1, Long id2) {
        if (id1.equals(id2)) {
            throw new IllegalArgumentException("Un utilizator nu poate fi prieten cu el însuși!");
        }

        Optional.ofNullable(repoUtilizator.findOne(id1))
                .orElseThrow(() -> new IllegalArgumentException("Utilizatorul 1 nu există!"));
        Optional.ofNullable(repoUtilizator.findOne(id2))
                .orElseThrow(() -> new IllegalArgumentException("Utilizatorul 2 nu există!"));

        Tuple<Long, Long> id = new Tuple<>(id1, id2);
        Tuple<Long, Long> idInvers = new Tuple<>(id2, id1);

        if (repoPrietenie.findOne(id) != null || repoPrietenie.findOne(idInvers) != null) {
            throw new IllegalArgumentException("Prietenia există deja!");
        }

        Prietenie prietenie = new Prietenie(LocalDateTime.now());
        prietenie.setId(id);
        validator.validate(prietenie);
        repoPrietenie.save(prietenie);
    }

    /**
     * Removes a friendship.
     */
    public void removePrietenie(Long id1, Long id2) {
        Tuple<Long, Long> id = new Tuple<>(id1, id2);
        Prietenie prietenie = repoPrietenie.findOne(id);
        if (prietenie == null) {
            id = new Tuple<>(id2, id1);
            prietenie = repoPrietenie.findOne(id);
        }

        if (prietenie == null) {
            throw new IllegalArgumentException("Prietenia nu există!");
        }

        repoPrietenie.delete(id);
    }

    /**
     * Removes all friendships associated with a user using Stream API (Lab 4 requirement).
     */
    public void removePrieteniiForUser(Long userId) {
        StreamSupport.stream(repoPrietenie.findAll().spliterator(), false)
                .filter(p -> p.getId().getE1().equals(userId) || p.getId().getE2().equals(userId))
                .map(Prietenie::getId)
                .toList() // sau Collectors.toList()
                .forEach(repoPrietenie::delete);
    }

    /**
     * Returns all friendships.
     */
    public Iterable<Prietenie> getAllPrietenii() {
        return repoPrietenie.findAll();
    }
}