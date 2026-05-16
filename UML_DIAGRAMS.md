# UML Diyagramları - Smart Travel Planner Platform

## 1. Class Diagram - Decorator Pattern

```
┌─────────────────────────────────────────────────────────────────┐
│                    <<interface>>                                 │
│                    CityActivity                                  │
├─────────────────────────────────────────────────────────────────┤
│ + getDescription(): String                                       │
│ + getCost(): double                                              │
│ + getRequiredTime(): double                                      │
└─────────────────────────────────────────────────────────────────┘
                              △
                              │ implements
                    ┌─────────┴──────────┐
                    │                    │
    ┌───────────────────────────┐  ┌──────────────────────┐
    │   BaseCityActivity        │  │ ActivityDecorator    │
    ├───────────────────────────┤  ├──────────────────────┤
    │ - city: City              │  │ # component: City    │
    ├───────────────────────────┤  │  Activity            │
    │ + getDescription()        │  ├──────────────────────┤
    │ + getCost()               │  │ + getDescription()   │
    │ + getRequiredTime()       │  │ + getCost()          │
    └───────────────────────────┘  │ + getRequiredTime()  │
                                   └──────────────────────┘
                                            △
                        ┌───────────────────┼───────────────────┐
                        │                   │                   │
         ┌──────────────────────┐  ┌──────────────────┐  ┌─────────────────┐
         │ MuseumVisit          │  │ ParkVisit        │  │ Shopping         │
         │ (Decorator)          │  │ (Decorator)      │  │ MallVisit        │
         ├──────────────────────┤  ├──────────────────┤  ├─────────────────┤
         │ Adds: +$15, +2h      │  │ Adds: +$0, +1.5h │  │ Adds: +$25, +3h │
         └──────────────────────┘  └──────────────────┘  └─────────────────┘
                                                           CityCenterVisit (similar)
```

## 2. Class Diagram - Composite Pattern

```
┌──────────────────────────────────────────────────────────────────┐
│                   <<interface>>                                   │
│                  ActivityComponent                                │
├──────────────────────────────────────────────────────────────────┤
│ + add(component: ActivityComponent): void                         │
│ + remove(component: ActivityComponent): void                      │
│ + getChild(index: int): ActivityComponent                         │
│ + getComponentIndex(component): int                               │
│ + getComponentCount(): int                                        │
│ + moveComponentUp(component: ActivityComponent): void             │
│ + moveComponentDown(component: ActivityComponent): void           │
│ + getName(): String                                               │
│ + getCost(): double                                               │
│ + getRequiredTime(): double                                       │
│ + print(): void                                                   │
└──────────────────────────────────────────────────────────────────┘
                              △
                              │ implements
                    ┌─────────┴──────────┐
                    │                    │
    ┌───────────────────────────┐  ┌──────────────────────────┐
    │    ActivityPlan           │  │   ActivityLeaf           │
    ├───────────────────────────┤  ├──────────────────────────┤
    │ - components: List<AC>    │  │ - name: String           │
    │ - planName: String        │  │ - cost: double           │
    ├───────────────────────────┤  │ - requiredTime: double   │
    │ + add(component)          │  ├──────────────────────────┤
    │ + remove(component)       │  │ + getName()              │
    │ + getChild(index)         │  │ + getCost()              │
    │ + getComponentIndex()     │  │ + getRequiredTime()      │
    │ + getComponentCount()     │  │ + print()                │
    │ + moveComponentUp()       │  │ + add() - throws ex      │
    │ + moveComponentDown()     │  │ + remove() - throws ex   │
    │ + getName()               │  └──────────────────────────┘
    │ + getCost() - sums all    │
    │ + getRequiredTime() - sum │
    │ + print()                 │
    └───────────────────────────┘
            │
            │ contains
            ▼
        ActivityComponent
```

## 3. Class Diagram - Command Pattern

