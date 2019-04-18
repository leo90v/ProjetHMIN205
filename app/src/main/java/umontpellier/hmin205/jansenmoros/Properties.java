package umontpellier.hmin205.jansenmoros;

// A singleton to store global variables
public class Properties {
    private static Properties mInstance= null;

    private boolean isLoggedIn;
    private String username;

    protected Properties(){
        this.isLoggedIn = false;
        this.username = "";
    }

    public static synchronized Properties getInstance() {
        if(null == mInstance){
            mInstance = new Properties();
        }
        return mInstance;
    }

    public void setLogin(boolean isLoggedIn, String username){
        this.isLoggedIn = isLoggedIn;
        this.username = username;
    }

    public String getUsername(){return this.username;}

    public boolean isLoggedIn(){return this.isLoggedIn;}
}
