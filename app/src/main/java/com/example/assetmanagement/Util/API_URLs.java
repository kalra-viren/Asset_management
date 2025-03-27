package com.example.assetmanagement.Util;

public class API_URLs {
    public static String live_url = "https://mgltest.mahanagargas.com/IT_AssetManagement_API/";

    public static String test_url="https://mgltest.mahanagargas.com/IT_AssetManagement/";

    public static String uat_url = "http://10.0.2.2:5089/api/";

    public static String base_url = test_url;

    public static String fetch_assets_type_url = base_url + "api/Data/ListOfAssets";

    public static String validate_login_url=base_url+"api/Data/Login";

    public static String generate_QR_url=base_url+"api/Data/Add_New_Asset";

    public static String audit_fetch_details_url=base_url+"api/Data/Fetch_Details";

    public static String assign_fetch_details_url=base_url+"api/Data/Fetch_Details";

    public static String assign_update_details_url=base_url+"api/Data/Update_Asset";

    public static String fetch_locations_url=base_url+"api/Data/ListOfLocations";

    public static  String fetchUsersByLocation_url=base_url+"api/Data/ListOfUsersOnLocation?loc_id=";

    public static String regenerateQRCode_url=base_url+"api/Data/Fetch_UID";

    public static String submit_audit_status_url=base_url+"api/Data/Audit_Record";

}
