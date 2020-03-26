package com.openrsc.server.login;

import com.openrsc.server.Server;
import com.openrsc.server.database.impl.mysql.queries.logging.SecurityChangeLog;
import com.openrsc.server.database.struct.PlayerLoginData;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.net.rsc.ActionSender;
import com.openrsc.server.util.rsc.DataConversions;
import io.netty.channel.Channel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Used to run change password functionality on the Login thread
 *
 * @author Kenix
 */
public class PasswordChangeRequest extends LoginExecutorProcess {

	/**
	 * The asynchronous logger.
	 */
	private static final Logger LOGGER = LogManager.getLogger();

	private final Server server;
	private final Channel channel;
	private Player player;
	private String oldPassword;
	private String newPassword;

	public PasswordChangeRequest(final Server server, final Channel channel, final Player player, final String oldPassword, final String newPassword) {
		this.server = server;
		this.channel = channel;
		this.setPlayer(player);
		this.setOldPassword(oldPassword);
		this.setNewPassword(newPassword);
	}

	public Player getPlayer() {
		return player;
	}

	private void setPlayer(Player player) {
		this.player = player;
	}

	public String getOldPassword() {
		return oldPassword;
	}

	private void setOldPassword(final String oldPassword) {
		this.oldPassword = oldPassword;
	}

	public String getNewPassword() {
		return newPassword;
	}

	private void setNewPassword(final String newPassword) {
		this.newPassword = newPassword;
	}

	public Server getServer() {
		return server;
	}

	public Channel getChannel() {
		return channel;
	}

	protected void processInternal() {
		LOGGER.info("Password change attempt from: " + getPlayer().getCurrentIP());

		try {
			PlayerLoginData playerData = getServer().getDatabase().getPlayerLoginData(player.getUsername());
			String lastDBPass = playerData.password;
			String DBsalt = playerData.salt;
			String newDBPass;
			int playerID = getPlayer().getID();
			if (!DataConversions.checkPassword(getOldPassword(), DBsalt, lastDBPass)) {
				LOGGER.info(getPlayer().getCurrentIP() + " - Pass change failed: The current password did not match players record.");
				ActionSender.sendMessage(getPlayer(), "No changes made, your current password did not match");
				return;
			}
			newDBPass = DataConversions.hashPassword(getNewPassword(), DBsalt);
			getServer().getDatabase().saveNewPassword(playerID, newDBPass);

			String lastPw, earlierPw;
			try {
				earlierPw = getServer().getDatabase().getPreviousPassword(playerID);
			} catch (Exception e) {
				earlierPw = "";
			}
			lastPw = lastDBPass;

			getServer().getDatabase().savePreviousPasswords(playerID, lastPw, earlierPw);

			getServer().getGameLogger().addQuery(new SecurityChangeLog(getPlayer(), SecurityChangeLog.ChangeEvent.PASSWORD_CHANGE,
				"From: " + lastDBPass + ", To: " + newDBPass));
			ActionSender.sendMessage(getPlayer(), "Your password was successfully changed!");
			LOGGER.info(getPlayer().getCurrentIP() + " - Password change successful");

		} catch (Exception e) {
			LOGGER.catching(e);
		}
	}
}
