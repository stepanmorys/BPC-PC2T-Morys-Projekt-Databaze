import java.sql.*;
import java.util.List;

public class DatabazeSQL {

    private static final String URL = "jdbc:sqlite:zaloha_dat.db";

    public DatabazeSQL() {

        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.out.println("❌ Kritická chyba: Java fyzicky nevidí soubor knihovny SQLite!");
        }

        vytvoritTabulky();
    }

    private void vytvoritTabulky() {

        String sqlZamestnanci = "CREATE TABLE IF NOT EXISTS zamestnanci (id INTEGER PRIMARY KEY, jmeno TEXT, prijmeni TEXT, rok_narozeni INTEGER, profese TEXT);";
        String sqlSpoluprace = "CREATE TABLE IF NOT EXISTS spoluprace (id_zamestnance INTEGER, id_kolegy INTEGER, uroven TEXT);";

        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sqlZamestnanci);
            stmt.execute(sqlSpoluprace);
        } catch (SQLException e) {
            System.out.println("⚠️ Upozornění: Nelze se spojit s SQL databází. Program běží v režimu bez zálohy. Chyba: " + e.getMessage());
        }
    }

    public void ulozitVse(List<Zamestnanec> vsichniZamestnanci) {
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {

            stmt.execute("DELETE FROM zamestnanci");
            stmt.execute("DELETE FROM spoluprace");

            PreparedStatement vlozZ = conn.prepareStatement("INSERT INTO zamestnanci VALUES (?, ?, ?, ?, ?)");
            PreparedStatement vlozS = conn.prepareStatement("INSERT INTO spoluprace VALUES (?, ?, ?)");

            for (Zamestnanec z : vsichniZamestnanci) {

                vlozZ.setInt(1, z.getId());
                vlozZ.setString(2, z.getJmeno());
                vlozZ.setString(3, z.getPrijmeni());
                vlozZ.setInt(4, z.getRokNarozeni());
                vlozZ.setString(5, (z instanceof DatovyAnalytik) ? "Analytik" : "Specialista");
                vlozZ.executeUpdate();

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

    public void nacistVse(SpravaZamestnancu sprava) {
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {

            ResultSet rsZ = stmt.executeQuery("SELECT * FROM zamestnanci");
            int maximalniId = 0;

            while (rsZ.next()) {
                int id = rsZ.getInt("id");
                String jmeno = rsZ.getString("jmeno");
                String prijmeni = rsZ.getString("prijmeni");
                int rok = rsZ.getInt("rok_narozeni");
                String profese = rsZ.getString("profese");

                Zamestnanec z = profese.equals("Analytik") ?
                        new DatovyAnalytik(id, jmeno, prijmeni, rok) :
                        new BezpecnostniSpecialista(id, jmeno, prijmeni, rok);

                sprava.getVsiZamestnanci().add(z);

                if (id > maximalniId) maximalniId = id;
            }

            sprava.nastavDalsiId(maximalniId + 1);

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

        }
    }

    public void vymazatCelouDatabazi() {
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {

            stmt.execute("DELETE FROM zamestnanci");
            stmt.execute("DELETE FROM spoluprace");

            System.out.println("🗑️ SQL databáze byla úspěšně a kompletně vymazána.");

        } catch (SQLException e) {
            System.out.println("❌ Chyba při mazání databáze: " + e.getMessage());
        }
    }
}