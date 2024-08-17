package io.xeros.model.tickable.impl;


import java.util.function.Consumer;

import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Position;
import io.xeros.model.tickable.Tickable;
import io.xeros.model.tickable.TickableContainer;

public class WalkToTickable implements Tickable<Player> {

	/**
	 * x distance
	 */
	private int xDistance = -1;

	/**
	 * y distance
	 */
	private int yDistance = -1;

	/**
	 * The associated game character.
	 */
	private final Player player;

	/**
	 * The destination the game character will move to.
	 */
	private Position destination;

	/**
	 * The task a player must execute upon reaching said destination.
	 */
	private final Consumer<Player> consumer;

	/**
	 * The WalkToTask constructor.
	 */
	public WalkToTickable(Player entity, Position destination, int xDistance, int yDistance, Consumer<Player> consumer) {
		this.player = entity;
		this.destination = destination;
		this.consumer = consumer;
		this.xDistance = xDistance;
		this.yDistance = yDistance;
	}

	@Override
	public void tick(TickableContainer<Player> container, Player player) {
		int x = player.getPosition().getX();
		int y = player.getPosition().getY();
		int xMin = destination.getX() - 1;
		int xMax = xMin + xDistance + 1;
		int yMin = destination.getY() - 1;
		int yMax = yMin + yDistance + 1;

		if (x >= xMin && y >= yMin && x <= xMax && y <= yMax) {
			container.stop();
			consumer.accept(player);
		}
	}
}
