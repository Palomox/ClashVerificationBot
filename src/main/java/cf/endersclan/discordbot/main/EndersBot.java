package cf.endersclan.discordbot.main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import javax.persistence.Id;
import javax.security.auth.login.LoginException;

import com.lycoon.clashapi.cocmodels.player.Player;
import com.lycoon.clashapi.core.exception.ClashAPIException;

import cf.endersclan.discordbot.coc.CocApiManager;
import cf.endersclan.discordbot.commands.CommandManager;
import cf.endersclan.discordbot.commands.LinkCommand;
import cf.endersclan.discordbot.listeners.CommandListener;
import cf.endersclan.discordbot.persistence.query.QEndersMember;
import cf.endersclan.discordbot.scheduler.Scheduler;
import io.ebean.DB;
import io.ebean.DatabaseFactory;
import io.ebean.config.DatabaseConfig;
import io.ebean.config.dbplatform.postgres.PostgresPlatform;
import io.ebean.dbmigration.DbMigration;
import io.ebean.migration.MigrationConfig;
import io.ebean.migration.MigrationRunner;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.hooks.AnnotatedEventManager;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.privileges.CommandPrivilege;

public class EndersBot {
	private JDA jda;
	private CocApiManager apiManager;
	private Properties properties;
	private CommandManager commandManager;
	private Scheduler scheduler;
	public static EndersBot instance;
	public static final String ENDERSTAG = "#P9PRJCUC";

	public static void main(String[] args) {
		instance = new EndersBot();
		instance.start();
	}

	public void start() {
		long start = System.currentTimeMillis();
		this.properties = new Properties();
		File propertiesFile = new File(System.getProperty("user.dir"), "bot.properties");
		if(!propertiesFile.exists()) {
			try {
				propertiesFile.createNewFile();
				FileOutputStream fileOutput = new FileOutputStream(propertiesFile);
				this.properties.setProperty("botToken", "null");
				this.properties.setProperty("clashToken", "null");
				this.properties.store(fileOutput, "AL2.0's properties file");
				fileOutput.close();
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
		}
		FileReader reader;
		try {
			reader = new FileReader(propertiesFile);
			this.properties.load(reader);
			this.jda = JDABuilder.createDefault(this.properties.getProperty("botToken"))
					.setEventManager(new AnnotatedEventManager())
					.addEventListeners(new CommandListener())
					.build().awaitReady();
			this.apiManager = new CocApiManager(this.properties.getProperty("clashToken"));
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		} catch (IOException e) {
			e.printStackTrace();
			return;
		} catch (LoginException e) {
			e.printStackTrace();
			return;
		} catch (InterruptedException e) {
			e.printStackTrace();
			return;
		}
		this.registerCommands();
		this.commandManager = new CommandManager(new LinkCommand());


		DbMigration migration = DbMigration.create();
		migration.setPlatform(new PostgresPlatform());
		migration.setVersion("1.0");
		migration.setName("Initial version");
		try {
			migration.generateMigration();
		} catch (IOException e) {
			e.printStackTrace();
		}

		DatabaseConfig cfg = new DatabaseConfig();

		Properties properties = new Properties();
		properties.put("datasource.db.username", this.properties.get("datasource.db.username"));
		properties.put("datasource.db.password", this.properties.get("datasource.db.password"));
		properties.put("datasource.db.databaseUrl", this.properties.get("datasource.db.databaseUrl"));
		properties.put("datasource.db.databaseDriver", this.properties.get("datasource.db.databaseDriver"));


		cfg.loadFromProperties(properties);
		cfg.setRegister(true);
		cfg.setDefaultServer(true);
		DatabaseFactory.create(cfg);

		MigrationConfig config = new MigrationConfig();
		config.setMigrationPath("classpath:dbmigration");

		MigrationRunner runner = new MigrationRunner(config);
		runner.run(DB.getDefault().getDataSource());

		this.loadSchedules();

		System.out.println(String.format("Bot started in %d ms", System.currentTimeMillis()-start));
	}
	private void loadSchedules() {
		this.scheduler = new Scheduler();
		/*
		 * Start scheduling tasks
		 */
		this.scheduler.scheduleEveryDay(() -> {
			// Checking if members are linked AND in the clan AND if they're leaders or coleaders let them be.
			List<List<String>> registeredMembers = new QEndersMember().findList().stream().map(endersMember -> endersMember.getTags()).collect(Collectors.toList());
			for(List<String> accounts : registeredMembers) {
				for(String account : accounts) {
					try{
						Player player = this.getApiManager().getApi().getPlayer(account);
						if(ENDERSTAG.equals(player.getClan().getTag())) {
							break;
							//TODO: ACTUALLY DO THIS A
						}
					}catch (ClashAPIException | IOException e) {
						e.printStackTrace();
					}
					}

				}
		}, 0, 0, 0);
	}

	public void registerCommands() {
		jda.updateCommands().addCommands(
				new CommandData("enderslink", "enlaza tu cuenta de coc con la de discord").setDefaultEnabled(false)
					.addOptions(new OptionData(OptionType.STRING, "tag", "Tu tag en coc", true),
								new OptionData(OptionType.STRING, "token", "Tu token del api de coc", true)))
		.queue();
		jda.retrieveCommands().queue(list -> {
			if(jda.getSelfUser().getIdLong() == 862348777711730718L) {
				list.get(0).updatePrivileges(jda.getGuilds().get(0), CommandPrivilege.enableRole(577096607997362226L)).queue();
			}else {
				list.get(0).updatePrivileges(jda.getGuilds().get(0), CommandPrivilege.enableRole(577094850852290567L)).queue();
			}
		});
	}

	public JDA getJda() {
		return jda;
	}

	public CocApiManager getApiManager() {
		return apiManager;
	}

	public CommandManager getCommandManager() {
		return commandManager;
	}

}
