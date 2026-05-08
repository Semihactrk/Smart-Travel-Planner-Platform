# ✈ Smart Travel Planner Platform

A sophisticated Java-based travel planning application implementing multiple design patterns to create an elegant, extensible, and feature-rich system for exploring cities, planning activities, and managing travel itineraries.

## 📋 Project Overview

This team project extends the foundational travel planner into a comprehensive platform that allows users to:
- Browse and filter cities by various criteria
- Plan detailed travel itineraries with hierarchical activity structures
- Track travel budgets and time requirements
- Execute and undo complex planning actions
- Monitor real-time weather updates and temperature changes

### Key Features

✅ **Modern, Intuitive GUI** with weather-responsive icons and real-time updates
✅ **Multi-City Planning** with independent activity plans per city
✅ **Hierarchical Activity Plans** (Composite Pattern) for complex trip structures
✅ **Undo/Redo Functionality** for all planning actions
✅ **Dynamic Data Visualization** with temperature and weather charts
✅ **Extensible Activity System** with built-in and custom activity support
✅ **Real-time Weather Simulation** with observer pattern updates

## 🏗 Design Patterns Implemented

This application demonstrates proper implementation of 7 core design patterns:

### 1. **Singleton Pattern** (CityRepository)
- Single point of access to all city data
- Loads cities from JSON file on initialization
- Thread-safe implementation with synchronized getInstance()

```java
public class CityRepository {
    private static CityRepository instance;
    private List<City> cities;

    public static synchronized CityRepository getInstance() {
        if (instance == null) {
            instance = new CityRepository();
        }
        return instance;
    }
}
```

### 2. **Strategy Pattern** (Sorting Strategies)
- Pluggable sorting algorithms
- Implemented strategies:
  - `SortByName`: Alphabetical order
  - `SortByPopulation`: Descending population
  - `SortByArea`: Descending area

```java
public interface SortStrategy {
    void sort(List<City> cities);
}
```

### 3. **Iterator Pattern** (Weather Filtering)
- Iterate through cities based on weather conditions
- Four weather-specific iterators:
  - `SunnyWeatherIterator`
  - `CloudyWeatherIterator`
  - `RainyWeatherIterator`
  - `SnowyWeatherIterator`

### 4. **Observer Pattern** (Weather Updates)
- `WeatherReportProvider`: Subject that publishes weather updates
- `WeatherObserver`: Observer interface for receiving updates
- Runs on separate thread, updates every 3 seconds
- Real-time GUI updates through `SwingUtilities.invokeLater()`

### 5. **Decorator Pattern** (Activity Enhancements)
- Base `CityActivity` interface
- Concrete decorators:
  - `MuseumVisit`: Cost $20, Duration 3h
  - `ShoppingMallVisit`: Cost $30, Duration 2h
  - `ParkVisit`: Cost $10, Duration 2h
  - `CityCenterVisit`: Cost $15, Duration 2h

```java
public abstract class ActivityDecorator implements CityActivity {
    protected CityActivity wrappedActivity;
    // ...
}
```

### 6. **Composite Pattern** (Activity Plans)
- `ActivityComponent`: Common interface
  - `ActivityPlan` (Composite): Contains other components
  - `ActivityLeaf` (Leaf): Individual activities
- Supports:
  - Recursive cost calculation
  - Total time aggregation
  - Hierarchical tree structure
  - Add/remove operations

### 7. **Command Pattern** (User Actions)
- `Command` interface with execute/undo
- Implemented commands:
  - `AddActivityCommand`
  - `RemoveActivityCommand`
  - `AddCityCommand`
  - `ClearPlanCommand`
  - `MoveActivityCommand`
- `CommandManager` maintains undo/redo stacks

## 📦 Project Structure

```
src/com/smarttravel/
├── City.java                              # City data model
├── WeatherState.java                      # Weather enumeration
│
├── repository/
│   └── CityRepository.java                # Singleton city repository
│
├── strategy/
│   ├── SortStrategy.java                  # Strategy interface
│   ├── SortByName.java
│   ├── SortByPopulation.java
│   └── SortByArea.java
│
├── iterator/
│   ├── WeatherCityIterator.java           # Iterator interface
│   ├── SunnyWeatherIterator.java
│   ├── CloudyWeatherIterator.java
│   ├── RainyWeatherIterator.java
│   └── SnowyWeatherIterator.java
│
├── observer/
│   ├── WeatherObserver.java               # Observer interface
│   ├── WeatherReportProvider.java         # Subject/Publisher
│   └── WeatherUpdateEvent.java
│
├── decorator/
│   ├── CityActivity.java                  # Component interface
│   ├── BaseCityActivity.java              # Base decorator
│   ├── ActivityDecorator.java             # Abstract decorator
│   ├── MuseumVisit.java
│   ├── ShoppingMallVisit.java
│   ├── ParkVisit.java
│   └── CityCenterVisit.java
│
├── composite/
│   ├── ActivityComponent.java             # Component interface
│   ├── ActivityPlan.java                  # Composite
│   └── ActivityLeaf.java                  # Leaf
│
├── command/
│   ├── Command.java                       # Command interface
│   ├── CommandManager.java                # Command invoker
│   ├── AddActivityCommand.java
│   ├── RemoveActivityCommand.java
│   ├── AddCityCommand.java
│   ├── ClearPlanCommand.java
│   └── MoveActivityCommand.java
│
└── gui/
    ├── ModernTravelPlannerGUI.java        # Main GUI window
    ├── EnhancedBarChartPanel.java         # Temperature chart
    ├── EnhancedPieChartPanel.java         # Weather distribution
    ├── BarChartPanel.java                 # Legacy chart
    └── PieChartPanel.java                 # Legacy chart
```

