import domain.Prietenie;
import domain.Tuple;
import domain.Utilizator;
import repository.Repository;
import repository.RepositoryFactory;
import repository.RepositoryType;
import service.ComunitateService;
import service.PrietenieService;
import service.UtilizatorService;
import ui.UI;
import validator.PrietenieValidator;
import validator.UtilizatorValidator;
import validator.Validator;

public class Main {
    public static void main(String[] args) {
        RepositoryFactory factory = RepositoryFactory.getInstance();

        Validator<Utilizator> utilizatorValidator = new UtilizatorValidator();
        Validator<Prietenie> prietenieValidator = new PrietenieValidator();

        // Tipul de repository utilizat (conform Lab 4)
        RepositoryType repoType = RepositoryType.FILE;

        // Apelăm factory-ul transmis de Lab 4 cu 3 argumente: type, validator și filePath
        Repository<Long, Utilizator> userRepo = factory.createUserRepository(
                repoType,
                utilizatorValidator,
                "/Users/rustimaria/Desktop/DOCUMENTS/map/lab3/src/data/utilizatori.txt"
        );

        Repository<Tuple<Long, Long>, Prietenie> prietenieRepo = factory.createPrietenieRepository(
                repoType,
                prietenieValidator,
                "/Users/rustimaria/Desktop/DOCUMENTS/map/lab3/src/data/prietenii.txt"
        );

        UtilizatorService utilizatorService = new UtilizatorService(userRepo);
        PrietenieService prietenieService = new PrietenieService(prietenieRepo, userRepo, prietenieValidator);
        ComunitateService comunitateService = new ComunitateService(userRepo, prietenieRepo);

        UI ui = new UI(utilizatorService, prietenieService, comunitateService);
        ui.run();
    }
}