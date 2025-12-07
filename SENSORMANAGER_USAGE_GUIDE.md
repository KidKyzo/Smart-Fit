# SensorManager Usage Analysis - Visual Guide

## 🔍 WHERE IS SENSORMANAGER BEING USED?

### File Locations & References:

```
┌─ Smart-Fit/
│  └─ app/src/main/java/com/example/smartfit/
│
├─ 📍 utils/SensorManager.kt (DEFINITION)
│  ├─ Class Name: StepSensor (NOTE: Named SensorManager.kt but class is StepSensor)
│  ├─ Contains: android.hardware.SensorManager import
│  ├─ Key Method: stepFlow: Flow<Int>
│  └─ Status: ✅ Properly implemented
│
├─ 📍 viewmodel/ActivityViewModel.kt (PRIMARY USAGE)
│  ├─ Line: ~import statement (top of file)
│  ├─ Line: ~54 - private val stepSensor = StepSensor(application)
│  ├─ Method: startStepTracking() - COLLECTS FROM stepFlow
│  └─ Status: ✅ Integration working
│
├─ ❌ screens/home/HomeScreen.kt (NO DIRECT USAGE)
│  └─ Uses ViewModel instead (CORRECT PATTERN)
│
├─ ❌ screens/home/HomeContent.kt (NO DIRECT USAGE)
│  └─ Uses ViewModel StateFlows instead (CORRECT PATTERN)
│
└─ ❌ MainActivity.kt (NO SENSOR INITIALIZATION)
   └─ No need (ViewModel handles it)
```

---

## 🔄 DATA FLOW: HOW STEP TRACKING WORKS

```
┌─────────────────────────────────────────────────────────────┐
│ Device Hardware (Phone Accelerometer)                        │
│ └─ Android OS Step Counter Sensor                            │
└──────────────────┬──────────────────────────────────────────┘
                   │
                   ▼
┌─────────────────────────────────────────────────────────────┐
│ StepSensor (utils/SensorManager.kt)                         │
│ └─ SensorManager.TYPE_STEP_COUNTER                          │
│    └─ SensorEventListener collects sensor events            │
│       └─ Emits via Flow<Int>                                │
└──────────────────┬──────────────────────────────────────────┘
                   │
                   ▼
┌─────────────────────────────────────────────────────────────┐
│ ActivityViewModel.startStepTracking()                        │
│ ├─ Collects from stepSensor.stepFlow                        │
│ ├─ Detects daily reset (midnight)                           │
│ ├─ Saves yesterday's data to database                       │
│ ├─ Updates StateFlows:                                      │
│ │  ├─ _steps (current steps)                                │
│ │  ├─ _calories (calculated)                                │
│ │  ├─ _distance (calculated)                                │
│ │  ├─ _activeTime (calculated)                              │
│ │  ├─ _averageSpeed (calculated)                            │
│ │  └─ _weeklyAvgSteps (calculated)                          │
│ └─ Persists to DataStore for next app start                 │
└──────────────────┬──────────────────────────────────────────┘
                   │
                   ▼
┌─────────────────────────────────────────────────────────────┐
│ HomeScreen / HomeContent (Compose UI)                        │
│ └─ Observes StateFlows with collectAsState()                │
│    └─ Updates UI components in real-time                    │
│       └─ StepTrackerCircle displays progress                │
│       └─ StatsGrid shows calculated values                  │
└─────────────────────────────────────────────────────────────┘
```

---

## ✅ CURRENT STATUS: SENSOR INTEGRATION

### What's Working:
1. ✅ **Sensor Detection**
   - StepSensor finds TYPE_STEP_COUNTER
   - Registers listener for sensor events
   - Emits steps via Kotlin Flow

2. ✅ **Step Tracking**
   - Collects sensor events continuously
   - Calculates differences between readings
   - Handles sensor resets (device reboot)

