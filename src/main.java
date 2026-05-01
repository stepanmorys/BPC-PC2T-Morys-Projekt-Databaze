import java.util.Scanner;

public class main {
    public static void main(String[] args) {


        SpravaZamestnancu sprava = new SpravaZamestnancu();
        Scanner scanner = new Scanner(System.in);
        boolean beziProgram = true;

        DatabazeSQL databazeSQL = new DatabazeSQL();


        databazeSQL.nacistVse(sprava);


        System.out.println("Vítejte v systému pro správu zaměstnanců!");


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
            System.out.println("k) Export celé databáze do souboru");
            System.out.println("l) Vymazat celou databázi");
            System.out.println("0) Ukončit program");
            System.out.print("Vyberte akci: ");

            String volba = scanner.nextLine().toLowerCase();


            switch (volba) {
                case "a":
                    System.out.println("\n--- Přidání zaměstnance ---");

                    String skupina = "";
                    while (true) {
                        System.out.print("Vyberte skupinu (1 = Datový analytik, 2 = Bezpečnostní specialista): ");
                        skupina = scanner.nextLine().trim();

                        if (skupina.equals("1") || skupina.equals("2")) {
                            break;
                        } else {
                            System.out.println("⚠️ Neplatná skupina! Zadejte prosím číslo 1 nebo 2.");
                        }
                    }

                    String jmeno = "";
                    while (true) {
                        System.out.print("Jméno: ");
                        jmeno = scanner.nextLine().trim();

                        if (jmeno.matches("^[\\p{L}]+$")) {
                            break;
                        } else {
                            System.out.println("⚠️ Chyba: Jméno smí obsahovat pouze text (písmena). Zkuste to znovu.");
                        }
                    }

                    String prijmeni = "";
                    while (true) {
                        System.out.print("Příjmení: ");
                        prijmeni = scanner.nextLine().trim();

                        if (prijmeni.matches("^[\\p{L}]+$")) {
                            break;
                        } else {
                            System.out.println("⚠️ Chyba: Příjmení smí obsahovat pouze text (písmena). Zkuste to znovu.");
                        }
                    }

                    int rok = 0;
                    while (true) {
                        System.out.print("Rok narození: ");
                        String vstupRok = scanner.nextLine().trim();

                        try {
                            rok = Integer.parseInt(vstupRok);
                            break;
                        } catch (NumberFormatException e) {
                            System.out.println("⚠️ Napiš platný rok narození.");
                        }
                    }

                    int noveId = sprava.generujId();

                    if (skupina.equals("1")) {
                        Zamestnanec novyAnalytik = new DatovyAnalytik(noveId, jmeno, prijmeni, rok);
                        sprava.pridatZamestnance(novyAnalytik);
                        System.out.println("✅ Datový analytik byl úspěšně přidán.");
                    } else if (skupina.equals("2")) {
                        Zamestnanec novySpecialista = new BezpecnostniSpecialista(noveId, jmeno, prijmeni, rok);
                        sprava.pridatZamestnance(novySpecialista);
                        System.out.println("✅ Bezpečnostní specialista byl úspěšně přidán.");
                    }

                    databazeSQL.ulozitVse(sprava.getVsiZamestnanci());
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
                    databazeSQL.ulozitVse(sprava.getVsiZamestnanci());
                    break;

                case "c":
                    System.out.print("Zadejte ID zaměstnance k odebrání: ");
                    int idSmazat = Integer.parseInt(scanner.nextLine());
                    sprava.odebratZamestnance(idSmazat);
                    databazeSQL.ulozitVse(sprava.getVsiZamestnanci());
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
                case "k":
                    SpravaSouboru export = new SpravaSouboru();
                    export.ulozitDoTextovehoSouboru(sprava.getVsiZamestnanci(), "zamestnanci_export.csv");
                    break;
                case "l":

                    sprava.getVsiZamestnanci().clear();

                    sprava.nastavDalsiId(1);

                    databazeSQL.vymazatCelouDatabazi();

                    System.out.println("✅ Program je nyní v čistém stavu a připraven na nová data.");
                    break;
                case "0":

                    databazeSQL.ulozitVse(sprava.getVsiZamestnanci());

                    System.out.println("Ukončuji program. Na shledanou!");
                    beziProgram = false;
                    break;

                default:
                    System.out.println("❌ Neplatná volba, zkuste to znovu.");
            }
        }

        scanner.close();
    }
}