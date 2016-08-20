package org.pb.no_waste.tutorial.tasks;

import org.pb.no_waste.tutorial.actions.CommonActions;
import org.pb.no_waste.tutorial.data.Stages;
import org.powerbot.script.rt4.*;

/**
 * Created by Piet Jetse Heeringa on 19-8-2016.
 */
public class CompleteQuest extends Task<ClientContext>{
    private int setting;
    private Stages stage = Stages.QUEST;

    private Npc guide = ctx.npcs.select().id(stage.getGuide()).nearest().poll();
    private GameObject ladder = ctx.objects.select().id(9726).nearest().poll();

    public CompleteQuest(ClientContext ctx) {
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

        if (!ladder.valid()) {
            ladder = ctx.objects.select().id(9726).nearest().poll();
        }

        switch (setting) {
            case 220:
                CommonActions.handleConversation(ctx, guide);
                break;

            case 230:
                ctx.game.tab(Game.Tab.QUESTS);
                break;

            case 240:
                CommonActions.handleConversation(ctx, guide);
                break;

            case 250:
                CommonActions.interactWithObject(ctx, ladder, "Climb-down", 281);
                break;

        }
    }
}
