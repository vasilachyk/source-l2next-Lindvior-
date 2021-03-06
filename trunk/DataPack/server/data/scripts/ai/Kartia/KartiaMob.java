package ai.Kartia;

import l2next.commons.collections.GArray;
import l2next.gameserver.ai.CtrlEvent;
import l2next.gameserver.ai.Fighter;
import l2next.gameserver.model.Creature;
import l2next.gameserver.model.instances.NpcInstance;

/**
 * @author KilRoy
 */
public class KartiaMob extends Fighter
{
	public KartiaMob(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected boolean thinkActive()
	{
		NpcInstance actor = getActor();
		if(actor.isDead())
		{
			return false;
		}

		GArray<NpcInstance> arounds = actor.getAroundNpc(3000, 3000);
		if(arounds != null && !arounds.isEmpty())
		{
			for(NpcInstance npc : arounds)
			{
				if(npc.getNpcId() != 33416 && npc.isNpc() && !npc.isInFaction(actor))
				{
					actor.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, npc, 300);
				}
			}
		}

		return true;
	}

	@Override
	protected void onEvtAttacked(Creature attacker, int damage)
	{
		if(attacker == null)
		{
			return;
		}

		super.onEvtAttacked(attacker, damage);
	}

	@Override
	public int getMaxAttackTimeout()
	{
		return 0;
	}

	@Override
	protected boolean maybeMoveToHome()
	{
		return false;
	}

	@Override
	protected boolean randomWalk()
	{
		return false;
	}
}