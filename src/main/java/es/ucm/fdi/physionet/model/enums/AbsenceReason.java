package es.ucm.fdi.physionet.model.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum AbsenceReason {
    SICKNESS("Enfermedad"),
    PERSONAL_AFFAIRS("Asuntos propios"),
    HOLIDAYS("Vacaciones"),
    CHILD_BIRTH("Nacimiento"),
    DEATH_OF_RELATIVE("Fallecimiento de familiar"),
    MEDICAL_SICK_LEAVE("Urgencia médica"),
    PREGNANCY("Paternindad/Maternidad");

    private String translatedValue;

    AbsenceReason(String translatedValue) {
        this.translatedValue = translatedValue;
    }

    public String getTranslatedValue() {
        return translatedValue;
    }

    public static List<String> getTranslatedValues() {
        return Arrays.stream(AbsenceReason.values())
                .map(AbsenceReason::getTranslatedValue)
                .collect(Collectors.toList());
    }

    public static AbsenceReason fromTranslatedValue(String translatedValue) {
        return Arrays.stream(AbsenceReason.values())
                .filter(reason -> reason.getTranslatedValue().equals(translatedValue))
                .collect(Collectors.toList())
                .get(0);
    }
}

