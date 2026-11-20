package repository;

import domain.Prietenie;
import domain.Tuple;
import domain.Utilizator;
import repository.database.PrietenieDbRepository;
import repository.database.UtilizatorDbRepository;
import repository.file.PrietenieFileRepository;
import repository.file.UtilizatorFileRepository;
import validator.Validator;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Factory class for creating repositories using a Map instead of switch (Lab 4).
 */
public class RepositoryFactory {
    private static RepositoryFactory instance;

    private RepositoryFactory() {}

    public static synchronized RepositoryFactory getInstance() {
        if (instance == null) {
            instance = new RepositoryFactory();
        }
        return instance;
    }

    public Repository<Long, Utilizator> createUserRepository(RepositoryType type, Validator<Utilizator> validator, String filePath) {
        Map<RepositoryType, Function<Validator<Utilizator>, Repository<Long, Utilizator>>> creators = new EnumMap<>(RepositoryType.class);

        creators.put(RepositoryType.MEMORY, v -> new InMemoryRepository<>(v));
        creators.put(RepositoryType.FILE, v -> new UtilizatorFileRepository(filePath, v));
        creators.put(RepositoryType.DATABASE, v -> {
            throw new UnsupportedOperationException("Database repository nu este implementat încă pentru Lab 4!");
        });

        Function<Validator<Utilizator>, Repository<Long, Utilizator>> creator = creators.get(type);
        if (creator == null) {
            throw new IllegalArgumentException("Unsupported repository type: " + type);
        }
        return creator.apply(validator);
    }

    public Repository<Tuple<Long, Long>, Prietenie> createPrietenieRepository(RepositoryType type, Validator<Prietenie> validator, String filePath) {
        Map<RepositoryType, Function<Validator<Prietenie>, Repository<Tuple<Long, Long>, Prietenie>>> creators = new EnumMap<>(RepositoryType.class);

        creators.put(RepositoryType.MEMORY, v -> new InMemoryRepository<>(v));
        creators.put(RepositoryType.FILE, v -> new PrietenieFileRepository(filePath, v));
        creators.put(RepositoryType.DATABASE, v -> {
            throw new UnsupportedOperationException("Database repository nu este implementat încă pentru Lab 4!");
        });
        Function<Validator<Prietenie>, Repository<Tuple<Long, Long>, Prietenie>> creator = creators.get(type);
        if (creator == null) {
            throw new IllegalArgumentException("Unsupported repository type: " + type);
        }
        return creator.apply(validator);
    }

    public Repository<Long, Utilizator> createUserRepository(
            RepositoryType type,
            Validator<Utilizator> validator,
            String url,
            String username,
            String password
    ) {
        if (type == RepositoryType.DATABASE) {
            return new UtilizatorDbRepository(url, username, password, validator);
        }
        throw new IllegalArgumentException("Unsupported repository type for database creation: " + type);
    }

    public Repository<Tuple<Long, Long>, Prietenie> createPrietenieRepository(
            RepositoryType type,
            Validator<Prietenie> validator,
            String url,
            String username,
            String password
    ) {
        if (type == RepositoryType.DATABASE) {
            return new PrietenieDbRepository(url, username, password, validator);
        }
        throw new IllegalArgumentException("Unsupported repository type for database creation: " + type);
    }
}