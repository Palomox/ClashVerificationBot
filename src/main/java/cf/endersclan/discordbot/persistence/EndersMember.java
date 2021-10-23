package cf.endersclan.discordbot.persistence;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;

import io.ebean.Model;

@Entity
public class EndersMember extends Model {
	@Id
	private long discordSnowflake;
	@ElementCollection
	private List<String> tags;

	public EndersMember(String tag, long discordId) {
		this.tags = new ArrayList<>();
		this.tags.add(tag);

		this.discordSnowflake = discordId;
	}

	public EndersMember() {

	}

	public long getDiscordSnowflake() {
		return discordSnowflake;
	}

	public List<String> getTags() {
		return this.tags;
	}

}
