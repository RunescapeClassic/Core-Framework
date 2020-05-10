package com.openrsc.server.plugins.itemactions;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.Skills;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.triggers.UseLocTrigger;
import com.openrsc.server.util.rsc.Formulae;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class SpinningWheel implements UseLocTrigger {

	@Override
	public boolean blockUseLoc(Player player, GameObject obj, Item item) {
		return obj.getID() == 121;
	}

	@Override
	public void onUseLoc(Player player, GameObject obj, final Item item) {
		int produceID = -1;
		int requiredLevel = -1;
		int experience = -1;
		String verb, consumedItem, producedItem;

		if (item.getCatalogId() == ItemId.WOOL.id()) {
			produceID = ItemId.BALL_OF_WOOL.id();
			requiredLevel = 1;
			experience = 10;
			verb = "spin";
			consumedItem = "sheeps wool";
			producedItem = "nice ball of wool";
		} else if (item.getCatalogId() == ItemId.FLAX.id()) {
			produceID = ItemId.BOW_STRING.id();
			requiredLevel = 10;
			experience = 60;
			verb = "make";
			consumedItem = "flax";
			producedItem = "bow string";
		} else {
			player.message("Nothing interesting happens");
			return;
		}

		if (produceID == -1) {
			return;
		}
		int repeat = 1;
		if (player.getWorld().getServer().getConfig().BATCH_PROGRESSION) {
			repeat = Formulae.getRepeatTimes(player, Skills.CRAFTING);
		}

		String resultString = "You " + verb + " the " + consumedItem + " into a " + producedItem;
		batchSpin(player, item, resultString, produceID, requiredLevel, experience, repeat);
	}

	private void batchSpin(Player player, Item item, String resultString, int resultCatalogID, int requiredLevel, int experience, int repeat) {
		if (player.getSkills().getLevel(Skills.CRAFTING) < requiredLevel) {
			mes(player, "You need to have a crafting of level "
				+ requiredLevel + " or higher to make a "
				+ new Item(resultCatalogID).getDef(player.getWorld()).getName().toLowerCase());
			return;
		}
		if (player.getWorld().getServer().getConfig().WANT_FATIGUE) {
			if (player.getWorld().getServer().getConfig().STOP_SKILLING_FATIGUED >= 2
				&& player.getFatigue() >= player.MAX_FATIGUE) {
				player.message("You are too tired to craft");
				return;
			}
		}

		item = player.getCarriedItems().getInventory().get(
			player.getCarriedItems().getInventory().getLastIndexById(item.getCatalogId(), Optional.of(false))
		);
		if (item == null) return;

		player.getCarriedItems().remove(item);
		thinkbubble(player, item);
		player.playSound("mechanical");
		player.message(resultString);
		player.getCarriedItems().getInventory().add(new Item(resultCatalogID, 1));
		player.incExp(Skills.CRAFTING, experience, true);
		delay(player.getWorld().getServer().getConfig().GAME_TICK);

		// Repeat
		if (!ifinterrupted() && --repeat > 0) {
			batchSpin(player, item, resultString, resultCatalogID, requiredLevel, experience, repeat);
		}
	}
}
