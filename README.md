# CaminoDeSantiago

**CaminoDeSantiago** — это Android-приложение на Kotlin (Jetpack Compose + OpenStreetMap), которое отображает путь Святого Иакова (Camino de Santiago).  
Приложение показывает монастыри и маршруты, проходящие через них. При нажатии на точку открывается карточка с фотографией и описанием.

---

## Функциональность

- Отображение карты с помощью **osmdroid** (данные OpenStreetMap).
- Маркеры на карте — монастыри, загруженные из `assets/monasteries.json`.
- Поддержка фотографий монастырей из папки `res/drawable`.
- Описание монастыря отображается вместе с картинкой в выезжающем нижнем окне (Bottom Sheet).
- Несколько маршрутов (пути), отображаемые разными цветами (загружаются из `assets/routes.json`).
- Автоматическое центрирование карты на выбранном монастыре при клике.

---

## Структура проекта

- `app/src/main/java/com/example/caminodesantiago/`
  - `MainActivity.kt` — точка входа приложения.
  - `MapScreen.kt` — главный экран с картой, маркерами и маршрутами.
  - `Repository.kt` — загрузка данных из JSON-файлов.
  - `Monastery.kt`, `Route.kt` — модели данных.
- `app/src/main/assets/`
  - `monasteries.json` — список монастырей (id, название, координаты, описание, имя картинки).
  - `routes.json` — описание маршрутов (цвет линии и id точек).
- `app/src/main/res/drawable/`
  - картинки монастырей (например, `monastery1.jpg`, `monastery2.png` и т.д.).

---

## Как добавить данные

1. Добавьте информацию о монастыре в `assets/monasteries.json`, например:
   ```json
   {
     "id": "s1",
     "name": "Monasterio de San Juan",
     "lat": 42.35,
     "lon": -3.7,
     "description": "Старинный монастырь XI века",
     "imageResName": "monastery1"
   }
