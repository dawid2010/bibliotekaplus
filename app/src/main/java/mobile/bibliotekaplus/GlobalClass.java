package mobile.bibliotekaplus;

import android.app.Application;

public class GlobalClass extends Application {
    private String userId;

    public String getKsiazkaId() {
        return ksiazkaId;
    }

    public void setKsiazkaId(String ksiazkaId) {
        this.ksiazkaId = ksiazkaId;
    }

    private String ksiazkaId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
