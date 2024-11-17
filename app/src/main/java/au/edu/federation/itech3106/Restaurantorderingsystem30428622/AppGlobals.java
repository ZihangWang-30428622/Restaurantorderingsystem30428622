package au.edu.federation.itech3106.Restaurantorderingsystem30428622;

import android.app.Application;

public class AppGlobals extends Application {

    // Global variables for food counts
    public String juice1Count = "";  // Default: empty string
    public String juice2Count = "";  // Default: empty string
    public String gongbaoCount = ""; // Default: empty string
    public String yuxiangCount = ""; // Default: empty string

    // Global variables for other selections
    public boolean sugarChecked = false; // Default: false
    public boolean needCutlery = false; // New variable to track if cutlery is needed
    public double totalPrice = 0.0; // Default: 0.0
    public String selectedSeatNumber = ""; // Default: empty string

    // Global variables for order details
    public String customerPhone = "";  // Customer phone number
    public String customerRemarks = ""; // Customer remarks

    // Global variables for ratings
    public float ratingValue = 0; // Rating value
    public String feedbackText = ""; // Feedback text

    @Override
    public void onCreate() {
        super.onCreate();
        // Initialization logic if needed
    }

    // Optional: Reset method to clear all global variables
    public void resetData() {
        juice1Count = "";
        juice2Count = "";
        gongbaoCount = "";
        yuxiangCount = "";
        sugarChecked = false;
        needCutlery = false; // Reset cutlery preference
        totalPrice = 0.0;
        selectedSeatNumber = "";
        customerPhone = "";
        customerRemarks = "";
        ratingValue = 0;
        feedbackText = "";
    }
}
