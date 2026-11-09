package ru.turtlebot.util;

import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.Formatting;

/** Клиентская зелёная подсветка черепах, которых бот уже кормил. */
public final class FedTurtleHighlighter {
    private static final String TEAM_NAME = "turtlebot_fed";

    private FedTurtleHighlighter() {}

    public static void apply(TurtleEntity turtle) {
        if (turtle == null || turtle.getWorld().isClient == false) return;
        Scoreboard scoreboard = turtle.getWorld().getScoreboard();
        Team team = scoreboard.getTeam(TEAM_NAME);
        if (team == null) {
            team = scoreboard.addTeam(TEAM_NAME);
            team.setColor(Formatting.GREEN);
        }
        scoreboard.addScoreHolderToTeam(turtle.getUuidAsString(), team);
        turtle.setGlowing(true);
    }
}
