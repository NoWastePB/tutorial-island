package org.pb.no_waste.tutorial.tasks;

import org.pb.no_waste.tutorial.actions.CommonActions;
import org.pb.no_waste.tutorial.data.Stages;
import org.powerbot.script.Condition;
import org.powerbot.script.rt4.*;


/**
 * Created by Piet Jetse Heeringa on 19-8-2016.
 */
public class CompleteSurvival extends Task<ClientContext> {

    private int setting;
    private Stages stage = Stages.SURVIVAL;
    final int[] bounds = {144, 116, -116, 0, -120, 112};

    private Npc guide = ctx.npcs.select().id(stage.getGuide()).nearest().poll();
    private Npc spot = ctx.npcs.select().id(3317).nearest().limit(3).shuffle().first().poll();
    private GameObject door = ctx.objects.select().id(9398).nearest().poll();
    private GameObject fire = ctx.objects.select().id(26185).nearest().poll();
    private GameObject gate = ctx.objects.select().id(9708).each(Interactive.doSetBounds(bounds)).poll();
    private GameObject tree = ctx.objects.select().id(9730).nearest().poll();
    private GameObject cookDoor = ctx.objects.select().id(9709).nearest().poll();
    private Item axe = ctx.inventory.select().id(1351).poll();
    private Item net = ctx.inventory.select().id(303).poll();
    private Item fish = ctx.inventory.select().id(2514).poll();


    public CompleteSurvival(ClientContext ctx) {
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

        if (!spot.valid()) {
            spot = ctx.npcs.select().id(3317).nearest().limit(3).shuffle().first().poll();
        }

        if (!door.valid()) {
            door = ctx.objects.select().id(9398).nearest().poll();
        }

        if (!cookDoor.valid()) {
            cookDoor = ctx.objects.select().id(9709).nearest().poll();
        }

        if (!fire.valid()) {
            fire = ctx.objects.select().id(26185).nearest().poll();
        }

        if (!gate.valid()) {
            gate = ctx.objects.select().id(9708).each(Interactive.doSetBounds(bounds)).poll();
        }

        if (!tree.valid()) {
            tree = ctx.objects.select().id(9730).nearest().poll();

        }

        if (!axe.valid()) {
            axe = ctx.inventory.select().id(1351).poll();
        }

        if (!net.valid()) {
            net = ctx.inventory.select().id(303).poll();
        }

        if (!fish.valid()) {
            fish = ctx.inventory.select().id(2514).poll();
        }

        switch (setting) {
            case 10:
                CommonActions.openDoor(ctx, door, setting);
                break;
            case 20:
                CommonActions.handleConversation(ctx, guide);
                break;
            case 30:
                collectAxe();
                break;
            case 40:
                CommonActions.interactWithObject(ctx, tree, "Chop down", setting);
                break;
            case 50:
                createFire();
                break;
            case 60:
                ctx.game.tab(Game.Tab.STATS);
                break;
            case 70:
                CommonActions.handleConversation(ctx, guide);
                break;
            case 80:
                catchFish();
                break;
            case 90:
            case 100:
            case 110:
                cookFish();
                break;
            case 120:
                CommonActions.interactWithObject(ctx,gate,"Open",setting);
                break;
            case 130:
                CommonActions.openDoor(ctx, cookDoor, setting);
                break;
        }

    }

    private void cookFish() {
        if (ctx.players.local().animation() == -1) {
            if (fish.valid()) {
                if (fire.valid()) {
                    CommonActions.useItemOnObject(ctx, fish, fire, new Condition.Check() {
                        @Override
                        public boolean poll() {
                            return ctx.players.local().animation() != -1;
                        }
                    }, 10, 500);
                } else {
                    createFire();
                }
            } else {
                catchFish();
            }
        }
    }

    private void createFire() {
        if (ctx.players.local().animation() == -1) {
            Item log = ctx.inventory.select().id(2511).poll();
            Item tinderbox = ctx.inventory.select().id(590).poll();
            if (log.valid()) {
                CommonActions.useItemOnItem(ctx, log, tinderbox, new Condition.Check() {
                    @Override
                    public boolean poll() {
                        return ctx.players.local().animation() != -1;
                    }
                }, 500, 10);
            } else {
                if (ctx.players.local().animation() == -1) {
                    CommonActions.interactWithObject(ctx, tree, "Chop down", new Condition.Check() {
                        @Override
                        public boolean poll() {
                            return ctx.players.local().animation() != -1;
                        }
                    }, 10, 500);
                }
            }
        }
    }

    private void catchFish() {

        if (net.valid()) {
            if (spot.valid()) {
                if (!spot.inViewport()) {
                    ctx.movement.step(spot);
                    ctx.camera.turnTo(spot);
                    Condition.wait(new Condition.Check() {
                        @Override
                        public boolean poll() {
                            return spot.inViewport();
                        }
                    }, 500, 10);
                } else {
                    if (ctx.players.local().animation() == -1) {
                        spot.interact("Net", "Fishing spot");
                        Condition.wait(new Condition.Check() {
                            @Override
                            public boolean poll() {
                                return ctx.players.local().animation() != -1;
                            }
                        }, 500, 10);
                    }
                }
            }
        }
    }

    private void collectAxe() {

        if (ctx.chat.canContinue()) {
            CommonActions.handleConversation(ctx, guide);
        } else {
            ctx.game.tab(Game.Tab.OPTIONS);
            ctx.game.tab(Game.Tab.INVENTORY);
            CommonActions.clickUnsupportedContinue(ctx);
        }

    }


}
