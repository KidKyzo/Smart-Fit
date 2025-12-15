// app/src/main/java/com/example/smartfit/Routes.kt
package com.example.smartfit

object Routes {
    // ... existing routes
    var splash = "splash_screen"
    var home = "home_screen"
    var log = "log_activity"
    var setting = "setting_screen"
    var profile = "profile_screen"
    var login = "login_screen"
    var register = "sign_up_step_1"
    var biodata = "sign_up_step_2"
    var plan = "plan_screen"

    var workoutList = "workout_list"
    var foodList = "food_list"

    // --- UPDATED THIS LINE ---
    var workoutDetail = "workout_detail" // REMOVED "/{workoutId}"
    // -------------------------

    var foodDetail = "food_detail"
    var weeklyReport = "weekly_report"

    // You can delete or comment out the helper function since we don't need IDs anymore
    // fun workoutDetailRoute(workoutId: Int) = "workout_detail/$workoutId"

    fun foodDetailRoute(foodId: Int) = "food_detail/$foodId"
    fun weeklyReportRoute(weekOffset: Int = 0) = "weekly_report/$weekOffset"
}