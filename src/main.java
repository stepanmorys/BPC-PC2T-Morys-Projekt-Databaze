import java.util.Scanner;

public class main {
    public static void main(String[] args) {

        // Vytvoříme si naši správu (databázi v paměti) a nástroj na čtení klávesnice
        SpravaZamestnancu sprava = new SpravaZamestnancu();
        Scanner scanner = new Scanner(System.in);
        boolean beziProgram = true;

        DatabazeSQL databazeSQL = new DatabazeSQL(); // Vytvoří napojení na SQLite

        // Bod l: Načteme zálohu při startu
        databazeSQL.nacistVse(sprava);

        // ... (následuje zbytek tvého kódu)

        System.out.println("Vítejte v systému pro správu zaměstnanců!");

        // Hlavní smyčka programu
        while (beziProgram) {
            System.out.println("\n=== HLAVNÍ MENU ===");
            System.out.println("a) Přidání zaměstnance");
            System.out.println("b) Přidání spolupráce ");
            System.out.println("c) Odebrání zaměstnance");
            System.out.println("d) Vyhledání zaměstnance ");
            System.out.println("e) Spuštění dovednosti ");
            System.out.println("f) Abecední výpis zaměstnanců ve skupinách");
            System.out.println("g) Výpis firemní statistiky");
            System.out.println("h) Výpis počtu zaměstnanců ve skupinách");
            System.out.println("i) Uložit zaměstnance do souboru");
            System.out.println("j) Nahrát zaměstnance ze souboru");
            System.out.println("0) Ukončit program");
            System.out.print("Vyberte akci: ");

            String volba = scanner.nextLine().toLowerCase(); // Přečteme volbu a převedeme na malá písmena

            // Rozhodovací strom podle volby uživatele
            switch (volba) {
                case "a":
                    System.out.println("\n--- Přidání zaměstnance ---");
                    System.out.print("Vyberte skupinu (1 = Datový analytik, 2 = Bezpečnostní specialista): ");
                    String skupina = scanner.nextLine();

                    System.out.print("Jméno: ");
                    String jmeno = scanner.nextLine();

                    System.out.print("Příjmení: ");
                    String prijmeni = scanner.nextLine();

                    System.out.print("Rok narození: ");
                    // Zde musíme text z konzole převést na číslo
                    int rok = Integer.parseInt(scanner.nextLine());

                    int noveId = sprava.generujId();

                    // Podle skupiny vytvoříme správný objekt
                    if (skupina.equals("1")) {
                        Zamestnanec novyAnalytik = new DatovyAnalytik(noveId, jmeno, prijmeni, rok);
                        sprava.pridatZamestnance(novyAnalytik);
                    } else if (skupina.equals("2")) {
                        Zamestnanec novySpecialista = new BezpecnostniSpecialista(noveId, jmeno, prijmeni, rok);
                        sprava.pridatZamestnance(novySpecialista);
                    } else {
                        System.out.println("❌ Neplatná skupina! Zaměstnanec nebyl přidán.");
                    }
                    break;

                case "b":
                    System.out.println("\n--- Přidání spolupráce ---");
                    System.out.print("Zadejte ID zaměstnance (kdo si píše záznam): ");
                    int idZ = Integer.parseInt(scanner.nextLine());
                    System.out.print("Zadejte ID kolegy (s kým spolupracoval): ");
                    int idK = Integer.parseInt(scanner.nextLine());
                    System.out.print("Úroveň spolupráce (1 = Dobrá, 2 = Průměrná, 3 = Špatná): ");
                    int uroven = Integer.parseInt(scanner.nextLine());

                    sprava.vytvoritSpolupraci(idZ, idK, uroven);
                    break;

                case "c":
                    System.out.print("Zadejte ID zaměstnance k odebrání: ");
                    int idSmazat = Integer.parseInt(scanner.nextLine());
                    sprava.odebratZamestnance(idSmazat);
                    break;

                case "d":
                    System.out.print("\nZadejte ID zaměstnance pro detailní výpis: ");
                    int idDetail = Integer.parseInt(scanner.nextLine());
                    sprava.vypisDetailZamestnance(idDetail);
                    break;

                case "e":
                    System.out.println("\n--- Spuštění dovednosti ---");
                    System.out.print("Zadejte ID zaměstnance pro spuštění jeho dovednosti: ");
                    int idE = Integer.parseInt(scanner.nextLine());
                    Zamestnanec hledany = sprava.najitZamestnance(idE);

                    if (hledany != null) {
                        // Zde programu předáváme celou správu, aby do ní analytik viděl
                        hledany.spustitDovednost(sprava);
                    } else {
                        System.out.println("❌ Zaměstnanec nenalezen.");
                    }
                    break;

                case "f":
                    sprava.vypisAbecedneVeSkupinach();
                    break;

                case "g":
                    sprava.vypisFiremniStatistiky();
                    break;

                case "h":
                    sprava.vypisPocetVeSkupinach();
                    break;

                case "i":
                    System.out.print("\nZadejte ID zaměstnance, kterého chcete uložit: ");
                    int idUlozit = Integer.parseInt(scanner.nextLine());
                    System.out.print("Zadejte název souboru (např. zamestnanec_1.txt): ");
                    String nazevSouboru = scanner.nextLine();

                    sprava.ulozitZamestnanceDoSouboru(idUlozit, nazevSouboru);
                    break;

                case "j":
                    System.out.print("\nZadejte název souboru k načtení (např. zamestnanec_1.txt): ");
                    String souborKNacteni = scanner.nextLine();
                    sprava.nacistZamestnanceZeSouboru(souborKNacteni);
                    break;

                case "0":
                    // Bod k: Uložíme vše do zálohy před vypnutím
                    databazeSQL.ulozitVse(sprava.getVsiZamestnanci());

                    System.out.println("Ukončuji program. Na shledanou!");
                    beziProgram = false;
                    break;

                default:
                    System.out.println("❌ Neplatná volba, zkuste to znovu.");
            }
        }

        scanner.close(); // Slušnost je po sobě uklidit a scanner zavřít
    }
}