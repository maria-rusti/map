package repository.database;

import domain.Utilizator;
import repository.Repository;
import validator.Validator;

public class UtilizatorDbRepository implements Repository<Long, Utilizator> {
    public UtilizatorDbRepository(String url, String username, String password, Validator<Utilizator> validator) {}

    @Override public Utilizator findOne(Long id) { throw new UnsupportedOperationException("Not implemented for Database at Lab 3"); }
    @Override public Iterable<Utilizator> findAll() { throw new UnsupportedOperationException("Not implemented for Database at Lab 3"); }
    @Override public Utilizator save(Utilizator entity) { throw new UnsupportedOperationException("Not implemented for Database at Lab 3"); }
    @Override public Utilizator delete(Long id) { throw new UnsupportedOperationException("Not implemented for Database at Lab 3"); }
    @Override public Utilizator update(Utilizator entity) { throw new UnsupportedOperationException("Not implemented for Database at Lab 3"); }
}