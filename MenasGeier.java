import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;


/**
 * Implementation einer Strategie für das Spiel "Hol's der Geier".
 * Diese Klasse erweitert HolsDerGeierSpieler und implementiert eine
 * Spielstrategie basierend auf verschiedenen Spielsituationen.
 *
 * !Ein detailliertes ASCII-Flussdiagramm der Implementierung befindet sich am Ende dieser Datei.
 * 
 * Spielstrategie:
 * Die Klasse MenasGeier implementiert eine Strategie für das Spiel "Hol's der Geier".
 * Der Bot versucht, Geierkarten zu vermeiden und Mauskarten zu gewinnen, indem er
 * die Spielsituation (eigene Karten, Gegnerkarten, aufgedeckte Karte, letzteZüge) analysiert.
 *
 * Bei Mauskarten >= 8 spielt der Bot seine höchste Karte, wenn der Gegner noch höhere
 * Karten hat, ansonsten die niedrigste Karte innerhalb einer dynamischen Grenze, die zum Gewinnen ausreicht.
 * Bei Mauskarten >= 5 und < 8 spielt er zufällig eine hohe Karte (9-12), wenn der
 * Gegner wahrscheinlich noch höhere Karten hat, sonst die niedrigste Karte innerhalb einer Grenze.
 * Bei Mauskarten < 5 spielt er eine mittlere Karte (5-8, sonst absteigend 4, 3, 2, 1), wenn der
 * Gegner wahrscheinlich noch höhere Karten hat, sonst die niedrigste Karte innerhalb einer Grenze.
 *
 * Bei Geierkarten (-5 bis -1) wird immer die Methode spieleMittlereKarte aufgerufen. Außer bei
 * einem Unentschieden ( < 5), dann wird eine hohe Karte (spieleHoheKarte) gespielt
 *
 * Die Methode `hatGegnerPotenziellHoehereKarte` prüft, ob der Gegner wahrscheinlich
 * eine höhere Karte als die aufgedeckte Karte spielen wird, basierend auf einer oberen
 * Grenze, die sich dynamisch an die aufgedeckte Karte anpasst.
 *
 * Die Methode `spieleNiedrigsteKarteDieGewinnt` verwendet ein dynamisches Grenzsystem:
 * - Für niedrige Mauskarten (< 5): Maximal naechsteKarte + 3
 * - Für mittlere Mauskarten (5-7): Maximal naechsteKarte + 5
 * - Für hohe Mauskarten (>= 8): Keine Grenze
 * Dies verhindert, dass zu hohe Karten für niedrigwertige Mauskarten verschwendet werden.
 *
 * Außerdem wird ein Unentschieden berücksichtigt. Dafür speichert der Bot die Punkte im Pot und addiert
 * sie zur nächsten aufgedeckten Karte. Außerdem merkt er sich die zuletzt gespielte
 * Karte des Gegners und die eigene zuletzt gespielte Karte, um das Unentschieden zu erkennen. 
 * Dadurch wird die zu gewinnende Punktzahl richtig berechnet.
 *
 *
 * HINWEIS: Mit KI generierte Zeilen wurden per Kommentar gekenzeichnet.
 *
 * @see HolsDerGeierSpieler
 * @author Marcel Mena Mekhaiel (Github: MMM1706) 
 * @version 2025-01-15
 */
public class MenasGeier extends HolsDerGeierSpieler {
	
	// Variablen für die Behandlung von Unentschieden
	private int letzterGegnerZug = -99;     // Initialisierung mit Standardwert (-99 bedeutet: noch keine Karte gespielt)
	private int meineLetzteKarte = 0;
	private int letzteNaechsteKarte = 0;   //war: MeineLetzteKarte, laut  Java-Konvention sollen Variablen aber mit Kleinbuchstaben beginnen
	private int punkteImPot = 0;
    
