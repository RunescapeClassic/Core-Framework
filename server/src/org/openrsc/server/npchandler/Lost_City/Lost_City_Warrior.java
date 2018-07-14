/**
* Generated By NPCScript :: A scripting engine created for openrsc by Zilent

script put together by Mr.Zain
*/
package org.openrsc.server.npchandler.Lost_City;

import org.openrsc.server.Config;
import org.openrsc.server.event.DelayedQuestChat;
import org.openrsc.server.event.SingleEvent;
import org.openrsc.server.model.ChatMessage;
import org.openrsc.server.model.MenuHandler;
import org.openrsc.server.model.Npc;
import org.openrsc.server.model.Player;
import org.openrsc.server.model.Quest;
import org.openrsc.server.model.World;
import org.openrsc.server.npchandler.NpcHandler;



public class Lost_City_Warrior implements NpcHandler {



	public void handleNpc(final Npc npc, final Player owner) throws Exception {
		npc.blockedBy(owner);
		owner.setBusy(true);
		Quest q = owner.getQuest(Config.Quests.LOST_CITY);
		if(q != null) {
			if(q.finished()) {
				finished(npc, owner);
			} else {
				switch(q.getStage()) {
					case 0:
						noQuestStarted(npc, owner);
						break;
					case 1:
						questStarted(npc, owner);
						break;
					case 2:
						questStarted2(npc, owner);
						break;
					default:
						finished(npc, owner);
				}
			}
		} else {
			noQuestStarted(npc, owner);
		}
	}
	
		
	private void noQuestStarted(final Npc npc, final Player owner) {
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Hello there fellow adventurer", "What business do you have here?"},true) {
			public void finished() {
				World.getDelayedEventHandler().add(new SingleEvent(owner, 1500) {
					public void action() {
						final String[] options107 = {"Who are you?", "I'm in search of a quest", "What are you doing here?"};
						owner.setBusy(false);
						owner.sendMenu(options107);
						owner.setMenuHandler(new MenuHandler(options107) {
							public void handleReply(final int option, final String reply) {
								owner.setBusy(true);
								for(Player informee : owner.getViewArea().getPlayersInView()) {
									informee.informOfChatMessage(new ChatMessage(owner, reply, npc));
								}
								switch(option) {
									case 0:
										whoAreYou(npc, owner);
										break;
									case 1:
										altQuest(npc, owner);
										break;
									case 2:
										doingHere(npc, owner);
										break;
								}
							}
						});
					}
				});
			}
		});
	}		
	
	
	
	
	private void whoAreYou(final Npc npc, final Player owner) {
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"I am a warrior, a master of melee combat", "Why are you here adventurer?"}) {
			public void finished() {
				World.getDelayedEventHandler().add(new SingleEvent(owner, 1500) {
					public void action() {
						final String[] options101 = {"I'm in search of a quest", "What are you doing here?", "No reason, I'll be on my way now"};
						owner.setBusy(false);
						owner.sendMenu(options101);
						owner.setMenuHandler(new MenuHandler(options101) {
							public void handleReply(final int option, final String reply) {
								owner.setBusy(true);
								for(Player informee : owner.getViewArea().getPlayersInView()) {
									informee.informOfChatMessage(new ChatMessage(owner, reply, npc));
								}
								switch(option) {
									case 0:
										altQuest(npc, owner);
										break;
									case 1:
										doingHere(npc, owner);
										break;
									case 2:
										iLeave(npc, owner);
										break;
								}
							}
						});
					}
				});
			}
		});
	}
	
	
	private void doingHere(final Npc npc, final Player owner) {
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Well our group is looking for a magical lost city", "I'm not going to share any information though", "because we want it for ourselves."}) {
			public void finished() {
				World.getDelayedEventHandler().add(new SingleEvent(owner, 1500) {
					public void action() {
						final String[] options102 = {"Oh come on, can you tell me anything?", "Alright, I'll be on my way now"};
						owner.setBusy(false);
						owner.sendMenu(options102);
						owner.setMenuHandler(new MenuHandler(options102) {
							public void handleReply(final int option, final String reply) {
								owner.setBusy(true);
								for(Player informee : owner.getViewArea().getPlayersInView()) {
									informee.informOfChatMessage(new ChatMessage(owner, reply, npc));
								}
								switch(option) {
									case 0:
										altQuest(npc, owner);
										break;
									case 1:
										iLeave(npc, owner);
										break;
								}
							}
						});
					}
				});
			}
		});
	}
	
	
	private void questStarted(final Npc npc, final Player owner) {
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Hey you're the one who has been asking about the lost city", "I'm not going to share any information though", "because we want it for ourselves."}, true) {
			public void finished() {
				World.getDelayedEventHandler().add(new SingleEvent(owner, 1500) {
					public void action() {
						final String[] options102 = {"Oh come on, can you tell me anything?", "Alright, I'll be on my way now"};
						owner.setBusy(false);
						owner.sendMenu(options102);
						owner.setMenuHandler(new MenuHandler(options102) {
							public void handleReply(final int option, final String reply) {
								owner.setBusy(true);
								for(Player informee : owner.getViewArea().getPlayersInView()) {
									informee.informOfChatMessage(new ChatMessage(owner, reply, npc));
								}
								switch(option) {
									case 0:
										quest(npc, owner);
										break;
									case 1:
										iLeave(npc, owner);
										break;
								}
							}
						});
					}
				});
			}
		});
	}
	
	

	private final void questStarted2(final Npc npc, final Player owner) {
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Hey have you found the leprechaun yet?"}, true) {
			public void finished() {
				World.getDelayedEventHandler().add(new DelayedQuestChat(owner, npc, new String[] {"No I haven't"}) {
					public void finished() {
						World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"He should be hiding in one of the trees around here"}) {
							public void finished() {
								owner.setBusy(false);
								npc.unblock();
							}
						});
					}
				});
			}
		});
	}
	
	
	private final void quest(final Npc npc, final Player owner) {
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"I suppose you could help me find the leprechaun", "He is hiding in one of these trees", "legend says that he knows how to get into zanaris."}) {
			public void finished() {
				World.getDelayedEventHandler().add(new DelayedQuestChat(owner, npc, new String[] {"Zanaris?", "Is that the name of the lost city?"}) {
					public void finished() {
						World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"uh-oh, I probably shouldn't have said that!", "Please don't tell the others that I told you"}) {
							public void finished() {
								World.getDelayedEventHandler().add(new DelayedQuestChat(owner, npc, new String[] {"Thats okay buddy", "It will just be between me and you okay?"}) {
									public void finished() {
										World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"The others are going to kill me."}) {
											public void finished() {
											owner.sendMessage("The warrior starts sobbing.");
											owner.setBusy(false);
											npc.unblock();
											owner.incQuestCompletionStage(Config.Quests.LOST_CITY);
											}
										});
									}
								});	
							}
						});
					}
				});
			}
		});
	}
	
	private final void altQuest(final Npc npc, final Player owner) {
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"I suppose you could help me find the leprechaun", "He is hiding in one of these trees", "legend says that he knows how to get into zanaris."}) {
			public void finished() {
				World.getDelayedEventHandler().add(new DelayedQuestChat(owner, npc, new String[] {"Zanaris?", "Is that the name of the lost city?"}) {
					public void finished() {
						World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"uh-oh, I probably shouldn't of said that!", "Please don't tell the others that I told you"}) {
							public void finished() {
								World.getDelayedEventHandler().add(new DelayedQuestChat(owner, npc, new String[] {"Thats okay buddy", "It will just be between me and you okay?"}) {
									public void finished() {
										World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"The others are going to kill me."}) {
											public void finished() {
											owner.sendMessage("The warrior starts sobbing.");
											owner.setBusy(false);
											npc.unblock();
											owner.addQuest(Config.Quests.LOST_CITY, 2);
											owner.incQuestCompletionStage(Config.Quests.LOST_CITY);
											owner.incQuestCompletionStage(Config.Quests.LOST_CITY); 
											}
										});
									}
								});	
							}
						});
					}
				});
			}
		});
	}
	
	private void iLeave(final Npc npc, final Player owner) {
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Farewell adventurer"}) {
			public void finished() {
				owner.setBusy(false);
				npc.unblock();
			}
		});
	}
	
	
	private final void finished(final Npc npc, final Player owner) {
		World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"oh its you","did you find out anything about zanaris?"}, true) {
			public void finished() {
				World.getDelayedEventHandler().add(new DelayedQuestChat(owner, npc, new String[] {"I think I'll keep that to myself"}) {
					public void finished() {
						owner.setBusy(false);
						npc.unblock();
					}
				});
			}
		});
	}


	
	
	
	
}