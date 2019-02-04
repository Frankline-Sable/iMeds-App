package com.fsdev.imeds;

/**
 * Created by Frankline Sable on 13/10/2017.
 */

public interface App_Urls {
    String url_signUp="http://192.168.137.1/iMeds/db_signup.php?imeds";
    String url_signIn="http://192.168.137.1/iMeds/db_signin.php?imeds";
    String url_feeds="http://192.168.137.1/iMeds/meds_Feeds.php";
    String url_forums="http://192.168.137.1/iMeds/meds_Forums.php";
    String url_events="http://192.168.137.1/iMeds/meds_Events.php";
    String url_forum_post="http://192.168.137.1/iMeds/meds_forum_post.php?imeds";
    String url_forum_reply="http://192.168.137.1/iMeds/meds_forum_comment.php?imeds";
    String url_upload_pImage="http://192.168.137.1/iMeds/uploadProfileImage.php";
    String url_update_account="http://192.168.137.1/iMeds/db_updateAccount.php";
    String url_fetch_account="http://192.168.137.1/iMeds/db_fetchAccount.php";
    String url_fetch_wiki="http://192.168.137.1/iMeds/wikis.json";
    String url_check_ntw="http://192.168.137.1/iMeds/check_ntw_state.php";
    String url_prefix="http://192.168.137.1/iMeds/";
}