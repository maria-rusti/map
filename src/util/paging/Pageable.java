package util.paging;

public class Pageable {
    private final int pageNumber;
    private final int pageSize;

    /**
     * Creates a pagination request.
     *
     * @param pageNumber zero-based page index (must be >= 0)
     * @param pageSize   number of elements per page (must be > 0)
     * @throws IllegalArgumentException if pageNumber < 0 or pageSize <= 0
     */
    public Pageable(int pageNumber, int pageSize) {
        if (pageNumber < 0 || pageSize <= 0) {
            throw new IllegalArgumentException("Invalid pagination parameters: pageNumber must be >= 0 and pageSize must be > 0.");
        }
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }
}