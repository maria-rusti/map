package validator;

import domain.Utilizator;

public class UtilizatorValidator implements Validator<Utilizator> {
    @Override
    public void validate(Utilizator entity) throws ValidationException {
        String errors = "";
        if (entity.getFirstName() == null || entity.getFirstName().trim().isEmpty()) {
            errors += "Prenumele nu poate fi vid! ";
        }
        if (entity.getLastName() == null || entity.getLastName().trim().isEmpty()) {
            errors += "Numele nu poate fi vid! ";
        }
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }
}