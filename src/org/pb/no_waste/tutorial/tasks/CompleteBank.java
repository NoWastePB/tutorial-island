package org.pb.no_waste.tutorial.tasks;

import org.pb.no_waste.tutorial.actions.CommonActions;
import org.pb.no_waste.tutorial.data.Stages;
import org.powerbot.script.Condition;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Component;
import org.powerbot.script.rt4.GameObject;
import org.powerbot.script.rt4.Npc;

/**
 * Created by Piet Jetse Heeringa on 20-8-2016.
 */
public class CompleteBank extends Task<ClientContext> {

    private int setting;
    private Stages stage = Stages.BANK;

    private Npc guide = ctx.npcs.select().id(stage.getGuide()).nearest().poll();

    private GameObject booth = ctx.objects.select().id(10083).nearest().poll();
    private GameObject poll = ctx.objects.select().id(26801).nearest().poll();
    private GameObject door = ctx.objects.select().id(9721).nearest().poll();
    private GameObject prayerDoor = ctx.objects.select().id(9722).nearest().poll();

    public CompleteBank(ClientContext ctx) {
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

        if (!booth.valid()) {
            booth = ctx.objects.select().id(10083).nearest().poll();
        }

        if (!poll.valid()) {
            poll = ctx.objects.select().id(26801).nearest().poll();
        }

        if (!door.valid()) {
            door = ctx.objects.select().id(9721).nearest().poll();
        }

        if (!prayerDoor.valid()) {
            prayerDoor = ctx.objects.select().id(9722).nearest().poll();
        }

        switch (setting) {
            case 510:
                openBank();
                break;
            case 520:
                openPoll();
                break;
            case 525:
                openDoor();
                break;
            case 530:
                CommonActions.handleConversation(ctx,guide);
                break;
            case 540:
                CommonActions.openDoor(ctx,prayerDoor,281);
                break;

        }
    }

    private void openDoor() {
        final Component closeComponent = ctx.widgets.widget(310).component(1).component(11);
        if(closeComponent.valid() && closeComponent.visible()){
            closeComponent.click();
            Condition.wait(new Condition.Check() {
                @Override
                public boolean poll() {
                    return !closeComponent.valid() || !closeComponent.visible();
                }
            }, 500, 5);
        }

        CommonActions.openDoor(ctx,door,281);

    }

    private void openPoll() {
        CommonActions.clickUnsupportedContinue(ctx);
        if (!ctx.chat.canContinue()) {
            ctx.bank.close();
            CommonActions.interactWithObject(ctx, poll, "Use", 281);
        } else {
            ctx.chat.clickContinue();
            Condition.sleep(1000);
        }
    }

    private void openBank() {
        Component yesComponent = ctx.widgets.widget(219).component(0).component(1);
        if(!yesComponent.valid() && !yesComponent.visible()) {
            if (!ctx.chat.canContinue()) {
                if (booth.valid()) {
                    if (booth.inViewport()) {
                        ctx.bank.open();
                    } else {
                        ctx.movement.step(booth);
                    }
                }
            } else {
                ctx.chat.clickContinue();
                Condition.sleep(500);
            }
        }else{
            yesComponent.click();
        }
    }
}