## 🎨 GUI Features

### Modern Interface Design
- **Material Design Colors**: Professional blue, orange, and green color scheme
- **Weather Icons**: Unicode emoji for visual clarity (☀ ☁ 🌧 ❄)
- **Responsive Layout**: Adaptive 6-panel grid layout
- **Real-time Updates**: Charts refresh every 3 seconds from weather provider

### Main Panels

1. **🌍 All Cities Panel**
   - Display all available cities
   - Sortable by Name, Population, or Area
   - Single selection with automatic weather filter sync

2. **🔍 Filtered Cities Panel**
   - Weather-based filtering (ALL, SUNNY, CLOUDY, RAINY, SNOWY)
   - Updates dynamically as weather changes
   - Integrated with city list selection

3. **🎯 Activity Planner Panel**
   - 6 pre-built activity buttons with emoji icons
   - Extensible for custom activities
   - One-click activity addition
   - Button colors indicate activity types

4. **🗺 Travel Plan Panel**
   - Hierarchical tree view of activities
   - Cost and time display per activity
   - Real-time updates when activities added/removed
   - Supports nested activity plans

5. **🌡 Temperature Chart**
   - Bar chart with color-coded temperature ranges:
     - Blue: < 0°C (Freezing)
     - Green: 0-15°C (Cool)
     - Yellow: 15-25°C (Moderate)
     - Red: > 25°C (Hot)
   - City labels on X-axis
   - Temperature values above bars

6. **📈 Weather Distribution Chart**
   - Pie chart showing percentage of cities by weather
   - Color-coded slices matching weather types
   - Percentage labels on slices
   - Legend with current city counts

### Control Elements

**Top Control Panel**
- Sort dropdown: Choose city sorting criteria
- Weather filter: Select weather condition
- Undo/Redo buttons: Revert/restore actions

**Bottom Info Panel**
- Selected city display with location icon
- Total budget calculation with currency format
- Total duration in hours with time icon

## 🚀 Getting Started

### Prerequisites
- Java 11 or higher
- Maven 3.6+ (optional, for building)

### Installation

1. **Clone the repository**
```bash
git clone https://github.com/Semihactrk/Smart-Travel-Planner-Platform.git
cd Smart-Travel-Planner-Platform
```

2. **Compile the project**
```bash
javac -d bin src/com/smarttravel/**/*.java
```

3. **Run the application**
```bash
java -cp bin com.smarttravel.gui.ModernTravelPlannerGUI
```

Or with the packaged JAR:
```bash
java -jar SmartTravelPlanner.jar
```

## 📖 Usage Guide

### Basic Workflow

#### 1. Select a City
- Click on any city in the "All Cities" panel
- The selected city appears in the info bar at the bottom
- A new travel plan is automatically created for this city

#### 2. Add Activities
- Click any activity button in the "Activity Planner" panel
- Activity is added to the selected city's plan
- Tree view updates immediately showing the new activity
- Budget and duration auto-calculate at the bottom

#### 3. Organize with Plans
- Click "Add Plan" to create a sub-plan (e.g., "Day 1", "Day 2")
- Select the new plan in the tree, then add activities to it
- Supports unlimited nesting levels
- Total calculations aggregate all nested activities

#### 4. Filter and Sort
- Use "Sort by" dropdown to reorder the city list
- Use "Weather Filter" to show only cities with specific weather
- Filters update in real-time as weather changes

#### 5. Manage Your Plan
- **Undo**: Revert the last action
- **Redo**: Restore a reverted action
- **Clear**: Remove all activities from current city's plan
- Click on activities in the tree to view details

### Advanced Features

#### Custom Activities
To add a custom activity:
```java
ActivityLeaf customActivity = new ActivityLeaf(
    "Activity Name",
    costInDollars,
    durationInHours
);
commandManager.executeCommand(
    new AddActivityCommand(cityPlan, customActivity)
);
```

#### Creating Decorators
To create a new activity decorator:
```java
public class CustomActivityVisit extends ActivityDecorator {
    public CustomActivityVisit(CityActivity wrappedActivity) {
        super(wrappedActivity);
    }
    
    @Override
    public String getDescription() {
        return wrappedActivity.getDescription() + " + Custom Activity";
    }
    
    @Override
    public double getCost() {
        return wrappedActivity.getCost() + 25.0;
    }
    
    @Override
    public double getRequiredTime() {
        return wrappedActivity.getRequiredTime() + 1.5;
    }
}
```

