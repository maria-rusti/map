package service;

import domain.Utilizator;
import repository.Repository;
import util.paging.Page;
import util.paging.Pageable;
import util.paging.PagingRepository;

import java.util.Optional;

/**
 * Service for managing users, using Optional and stream-compatible patterns (Lab 4),
 * with paginated retrieval support for paging repositories.
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

    /**
     * Retrieves a single page of users when the underlying repository implements {@link PagingRepository}.
     *
     * @param pageable pagination criteria containing page number and page size
     * @return a {@link Page} of {@link Utilizator} entities and the total user count
     * @throws IllegalArgumentException if {@code pageable} is {@code null}, has invalid parameters
     *                                  ({@code pageNumber < 0} or {@code pageSize <= 0}), or the repository
     *                                  does not support pagination
     */
    public Page<Utilizator> getUsersOnPage(Pageable pageable) {
        if (pageable == null || pageable.getPageNumber() < 0 || pageable.getPageSize() <= 0) {
            throw new IllegalArgumentException("Invalid pagination parameters: pageNumber must be >= 0 and pageSize must be > 0.");
        }
        if (!(repoUtilizator instanceof PagingRepository)) {
            throw new IllegalArgumentException("Pagination is only supported when using a PagingRepository.");
        }
        @SuppressWarnings("unchecked")
        PagingRepository<Long, Utilizator> pagingRepo = (PagingRepository<Long, Utilizator>) repoUtilizator;
        return pagingRepo.findAllOnPage(pageable);
    }
}