import java.util.List;
public class BezpecnostniSpecialista extends Zamestnanec {

    public BezpecnostniSpecialista(int id, String jmeno, String prijmeni, int rokNarozeni) {
        super(id, jmeno, prijmeni, rokNarozeni);
    }

    @Override
    public void spustitDovednost(SpravaZamestnancu sprava) {
        System.out.println("--- Globální bezpečnostní audit (vypracoval: " + getPrijmeni() + ") ---");

        // Získáme seznam úplně všech zaměstnanců v naší databázi
        List<Zamestnanec> vsichniZamestnanci = sprava.getVsiZamestnanci();

        if (vsichniZamestnanci.isEmpty()) {
            System.out.println("Databáze je prázdná, není koho auditovat.");
            return;
        }

        // Cyklus projde jednoho zaměstnance po druhém
        for (Zamestnanec z : vsichniZamestnanci) {
            int pocetSpolupracovniku = z.getSeznamSpolupracovniku().size();

            // Pokud nemá žádné spolupráce, skóre je nula
            if (pocetSpolupracovniku == 0) {
                System.out.println("Zaměstnanec: " + z.getJmeno() + " " + z.getPrijmeni() + " | Skóre: 0.0 (Žádné vazby)");
                continue; // Přeskočíme zbytek kódu a jdeme na dalšího člověka
            }

            double soucetHodnoceni = 0;
            // Projdeme spolupráce TOHOTO konkrétního zaměstnance a sečteme hodnocení
            for (Spoluprace s : z.getSeznamSpolupracovniku()) {
                soucetHodnoceni += s.getUroven().getHodnota();
            }

            // Vlastní algoritmus pro výpočet
            double prumer = soucetHodnoceni / pocetSpolupracovniku;
            double rizikoveSkore = prumer * pocetSpolupracovniku;

            // Vypsání výsledku pro daného zaměstnance
            System.out.println("Zaměstnanec: " + z.getJmeno() + " " + z.getPrijmeni() +
                    " | Počet vazeb: " + pocetSpolupracovniku +
                    " | Rizikové skóre: " + rizikoveSkore);
        }
    }

}