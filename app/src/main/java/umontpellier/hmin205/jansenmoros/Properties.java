package umontpellier.hmin205.jansenmoros;

// A singleton to store global variables
public class Properties {
    private static Properties mInstance= null;

    private boolean isLoggedIn;
    private String username;
    private String baseUrl;
    private int userType;
    private int grade;

    protected Properties(){
        this.isLoggedIn = false;
        this.username = "";
        this.baseUrl = "http://10.0.2.2:8888/";
    }

    public static synchronized Properties getInstance() {
        if(null == mInstance){
            mInstance = new Properties();
        }
        return mInstance;
    }

    public synchronized void setLogin(boolean isLoggedIn, String username, int userType, int grade){
        this.isLoggedIn = isLoggedIn;
        this.username = username;
        this.userType = userType;
        this.grade = grade;
    }

    public String getUsername(){return this.username;}

    public boolean isLoggedIn(){return this.isLoggedIn;}

    public String getBaseUrl() {return this.baseUrl;}

    public int getUserType() {return this.userType;}

    public int getGrade() {return this.grade;}
}
