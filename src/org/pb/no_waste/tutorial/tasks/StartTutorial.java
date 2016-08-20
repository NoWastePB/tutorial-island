package org.pb.no_waste.tutorial.tasks;

import org.powerbot.script.rt4.ClientContext;

/**
 * Created by Piet Jetse Heeringa on 14-8-2016.
 */
public class StartTutorial extends Task<ClientContext>{

    public StartTutorial(ClientContext ctx) {
       super(ctx);
    }

    @Override
    public boolean activate() {
        return ctx.varpbits.varpbit(281) < 10;
    }

    @Override
    public void execute() {

    }
}
