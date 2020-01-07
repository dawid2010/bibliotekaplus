package mobile.bibliotekaplus;

public class Spacecraft {
    /*
    INSTANCE FIELDS
     */
    private String id;
    private String name;
    private String propellant;
    private String epoka;
    private String gatunek;
    private String rodzaj;
    private String imageURL;
    private Double kaucja;
    private int technologyExists;

    /*GETTERS AND SETTERS  */

    public Double getKaucja() {
        return kaucja;
    }
    public void setKaucja(Double kaucja) {
        this.kaucja = kaucja;
    }
    public String getEpoka() {
        return epoka;
    }
    public void setEpoka(String epoka) {
        this.epoka = epoka;
    }
    public String getGatunek() {
        return gatunek;
    }
    public void setGatunek(String gatunek) {
        this.gatunek = gatunek;
    }
    public String getRodzaj() {
        return rodzaj;
    }
    public void setRodzaj(String rodzaj) {
        this.rodzaj = rodzaj;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getPropellant() {
        return propellant;
    }
    public void setPropellant(String propellant) {
        this.propellant = propellant;
    }
    public String getImageURL() {
        return imageURL;
    }
    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
    public int getTechnologyExists() {
        return technologyExists;
    }
    public void setTechnologyExists(int technologyExists) {
        this.technologyExists = technologyExists;
    }
    /*
    TOSTRING
     */
    @Override
    public String toString() {
        return name;
    }
}
