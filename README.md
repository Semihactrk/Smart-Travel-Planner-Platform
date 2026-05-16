# Smart Travel Planner Platform

## Proje Açıklaması

Smart Travel Planner Platform, seyahat planlaması için kapsamlı bir Java uygulamasıdır. Kullanıcılar şehirler seçebilir, faaliyetler planlayabilir, hava durumuna göre filtreleme yapabilir ve detaylı planlama ağacı oluşturabilirler.

## Kullanılan Tasarım Desenleri

### 1. **Decorator Pattern** (`decorator/`)
- **Amaç**: Temel aktivitelere dinamik olarak özellik eklemek
- **Sınıflar**:
  - `BaseCityActivity`: Temel aktivite
  - `ActivityDecorator`: Dekoratör temel sınıfı
  - `MuseumVisit`: Müze ziyareti decorator'ü
  - `ParkVisit`: Park ziyareti decorator'ü
  - `ShoppingMallVisit`: Alışveriş merkezi decorator'ü
  - `CityCenterVisit`: Şehir merkezi ziyareti decorator'ü

**Örnek Kullanım**:
```java
CityActivity activity = new MuseumVisit(
    new ShoppingMallVisit(
        new BaseCityActivity(city)
    )
);
```

### 2. **Composite Pattern** (`composite/`)
- **Amaç**: Aktiviteleri ağaç yapısında organize etmek
- **Sınıflar**:
  - `ActivityComponent`: Interface
  - `ActivityPlan`: Bileşik düğüm (ActivityPlan'lar içerebilir)
  - `ActivityLeaf`: Yaprak düğüm (tek aktivite)

**Ağaç Yapısı**:
```
Root Plan
├── Day 1 Plan
│   ├── Museum Visit (Cost: $15, Time: 2h)
│   ├── Park Visit (Cost: $0, Time: 1.5h)
│   └── Shopping Mall (Cost: $25, Time: 3h)
└── Day 2 Plan
    └── City Center Visit (Cost: $12, Time: 2.5h)
```

### 3. **Command Pattern** (`command/`)
- **Amaç**: İşlemleri komut nesnelerine dönüştürmek (Undo/Redo desteği)
- **Sınıflar**:
  - `Command`: Interface
  - `CommandManager`: Komut yöneticisi
  - `AddCityCommand`: Şehir ekleme
  - `RemoveCityCommand`: Şehir kaldırma
  - `AddActivityCommand`: Aktivite ekleme
  - `RemoveActivityCommand`: Aktivite kaldırma
  - `AddPlanNodeCommand`: Plan düğümü ekleme
  - `MoveActivityUpCommand`: Aktiviteyi yukarı taşıma
  - `MoveActivityDownCommand`: Aktiviteyi aşağı taşıma
  - `ClearPlanCommand`: Planı temizleme

### 4. **Observer Pattern** (`observer/`)
- **Amaç**: Hava durumu güncellemelerini izlemek
- **Sınıflar**:
  - `WeatherObserver`: Observer interface
  - `WeatherReportProvider`: Observable sınıfı
  - Güncellemeler otomatik olarak GUI'ye yayınlanır

### 5. **Iterator Pattern** (`iterator/`)
- **Amaç**: Şehirleri hava durumuna göre dolaşmak
- **Sınıflar**:
  - `WeatherCityIterator`: Hava durumuna göre filtreleme

### 6. **Strategy Pattern** (`strategy/`)
- **Amaç**: Farklı sıralama stratejileri
- **Sınıflar**:
  - `SortStrategy`: Interface
  - `SortByName`: İsme göre sırala
  - `SortByPopulation`: Nüfusa göre sırala
  - `SortByArea`: Alana göre sırala

### 7. **Repository Pattern** (`repository/`)
- **Amaç**: Veri erişim katmanı
- **Sınıflar**:
  - `CityRepository`: Singleton pattern ile şehir yönetimi

### 8. **Singleton Pattern**
- `CityRepository`
- `CommandManager` (GUI tarafından)

## Proje Yapısı

```
src/com/smarttravel/
├── gui/
│   ├── TravelPlannerGUI.java       (Ana GUI)
│   ├── BarChartPanel.java          (Grafik paneli)
│   └── PieChartPanel.java          (Pasta grafiği)
├── composite/
│   ├── ActivityComponent.java       (Interface)
│   ├── ActivityPlan.java            (Bileşik)
│   └── ActivityLeaf.java            (Yaprak)
├── decorator/
│   ├── CityActivity.java
│   ├── ActivityDecorator.java
│   ├── BaseCityActivity.java
│   ├── MuseumVisit.java
│   ├── ParkVisit.java
│   ├── ShoppingMallVisit.java
│   └── CityCenterVisit.java
├── command/
│   ├── Command.java
│   ├── CommandManager.java
│   ├── AddCityCommand.java
│   ├── RemoveCityCommand.java
│   ├── AddActivityCommand.java
│   ├── RemoveActivityCommand.java
│   ├── AddPlanNodeCommand.java
│   ├── MoveActivityUpCommand.java
│   ├── MoveActivityDownCommand.java
│   └── ClearPlanCommand.java
├── observer/
│   ├── WeatherObserver.java
│   └── WeatherReportProvider.java
├── iterator/
│   └── WeatherCityIterator.java
├── strategy/
│   ├── SortStrategy.java
│   ├── SortByName.java
│   ├── SortByPopulation.java
│   └── SortByArea.java
├── repository/
│   └── CityRepository.java
├── City.java
└── WeatherState.java
```

## Özellikler

### 1. **Şehir Yönetimi**
- Tüm şehirleri görüntüle
- Şehirleri ada göre sırala (İsim, Nüfus, Alan)
- Şehirleri hava durumuna göre filtrele

### 2. **Seyahat Planlama**
- Şehir başına etkinlik planları oluştur
- Günlere bölünmüş aktiviteler
- Aktiviteleri ekle, kaldır ve sırala
- Özel aktiviteler oluştur

### 3. **Aktivite Dekorasyon**
- Müze ziyareti
- Park ziyareti
- Alışveriş merkezi
- Şehir merkezi ziyareti

### 4. **Veriler**
- Maliyet hesaplama
- Zaman hesaplama
- Otomatik toplam hesaplama

### 5. **Undo/Redo**
- Tüm komutlar geri alınabilir
- Tüm komutlar yeniden yapılabilir

### 6. **Görselleştirme**
- Etkinlik ağacı görünümü
- Şehir sıcaklıkları çubuk grafik
- Hava durumu dağılımı pasta grafiği

## Kullanım

### Başlangıç
```bash
javac -d bin -cp src src/**/*.java
java -cp bin com.smarttravel.gui.TravelPlannerGUI
```

### Temel İş Akışı
1. **Şehir Seç**: Sol taraftaki "All Cities" listesinden bir şehir seç
2. **Plan Düğümü Ekle**: "Add Activity Plan Node" ile gün planı oluştur
3. **Aktivite Ekle**: Önceden tanımlanmış veya özel aktiviteler ekle
4. **Düzenle**: Aktiviteleri sırala veya kaldır
5. **Geri Al**: Undo butonu ile hataları düzelt

## Katılımcılar

| Rol | Adı | Sorumluluklar |
|-----|-----|--------------|
| Proje Yöneticisi | Serenay Kumandaveren | Genel koordinasyon, tasarım |
| Geliştirici 1 | Serenay Kumandaveren | Decorator, Command, Observer |
| Geliştirici 2 | Serenay Kumandaveren | Composite, Iterator, Strategy |
| Test Uzmanı | Serenay Kumandaveren | GUI Testing, Senaryolar |

## Teknik Gereksinimler

- **Java Version**: 8 veya üzeri
- **Swing Framework**: GUI için
- **JUnit**: Unit testing (opsiyonel)

## Notlar

- Repository'de ön yüklenmiş 20+ şehir vardır
- Hava durumu simülasyonu periyodik olarak güncellenir
- Tüm maliyetler USD cinsinden
- Tüm zamanlar saat cinsinden

## Gelecek Geliştirmeler

- [ ] Harita entegrasyonu
- [ ] Otel ayırma
- [ ] Ulaşım rehberi
- [ ] Bütçe önerileri
- [ ] Sosyal paylaşım
- [ ] Offline mod