	//Kartenverwaltung
    private ArrayList<Integer> meineKarten = new ArrayList<>();
    private ArrayList<Integer> gegnerKarten = new ArrayList<>();

  
    /**
     * Setzt den Spielzustand zurück und initialisiert die Kartenlisten.
     * Wird zu Beginn jedes neuen Spiels aufgerufen.
     *
     * @throws IllegalStateException wenn die Karteninitialisierung fehlschlägt
     * @see HolsDerGeierSpieler#reset()
     * 
     * HINWEIS: Das Überprüfen der korrekten Initialisierung ist nicht zwingend notwendig,
     * habe ich aber hinzugefügt, da wir Exceptions in den Vorlseungen behandelt haben.
     */
    @Override
    public void reset() {
        // Liste der eigenen Karten leeren und mit den Zahlen 1 bis 15 füllen.
        meineKarten.clear();
        for (int i = 1; i <= 15; i++) {
            meineKarten.add(i);
        }

        // Liste der gegnerischen Karten leeren und mit den Zahlen 1 bis 15 füllen.
        gegnerKarten.clear();
        for (int i = 1; i <= 15; i++) {
            gegnerKarten.add(i);
        }

        // Zurücksetzen der Unentschieden-Variablen
        punkteImPot = 0;
        letzteNaechsteKarte = 0;
        meineLetzteKarte = 0;
        
        // Überprüfung der korrekten Initialisierung
        if (meineKarten.size() != 15) {
            throw new IllegalStateException("Fehler bei der Initialisierung: meineKarten enthält " 
                + meineKarten.size() + " statt 15 Karten");
        }
        if (gegnerKarten.size() != 15) {
            throw new IllegalStateException("Fehler bei der Initialisierung: gegnerKarten enthält " 
                + gegnerKarten.size() + " statt 15 Karten");
        }
    }
    /**
     * Bestimmt die nächste zu spielende Karte basierend auf der aktuellen Spielsituation.
     *
     * @param naechsteKarte Die aufgedeckte Karte nach der aktuellen Strategie
     * @return Die zu spielende Karte
     */
   
    @Override
    public int gibKarte(int naechsteKarte) {
    	// Ermitteln des letzten Gegner-Zugs
        letzterGegnerZug = getHdg().letzterZug(getNummer() == 1 ? 0 : 1);
    	
     // Unentschieden-Behandlung
        if (letzterGegnerZug == meineLetzteKarte) {
            // Wenn beide Spieler die gleiche Karte gespielt haben,
            // werden die Punkte für die nächste Runde aufgehoben
            punkteImPot += letzteNaechsteKarte;
        }
    	
        // Sortiere der eigenen Karten in aufsteigender Reihenfolge
        Collections.sort(meineKarten);   //KI (ChatGPT) -- Wie sortiert man die ArrayList?
        
        // Überprüfe, ob es einen Pot gibt und addiere ihn zur nächsten Karte
        if (punkteImPot != 0) {
            naechsteKarte += punkteImPot;
            punkteImPot = 0; // Pot leeren, da die Punkte jetzt berücksichtigt werden
        }

        // Strategieauswahl und Kartenspiel
        int karte;
        if (naechsteKarte > 0) {
            // Mauskarte:
            karte = spieleMauskarte(naechsteKarte);
        } else {
            // Geierkarte:
            karte = spieleGeierkarte(naechsteKarte);
        }
        //Alternative Schreibweise (Kurzform aus der Probeklausur S. 12):
        //int karte = (naechsteKarte > 0) ? spieleMauskarte(naechsteKarte) : spieleGeierkarte(naechsteKarte);
        
        // Aktualisierung der Gegner-Kartenliste (-99 wenn noch keine Karte gespielt wurde)
        if (letzterGegnerZug != -99) {
            gegnerKarten.remove(Integer.valueOf(letzterGegnerZug));  
        }
       

        // Speichern des aktuellen Zugs für die nächste Runde
        meineLetzteKarte = karte;     //Es wird die von mir zuletzt gelegte Karte gespeichert (für Unentschieden)
        letzteNaechsteKarte = naechsteKarte;   //Es wird die letzte Tischkarte gespeichert (für Unentschieden)
        return karte;
    }

