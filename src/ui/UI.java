package ui;

import domain.Prietenie;
import domain.Utilizator;
import repository.RepositoryException;
import service.ComunitateService;
import service.PrietenieService;
import service.UtilizatorService;
import util.paging.Page;
import util.paging.Pageable;
import validator.ValidationException;

import java.util.List;
import java.util.Scanner;

public class UI {
    private final UtilizatorService utilizatorService;
    private final PrietenieService prietenieService;
    private final ComunitateService comunitateService;
    private final int pageSize;
    private final Scanner scanner;

    public UI(UtilizatorService utilizatorService,
              PrietenieService prietenieService,
              ComunitateService comunitateService,
              int pageSize) {
        this.utilizatorService = utilizatorService;
        this.prietenieService = prietenieService;
        this.comunitateService = comunitateService;
        this.pageSize = pageSize;
        this.scanner = new Scanner(System.in);
    }

    public void run() {
        boolean running = true;
        while (running) {
            printMenu();
            String optiune = scanner.nextLine().trim();
            try {
                // Switch în formă clasică (fără operatori lambda/arrow conform cerințelor)
                switch (optiune) {
                    case "1":
                        adaugaUtilizator();
                        break;
                    case "2":
                        stergeUtilizator();
                        break;
                    case "3":
                        adaugaPrietenie();
                        break;
                    case "4":
                        stergePrietenie();
                        break;
                    case "5":
                        afiseazaUtilizatori();
                        break;
                    case "6":
                        afiseazaPrietenii();
                        break;
                    case "7":
                        afiseazaNumarComunitati();
                        break;
                    case "8":
                        afiseazaCeaMaiSociabilaComunitate();
                        break;
                    case "9":
                        afiseazaUtilizatoriPaginat();
                        break;
                    case "10":
                        afiseazaPrieteniUtilizatorPaginat();
                        break;
                    case "0":
                        running = false;
                        System.out.println("La revedere!");
                        break;
                    default:
                        System.out.println("Opțiune invalidă! Încearcă din nou.");
                }
            } catch (ValidationException | RepositoryException | IllegalArgumentException e) {
                System.out.println("Eroare: " + e.getMessage());
            }
            System.out.println();
        }
    }

    private void printMenu() {
        System.out.println("--- MENIU REȚEA SOCIALĂ ---");
        System.out.println("1. Adaugă utilizator");
        System.out.println("2. Șterge utilizator");
        System.out.println("3. Adaugă prietenie");
        System.out.println("4. Șterge prietenie");
        System.out.println("5. Afișează toți utilizatorii");
        System.out.println("6. Afișează toate prieteniile");
        System.out.println("7. Afișează numărul de comunități");
        System.out.println("8. Afișează cea mai sociabilă comunitate");
        System.out.println("9. Afișează utilizatorii paginat");
        System.out.println("10. Afișează prietenii unui utilizator paginat");
        System.out.println("0. Ieșire");
        System.out.print("Alege opțiunea: ");
    }

    private void adaugaUtilizator() {
        try {
            System.out.print("ID: ");
            Long id = Long.parseLong(scanner.nextLine().trim());
            System.out.print("Prenume: ");
            String prenume = scanner.nextLine().trim();
            System.out.print("Nume: ");
            String nume = scanner.nextLine().trim();

            utilizatorService.addUtilizator(prenume, nume, id);
            System.out.println("Utilizator adăugat cu succes!");
        } catch (NumberFormatException e) {
            System.out.println("Eroare: ID-ul trebuie să fie un număr întreg valid.");
        }
    }

    private void stergeUtilizator() {
        try {
            System.out.print("ID-ul utilizatorului de șters: ");
            Long id = Long.parseLong(scanner.nextLine().trim());
            prietenieService.removePrieteniiForUser(id);
            utilizatorService.removeUtilizator(id);
            System.out.println("Utilizator și prieteniile asociate au fost șterse cu succes!");
        } catch (NumberFormatException e) {
            System.out.println("Eroare: ID-ul trebuie să fie un număr întreg valid.");
        }
    }

    private void adaugaPrietenie() {
        try {
            System.out.print("ID Utilizator 1: ");
            Long id1 = Long.parseLong(scanner.nextLine().trim());
            System.out.print("ID Utilizator 2: ");
            Long id2 = Long.parseLong(scanner.nextLine().trim());

            prietenieService.addPrietenie(id1, id2);
            System.out.println("Prietenie adăugată cu succes!");
        } catch (NumberFormatException e) {
            System.out.println("Eroare: ID-urile trebuie să fie numere întregi valide.");
        }
    }

