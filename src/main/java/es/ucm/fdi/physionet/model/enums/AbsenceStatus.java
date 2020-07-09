package es.ucm.fdi.physionet.model.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum AbsenceStatus {
    PROPOSAL("Propuesta"),
    ACCEPTED("Aceptada"),
    DENIED("Denegada");

    private String translatedValue;

    AbsenceStatus(String translatedValue) {
        this.translatedValue = translatedValue;
    }

    public String getTranslatedValue() {
        return translatedValue;
    }

    public static List<String> getTranslatedValues() {
        return Arrays.stream(AbsenceStatus.values())
                .map(AbsenceStatus::getTranslatedValue)
                .collect(Collectors.toList());
    }

    public static AbsenceStatus fromTranslatedValue(String translatedValue) {
        return Arrays.stream(AbsenceStatus.values())
                .filter(reason -> reason.getTranslatedValue().equals(translatedValue))
                .collect(Collectors.toList())
                .get(0);
    }
}