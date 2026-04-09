public enum UrovenSpoluprace {
    DOBRA(1),
    PRUMERNA(2),
    SPATNA(3);

    private final int hodnota;

    UrovenSpoluprace(int hodnota) {
        this.hodnota = hodnota;
    }

    public int getHodnota() {
        return hodnota;
    }
}
