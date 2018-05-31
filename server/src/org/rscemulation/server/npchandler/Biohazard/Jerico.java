/**
* Generated By NPCScript :: A scripting engine created for RSCEmulation by Zilent
*/
//npc ID 486
package org.rscemulation.server.npchandler.Biohazard;
import org.rscemulation.server.event.DelayedQuestChat;
import org.rscemulation.server.model.Npc;
import org.rscemulation.server.model.Player;
import org.rscemulation.server.model.Quest;
import org.rscemulation.server.model.World;
import org.rscemulation.server.npchandler.NpcHandler;



public class Jerico implements NpcHandler
 {
	public void handleNpc(final Npc npc, final Player owner) throws Exception
	{
		npc.blockedBy(owner);
		owner.setBusy(true);
		
		Quest q = owner.getQuest(38);
		Quest plagueCity = owner.getQuest(35);
		
		if(q != null) 
		{
			if(q.finished()) 
			{
				noQuestStarted(npc, owner);
			}
			else 
			{
				switch(q.getStage())
				{
					case 1:
						questStage1(npc, owner);
					break;
					case 2:
						questStage2(npc, owner);
					break;
					default:
						noQuestStarted(npc, owner);
				}
			}
		} 
		else
		{
			noQuestStarted(npc, owner);
		}
	}
	
	private void noQuestStarted(final Npc npc, final Player owner)
	{
		World.getDelayedEventHandler().add(new DelayedQuestChat(owner, npc, new String[] {"Hello"}, true)
		{
			public void finished()
			{	
				World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Can I help you?"})
				{
					public void finished()
					{	
						World.getDelayedEventHandler().add(new DelayedQuestChat(owner, npc, new String[] {"Just passing by"})
						{
							public void finished()
							{
								owner.setBusy(false);
								npc.unblock();
							}
						});
					}
				});
			}	
		});	
	}
	
	private void questStage1(final Npc npc, final Player owner) 
	{
		World.getDelayedEventHandler().add(new DelayedQuestChat(owner, npc, new String[] {"Hello Jerico"}, true) 
		{
			public void finished()
			{
				World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Hello, I've been expecting you", "Elena tells me you need to cross the wall"}) 
				{
					public void finished()
					{
						World.getDelayedEventHandler().add(new DelayedQuestChat(owner, npc, new String[] {"That's right"}) 
						{
							public void finished()
							{
								World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"My messenger pigeons help me communicate with friends over the wall", "I have arranged for two friends to aid you with a rope ladder", "Omart is waiting for you at the southend of the wall", "Be careful, if the mourners catch you the punishment will be severe"}) 
								{
									public void finished()
									{
										World.getDelayedEventHandler().add(new DelayedQuestChat(owner, npc, new String[] {"Thanks Jerico"}) 
										{
											public void finished()
											{
												owner.incQuestCompletionStage(38);
												owner.setBusy(false);
												npc.unblock();
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
	
	private void questStage2(final Npc npc, final Player owner) 
	{
		World.getDelayedEventHandler().add(new DelayedQuestChat(owner, npc, new String[] {"Hello Jerico"}, true) 
		{
			public void finished()
			{
				World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Hello again", "You'll need someway to distract the watch tower", "Otherwise you'll be caught for sure"}) 
				{
					public void finished()
					{
						World.getDelayedEventHandler().add(new DelayedQuestChat(owner, npc, new String[] {"Any ideas?"}) 
						{
							public void finished()
							{
								World.getDelayedEventHandler().add(new DelayedQuestChat(npc, owner, new String[] {"Sorry, try asking Omart", "I really must get back to feeding the messenger birds"}) 
								{
									public void finished()
									{
										owner.setBusy(false);
										npc.unblock();
									}
								});
							}
						});
					}
				});
			}
		});
	}
}