package com.example.smartfit.data.model

data class FoodData(
    val id: Int,
    val title: String,
    val description: String,
    val imageUrl: String,
    val calories: Int,
    val protein: String,
    val carbs: String,
    val fats: String,
    val servingSize: String
)

object MockFoodData {
    fun getFoods(): List<FoodData> = listOf(
        FoodData(
            id = 1,
            title = "Egg",
            description = "A whole egg is one of the most nutritious foods available. Rich in high-quality protein, vitamins, and minerals. Contains all essential amino acids needed for muscle recovery and growth.",
            imageUrl = "food_egg",
            calories = 78,
            protein = "6.3g",
            carbs = "0.6g",
            fats = "5.3g",
            servingSize = "1 large egg (50g)"
        ),
        FoodData(
            id = 2,
            title = "Chicken Breast",
            description = "Lean protein source perfect for muscle building. Low in fat and high in protein, making it ideal for fitness enthusiasts. Contains essential B vitamins and minerals.",
            imageUrl = "food_chicken",
            calories = 165,
            protein = "31g",
            carbs = "0g",
            fats = "3.6g",
            servingSize = "100g cooked"
        ),
        FoodData(
            id = 3,
            title = "Oatmeal",
            description = "Whole grain packed with fiber and complex carbohydrates. Provides sustained energy for workouts. Rich in beta-glucan which supports heart health.",
            imageUrl = "food_oatmeal",
            calories = 389,
            protein = "16.9g",
            carbs = "66.3g",
            fats = "6.9g",
            servingSize = "100g dry"
        ),
        FoodData(
            id = 4,
            title = "Banana",
            description = "Natural energy booster rich in potassium and carbohydrates. Perfect pre or post-workout snack. Helps prevent muscle cramps and supports recovery.",
            imageUrl = "food_banana",
            calories = 89,
            protein = "1.1g",
            carbs = "22.8g",
            fats = "0.3g",
            servingSize = "1 medium (118g)"
        ),
        FoodData(
            id = 5,
            title = "Salmon",
            description = "Fatty fish rich in omega-3 fatty acids and high-quality protein. Supports muscle recovery and reduces inflammation. Excellent source of vitamin D and B vitamins.",
            imageUrl = "food_salmon",
            calories = 208,
            protein = "20g",
            carbs = "0g",
            fats = "13g",
            servingSize = "100g cooked"
        ),
        FoodData(
            id = 6,
            title = "Greek Yogurt",
            description = "Protein-packed dairy product with probiotics for gut health. Contains calcium for bone strength. Lower in sugar than regular yogurt.",
            imageUrl = "food_yogurt",
            calories = 59,
            protein = "10g",
            carbs = "3.6g",
            fats = "0.4g",
            servingSize = "100g plain, non-fat"
        ),
        FoodData(
            id = 7,
            title = "Sweet Potato",
            description = "Complex carbohydrate rich in fiber, vitamins, and antioxidants. Provides sustained energy for workouts. High in vitamin A and potassium.",
            imageUrl = "food_sweet_potato",
            calories = 86,
            protein = "1.6g",
            carbs = "20.1g",
            fats = "0.1g",
            servingSize = "100g baked"
        ),
        FoodData(
            id = 8,
            title = "Almonds",
            description = "Nutrient-dense nuts packed with healthy fats, protein, and fiber. Rich in vitamin E and magnesium. Great for heart health and satiety.",
            imageUrl = "food_almonds",
            calories = 579,
            protein = "21.2g",
            carbs = "21.6g",
            fats = "49.9g",
            servingSize = "100g (about 23 almonds)"
        ),
        FoodData(
            id = 9,
            title = "Spinach",
            description = "Leafy green vegetable loaded with vitamins, minerals, and antioxidants. Very low in calories but high in nutrients. Excellent source of iron and calcium.",
            imageUrl = "food_spinach",
            calories = 23,
            protein = "2.9g",
            carbs = "3.6g",
            fats = "0.4g",
            servingSize = "100g raw"
        ),
        FoodData(
            id = 10,
            title = "Brown Rice",
            description = "Whole grain providing complex carbohydrates and fiber. Better nutritional profile than white rice. Good source of manganese and selenium.",
            imageUrl = "food_brown_rice",
            calories = 111,
            protein = "2.6g",
            carbs = "23g",
            fats = "0.9g",
            servingSize = "100g cooked"
        )
    )
    
    fun getFoodById(id: Int): FoodData? = getFoods().find { it.id == id }
}
