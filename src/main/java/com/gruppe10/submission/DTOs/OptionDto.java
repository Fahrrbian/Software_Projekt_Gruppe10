package com.gruppe10.submission.DTOs;



/**
 * InstructorExamExportController.java
 * <p>
 * Created by Fabian Holtapel on 31.05.2025.
 * <p>
 * Description:
 * Fü+r jede ChoiceOPtion wird ein DTO erzeugt, das aus einer ChoiceOption‐Entity nur die ID und den Text herausfiltert.
 * Das ist nicht zwingend notwendig, so kann gewährleistet werden, dass eindeutige ids verwendet werden
 * pro option können meta daten pro option wie bild url oder score modifier hinzugefügt werden
 * verwirrung durch identische texte unterbunden werden kann
 * So kann man sicherstellen, dass keine text-duplikate auftauchen und keine Sonderzeichen fehler enstehen
 * */

public record OptionDto(String optionId, String text) {}
