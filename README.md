# ✈ Smart Travel Planner Platform

## Project Overview

The **Smart Travel Planner Platform** is a comprehensive Java application designed for exploring, monitoring, and planning trips across different cities. Built using modern design patterns, this team project integrates multiple subsystems to provide a rich, interactive travel planning experience.

### Key Features

- 🌍 **City Exploration**: Browse and sort cities by name, population, or area
- 🌤 **Weather Monitoring**: Real-time weather updates with dynamic filtering
- 📋 **Travel Planning**: Create hierarchical activity plans with cost and time tracking
- 📊 **Analytics**: Temperature charts and weather distribution visualization
- ↩️ **Undo/Redo**: Full command history support for all user actions
- 🎨 **Modern UI**: Enhanced, elegant interface with intuitive controls

---

## Design Patterns Implemented

This project demonstrates the practical application of 7 essential design patterns:

### 1. **Singleton Pattern**
- **Class**: `CityRepository`
- **Purpose**: Ensures a single instance manages city data
- **Implementation**: Thread-safe singleton that loads city data from JSON once

### 2. **Strategy Pattern**
- **Classes**: `SortStrategy`, `SortByName`, `SortByPopulation`, `SortByArea`
- **Purpose**: Enables flexible sorting algorithms without modifying client code
- **Usage**: Users select sorting criteria from dropdown; strategy is applied dynamically

### 3. **Iterator Pattern**
- **Classes**: `WeatherCityIterator`, `WeatherObserver`
- **Purpose**: Provides sequential access to cities filtered by weather conditions
- **Usage**: Four iterators for each weather state (SUNNY, CLOUDY, RAINY, SNOWY)

### 4. **Observer Pattern (Publisher-Subscriber)**
- **Classes**: `WeatherReportProvider`, `WeatherObserver`, `ModernTravelPlannerGUI`
- **Purpose**: Enables real-time updates to weather data and UI
- **Implementation**: Weather provider runs on separate thread, updates every 3 seconds

### 5. **Decorator Pattern**
- **Classes**: `MuseumVisit`, `ShoppingMallVisit`, `ParkVisit`, `CityCenterVisit`
- **Purpose**: Wraps City objects with additional "visit planning" features
- **Features**: Each decorator adds cost and time information

### 6. **Composite Pattern**
- **Classes**: `ActivityComponent`, `ActivityPlan`, `ActivityLeaf`
- **Purpose**: Creates hierarchical tree structure for travel plans
- **Features**:
  - Recursive composition of activities and sub-plans
  - Automatic cost and time calculation
  - Support for complex trip structures

### 7. **Command Pattern**
- **Classes**: `Command`, `CommandManager`, `AddActivityCommand`, `ClearPlanCommand`
- **Purpose**: Encapsulates user actions as objects for undo/redo support
- **Operations**:
  - Add city to trip
  - Remove city from trip
  - Add activity to plan
  - Remove activity from plan
  - Clear entire plan

---

## Project Structure

```
src/com/smarttravel/
├── City.java                          # Core city entity
├── WeatherState.java                  # Weather enumeration
├── repository/
│   └── CityRepository.java            # Singleton city manager
├── strategy/
│   ├── SortStrategy.java              # Strategy interface
│   ├── SortByName.java                # Name-based sorting
│   ├── SortByPopulation.java          # Population-based sorting
│   └── SortByArea.java                # Area-based sorting
├── iterator/
│   └── WeatherCityIterator.java       # Weather-based city iterator
├── observer/
│   ├── WeatherObserver.java           # Observer interface
│   └── WeatherReportProvider.java     # Observable weather provider
├── decorator/
│   ├── CityActivity.java              # Activity interface
│   ├── BaseCityActivity.java          # Base activity implementation
│   ├── MuseumVisit.java               # Museum activity decorator
│   ├── ShoppingMallVisit.java         # Shopping activity decorator
│   ├── ParkVisit.java                 # Park activity decorator
│   └── CityCenterVisit.java           # City center activity decorator
├── composite/
│   ├── ActivityComponent.java         # Composite interface
│   ├── ActivityPlan.java              # Composite node
│   └── ActivityLeaf.java              # Leaf node
├── command/
│   ├── Command.java                   # Command interface
│   ├── CommandManager.java            # Command executor with undo/redo
│   ├── AddActivityCommand.java        # Add activity command
│   └── ClearPlanCommand.java          # Clear plan command
└── gui/
    ├── ModernTravelPlannerGUI.java    # Main GUI application
    ├── EnhancedBarChartPanel.java     # Temperature chart visualization
    └── EnhancedPieChartPanel.java     # Weather distribution chart
```

---

## Installation & Setup

### Prerequisites
- Java 11 or higher
- Maven (optional, for dependency management)
- IDE: IntelliJ IDEA, Eclipse, or NetBeans

### Building the Project

```bash
# Clone the repository
git clone https://github.com/Semihactrk/Smart-Travel-Planner-Platform.git
cd Smart-Travel-Planner-Platform

# Compile the project
javac -d bin src/com/smarttravel/**/*.java

# Or using Maven
mvn clean compile
```

### Running the Application

```bash
# Run the main GUI application
java -cp bin com.smarttravel.gui.ModernTravelPlannerGUI

# Or using Maven
mvn exec:java -Dexec.mainClass="com.smarttravel.gui.ModernTravelPlannerGUI"
```

---

## User Guide

### Interface Overview

