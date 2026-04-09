public class Spoluprace {
    private int idKolegy;
    private UrovenSpoluprace uroven;

    public Spoluprace(int idKolegy, UrovenSpoluprace uroven) {
        this.idKolegy = idKolegy;
        this.uroven = uroven;
    }

    public int getIdKolegy() {
        return idKolegy;
    }

    public UrovenSpoluprace getUroven() {
        return uroven;
    }
}

