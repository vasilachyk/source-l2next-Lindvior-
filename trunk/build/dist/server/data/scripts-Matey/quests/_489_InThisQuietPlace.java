package quests;

import l2next.gameserver.model.Player;
import l2next.gameserver.model.instances.NpcInstance;
import l2next.gameserver.model.quest.Quest;
import l2next.gameserver.model.quest.QuestState;
import l2next.gameserver.scripts.ScriptFile;
import org.apache.commons.lang3.ArrayUtils;

//By Darvin la2era.ru
public class _489_InThisQuietPlace extends Quest implements ScriptFile
{
	//Шанс дропа
	private static final int chance = 35;
	//Квест итем
	private static final int TraceofEvil = 19501;
	//Монстры
	private static final int[] mobstohunt = {
		21646,
		21647,
		21648,
		21649,
		21650,
		21651
	};
	//НПСы
	private static final int bastian = 31280;
	private static final int Adventurequid = 33463;

	@Override
	public void onLoad()
	{
	}

	@Override
	public void onReload()
	{
	}

	@Override
	public void onShutdown()
	{
	}

	public _489_InThisQuietPlace()
	{
		super(false);
		addStartNpc(Adventurequid);
		addTalkId(Adventurequid);
		addTalkId(bastian);
		addKillId(mobstohunt);
		addQuestItem(TraceofEvil);

		addLevelCheck(75, 79);
	}

	@Override
	public String onEvent(String event, QuestState st, NpcInstance npc)
	{
		String htmltext = event;
		if(event.equalsIgnoreCase("quest_ac"))
		{
			st.setState(STARTED);
			st.setCond(1);
			st.playSound(SOUND_ACCEPT);
			htmltext = "0-4.htm";
		}
		if(event.equalsIgnoreCase("qet_rev"))
		{
			htmltext = "1-3.htm";
		}
		return htmltext;
	}

	@Override
	public String onTalk(NpcInstance npc, QuestState st)
	{
		int cond = st.getCond();
		int npcId = npc.getNpcId();
		String htmltext = "noquest";

		if(npcId == Adventurequid)
		{
			if(cond == 0)
			{
				if(isAvailableFor(st.getPlayer()))
				{
					if(st.isNowAvailableByTime())
					{
						htmltext = "0-1.htm";
					}
					else
					{
						htmltext = "0-c.htm";
					}
				}
				else
				{
					htmltext = "0-nc.htm";
				}
			}
			else if(cond == 1 || cond == 2)
			{
				htmltext = "0-5.htm";
			}

		}
		else if(npcId == bastian)
		{
			if(cond == 0)
			{
				if(isAvailableFor(st.getPlayer()))
				{
					if(st.isNowAvailableByTime())
					{
						htmltext = TODO_FIND_HTML;
					}
					else
					{
						htmltext = "1-c.htm";
					}
				}
				else
				{
					htmltext = "1-nc.htm";
				}
			}
			else if(cond == 1)
			{
				htmltext = TODO_FIND_HTML;
			}
			else if(cond == 2)
			{
				htmltext = "1-1.htm";
				st.getPlayer().addExpAndSp(19890000, 22602330);
				st.giveItems(57, 426045);
				st.takeAllItems(TraceofEvil);
				st.exitCurrentQuest(this);
				st.playSound(SOUND_FINISH);
			}
		}
		return htmltext;
	}

	@Override
	public String onKill(NpcInstance npc, QuestState st)
	{
		int npcId = npc.getNpcId();

		if(st.getCond() == 1 && ArrayUtils.contains(mobstohunt, npcId) && st.getQuestItemsCount(TraceofEvil) < 77)
		{
			st.rollAndGive(TraceofEvil, 1, chance);
			st.playSound(SOUND_ITEMGET);
		}
		if(st.getQuestItemsCount(TraceofEvil) >= 77)
		{
			st.setCond(2);
			st.playSound(SOUND_MIDDLE);
		}
		return null;
	}

	@Override
	public boolean isVisible(Player player)
	{
		boolean aval = true;
		if(!isAvailableFor(player))
			aval = false;
			
		return aval;
	}
}