package mobile.bibliotekaplus;

import android.app.Application;

import java.util.Date;

public class GlobalClass extends Application {
    private String userId;
    private String ksiazkaId;
    private Double userWiek;
    private String zamowienieId;
    private String placowka;
    private String plec;
    private String kodU;
    private Date rankStart;
    private Date rankEnd;

    public Date getRankStart() {
        return rankStart;
    }

    public void setRankStart(Date rankStart) {
        this.rankStart = rankStart;
    }

    public Date getRankEnd() {
        return rankEnd;
    }

    public void setRankEnd(Date rankEnd) {
        this.rankEnd = rankEnd;
    }

    public String getKodU() {
        return kodU;
    }

    public void setKodU(String kodU) {
        this.kodU = kodU;
    }

    public String getPlec() {
        return plec;
    }

    public void setPlec(String plec) {
        this.plec = plec;
    }

    public String getPlacowka() {
        return placowka;
    }

    public void setPlacowka(String placowka) {
        this.placowka = placowka;
    }

    public String getZamowienieId() {
        return zamowienieId;
    }

    public void setZamowienieId(String zamowienieId) {
        this.zamowienieId = zamowienieId;
    }

    public Double getUserWiek() {
        return userWiek;
    }
    public void setUserWiek(Double userWiek) {
        this.userWiek = userWiek;
    }

    public String getKsiazkaId() {
        return ksiazkaId;
    }
    public void setKsiazkaId(String ksiazkaId) {
        this.ksiazkaId = ksiazkaId;
    }
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
}
