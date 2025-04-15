package dev.drtheo.autojson.benchmark.ait;

public class DoorHandler {

    DoorState state = DoorState.CLOSED;
    float leftDoorRot;
    float rightDoorRot;

    boolean locked;
    boolean previouslyLocked;
    boolean deadlocked;

    AnimationDoorState animState = AnimationDoorState.CLOSED;

    enum DoorState {
        BOTH,
        HALF,
        CLOSED
    }

    enum AnimationDoorState {
        CLOSED, FIRST, SECOND, BOTH
    }
}
