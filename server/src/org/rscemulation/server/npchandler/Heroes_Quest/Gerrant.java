/**
* Generated By NPCScript :: A scripting engine created for RSCEmulation by Zilent

script put together by Mr.Zain
*/
package org.rscemulation.server.npchandler.Heroes_Quest;
import org.rscemulation.server.model.Npc;
import org.rscemulation.server.model.Player;
import org.rscemulation.server.model.Quest;
import org.rscemulation.server.npchandler.NpcHandler;



public class Gerrant implements NpcHandler {

	public void handleNpc(final Npc npc, final Player owner) throws Exception {
	
		npc.blockedBy(owner);
		owner.setBusy(true);
		
		Quest q = owner.getQuest(20);
		
		if(q != null) 
		{
			
		} 
	}
	

	
		
	
}