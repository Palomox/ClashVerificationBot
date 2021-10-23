package cf.endersclan.discordbot.commands;

import java.io.IOException;

import com.lycoon.clashapi.cocmodels.player.Player;
import com.lycoon.clashapi.core.ClashAPI;
import com.lycoon.clashapi.core.exception.ClashAPIException;

import cf.endersclan.discordbot.main.EndersBot;
import cf.endersclan.discordbot.persistence.EndersMember;
import cf.endersclan.discordbot.persistence.query.QEndersMember;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

public class LinkCommand implements Command{
	private static final Role NON_CHOOSEN = EndersBot.instance.getJda().getRoleById(577160958359633932L);
	private static final Role TH3 = EndersBot.instance.getJda().getRoleById(577096735755862037L);
	private static final Role TH4 = EndersBot.instance.getJda().getRoleById(577096859466727424L);
	private static final Role TH5 = EndersBot.instance.getJda().getRoleById(577096888755421224L);
	private static final Role TH6 = EndersBot.instance.getJda().getRoleById(577096919441080322L);
	private static final Role TH7 = EndersBot.instance.getJda().getRoleById(577096945928110080L);
	private static final Role TH8 = EndersBot.instance.getJda().getRoleById(577096981118189577L);
	private static final Role TH9 = EndersBot.instance.getJda().getRoleById(577097007466938388L);
	private static final Role TH10 = EndersBot.instance.getJda().getRoleById(577097043277905969L);
	private static final Role TH11 = EndersBot.instance.getJda().getRoleById(577097092518903819L);
	private static final Role TH12 = EndersBot.instance.getJda().getRoleById(577097128355037195L);
	private static final Role TH13 = EndersBot.instance.getJda().getRoleById(662359399754366996L);
	private static final Role TH14 = EndersBot.instance.getJda().getRoleById(861791578967506954L);


	public boolean run(SlashCommandEvent event) {
		event.deferReply(true).queue();
		String tag = event.getOption("tag").getAsString();
		String token = event.getOption("token").getAsString();

		ClashAPI cocApi = EndersBot.instance.getApiManager().getApi();
		boolean verified;
		try {
			verified = cocApi.isVerifiedPlayer(tag, token);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (ClashAPIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		if(!verified) {
			event.getHook().sendMessage("No se te ha podido verificar").setEphemeral(true).queue();
			return true;
		}
		/*
		 * We check the player being in Enders ñ clan
		 */
		Player player;
		try {
			player = cocApi.getPlayer(tag);
			if(!EndersBot.ENDERSTAG.equals(player.getClan().getTag())) {
				event.getHook().sendMessage("Tienes que ser miembro de Enders ñ clan!").setEphemeral(true).queue();
				return true;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} catch (ClashAPIException e) {
			e.printStackTrace();
			return false;
		}
		if(new QEndersMember().discordSnowflake.eq(event.getMember().getIdLong()).findOne() != null) {
			EndersMember user = new QEndersMember().discordSnowflake.eq(event.getMember().getIdLong()).findOne();
			user.getTags().add(tag);
			user.update();
			event.getHook().sendMessage("Se te ha enlazado la cuenta correctamente!").setEphemeral(true).queue();
			return true;
		}
		EndersMember user = new EndersMember(tag, event.getMember().getIdLong());
		user.save();
		/*
		 * We now set the user's role
		 */
		Role role = switch(player.getTownHallLevel()) {
		case 3 -> TH3;
		case 4 -> TH4;
		case 5 -> TH5;
		case 6 -> TH6;
		case 7 -> TH7;
		case 8 -> TH8;
		case 9 -> TH9;
		case 10 -> TH10;
		case 11 -> TH11;
		case 12 -> TH12;
		case 13 -> TH13;
		case 14 -> TH14;
		default -> TH3;
		};
		event.getGuild().addRoleToMember(event.getMember().getIdLong(), role).queue();
		event.getGuild().removeRoleFromMember(event.getMember().getIdLong(), NON_CHOOSEN).queue();
		event.getHook().sendMessage("Se te ha enlazado la cuenta correctamente!").setEphemeral(true).queue();
		return true;
	}

	@Override
	public String getLabel() {
		return "enderslink";
	}

}
