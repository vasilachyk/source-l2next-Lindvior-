package l2next.gameserver.stats.conditions;

import l2next.gameserver.stats.Env;

public class ConditionTargetPercentMp extends Condition
{
	private final double _mp;

	public ConditionTargetPercentMp(int mp)
	{
		_mp = mp / 100.;
	}

	@Override
	protected boolean testImpl(Env env)
	{
		return env.target != null && env.target.getCurrentMpRatio() <= _mp;
	}
}