package mobile.bibliotekaplus;

class Zamowienia { /*
    INSTANCE FIELDS
     */
    private String id;
    private String data;
    private String kod;
    private String placowka;
    private Boolean platnosc;

    public Boolean getPlatnosc() {
        return platnosc;
    }

    public void setPlatnosc(Boolean platnosc) {
        this.platnosc = platnosc;
    }
    /*GETTERS AND SETTERS  */

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getKod() {
        return kod;
    }

    public void setKod(String kod) {
        this.kod = kod;
    }

    public String getPlacowka() {
        return placowka;
    }

    public void setPlacowka(String placowka) {
        this.placowka = placowka;
    }
}