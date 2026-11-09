package repository.file;

import domain.Entity;
import repository.InMemoryRepository;
import repository.RepositoryException;
import validator.Validator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public abstract class AbstractFileRepository<ID, E extends Entity<ID>> extends InMemoryRepository<ID, E> {
    protected String fileName;

    public AbstractFileRepository(String fileName, Validator<E> validator) {
        super(validator);
        this.fileName = fileName;
        loadData();
    }

    protected abstract E extractEntity(String line);

    protected abstract String createEntityAsString(E entity);

    protected void loadData() {
        Path path = Paths.get(fileName);
        if (!Files.exists(path)) {
            return;
        }
        try (Stream<String> lines = Files.lines(path)) {
            List<String> lineList = lines.collect(Collectors.toList());
            IntStream.range(0, lineList.size()).forEach(i -> {
                String line = lineList.get(i);
                if (!line.trim().isEmpty()) {
                    try {
                        E entity = extractEntity(line);
                        super.save(entity);
                    } catch (Exception e) {
                        System.err.println("Linia " + (i + 1) + " din " + fileName + " este coruptă: " + line);
                    }
                }
            });
        } catch (IOException e) {
            throw new RepositoryException("Eroare la citirea fișierului: " + fileName, e);
        }
    }

    protected void writeToFile() {
        Path path = Paths.get(fileName);
        try {
            List<String> lines = StreamSupport.stream(findAll().spliterator(), false)
                    .map(this::createEntityAsString)
                    .collect(Collectors.toList());
            Files.write(path, lines);
        } catch (IOException e) {
            throw new RepositoryException("Eroare la scrierea în fișier: " + fileName, e);
        }
    }

    @Override
    public E save(E entity) {
        E result = super.save(entity);
        if (result == null) {
            writeToFile();
        }
        return result;
    }

    @Override
    public E delete(ID id) {
        E result = super.delete(id);
        if (result != null) {
            writeToFile();
        }
        return result;
    }

    @Override
    public E update(E entity) {
        E result = super.update(entity);
        if (result == null) {
            writeToFile();
        }
        return result;
    }
}