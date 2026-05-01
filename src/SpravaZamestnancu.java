import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashSet;
import java.util.Set;


public class SpravaZamestnancu {
    private List<Zamestnanec> databaze;
    private int dalsiId;

    public SpravaZamestnancu() {
        this.databaze = new ArrayList<>();
        this.dalsiId = 1;
    }

    public int generujId() {
        return dalsiId;
    }

    public void pridatZamestnance(Zamestnanec z) {
        databaze.add(z);
        System.out.println("✅ Zaměstnanec " + z.getJmeno() + " " + z.getPrijmeni() + " byl úspěšně přidán s ID: " + z.getId());
        dalsiId++;
    }

    public Zamestnanec najitZamestnance(int id) {
        for (Zamestnanec z : databaze) {
            if (z.getId() == id) {
                return z;
            }
        }
        return null;
    }

    public List<Zamestnanec> getVsiZamestnanci() {
        return databaze;
    }

    public void odebratZamestnance(int id) {
        Zamestnanec zKeSmazani = najitZamestnance(id);

        if (zKeSmazani != null) {
            databaze.remove(zKeSmazani);

            for (Zamestnanec z : databaze) {
                z.getSeznamSpolupracovniku().removeIf(spoluprace -> spoluprace.getIdKolegy() == id);
            }
            System.out.println("✅ Zaměstnanec a všechny jeho vazby byly úspěšně smazány.");
        } else {
            System.out.println("❌ Zaměstnanec s ID " + id + " nebyl nalezen.");
        }
    }
    public void vytvoritSpolupraci(int idZamestnance, int idKolegy, int volbaUrovne) {
        Zamestnanec z1 = najitZamestnance(idZamestnance);
        Zamestnanec z2 = najitZamestnance(idKolegy);

        if (z1 == null || z2 == null) {
            System.out.println("❌ Chyba: Jeden nebo oba zaměstnanci nebyli v databázi nalezeni.");
            return;
        }
        if (idZamestnance == idKolegy) {
            System.out.println("❌ Chyba: Zaměstnanec nemůže spolupracovat sám se sebou.");
            return;
        }

        UrovenSpoluprace uroven;
        if (volbaUrovne == 1) uroven = UrovenSpoluprace.DOBRA;
        else if (volbaUrovne == 2) uroven = UrovenSpoluprace.PRUMERNA;
        else uroven = UrovenSpoluprace.SPATNA;

        z1.pridatSpolupraci(idKolegy, uroven);
        System.out.println("✅ Spolupráce úspěšně zaevidována!");
    }
    public void vypisPocetVeSkupinach() {
        int pocetAnalytiku = 0;
        int pocetSpecialistu = 0;

        for (Zamestnanec z : databaze) {
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
    public void vypisAbecedneVeSkupinach() {
        List<Zamestnanec> analytici = new ArrayList<>();
        List<Zamestnanec> specialiste = new ArrayList<>();

        for (Zamestnanec z : databaze) {
            if (z instanceof DatovyAnalytik) {
                analytici.add(z);
            } else if (z instanceof BezpecnostniSpecialista) {
                specialiste.add(z);
            }
        }
        analytici.sort(Comparator.comparing(Zamestnanec::getPrijmeni));
        specialiste.sort(Comparator.comparing(Zamestnanec::getPrijmeni));

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

        String profese = (z instanceof DatovyAnalytik) ? "Datový analytik" : "Bezpečnostní specialista";
        System.out.println("Profese: " + profese);

        Set<Integer> unikatniKolegove = new HashSet<>();

        List<Spoluprace> odchoziSpoluprace = z.getSeznamSpolupracovniku();
        for (Spoluprace s : odchoziSpoluprace) {
            unikatniKolegove.add(s.getIdKolegy());
        }

        for (Zamestnanec ostatni : databaze) {
            if (ostatni.getId() != id) {
                for (Spoluprace s : ostatni.getSeznamSpolupracovniku()) {
                    if (s.getIdKolegy() == id) {
                        unikatniKolegove.add(ostatni.getId());
                    }
                }
            }
        }

        int celkemUnikatnichVazeb = unikatniKolegove.size();
        System.out.println("Celkový počet unikátních spoluprácí: " + celkemUnikatnichVazeb);

        if (!odchoziSpoluprace.isEmpty()) {
            int dobra = 0, prumerna = 0, spatna = 0;
            for (Spoluprace s : odchoziSpoluprace) {
                if (s.getUroven() == UrovenSpoluprace.DOBRA) dobra++;
                else if (s.getUroven() == UrovenSpoluprace.PRUMERNA) prumerna++;
                else spatna++;
            }
            System.out.println("Hodnocení, která on rozdal kolegům: " + dobra + "x Dobrá, " + prumerna + "x Průměrná, " + spatna + "x Špatná");
        }
    }

    public void vypisFiremniStatistiky() {
        if (databaze.isEmpty()) {
            System.out.println("Databáze je prázdná, nelze vypsat statistiky.");
            return;
        }

        int maxVazeb = -1;
        Zamestnanec nejvetsiSit = null;
        int celkemDobra = 0, celkemPrumerna = 0, celkemSpatna = 0;

        for (Zamestnanec z : databaze) {

            int pocetVazeb = z.getSeznamSpolupracovniku().size();
            if (pocetVazeb > maxVazeb) {
                maxVazeb = pocetVazeb;
                nejvetsiSit = z;
            }

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

    public void ulozitZamestnanceDoSouboru(int id, String nazevSouboru) {
        Zamestnanec z = najitZamestnance(id);
        if (z == null) {
            System.out.println("❌ Zaměstnanec s ID " + id + " nebyl nalezen.");
            return;
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(nazevSouboru))) {

            writer.println(z.getId());
            writer.println(z.getJmeno());
            writer.println(z.getPrijmeni());
            writer.println(z.getRokNarozeni());


            if (z instanceof DatovyAnalytik) {
                writer.println("DatovyAnalytik");
            } else {
                writer.println("BezpecnostniSpecialista");
            }
            for (Spoluprace s : z.getSeznamSpolupracovniku()) {
                writer.println(s.getIdKolegy() + "," + s.getUroven().name());
            }

            System.out.println("✅ Data zaměstnance byla úspěšně uložena do souboru: " + nazevSouboru);

        } catch (IOException e) {
            System.out.println("❌ Nastala chyba při ukládání do souboru: " + e.getMessage());
        }
    }
    public void nacistZamestnanceZeSouboru(String nazevSouboru) {

        try (BufferedReader reader = new BufferedReader(new FileReader(nazevSouboru))) {

            int id = Integer.parseInt(reader.readLine());

            if (najitZamestnance(id) != null) {
                System.out.println("❌ Zaměstnanec s ID " + id + " už v systému existuje. Nelze načíst duplicitu.");
                return;
            }

            String jmeno = reader.readLine();
            String prijmeni = reader.readLine();
            int rok = Integer.parseInt(reader.readLine());
            String profese = reader.readLine();

            Zamestnanec nactenyZ;
            if (profese.equals("DatovyAnalytik")) {
                nactenyZ = new DatovyAnalytik(id, jmeno, prijmeni, rok);
            } else {
                nactenyZ = new BezpecnostniSpecialista(id, jmeno, prijmeni, rok);
            }

            String radek;
            while ((radek = reader.readLine()) != null) {
                String[] casti = radek.split(",");
                if (casti.length == 2) {
                    int idKolegy = Integer.parseInt(casti[0]);
                    UrovenSpoluprace uroven = UrovenSpoluprace.valueOf(casti[1]);
                    nactenyZ.pridatSpolupraci(idKolegy, uroven);
                }
            }
            databaze.add(nactenyZ);

            if (id >= dalsiId) {
                dalsiId = id + 1;
            }

            System.out.println("✅ Zaměstnanec " + jmeno + " " + prijmeni + " byl úspěšně načten ze souboru.");

        } catch (Exception e) {
            System.out.println("❌ Nastala chyba při čtení souboru (možná soubor neexistuje nebo je poškozený): " + e.getMessage());
        }
    }
    public void nastavDalsiId(int noveId) {
        this.dalsiId = noveId;
    }
}
