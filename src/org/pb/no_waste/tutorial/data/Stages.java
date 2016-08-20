package org.pb.no_waste.tutorial.data;

/**
 * Created by Piet Jetse Heeringa on 19-8-2016.
 */
public enum Stages {
    START(3308,0,10),
    SURVIVAL(3306,10,140),
    COOKING(3305,140,220),
    QUEST(3312,220,260),
    MINE_AND_SMITH(3311,260,370),
    COMBAT(3307,370,510),
    BANK(3310,510,550),
    PRAYER(3319,550,620),
    MAGIC(3309,620,700);

    private final int guide;
    private final int start;
    private final int end;

    Stages(int guide, int start, int end) {
        this.guide = guide;
        this.start = start;
        this.end = end;
    }

    public int getGuide() {
        return guide;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }
}
