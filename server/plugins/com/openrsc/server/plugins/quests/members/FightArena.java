package com.openrsc.server.plugins.quests.members;

import com.openrsc.server.constants.ItemId;
import com.openrsc.server.constants.NpcId;
import com.openrsc.server.constants.Quests;
import com.openrsc.server.constants.Skills;
import com.openrsc.server.model.container.Item;
import com.openrsc.server.model.entity.GameObject;
import com.openrsc.server.model.entity.npc.Npc;
import com.openrsc.server.model.entity.player.Player;
import com.openrsc.server.plugins.QuestInterface;
import com.openrsc.server.plugins.triggers.UseLocTrigger;
import com.openrsc.server.plugins.triggers.OpLocTrigger;
import com.openrsc.server.plugins.triggers.KillNpcTrigger;
import com.openrsc.server.plugins.triggers.TalkNpcTrigger;
import com.openrsc.server.util.rsc.DataConversions;

import java.util.Optional;

import static com.openrsc.server.plugins.Functions.*;

public class FightArena implements QuestInterface, TalkNpcTrigger,
	OpLocTrigger,
	UseLocTrigger,
	KillNpcTrigger {

	private static final int GUARDS_CUPBOARD_OPEN = 382;
	private static final int GUARDS_CUPBOARD_CLOSED = 381;

	@Override
	public int getQuestId() {
		return Quests.FIGHT_ARENA;
	}

	@Override
	public String getQuestName() {
		return "Fight Arena (members)";
	}

	@Override
	public boolean isMembers() {
		return true;
	}

	@Override
	public void handleReward(Player player) {
		mes(player, "you have completed the fight arena quest",
			"Lady Servil gives you 1000 gold coins",
			"you gain two quest points");
		give(player, ItemId.COINS.id(), 1000);
		player.message("@gre@You haved gained 2 quest points!");
		int[] questData = player.getWorld().getServer().getConstants().getQuests().questData.get(Quests.FIGHT_ARENA);
		//keep order kosher
		int[] skillIDs = {Skills.ATTACK, Skills.THIEVING};
		for (int i = 0; i < skillIDs.length; i++) {
			questData[Quests.MAPIDX_SKILL] = skillIDs[i];
			incQuestReward(player, questData, i == (skillIDs.length - 1));
		}
		player.getCache().remove("freed_servil");
		player.getCache().remove("killed_ogre");
	}

	@Override
	public boolean blockTalkNpc(Player player, Npc n) {
		return DataConversions.inArray(new int[] {NpcId.LADY_SERVIL.id(), NpcId.LOCAL.id(), NpcId.GUARD_KHAZARD_BRIBABLE.id(), NpcId.GUARD_KHAZARD_BYPRISONER.id(),
				NpcId.GUARD_KHAZARD_MACE.id(), NpcId.JEREMY_SERVIL.id(), NpcId.HENGRAD.id()}, n.getID());
	}

	@Override
	public boolean blockKillNpc(Player player, Npc n) {
		return DataConversions.inArray(new int[] {NpcId.KHAZARD_SCORPION.id(), NpcId.KHAZARD_OGRE.id(),
				NpcId.BOUNCER.id(), NpcId.GENERAL_KHAZARD.id()}, n.getID());
	}

	@Override
	public void onKillNpc(Player player, Npc n) {
		n.killedBy(player);
		n.remove();
		if (n.getID() ==  NpcId.KHAZARD_OGRE.id()) {
			if (!player.getCache().hasKey("killed_ogre")) {
				player.getCache().store("killed_ogre", true);
			}
			player.message("You kill the ogre");
			mes(player, "Jeremy's father survives");
			Npc justin = ifnearvisnpc(player, NpcId.JUSTIN_SERVIL.id(), 15);
			if (justin != null) {
				npcsay(player, justin, "You saved my life and my son's",
					"I am eternally in your debt brave traveller");
			}
			addnpc(player.getWorld(), NpcId.GENERAL_KHAZARD.id(), 613, 708, 60000);
			delay(player.getWorld().getServer().getConfig().GAME_TICK * 2);
			Npc general = ifnearvisnpc(player, NpcId.GENERAL_KHAZARD.id(), 8);
			if (general != null) {
				npcsay(player,
					general,
					"Haha, well done, well done that was rather entertaining",
					"I'm the great General Khazard",
					"And the two men you just saved are my property");
				say(player, general, "They belong to no one");
				npcsay(player, general, "I suppose we could find some arrangement",
					"for their freedom... hmmmm");
				say(player, general, "What do you mean?");
				npcsay(player,
					general,
					"I'll let them go but you must stay and fight for me",
					"You'll make me double the gold if you manage to last a few fights",
					"Guards! take him away!");
				player.message("Khazard's men have locked you in a cell");
				player.teleport(609, 715, false);
			}
		}
		else if (n.getID() == NpcId.KHAZARD_SCORPION.id()) {
			player.message("You defeat the scorpion");
			addnpc(player.getWorld(), NpcId.GENERAL_KHAZARD.id(), 613, 708, 30000);
			delay(player.getWorld().getServer().getConfig().GAME_TICK * 2);
			Npc generalAgain = ifnearvisnpc(player, NpcId.GENERAL_KHAZARD.id(), 15);
			if (generalAgain != null) {
				npcsay(player, generalAgain, "Not bad, not bad at all",
					"I think you need a tougher challenge",
					"Time for my puppy", "Guards, guards bring on bouncer");
			}
			mes(player, "From above you hear a voice...",
				"Ladies and gentlemen!", "Todays second round");
			addnpc(player.getWorld(), NpcId.BOUNCER.id(), 613, 708, 240000);
			player.message("between the Outsider and bouncer");
			Npc bouncer = player.getWorld().getNpcById(NpcId.BOUNCER.id());
			if (bouncer != null) {
				bouncer.setChasing(player);
			}
		}
		else if (n.getID() == NpcId.BOUNCER.id()) {
			player.message("You defeat bouncer");
			addnpc(player.getWorld(), NpcId.GENERAL_KHAZARD.id(), 613, 708, 60000 * 2);
			delay(player.getWorld().getServer().getConfig().GAME_TICK * 2);
			Npc generalAgainAgain = ifnearvisnpc(player, NpcId.GENERAL_KHAZARD.id(), 15);
			if (generalAgainAgain != null) {
				npcsay(player, generalAgainAgain, "nooooo! bouncer, how dare you?",
					"you've taken the life of my only friend!");
				player.message("Khazard looks very angry");
				npcsay(player, generalAgainAgain,
					"now you'll suffer traveller, prepare to meet your maker");
				mes(player, "No, he doesn't look happy at all",
					"You might want to run for it",
					"Go back to lady servil to claim your reward");
				generalAgainAgain.setChasing(player);
			}
			player.updateQuestStage(getQuestId(), 3);
		}
		else if (n.getID() == NpcId.GENERAL_KHAZARD.id()) {
			player.message("You kill general khazard");
			player.message("but he shall return");
		}
	}

	@Override
	public void onTalkNpc(Player player, Npc n) {
		if (n.getID() == NpcId.HENGRAD.id()) {
			if (player.getQuestStage(getQuestId()) == 2
				&& player.getCache().hasKey("killed_ogre")) {
				say(player, n, "Are you ok stranger?");
				npcsay(player, n, "I'm fine thanks, my name's Hengrad",
					"So khazard got his hands on you too?");
				say(player, n, "I'm afraid so");
				npcsay(player, n, "If you're lucky you may last as long as me");
				say(player, n, "How long have you been here?");
				npcsay(player,
					n,
					"I've been in khazard's prisons ever since i can remember",
					"I was a child when his men kidnapped me",
					"My whole life has been spent killing and fighting",
					"All in the hope that one day I'll escape");
				say(player, n, "Don't give up");
				npcsay(player, n, "Thanks friend..wait..sshh,the guard is coming",
					"He'll be taking one of us to the arena");
				mes(player, "A guard approaches the cell");
				npcsay(player, n, "Looks like it's you,good luck friend");
				mes(player, "The guard leads you to the arena",
					"For your battle");
				player.teleport(609, 705, false);
				mes(player, "From above you hear a voice...",
					"Ladies and gentlemen!",
					"Todays first fight between the outsider",
					"And everyone's favorite scorpion has begun");
				addnpc(player.getWorld(), NpcId.KHAZARD_SCORPION.id(), 613, 708, 120000);
				Npc scorp = player.getWorld().getNpcById(NpcId.KHAZARD_SCORPION.id());
				if (scorp != null) {
					scorp.setChasing(player);
				}
			}
		}
		else if (n.getID() == NpcId.JEREMY_SERVIL.id()) {
			if ((player.getQuestStage(getQuestId()) >= 3)
				|| player.getQuestStage(getQuestId()) == -1) {
				player.message("You need to kill the creatures in the arena");
				return;
			}
			if (player.getQuestStage(getQuestId()) == 2
				&& player.getCache().hasKey("freed_servil")) {
				say(player, n, "Jeremy where's your father?");
				npcsay(player, n, "Quick, help him! that beast will kill him",
					"He can't fight! he's too old!");
				mes(player, "You see Jeremy's father Justin",
					"Trying to escape an ogre");
				npcsay(player, n, "Please help him!");
				addnpc(player.getWorld(), NpcId.KHAZARD_OGRE.id(), 613, 708, 60000 * 2);
			}
		}
		else if (n.getID() == NpcId.GUARD_KHAZARD_MACE.id()) {
			if (player.getQuestStage(getQuestId()) == 3
				|| player.getQuestStage(getQuestId()) == -1) {
				say(player, n, "hello");
				npcsay(player, n, "you're the outsider who killed bouncer",
					"die traitor!");
				n.setChasing(player);
				return;
			}
			say(player, n, "hello");
			if (player.getCarriedItems().getEquipment().hasEquipped(ItemId.KHAZARD_HELMET.id())
				&& player.getCarriedItems().getEquipment().hasEquipped(ItemId.KHAZARD_CHAINMAIL.id())) {
				npcsay(player, n, "can i help you stranger?",
					"oh.. you're a guard as well", "that's ok then",
					"we don't like outsiders around here");
			} else {
				npcsay(player, n, "i don't know you stranger", "get of our land");
				n.setChasing(player);
			}
		}
		else if (n.getID() == NpcId.GUARD_KHAZARD_BYPRISONER.id()) {
			if (player.getQuestStage(getQuestId()) >= 2) {
				if (player.getCarriedItems().getEquipment().hasEquipped(ItemId.KHAZARD_HELMET.id())
					&& player.getCarriedItems().getEquipment().hasEquipped(ItemId.KHAZARD_CHAINMAIL.id())) {
					say(player, n, "hello");
					npcsay(player, n, "hello, hope you're keeping busy?");
					say(player, n, "of course");
				} else {
					npcsay(player, n, "this area is restricted, leave now",
						"OUT and don't come back!");
					mes(player, "the guard has thrown you out");
					player.teleport(602, 717, false);
				}
				return;
			}
			say(player, n, "long live General Khazard");
			npcsay(player, n, "erm.. yes.. soldier", "i take it you're new");
			say(player, n, "you could say that");
			npcsay(player, n, "Khazard died two hundred years ago",
				"however his dark spirit remains",
				"in the form of the undead maniac...General Khazard",
				"remember he is your master, always watching",
				"you got that, newbie?");
			say(player, n, "undead, maniac, master, got it - loud and clear");
		}
		else if (n.getID() == NpcId.GUARD_KHAZARD_BRIBABLE.id()) {
			if (player.getQuestStage(getQuestId()) == 3
				|| player.getQuestStage(getQuestId()) == -1) {
				if (player.getCarriedItems().getEquipment().hasEquipped(ItemId.KHAZARD_HELMET.id())
					&& player.getCarriedItems().getEquipment().hasEquipped(ItemId.KHAZARD_CHAINMAIL.id())) {
					say(player, n, "hello");
					npcsay(player, n, "less chat and more work",
						"i can't stand lazy guards");
				} else {
					npcsay(player, n, "this area is restricted, leave now",
						"OUT and don't come back!");
					mes(player, "the guard has thrown you out");
					player.teleport(621, 698, false);
				}
				return;
			}
			if (player.getCache().hasKey("guard_sleeping")
				|| player.getCache().hasKey("freed_servil")) {
				npcsay(player, n, "please, let me rest");
				return;
			}
			if (player.getQuestStage(getQuestId()) == 2) {
				say(player, n, "hello again");
				npcsay(player,
					n,
					"bored, bored, bored",
					"you would think the slaves would be more entertaining",
					"selfish.. the lot of 'em");
				if (player.getCarriedItems().hasCatalogID(ItemId.KHALI_BREW.id(), Optional.of(false))) {
					say(player, n, "do you still fancy a drink?");
					npcsay(player, n,
						"I really shouldn't... ok then, just the one",
						"this stuff looks good");
					player.getCarriedItems().remove(new Item(ItemId.KHALI_BREW.id()));
					mes(player, "the guard takes a mouthful of drink");
					npcsay(player, n, "blimey this stuff is pretty good",
						"it's not too strong is it?");
					say(player, n, "no, not at all, you'll be fine");
					mes(player, "the guard finishes the bottle");
					npcsay(player, n, "that is some gooood stuff",
						"yeah... woooh... yeah");
					mes(player, "the guard seems quite typsy");
					say(player, n, "are you alright?");
					npcsay(player, n, "yeesshh, ooohh, 'hiccup'",
						"maybe i should relax for a while....");
					say(player, n, "good idea, i'll look after the prisoners");
					npcsay(player, n, "ok then, here, 'hiccup',",
						"take these keys",
						"any trouble you give 'em a good beating");
					say(player, n, "no problem, i'll keep them in line");
					npcsay(player, n, "zzzzz zzzzz zzzzz");
					mes(player, "the guard is asleep");
					player.getCache().store("guard_sleeping", true);
					give(player, ItemId.KHAZARD_CELL_KEYS.id(), 1);
				}
				return;
			}
			say(player, n, "long live General Khazard");
			npcsay(player, n, "erm.. yes.. quite right",
				"have you come to laugh at the fight slaves?",
				"i used to really enjoy it",
				"but after a while they become quite boring",
				"now i just want a decent drink",
				"mind you, too much khali brew and i'll fall asleep");
		}
		else if (n.getID() == NpcId.LOCAL.id()) {
			if (player.getQuestStage(getQuestId()) == -1) {
				say(player, n, "hello");
				npcsay(player, n, "please, i haven't done anything");
				say(player, n, "what?");
				npcsay(player, n, "i love General Khazard, please believe me");
				return;
			}
			if (player.getQuestStage(getQuestId()) == 3) {
				say(player, n, "hello");
				npcsay(player, n, "hello stranger",
					"Khazard's got some great fights lined up this week",
					"i can't wait");
				return;
			}
			say(player, n, "hello");
			npcsay(player, n, "are you enjoying the arena?",
				"i heard the servil family are fighting soon",
				"should be very entertaining");
		}
		else if (n.getID() == NpcId.LADY_SERVIL.id()) {
			switch (player.getQuestStage(this)) {
				case 0:
					say(player, n, "hi there, looks like you're in some trouble");
					npcsay(player, n, "oh, i wish this broken cart was my only problem");
					npcsay(player, n, "sob.. i've got to find my family.. sob");
					int first = multi(player, n, "I hope you can, good luck",
						"can i help you?");
					if (first == 0) {
						npcsay(player, n, " sob..sob");
					} else if (first == 1) {
						npcsay(player, n, "sob.. would you? please?",
							"i'm Lady Servil, my husband's Sir Servil",
							"we were travelling north with my son",
							"when we were ambushed by general Khazard's men");
						say(player, n, "general Khazard? i haven't heard of him");
						npcsay(player, n, "he's been after me ever since i",
							"declined his hand in marriage",
							"now he's kidnapped my husband and son",
							"to fight slaves in his",
							"battle arena, to the south of here",
							"i hate to think what he'll do to them",
							"he's a sick, twisted man");
						say(player, n, "I'll try my best to return your family");
						npcsay(player, n, "please do, i'm a wealthy woman",
							"and can reward you handsomely",
							"i'll be waiting for you here");
						player.updateQuestStage(getQuestId(), 1);
					}
					break;
				case 1:
				case 2:
					say(player, n, "hello Lady Servil");
					npcsay(player, n, "Brave traveller, please..bring back my family");
					break;
				case 3:
					say(player, n, "Lady Servil");
					npcsay(player, n, "you're alive, i thought Khazard's men took you",
						"My son and husband are safe and recovering at home",
						"without you they would certainly be dead",
						"I am truly grateful for your service",
						"all i can offer in return is material wealth",
						"please take these coins and enjoy");
					player.sendQuestComplete(Quests.FIGHT_ARENA);
					break;
				case -1:
					say(player, n, "Hello lady Servil");
					npcsay(player, n, "oh hello my dear",
						"my husband and son are resting",
						"while i wait for the cart fixer");
					say(player, n, "hope he's not too long");
					npcsay(player, n, "thanks again for everything");
					break;
			}
		}

	}

	@Override
	public boolean blockOpLoc(Player player, GameObject obj, String command) {
		return (obj.getID() == GUARDS_CUPBOARD_OPEN || obj.getID() == GUARDS_CUPBOARD_CLOSED) && (obj.getY() == 683 || obj.getY() == 1623)
				|| (obj.getID() == 371 && (obj.getY() == 700 || obj.getY() == 707)) || (obj.getID() == 371 && obj.getY() == 716);
	}

	@Override
	public void onOpLoc(Player player, GameObject obj, String command) {
		if ((obj.getID() == GUARDS_CUPBOARD_OPEN || obj.getID() == GUARDS_CUPBOARD_CLOSED) && (obj.getY() == 683 || obj.getY() == 1623)) {
			if (command.equalsIgnoreCase("open")) {
				openCupboard(obj, player, GUARDS_CUPBOARD_OPEN);
			} else if (command.equalsIgnoreCase("close")) {
				closeCupboard(obj, player, GUARDS_CUPBOARD_CLOSED);
			} else {
				if (!player.getCarriedItems().hasCatalogID(ItemId.KHAZARD_CHAINMAIL.id(), Optional.empty())
					&& !player.getCarriedItems().hasCatalogID(ItemId.KHAZARD_HELMET.id(), Optional.empty())
					&& player.getQuestStage(getQuestId()) >= 1) {
					player.message("You search the cupboard...");
					player.message("You find a khazard helmet");
					player.message("You find a khazard chainmail");
					give(player, ItemId.KHAZARD_CHAINMAIL.id(), 1);
					give(player, ItemId.KHAZARD_HELMET.id(), 1);
				} else {
					player.message("You search the cupboard, but find nothing");
				}
			}
		}
		else if (obj.getID() == 371 && (obj.getY() == 700 || obj.getY() == 707)) {
			Npc joe = ifnearvisnpc(player, NpcId.FIGHTSLAVE_JOE.id(), 5);

			if (joe != null) {
				say(player, joe, "are you ok?");
				npcsay(player, joe, "spare me your fake pity",
					"I spit on Khazard's grave and all who do his bidding");
			}
			Npc kelvin = ifnearvisnpc(player, NpcId.FIGHTSLAVE_KELVIN.id(), 5);
			if (kelvin != null) {
				say(player, kelvin, "hello there");
				npcsay(player, kelvin, "get away, get away",
					"one day i'll have my revenge",
					"and i'll have all your heads!");
			}
		}
		else if (obj.getID() == 371 && obj.getY() == 716) {
			if (player.getCache().hasKey("freed_servil")
				|| player.getQuestStage(getQuestId()) == 3
				|| player.getQuestStage(getQuestId()) == -1) {
				player.message("You have already freed jeremy");
				return;
			}
			Npc servil = ifnearvisnpc(player, NpcId.JEREMY_SERVIL.id(), 5);
			Npc guard = ifnearvisnpc(player, NpcId.GUARD_KHAZARD_BYPRISONER.id(), 5);
			if (servil != null && guard != null) {
				if (player.getCache().hasKey("guard_sleeping") && player.getCarriedItems().hasCatalogID(ItemId.KHAZARD_CELL_KEYS.id(), Optional.of(false))) {
					say(player, servil, "Jeremy, look, I have the cell keys");
					npcsay(player, servil, "Wow! Please help me");
					say(player, servil, "ok, keep quiet");
					npcsay(player, servil, "Set me free then we can find dad");
					mes(player, "You use your key to open the cell door",
						"The gate swings open");
					player.playSound("opendoor");
					player.getWorld().replaceGameObject(obj,
						new GameObject(obj.getWorld(), obj.getLocation(), 181, obj
							.getDirection(), obj.getType()));
					player.getWorld().delayedSpawnObject(obj.getLoc(), 3000);
					servil.teleport(605, 718);
					say(player, servil,
						"There you go, now we need to find your father");
					npcsay(player, servil, "I overheard a guard talking",
						"I think they've taken him to the arena");
					say(player, servil, "OK we'd better hurry");
					npcsay(player, servil, " I'll run ahead");
					servil.remove();
					player.getCache().store("freed_servil", true);
					player.getCache().remove("guard_sleeping");
					npcsay(player, guard, "What are you doing?",
						"It's an imposter!");
					delay(player.getWorld().getServer().getConfig().GAME_TICK * 2);
					guard.setChasing(player);
					return;
				}
				npcsay(player, servil, "I'm Jeremy Servil",
					"Please sir, don't hurt me");
				say(player, servil, "I'm here to help",
					"Where do they keep the keys?");
				npcsay(player, servil, "The guard keeps them.. always");
				player.updateQuestStage(getQuestId(), 2);
			}
		}
	}

	@Override
	public boolean blockUseLoc(Player player, GameObject obj, Item item) {
		return obj.getID() == 371 && obj.getY() == 716 && item.getCatalogId() == ItemId.KHAZARD_CELL_KEYS.id();
	}

	@Override
	public void onUseLoc(Player player, GameObject obj, Item item) {
		if (obj.getID() == 371 && obj.getY() == 716 && item.getCatalogId() == ItemId.KHAZARD_CELL_KEYS.id()) {
			player.message("To unlock the gate, left click on it");
		}
	}

}
