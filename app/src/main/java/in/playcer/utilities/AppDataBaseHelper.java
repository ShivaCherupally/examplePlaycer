package in.playcer.utilities;

/**
 * Created by HARIKRISHNA on 8/20/2015.
 * at CaretTech
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import in.playcer.model.CartDataTable;
import in.playcer.model.EventsDataTable;

public class AppDataBaseHelper extends SQLiteOpenHelper {
    public static final String dbName ="PlaycerDB";
    public static final int dbVersion = 13; // version 12 to version 13 only webview integrated
    public static final String oneTimeRegistrationTable ="oneTimeRegistrationTable";
    public static final String deviceID ="deviceID";
    public static final String cookie ="cookie";
    public static final String userID ="userID";
    public static final String emailID ="emailID";
    public static final String mobileNumber ="mobileNumber";
    public static final String name ="name";
    public static final String role ="role";

    public static final String citiesTable ="citiesTable";
    public static final String currentCity = "currentCity";

    public static final String addToCartTable ="addToCartTable";
    public static final String cartpickedClub = "cartpickedClub";
    public static final String cartSportName = "cartSportName";
    public static final String cartCourtName = "cartCourtName";
    public static final String cartDate = "cartDate";
    public static final String cartSlotTime = "cartSlotTime";
    public static final String cartSlotPrice = "cartSlotPrice";
    public static final String cartPickedCourtID = "cartPickedCourtID";
    public static final String convenienceFee = "convenienceFee";
    public static final String convenienceLabel = "convenienceLabel";
    public static final String cartItemPosition= "cartItemPosition";

    public static final String addToEventsTable ="addToEventsTable";
    public static final String eventName = "eventName";
    public static final String eventDate = "eventDate";
    public static final String eventTime = "eventTime";
    public static final String eventPrice = "eventPrice";
    public static final String eventItemPostion = "eventItemPostion";
    public static final String event_TicketID = "event_TicketID";

    public AppDataBaseHelper(Context _ctx) {
        super(_ctx, dbName, null, dbVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + oneTimeRegistrationTable + " (" + deviceID + " TEXT, " + cookie + " TEXT, " + userID
                + " TEXT, "+ emailID + " TEXT, "+ mobileNumber + " TEXT, "+ name + " TEXT, " + role + " TEXT)");
        db.execSQL("CREATE TABLE " + addToCartTable + " (" + cartpickedClub + " TEXT, " + cartSportName + " TEXT, "
                + cartCourtName + " TEXT, " + cartDate + " TEXT, "+ cartSlotTime + " TEXT, "+ cartSlotPrice
                + " TEXT, "+ cartPickedCourtID + " TEXT, "+ convenienceFee + " TEXT, "+ convenienceLabel
                + " TEXT, "+ cartItemPosition + " INTEGER)");
        db.execSQL("CREATE TABLE " + citiesTable + " (" + currentCity + " TEXT)");

        db.execSQL("CREATE TABLE " + addToEventsTable + " (" + eventName + " TEXT, " + eventDate + " TEXT, "
                + eventTime + " TEXT, " + eventPrice + " TEXT, "+eventItemPostion + " INTEGER, "+ event_TicketID + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + oneTimeRegistrationTable);
        db.execSQL("DROP TABLE IF EXISTS " + addToCartTable);
        db.execSQL("DROP TABLE IF EXISTS " + citiesTable);
        db.execSQL("DROP TABLE IF EXISTS " + addToEventsTable);
        onCreate(db);
    }

    public void addRegisteredUDID(String _UDID, String _cookie, String _userID, String _emailID, String _mobileNumber, String _nameStr, String _role) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(deviceID, _UDID);
        cv.put(cookie, _cookie);
        cv.put(userID, _userID);
        cv.put(emailID, _emailID);
        cv.put(mobileNumber, _mobileNumber);
        cv.put(name, _nameStr);
        cv.put(role, _role);
        db.insert(oneTimeRegistrationTable, deviceID, cv);
        db.close();
    }

    public void addToCartListItems(CartDataTable _cartListData) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(cartpickedClub, _cartListData.get_pickedClubName_CLUB());
        cv.put(cartSportName, _cartListData.get_cartSportName());
        cv.put(cartCourtName, _cartListData.get_cartCourtName());
        cv.put(cartDate, _cartListData.get_cartDate());
        cv.put(cartSlotTime, _cartListData.get_cartSlotTime());
        cv.put(cartSlotPrice, _cartListData.get_cartSlotPrice());
        cv.put(cartPickedCourtID, _cartListData.get_cartPickedCourtID());
        cv.put(convenienceFee, _cartListData.get_convenienceFee());
        cv.put(convenienceLabel, _cartListData.get_convenienceLabel());
        cv.put(cartItemPosition, _cartListData.get_position());
        db.insert(addToCartTable, cartDate, cv);
        db.close();
    }

    public ArrayList<CartDataTable> getCartDataTableList() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = db.rawQuery("SELECT * FROM " + addToCartTable, new String[]{});
        ArrayList<CartDataTable> cartDataList = new ArrayList<CartDataTable>();
        if (cur.moveToFirst()) {
            do {
                CartDataTable cartDataObj = new CartDataTable();
                cartDataObj.set_pickedClubName_CLUB(cur.getString(cur.getColumnIndex(cartpickedClub)));
                cartDataObj.set_cartSportName(cur.getString(cur.getColumnIndex(cartSportName)));
                cartDataObj.set_cartCourtName(cur.getString(cur.getColumnIndex(cartCourtName)));
                cartDataObj.set_cartDate(cur.getString(cur.getColumnIndex(cartDate)));
                cartDataObj.set_cartSlotTime(cur.getString(cur.getColumnIndex(cartSlotTime)));
                cartDataObj.set_cartSlotPrice(cur.getString(cur.getColumnIndex(cartSlotPrice)));
                cartDataObj.set_cartPickedCourtID(cur.getString(cur.getColumnIndex(cartPickedCourtID)));
                cartDataObj.set_convenienceFee(cur.getString(cur.getColumnIndex(convenienceFee)));
                cartDataObj.set_convenienceLabel(cur.getString(cur.getColumnIndex(convenienceLabel)));
                cartDataObj.set_position(cur.getInt(cur.getColumnIndex(cartItemPosition)));
                cartDataList.add(cartDataObj);
            } while (cur.moveToNext());
        }
        cur.close();
        db.close();
        return cartDataList;
    }

    public String getConvenienceFeeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = db.rawQuery("SELECT * FROM " + addToCartTable, new String[]{});
        String convenienceFeeStr = "";
        if (cur.getCount() > 0) {
            if (cur.moveToFirst()) {
                do {
                    convenienceFeeStr = cur.getString(cur.getColumnIndex(convenienceFee));
                } while (cur.moveToNext());
            }
        }
        cur.close();
        db.close();
        return convenienceFeeStr;
    }

    public String getconvenienceLabelDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = db.rawQuery("SELECT * FROM " + addToCartTable, new String[]{});
        String convenienceLabelStr = "";
        if (cur.getCount() > 0) {
            if (cur.moveToFirst()) {
                do {
                    convenienceLabelStr = cur.getString(cur.getColumnIndex(convenienceLabel));
                } while (cur.moveToNext());
            }
        }
        cur.close();
        db.close();
        return convenienceLabelStr;
    }

    public void deleteCartItem(String _cartDate,String _cartSlotTime) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + addToCartTable + " WHERE " + cartDate + "='" + _cartDate + "' AND " + cartSlotTime + "='" + _cartSlotTime + "'");
        db.close();
    }

    public String getRegisteredDeviceID() {
       try{
           SQLiteDatabase db = this.getReadableDatabase();
           Cursor cur = db.rawQuery("SELECT * FROM " + oneTimeRegistrationTable, new String[]{});
           String UDID = null;
           if (cur.getCount() > 0) {
               if (cur.moveToFirst()) {
                   do {
                       UDID = cur.getString(cur.getColumnIndex(deviceID));
                   } while (cur.moveToNext());
               }
           }
           cur.close();
           db.close();
           return UDID;
       } catch (Exception ee){
           ee.printStackTrace();
           return null;
       }
    }

    public String getRegisteredEmailID() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = db.rawQuery("SELECT * FROM " + oneTimeRegistrationTable, new String[]{});
        String emailIDStr = null;
        if (cur.getCount() > 0) {
            if (cur.moveToFirst()) {
                do {
                    emailIDStr = cur.getString(cur.getColumnIndex(emailID));
                } while (cur.moveToNext());
            }
        }
        cur.close();
        db.close();
        return emailIDStr;
    }

    public String getRegisteredMobileNumber() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = db.rawQuery("SELECT * FROM " + oneTimeRegistrationTable, new String[]{});
        String mobileStr = null;
        if (cur.getCount() > 0) {
            if (cur.moveToFirst()) {
                do {
                    mobileStr = cur.getString(cur.getColumnIndex(mobileNumber));
                } while (cur.moveToNext());
            }
        }
        cur.close();
        db.close();
        return mobileStr;
    }

    public String getRegisteredCookie() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = db.rawQuery("SELECT * FROM " + oneTimeRegistrationTable, new String[]{});
        String _cookie = null;
        if (cur.getCount() > 0) {
            if (cur.moveToFirst()) {
                do {
                    _cookie = cur.getString(cur.getColumnIndex(cookie));
                } while (cur.moveToNext());
            }
        }
        cur.close();
        db.close();
        return _cookie;
    }

    public String getRegisteredRole() {
       try{
           SQLiteDatabase db = this.getReadableDatabase();
           Cursor cur = db.rawQuery("SELECT * FROM " + oneTimeRegistrationTable, new String[]{});
           String _role = null;
           if (cur.getCount() > 0) {
               if (cur.moveToFirst()) {
                   do {
                       _role = cur.getString(cur.getColumnIndex(role));
                   } while (cur.moveToNext());
               }
           }
           cur.close();
           db.close();
           return _role;
       } catch (Exception e){
           e.printStackTrace();
           return null;
       }
    }

    public void addCityToDB(String _currentCity) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + citiesTable);
        ContentValues cv = new ContentValues();
        cv.put(currentCity, _currentCity);
        db.insert(citiesTable, currentCity, cv);
        db.close();
    }

    public void deleteCart() {
        SQLiteDatabase db = this.getWritableDatabase();
        //db.execSQL("DELETE FROM " + citiesTable);
        db.delete(addToCartTable, null, null);
        db.close();
    }

    public String getCurrentCity() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = db.rawQuery("SELECT * FROM " + citiesTable, new String[]{});
        String _currentCity = "empty";
        if (cur.getCount() > 0) {
            if (cur.moveToFirst()) {
                do {
                   _currentCity = cur.getString(cur.getColumnIndex(currentCity));
                } while (cur.moveToNext());
            }
        }
        cur.close();
        db.close();
        return _currentCity;
    }

    public void addToEventsDataTableItems(EventsDataTable _eventsData) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(eventName, _eventsData.get_eventName());
        cv.put(eventDate, _eventsData.get_eventDate());
        cv.put(eventTime, _eventsData.get_eventTime());
        cv.put(eventPrice, _eventsData.get_eventPrice());
        cv.put(eventItemPostion, _eventsData.get_eventItemPostion());
        cv.put(event_TicketID, _eventsData.get_eventID());
        db.insert(addToEventsTable, eventItemPostion, cv);
        db.close();
    }

    public ArrayList<EventsDataTable> getEventsDataTableList() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = db.rawQuery("SELECT * FROM " + addToEventsTable, new String[]{});
        ArrayList<EventsDataTable> eventsDataTableList = new ArrayList<EventsDataTable>();
        if (cur.moveToFirst()) {
            do {
                EventsDataTable cartDataObj = new EventsDataTable();
                cartDataObj.set_eventName(cur.getString(cur.getColumnIndex(eventName)));
                cartDataObj.set_eventDate(cur.getString(cur.getColumnIndex(eventDate)));
                cartDataObj.set_eventTime(cur.getString(cur.getColumnIndex(eventTime)));
                cartDataObj.set_eventPrice(cur.getString(cur.getColumnIndex(eventPrice)));
                cartDataObj.set_eventItemPostion(cur.getInt(cur.getColumnIndex(eventItemPostion)));
                cartDataObj.set_eventID(cur.getString(cur.getColumnIndex(event_TicketID)));
                eventsDataTableList.add(cartDataObj);
            } while (cur.moveToNext());
        }
        cur.close();
        db.close();
        return eventsDataTableList;
    }

    public void deleteEventItem(String _eventDate, String _eventTicketID) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + addToEventsTable + " WHERE " + eventDate + "='" + _eventDate + "' AND " + event_TicketID+ "='" + _eventTicketID + "'");
        db.close();
    }

    public void deleteEventsCart() {
        SQLiteDatabase db = this.getWritableDatabase();
        //db.execSQL("DELETE FROM " + citiesTable);
        db.delete(addToEventsTable, null, null);
        db.close();
    }
}