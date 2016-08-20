package org.pb.no_waste.tutorial.actions;

import org.pb.no_waste.tutorial.logger.Log;
import org.powerbot.script.Condition;
import org.powerbot.script.Random;
import org.powerbot.script.Tile;
import org.powerbot.script.rt4.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Piet Jetse Heeringa on 15-8-2016.
 */
public class CommonActions {

    public static void interactWithObject(final ClientContext ctx, final GameObject object, String action, int setting) {
        if (object.valid()) {
            if (object.inViewport()) {
                object.interact(action, object.name());
                waitForSettingChange(ctx, setting, 500, 15);
            } else {
                ctx.movement.step(object);
                ctx.camera.turnTo(object);
                Condition.wait(new Condition.Check() {
                    @Override
                    public boolean poll() {
                        return object.tile().distanceTo(ctx.players.local()) < 3;
                    }
                }, 500, 10);
            }
        } else {
            Log.w("Common Actions", object.name() + " is'nt found so we can't use it.");
        }
    }

    public static void interactWithObject(final ClientContext ctx, final GameObject object, String action, Condition.Check condition, int time, int freq) {
        if (object.valid()) {
            if (object.inViewport()) {
                object.interact(action, object.name());
                Condition.wait(condition, time, freq);
            } else {
                ctx.movement.step(object);
                ctx.camera.turnTo(object);
                Condition.wait(new Condition.Check() {
                    @Override
                    public boolean poll() {
                        return object.tile().distanceTo(ctx.players.local()) < 3;
                    }
                }, 500, 10);
            }
        } else {
            Log.w("Common Actions", object.name() + " is'nt found so we can't use it.");
        }
    }

    public static void openDoor(final ClientContext ctx, final GameObject door, int setting) {
        Log.i("Common Actions", String.format("[%s] Opening door", setting));
        if (door.valid()) {
            if (door.inViewport()) {
                door.interact("Open", door.name());
                waitForSettingChange(ctx, setting, 250, 10);
            } else {
                ctx.movement.step(door);
                ctx.camera.turnTo(door);
                Condition.wait(new Condition.Check() {
                    @Override
                    public boolean poll() {
                        return door.tile().distanceTo(ctx.players.local()) < 3;
                    }
                }, 500, 10);
            }
        } else {
            Log.w("Common Actions", "Door is'nt found so we can't open it.");
        }
    }

    public static void handleConversation(final ClientContext ctx, final Npc guide) {

        if (ctx.chat.canContinue()) {
            ctx.chat.clickContinue();
            Condition.sleep(Random.nextInt(250, 750));
        } else {

            if (guide.valid()) {

                if (guide.inViewport()) {
                    guide.interact("Talk-to", guide.name());
                    Condition.wait(new Condition.Check() {
                        @Override
                        public boolean poll() {
                            return ctx.chat.canContinue();
                        }
                    }, 500, 3);
                } else {
                    ctx.movement.step(guide);
                    Condition.wait(new Condition.Check() {
                        @Override
                        public boolean poll() {
                            return guide.inViewport();
                        }
                    }, 500, 8);
                }
            }
        }

    }

    public static void waitForSettingChange(final ClientContext ctx, final int setting, int sleep, int freq) {
        final int value = ctx.varpbits.varpbit(setting);
        Condition.wait(new Condition.Check() {
            @Override
            public boolean poll() {
                return value != ctx.varpbits.varpbit(setting);
            }
        }, sleep, freq);
    }

    public static void clickUnsupportedContinue(ClientContext ctx) {
        for (Widget widget : ctx.widgets.select()) {
            for (Component component : widget.components()) {
                if (component.valid() && component.visible() && component.text().toLowerCase().contains("continue")) {
                    component.click();
                    Condition.sleep(Random.nextInt(1000, 2500));
                }
            }
        }
    }

    public static void useItemOnItem(final ClientContext ctx, final Item first, Item second, Condition.Check condition, int time, int freq) {

        ctx.game.tab(Game.Tab.INVENTORY);

        if (ctx.inventory.selectedItem().id() != first.id()) {
            first.click();
            Condition.wait(new Condition.Check() {
                @Override
                public boolean poll() {
                    return ctx.inventory.selectedItem() == first;
                }
            }, 200, 5);
        }

        if (ctx.inventory.selectedItem().id() == first.id()) {
            second.click();
            Condition.wait(condition, time, freq);
        }
    }

    public static void useItemOnObject(ClientContext ctx, Item item, final GameObject object, Condition.Check condition, int time, int freq) {

        if (item.valid()) {
            if (object.valid()) {
                if (object.inViewport()) {
                    ctx.game.tab(Game.Tab.INVENTORY);
                    item.click();
                    object.interact("Use", object.name());
                    Condition.wait(condition, time, freq);
                } else {
                    ctx.movement.step(object);
                    ctx.camera.turnTo(object);
                    Condition.wait(new Condition.Check() {
                        @Override
                        public boolean poll() {
                            return object.inViewport();
                        }
                    }, 10, 500);
                }
            } else {
                Log.w("Common Actions", object.name() + " is'nt found so we can't use it.");
            }
        } else {
            Log.w("Common Actions", item.name() + " is'nt found so we can't use it.");
        }

    }

    public static int getFlag(ClientContext ctx, final Tile tile) {
        return ctx.client().getCollisionMaps()[tile.floor()].getFlags()[tile.x() - ctx.game.mapOffset().x()][tile.y() - ctx.game.mapOffset().y()];
    }

    public static Tile[] getSurroundingTiles(final Tile tile, final int size) {
        final List<Tile> tiles = new ArrayList<Tile>();
        for (int x = -(size); x < size + 1; x++) {
            for (int y = -(size); y < size + 1; y++) {
                tiles.add(tile.derive(x, y, tile.floor()));
            }
        }
        return tiles.toArray(new Tile[tiles.size()]);
    }

    public static void equip(final ClientContext ctx, final Item item) {
        if (item.valid()) {
            if (ctx.equipment.name(item.name()).isEmpty()) {
                item.click();
                Condition.wait(new Condition.Check() {
                    @Override
                    public boolean poll() {
                        return !ctx.equipment.name(item.name()).isEmpty();
                    }
                }, 200, 10);
            }
        }
    }

    public static boolean hasUnsupportedContinue(ClientContext ctx) {
        for (Widget widget : ctx.widgets.select()) {
            for (Component component : widget.components()) {
                if (component.valid() && component.visible() && component.text().toLowerCase().contains("continue")) {
                    return true;
                }
            }
        }
        return false;
    }
}
