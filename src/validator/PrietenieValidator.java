package validator;

import domain.Prietenie;

public class PrietenieValidator implements Validator<Prietenie> {
    @Override
    public void validate(Prietenie entity) throws ValidationException {
        if (entity.getId() == null || entity.getId().getE1() == null || entity.getId().getE2() == null) {
            throw new ValidationException("O prietenie trebuie să aibă ID-uri valide pentru abândoi utilizatorii!");
        }
        if (entity.getId().getE1().equals(entity.getId().getE2())) {
            throw new ValidationException("Un utilizator nu poate fi prieten cu sine însuși!");
        }
    }
}