## 🔄 Real-time Weather Updates

The application includes a background weather provider that:
- Updates city conditions every 3 seconds
- Randomly changes weather states
- Adjusts temperatures by ±5°C per update
- Notifies all observers (GUI elements) of changes
- Updates charts and filtered lists in real-time

## 💾 Data Persistence

Cities are loaded from a built-in dataset (currently mocked):

| City | Population | Area | Default Temp | Default Weather |
|------|-----------|------|--------------|------------------|
| Ankara | 5,317,215 | 25,615 km² | 15°C | SUNNY |
| Istanbul | 15,719,600 | 5,343 km² | 34.3°C | SNOWY |
| Gaziantep | 1,835,508 | 6,778.9 km² | -4.7°C | SUNNY |
| Gazze | 2,180,400 | 70.9 km² | 40°C | SNOWY |
| Kudus | 981,711 | 126 km² | 26.2°C | CLOUDY |
| Saraybosna | 347,000 | 141.6 km² | 36.8°C | RAINY |
| Buhara | 280,000 | 143.1 km² | 22.2°C | CLOUDY |

## 📊 UML Diagram

See `UML_Diagram.pdf` for the complete architecture diagram showing:
- All design pattern implementations
- Class hierarchies and interfaces
- Relationships between components
- Method signatures and attributes

## 👥 Team Contributions

### Serenay Kumandaveren
- **Subsystem**: GUI and User Interface
- **Components**: 
  - `ModernTravelPlannerGUI.java` - Main window with modern design
  - `EnhancedBarChartPanel.java` - Temperature visualization
  - `EnhancedPieChartPanel.java` - Weather distribution charts
  - Pattern Implementation: Observer (GUI updates), Command (button actions)

### Semihactrk (Lead)
- **Subsystem**: Core Patterns and Architecture
- **Components**:
  - `CityRepository.java` - Singleton pattern
  - `SortStrategy` implementations - Strategy pattern
  - `ActivityDecorator` hierarchy - Decorator pattern
  - `ActivityComponent` hierarchy - Composite pattern
  - Pattern Implementation: Singleton, Strategy, Decorator, Composite

### Contributors
- Iterator pattern implementations for weather filtering
- Command pattern for action management
- Observer pattern for weather updates

## 🧪 Testing the Application

### Test Scenarios

1. **Pattern Validation**
   - Add multiple cities and verify Singleton behavior
   - Switch sort strategies and verify Strategy pattern
   - Add/remove activities and verify Composite calculations
   - Execute undo/redo and verify Command pattern

2. **GUI Responsiveness**
   - Monitor real-time chart updates every 3 seconds
   - Verify weather filter updates as conditions change
   - Check budget/duration calculations for accuracy
   - Confirm undo/redo functionality

3. **Data Integrity**
   - Nested plan cost aggregation
   - Time calculation with multiple activities
   - Weather state transitions
   - Iterator filtering across weather conditions

## 📝 Implementation Notes

### Thread Safety
- Weather updates run on separate thread
- GUI updates marshalled through `SwingUtilities.invokeLater()`
- Singleton uses synchronized getInstance() method
- Command stacks are thread-safe Stack collections

### Performance Considerations
- Charts render efficiently with Graphics2D antialiasing
- Lazy initialization of city plans (created on first selection)
- Efficient collection operations using streams
- Minimal memory footprint with reusable components

### Extensibility
- Add new sort strategies by implementing `SortStrategy`
- Add new activities by creating `ActivityDecorator` subclasses
- Add new weather conditions by extending `WeatherState` enum
- Add new chart types by extending `JPanel`
- Add new commands by implementing `Command` interface

## 🎓 Educational Value

This project demonstrates:
- **Design Pattern Mastery**: Real-world implementations of 7 core patterns
- **Object-Oriented Principles**: Encapsulation, inheritance, polymorphism
- **GUI Development**: Modern Swing with custom rendering
- **Threading**: Background tasks with observer notifications
- **Data Structures**: Trees, lists, hashmaps for different use cases
- **Software Architecture**: Layered design with clear separation of concerns

## 📄 License

MIT License - Free to use and modify for educational purposes.

## 🤝 Contributing

To extend this project:
1. Create a feature branch: `git checkout -b feature/your-feature`
2. Implement your changes with pattern consistency
3. Test thoroughly with multiple cities and scenarios
4. Submit a pull request with detailed description

## 📞 Support

For questions or issues:
- Review the UML diagram for architecture understanding
- Check existing pattern implementations as examples
- Refer to Java documentation for Swing components
- Consult Gang of Four design patterns book

---

**Last Updated**: May 8, 2026
**Version**: 2.0 (Enhanced GUI)
**Status**: Production Ready ✅
