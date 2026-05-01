import java.util.List;
import java.util.HashSet;
import java.util.Set;

public class BezpecnostniSpecialista extends Zamestnanec {

    public BezpecnostniSpecialista(int id, String jmeno, String prijmeni, int rokNarozeni) {
        super(id, jmeno, prijmeni, rokNarozeni);
    }

    @Override
    public void spustitDovednost(SpravaZamestnancu sprava) {
        System.out.println("--- Globální bezpečnostní audit (vypracoval: " + getPrijmeni() + ") ---");

        List<Zamestnanec> vsichniZamestnanci = sprava.getVsiZamestnanci();

        if (vsichniZamestnanci.isEmpty()) {
            System.out.println("Databáze je prázdná, není koho auditovat.");
            return;
        }

        for (Zamestnanec z : vsichniZamestnanci) {
            int idZamestnance = z.getId();


            Set<Integer> unikatniKolegove = new HashSet<>();


            for (Spoluprace s : z.getSeznamSpolupracovniku()) {
                unikatniKolegove.add(s.getIdKolegy());
            }


            int pocetPrichozichHodnoceni = 0;
            double soucetPrijatychHodnoceni = 0;

            for (Zamestnanec ostatni : vsichniZamestnanci) {
                if (ostatni.getId() != idZamestnance) {
                    for (Spoluprace s : ostatni.getSeznamSpolupracovniku()) {
                        if (s.getIdKolegy() == idZamestnance) {
                            unikatniKolegove.add(ostatni.getId());
                            pocetPrichozichHodnoceni++;
                            soucetPrijatychHodnoceni += s.getUroven().getHodnota();
                        }
                    }
                }
            }

            int celkovyPocetVazeb = unikatniKolegove.size();

            if (celkovyPocetVazeb == 0) {
                System.out.println("Zaměstnanec: " + z.getJmeno() + " " + z.getPrijmeni() +
                        " | Počet spoluprací: 0 | Rizikové skóre: 0.00");
                continue;
            }

            double rizikoveSkore = 0;
            if (pocetPrichozichHodnoceni > 0) {
                rizikoveSkore = soucetPrijatychHodnoceni / pocetPrichozichHodnoceni;
            }

            System.out.println("Zaměstnanec: " + z.getJmeno() + " " + z.getPrijmeni() +
                    " | Počet unikátních spoluprací: " + celkovyPocetVazeb +
                    " | Rizikové skóre: " + String.format("%.2f", rizikoveSkore));
        }
    }

}