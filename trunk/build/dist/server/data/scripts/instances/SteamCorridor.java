package instances;

import l2next.commons.threading.RunnableImpl;
import l2next.gameserver.ThreadPoolManager;
import l2next.gameserver.listener.actor.OnDeathListener;
import l2next.gameserver.listener.zone.OnZoneEnterLeaveListener;
import l2next.gameserver.model.Creature;
import l2next.gameserver.model.Player;
import l2next.gameserver.model.Zone;
import l2next.gameserver.model.entity.Reflection;
import l2next.gameserver.model.instances.NpcInstance;
import l2next.gameserver.network.serverpackets.ExSendUIEvent;
import l2next.gameserver.network.serverpackets.ExShowScreenMessage;
import l2next.gameserver.network.serverpackets.SystemMessage2;
import l2next.gameserver.network.serverpackets.components.NpcString;
import l2next.gameserver.network.serverpackets.components.SystemMsg;
import l2next.gameserver.utils.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Класс инстанса на парнасе.
 *
 * @author Awakeninger
 * Спаун мобов в *.xml
 * Рб здесь, т.к. к нему деф листнер
23013	u,Перехватчик Махона\0	a,	9C	E8	A9	-1
23014	u,Соперник Махона\0	a,	9C	E8	A9	-1
23015	u,Мастер Меча Махона\0	a,	9C	E8	A9	-1
23016	u,Разоритель Махона\0
//18381 - ловушка
//Сообщени: Открылся портал ведущий в следущую комнату.
 */

public class SteamCorridor extends Reflection
{
	private long _savedTime;
	private DeathListener _deathListener = new DeathListener();
	private ZoneListener _epicZoneListener = new ZoneListener();
	private final int Kechi = 25797;
	private static final Logger _log = LoggerFactory.getLogger(SteamCorridor.class);
	private static final String[] zones = {
		"[Steam1to2]",
		"[Steam2to3]",
		"[Steam3to4]",
		"[Steam4to5]",
		"[Steam5toBoss]"
	};

	@Override
	protected void onCreate()
	{
		super.onCreate();
		//Пробуем хотя бы на одной зоне. Не забываем, что зона в рефлекте.
		getZone("[Steam1to2]").addListener(_epicZoneListener);//NPE
		getZone("[Steam2to3]").addListener(_epicZoneListener);//NPE
		getZone("[Steam3to4]").addListener(_epicZoneListener);//NPE
		getZone("[Steam4to5]").addListener(_epicZoneListener);//NPE
		getZone("[Steam5toBoss]").addListener(_epicZoneListener);//NPE
	}

	@Override
	protected void onCollapse()
	{
		super.onCollapse();
	}
	//Раскоментить когда допишу.
	//private boolean checkstartCond(int raidplayers)
	//{
	//	return !(raidplayers < getInstancedZone().getMinParty() || _startLaunched);
	//}										

	@Override
	public void onPlayerEnter(Player player)
	{
		super.onPlayerEnter(player);
		_savedTime = System.currentTimeMillis();
		player.sendPacket(new ExSendUIEvent(player, 0, 1, (int) (System.currentTimeMillis() - _savedTime) / 1000, 0, NpcString.ELAPSED_TIME));//Пускаем таймер
		NpcInstance Boss = addSpawnWithoutRespawn(Kechi, new Location(154088, 215128, -12152, 31900), 0);//Спавним финального босса
		Boss.addListener(_deathListener);//Вешаем листнер на финального босса
		getDoor(24240061).openMe();
		getDoor(24240023).openMe();
	}

	private class DeathListener implements OnDeathListener
	{
		@Override
		public void onDeath(Creature self, Creature killer)
		{
			if(self.isNpc() && self.getNpcId() == Kechi)//Если убитый нпц - финальный босс, то обрубаем таймер и ставим отсчет на закрытие инстанса.
			{
				for(Player p : getPlayers())
				{
					p.sendPacket(new ExSendUIEvent(p, 1, 1, 0, 0));
					p.sendPacket(new SystemMessage2(SystemMsg.THIS_DUNGEON_WILL_EXPIRE_IN_S1_MINUTES).addInteger(5));
					startCollapseTimer(5 * 60 * 1000L);
					setReenterTime(System.currentTimeMillis());
					return;
				}
			}
		}
	}

