import java.util.ArrayList;
import java.util.List;

public abstract class Zamestnanec {
    private int id;
    private String jmeno;
    private String prijmeni;
    private int rokNarozeni;

    
    protected List<Spoluprace> seznamSpolupracovniku;

    public Zamestnanec(int id, String jmeno, String prijmeni, int rokNarozeni) {
        this.id = id;
        this.jmeno = jmeno;
        this.prijmeni = prijmeni;
        this.rokNarozeni = rokNarozeni;
        this.seznamSpolupracovniku = new ArrayList<>();
    }

    public void pridatSpolupraci(int idKolegy, UrovenSpoluprace uroven) {
        seznamSpolupracovniku.add(new Spoluprace(idKolegy, uroven));
    }

    
    public abstract void spustitDovednost(SpravaZamestnancu sprava);

    
    public int getId() { return id; }
    public String getJmeno() { return jmeno; }
    public String getPrijmeni() { return prijmeni; }
    public int getRokNarozeni() { return rokNarozeni; }
    public List<Spoluprace> getSeznamSpolupracovniku() { return seznamSpolupracovniku; }
}