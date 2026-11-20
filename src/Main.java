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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class Main {
    private static Properties loadConfig() {
        Properties props = new Properties();
        Path configPath = Files.exists(Path.of("src/config.properties"))
                ? Path.of("src/config.properties")
                : Path.of("config.properties");

        try (InputStream input = new FileInputStream(configPath.toFile())) {
            props.load(input);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot load configuration file: " + configPath, e);
        }
        return props;
    }

    public static void main(String[] args) {
        Properties props = loadConfig();

        RepositoryType repoType = RepositoryType.valueOf(
                props.getProperty("repository.type", "FILE").trim().toUpperCase()
        );

        String dbUrl = props.getProperty("db.url");
        String dbUser = props.getProperty("db.user");
        String dbPassword = props.getProperty("db.password");

        String usersFile = props.getProperty(
                "data.utilizatori",
                "/Users/rustimaria/Desktop/DOCUMENTS/map/lab3/src/data/utilizatori.txt"
        );
        String friendshipsFile = props.getProperty(
                "data.prietenii",
                "/Users/rustimaria/Desktop/DOCUMENTS/map/lab3/src/data/prietenii.txt"
        );

        RepositoryFactory factory = RepositoryFactory.getInstance();

        Validator<Utilizator> utilizatorValidator = new UtilizatorValidator();
        Validator<Prietenie> prietenieValidator = new PrietenieValidator();

        Repository<Long, Utilizator> userRepo;
        Repository<Tuple<Long, Long>, Prietenie> prietenieRepo;

        if (repoType == RepositoryType.DATABASE) {
            userRepo = factory.createUserRepository(
                    repoType,
                    utilizatorValidator,
                    dbUrl,
                    dbUser,
                    dbPassword
            );
            prietenieRepo = factory.createPrietenieRepository(
                    repoType,
                    prietenieValidator,
                    dbUrl,
                    dbUser,
                    dbPassword
            );
        } else {
            userRepo = factory.createUserRepository(
                    repoType,
                    utilizatorValidator,
                    usersFile
            );
            prietenieRepo = factory.createPrietenieRepository(
                    repoType,
                    prietenieValidator,
                    friendshipsFile
            );
        }

        UtilizatorService utilizatorService = new UtilizatorService(userRepo);
        PrietenieService prietenieService = new PrietenieService(prietenieRepo, userRepo, prietenieValidator);
        ComunitateService comunitateService = new ComunitateService(userRepo, prietenieRepo);

        UI ui = new UI(utilizatorService, prietenieService, comunitateService);
        ui.run();
    }
}