    private void stergePrietenie() {
        try {
            System.out.print("ID Utilizator 1: ");
            Long id1 = Long.parseLong(scanner.nextLine().trim());
            System.out.print("ID Utilizator 2: ");
            Long id2 = Long.parseLong(scanner.nextLine().trim());

            prietenieService.removePrietenie(id1, id2);
            System.out.println("Prietenie ștearsă cu succes!");
        } catch (NumberFormatException e) {
            System.out.println("Eroare: ID-urile trebuie să fie numere întregi valide.");
        }
    }

    private void afiseazaUtilizatori() {
        System.out.println("--- LISTA UTILIZATORI ---");
        for (Utilizator u : utilizatorService.getAllUtilizatori()) {
            System.out.println("ID: " + u.getId() + " | " + u.getFirstName() + " " + u.getLastName());
        }
    }

    private void afiseazaPrietenii() {
        System.out.println("--- LISTA PRIETENII ---");
        for (Prietenie p : prietenieService.getAllPrietenii()) {
            System.out.println("Utilizator " + p.getId().getE1() + " - Utilizator " + p.getId().getE2() + " (din " + p.getFriendsFrom() + ")");
        }
    }

    private void afiseazaNumarComunitati() {
        int nr = comunitateService.getNumarComunitati();
        System.out.println("Numărul total de comunități din rețea este: " + nr);
    }

    private void afiseazaCeaMaiSociabilaComunitate() {
        var comunitate = comunitateService.getCeaMaiSociabilaComunitate();
        System.out.println("--- CEA MAI SOCIABILĂ COMUNITATE ---");
        for (Utilizator u : comunitate) {
            System.out.println("- " + u.getFirstName() + " " + u.getLastName() + " (ID: " + u.getId() + ")");
        }
    }

    private void afiseazaUtilizatoriPaginat() {
        int currentPage = 0;
        while (true) {
            Page<Utilizator> page = utilizatorService.getUsersOnPage(new Pageable(currentPage, pageSize));
            int totalElements = page.getTotalNumberOfElements();
            int totalPages = totalElements == 0 ? 1 : (int) Math.ceil((double) totalElements / pageSize);

            System.out.println("--- UTILIZATORI (Pagina " + (currentPage + 1) + " din " + totalPages + ") ---");
            List<Utilizator> elements = page.getElementsOnPage();
            if (elements.isEmpty()) {
                System.out.println("Pagină goală — nu există utilizatori pe această pagină.");
            } else {
                for (Utilizator u : elements) {
                    System.out.println("ID: " + u.getId() + " | " + u.getFirstName() + " " + u.getLastName());
                }
            }

            System.out.print("[n] Înainte | [p] Înapoi | [q] Ieșire: ");
            String cmd = scanner.nextLine().trim().toLowerCase();
            if ("n".equals(cmd)) {
                currentPage++;
            } else if ("p".equals(cmd)) {
                if (currentPage > 0) {
                    currentPage--;
                } else {
                    System.out.println("Ești deja pe prima pagină.");
                }
            } else if ("q".equals(cmd)) {
                break;
            } else {
                System.out.println("Comandă invalidă! Folosește n, p sau q.");
            }
        }
    }

    private void afiseazaPrieteniUtilizatorPaginat() {
        try {
            System.out.print("ID utilizator: ");
            Long userId = Long.parseLong(scanner.nextLine().trim());

            int currentPage = 0;
            while (true) {
                Page<Utilizator> page = prietenieService.getFriendsOnPage(userId, new Pageable(currentPage, pageSize));
                int totalElements = page.getTotalNumberOfElements();
                int totalPages = totalElements == 0 ? 1 : (int) Math.ceil((double) totalElements / pageSize);

                System.out.println("--- PRIETENII UTILIZATORULUI " + userId + " (Pagina " + (currentPage + 1) + " din " + totalPages + ") ---");
                List<Utilizator> elements = page.getElementsOnPage();
                if (elements.isEmpty()) {
                    System.out.println("Pagină goală — nu există prieteni pe această pagină.");
                } else {
                    for (Utilizator u : elements) {
                        System.out.println("ID: " + u.getId() + " | " + u.getFirstName() + " " + u.getLastName());
                    }
                }

                System.out.print("[n] Înainte | [p] Înapoi | [q] Ieșire: ");
                String cmd = scanner.nextLine().trim().toLowerCase();
                if ("n".equals(cmd)) {
                    currentPage++;
                } else if ("p".equals(cmd)) {
                    if (currentPage > 0) {
                        currentPage--;
                    } else {
                        System.out.println("Ești deja pe prima pagină.");
                    }
                } else if ("q".equals(cmd)) {
                    break;
                } else {
                    System.out.println("Comandă invalidă! Folosește n, p sau q.");
                }
            }
        } catch (NumberFormatException e) {
            System.out.println("Eroare: ID-ul trebuie să fie un număr întreg valid.");
        }
    }
}