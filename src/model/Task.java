package model;

import model.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    private int id;
    private String name;
    private String description;
    private TaskStatus status;
    private Duration  duration;
    LocalDateTime startTime;

    public Task() {
        this.status = TaskStatus.NEW;
        this.duration = Duration.ZERO;
    }

    public Task(String name, String description) {
        this();
        this.name = name;
        this.description = description;
    }
    public Task(String name, String description, TaskStatus status) {
        this(name, description);
        this.status = status;
    }




    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskStatus getStatus() {
        return this.status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public Duration getDuration() {return this.duration;}

    public void setDuration(Duration duration) {this.duration = duration;}

    public LocalDateTime getStartTime() {return this.startTime;}

    public void setStartTime(LocalDateTime startTime) {this.startTime = startTime;}

    public LocalDateTime getEndTime(){
        if (startTime == null || duration == null) {
            return null;
        }
        return this.startTime.plus(duration);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            Task task = (Task)o;
            return this.id == task.id;
        } else {
            return false;
        }
    }


    public int hashCode() {
        return Objects.hash(new Object[]{this.id});
    }


    public String toString() {
        return "Task{id=" + id +
                ", name='" + name + "'" +
                ", description='" + description + "'" +
                ", status=" + status +
                ", startTime=" + startTime +
                ", duration=" + duration +
                ", endTime=" + getEndTime() + "}";
    }
}
