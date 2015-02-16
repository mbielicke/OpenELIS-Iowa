package org.openelis.stfu.domain;

import java.util.Random;

import org.openelis.domain.DataObject;

import java.util.Calendar;

public class RulesTest extends DataObject {
	
	private static final long serialVersionUID = 1L;
	
	public static Random random = new Random(Calendar.getInstance().getTimeInMillis());
	
	public int randomInt;
	
	public RulesTest() {
		randomInt = random.nextInt(1000);
	}

	public int getRandom() {
		return randomInt;
	}
	
	public void redoRandom() {
		randomInt = random.nextInt(1000);
	}
}
