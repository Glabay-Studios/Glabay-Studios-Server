package io.xeros.model.entity.player;

public class PlayerMovementState {

	public static PlayerMovementState getDefault() {
		return new PlayerMovementStateBuilder().createPlayerMovementState();
	}

	private final boolean allowClickToMove;
	private final boolean runningEnabled;
	private final boolean locked;

	public PlayerMovementState(boolean allowClickToMove, boolean runningEnabled, boolean locked) {
		this.allowClickToMove = allowClickToMove;
		this.runningEnabled = runningEnabled;
		this.locked = locked;
	}

	public boolean isAllowClickToMove() {
		return allowClickToMove;
	}

	public boolean isRunningEnabled() {
		return runningEnabled;
	}

	public boolean isLocked() {
		return locked;
	}
}








