package model;

import java.time.LocalDateTime;

public class SubTask extends Task {
    private int epicId;

    public SubTask() {
        super();
    }

    public SubTask(String name, String description, int epicId) {
        super(name, description);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return this.epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public LocalDateTime getEndTime() {
        if(getStartTime() == null || getDuration() == null){
            return null;
        }
        return getStartTime().plus(getDuration());
    }

    @Override
    public String toString() {
        return "SubTask{id=" + getId() +
                ", name='" + getName() + "'" +
                ", description='" + getDescription() + "'" +
                ", status=" + getStatus() +
                ", startTime=" + getStartTime() +
                ", duration=" + getDuration() +
                ", endTime=" + getEndTime() +
                ", epicId=" + epicId + "}";
    }
}
