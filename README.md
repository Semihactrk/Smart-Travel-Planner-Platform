# ✈ Smart Travel Planner Platform

## Project Overview

The **Smart Travel Planner Platform** is a comprehensive Java application designed for exploring, monitoring, and planning multi-city trips. The system integrates multiple design patterns to provide an elegant, extensible, and feature-rich travel planning experience.

### Key Features
- **City Management**: Browse and filter cities by various criteria
- **Real-time Weather Updates**: Live weather monitoring with dynamic chart updates
- **Travel Planning**: Create hierarchical activity plans with multiple nested levels
- **Budget Tracking**: Automatic cost and duration calculation for travel activities
- **Undo/Redo Functionality**: Full command history support for user actions
- **Rich GUI**: Modern, intuitive interface with interactive charts and controls

---

## Design Patterns Implemented

### 1. **Singleton Pattern** (CityRepository)
Ensures a single instance of the city repository throughout the application lifecycle.
```java
public static synchronized CityRepository getInstance() {
    if (instance == null) {
        instance = new CityRepository();
    }
    return instance;
}
```

### 2. **Strategy Pattern** (Sorting)
Provides interchangeable sorting algorithms for cities:
- `SortByName` - Alphabetical ordering
- `SortByPopulation` - Descending by population
- `SortByArea` - Descending by area

```java
public interface SortStrategy {
    void sort(List<City> cities);
}
```

### 3. **Iterator Pattern** (Weather Filtering)
Implements custom iterators for filtering cities by weather conditions:
- `WeatherCityIterator` - Traverses cities matching a specific weather state

### 4. **Observer Pattern** (Weather Updates)
- **Subject**: `WeatherReportProvider` - Updates weather information every 3 seconds
- **Observer**: `WeatherObserver` - Receives updates and refreshes UI components

```java
public interface WeatherObserver {
    void update(List<City> cities);
}
```

### 5. **Decorator Pattern** (Activity Planning)
Dynamically adds activity options to cities without modifying the City class:
- `MuseumVisit` - €25, 2 hours
- `ShoppingMallVisit` - €40, 3 hours
- `ParkVisit` - €10, 1.5 hours
- `CityCenterVisit` - €20, 2 hours

```java
public abstract class ActivityDecorator implements CityActivity {
    protected CityActivity wrappedActivity;
    // Implementation...
}
```

### 6. **Composite Pattern** (Activity Plans)
Allows building hierarchical activity plans:
- `ActivityPlan` (Composite) - Contains multiple activities or sub-plans
- `ActivityLeaf` (Leaf) - Individual activity with cost and duration

```java
public interface ActivityComponent {
    void add(ActivityComponent component);
    void remove(ActivityComponent component);
    double getCost();
    double getRequiredTime();
}
```

### 7. **Command Pattern** (Undo/Redo)
Encapsulates user actions as command objects:
- `AddActivityCommand` - Adds activities to plans
- `ClearPlanCommand` - Clears all activities from a plan
- `CommandManager` - Manages undo/redo stacks

```java
public interface Command {
    void execute();
    void undo();
}
```

---

## Project Structure

```
src/com/smarttravel/
├── City.java                          # Core city model
├── WeatherState.java                  # Weather enumeration
├── repository/
│   └── CityRepository.java            # Singleton city data source
├── strategy/
│   ├── SortStrategy.java              # Strategy interface
│   ├── SortByName.java
│   ├── SortByPopulation.java
│   └── SortByArea.java
├── iterator/
│   └── WeatherCityIterator.java       # Weather-based filtering
├── observer/
│   ├── WeatherObserver.java           # Observer interface
│   └── WeatherReportProvider.java     # Observable weather source
├── decorator/
│   ├── CityActivity.java              # Decorator interface
│   ├── ActivityDecorator.java         # Base decorator
│   ├── MuseumVisit.java
│   ├── ShoppingMallVisit.java
│   ├── ParkVisit.java
│   └── CityCenterVisit.java
├── composite/
│   ├── ActivityComponent.java         # Component interface
│   ├── ActivityPlan.java              # Composite
│   └── ActivityLeaf.java              # Leaf
├── command/
│   ├── Command.java                   # Command interface
│   ├── CommandManager.java            # Undo/redo manager
│   ├── AddActivityCommand.java
│   └── ClearPlanCommand.java
└── gui/
    ├── ModernTravelPlannerGUI.java    # Main GUI (enhanced version)
    ├── EnhancedBarChartPanel.java     # Temperature visualization
    └── EnhancedPieChartPanel.java     # Weather distribution
```

---

## Installation & Setup

### Prerequisites
- Java 8 or higher
- Maven (for building)

### Building the Project

```bash
# Clone the repository
git clone https://github.com/Semihactrk/Smart-Travel-Planner-Platform.git
cd Smart-Travel-Planner-Platform

# Compile
mvn clean compile

# Package as JAR
mvn clean package
```

### Running the Application

```bash
# Using Maven
mvn exec:java -Dexec.mainClass="com.smarttravel.gui.ModernTravelPlannerGUI"

# Using JAR file
java -jar Smart-Travel-Planner-Platform.jar
```