    /**
     * Wählt die optimale Karte für eine Maus-Situation.
     *
     * @param naechsteKarte Wert der Mauskarte
     * @return Gewählte Karte
     */
    private int spieleMauskarte(int naechsteKarte) {
        // Strategie für sehr hohe Mauskarte anpassen
        if (naechsteKarte >= 8) {
            // Sehr hohe Mauskarte:
            if (hatGegnerHoehereKartenAlsIch()) {
                // Gegner hat noch höhere Karten.
                // Spiele die höchste Karte
                return spieleHoechsteKarte();
            } else {
                // Gegner hat keine höheren Karten mehr.
                // Spiele die niedrigste Karte, die zum Gewinnen ausreicht.
                return spieleNiedrigsteKarteDieGewinnt(naechsteKarte);
            }
        } else if (naechsteKarte >= 5) {
            // Hohe Mauskarte:
            if (hatGegnerPotenziellHoehereKarte(naechsteKarte)) {
                // Gegner hat wahrscheinlich noch hohe Karten.
                // Spiele eine hohe Karte (9-12) zufällig.
                return spieleHoheKarte();
            } else {
                // Gegner hat wahrscheinlich keine höheren Karten.
                // Spiele die niedrigste Karte, die zum Gewinnen ausreicht.
                return spieleNiedrigsteKarteDieGewinnt(naechsteKarte);
            }
        } else {
        	// Strategie für niedrige Mauskarten
            if (hatGegnerPotenziellHoehereKarte(naechsteKarte)) {
                // Gegner hat wahrscheinlich noch höhere Karten.
                // Spiele eine mittlere Karte (5-8), sonst 4,3,2,1 
                return spieleMittlereKarte();
            } else { 
                // Gegner hat wahrscheinlich keine höheren Karten.
                // Spiele die niedrigste Karte, die zum Gewinnen ausreicht.
                return spieleNiedrigsteKarteDieGewinnt(naechsteKarte);
            }
        }
    }

