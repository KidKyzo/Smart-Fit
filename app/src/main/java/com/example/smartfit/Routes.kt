package com.example.smartfit

object Routes {
    var splash = "splash_screen"
    var home = "home_screen"
    var log = "log_activity"
    var setting = "setting _screen"
    var profile = "profile_screen"
    var login = "login_screen"
    var plan = "plan_screen"
    var workoutList = "workout_list"
    var foodList = "food_list"
    var workoutDetail = "workout_detail"
    var foodDetail = "food_detail"
    
    fun workoutDetailRoute(workoutId: Int) = "workout_detail/$workoutId"
    fun foodDetailRoute(foodId: Int) = "food_detail/$foodId"
}