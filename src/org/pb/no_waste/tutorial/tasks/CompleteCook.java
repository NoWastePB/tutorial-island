package org.pb.no_waste.tutorial.tasks;

import org.pb.no_waste.tutorial.actions.CommonActions;
import org.pb.no_waste.tutorial.data.Stages;
import org.powerbot.script.Condition;
import org.powerbot.script.rt4.*;

import java.util.concurrent.Callable;

/**
 * Created by Piet Jetse Heeringa on 19-8-2016.
 */
public class CompleteCook extends Task<ClientContext> {

    private int setting;
    private Stages stage = Stages.COOKING;
    final int[] cookDoorBounds = {128, 104, -228, 0, -8, 120};
    final int[] questDoorBounds = {8, 136, -220, 0, -32, 32};

    private Npc guide = ctx.npcs.select().id(stage.getGuide()).nearest().poll();
    private Item water = ctx.inventory.select().id(1929).poll();
    private Item flour = ctx.inventory.select().id(2516).poll();
    private Item dough = ctx.inventory.select().id(2307).poll();
    private GameObject range = ctx.objects.select().id(9736).nearest().poll();
    private GameObject door = ctx.objects.select().id(9710).each(Interactive.doSetBounds(cookDoorBounds)).nearest().poll();
    private GameObject questDoor = ctx.objects.select().id(9716).each(Interactive.doSetBounds(questDoorBounds)).nearest().poll();

    public CompleteCook(ClientContext ctx) {
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

        if (!water.valid()) {
            water = ctx.inventory.select().id(1929).poll();
        }

        if (!flour.valid()) {
            flour = ctx.inventory.select().id(2516).poll();
        }

        if (!dough.valid()) {
            dough = ctx.inventory.select().id(2307).poll();
        }

        if (!range.valid()) {
            range = ctx.objects.select().id(9736).nearest().poll();
        }

        if (!door.valid()) {
            door = ctx.objects.select().id(9710).each(Interactive.doSetBounds(cookDoorBounds)).nearest().poll();
        }

        if (!questDoor.valid()) {
            questDoor = ctx.objects.select().id(9716).each(Interactive.doSetBounds(questDoorBounds)).nearest().poll();
        }

        switch (setting) {
            case 140:
                CommonActions.handleConversation(ctx, guide);
                break;
            case 150:
                CommonActions.useItemOnItem(ctx, flour, water, new Condition.Check() {
                    @Override
                    public boolean poll() {
                        return dough.valid();
                    }
                }, 250, 10);
                break;
            case 160:
                CommonActions.useItemOnObject(ctx, dough, range, new Condition.Check() {
                    @Override
                    public boolean poll() {
                        return setting > 160;
                    }
                }, 250, 10);
                break;
            case 170:
                ctx.game.tab(Game.Tab.MUSIC);
                break;
            case 180:
                CommonActions.openDoor(ctx, door, setting);
                break;
            case 183:
                ctx.game.tab(Game.Tab.EMOTES);
                break;
            case 187:
                doEmote();
                break;
            case 190:
                ctx.game.tab(Game.Tab.OPTIONS);
                break;
            case 200:
                ctx.movement.running(true);
                break;
            case 210:
                CommonActions.openDoor(ctx,questDoor,setting);
                break;
        }

    }


    private void doEmote() {
        ctx.game.tab(Game.Tab.EMOTES);

        Component yesEmote = ctx.widgets.widget(216).component(1).component(0);
        if (yesEmote.visible()) {
            yesEmote.click();
            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return ctx.players.local().animation() != -1;
                }
            }, 250, 10);
        }
    }

}