    /**
     * Hilfsmethode, um die Logik für das Spielen einer Geierkarte zu implementieren.
     * Die Implementierung verwendet einen switch-Block statt if-else-Ketten für bessere:
     * - Lesbarkeit: Der Code ist kompakter und übersichtlicher
     * - Performance: Switch-Cases werden vom Compiler in eine Jump-Table übersetzt,
     *   was bei mehreren Fällen effizienter ist als if-else-Ketten
     * - Wartbarkeit: Neue Fälle können einfach hinzugefügt werden
     * - Fehleranfälligkeit: Weniger Verschachtelung bedeutet weniger potenzielle Fehler
     * 
     * Da alle Geierkarten (-5 bis -1) zum gleichen Verhalten führen (spieleMittlereKarte),
     * können alle Fälle in einem einzigen case zusammengefasst werden.
     * Ausnahme ist die Bedingung < -5 (Ausnahme beim Unentschieden)
     *
     * @param naechsteKarte Der Wert der Geierkarte
     * @return Die Nummer der Karte, die der Bot spielen möchte.
     */
    private int spieleGeierkarte(int naechsteKarte) {
        // Alte Implementierung mit if-else (als Referenz):
        /*
        int geierWert;
        if (naechsteKarte < -5) {
        	geierWert = -5;    //es wird also die Methode "spieleHoheKarte" aufgerufen
        } else if (naechsteKarte == -5) {
            geierWert = 4;     //es wird also die Methode "spieleMittlereKarte" aufgerufen"
        } else if (naechsteKarte == -4) {
            geierWert = 4;     //es wird also die Methode "spieleMittlereKarte" aufgerufen"
        } else if (naechsteKarte == -3) {
            geierWert = 3;     //es wird also die Methode "spieleMittlereKarte" aufgerufen"
        } else if (naechsteKarte == -2) {
            geierWert = 2;     //es wird also die Methode "spieleMittlereKarte" aufgerufen"
        } else {
            geierWert = 1; // für -1  --> //es wird also die Methode "spieleMittlereKarte" aufgerufen
        }
        */

        
        // Überprüfen, ob die Karte kleiner als -5 ist (Sonderfall beim Unentschieden
    	/* HINWEIS: Man könnte meinen, dass es sinnvoll wäre, auch einen Wert festzulegen,
    	   bei dem eine sehr hohe Karte gespielt wird (z.B naechsteKarte < -8).
    	   Intensives Testen gegen verschiedene Strategien hat aber gezeigt, dass die 
    	   Gewinnchancen dadurch minimiert werden. */
    	
    	if (naechsteKarte < -5) {
    	    return spieleMauskarte(5); // Behandelt den Sonderfall (Unentschieden) wie eine Mauskarte 5
    	}

    /* Neue, vereinfachte Implementierung mit switch-Expression:
    Alle Geierkarten werden gleich behandelt - da alle die Methode 
    spieleMittlereKarte aufruft, in der eine Karte aus einem Zahlenbereich zufällig 
    gelegt wird (außer bei <5, was nur bei einem Unentschieden geschieht) */
    int geierWert = switch (naechsteKarte) {
        case -5, -4, -3, -2, -1 -> 2;  // Alle diese Fälle führen zu einer 2
        default -> 1;  // Standardfall
    };

    return spieleMauskarte(geierWert);
	
    }    
     
    
    /**
     * Hilfsmethode, um zu prüfen, ob der Gegner noch höhere Karten als die
     * eigene höchste Karte hat.
     *
     * @return True, wenn der Gegner noch höhere Karten hat, sonst false.
     */
    private boolean hatGegnerHoehereKartenAlsIch() {
        if (meineKarten.isEmpty()) {
            return false; // Keine Karten mehr im Besitz
        }
        int meineHoechsteKarte = meineKarten.get(meineKarten.size() - 1);
        for (int gegnerKarte : gegnerKarten) {
            if (gegnerKarte > meineHoechsteKarte) { 
                return true;
            }
        }
        return false;
    }

    /**
     * Hilfsmethode, um zu prüfen, ob der Gegner wahrscheinlich eine höhere Karte als naechsteKarte spielen wird.
     *
     * @param naechsteKarte Der Wert der aufgedeckten Mauskarte.
     * @return True, wenn der Gegner wahrscheinlich eine höhere Karte spielen wird, sonst false.
     */
    private boolean hatGegnerPotenziellHoehereKarte(int naechsteKarte) {
        // Die obere Grenze definiert den Bereich der Karten, die geprüft werden.
        // Dadurch wird vermieden, dass die Methode immer true zurückgibt, z. B. wenn die Tischkarte sehr niedrig ist.
        // Beispiel: Bei einer Tischkarte von 1 wird geprüft, ob der Gegner Karten wie 2, 3 oder 4 hat.
    	// Hinweis: Die Grenzen wurden von mir bestimmt und durch Testspiele angepasst
        int obereGrenze;
        if (naechsteKarte < 5) {
            obereGrenze = naechsteKarte + 3;
        } else if (naechsteKarte < 8) {
            obereGrenze = naechsteKarte + 5;
        } else {
            obereGrenze = 55; // (10+9+8+7+6+5+4+3+2+1) = 55 = Maximale Kartenwert in diesem Spiel
        }

        // Wichtiger Hinweis: Hier wird bewusst nicht mit der eigenen höchsten Karte verglichen.
        // Stattdessen wird geprüft, ob der Gegner Karten im Bereich (naechsteKarte + obereGrenze] hat.
        // Testen hat gezeigt, dass diese Strategie besonders effektiv gegen Bots oder Spieler ist, 
        // die sich an der naechsteKarte orientieren, z. B. indem sie Karten wie naechsteKarte + 3 spielen.
        for (int gegnerKarte : gegnerKarten) {  
            if (gegnerKarte > naechsteKarte && gegnerKarte <= obereGrenze) {
                return true; // Der Gegner hat eine Karte, die höher als naechsteKarte ist und im definierten Bereich liegt.
            }
        }

        return false; // Der Gegner hat keine relevanten höheren Karten im definierten Bereich.
    }


