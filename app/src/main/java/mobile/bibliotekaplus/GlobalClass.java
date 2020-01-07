package mobile.bibliotekaplus;

import android.app.Application;

public class GlobalClass extends Application {
    private String userId;
    private String ksiazkaId;
    private Double userWiek;

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
