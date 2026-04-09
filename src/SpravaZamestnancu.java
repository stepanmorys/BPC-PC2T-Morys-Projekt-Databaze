import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;


public class SpravaZamestnancu {
    // Zde uchováváme všechny zaměstnance v paměti
    private List<Zamestnanec> databaze;
    // Počítadlo pro automatické přidělování ID
    private int dalsiId;

    public SpravaZamestnancu() {
        this.databaze = new ArrayList<>();
        this.dalsiId = 1; // Jak jsi chtěl, začínáme od 1
    }

    // Metoda, která nám dá správné ID pro nového člověka
    public int generujId() {
        return dalsiId;
    }

    // Přidání zaměstnance do seznamu
    public void pridatZamestnance(Zamestnanec z) {
        databaze.add(z);
        System.out.println("✅ Zaměstnanec " + z.getJmeno() + " " + z.getPrijmeni() + " byl úspěšně přidán s ID: " + z.getId());
        dalsiId++; // Zvýšíme ID pro dalšího příchozího
    }

    // Vyhledání zaměstnance podle ID (bod d ze zadání)
    public Zamestnanec najitZamestnance(int id) {
        // Tzv. for-each cyklus: "Pro každého Zaměstnance z v naší databázi udělej..."
        for (Zamestnanec z : databaze) {
            if (z.getId() == id) {
                return z; // Našli jsme ho!
            }
        }
        return null; // Pokud cyklus dojede do konce a nikoho nenajde, vrátí prázdnou hodnotu
    }

    // Pomocná metoda pro vypsání všech (bod f, h ze zadání budeme stavět na tomto)
    public List<Zamestnanec> getVsiZamestnanci() {
        return databaze;
    }
    // --- PŘIDEJ TYTO METODY DO SpravaZamestnancu ---

    // Odebrání zaměstnance včetně všech vazeb
    public void odebratZamestnance(int id) {
        Zamestnanec zKeSmazani = najitZamestnance(id);

        if (zKeSmazani != null) {
            // 1. Odstraníme ho z naší hlavní databáze
            databaze.remove(zKeSmazani);

            // 2. Musíme ho smazat ze všech záznamů o spolupráci u ostatních
            for (Zamestnanec z : databaze) {
                // Použijeme šikovnou funkci removeIf, která přečte seznam a smaže shody
                // Zápis v závorce znamená: "Smaž tu spolupráci, kde se idKolegy rovná našemu id"
                z.getSeznamSpolupracovniku().removeIf(spoluprace -> spoluprace.getIdKolegy() == id);
            }
            System.out.println("✅ Zaměstnanec a všechny jeho vazby byly úspěšně smazány.");
        } else {
            System.out.println("❌ Zaměstnanec s ID " + id + " nebyl nalezen.");
        }
    }
    // Přidání spolupráce mezi dvěma zaměstnanci
    public void vytvoritSpolupraci(int idZamestnance, int idKolegy, int volbaUrovne) {
        Zamestnanec z1 = najitZamestnance(idZamestnance);
        Zamestnanec z2 = najitZamestnance(idKolegy);

        // Zkontrolujeme, jestli oba vůbec existují a jestli se nesnaží přidat sám sebe
        if (z1 == null || z2 == null) {
            System.out.println("❌ Chyba: Jeden nebo oba zaměstnanci nebyli v databázi nalezeni.");
            return;
        }
        if (idZamestnance == idKolegy) {
            System.out.println("❌ Chyba: Zaměstnanec nemůže spolupracovat sám se sebou.");
            return;
        }

        // Překlad čísla z konzole na náš Enum
        UrovenSpoluprace uroven;
        if (volbaUrovne == 1) uroven = UrovenSpoluprace.DOBRA;
        else if (volbaUrovne == 2) uroven = UrovenSpoluprace.PRUMERNA;
        else uroven = UrovenSpoluprace.SPATNA;

        z1.pridatSpolupraci(idKolegy, uroven);
        System.out.println("✅ Spolupráce úspěšně zaevidována!");
    }
    // Výpis počtu zaměstnanců v jednotlivých skupinách
    public void vypisPocetVeSkupinach() {
        int pocetAnalytiku = 0;
        int pocetSpecialistu = 0;

        for (Zamestnanec z : databaze) {
            // Zeptáme se Javy, do jaké třídy (skupiny) objekt patří
            if (z instanceof DatovyAnalytik) {
                pocetAnalytiku++;
            } else if (z instanceof BezpecnostniSpecialista) {
                pocetSpecialistu++;
            }
        }

        System.out.println("--- Počty zaměstnanců ---");
        System.out.println("Datoví analytici: " + pocetAnalytiku);
        System.out.println("Bezpečnostní specialisté: " + pocetSpecialistu);
    }
// --- PŘIDEJ TUTO METODU DO SpravaZamestnancu ---

