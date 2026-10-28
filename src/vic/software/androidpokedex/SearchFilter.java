package vic.software.androidpokedex;

/** Literal prefix for a bound SQLite LIKE argument, not an SQL fragment. */
final class SearchFilter {
	private SearchFilter() { }

	static String prefix(String input) {
		String text = input == null ? "" : input.trim();
		return text.replace("\\", "\\\\").replace("%", "\\%")
				.replace("_", "\\_") + "%";
	}
}