package com.openrsc.server.event.rsc;

import com.openrsc.server.model.entity.Mob;

public abstract class ImmediateEvent extends GameTickEvent {

	protected ImmediateEvent(String descriptor) {
		super(null, 0, descriptor);
	}

	public ImmediateEvent(Mob mob, String descriptor) {
		super(mob, 0, descriptor);
	}

	@Override
	public void run() {
		action();
		stop();
	}

	public abstract void action();
}
