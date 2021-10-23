package cf.endersclan.discordbot.commands;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

public interface Command {
	boolean run(SlashCommandEvent e);
	String getLabel();
}
