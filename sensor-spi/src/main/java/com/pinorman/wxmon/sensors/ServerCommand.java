package com.pinorman.wxmon.sensors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Created by Paul on 1/25/2015.
 * This Class is used to build a command packet which is sent to the PI.
 * From this command class, the PI will know what data is desired to be returned to the caller (client)
 */
public class ServerCommand implements Serializable {
    public enum CmdType { TEMPERATURE, RAIN }
    public enum DateRange { LASTHOUR, LASTDAY, LASTWEEK, LASTMONTH, DATERANGE }

    private CmdType command = CmdType.TEMPERATURE;

    //* below describes the data beyond its type (e.g., temperature's ID could be -> "inside" or "outside"
    private String sensorId;

    private DateRange quickDateEnum = DateRange.LASTHOUR;
    private LocalDateTime begDate, endDate;

    public ServerCommand() {

    }

    public CmdType getCommand() {
        return command;
    }

    public void setCommand(CmdType command) {
        this.command = command;
    }

    public String getSensorId() {
        return sensorId;
    }

    public void SetSensorId(String commandType) {
        this.sensorId = commandType;
    }

    public DateRange getQuickDateEnum() {
        return quickDateEnum;
    }

    public void setQuickDateEnum(DateRange quickDateEnum) {
        endDate = LocalDateTime.now();

        this.quickDateEnum = quickDateEnum;
    }


    public void setDates(LocalDateTime begDate, LocalDateTime endDate) {
        this.begDate = begDate;
        this.endDate = endDate;
    }

    public LocalDateTime getBegDate() {
        return begDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }
}
