# Gardening-Products-Webshop
Kovács Alex - AUTP2E - elekssz#8884

## Egy kis leírás

### 1. Fordítási hiba nincs
&nbsp;&nbsp;&nbsp;&nbsp;Nem tapasztaltam.

### 2. Futtatási hiba nincs
&nbsp;&nbsp;&nbsp;&nbsp;Nem tapasztaltam.

### 3. Firebase autentikáció meg van valósítva: Be lehet jelentkezni és regisztrálni
&nbsp;&nbsp;&nbsp;&nbsp;Regisztráció, bejelentkezés működik.

### 4. Adatmodell definiálása (class vagy interfész formájában)
&nbsp;&nbsp;&nbsp;&nbsp;ShoppingItem így lett létrehozva.

### 5. Legalább 3 különböző activity használata 
&nbsp;&nbsp;&nbsp;&nbsp;CartActivity/MainActivity/ShoppingActivity/RegistrationActivity

### 7. Beviteli mezők beviteli típusa megfelelő (jelszó kicsillagozva, email-nél megfelelő billentyűzet jelenik meg stb.)
&nbsp;&nbsp;&nbsp;&nbsp;MainActivity/ShoppingActivity-ben vannak megvalósítva.

### 8. ConstraintLayout és még egy másik layout típus használata
&nbsp;&nbsp;&nbsp;&nbsp;activity_main.xml/activity_registration.xml/list_item.xml

### 9. Reszponzív
&nbsp;&nbsp;&nbsp;&nbsp;activity_main.xml/activity_registration.xml különböző kijelzőre meg lett valósítva. ShoppingActivity is működik.

### 10. Legalább 2 különböző animáció használata
&nbsp;&nbsp;&nbsp;&nbsp;back_in_row.xml/side_in_row.xml

### 11. Intentek használata: navigáció meg van valósítva az activityk között (minden activity elérhető)
&nbsp;&nbsp;&nbsp;&nbsp;Mindegyik activity elérhető, intentek segítségével.

### 12. Legalább egy Lifecycle Hook használata a teljes projektben
&nbsp;&nbsp;&nbsp;&nbsp;MainActivity -> onPause például.

### 13. Legalább egy olyan androidos erőforrás használata,amihez kell android permission
&nbsp;&nbsp;&nbsp;&nbsp;AndroidManifest.xml -> Internethez való hozzáférés.

### 14. Legalább egy notification vagy alam manager vagy job scheduler használata 
&nbsp;&nbsp;&nbsp;&nbsp;Belépés után kapunk egy vásárlásra "ösztönző" értesítést, minden kosárba tett tárgy után értesítést kapunk róla.

### 15. CRUD műveletek mindegyike megvalósult és műveletek service-(ek)be vannak kiszervezve (AsyncTasks)
&nbsp;&nbsp;&nbsp;&nbsp;Create -> új felhasználó létrehozása.  
&nbsp;&nbsp;&nbsp;&nbsp;Read -> termékek betöltése.  
&nbsp;&nbsp;&nbsp;&nbsp;Update -> cartedCount növelése.  
&nbsp;&nbsp;&nbsp;&nbsp;Delete -> termék törlése a kosárból.  
&nbsp;&nbsp;&nbsp;&nbsp;Nincs AsyncTask-ba kiszervezve.

### 16.Legalább 2 komplex Firestore lekérdezés megvalósítása, amely indexet igényel (ide tartoznak: where feltétel, rendezés, léptetés, limitálás)
&nbsp;&nbsp;&nbsp;&nbsp;Nincs
