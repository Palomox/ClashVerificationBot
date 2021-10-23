package cf.endersclan.discordbot.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

public class CommandManager {
	private List<Command> commands;

	public CommandManager(Command...commands) {
		this.commands = Arrays.asList(commands);
	}
	public CommandManager() {
		this.commands = new ArrayList<>();
	}
	public void runCommand(SlashCommandEvent e) {
		var command = this.commands.stream()
						.filter(c -> c.getLabel().equals(e.getName()))
						.findFirst();
		if(command.isEmpty()) {
			e.reply("Command not found").setEphemeral(true).queue();
			return;
		}
		if(!command.get().run(e)) {
			e.reply("An internal error ocurred while executing this command").setEphemeral(true).queue();
		}
	}
}
