package test.de.katzenpapst.amunra;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestingTest {

	@Test
	public void testDivision() {
		assertEquals(15, 60/4);
		assertEquals(-15, -60/4);
		assertEquals(15, 60 >> 2);
		assertEquals(-15, -60 >> 2);
		assertEquals(60, 15 << 2);
		assertEquals(-60, -15 << 2);
		//fail("Not yet implemented");
	}

}
