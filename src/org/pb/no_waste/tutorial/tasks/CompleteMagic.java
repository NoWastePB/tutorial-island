package org.pb.no_waste.tutorial.tasks;

import org.pb.no_waste.tutorial.actions.CommonActions;
import org.pb.no_waste.tutorial.data.Stages;
import org.powerbot.script.Condition;
import org.powerbot.script.Filter;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.*;

/**
 * Created by Piet Jetse Heeringa on 20-8-2016.
 */
public class CompleteMagic extends Task<ClientContext> {

    private int setting;
    private Stages stage = Stages.MAGIC;

    private Npc guide = ctx.npcs.select().id(stage.getGuide()).nearest().poll();
    private Npc chicken = ctx.npcs.select().id(3316).select(new Filter<Npc>() {
        @Override
        public boolean accept(Npc npc) {
            return !npc.inCombat();
        }
    }).limit(5).nearest().shuffle().poll();
    ;

    public CompleteMagic(ClientContext ctx) {
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

        if (!chicken.valid()) {
            chicken = ctx.npcs.select().id(3316).select(new Filter<Npc>() {
                @Override
                public boolean accept(Npc npc) {
                    return !npc.inCombat();
                }
            }).limit(5).nearest().shuffle().poll();
        }

        switch (setting) {
            case 620:
                converate();
                break;
            case 630:
                ctx.game.tab(Game.Tab.MAGIC);
                break;
            case 640:
                CommonActions.handleConversation(ctx, guide);
                break;
            case 650:
                killChicken();
                break;
            case 670:
                finish();
                break;
        }

    }

    private void finish() {
        Component yesComponent = ctx.widgets.widget(219).component(0).component(1);
        Component continueComponent = ctx.widgets.widget(231).component(2);
        if (!yesComponent.valid() && !yesComponent.visible()) {
            if (ctx.chat.canContinue()) {
                ctx.chat.clickContinue();
                Condition.sleep(1000);
            } else if (CommonActions.hasUnsupportedContinue(ctx)) {
                CommonActions.clickUnsupportedContinue(ctx);
                Condition.sleep(1000);
            } else {
                CommonActions.handleConversation(ctx, guide);
            }
        } else {
            yesComponent.click();
        }
    }

    private void killChicken() {
        Tile tile = new Tile(3139, 3091);
        if (!ctx.players.local().inCombat()) {
            if (chicken.valid()) {
                if (chicken.inViewport()) {
                    ctx.magic.cast(Magic.Spell.WIND_STRIKE);
                    chicken.interact("Cast", chicken.name());
                    Condition.wait(new Condition.Check() {
                        @Override
                        public boolean poll() {
                            return ctx.players.local().inCombat();
                        }
                    }, 500, 10);
                } else {
                    ctx.movement.step(tile);
                    ctx.camera.turnTo(chicken);
                    Condition.wait(new Condition.Check() {
                        @Override
                        public boolean poll() {
                            return chicken.inViewport();
                        }
                    }, 500, 10);
                }
            }
        }
    }

    private void converate() {
        Tile tile = new Tile(3141, 3087);

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
