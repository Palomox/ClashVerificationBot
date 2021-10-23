package cf.endersclan.discordbot.listeners;

import cf.endersclan.discordbot.main.EndersBot;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;

public class CommandListener {

	@SubscribeEvent
	public void onCommand(SlashCommandEvent event) {
		EndersBot.instance.getCommandManager().runCommand(event);
	}
}
