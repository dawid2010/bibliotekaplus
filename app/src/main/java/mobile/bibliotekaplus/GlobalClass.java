package mobile.bibliotekaplus;

import android.app.Application;

public class GlobalClass extends Application {
    private String userId;
    private String ksiazkaId;
    private Double userWiek;
    private String zamowienieId;
    private String placowka;

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
