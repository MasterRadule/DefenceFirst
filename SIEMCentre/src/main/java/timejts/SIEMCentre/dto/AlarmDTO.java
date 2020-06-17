package timejts.SIEMCentre.dto;

import timejts.SIEMCentre.model.Alarm;
import timejts.SIEMCentre.model.AlarmType;

public class AlarmDTO {

    private Alarm alarm;

    private AlarmType alarmType;

    public AlarmDTO() {}

    public AlarmDTO(Alarm alarm, AlarmType alarmType) {
        this.alarm = alarm;
        this.alarmType = alarmType;
    }

    public Alarm getAlarm() {
        return alarm;
    }

    public void setAlarm(Alarm alarm) {
        this.alarm = alarm;
    }

    public AlarmType getAlarmType() {
        return alarmType;
    }

    public void setAlarmType(AlarmType alarmType) {
        this.alarmType = alarmType;
    }
}
