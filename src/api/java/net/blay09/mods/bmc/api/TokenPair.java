package net.blay09.mods.bmc.api;

public class TokenPair {
	private final String username;
	private final String token;

	public TokenPair(String username, String token) {
		this.username = username;
		this.token = token;
	}

	public String getUsername() {
		return username;
	}

	public String getToken() {
		return token;
	}
}
