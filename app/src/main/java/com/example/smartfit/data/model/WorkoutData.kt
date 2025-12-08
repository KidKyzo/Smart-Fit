package com.example.smartfit.data.model

data class WorkoutData(
    val id: Int,
    val title: String,
    val description: String,
    val imageUrl: String, // For now, we'll use placeholder or local resources
    val steps: List<String>,
    val duration: String,
    val difficulty: String
)

object MockWorkoutData {
    fun getWorkouts(): List<WorkoutData> = listOf(
        WorkoutData(
            id = 1,
            title = "Push Up",
            description = "A classic upper body exercise that targets chest, shoulders, and triceps.",
            imageUrl = "workout_pushup",
            steps = listOf(
                "Start in a plank position with hands shoulder-width apart",
                "Lower your body until your chest nearly touches the floor",
                "Keep your core engaged and back straight",
                "Push back up to starting position",
                "Repeat for desired reps"
            ),
            duration = "10-15 mins",
            difficulty = "Beginner"
        ),
        WorkoutData(
            id = 2,
            title = "Sit Up",
            description = "Core strengthening exercise focusing on abdominal muscles.",
            imageUrl = "workout_situp",
            steps = listOf(
                "Lie on your back with knees bent and feet flat",
                "Place hands behind your head or across chest",
                "Engage your core and lift your upper body",
                "Lower back down with control",
                "Repeat for desired reps"
            ),
            duration = "8-12 mins",
            difficulty = "Beginner"
        ),
        WorkoutData(
            id = 3,
            title = "Squat",
            description = "Lower body exercise targeting quads, hamstrings, and glutes.",
            imageUrl = "workout_squat",
            steps = listOf(
                "Stand with feet shoulder-width apart",
                "Lower your body by bending knees and hips",
                "Keep chest up and weight on heels",
                "Go down until thighs are parallel to ground",
                "Push through heels to return to start"
            ),
            duration = "12-15 mins",
            difficulty = "Beginner"
        ),
        WorkoutData(
            id = 4,
            title = "Plank",
            description = "Isometric core exercise that builds endurance and stability.",
            imageUrl = "workout_plank",
            steps = listOf(
                "Start in a forearm plank position",
                "Keep body in a straight line from head to heels",
                "Engage core and glutes",
                "Hold position without sagging or raising hips",
                "Breathe steadily throughout"
            ),
            duration = "5-10 mins",
            difficulty = "Beginner"
        ),
        WorkoutData(
            id = 5,
            title = "Lunges",
            description = "Single-leg exercise for building leg strength and balance.",
            imageUrl = "workout_lunges",
            steps = listOf(
                "Stand tall with feet hip-width apart",
                "Step forward with one leg",
                "Lower hips until both knees are at 90 degrees",
                "Push back to starting position",
                "Alternate legs and repeat"
            ),
            duration = "10-12 mins",
            difficulty = "Intermediate"
        ),
        WorkoutData(
            id = 6,
            title = "Burpees",
            description = "Full-body exercise combining strength and cardio.",
            imageUrl = "workout_burpees",
            steps = listOf(
                "Start standing, then drop into a squat",
                "Place hands on ground and jump feet back to plank",
                "Do a push-up (optional)",
                "Jump feet back to squat position",
                "Explode up with a jump"
            ),
            duration = "8-10 mins",
            difficulty = "Advanced"
        ),
        WorkoutData(
            id = 7,
            title = "Mountain Climbers",
            description = "Dynamic exercise that works core and cardiovascular system.",
            imageUrl = "workout_mountain_climbers",
            steps = listOf(
                "Start in a high plank position",
                "Bring right knee toward chest",
                "Quickly switch legs",
                "Continue alternating at a running pace",
                "Keep core tight and hips level"
            ),
            duration = "6-8 mins",
            difficulty = "Intermediate"
        ),
        WorkoutData(
            id = 8,
            title = "Jumping Jacks",
            description = "Cardio warm-up exercise to get your heart rate up.",
            imageUrl = "workout_jumping_jacks",
            steps = listOf(
                "Stand with feet together, arms at sides",
                "Jump feet apart while raising arms overhead",
                "Jump back to starting position",
                "Maintain a steady rhythm",
                "Continue for desired duration"
            ),
            duration = "5-8 mins",
            difficulty = "Beginner"
        )
    )
    
    fun getWorkoutById(id: Int): WorkoutData? = getWorkouts().find { it.id == id }
}
