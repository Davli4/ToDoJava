package model;

import model.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subtaskIds = new ArrayList();
    private LocalDateTime endTime;
    public Epic() {
        super();
    }

    public Epic(String name, String description) {
        super(name, description);
    }

    public ArrayList<Integer> getSubtaskIds() {
        return this.subtaskIds;
    }

    public void setSubtaskIds(ArrayList<Integer> subtaskIds) {
        this.subtaskIds = subtaskIds;
    }

    public void addSubtaskId(int subtaskId) {
        this.subtaskIds.add(subtaskId);
    }

    public void removeSubtaskId(int subtaskId) {
        this.subtaskIds.remove((Integer) subtaskId);
    }

    public void setEndTime(LocalDateTime endTime) {this.endTime = endTime;}

    @Override
    public LocalDateTime getEndTime() {return this.endTime;}

    public void calculateEndTime(LocalDateTime subTaskStartTime, Duration subTaskDuration, LocalDateTime subtaskEndTime) {
        if(getStartTime() == null ||  subTaskStartTime.isBefore(getStartTime())) {
            setStartTime(subTaskStartTime);
        }

        if(getDuration() == null){
            setDuration(Duration.ZERO);
        }

        setDuration(getDuration().plus(subTaskDuration));

        if(endTime == null || subtaskEndTime.isAfter(endTime)) {
            endTime = subtaskEndTime;
        }
    }

    public void clearTime(){
        setStartTime(null);
        setDuration(Duration.ZERO);
        endTime = null;
    }

    @Override
    public String toString() {
        return "Epic{id=" + getId() +
                ", name='" + getName() + "'" +
                ", description='" + getDescription() + "'" +
                ", status=" + getStatus() +
                ", startTime=" + getStartTime() +
                ", duration=" + getDuration() +
                ", endTime=" + getEndTime() +
                ", subtaskIds=" + subtaskIds + "}";
    }

}
