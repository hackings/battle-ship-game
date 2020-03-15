package org.game.app;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class BattleShipGameTest extends TestCase {
	public BattleShipGameTest(String testName) {
		super(testName);
	}

	public static Test suite() {
		return new TestSuite(BattleShipGameTest.class);
	}

	public void testApp() {
		assertTrue(true);
	}
}
