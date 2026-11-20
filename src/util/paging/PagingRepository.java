package util.paging;

import domain.Entity;
import repository.Repository;

public interface PagingRepository<ID, E extends Entity<ID>> extends Repository<ID, E> {
    /**
     * Retrieves a single page of entities.
     *
     * @param pageable pagination criteria (page index and page size)
     * @return a {@link Page} containing the page elements and total element count
     */
    Page<E> findAllOnPage(Pageable pageable);
}