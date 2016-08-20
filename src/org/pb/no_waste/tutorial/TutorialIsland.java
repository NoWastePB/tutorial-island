package org.pb.no_waste.tutorial;

import org.pb.no_waste.tutorial.logger.Log;
import org.pb.no_waste.tutorial.tasks.*;
import org.powerbot.script.Condition;
import org.powerbot.script.PaintListener;
import org.powerbot.script.PollingScript;
import org.powerbot.script.Script;
import org.powerbot.script.rt4.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.concurrent.Callable;

/**
 * Created by Piet Jetse Heeringa on 14-8-2016.
 */
@Script.Manifest(name = "NW Tutorial Island", properties = "author=No Waste; client=4;", description = "[BETA]Completes Tutorial Island!")
public class TutorialIsland extends PollingScript<ClientContext> implements PaintListener {

    private ArrayList<Task> tasks = new ArrayList<Task>();

    @Override
    public void start() {
        tasks.addAll(Arrays.asList(

                new CompleteStart(ctx),
                new CompleteSurvival(ctx),
                new CompleteCook(ctx),
                new CompleteQuest(ctx),
                new CompleteMiningAndSmithing(ctx),
                new CompleteCombat(ctx),
                new CompleteBank(ctx),
                new CompletePrayer(ctx),
                new CompleteMagic(ctx)
                ));
        Log.i("Core", "Starting Script");

    }

    @Override
    public void stop() {

    }

    @Override
    public void poll() {

        for (Task task : tasks) {
            if (task.activate()) {
                task.execute();
            }
        }

    }


    @Override
    public void repaint(Graphics graphics) {
        Npc guide = ctx.npcs.select().id(3308).nearest().poll();
        Npc expert = ctx.npcs.select().id(3306).nearest().poll();

        if (guide.valid() && guide.inViewport()) {
            guide.draw(graphics);
        }

        if (expert.valid() && expert.inViewport()) {
            expert.draw(graphics);
        }



    }
}