The application is divided into 6 main sections:

#### 1. **Control Panel (Top)**
- **Sort Dropdown**: Choose sorting criteria (Name, Population, Area)
- **Weather Filter**: Filter cities by weather conditions
- **Undo/Redo Buttons**: Navigate command history

#### 2. **All Cities Panel (Left)**
- Displays all cities in selected sort order
- Click to select a city for planning activities

#### 3. **Filtered Cities Panel (Middle-Left)**
- Shows cities matching the selected weather condition
- Synchronized with main city list

#### 4. **Activity Planner Panel (Middle)**
- **Activity Buttons**: Add specific activities (Museum, Shopping, Park, etc.)
- **Add Plan Button**: Create hierarchical sub-plans
- **Clear Button**: Remove all activities from current city

#### 5. **Travel Plan Panel (Middle-Right)**
- Tree view of hierarchical activity structure
- Shows cost and time for each activity
- Displays total plan summary

#### 6. **Analytics Panels (Bottom)**
- **Temperature Chart**: Bar graph of current temperatures
- **Weather Distribution**: Pie chart showing weather percentages

#### 7. **Info Panel (Bottom)**
- **Selected City**: Current city being edited
- **Total Budget**: Sum of all activity costs
- **Total Duration**: Sum of all activity times

### Step-by-Step Usage

#### Creating a Travel Plan

1. **Select a City**: Click on a city in the "All Cities" list
   - Status updates in the info panel

2. **Add Activities**: Click activity buttons to add planned activities
   - Museum Visit: $20, 2 hours
   - Shopping Mall: $50, 3 hours
   - Park Visit: $10, 2 hours
   - City Center: $15, 1.5 hours
   - Cinema: $15, 3 hours
   - Dinner: $45, 2 hours

3. **Create Sub-Plans**: Click "Add Plan" to organize activities
   - Enter plan name (e.g., "Morning Activities")
   - Activities added will be grouped under this plan

4. **Monitor Budget**: View total cost and duration at the bottom

5. **Switch Cities**: Select a different city to create separate plans
   - Each city maintains its own activity tree

#### Filtering and Sorting

- **By Weather**: Select condition from dropdown
- **By Property**: Choose sort criteria
- **Real-time Updates**: Weather changes every 3 seconds

#### Undoing Actions

- **Undo**: Reverts the last action
- **Redo**: Restores a reverted action
- **Full History**: All actions maintain complete undo/redo stack

---

## Data Structure: Cities

The application includes 7 sample cities:

| City | Population | Area (km²) | Weather |
|------|-----------|-----------|----------|
| Ankara | 5,317,215 | 25,615.0 | SUNNY |
| Buhara | 280,000 | 143.1 | CLOUDY |
| Gaziantep | 1,835,508 | 6,778.9 | SUNNY |
| Gazze | 2,180,400 | 70.9 | SNOWY |
| Istanbul | 15,719,600 | 5,343.0 | SNOWY |
| Kudus | 981,711 | 126.0 | CLOUDY |
| Saraybosna | 347,000 | 141.6 | RAINY |

**Note**: Weather and temperature update randomly every 3 seconds for demonstration.

---

## Technical Features

### Real-Time Updates
- Weather provider thread updates conditions every 3 seconds
- Observer pattern ensures UI stays synchronized
- Charts and filters update automatically

### Hierarchical Activity Planning
- Support for unlimited nesting levels
- Automatic recursive cost/time calculation
- Full composite pattern implementation

### Command History
- Stack-based undo/redo system
- All user actions are reversible
- Clean separation of commands from business logic

### Modern UI/UX
- Color-coded sections for visual organization
- Emoji icons for enhanced usability
- Responsive layout with proper spacing
- Professional typography and color scheme

---

## Team Contributions

### Serenay Kumandaveren
- **GUI Enhancement**: Modern, elegant interface redesign
- **Chart Visualization**: Enhanced bar and pie charts
- **User Experience**: Intuitive layout and controls
- **Documentation**: Comprehensive README and usage guide

### Additional Team Members
- Core pattern implementations
- City repository and data management
- Weather observer system
- Activity decorator hierarchy

---

## UML Diagram

A complete UML diagram showing all design patterns and their relationships is provided as `UML_Diagram.pdf`. The diagram includes:

- Pattern role mappings
- Class hierarchies and relationships
- Interface implementations
- Dependency arrows
- Component interactions

---

## Future Enhancements

- 💾 **Save/Load**: Persist travel plans to JSON/XML
- 🌐 **Network**: Multi-user collaboration features
- 📱 **Mobile**: Cross-platform mobile application
- 🗺 **Maps Integration**: Google Maps API integration
- 💳 **Payment**: Integration with travel booking systems
- 🔍 **Search**: Advanced city and activity search
- 📧 **Sharing**: Export plans as PDF or email

---

## Requirements Met

✅ All 7 design patterns meaningfully implemented
✅ GUI with 6+ panel types
✅ Real-time weather updates
✅ Hierarchical activity planning
✅ Full undo/redo support
✅ Modern, elegant interface
✅ Comprehensive documentation
✅ Runnable JAR file
✅ UML diagram with pattern mappings

---

## License

This project is created for educational purposes as part of a Software Design Patterns course.

---

## Contact & Support

For questions or issues, please contact the development team or open an issue on GitHub.

**Repository**: [Smart-Travel-Planner-Platform](https://github.com/Semihactrk/Smart-Travel-Planner-Platform)
