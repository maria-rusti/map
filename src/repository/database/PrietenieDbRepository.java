package repository.database;

import domain.Prietenie;
import domain.Tuple;
import repository.Repository;
import validator.Validator;

public class PrietenieDbRepository implements Repository<Tuple<Long, Long>, Prietenie> {
    public PrietenieDbRepository(String url, String username, String password, Validator<Prietenie> validator) {}

    @Override public Prietenie findOne(Tuple<Long, Long> id) { throw new UnsupportedOperationException("Not implemented for Database at Lab 3"); }
    @Override public Iterable<Prietenie> findAll() { throw new UnsupportedOperationException("Not implemented for Database at Lab 3"); }
    @Override public Prietenie save(Prietenie entity) { throw new UnsupportedOperationException("Not implemented for Database at Lab 3"); }
    @Override public Prietenie delete(Tuple<Long, Long> id) { throw new UnsupportedOperationException("Not implemented for Database at Lab 3"); }
    @Override public Prietenie update(Prietenie entity) { throw new UnsupportedOperationException("Not implemented for Database at Lab 3"); }
}