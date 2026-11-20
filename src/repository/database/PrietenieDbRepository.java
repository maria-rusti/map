package repository.database;

import domain.Prietenie;
import domain.Tuple;
import domain.Utilizator;
import repository.Repository;
import repository.RepositoryException;
import util.paging.Page;
import util.paging.Pageable;
import validator.ValidationException;
import validator.Validator;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * JDBC repository implementation for {@link Prietenie} backed by PostgreSQL.
 */
public class PrietenieDbRepository implements Repository<Tuple<Long, Long>, Prietenie> {
    private final String url;
    private final String username;
    private final String password;
    private final Validator<Prietenie> validator;

    /**
     * Creates a new database repository for friendships.
     *
     * @param url       JDBC connection URL
     * @param username  database username
     * @param password  database password
     * @param validator entity validator
     */
    public PrietenieDbRepository(String url, String username, String password, Validator<Prietenie> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }

    private Tuple<Long, Long> normalizeId(Tuple<Long, Long> id) {
        if (id == null || id.getE1() == null || id.getE2() == null) {
            throw new IllegalArgumentException("Friendship id and its elements must not be null.");
        }
        long a = id.getE1();
        long b = id.getE2();
        return a <= b ? new Tuple<>(a, b) : new Tuple<>(b, a);
    }