3. ✅ **Daily Reset**
   - Detects midnight transition
   - Saves previous day's activity to database
   - Resets counter for new day

4. ✅ **Data Calculations**
   - Distance: steps × 0.762m (stride length)
   - Calories: steps × 0.04 kcal
   - Active Time: steps / 100 steps/min
   - Average Speed: distance / time

5. ✅ **Persistence**
   - DataStore saves current state
   - Survives app restart
   - Weekly average calculated across days

### ⚠️ What Might Need Attention:
1. Initial sensor reading might lag on first app launch
2. No explicit warmup for cold start
3. Edge case: Device reboot while app running

---

## 🔧 HOME SCREEN STEP TRACKER CHANGES NEEDED

### Current Issue: 10,000 Step Limit

**File:** `screens/home/HomeContent.kt`  
**Function:** `StepTrackerCircle` (lines 231-267)

```kotlin
// CURRENT (WITH LIMITATION):
val progress = if (stepGoal > 0) steps.toFloat() / stepGoal.toFloat() else 0f

// PROBLEM:
// - If steps = 12,000 and goal = 10,000
// - progress = 12,000 / 10,000 = 1.2 (clamped to 1.0 by Canvas)
// - Circle only shows 100% even with more steps
```

### Required Change:

```kotlin
// OPTION 1: Remove limit entirely (continuous scale)
val progress = if (stepGoal > 0) (steps.toFloat() / stepGoal.toFloat()).coerceIn(0f, 1f) else 0f
// Result: Circle stays at 100% once goal is reached

// OPTION 2: Multiple circles for milestones
// Shows progress: 0-25k in different visual states

// OPTION 3: Percentage-based display
// Shows 100%+ if exceeding goal
```

---

## 📊 DATABASE & PERSISTENCE CHAIN

```
Step Sensor Data Flow to Persistence:
┌─ Sensor Raw Data (integer step count)
│  └─ StepSensor Flow
│     └─ ActivityViewModel.startStepTracking()
│        ├─ Option 1: Direct to StateFlow (UI updates immediately)
│        └─ Option 2: Save to ActivityLog (database, daily record)
│           └─ UserRepository.saveStepTrackingData()
│              └─ DataStore (for app restart recovery)

Database: Room
├─ ActivityLog Entity
│  ├─ id (primary key)
│  ├─ activityType (e.g., "Walking (Daily)")
│  ├─ duration (minutes)
│  ├─ calories (calculated)
│  ├─ distance (calculated)
│  ├─ steps (tracked)
│  ├─ date (timestamp)
│  └─ notes
└─ ActivityDao (CRUD operations)

DataStore: Preferences
├─ THEME_KEY → isDarkMode
├─ STEP_GOAL_KEY → target steps (10,000)
├─ IS_LOGGED_IN_KEY → user status
├─ LAST_STEP_COUNT_KEY → last sensor value
├─ SAVED_STEPS_TODAY_KEY → today's steps
└─ LAST_TRACKING_DATE_KEY → date of tracking
```

---

## 🎯 TRACKING METRICS EXPLANATION

### What Gets Tracked:

| Metric | Source | Calculation | Unit |
|--------|--------|-------------|------|
| **Steps** | Sensor (real-time) | Direct from sensor | count |
| **Distance** | Calculated | steps × 0.762m | km |
| **Calories** | Calculated | steps × 0.04 | kcal |
| **Active Time** | Calculated | steps ÷ 100 | min |
| **Avg Speed** | Calculated | distance ÷ time | km/h |
| **Weekly Avg** | Aggregated | Total 7-day steps ÷ 7 | steps |

### Daily Reset Behavior:

```
Day 1 (Example):
├─ Morning 00:00:00 → Reset to 0
├─ Throughout day → Sensor tracks steps
├─ Evening 23:59:59 → 8,500 steps accumulated
└─ End of Day 1 (record saved)

Day 2:
├─ Midnight 00:00:00 → 
│  ├─ Yesterday's 8,500 steps saved as ActivityLog
│  ├─ Counter resets to 0
│  └─ New day tracking begins
└─ Throughout day → New steps tracked
```

