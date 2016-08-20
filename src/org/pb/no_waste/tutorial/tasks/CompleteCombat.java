package org.pb.no_waste.tutorial.tasks;

import org.pb.no_waste.tutorial.actions.CommonActions;
import org.pb.no_waste.tutorial.data.Stages;
import org.powerbot.script.Condition;
import org.powerbot.script.Filter;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.*;

/**
 * Created by Piet Jetse Heeringa on 19-8-2016.
 */
public class CompleteCombat extends Task<ClientContext> {

    private int setting;
    private Stages stage = Stages.COMBAT;

    private Npc guide = ctx.npcs.select().id(stage.getGuide()).nearest().poll();
    private Npc rat = ctx.npcs.select().id(3313).select(new Filter<Npc>() {
        @Override
        public boolean accept(Npc npc) {
            return !npc.inCombat() && npc.tile().distanceTo(ctx.players.local()) < 6;
        }
    }).limit(5).nearest().shuffle().poll();

    private Component equipmentComponent = ctx.widgets.widget(387).component(17);
    private Component closeEquipmentComponent = ctx.widgets.widget(84).component(4);

    private Item dagger = ctx.inventory.select().id(1205).poll();
    private Item sword = ctx.inventory.select().id(1277).poll();
    private Item shield = ctx.inventory.select().id(1171).poll();
    private Item bow = ctx.inventory.select().id(841).poll();
    private Item arrow = ctx.inventory.select().id(882).poll();

    private GameObject ladder = ctx.objects.select().id(9727).nearest().poll();
    private GameObject gate = ctx.objects.select().id(9719).nearest().poll();

    public CompleteCombat(ClientContext ctx) {
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

        if (!rat.valid()) {
            rat = ctx.npcs.select().id(3313).select(new Filter<Npc>() {
                @Override
                public boolean accept(Npc npc) {
                    return !npc.inCombat() && npc.interacting() == null;
                }
            }).limit(5).nearest().shuffle().poll();
        }

        if (!equipmentComponent.valid()) {
            equipmentComponent = ctx.widgets.widget(387).component(17);
        }

        if (!closeEquipmentComponent.valid()) {
            closeEquipmentComponent = ctx.widgets.widget(84).component(4);
        }

        if (!dagger.valid()) {
            dagger = ctx.inventory.select().id(1205).poll();
        }

        if (!sword.valid()) {
            sword = ctx.inventory.select().id(1277).poll();
        }

        if (!shield.valid()) {
            shield = ctx.inventory.select().id(1171).poll();
        }

        if (!bow.valid()) {
            bow = ctx.inventory.select().id(841).poll();
        }

        if (!arrow.valid()) {
            arrow = ctx.inventory.select().id(882).poll();
        }

        if (!gate.valid()) {
            gate = ctx.objects.select().id(9719).nearest().poll();
        }

        if (!ladder.valid()) {
            ladder = ctx.objects.select().id(9727).nearest().poll();
        }

        switch (setting) {
            case 370:
                CommonActions.handleConversation(ctx, guide);
                break;

            case 390:
                ctx.game.tab(Game.Tab.EQUIPMENT);
                break;
            case 400:
                openEquipmentStats();
                break;
            case 405:
                CommonActions.equip(ctx, dagger);
                break;
            case 410:
                closeEquipmentStats();
                CommonActions.handleConversation(ctx, guide);
                break;
            case 420:
                ctx.game.tab(Game.Tab.INVENTORY);
                CommonActions.equip(ctx, sword);
                CommonActions.equip(ctx, shield);
                break;
            case 430:
                ctx.game.tab(Game.Tab.ATTACK);
                break;
            case 440:
                CommonActions.openDoor(ctx, gate, setting);
                break;
            case 450:
            case 460:
                killRat();
                break;
            case 470:
                if (!guide.tile().matrix(ctx).reachable()) {
                    CommonActions.openDoor(ctx, gate, setting);
                } else {
                    CommonActions.handleConversation(ctx, guide);
                }
                break;
            case 480:
            case 490:
                ctx.game.tab(Game.Tab.INVENTORY);
                CommonActions.equip(ctx, bow);
                CommonActions.equip(ctx, arrow);
                killRat();
                break;
            case 500:
                climbLadder();
                break;
        }

    }

    private void climbLadder() {
        Tile ladderTile = new Tile(3110, 9524);
        if (ladderTile.distanceTo(ctx.players.local()) < 10) {
            CommonActions.interactWithObject(ctx, ladder, "Climb-up", 281);
        } else {
            ctx.movement.findPath(ladderTile).traverse();
        }
    }

    private void killRat() {
        System.out.println(rat.name());
        if (!ctx.players.local().inCombat()) {
            if (rat.valid()) {
                if (rat.inViewport()) {
                    rat.interact("Attack", rat.name());
                    Condition.wait(new Condition.Check() {
                        @Override
                        public boolean poll() {
                            return ctx.players.local().inCombat();
                        }
                    }, 500, 10);
                } else {
                    ctx.movement.step(rat);
                    ctx.camera.turnTo(rat);
                    Condition.wait(new Condition.Check() {
                        @Override
                        public boolean poll() {
                            return rat.inViewport();
                        }
                    }, 500, 10);
                }
            }
        }
    }

    private void closeEquipmentStats() {
        if (closeEquipmentComponent.valid() && closeEquipmentComponent.visible()) {
            closeEquipmentComponent.click();
            CommonActions.waitForSettingChange(ctx, setting, 250, 10);
        }
    }


    private void openEquipmentStats() {
        ctx.game.tab(Game.Tab.EQUIPMENT);
        if (equipmentComponent.valid() && equipmentComponent.visible()) {
            equipmentComponent.click();
            CommonActions.waitForSettingChange(ctx, setting, 250, 10);
        }
    }
}
