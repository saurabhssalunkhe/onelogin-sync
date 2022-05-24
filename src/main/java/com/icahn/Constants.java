package com.icahn;

public final class Constants {

    private Constants() {
            // restrict instantiation
    }
    public static final String accessToken_URL = "https://api.us.onelogin.com/auth/oauth2/v2/token";
    public static final String oneloginUser_URL = "https://api.us.onelogin.com/api/2/users";
    public static final String driverName = "com.mysql.cj.jdbc.Driver";  //com.mysql.jdbc.Driver
    public static final int limit=99;
    public static final String source="ADP-DW-Sync";
    public static final String revokeToken_URL = "https://api.us.onelogin.com/auth/oauth2/revoke";
}