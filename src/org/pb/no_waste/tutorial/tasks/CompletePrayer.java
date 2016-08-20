package org.pb.no_waste.tutorial.tasks;

import org.pb.no_waste.tutorial.actions.CommonActions;
import org.pb.no_waste.tutorial.data.Stages;
import org.powerbot.script.Condition;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.*;

/**
 * Created by Piet Jetse Heeringa on 20-8-2016.
 */
public class CompletePrayer extends Task<ClientContext> {

    private int setting;
    private Stages stage = Stages.PRAYER;
    final int[] bounds = {-20, 128, -208, 0, 116, 136};

    private Npc guide = ctx.npcs.select().id(stage.getGuide()).nearest().poll();

    private GameObject largeDoor = ctx.objects.select().id(1524).nearest().poll();
    private GameObject door = ctx.objects.select().id(9723).each(Interactive.doSetBounds(bounds)).nearest().poll();

    public CompletePrayer(ClientContext ctx) {
        super(ctx);
    }

    @Override
    public boolean activate() {
        setting = ctx.varpbits.varpbit(281);
        return setting >= stage.getStart() && setting < stage.getEnd();
    }

    @Override
    public void execute() {

        if (!guide.valid()) {
            guide = ctx.npcs.select().id(stage.getGuide()).nearest().poll();
        }

        if (!largeDoor.valid()) {
            largeDoor = ctx.objects.select().id(1524).nearest().poll();
        }

        if (!door.valid()) {
            door = ctx.objects.select().id(9723).nearest().poll();
        }

        switch (setting) {
            case 550:
                converate();
                break;
            case 560:
                ctx.game.tab(Game.Tab.PRAYER);
                break;
            case 570:
                CommonActions.handleConversation(ctx,guide);
                break;
            case 580:
                ctx.game.tab(Game.Tab.FRIENDS_LIST);
                break;
            case 590:
                ctx.game.tab(Game.Tab.IGNORED_LIST);
                break;
            case 600:
                CommonActions.handleConversation(ctx,guide);
                break;
            case 610:
                CommonActions.openDoor(ctx,door,281);
                break;
        }

    }

    private void converate() {
        Tile tile = new Tile(3125, 3106);
        if (largeDoor.valid()) {
            CommonActions.interactWithObject(ctx, largeDoor, "Open", new Condition.Check() {
                @Override
                public boolean poll() {
                    return !largeDoor.valid();
                }
            }, 500, 5);
        }

        if (guide.valid()) {
            if (guide.inViewport()) {
                CommonActions.handleConversation(ctx, guide);
            } else {
                ctx.movement.step(guide);
            }
        } else {
            ctx.movement.findPath(tile).traverse();
        }

    }
}
