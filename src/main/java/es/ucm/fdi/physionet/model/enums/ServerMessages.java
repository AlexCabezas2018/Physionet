package es.ucm.fdi.physionet.model.enums;

public enum ServerMessages {
    ABSENCE_TO_LONG("absence.to.long"),
    APPOINTMENTS_IN_ABSENCE("appointment.in.absence"),
    ABSENCE_ADDED_SUCCESS("absence.added.success"),
    ABSENCE_IS_NOT_FROM_USER("absence.is.not.from.user"),
    ABSENCE_DELETED_SUCCESS("absence.deleted.success"),
    APPOINTMENT_ADDED_SUCCESS("appointment.added.success"),
    APPOINTMENT_DELETED_SUCCESS("appointment.deleted.success"),
    APPOINTMENT_DELETED_ERROR("appointment.deleted.error"),
    APPOINTMENT_BETWEEN_ABSENCE("appointment.between.absence"),
    APPOINTMENT_FINALIZED_SUCCESS("appointment.finalized.success"),
    APPOINTMENT_FINALIZED_ERROR("appointment.finalized.error");

    private final String propertyName;

    ServerMessages(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getPropertyName(){
        return propertyName;
    }
}
