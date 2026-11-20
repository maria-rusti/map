package service;

import domain.Prietenie;
import domain.Tuple;
import domain.Utilizator;
import repository.Repository;
import repository.database.PrietenieDbRepository;
import util.paging.Page;
import util.paging.Pageable;
import validator.Validator;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.StreamSupport;

/**
 * Service for managing friendships using Stream API and Optional (Lab 4),
 * with paginated friend retrieval for database repositories.
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

    /**
     * Retrieves a single page of friends for a given user using the database repository.
     *
     * @param userId   identifier of the user whose friends are requested
     * @param pageable pagination criteria containing page number and page size
     * @return a {@link Page} of {@link Utilizator} representing friends on the requested page
     * @throws IllegalArgumentException if {@code userId} or {@code pageable} is {@code null},
     *                                  pagination parameters are invalid ({@code pageNumber < 0} or {@code pageSize <= 0}),
     *                                  or the repository is not a database repository
     */
    public Page<Utilizator> getFriendsOnPage(Long userId, Pageable pageable) {
        if (userId == null) {
            throw new IllegalArgumentException("User id must not be null.");
        }
        if (pageable == null || pageable.getPageNumber() < 0 || pageable.getPageSize() <= 0) {
            throw new IllegalArgumentException("Invalid pagination parameters: pageNumber must be >= 0 and pageSize must be > 0.");
        }
        if (!(repoPrietenie instanceof PrietenieDbRepository)) {
            throw new IllegalArgumentException("Friend pagination is only supported for DATABASE repositories.");
        }
        PrietenieDbRepository dbRepo = (PrietenieDbRepository) repoPrietenie;
        return dbRepo.findFriendsOnPage(userId, pageable);
    }
}