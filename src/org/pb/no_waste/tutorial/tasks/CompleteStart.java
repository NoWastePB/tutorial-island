package org.pb.no_waste.tutorial.tasks;

import org.pb.no_waste.tutorial.actions.CommonActions;
import org.pb.no_waste.tutorial.data.Stages;
import org.powerbot.script.Condition;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Component;
import org.powerbot.script.rt4.Game;
import org.powerbot.script.rt4.Npc;

/**
 * Created by Piet Jetse Heeringa on 19-8-2016.
 */
public class CompleteStart extends Task<ClientContext>{
    private int setting;
    private Stages stage = Stages.START;
    private Npc guide = ctx.npcs.nil();

    public CompleteStart(ClientContext ctx) {
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
            System.out.println("setting guide");
            guide = ctx.npcs.select().id(stage.getGuide()).nearest().poll();
        }


        switch (setting){
            case 0:
                Component characterAcceptComponent = ctx.widgets.widget(269).component(100);
                if(characterAcceptComponent.visible()) {
                    clickAccept(characterAcceptComponent);
                }else{
                    CommonActions.handleConversation(ctx,guide);
                }
                break;
            case 3:
                if(!ctx.chat.canContinue()) {
                    ctx.game.tab(Game.Tab.OPTIONS);
                }else{
                    CommonActions.handleConversation(ctx,guide);
                }
                break;
            case 7:
                CommonActions.handleConversation(ctx,guide);
                break;
        }
    }

    private void clickAccept(final Component characterAcceptComponent) {
        if(characterAcceptComponent.valid() && characterAcceptComponent.visible()){
            characterAcceptComponent.click();
            Condition.wait(new Condition.Check() {
                @Override
                public boolean poll() {
                    return !characterAcceptComponent.visible();
                }
            }, 500, 5);
        }
    }
}
