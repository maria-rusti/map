package ui;

import domain.Prietenie;
import domain.Utilizator;
import repository.RepositoryException;
import service.ComunitateService;
import service.PrietenieService;
import service.UtilizatorService;
import validator.ValidationException;

import java.util.Scanner;

public class UI {
    private final UtilizatorService utilizatorService;
    private final PrietenieService prietenieService;
    private final ComunitateService comunitateService;
    private final Scanner scanner;

    public UI(UtilizatorService utilizatorService, PrietenieService prietenieService, ComunitateService comunitateService) {
        this.utilizatorService = utilizatorService;
        this.prietenieService = prietenieService;
        this.comunitateService = comunitateService;
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
            // Ștergem prieteniile asociate pentru a menține consistența
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
}