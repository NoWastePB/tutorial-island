package org.pb.no_waste.tutorial.tasks;

import org.powerbot.script.rt4.ClientAccessor;
import org.powerbot.script.rt4.ClientContext;

/**
 * Created by Piet Jetse Heeringa on 14-8-2016.
 */

public abstract class Task<C extends ClientContext> extends ClientAccessor {
    public Task(C ctx) {
        super(ctx);
    }

    public abstract boolean activate();
    public abstract void execute();
}

