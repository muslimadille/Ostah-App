package com.ostah_app.utiles

import java.util.*

object Q {
    /*************** Locales  ***********/
    /**..................api......................................***/
    const val BASE_URL = "https://dash.ostaaa.com/api/"
    const val IMAGES_PATH = "https://dash.ostaaa.com/"

    const val LOGIN_API="login"
    const val SERVICES_API="services"
    const val USER_PROFILE="user/profile"
    const val USER_ALL_ORDERS="user/tickets"
    const val USER_PREVIOUS_ORDERS="user/previous-tickets"
    const val OSTAHS_LIST_API="ostaaas/search"
    const val CREATE_ORDER="user/ticket/create"
    const val DIRECT_ORDER="user/direct-ticket/create"
    const val CANCEL_ORDER="user/ticket/cancel"
    const val DONE_ORDER="user/ticket/finish"
    const val USER_SLIDERS="user/slides"
    const val OSTAH_SLIDERS="ostaaa/slides"
    const val PRIVACY="privacy-policy"
    const val ABOUT="about-us"
    const val CONTACT_US="contact-us"
    const val USER_UPDATE="user/profile/update"
    const val USER_REGISTER="register"
    const val VERIFICATION_API="send-message"
    const val SEND_CODE_API="verify-user"
    const val CHANGE_PASSWORD_USR="user/password/update"
    const val USER_PHONE_UPDATE="user/phonenumber/update"
    const val USER__UPDATET_PHONE_VERIFI="user/phonenumber/verify"
    const val ORDER_STATES="user/ticket-statuses"


    /*****************ostah apis*******************************************************/

    const val OSTAH_LAST_ORDERS="ostaaa/last-tickets"
    const val OSTAH_ORDERS="ostaaa/tickets"
    const val OSTAH_PREVIOS_ORDERS="ostaaa/previous-tickets"
    const val OSTAH_PROFILE="ostaaa/profile"
    const val SENDER_DATA="ostaaa/user"
    const val CHANGE_PASSWORD_OSTAH="ostaaa/password/update"
    const val UPDATE_OSTAH="ostaaa/profile/update"
    const val UPDATE_ORDER="ostaaa/ticket/refresh"
    const val UPDATE_OSTAH_PHONE="ostaaa/phonenumber/update"
    const val UPDATE_OSTAH_PHONE_VERIFY="ostaaa/phonenumber/verify"

    /*****************************************************************/


    const val MODE_PRIVATE = 0
    const val PREF_FILE = "ostah_pref"
    const val SELECTED_LOCALE_PREF = "sehaty_selected_locale"

    const val TYPE_USER = 1
    const val TYPE_OSTA = 2

    const val LOCALE_AR_INDEX = 0
    const val LOCALE_EN_INDEX = 1
    var FIRST_TIME = true
    var IS_FIRST_TIME = "first_time"

    var IS_LOGIN="is_login"
    var USER_NAME="USER_NAME"
    var USER_PHONE="USER_PHONE"
    var USER_EMAIL="USER_EMAIL"
    var USER_ID="user_id"
    var USER_GENDER="gender_id"
    var USER_BIRTH="USER_BIRTH"
    var NORMAL_USER=1
    var OSTAH_USER=2
    var USER_TYPE="user_type"
    val NOTIFICATION_TOKEN="notification_token"








    const val FIRST_TIME_PREF = "ADW_first_time"

}