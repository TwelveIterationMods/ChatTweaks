package net.blay09.mods.bmc.api.chat;

public interface IChatChannel {

	String getName();

	void clearChat();

	boolean hasUnreadMessages();

	void setOutgoingPrefix(String outgoingPrefix);

	String getOutgoingPrefix();

	void setMuted(boolean muted);

	boolean isMuted();

	boolean isHidden();

	void addManagedChatLine(IChatMessage message);

	IChatMessage getChatLine(int id);

	void setFilterPattern(String pattern);

	String getFilterPattern();

	boolean isExclusive();

	void setExclusive(boolean exclusive);

	boolean isShowTimestamp();

	MessageStyle getMessageStyle();

	void setMessageStyle(MessageStyle messageStyle);

	void setFormat(String format);

	IChatChannel getDisplayChannel();

	void setDisplayChannel(IChatChannel channel);

	boolean isTemporary();

	void setTemporary(boolean temporary);

	void disableDefaultNameTransformer();
}
