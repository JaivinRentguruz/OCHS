package com.ochs.app.apicall;

import android.graphics.Bitmap;
import android.media.Image;

public class ApiEndPoint
{
    //live = api.flexiicar.com/api  //test == api.flexiicar.com/v1/api

    public static final String TEST = "api.flexiicar.com/v1/api";
    public static final String LIVE = "api.flexiicar.com/api";
  //  public static final String LIVE = "api.flexiicar.com/v1/api";
    // ApiEndPoint
    public static final String BASE_URL_LOGIN ="https://"+LIVE +"/login/";
    public static final String BASE_URL_BOOKING ="https://" +LIVE + "/booking/";
    public static final String BASE_URL_PAYMENT ="https://" +LIVE +"/Payment/";
    public static final String BASE_URL_CUSTOMER ="https://"+LIVE +"/Customer/";
    public static final String BASE_URL_SETTINGS ="https://" +LIVE +"/Settings/";
    public static final String BASE_URL_CHECKOUT ="https://" +LIVE + "/CheckOut/";
    public static final String BASE_URL_CHECKIN="https://" +LIVE + "/CheckIn/";
    public static final String BASE_URL_VEHICLE="https://"+LIVE+ "/Vehicle/";
    public static final String BASE_URL_HUFKEY="https://" + LIVE+ "/HUFKeyAccessLog/";
   // public static final String BASE_URL_BOOKING_V1="https://api.flexiicar.com/v1/api/Booking/";
    public static final String CURRENTBOOKING="CurrentBookingList";

    public static final String UPLOAD_BASE_URL = "";
//http://api.rentguruz.com/api/login/
    //LOGIN
    public static final String LOGIN_VERIFICATION = "LoginVerification";
    public static final String APP_INTIALIZATION = "AppInitialization";
    public static final String REGISTRATION = "Registration";
    public static final String CHANGEPASSWORD = "ChangePassword";

    public static Bitmap firstImage = null;
    public static Bitmap secondImage = null;

    //BOOKING
    public static final String LOCATION_LIST = "locationlist";
    public static final String BOOKING = "booking";
    public static final String LOCATION_SEARCH_LIST="locationsearchlist";
    public static final String LOCATION_SEARCH_BY_DISTANCE="LocationSearchByDistance";
    public static final String FILTERLIST="filterlist";
    public static final String GETDEFAULTCREDITCARD="GetDefaultCreditCard";
    public static final String GETDCREDITCARDLIST="GetCreditCardList";
    public static final String GETTERMSCONDITION="GetTermsCondition";
    public static final String GETPAYMENT="PaymentProcess";
    public static final String UPDATECREDITCARD="UpdateCreditCard";
    public static final String DELETECREDITCARD="DeleteCreditCard";
    public static final String ADDCREDITCARD="AddCreditCard";
    public static final String GETPICKUPLIST="GetPickupList";
    public static final String GETDELIVERYLIST="GetDeliveryList";
    public static final String CALCULATE_DISTANCE="CalculatesDistance";
    public static final String CANCELBOOKING="CancelBooking";
    public static final String VEHICLE_LIST="VehicleList";
    public static final String GET_MOBILEKEY_DETAILS="GetMobileKeyDetail";
    public static final String GET_BOOKING_CANCEL_CHARGE="GetBookingCancelCharge";
    public static final String PROCEED_CANCELLATION="ProceedCancellation";

    public static final String ADD_BILLING="AddBilling";

    public static final String GET_ADDITIONAL_DRIVER_LIST="GetAdditionalDriverList";

    //PAYMENT
    public static final String PAYMENTPROCESS="PaymentProcess";

    //CUSTOMER
    public static final String GETCUSTOMERPROFILE="CustomerProfile";
    public static final String UPDATECUSTOMERPROFILE="UpdateCustomerProfile";
    public static final String GETCUSTOMERSUMMARY="customerSummary";
    public static final String GETRESERVATIONLIST="ReservationList";
    public static final String GETAGREEMENTLIST="AgreementsList";

    public static final String GETCUSTOMERINSURANCE="CustomerInsurance";
    public static final String UPDATECUSTOMERINSURANCE="UpdateCustomerInsurance";
    public static final String ADD_INSURANCE="AddInsurance";

    public static final String DRIVINGLICENSE="DrivingLicense";
    public static final String UPDATEDRIVINGLICENSE="UpdateDrivingLicense";
    public static final String ADD_DRIVER="AddDriver";

    public static final String GETACCOUNTSTATEMENT="AccountStatementList";

    public static final String ACTIVITYTIMELINELIST="ActivityTimeLineList";
    public static final String ADDCUSTOMERACTIVITY="AddCustomerActivity";
    public static final String UPDATECUSTOMERACTIVITY="UpdateCustomerActivity";
    public static final String ACTIVITYTIMELINE = "ActivityTimeLine";

    public static final String FORGETPASSWORD = "ForgotPassword";
    public static final String ADDPROFILEPICTURE = "AddProfilePicture";
    public static final String REMOVEPROFILEPICTURE = "RemoveProfilePicture";
    public static final String ACCOUNTSTATEMENTSTATUSUPDATE = "AccountStatementStatusUpdate";

    public static final String CURRENT_BOOKING_LIST = "CurrentBookingList";

    //SETTINGS
    public static final String GETCOUNTRYLIST="CountryList";
    public static final String STATELIST="StateList";
    public static final String ACTIVITYTYPELIST="ActivityTypeList";
    public static final String INSURANCECOMPANYLIST="InsuranceCompanyList";
    //CheckOut
    public static final String GETSELFCHECKOUT="GetSelfCheckOut";
    public static final String UPDATESELFCHECKOUT="UpdateSelfCheckOut";

    //CheckIn
    public static final String GETSELFCHECKIN="GetSelfCheckIn";
    public static final String UPDATESELFCHECKIN="UpdateSelfCheckIn";
    public static final String GETAGREEMENTREPORT="GetAgreementReport";
    public static final String VERSION_API = "v1/";

}
