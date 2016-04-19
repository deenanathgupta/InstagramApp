package instagram.robosoft.com.mytestapplication.constant;

/**
 * Created by deena on 24/2/16.
 */
public class AppData {
    public static final String CLIENT_ID = "34ae8543c6214ca684eb28a2b63f233d";
    public static final String CLIENT_SECRET = "2d189f48a2c442fcbf5e7ba58364329c";
    public static final String accesstokn = "1792605782.34ae854.4533e192666a41bcaf546c6d8121c09f";
    public static final String CALLBACKURL = "https://www.google.co.in/";
    public static final String AUTH_URL = "https://api.instagram.com/oauth/authorize/";
    public static final String TOKENURL = "https://api.instagram.com/oauth/access_token";
    public static final String APIURL = "https://api.instagram.com/v1";

    public static final String authURLString = AUTH_URL + "?client_id=" + CLIENT_ID + "&redirect_uri=" +
            CALLBACKURL + "&response_type=code&display=touch&scope=likes+comments+relationships+public_content+follower_list";
    public static final String tokenURLString = TOKENURL + "?client_id=" + CLIENT_ID + "&client_secret=" + CLIENT_SECRET + "&redirect_uri=" + CALLBACKURL + "&grant_type=authorization_code";

    public static final String USER_INFORMATION = APIURL + "/users/" + 1792605782 + "/media/recent/?access_token=" + accesstokn;
    public static final String FOLLWERS = "https://api.instagram.com/v1/users/self/follows?access_token=" + AppData.accesstokn;

    public static final String MYPREFERENCE = "Data";
    public static final String SETTINGPREFRENCE="SettingData";
    public static final String Name = "name";
    public static final String Id = "id";
    public static final String accesstoken = "token";

    public static final String SettingKey = "SettingKey";

}

