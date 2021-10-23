package cf.endersclan.discordbot.coc;

import com.lycoon.clashapi.core.ClashAPI;

public class CocApiManager {
	private ClashAPI api;

	public CocApiManager(String token) {
		this.api = new ClashAPI(token);
	}

	public ClashAPI getApi() {
		return this.api;
	}
}