    /**
     * Spielt die höchste Karte
     * @return die zu spielende Karte
     */
    private int spieleHoechsteKarte() {
        
        return meineKarten.remove(meineKarten.size() - 1); // Höchste Karte spielen
    
}

    /**
     * Spielt eine hohe Karte (Zufall von 9-12), aber versucht, die sehr hohen Karten zu sparen, falls möglich.
     * @return die zu spielende Karte
     */
    private int spieleHoheKarte() {
        ArrayList<Integer> hoheKarten = new ArrayList<>();
        if (meineKarten.contains(12)) {
            hoheKarten.add(12);
        }
        if (meineKarten.contains(11)) {
            hoheKarten.add(11);
        }
        if (meineKarten.contains(10)) {
            hoheKarten.add(10);
        }
        if (meineKarten.contains(9)) {
            hoheKarten.add(9);
        }

        if (hoheKarten.isEmpty()) {
            return meineKarten.remove(meineKarten.size() - 1); // Höchste Karte spielen, falls keine hohen Karten vorhanden
        } else {
            // Wähle zufällig eine Karte aus der Liste der hohen Karten
            int randomIndex = new Random().nextInt(hoheKarten.size());
            int zuSpielendeKarte = hoheKarten.get(randomIndex);
            meineKarten.remove(Integer.valueOf(zuSpielendeKarte));
            return zuSpielendeKarte;
        }
    }

    /**
     * Spielt eine mittlere Karte (Zufallsauswahl aus 5-8), falls keine verfügbar 
     * dann absteigend 4,3,2,1, als letzte Option die niedrigste verfügbare Karte.
     *
     * @return die ausgewählte Karte
     */
    private int spieleMittlereKarte() {
        // Liste für mittlere Karten (5 bis 8)
        ArrayList<Integer> mittlereKarten = new ArrayList<>();

        // Prüfe, ob die Handkarten die Werte 5 bis 8 enthalten
        if (meineKarten.contains(5)) {
            mittlereKarten.add(5);
        }
        if (meineKarten.contains(6)) {
            mittlereKarten.add(6);
        }
        if (meineKarten.contains(7)) {
            mittlereKarten.add(7);
        }
        if (meineKarten.contains(8)) {
            mittlereKarten.add(8);
        }

        // Wenn keine mittlere Karte (5 bis 8) vorhanden ist, spiele 4, 3, 2, 1 (der Reihenfolge nach)
        if (mittlereKarten.isEmpty()) {
            // Prüfe, ob die Handkarten die Werte 1 bis 4 enthalten
            if (meineKarten.contains(4)) {
                mittlereKarten.add(4);
            }
            if (meineKarten.contains(3)) {
                mittlereKarten.add(3);
            }
            if (meineKarten.contains(2)) {
                mittlereKarten.add(2);
            }
            if (meineKarten.contains(1)) {
                mittlereKarten.add(1);
            }

            // Suche die niedrigste Karte aus den Handkarten
            int minIndex = 0;
            for (int i = 1; i < meineKarten.size(); i++) {
                if (meineKarten.get(i) < meineKarten.get(minIndex)) {
                    minIndex = i;
                }
            }

            // Entferne die niedrigste Karte und gebe sie zurück
            return meineKarten.remove(minIndex);

        } else {
            // Wenn mittlere Karten vorhanden sind, wähle zufällig eine Karte aus der Liste der mittleren Karten
            int randomIndex = new Random().nextInt(mittlereKarten.size());
            int zuSpielendeKarte = mittlereKarten.get(randomIndex);

            // Entferne die gewählte Karte und gebe sie zurück
            meineKarten.remove(Integer.valueOf(zuSpielendeKarte));
            return zuSpielendeKarte;
        }
    }

