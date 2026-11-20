package repository.database;

import domain.Utilizator;
import repository.Repository;
import repository.RepositoryException;
import validator.ValidationException;
import validator.Validator;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * JDBC repository implementation for {@link Utilizator} backed by PostgreSQL.
 */
public class UtilizatorDbRepository implements Repository<Long, Utilizator> {
    private final String url;
    private final String username;
    private final String password;
    private final Validator<Utilizator> validator;

    /**
     * Creates a new database repository for users.
     *
     * @param url       JDBC connection URL
     * @param username  database username
     * @param password  database password
     * @param validator entity validator
     */
    public UtilizatorDbRepository(String url, String username, String password, Validator<Utilizator> validator) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }

    /**
     * Finds a user by its identifier.
     *
     * @param id user identifier
     * @return the matching {@link Utilizator}, or {@code null} if not found
     * @throws IllegalArgumentException if {@code id} is {@code null}
     * @throws RepositoryException      if a database access error occurs
     */
    @Override
    public Utilizator findOne(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Id must not be null.");
        }
        String sql = "SELECT id, first_name, last_name FROM users WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RepositoryException("Error finding user by id: " + e.getMessage(), e);
        }
    }

    /**
     * Returns all users stored in the database.
     *
     * @return iterable collection of all {@link Utilizator} entities
     * @throws RepositoryException if a database access error occurs
     */
    @Override
    public Iterable<Utilizator> findAll() {
        String sql = "SELECT id, first_name, last_name FROM users ORDER BY id";
        List<Utilizator> users = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                users.add(mapRow(rs));
            }
            return users;
        } catch (SQLException e) {
            throw new RepositoryException("Error retrieving users: " + e.getMessage(), e);
        }
    }

    /**
     * Saves a new user and populates its database-generated identifier.
     *
     * @param entity user entity to save
     * @return the saved {@link Utilizator} with generated {@code id}
     * @throws IllegalArgumentException if {@code entity} is {@code null}
     * @throws ValidationException      if {@code entity} is invalid
     * @throws RepositoryException      if a database access error occurs
     */
    @Override
    public Utilizator save(Utilizator entity) {
        if (entity == null) {
            throw new IllegalArgumentException("Entity must not be null.");
        }
        validator.validate(entity);

        String sql = "INSERT INTO users (first_name, last_name) VALUES (?, ?)";
        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, entity.getFirstName());
            stmt.setString(2, entity.getLastName());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new RepositoryException("Inserting user failed, no rows affected.");
            }

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    entity.setId(keys.getLong(1));
                } else {
                    throw new RepositoryException("Inserting user failed, no generated ID obtained.");
                }
            }
            return entity;
        } catch (SQLException e) {
            throw new RepositoryException("Error saving user: " + e.getMessage(), e);
        }
    }

    /**
     * Deletes a user by its identifier.
     *
     * @param id identifier of the user to delete
     * @return the deleted {@link Utilizator}, or {@code null} if no user had the given {@code id}
     * @throws IllegalArgumentException if {@code id} is {@code null}
     * @throws RepositoryException      if a database access error occurs
     */
    @Override
    public Utilizator delete(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Id must not be null.");
        }
        Utilizator existing = findOne(id);
        if (existing == null) {
            return null;
        }

        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0 ? existing : null;
        } catch (SQLException e) {
            throw new RepositoryException("Error deleting user: " + e.getMessage(), e);
        }
    }

    /**
     * Updates an existing user in the database.
     *
     * @param entity updated user entity
     * @return {@code null} if updated successfully, or {@code entity} if the id does not exist
     * @throws IllegalArgumentException if {@code entity} or its id is {@code null}
     * @throws ValidationException      if {@code entity} is invalid
     * @throws RepositoryException      if a database access error occurs
     */
    @Override
    public Utilizator update(Utilizator entity) {
        if (entity == null || entity.getId() == null) {
            throw new IllegalArgumentException("Entity and entity id must not be null.");
        }
        validator.validate(entity);

        String sql = "UPDATE users SET first_name = ?, last_name = ? WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(url, username, password);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, entity.getFirstName());
            stmt.setString(2, entity.getLastName());
            stmt.setLong(3, entity.getId());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected == 0 ? entity : null;
        } catch (SQLException e) {
            throw new RepositoryException("Error updating user: " + e.getMessage(), e);
        }
    }

    private Utilizator mapRow(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        String firstName = rs.getString("first_name");
        String lastName = rs.getString("last_name");
        Utilizator user = new Utilizator(firstName, lastName);
        user.setId(id);
        return user;
    }
}