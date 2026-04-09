// Datový analytik
public class DatovyAnalytik extends Zamestnanec {

    public DatovyAnalytik(int id, String jmeno, String prijmeni, int rokNarozeni) {
        super(id, jmeno, prijmeni, rokNarozeni);
    }

    @Override
    public void spustitDovednost(SpravaZamestnancu sprava) {
        System.out.println("--- Analýza sítě: " + getJmeno() + " " + getPrijmeni() + " ---");

        if (seznamSpolupracovniku.isEmpty()) {
            System.out.println("Nemám žádné spolupracovníky k analýze.");
            return;
        }

        int maxSpolecnych = -1;
        int idNejlepsihoKolegy = -1;

        // 1. Projdeme všechny mé spolupracovníky
        for (Spoluprace mojeSpoluprace : seznamSpolupracovniku) {
            int idKolegy = mojeSpoluprace.getIdKolegy();
            Zamestnanec kolega = sprava.najitZamestnance(idKolegy);

            if (kolega != null) {
                int spolecnych = 0;
                // 2. Projdeme spolupracovníky mého kolegy a hledáme shodu v mém seznamu
                for (Spoluprace jehoSpoluprace : kolega.getSeznamSpolupracovniku()) {
                    for(Spoluprace s : seznamSpolupracovniku) {
                        if(s.getIdKolegy() == jehoSpoluprace.getIdKolegy()) {
                            spolecnych++;
                        }
                    }
                }

                // 3. Pamatujeme si toho, s kým máme shodu největší
                if (spolecnych > maxSpolecnych) {
                    maxSpolecnych = spolecnych;
                    idNejlepsihoKolegy = idKolegy;
                }
            }
        }

        if (idNejlepsihoKolegy != -1) {
            Zamestnanec nejKolega = sprava.najitZamestnance(idNejlepsihoKolegy);
            System.out.println("Nejvíce společných známých mám s kolegou: " + nejKolega.getJmeno() + " " + nejKolega.getPrijmeni() + " (Společných: " + maxSpolecnych + ")");
        }

    }
}