    /**
     * Finds a friendship by its pair of user identifiers.
     *
     * @param id pair of user identifiers
     * @return the matching {@link Prietenie}, or {@code null} if not found
     * @throws IllegalArgumentException if {@code id} or its elements are {@code null}
     * @throws RepositoryException      if a database access error occurs
     */
    @Override
    public Prietenie findOne(Tuple<Long, Long> id) {
        Tuple<Long, Long> orderedId = normalizeId(id);
        String sql = "SELECT id1, id2, friends_from FROM friendships WHERE id1 = ? AND id2 = ?";
        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, orderedId.getE1());
            stmt.setLong(2, orderedId.getE2());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RepositoryException("Error finding friendship: " + e.getMessage(), e);
        }
    }

    /**
     * Returns all friendships stored in the database.
     *
     * @return iterable collection of all {@link Prietenie} entities
     * @throws RepositoryException if a database access error occurs
     */
    @Override
    public Iterable<Prietenie> findAll() {
        String sql = "SELECT id1, id2, friends_from FROM friendships ORDER BY id1, id2";
        List<Prietenie> friendships = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                friendships.add(mapRow(rs));
            }
            return friendships;
        } catch (SQLException e) {
            throw new RepositoryException("Error retrieving friendships: " + e.getMessage(), e);
        }
    }

    /**
     * Retrieves a single page of friends for the specified user using a single SQL
     * {@code JOIN} query with database-level pagination.
     *
     * @param userId   identifier of the user whose friends are requested
     * @param pageable pagination criteria containing page number and page size
     * @return a {@link Page} of {@link Utilizator} representing friends on the requested page
     * @throws IllegalArgumentException if {@code userId} or {@code pageable} is {@code null}
     *                                  or if pagination values are invalid
     * @throws RepositoryException      if a database access error occurs
     */
    public Page<Utilizator> findFriendsOnPage(Long userId, Pageable pageable) {
        if (userId == null) {
            throw new IllegalArgumentException("User id must not be null.");
        }
        if (pageable == null) {
            throw new IllegalArgumentException("Pageable must not be null.");
        }
        int pageNumber = pageable.getPageNumber();
        int pageSize = pageable.getPageSize();
        if (pageNumber < 0 || pageSize <= 0) {
            throw new IllegalArgumentException("Invalid pagination parameters: pageNumber must be >= 0 and pageSize must be > 0.");
        }

        String countSql = "SELECT COUNT(*) AS total FROM friendships WHERE id1 = ? OR id2 = ?";
        String pageSql =
                "SELECT u.id, u.first_name, u.last_name " +
                        "FROM friendships f " +
                        "JOIN users u ON u.id = CASE WHEN f.id1 = ? THEN f.id2 ELSE f.id1 END " +
                        "WHERE f.id1 = ? OR f.id2 = ? " +
                        "ORDER BY u.id " +
                        "LIMIT ? OFFSET ?";

        int offset = pageNumber * pageSize;

        try (Connection conn = DriverManager.getConnection(url, username, password)) {
            int totalNumberOfElements = 0;
            try (PreparedStatement countStmt = conn.prepareStatement(countSql)) {
                countStmt.setLong(1, userId);
                countStmt.setLong(2, userId);
                try (ResultSet countRs = countStmt.executeQuery()) {
                    if (countRs.next()) {
                        totalNumberOfElements = countRs.getInt("total");
                    }
                }
            }

            List<Utilizator> friendsOnPage = new ArrayList<>();
            try (PreparedStatement pageStmt = conn.prepareStatement(pageSql)) {
                pageStmt.setLong(1, userId);
                pageStmt.setLong(2, userId);
                pageStmt.setLong(3, userId);
                pageStmt.setInt(4, pageSize);
                pageStmt.setInt(5, offset);
                try (ResultSet rs = pageStmt.executeQuery()) {
                    while (rs.next()) {
                        long friendId = rs.getLong("id");
                        String firstName = rs.getString("first_name");
                        String lastName = rs.getString("last_name");
                        Utilizator friend = new Utilizator(firstName, lastName);
                        friend.setId(friendId);
                        friendsOnPage.add(friend);
                    }
                }
            }

            return new Page<>(friendsOnPage, totalNumberOfElements);
        } catch (SQLException e) {
            throw new RepositoryException("Error retrieving friends on page: " + e.getMessage(), e);
        }
    }

    /**
     * Saves a new friendship in the database.
     *
     * @param entity friendship entity to save
     * @return {@code null} if saved successfully, or {@code entity} if it already exists
     * @throws IllegalArgumentException if {@code entity} or its id is {@code null}
     * @throws ValidationException      if {@code entity} is invalid
     * @throws RepositoryException      if a database access error occurs
     */
    @Override
    public Prietenie save(Prietenie entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity must not be null.");
        }
        validator.validate(entity);

        Tuple<Long, Long> orderedId = normalizeId(entity.getId());
        if (findOne(orderedId) != null) {
            return entity;
        }

        LocalDateTime friendsFrom = entity.getFriendsFrom() != null ? entity.getFriendsFrom() : LocalDateTime.now();
        String sql = "INSERT INTO friendships (id1, id2, friends_from) VALUES (?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, orderedId.getE1());
            stmt.setLong(2, orderedId.getE2());
            stmt.setTimestamp(3, Timestamp.valueOf(friendsFrom));

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new RepositoryException("Inserting friendship failed, no rows affected.");
            }
            return null;
        } catch (SQLException e) {
            throw new RepositoryException("Error saving friendship: " + e.getMessage(), e);
        }
    }

    /**
     * Deletes a friendship by its pair of user identifiers.
     *
     * @param id pair of user identifiers
     * @return the deleted {@link Prietenie}, or {@code null} if not found
     * @throws IllegalArgumentException if {@code id} or its elements are {@code null}
     * @throws RepositoryException      if a database access error occurs
     */
    @Override
    public Prietenie delete(Tuple<Long, Long> id) {
        Tuple<Long, Long> orderedId = normalizeId(id);
        Prietenie existing = findOne(orderedId);
        if (existing == null) {
            return null;
        }

        String sql = "DELETE FROM friendships WHERE id1 = ? AND id2 = ?";
        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, orderedId.getE1());
            stmt.setLong(2, orderedId.getE2());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0 ? existing : null;
        } catch (SQLException e) {
            throw new RepositoryException("Error deleting friendship: " + e.getMessage(), e);
        }
    }

    /**
     * Updates an existing friendship in the database.
     *
     * @param entity updated friendship entity
     * @return {@code null} if updated successfully, or {@code entity} if the id does not exist
     * @throws IllegalArgumentException if {@code entity} or its id is {@code null}
     * @throws ValidationException      if {@code entity} is invalid
     * @throws RepositoryException      if a database access error occurs
     */
    @Override
    public Prietenie update(Prietenie entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity must not be null.");
        }
        validator.validate(entity);

        Tuple<Long, Long> orderedId = normalizeId(entity.getId());
        LocalDateTime friendsFrom = entity.getFriendsFrom() != null ? entity.getFriendsFrom() : LocalDateTime.now();
        String sql = "UPDATE friendships SET friends_from = ? WHERE id1 = ? AND id2 = ?";
        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setTimestamp(1, Timestamp.valueOf(friendsFrom));
            stmt.setLong(2, orderedId.getE1());
            stmt.setLong(3, orderedId.getE2());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected == 0 ? entity : null;
        } catch (SQLException e) {
            throw new RepositoryException("Error updating friendship: " + e.getMessage(), e);
        }
    }

    private Prietenie mapRow(ResultSet rs) throws SQLException {
        long id1 = rs.getLong("id1");
        long id2 = rs.getLong("id2");
        LocalDateTime friendsFrom = rs.getTimestamp("friends_from").toLocalDateTime();
        Prietenie p = new Prietenie(friendsFrom);
        p.setId(new Tuple<>(id1, id2));
        return p;
    }
}