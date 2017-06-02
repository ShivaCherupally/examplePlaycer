package in.playcer.utilities;

public class AppConstants {

    // Test Server root URL
    //public static String SERVER_ROOT_URL = "http://demo.carettech.com/playcer/";

    // Beta Server root URL
    //public static String SERVER_ROOT_URL = "http://beta.playcer.in/";

    // Live Server root URL
    public static String SERVER_ROOT_URL = "http://play.playcer.in/";

    public static String UNABLETOESTABLISHCONNECTION_URL = "";

    public static final String GET_NONCE_URL = SERVER_ROOT_URL + "api/get_nonce/?controller=user&method=register";

    public static final String GET_AUTH_OTP_URL = SERVER_ROOT_URL + "api/playcerapi/auth_otp/?";

    public static final String OTP_REQUEST_URL = SERVER_ROOT_URL + "api/playcerapi/get_send_otp/?";

    public static final String REGISTER_REQUEST_URL = SERVER_ROOT_URL + "api/playcerapi/register_user/?";

    //Get My Bookings (Needs Cookie)
    public static final String GET_MYBOOKINGS_LIST_URL = SERVER_ROOT_URL + "api/playcerapi/my_bookings/?";

    //Get My Bookings (Needs Cookie) http://play.playcer.in/news/api/get_posts/?page=1
    public static final String GET_NEWS_LIST_URL = SERVER_ROOT_URL + "news/api/get_posts/?page=";

    //http://play.playcer.in/news/api/get_post/?id=14
    public static final String SINGLE_ARTICLE_URL = SERVER_ROOT_URL + "news/api/get_post/?";

    // Get Cities
    public static final String GET_CITIES_LIST_URL = SERVER_ROOT_URL + "api/playcerapi/cities/";

    //Get Sports
    public static final String SPORTS_LIST_URL = SERVER_ROOT_URL + "api/playcerapi/get_terms/?taxonomy=sports&";

    //slot=7:00&date=20151012&court_id=146&cookie=9550412688|1445857085|cp97yAmoZL6OKuMLDSKAzZ0HWTKjcaTOqkSumey6bzC|e284cab0ea72c34fc0a65b19ee76c85ec259fe4ff82c29e537a9d67655894cf3
    //public static final String CONFIRMATION_URL = SERVER_ROOT_URL+"api/playcerapi/booking_confirmation/?";

    public static final String CONFIRMATION_URL = SERVER_ROOT_URL + "api/playcerapi/create_booking/?";

    //GET SLOTS
    public static final String GET_SLOTS_URL = SERVER_ROOT_URL + "api/playcerapi/slots/?";

    public static final String ABOUT_URL = SERVER_ROOT_URL + "api/get_page/?slug=about";

    //public static final String PAYMENT_GATEWAY_RETURN_URL = SERVER_ROOT_URL + "returnData.php?";  // Company Related

    public static final String PAYMENT_GATEWAY_RETURN_URL = SERVER_ROOT_URL + "returnDatav2.php?";  // Client Related

    // public static final String PAYMENT_GATEWAY_BILL_GENERATOR_URL = SERVER_ROOT_URL + "vanity.php? // Company Related

    public static final String PAYMENT_GATEWAY_BILL_GENERATOR_URL = SERVER_ROOT_URL + "vanityv2.php?"; // Client Related

    //public static final String ORDER_UPDATE_URL = SERVER_ROOT_URL+"api/playcerapi/update_order/";

    public static final String ORDER_UPDATE_URL = SERVER_ROOT_URL + "api/playcerapi/update_order_status/";

    public static final String ORDER_FULL_DETAILS_URL = SERVER_ROOT_URL + "api/playcerapi/booking_details/?";
    //http://beta.playcer.in/api/playcerapi/my_bookings_full

    public static final String EVENTS_LIST_URL = SERVER_ROOT_URL + "api/eventsapi/events/";

    public static final String SINGLE_EVENT_URL = SERVER_ROOT_URL + "api/eventsapi/single_event/?";

    public static final String EVENT_BOOKING_URL = SERVER_ROOT_URL + "api/eventsapi/event_booking/";

    public static final String EVENT_ORDER_UPDATE_URL = SERVER_ROOT_URL + "api/eventsapi/event_booking_update/";

    public static final String GET_MY_EVENTS_BOOKINGS_LIST_URL = SERVER_ROOT_URL + "api/eventsapi/event_my_bookings/";

    public static final String ORDER_FULL_EVENT_DETAILS_URL = SERVER_ROOT_URL + "api/eventsapi/event_single_booking/?";

    public static final String COUPON_CODE_AMOUNT_REQUEST_URL = SERVER_ROOT_URL + "api/couponapi/check_coupon/?";

    public static final String INVITE_FRIENDS_REQUEST_URL = SERVER_ROOT_URL + "api/promotionapi/referral_promo/?";

    //http://play.playcer.in/api/walletapi/credits
    public static final String WALLET_BALANCE_REQUEST_URL = SERVER_ROOT_URL + "api/walletapi/credits/?";
}