    /**
     * Hilfsmethode, um die niedrigste Karte zu spielen, die zum Gewinnen einer
     * Mauskarte ausreicht, unter Berücksichtigung einer dynamischen Obergrenze.
     * Die Obergrenze verhindert, dass zu hohe Karten für niedrigwertige
     * Mauskarten verschwendet werden.
     *
     * Beispiel:
     * naechsteKarte = 3, --> maxEinsetzbareKarte = 6
     * Meine Karten: [3, 13, 15]; Gegner Karten: [1, 8, 9]
     * Ohne Grenze wäre die niedrigste Karte 13; mit Grenze ist es die 3.
     * Man spart somit hohe Karten. Tests gegen verschiedene Strategien haben eine Gewinnsteigerung gezeigt.
     *
     * @param naechsteKarte Der Wert der Mauskarte.
     * @return Die Nummer der Karte, die der Bot spielen möchte.
     */
    private int spieleNiedrigsteKarteDieGewinnt(int naechsteKarte) {
        // Definiere eine maximale Kartengrenze basierend auf dem Wert der Mauskarte
        int maxEinsetzbareKarte;
        if (naechsteKarte < 5) {
            maxEinsetzbareKarte = naechsteKarte + 3;  // Für niedrige Mauskarten (1-4) maximal 4-7
        } else if (naechsteKarte < 8) {
            maxEinsetzbareKarte = naechsteKarte + 5;  // Für mittlere Mauskarten (5-7) maximal 10-12
        } else {
            maxEinsetzbareKarte = 15;  // Für hohe Mauskarten (8+) keine Grenze
        }

        // Finde die höchste Karte des Gegners
        int hoechsteGegnerKarte = -1;
        for (int karte : gegnerKarten) {
            if (karte > hoechsteGegnerKarte) {
                hoechsteGegnerKarte = karte;
            }
        }

        // Finde die niedrigste eigene Karte, die höher ist als die höchste Gegnerkarte
        // UND nicht über der maxEinsetzbareKarte liegt
        int besteKarte = -1;
        for (int karte : meineKarten) {
            if (karte > hoechsteGegnerKarte && karte <= maxEinsetzbareKarte) {
                besteKarte = karte;
                break;
            }
        }

        // Wenn eine passende Karte gefunden wurde, spiele sie
        if (besteKarte > 0) {
            meineKarten.remove(Integer.valueOf(besteKarte));
            return besteKarte;
        }

        // Falls keine passende Karte gefunden wurde, spiele die niedrigste Karte
        int kleinsteKarte = meineKarten.get(0);
        for (int karte : meineKarten) {
            if (karte < kleinsteKarte) {
                kleinsteKarte = karte;
            }
        }
        meineKarten.remove(Integer.valueOf(kleinsteKarte));
        return kleinsteKarte;
    }
}  



