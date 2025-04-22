package dev.drtheo.autojson.benchmark.ait.data;

public class DoorHandler extends TardisComponent {

    DoorState state = DoorState.CLOSED;
    float leftDoorRot;
    float rightDoorRot;

    boolean locked;
    boolean previouslyLocked;
    boolean deadlocked;

    AnimationDoorState animState = AnimationDoorState.CLOSED;

    public DoorHandler() {
        super(IdLike.DOOR);
    }

    enum DoorState {
        BOTH,
        HALF,
        CLOSED
    }

    enum AnimationDoorState {
        CLOSED, FIRST, SECOND, BOTH
    }
}
