package org.pb.no_waste.tutorial.tasks;

import org.pb.no_waste.tutorial.actions.CommonActions;
import org.pb.no_waste.tutorial.data.Stages;
import org.powerbot.bot.rt4.client.CollisionMap;
import org.powerbot.script.Condition;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.*;
import org.powerbot.script.rt6.CollisionFlag;

import java.util.concurrent.Callable;

/**
 * Created by Piet Jetse Heeringa on 19-8-2016.
 */
public class CompleteMiningAndSmithing extends Task<ClientContext> {

    private int setting;
    private Stages stage = Stages.MINE_AND_SMITH;
    final int[] gateBounds = {112, 144, -180, 0, -12, 244};

    private Npc guide = ctx.npcs.select().id(stage.getGuide()).nearest().poll();

    private GameObject tinRock = ctx.objects.select().id(10080).nearest().limit(5).shuffle().first().poll();
    private GameObject copperRock = ctx.objects.select().id(10079).nearest().limit(5).shuffle().first().poll();
    private GameObject furnace = ctx.objects.select().id(10082).nearest().poll();
    private GameObject anvil = ctx.objects.select().id(2097).nearest().poll();
    private GameObject gate = ctx.objects.select().id(9717).each(Interactive.doSetBounds(gateBounds)).nearest().poll();

    private Item tinOre = ctx.inventory.select().id(438).poll();
    private Item bar = ctx.inventory.select().id(2349).poll();

    public CompleteMiningAndSmithing(ClientContext ctx) {
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

        if (!tinRock.valid()) {
            tinRock = ctx.objects.select().id(10080).nearest().limit(5).shuffle().first().poll();
        }

        if (!copperRock.valid()) {
            copperRock = ctx.objects.select().id(10079).nearest().limit(5).shuffle().first().poll();
        }

        if (!furnace.valid()) {
            furnace = ctx.objects.select().id(10082).nearest().poll();
        }

        if (!anvil.valid()) {
            anvil = ctx.objects.select().id(2097).nearest().poll();
        }

        if (!tinOre.valid()) {
            tinOre = ctx.inventory.select().id(438).poll();
        }

        if (!bar.valid()) {
            bar = ctx.inventory.select().id(2349).poll();
        }

        if (!gate.valid()) {
            gate = ctx.objects.select().id(9717).each(Interactive.doSetBounds(gateBounds)).nearest().poll();
        }

        switch (setting) {

            case 260:
                greetInstructor();
                break;

            case 270:
                CommonActions.interactWithObject(ctx, tinRock, "Prospect", 281);
                break;
            case 280:
                CommonActions.interactWithObject(ctx, copperRock, "Prospect", 281);
                break;
            case 290:
                CommonActions.handleConversation(ctx, guide);
                break;
            case 300:
                CommonActions.interactWithObject(ctx, tinRock, "Mine", 281);
                break;
            case 310:
                 CommonActions.interactWithObject(ctx, copperRock, "Mine", 281);
                break;
            case 320:
               CommonActions.useItemOnObject(ctx, tinOre, furnace, new Condition.Check() {
                   @Override
                   public boolean poll() {
                       return setting > 320;
                   }
               }, 500, 10);
                break;
            case 330:
                CommonActions.clickUnsupportedContinue(ctx);
                CommonActions.handleConversation(ctx, guide);
                break;
            case 340:
                CommonActions.useItemOnObject(ctx, bar, anvil, new Condition.Check() {
                    @Override
                    public boolean poll() {
                        return setting > 340;
                    }
                }, 500, 10);
                break;
            case 350:
                smithWeapon();
                break;
            case 360:
                CommonActions.interactWithObject(ctx,gate,"Open",281);
                break;
        }
    }

    private void smithWeapon() {
        Component daggerComponent = ctx.widgets.widget(312).component(2).component(2);
        if(daggerComponent.valid() && daggerComponent.visible()){
            daggerComponent.click();
            Condition.wait(new Condition.Check() {
                @Override
                public boolean poll() {
                    return setting > 350;
                }
            }, 500, 10);
        }else{
            CommonActions.useItemOnObject(ctx, bar, anvil, new Condition.Check() {
                @Override
                public boolean poll() {
                    return setting > 340;
                }
            }, 500, 10);
        }
    }

    private void greetInstructor() {
        final Tile center = new Tile(3083, 9505, 0);
        if (center.distanceTo(ctx.players.local()) > 5) {
            ctx.movement.findPath(center).traverse();
            Condition.wait(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    return center.distanceTo(ctx.players.local()) < 6;
                }
            }, 500, 10);
        } else {
            CommonActions.handleConversation(ctx, guide);
        }
    }


}
