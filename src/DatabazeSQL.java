import java.sql.*;
import java.util.List;

public class DatabazeSQL {
    // Cesta k souboru s databází (vytvoří se sama ve složce projektu)
    private static final String URL = "jdbc:sqlite:zaloha_dat.db";

    public DatabazeSQL() {
        // Tímto Javě natvrdo řekneme: "Vzbuď se a načti SQLite překladač dřív, než něco uděláš!"
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.out.println("❌ Kritická chyba: Java fyzicky nevidí soubor knihovny SQLite!");
        }

        // Při startu se ujistíme, že tabulky existují
        vytvoritTabulky();
    }

    private void vytvoritTabulky() {
        // SQL příkazy pro vytvoření dvou tabulek (pokud už neexistují)
        String sqlZamestnanci = "CREATE TABLE IF NOT EXISTS zamestnanci (id INTEGER PRIMARY KEY, jmeno TEXT, prijmeni TEXT, rok_narozeni INTEGER, profese TEXT);";
        String sqlSpoluprace = "CREATE TABLE IF NOT EXISTS spoluprace (id_zamestnance INTEGER, id_kolegy INTEGER, uroven TEXT);";

        // Připojíme se a příkazy spustíme
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sqlZamestnanci);
            stmt.execute(sqlSpoluprace);
        } catch (SQLException e) {
            System.out.println("⚠️ Upozornění: Nelze se spojit s SQL databází. Program běží v režimu bez zálohy. Chyba: " + e.getMessage());
        }
    }

    // Bod k: Uložení všech dat při ukončení programu
    public void ulozitVse(List<Zamestnanec> vsichniZamestnanci) {
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {

            // 1. Nejprve smažeme starou zálohu, ať tam nemáme duplikáty
            stmt.execute("DELETE FROM zamestnanci");
            stmt.execute("DELETE FROM spoluprace");

            // 2. Připravíme si šablony pro vkládání nových dat
            PreparedStatement vlozZ = conn.prepareStatement("INSERT INTO zamestnanci VALUES (?, ?, ?, ?, ?)");
            PreparedStatement vlozS = conn.prepareStatement("INSERT INTO spoluprace VALUES (?, ?, ?)");

            for (Zamestnanec z : vsichniZamestnanci) {
                // Naplníme šablonu pro zaměstnance
                vlozZ.setInt(1, z.getId());
                vlozZ.setString(2, z.getJmeno());
                vlozZ.setString(3, z.getPrijmeni());
                vlozZ.setInt(4, z.getRokNarozeni());
                vlozZ.setString(5, (z instanceof DatovyAnalytik) ? "Analytik" : "Specialista");
                vlozZ.executeUpdate(); // Provedeme zápis

                // Naplníme šablonu pro jeho spolupráce
                for (Spoluprace s : z.getSeznamSpolupracovniku()) {
                    vlozS.setInt(1, z.getId());
                    vlozS.setInt(2, s.getIdKolegy());
                    vlozS.setString(3, s.getUroven().name());
                    vlozS.executeUpdate();
                }
            }
            System.out.println("💾 Záloha do SQL databáze proběhla úspěšně.");

        } catch (SQLException e) {
            System.out.println("❌ Chyba při ukládání do SQL: " + e.getMessage());
        }
    }

    // Bod l: Načtení všech dat při spuštění programu
    public void nacistVse(SpravaZamestnancu sprava) {
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {

            // 1. Načteme zaměstnance
            ResultSet rsZ = stmt.executeQuery("SELECT * FROM zamestnanci");
            int maximalniId = 0; // Pamatuje si nejvyšší ID, ať víme, kde má počítadlo pokračovat

            while (rsZ.next()) {
                int id = rsZ.getInt("id");
                String jmeno = rsZ.getString("jmeno");
                String prijmeni = rsZ.getString("prijmeni");
                int rok = rsZ.getInt("rok_narozeni");
                String profese = rsZ.getString("profese");

                Zamestnanec z = profese.equals("Analytik") ?
                        new DatovyAnalytik(id, jmeno, prijmeni, rok) :
                        new BezpecnostniSpecialista(id, jmeno, prijmeni, rok);

                // Přidáme přímo do interního seznamu
                sprava.getVsiZamestnanci().add(z);

                if (id > maximalniId) maximalniId = id;
            }

            // Nastavíme počítadlo na další volné ID
            sprava.nastavDalsiId(maximalniId + 1);

            // 2. Načteme spolupráce
            ResultSet rsS = stmt.executeQuery("SELECT * FROM spoluprace");
            while (rsS.next()) {
                int idZ = rsS.getInt("id_zamestnance");
                int idK = rsS.getInt("id_kolegy");
                UrovenSpoluprace uroven = UrovenSpoluprace.valueOf(rsS.getString("uroven"));

                Zamestnanec z = sprava.najitZamestnance(idZ);
                if (z != null) {
                    z.pridatSpolupraci(idK, uroven);
                }
            }

            if (!sprava.getVsiZamestnanci().isEmpty()) {
                System.out.println("📂 Data ze zálohy SQL byla úspěšně načtena.");
            }

        } catch (SQLException e) {
            // Záměrně nevyhazujeme velkou chybu do konzole, protože při prvním spuštění databáze ještě existovat nemusí.
        }
    }
}