```
┌──────────────────────────────────┐
│      <<interface>>               │
│         Command                  │
├──────────────────────────────────┤
│ + execute(): void                │
│ + undo(): void                   │
└──────────────────────────────────┘
           △
           │ implements
    ┌──────┴──────────┬──────────────┬──────────────┬──────────────┐
    │                 │              │              │              │
┌────────────────┐ ┌────────────────┐ ┌────────────────┐ ┌────────────────┐
│AddCityCommand  │ │RemoveCityCmd   │ │AddActivityCmd  │ │RemoveActivityC │
├────────────────┤ ├────────────────┤ ├────────────────┤ ├────────────────┤
│-cityToAdd      │ │-cityToRemove   │ │-parentPlan     │ │-parentPlan     │
│+execute()      │ │+execute()      │ │-componentAdd   │ │-componentRm    │
│+undo()         │ │+undo()         │ │+execute()      │ │+execute()      │
└────────────────┘ └────────────────┘ │+undo()         │ │+undo()         │
                                       └────────────────┘ └────────────────┘

┌──────────────────────────────┐
│   CommandManager             │
├──────────────────────────────┤
│ - executeStack: Stack        │
│ - undoStack: Stack           │
├──────────────────────────────┤
│ + executeCommand(cmd)        │
│ + undo()                     │
│ + redo()                     │
└──────────────────────────────┘
```

## 4. Class Diagram - Observer Pattern

```
┌─────────────────────────────────┐
│   <<interface>>                 │
│    WeatherObserver              │
├─────────────────────────────────┤
│ + update(cities: List): void    │
└─────────────────────────────────┘
           △
           │ implements
           │
    ┌──────────────────────┐
    │  TravelPlannerGUI    │
    ├──────────────────────┤
    │ + update(cities)     │
    └──────────────────────┘


┌────────────────────────────────────────┐
│   WeatherReportProvider                │
├────────────────────────────────────────┤
│ - cities: List<City>                   │
│ - observers: List<WeatherObserver>     │
├────────────────────────────────────────┤
│ + addObserver(observer)                │
│ + removeObserver(observer)             │
│ + notifyObservers()                    │
│ + run()                                │
│ + updateWeatherRandomly()              │
└────────────────────────────────────────┘
```

## 5. Class Diagram - Strategy Pattern

```
┌──────────────────────────────┐
│  <<interface>>               │
│   SortStrategy               │
├──────────────────────────────┤
│ + sort(cities: List): void   │
└──────────────────────────────┘
           △
    ┌──────┴──────┬──────────┐
    │             │          │
┌──────────────┐ ┌─────────────────┐ ┌──────────────┐
│SortByName    │ │SortByPopulation │ │SortByArea    │
├──────────────┤ ├─────────────────┤ ├──────────────┤
│+sort()       │ │+sort()          │ │+sort()       │
│ uses City    │ │ uses City       │ │ uses City    │
│ name         │ │ population      │ │ area         │
└──────────────┘ └─────────────────┘ └──────────────┘
```

## 6. Class Diagram - Repository Pattern

```
┌─────────────────────────────────┐
│   CityRepository (Singleton)     │
├─────────────────────────────────┤
│ - instance: CityRepository       │
│ - cities: List<City>            │
├─────────────────────────────────┤
│ + getInstance()                 │
│ + getCities()                   │
│ + addCity(city)                 │
│ + removeCity(city)              │
│ + findCityByName(name)          │
└─────────────────────────────────┘
           │
           │ manages
           ▼
┌──────────────────────────────────┐
│       City                       │
├──────────────────────────────────┤
│ - name: String                   │
│ - population: long               │
│ - area: double                   │
│ - temperature: double            │
│ - weatherState: WeatherState     │
├──────────────────────────────────┤
│ + getName()                      │
│ + getPopulation()                │
│ + getArea()                      │
│ + setTemperature(temp)           │
│ + getTemperature()               │
│ + setWeatherState(state)         │
│ + getWeatherState()              │
└──────────────────────────────────┘
```

