package repository.file;

import domain.Utilizator;
import validator.Validator;

public class UtilizatorFileRepository extends AbstractFileRepository<Long, Utilizator> {

    public UtilizatorFileRepository(String fileName, Validator<Utilizator> validator) {
        super(fileName, validator);
    }

    @Override
    protected Utilizator extractEntity(String line) {
        String[] tokens = line.split(";", -1);
        if (tokens.length < 3) {
            throw new IllegalArgumentException("Linie incompletă pentru Utilizator!");
        }
        Long id = Long.parseLong(tokens[0].trim());
        String firstName = tokens[1].trim();
        String lastName = tokens[2].trim();

        Utilizator u = new Utilizator(firstName, lastName);
        u.setId(id);
        return u;
    }

    @Override
    protected String createEntityAsString(Utilizator entity) {
        return entity.getId() + ";" + entity.getFirstName() + ";" + entity.getLastName();
    }
}