/*
 * ASCII-Flussdiagramm der MenasGeier-Implementierung
 * ------------------------------------------------
 * Erstellt von: Marcel Mena Mekhaiel (MMM1706)
 * Datum: 13.01.2025
 * 
 * Legende:
 * │ Vertikale Verbindung
 * ├ Verzweigung (nicht letztes Element)
 * └ Verzweigung (letztes Element)
 * ------------------------------------------------
 * 
 * Start: gibKarte(naechsteKarte)
 * │
 * ├─── Ermittle letzterGegnerZug
 * │
 * ├─── Unentschieden-Behandlung:
 * │    └─── Wenn letzterGegnerZug == meineLetzteKarte:
 * │         └─── punkteImPot += letzteNaechsteKarte
 * │
 * ├─── Sortiere meineKarten (aufsteigend)
 * │
 * ├─── Pot-Behandlung:
 * │    └─── Wenn punkteImPot != 0:
 * │         ├─── naechsteKarte += punkteImPot
 * │         └─── punkteImPot = 0
 * │
 * ├─── Ist naechsteKarte > 0?
 * │    │
 * │    ├─── Ja (Mauskarte):
 * │    │    │
 * │    │    ├─── Wenn naechsteKarte >= 8 (Sehr hohe Mauskarte):
 * │    │    │    ├─── Hat Gegner höhere Karten als ich?
 * │    │    │    │    ├─── Ja: spieleHoechsteKarte()
 * │    │    │    │    └─── Nein: spieleNiedrigsteKarteDieGewinnt(naechsteKarte)
 * │    │    │
 * │    │    ├─── Wenn 5 <= naechsteKarte < 8 (Hohe Mauskarte):
 * │    │    │    ├─── Hat Gegner potenziell höhere Karten?
 * │    │    │    │    ├─── Ja: spieleHoheKarte() [9-12, zufällig]
 * │    │    │    │    └─── Nein: spieleNiedrigsteKarteDieGewinnt(naechsteKarte)
 * │    │    │
 * │    │    └─── Wenn naechsteKarte < 5 (Niedrige Mauskarte):
 * │    │         ├─── Hat Gegner potenziell höhere Karten?
 * │    │         │    ├─── Ja: spieleMittlereKarte() [5-8, sonst 4,3,2,1]
 * │    │         │    └─── Nein: spieleNiedrigsteKarteDieGewinnt(naechsteKarte)
 * │    │
 * │    └─── Nein (Geierkarte):
 * │         ├─── Wenn naechsteKarte < -5 (Unentschieden):
 * │         │    └─── spieleMauskarte(5) [Behandlung wie hohe Mauskarte]
 * │         │
 * │         └─── Sonst (normale Geierkarte, -5 bis -1):
 * │              └─── spieleMauskarte(2) [Behandlung wie niedrige Mauskarte]
 * │
 * └─── Nachbearbeitung:
 *      ├─── Aktualisiere Gegner-Kartenliste (wenn letzterGegnerZug != -99)
 *      ├─── Speichere meineLetzteKarte
 *      └─── Speichere letzteNaechsteKarte
 * 
 * //--------------------------------------
 * // Hilfsmethoden
 * //--------------------------------------
 * 
 * spieleHoechsteKarte():
 * └─── Return und entferne höchste Karte aus meineKarten
 * 
 * spieleHoheKarte():
 * ├─── Sammle verfügbare Karten [12,11,10,9]
 * ├─── Wenn keine vorhanden: Return höchste verfügbare Karte
 * └─── Sonst: Return zufällige Karte aus den gesammelten
 * 
 * spieleMittlereKarte():
 * ├─── Sammle verfügbare Karten [8,7,6,5]
 * ├─── Wenn keine vorhanden:
 * │    ├─── Sammle verfügbare Karten [4,3,2,1]
 * │    └─── Return niedrigste verfügbare Karte
 * └─── Sonst: Return zufällige Karte aus den gesammelten
 * 
 * spieleNiedrigsteKarteDieGewinnt(naechsteKarte):
 * ├─── Berechne maxEinsetzbareKarte:
 * │    ├─── naechsteKarte < 5  -> maxKarte = naechsteKarte + 3
 * │    ├─── naechsteKarte < 8  -> maxKarte = naechsteKarte + 5
 * │    └─── sonst              -> maxKarte = 15
 * │
 * ├─── Bestimme höchste Gegnerkarte
 * ├─── Suche niedrigste eigene Karte die:
 * │    └─── > höchste Gegnerkarte UND <= maxEinsetzbareKarte
 * │
 * ├─── Wenn Karte gefunden: Return diese Karte
 * └─── Sonst: Return niedrigste eigene Karte
 */