## 7. Sequence Diagram - Add Activity

```
User      GUI             CommandManager      ActivityPlan    ActivityLeaf
│         │                    │                   │              │
├─Add─Activity──>               │                   │              │
│         │                     │                   │              │
│         ├─new AddActivityCmd──>                   │              │
│         │                     │                   │              │
│         ├─executeCommand()────>                   │              │
│         │                     │                   │              │
│         │                     ├─execute()────────>│              │
│         │                     │                   │              │
│         │                     │                   ├─add(leaf)───>│
│         │                     │                   │              │
│         │                     │<──────success─────│              │
│         │                     │                   │              │
│         │<──command logged────┤                   │              │
│         │                     │                   │              │
│         ├─updateTree()───────>│                   │              │
│         │                     │                   │              │
│         ├─GUI updates────────>│                   │              │
│         │                     │                   │              │
```

## 8. State Diagram - Weather Updates

```
                    ┌────────────────┐
                    │   SUNNY        │
                    └────────────────┘
                      │           △
                      │           │
                      ▼           │
                    ┌────────────────┐
         ┌─────────>│   CLOUDY       │<─────────┐
         │          └────────────────┘          │
         │            │           △             │
         │            │           │             │
    ┌────────────────┐ │           │ ┌────────────────┐
    │    RAINY       │ └───────────┴─>    SNOWY       │
    └────────────────┘                 └────────────────┘
         │                                     │
         └─────────────────┬───────────────────┘
                           │
                    Random Update
                    (10 second interval)
```

## 9. Architecture Overview

```
┌────────────────────────────────────────────────────────────────────┐
│                      TravelPlannerGUI                              │
│          (Main Swing Application - WeatherObserver)                │
├────────────────────────────────────────────────────────────────────┤
│                                                                    │
│  ┌──────────────────────────────────────────────────────────────┐ │
│  │ Presentation Layer                                           │ │
│  │ - City lists (All Cities, Weather Filtered)                 │ │
│  │ - Activity Tree (Composite visualization)                   │ │
│  │ - Charts (Temperature, Weather Distribution)                │ │
│  └──────────────────────────────────────────────────────────────┘ │
│                           △                                        │
│                           │                                        │
│  ┌──────────────────────────────────────────────────────────────┐ │
│  │ Business Logic Layer                                         │ │
│  │ - Command Execution (CommandManager)                        │ │
│  │ - Activity Planning (Composite + Decorator)                 │ │
│  │ - Sorting & Filtering (Strategy + Iterator)                 │ │
│  └──────────────────────────────────────────────────────────────┘ │
│                           △                                        │
│                           │                                        │
│  ┌──────────────────────────────────────────────────────────────┐ │
│  │ Data Access Layer                                            │ │
│  │ - CityRepository (Singleton)                                │ │
│  │ - WeatherReportProvider (Observable)                        │ │
│  └──────────────────────────────────────────────────────────────┘ │
│                                                                    │
└────────────────────────────────────────────────────────────────────┘
```

## 10. Design Pattern Summary Table

| Pattern | Purpose | Location | Key Classes |
|---------|---------|----------|------------|
| **Decorator** | Add features dynamically | `decorator/` | ActivityDecorator, MuseumVisit, ParkVisit, etc. |
| **Composite** | Tree structure | `composite/` | ActivityPlan, ActivityLeaf, ActivityComponent |
| **Command** | Encapsulate actions (Undo/Redo) | `command/` | CommandManager, AddActivityCommand, RemoveActivityCommand |
| **Observer** | Weather updates | `observer/` | WeatherObserver, WeatherReportProvider |
| **Iterator** | Weather filtering | `iterator/` | WeatherCityIterator |
| **Strategy** | Different sorting | `strategy/` | SortStrategy, SortByName, SortByPopulation, SortByArea |
| **Repository** | Data access | `repository/` | CityRepository (Singleton) |
| **Singleton** | Single instance | `repository/`, `command/` | CityRepository, CommandManager |
