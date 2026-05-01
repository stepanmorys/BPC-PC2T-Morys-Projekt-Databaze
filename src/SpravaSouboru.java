import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class SpravaSouboru {

    public void ulozitDoTextovehoSouboru(List<Zamestnanec> vsichniZamestnanci, String nazevSouboru) {

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(nazevSouboru))) {

            for (Zamestnanec z : vsichniZamestnanci) {

                String profese = (z instanceof DatovyAnalytik) ? "Datový analytik" : "Bezpečnostní specialista";

                String radek = z.getId() + ";" + z.getJmeno() + ";" + z.getPrijmeni() + ";" + z.getRokNarozeni() + ";" + profese;

                writer.write(radek);
                writer.newLine();
            }
            System.out.println("📄 Všichni zaměstnanci byli úspěšně uloženi do souboru: " + nazevSouboru);

        } catch (IOException e) {
            System.out.println("❌ Nastala chyba při zápisu do souboru: " + e.getMessage());
        }
    }
}