    // Abecední výpis zaměstnanců podle příjmení ve skupinách
    public void vypisAbecedneVeSkupinach() {
        // 1. Připravíme si dočasné prázdné seznamy pro obě skupiny
        List<Zamestnanec> analytici = new ArrayList<>();
        List<Zamestnanec> specialiste = new ArrayList<>();

        // 2. Rozdělíme zaměstnance do správných seznamů
        for (Zamestnanec z : databaze) {
            if (z instanceof DatovyAnalytik) {
                analytici.add(z);
            } else if (z instanceof BezpecnostniSpecialista) {
                specialiste.add(z);
            }
        }

        // 3. Seřadíme seznamy podle příjmení
        // Zápis Zamestnanec::getPrijmeni je zkratka, která říká:
        // "Vezmi každého zaměstnance, zavolej jeho metodu getPrijmeni() a podle toho ho zařaď"
        analytici.sort(Comparator.comparing(Zamestnanec::getPrijmeni));
        specialiste.sort(Comparator.comparing(Zamestnanec::getPrijmeni));

        // 4. Vypíšeme výsledek do konzole úhledně pod sebe
        System.out.println("\n--- Datoví analytici (abecedně) ---");
        if (analytici.isEmpty()) {
            System.out.println("Žádní datoví analytici v databázi.");
        } else {
            for (Zamestnanec a : analytici) {
                System.out.println(a.getPrijmeni() + " " + a.getJmeno() + " (ID: " + a.getId() + ")");
            }
        }

        System.out.println("\n--- Bezpečnostní specialisté (abecedně) ---");
        if (specialiste.isEmpty()) {
            System.out.println("Žádní bezpečnostní specialisté v databázi.");
        } else {
            for (Zamestnanec s : specialiste) {
                System.out.println(s.getPrijmeni() + " " + s.getJmeno() + " (ID: " + s.getId() + ")");
            }
        }
    }
    // --- PŘIDEJ TOTO DO SpravaZamestnancu.java ---

    // Bod d: Vyhledání a detailní výpis zaměstnance
    public void vypisDetailZamestnance(int id) {
        Zamestnanec z = najitZamestnance(id);
        if (z == null) {
            System.out.println("❌ Zaměstnanec s ID " + id + " nebyl nalezen.");
            return;
        }

        System.out.println("\n--- Detail zaměstnance ---");
        System.out.println("ID: " + z.getId());
        System.out.println("Jméno a příjmení: " + z.getJmeno() + " " + z.getPrijmeni());
        System.out.println("Rok narození: " + z.getRokNarozeni());

        // Pomocí instanceof zjistíme profesi pro hezčí výpis
        String profese = (z instanceof DatovyAnalytik) ? "Datový analytik" : "Bezpečnostní specialista";
        System.out.println("Profese: " + profese);

        List<Spoluprace> spoluprace = z.getSeznamSpolupracovniku();
        System.out.println("Celkový počet vazeb: " + spoluprace.size());

        // Spočítáme jednotlivé kvality spolupráce
        if (!spoluprace.isEmpty()) {
            int dobra = 0, prumerna = 0, spatna = 0;
            for (Spoluprace s : spoluprace) {
                if (s.getUroven() == UrovenSpoluprace.DOBRA) dobra++;
                else if (s.getUroven() == UrovenSpoluprace.PRUMERNA) prumerna++;
                else spatna++;
            }
            System.out.println("Z toho: " + dobra + "x Dobrá, " + prumerna + "x Průměrná, " + spatna + "x Špatná");
        }
    }

