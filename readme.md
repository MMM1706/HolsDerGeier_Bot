HINWEIS: Dieses Readme.md wurde von einer KI verbessert, d.h. ich habe sie selbst geschrieben, die KI hat aber Punkte ergänzt, welche ich vergessen hatte. 


# Hol's der Geier Bot - "MenasGeier"
**Dokumentation und technische Spezifikation**

**Version:** 2025-01-17  
**Zeitstempel:** 22:06:01 UTC  
**Autor:** Marcel Mena Mekhaiel (MMM1706)

## Inhaltsverzeichnis
1. [Spielbeschreibung und Regeln](#spielbeschreibung-und-regeln)
2. [Bot-Strategie](#bot-strategie)
3. [Technische Implementation](#technische-implementation)
4. [Installation und Verwendung](#installation-und-verwendung)
5. [Dokumentation der Methoden](#dokumentation-der-methoden)

## Spielbeschreibung und Regeln

### Hol's der Geier - Offizielle Spielregeln

"Hol's der Geier" ist ein strategisches Kartenspiel für 2-5 Spieler. Ziel des Spiels ist es, durch geschicktes Ausspielen der eigenen Karten die meisten Punkte zu sammeln.

#### Spielmaterial
* **Spielkarten:** Jeder Spieler erhält einen identischen Satz mit 15 Karten (Werte 1-15)
* **Tierkarten:** Ein gemeinsamer Stapel bestehend aus:
    * 10 Mauskarten (positive Werte: +1 bis +10)
    * 5 Geierkarten (negative Werte: -1 bis -5)

#### Spielablauf pro Runde
1. Eine Tierkarte wird vom Stapel aufgedeckt
2. Alle Spieler wählen gleichzeitig und verdeckt eine ihrer Spielkarten
3. Die gewählten Karten werden gleichzeitig aufgedeckt
4. Punktevergabe:
    * **Bei Mauskarte:** Spieler mit höchster Karte gewinnt die Punkte
    * **Bei Geierkarte:** Spieler mit niedrigster Karte erhält die Minuspunkte
5. Bei Gleichstand:
    * Keine Punktevergabe
    * Tierkarte bleibt liegen
    * Punkte werden zum nächsten Durchgang addiert ("Pot")
6. Gespielte Karten scheiden aus
7. Nächste Runde beginnt
8. Das Spiel endet, wenn es keine Tierkarten mehr gibt (also nach 15 Runden)
9. Der Gewinner ist der mit der höchsten Gesamtpunktzahl.



## Bot-Strategie

### Kartenauswahl-Logik

#### Mauskarten (> 0)
1. **Sehr hohe Mauskarte (≥ 8)**
   * Mit höheren Gegnerkarten → Höchste eigene Karte
   * Ohne höhere Gegnerkarten → Niedrigste geeignete Karte

2. **Hohe Mauskarte (5-7)**
   * Bei Bedrohung → Zufällige hohe Karte (9-12)
   * Ohne Bedrohung → Niedrigste geeignete Karte

3. **Niedrige Mauskarte (< 5)**
   * Bei Bedrohung → Mittlere Karte (5-8, alternativ 4-1)
   * Ohne Bedrohung → Niedrigste geeignete Karte

#### Geierkarten (< 0)
* **Standard (-5 bis -1):** Behandlung wie niedrige Mauskarte (2)
* **Unentschieden (< -5):** Behandlung wie hohe Mauskarte (5)

### Strategische Besonderheiten

#### Dynamisches Grenzsystem
* Niedrige Mauskarten: maxKarte = naechsteKarte + 3
* Mittlere Mauskarten: maxKarte = naechsteKarte + 5
* Hohe Mauskarten: maxKarte = 15

#### Unentschieden-Behandlung
* Punktespeicherung im Pot
* Addition zur nächsten Karte
* Tracking der letzten Züge

## Technische Implementation

### Hauptkomponenten

#### Variablen
```java
private int letzterGegnerZug = -99;
private int meineLetzteKarte = 0;
private int letzteNaechsteKarte = 0;
private int punkteImPot = 0;
private ArrayList<Integer> meineKarten;
private ArrayList<Integer> gegnerKarten;
```

#### Kernmethoden
1. `reset()`: Spielinitialisierung
2. `gibKarte()`: Hauptentscheidungslogik
3. `spieleMauskarte()`: Mauskarten-Strategien
4. `spieleGeierkarte()`: Geierkarten-Strategien

### Hilfsmethoden
* `spieleHoechsteKarte()`
* `spieleHoheKarte()`
* `spieleMittlereKarte()`
* `spieleNiedrigsteKarteDieGewinnt()`
* `hatGegnerHoehereKartenAlsIch()`
* `hatGegnerPotenziellHoehereKarte()`

## Installation und Verwendung

### Installation
```bash
# 1. Repository klonen oder MenasGeier.java herunterladen
# 2. Kompilieren
javac MenasGeier.java
```

### Verwendung
```java
// Bot erstellen und initialisieren
HolsDerGeierSpieler bot = new MenasGeier();
bot.reset();

// Spielzug durchführen
int karte = bot.gibKarte(naechsteKarte);
```

## Dokumentation der Methoden

### gibKarte(int naechsteKarte)
Hauptmethode für die Kartenauswahl.
```java
@param naechsteKarte Die aufgedeckte Punktekarte
@return Die zu spielende Zahlenkarte
```

### spieleMauskarte(int naechsteKarte)
Strategie für positive Punktekarten.
```java
@param naechsteKarte Wert der Mauskarte
@return Gewählte Zahlenkarte
```

### spieleGeierkarte(int naechsteKarte)
Strategie für negative Punktekarten.
```java
@param naechsteKarte Wert der Geierkarte
@return Gewählte Zahlenkarte
```

### reset()
Initialisiert einen neuen Spielzustand.
```java
@throws IllegalStateException bei Initialisierungsfehlern
```

---
**Letzte Aktualisierung:** 2025-01-17 22:06:01 UTC  
**Dokumentation erstellt von:** MMM1706

*Eine detaillierte Implementierung mit vollständigem ASCII-Flussdiagramm finden Sie in der Datei `MenasGeier.java`.*