---

## Usage Guide

### Main Interface Sections

#### 1. **Control Panel** (Top)
- **Sort Dropdown**: Choose sorting method (Name, Population, Area)
- **Weather Filter**: Filter cities by weather condition
- **Undo/Redo Buttons**: Navigate command history

#### 2. **City Lists** (Left Section)
- **All Cities**: Complete list of available cities
- **Filtered Cities**: Cities matching current weather filter

#### 3. **Activity Planner** (Top Right)
Quick-add buttons for common activities:
- 🏛 Museum
- 🛍 Shopping Mall
- 🌳 Park
- 🏰 City Center
- 🎬 Cinema
- 🍽 Dinner
- 🗑 Clear Plan
- 📝 Add Plan

#### 4. **Travel Plan Tree** (Center)
Hierarchical view of activities organized by city:
- Expandable/collapsible plan nodes
- Cost and duration display for each activity

#### 5. **Analytics Charts** (Bottom)
- **Temperature Chart**: Bar chart showing current temperatures
- **Weather Distribution**: Pie chart showing weather percentages

#### 6. **Info Panel** (Bottom Status)
- Current selected city
- Total budget for selected city
- Total duration for selected city

### Workflow Example

1. **Select a City**: Click on any city in the "All Cities" list
2. **Add Activities**: Click activity buttons to add to the selected city
3. **Create Sub-Plans**: Click "Add Plan" to create hierarchical organization
4. **Monitor Budget**: Watch the budget and duration update in real-time
5. **Undo Changes**: Use "Undo" to revert any action
6. **Filter by Weather**: Use the weather dropdown to find suitable destinations

---

## Team Contribution

### Team Members
- **Serenay Kumandaveren** - GUI Enhancement & Improvement
  - Modern UI Design Implementation
  - Enhanced Chart Panels
  - User Experience Improvements

- **Semiha Çiturk** - Core Architecture & Patterns
  - Design pattern implementation
  - Repository and data management
  - Observer/weather system

- **[Third Team Member]** - Activity Planning & Commands
  - Composite pattern implementation
  - Command/undo-redo system
  - Decorator implementations

---

## Configuration Files

### Cities Data
Cities are loaded from an internal mock data source. To modify cities, edit `CityRepository.loadCitiesFromJson()`:

```java
private void loadCitiesFromJson(String filename) {
    cities.add(new City("Istanbul", 15719600, 5343.0, 34.3, WeatherState.SNOWY));
    // Add more cities...
}
```

### Activity Costs
Customize activity costs in decorator classes:

```java
public class MuseumVisit extends ActivityDecorator {
    private static final double COST = 25.0;
    private static final double TIME = 2.0;
}
```

---

## Advanced Features

### Dynamic Weather Updates
The weather system runs on a separate thread and updates cities every 3 seconds:
- Random temperature changes (±5°C)
- Random weather state transitions
- Automatic UI refresh

### Hierarchical Plans
Create nested activity plans:
```
My Travel Plan
├── Istanbul Plan
│   ├── Museums Day
│   │   ├── Museum Visit
│   │   └── City Center Visit
│   └── Shopping Day
│       ├── Shopping Mall
│       └── Dinner
└── Ankara Plan
    ├── Park Visit
    └── Cinema
```

### Real-time Cost Calculation
Automatically calculates:
- Individual activity costs
- Total plan costs
- Cumulative city visit costs

---

## Performance Considerations

- **Memory**: Efficient list management with ArrayList
- **Threading**: Weather updates run on separate daemon thread
- **UI Updates**: SwingUtilities.invokeLater() ensures thread safety
- **Rendering**: Graphics2D with antialiasing for smooth charts

---

## Future Enhancements

1. **Data Persistence**: Save/load plans to JSON
2. **Advanced Filtering**: Multi-criteria city search
3. **Distance Calculation**: Route optimization between cities
4. **Budget Alerts**: Notifications when exceeding budget
5. **Export Options**: PDF/Excel export of travel plans
6. **Mobile Version**: Responsive design for tablets
7. **User Preferences**: Customizable themes and layouts

---

## Troubleshooting

### Issue: GUI not displaying properly
**Solution**: Ensure Java 8+ is installed and display scaling is set correctly:
```bash
java -Dsun.java2d.dpiaware=false -jar Smart-Travel-Planner-Platform.jar
```

### Issue: Weather updates not showing
**Solution**: Check that the weather provider thread is running. Verify observer registration.

### Issue: Undo/Redo not working
**Solution**: Ensure commands are executed through `CommandManager.executeCommand()`

---

## UML Class Diagram

See `uml-diagram.pdf` for the complete design pattern mapping and class relationships.

---

## License

This project is developed for educational purposes as part of a Design Patterns course.

---

## Contact & Support

For questions or issues, please contact:
- **Serenay Kumandaveren**: serenaykumandaveren11@gmail.com

---

## Acknowledgments

- Course Instructor: [Professor Name]
- Design Patterns Reference: Gang of Four patterns
- Java Swing Framework Documentation
