# Hunting Season

![Java](https://img.shields.io/badge/Java-22-orange)
![JavaFX](https://img.shields.io/badge/JavaFX-25-1f6feb)
![Concurrency](https://img.shields.io/badge/java.util.concurrent-threads-6e40c9)

Многопоточная 2D-игра «Сезон охоты» на JavaFX (учебный проект). Охотник ходит по лесу, стреляет по зверям,
собирает бонусы и старается не попасться волкам и медведям. Каждый зверь живёт в своём потоке.

## Возможности

| № | Функция | Что умеет |
|---|---|---|
| 1 | Звери | олень, волк, заяц, медведь: у каждого своя скорость, здоровье и манера движения, убегают от охотника |
| 2 | Агрессия | волки и медведи замечают охотника, преследуют и атакуют с перезарядкой атаки |
| 3 | Оружие | винтовка, дробовик, пистолет: свой магазин, урон, точность и разброс, перезарядка |
| 4 | Стрельба | точность падает с расстоянием, урон зависит от дистанции и «брони» зверя, промах отнимает здоровье |
| 5 | Бонусы | здоровье, патроны, очки, не больше трёх на карте одновременно |
| 6 | Сохранение | сохранение и загрузка игры из меню паузы и со стартового экрана |
| 7 | Статистика | очки, здоровье, время, выстрелы, попадания, точность, перезарядки, экран Game Over |

### Звери

| Зверь | Очки | Здоровье | Поведение |
|---|---|---|---|
| Заяц | 10 | 20 | быстрый, часто меняет направление |
| Олень | 50 | 60 | убегает, если охотник ближе 150 px |
| Волк | 30 | 45 | замечает с 200 px, атакует на 15 урона |
| Медведь | 100 | 120 | медленный и бронированный, атакует на 25 урона |

Если зверя не подстрелить за 20 секунд, он уходит с карты, а игрок теряет 5 очков.

### Оружие

| Оружие | Магазин | Урон | Точность |
|---|---|---|---|
| Винтовка | 30 | 35 | 0.90 |
| Дробовик | 8 | 60 | 0.60 |
| Пистолет | 50 | 20 | 0.85 |

## Управление

| Клавиша | Действие |
|---|---|
| `W A S D` / стрелки | движение |
| ЛКМ | выстрел |
| `R` | перезарядка |
| колесо мыши | смена оружия |
| `Esc` | меню паузы (сохранить / загрузить / главное меню) |

## Как устроено

Архитектура MVC:

- `model` — состояние игры: карта, сущности, статистика, события, сохранения
- `controller` — `GameController`, связывает интерфейс с моделью
- `view` — экраны и отрисовка на `Canvas`

Многопоточность:

- каждый зверь — отдельная задача `AnimalSimulationTask` в `CachedThreadPool`
- появление зверей (раз в 5 с) и бонусов (раз в 15 с) — через `ScheduledExecutorService`
- звери не меняют состояние игры напрямую, а кладут события в `BlockingQueue`, которую разбирает отдельный поток
- общие данные лежат в `CopyOnWriteArrayList`, `AtomicInteger` и `AtomicBoolean`
- интерфейс обновляется через `AnimationTimer` и `Platform.runLater`

Сохранение делается сериализацией `GameSaveData` в файл `saves/manual_save.sav`.
Логирование через `java.util.logging`.

## Структура

```
src/main/java
├── com/prutzkow/projectlogger    настройка логгера
└── ru/rsreu/Prilepskij0215
    ├── controller                GameController
    ├── model
    │   ├── core                  GameModel, классы сохранения
    │   ├── entities              Hunter, Animal, Weapon, Bonus и их типы
    │   ├── events                события и симуляция зверей
    │   └── map                   GameMap
    └── view                      экраны, Canvas, панели
src/main/resources/images         иконки оружия
```

## Запуск

Нужны JDK 22+ и [JavaFX SDK](https://gluonhq.com/products/javafx/) 25.
Сохранения пишутся в папку `saves/` рядом с проектом, её нужно создать перед первым запуском.

### IntelliJ IDEA

1. Открыть проект.
2. File → Project Structure → Libraries → добавить папку `lib` из JavaFX SDK.
3. Запустить `ru.rsreu.Prilepskij0215.Runner`.

### Из консоли (Windows, PowerShell)

```powershell
$FX = "C:\path\to\javafx-sdk-25\lib"
mkdir saves -Force
javac --module-path $FX -d out (Get-ChildItem -Recurse src\main\java -Filter *.java).FullName
Copy-Item -Recurse src\main\resources\* out\
java --module-path "$FX;out" -m HuntingSeason/ru.rsreu.Prilepskij0215.Runner
```

### Из консоли (Linux, macOS)

```bash
FX=/path/to/javafx-sdk-25/lib
mkdir -p saves
javac --module-path "$FX" -d out $(find src/main/java -name "*.java")
cp -r src/main/resources/* out/
java --module-path "$FX:out" -m HuntingSeason/ru.rsreu.Prilepskij0215.Runner
```