	public class ZoneListener implements OnZoneEnterLeaveListener
	{
		@Override
		public void onZoneEnter(Zone zone, Creature cha)//Вход в зону.
		{
			Player player = cha.getPlayer();
			if(player == null || !cha.isPlayer())//если персонаж присутствует и если это ТОЛЬКО ПЕРСОНАЖ продолжаем.
			{
				return;
			}

			if(zone.getName().equalsIgnoreCase("[Steam1to2]")) //По идее тут входим в зону с конкретно заданным именем.
			{
				ThreadPoolManager.getInstance().schedule(new First(), 15000L);//Запускаем Runnable
			}
			
			if(zone.getName().equalsIgnoreCase("[Steam2to3]")) //По идее тут входим в зону с конкретно заданным именем.
			{
				ThreadPoolManager.getInstance().schedule(new Second(), 15000L);//Запускаем Runnable
			}
			
			if(zone.getName().equalsIgnoreCase("[Steam3to4]")) //По идее тут входим в зону с конкретно заданным именем.
			{
				ThreadPoolManager.getInstance().schedule(new Third(), 15000L);//Запускаем Runnable
			}
			
			if(zone.getName().equalsIgnoreCase("[Steam4to5]")) //По идее тут входим в зону с конкретно заданным именем.
			{
				ThreadPoolManager.getInstance().schedule(new Fourth(), 15000L);//Запускаем Runnable
			}
			
			if(zone.getName().equalsIgnoreCase("[Steam5toBoss]")) //По идее тут входим в зону с конкретно заданным именем.
			{
				ThreadPoolManager.getInstance().schedule(new Boss(), 15000L);//Запускаем Runnable
			}
		}

		@Override
		public void onZoneLeave(Zone zone, Creature cha)//На выходе из зоны ничего не происходит
		{
		}
	}

	private class First extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			for(Player p : getPlayers())
			{
				Reflection ref = p.getActiveReflection();//Обозначаем текущий инстанс как активный
				p.teleToLocation(147528, 218200, -12162, ref);//Телепортируемся сохораняя рефлекшн
				p.sendPacket(new ExShowScreenMessage("Открылся портал ведущий в следущую комнату.", 3000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, 1, -1, true));//Сообщение посреди экрана при телепорте
			}
		}
	}
	
	private class Second extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			for(Player p : getPlayers())
			{
				Reflection ref = p.getActiveReflection();//Обозначаем текущий инстанс как активный
				p.teleToLocation(150200, 218200, -12162, ref);//Телепортируемся сохораняя рефлекшн
				p.sendPacket(new ExShowScreenMessage("Открылся портал ведущий в следущую комнату.", 3000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, 1, -1, true));//Сообщение посреди экрана при телепорте
			}
		}
	}
	
	private class Third extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			for(Player p : getPlayers())
			{
				Reflection ref = p.getActiveReflection();//Обозначаем текущий инстанс как активный
				p.teleToLocation(150632, 220088, -12162, ref);//Телепортируемся сохораняя рефлекшн
				p.sendPacket(new ExShowScreenMessage("Открылся портал ведущий в следущую комнату.", 3000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, 1, -1, true));//Сообщение посреди экрана при телепорте
			}
		}
	}
	
	private class Fourth extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			for(Player p : getPlayers())
			{
				Reflection ref = p.getActiveReflection();//Обозначаем текущий инстанс как активный
				p.teleToLocation(147928, 220088, -12162, ref);//Телепортируемся сохораняя рефлекшн
				p.sendPacket(new ExShowScreenMessage("Открылся портал ведущий в следущую комнату.", 3000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, 1, -1, true));//Сообщение посреди экрана при телепорте
			}
		}
	}
	
	private class Boss extends RunnableImpl
	{
		@Override
		public void runImpl() throws Exception
		{
			for(Player p : getPlayers())
			{
				Reflection ref = p.getActiveReflection();//Обозначаем текущий инстанс как активный
				p.teleToLocation(149864, 215576, -12162, ref);//Телепортируемся сохораняя рефлекшн
				p.sendPacket(new ExShowScreenMessage("Открылся портал ведущий в следущую комнату.", 3000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true, 1, -1, true));//Сообщение посреди экрана при телепорте
			}
		}
	}
}