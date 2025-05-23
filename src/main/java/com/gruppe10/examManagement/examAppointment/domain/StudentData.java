package com.gruppe10.examManagement.examAppointment.domain;

/**
 * Hilfklasse für den PrüflingsImport in die Daten aus der DAtenbank in diese Entität umgewandelt werden
 * **/


public class StudentData {
    private String nachname;
    private String vorname;
    private String matrikelnummer;

    public StudentData(String nachname, String vorname, String matrikelnummer) {
        this.nachname = nachname;
        this.vorname = vorname;
        this.matrikelnummer = matrikelnummer;
    }

    // Getter und Setter

    public String getNachname() {
        return nachname;
    }

    public void setNachname(String nachname) {
        this.nachname = nachname;
    }

    public String getVorname() {
        return vorname;
    }

    public void setVorname(String vorname) {
        this.vorname = vorname;
    }

    public String getMatrikelnummer() {
        return matrikelnummer;
    }

    public void setMatrikelnummer(String matrikelnummer) {
        this.matrikelnummer = matrikelnummer;
    }
}