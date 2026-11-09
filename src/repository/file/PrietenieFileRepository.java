package repository.file;

import domain.Prietenie;
import domain.Tuple;
import validator.Validator;

import java.time.LocalDateTime;

public class PrietenieFileRepository extends AbstractFileRepository<Tuple<Long, Long>, Prietenie> {

    public PrietenieFileRepository(String fileName, Validator<Prietenie> validator) {
        super(fileName, validator);
    }

    @Override
    protected Prietenie extractEntity(String line) {
        String[] tokens = line.split(";");
        Long id1 = Long.parseLong(tokens[0].trim());
        Long id2 = Long.parseLong(tokens[1].trim());
        LocalDateTime date = LocalDateTime.parse(tokens[2].trim());

        Prietenie prietenie = new Prietenie(date);
        prietenie.setId(new Tuple<>(id1, id2));
        return prietenie;
    }

    @Override
    protected String createEntityAsString(Prietenie entity) {
        return entity.getId().getE1() + ";" +
                entity.getId().getE2() + ";" +
                entity.getFriendsFrom();
    }
}