    // Bod g: Celofiremní statistiky
    public void vypisFiremniStatistiky() {
        if (databaze.isEmpty()) {
            System.out.println("Databáze je prázdná, nelze vypsat statistiky.");
            return;
        }

        int maxVazeb = -1;
        Zamestnanec nejvetsiSit = null;
        int celkemDobra = 0, celkemPrumerna = 0, celkemSpatna = 0;

        for (Zamestnanec z : databaze) {
            // Hledáme člověka s nejvíce vazbami
            int pocetVazeb = z.getSeznamSpolupracovniku().size();
            if (pocetVazeb > maxVazeb) {
                maxVazeb = pocetVazeb;
                nejvetsiSit = z;
            }

            // Sčítáme všechny kvality spoluprací napříč firmou
            for (Spoluprace s : z.getSeznamSpolupracovniku()) {
                if (s.getUroven() == UrovenSpoluprace.DOBRA) celkemDobra++;
                else if (s.getUroven() == UrovenSpoluprace.PRUMERNA) celkemPrumerna++;
                else celkemSpatna++;
            }
        }

        System.out.println("\n--- Celofiremní statistiky ---");
        if (nejvetsiSit != null && maxVazeb > 0) {
            System.out.println("Zaměstnanec s nejvíce vazbami: " + nejvetsiSit.getJmeno() + " " + nejvetsiSit.getPrijmeni() + " (" + maxVazeb + " vazeb)");
        } else {
            System.out.println("Zatím nebyly navázány žádné spolupráce.");
        }

        // Zjištění převažující kvality - Math.max vybere největší číslo ze zadaných
        int nejviceSpolupraci = Math.max(celkemDobra, Math.max(celkemPrumerna, celkemSpatna));

        if (nejviceSpolupraci == 0) {
            System.out.println("Převažující kvalita spolupráce: Nelze určit (žádná data).");
        } else if (nejviceSpolupraci == celkemDobra) {
            System.out.println("Převažující kvalita spolupráce ve firmě: DOBRÁ");
        } else if (nejviceSpolupraci == celkemPrumerna) {
            System.out.println("Převažující kvalita spolupráce ve firmě: PRŮMĚRNÁ");
        } else {
            System.out.println("Převažující kvalita spolupráce ve firmě: ŠPATNÁ");
        }
    }
    // Bod i: Uložení zaměstnance do textového souboru
    public void ulozitZamestnanceDoSouboru(int id, String nazevSouboru) {
        Zamestnanec z = najitZamestnance(id);
        if (z == null) {
            System.out.println("❌ Zaměstnanec s ID " + id + " nebyl nalezen.");
            return;
        }

        // Tzv. try-with-resources. Zaručí, že se soubor po zápisu sám bezpečně zavře.
        try (PrintWriter writer = new PrintWriter(new FileWriter(nazevSouboru))) {
            // Zapisujeme základní data, každé na nový řádek
            writer.println(z.getId());
            writer.println(z.getJmeno());
            writer.println(z.getPrijmeni());
            writer.println(z.getRokNarozeni());

            // Zápis profese
            if (z instanceof DatovyAnalytik) {
                writer.println("DatovyAnalytik");
            } else {
                writer.println("BezpecnostniSpecialista");
            }

            // Zápis všech spoluprací ve formátu "ID_Kolegy,UROVEN"
            for (Spoluprace s : z.getSeznamSpolupracovniku()) {
                writer.println(s.getIdKolegy() + "," + s.getUroven().name());
            }

            System.out.println("✅ Data zaměstnance byla úspěšně uložena do souboru: " + nazevSouboru);

        } catch (IOException e) {
            // Pokud nastane chyba (např. chybí oprávnění k disku), zachytíme ji
            System.out.println("❌ Nastala chyba při ukládání do souboru: " + e.getMessage());
        }
    }
    // Bod j: Načtení zaměstnance z textového souboru
    public void nacistZamestnanceZeSouboru(String nazevSouboru) {
        // Tzv. try-with-resources pro bezpečné čtení a zavření souboru
        try (BufferedReader reader = new BufferedReader(new FileReader(nazevSouboru))) {

            // Čteme první 4 řádky se základními údaji
            int id = Integer.parseInt(reader.readLine());

            // Bezpečnostní kontrola: co když už někdo takový v databázi je?
            if (najitZamestnance(id) != null) {
                System.out.println("❌ Zaměstnanec s ID " + id + " už v systému existuje. Nelze načíst duplicitu.");
                return;
            }

            String jmeno = reader.readLine();
            String prijmeni = reader.readLine();
            int rok = Integer.parseInt(reader.readLine());
            String profese = reader.readLine();

            // Podle přečtené profese vytvoříme správný objekt
            Zamestnanec nactenyZ;
            if (profese.equals("DatovyAnalytik")) {
                nactenyZ = new DatovyAnalytik(id, jmeno, prijmeni, rok);
            } else {
                nactenyZ = new BezpecnostniSpecialista(id, jmeno, prijmeni, rok);
            }

            // Nyní čteme zbytek souboru (spolupráce), dokud nedojdeme na konec
            String radek;
            while ((radek = reader.readLine()) != null) {
                // Rozdělíme řádek podle čárky (např. "2,DOBRA" -> ["2", "DOBRA"])
                String[] casti = radek.split(",");
                if (casti.length == 2) {
                    int idKolegy = Integer.parseInt(casti[0]);
                    // UrovenSpoluprace.valueOf() převede text "DOBRA" zpět na náš Enum
                    UrovenSpoluprace uroven = UrovenSpoluprace.valueOf(casti[1]);
                    nactenyZ.pridatSpolupraci(idKolegy, uroven);
                }
            }

            // Hotového zaměstnance i s jeho "deníčkem" přidáme do naší databáze
            databaze.add(nactenyZ);

            // Malý trik: Abychom nerozbili naše automatické počítadlo ID,
            // musíme ho posunout, pokud jsme načetli někoho s vysokým ID.
            if (id >= dalsiId) {
                dalsiId = id + 1;
            }

            System.out.println("✅ Zaměstnanec " + jmeno + " " + prijmeni + " byl úspěšně načten ze souboru.");

        } catch (Exception e) {
            System.out.println("❌ Nastala chyba při čtení souboru (možná soubor neexistuje nebo je poškozený): " + e.getMessage());
        }
    }
    // Pomocná metoda pro správné nastavení počítadla ID (např. po načtení z databáze)
    public void nastavDalsiId(int noveId) {
        this.dalsiId = noveId;
    }
}
