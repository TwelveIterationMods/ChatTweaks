package net.blay09.mods.chattweaks.auth;

public class TokenPair {
	private final String username;
	private final String token;
	private final boolean doNotStore;

	public TokenPair(String username, String token, boolean doNotStore) {
		this.username = username;
		this.token = token;
		this.doNotStore = doNotStore;
	}

	public String getUsername() {
		return username;
	}

	public String getToken() {
		return token;
	}

	public boolean isDoNotStore() {
		return doNotStore;
	}
}
