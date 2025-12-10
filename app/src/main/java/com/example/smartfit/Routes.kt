package com.example.smartfit

object Routes {
    var splash = "splash_screen"
    var home = "home_screen"
    var log = "log_activity"
    var setting = "setting_screen"
    var profile = "profile_screen"
    var login = "login_screen"
    var plan = "plan_screen"
    var workoutList = "workout_list"
    var foodList = "food_list"
    var workoutDetail = "workout_detail"
    var foodDetail = "food_detail"
    var weeklyReport = "weekly_report"
    
    fun workoutDetailRoute(workoutId: Int) = "workout_detail/$workoutId"
    fun foodDetailRoute(foodId: Int) = "food_detail/$foodId"
    fun weeklyReportRoute(weekOffset: Int = 0) = "weekly_report/$weekOffset"
}