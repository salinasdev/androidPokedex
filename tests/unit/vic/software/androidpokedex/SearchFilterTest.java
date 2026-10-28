package vic.software.androidpokedex;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class SearchFilterTest {
	@Test public void emptyPrefixMatchesAll() {
		assertEquals("%", SearchFilter.prefix(""));
		assertEquals("%", SearchFilter.prefix(null));
		assertEquals("%", SearchFilter.prefix("   "));
	}

	@Test public void trimsWhitespace() {
		assertEquals("Pika%", SearchFilter.prefix("  Pika  "));
	}

	@Test public void treatsWildcardsAndEscapeAsLiteral() {
		assertEquals("\\%\\_\\\\%", SearchFilter.prefix("%_\\"));
	}

	@Test public void apostrophesArePassedAsData() {
		assertEquals("' OR 1=1 --%", SearchFilter.prefix("' OR 1=1 --"));
	}
}