---

## 🔑 KEY INTEGRATION POINTS

### 1. MainActivity.kt
```kotlin
// Creates ThemeViewModel but NOT ActivityViewModel directly
// AppNav handles ActivityViewModel creation
```

### 2. AppNav.kt
```kotlin
// Creates ActivityViewModel with:
// - Application context
// - ActivityRepository (Database)
// - UserRepository (Preferences + DataStore)
// AppNav passes to HomeScreen
```

### 3. HomeScreen.kt
```kotlin
// Receives all ViewModels
// Passes to ScreenContent
// ScreenContent routes to specific screens
```

### 4. HomeContent.kt
```kotlin
// Observes StateFlows from ActivityViewModel
val steps by activityViewModel.steps.collectAsState()
val stepGoal by activityViewModel.stepGoal.collectAsState()
// Passes to UI components like StepTrackerCircle
```

### 5. StepTrackerCircle
```kotlin
// Displays steps and progress
// Currently limited by: progress = steps / goal
// NEEDS: Removal of goal-based limitation
```

---

## ⚠️ POTENTIAL ISSUES & SOLUTIONS

### Issue 1: First App Launch Sensor Lag
**Problem:** Sensor might not have initial value immediately  
**Solution:** Add a placeholder UI state or loading indicator  
**File:** `ActivityViewModel.kt` - Add `_isInitializing` StateFlow

### Issue 2: Goal Always 10,000 (Hardcoded)
**Problem:** Users can't change their goal, step limit enforced  
**Solution:** Allow goal to be set per user preference  
**File:** `UserPreferences.kt` + UI for changing goal

### Issue 3: No Sensor Permission Request
**Problem:** Manifest declares permission but might not request at runtime  
**Solution:** Add runtime permission request for Android 6.0+  
**File:** `MainActivity.kt` - Add permission launcher

### Issue 4: Circle Progress Capped at 100%
**Problem:** Progress value gets clamped when steps > goal  
**Solution:** Use continuous scale or multiple visual indicators  
**File:** `HomeContent.kt` - Modify StepTrackerCircle

---

## 📝 SENSOR MANAGER NAMING NOTE

⚠️ **Naming Inconsistency:**
- File is named: `SensorManager.kt`
- Class inside is: `StepSensor`
- Android Framework class: `SensorManager`

**Recommendation:** Rename file to `StepSensor.kt` for clarity

```
Current:  utils/SensorManager.kt (contains StepSensor class)
Better:   utils/StepSensor.kt
```

---

## 🧪 TESTING CHECKLIST FOR SENSOR

- [ ] Steps count increases during physical activity
- [ ] Reset occurs at midnight
- [ ] Previous day's data appears in activity log
- [ ] Weekly average calculates correctly
- [ ] App restart preserves step count
- [ ] Distance calculation is accurate
- [ ] Calories calculation is reasonable
- [ ] Average speed shows 0 when no movement
- [ ] Multiple users don't affect each other's data
- [ ] Device reboot doesn't lose progress

---

## 🎯 RECOMMENDATION SUMMARY

### HIGH PRIORITY:
1. ✏️ Remove 10,000 step goal limitation from circle progress
2. ✏️ Fix file naming: `SensorManager.kt` → `StepSensor.kt`
3. ✏️ Add runtime permission request for step sensor

### MEDIUM PRIORITY:
1. 📝 Add accessibility descriptions for sensor data
2. 📝 Add user-facing goal customization UI
3. 📝 Add error handling for missing sensor

### LOW PRIORITY:
1. 💡 Add sensor warmup logic
2. 💡 Add loading state for first reading
3. 💡 Add detailed sensor diagnostics screen

---

Generated: December 7, 2025

