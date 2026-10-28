package vic.software.androidpokedex;

/** Explicit references keep every bundled sprite and cry visible to Android tooling. */
final class PokemonResources {
	private PokemonResources() { }
	private static final int[] SPRITES = {
		0,
		R.drawable.p1, R.drawable.p2, R.drawable.p3, R.drawable.p4, R.drawable.p5,
		R.drawable.p6, R.drawable.p7, R.drawable.p8, R.drawable.p9, R.drawable.p10,
		R.drawable.p11, R.drawable.p12, R.drawable.p13, R.drawable.p14, R.drawable.p15,
		R.drawable.p16, R.drawable.p17, R.drawable.p18, R.drawable.p19, R.drawable.p20,
		R.drawable.p21, R.drawable.p22, R.drawable.p23, R.drawable.p24, R.drawable.p25,
		R.drawable.p26, R.drawable.p27, R.drawable.p28, R.drawable.p29, R.drawable.p30,
		R.drawable.p31, R.drawable.p32, R.drawable.p33, R.drawable.p34, R.drawable.p35,
		R.drawable.p36, R.drawable.p37, R.drawable.p38, R.drawable.p39, R.drawable.p40,
		R.drawable.p41, R.drawable.p42, R.drawable.p43, R.drawable.p44, R.drawable.p45,
		R.drawable.p46, R.drawable.p47, R.drawable.p48, R.drawable.p49, R.drawable.p50,
		R.drawable.p51, R.drawable.p52, R.drawable.p53, R.drawable.p54, R.drawable.p55,
		R.drawable.p56, R.drawable.p57, R.drawable.p58, R.drawable.p59, R.drawable.p60,
		R.drawable.p61, R.drawable.p62, R.drawable.p63, R.drawable.p64, R.drawable.p65,
		R.drawable.p66, R.drawable.p67, R.drawable.p68, R.drawable.p69, R.drawable.p70,
		R.drawable.p71, R.drawable.p72, R.drawable.p73, R.drawable.p74, R.drawable.p75,
		R.drawable.p76, R.drawable.p77, R.drawable.p78, R.drawable.p79, R.drawable.p80,
		R.drawable.p81, R.drawable.p82, R.drawable.p83, R.drawable.p84, R.drawable.p85,
		R.drawable.p86, R.drawable.p87, R.drawable.p88, R.drawable.p89, R.drawable.p90,
		R.drawable.p91, R.drawable.p92, R.drawable.p93, R.drawable.p94, R.drawable.p95,
		R.drawable.p96, R.drawable.p97, R.drawable.p98, R.drawable.p99, R.drawable.p100,
		R.drawable.p101, R.drawable.p102, R.drawable.p103, R.drawable.p104, R.drawable.p105,
		R.drawable.p106, R.drawable.p107, R.drawable.p108, R.drawable.p109, R.drawable.p110,
		R.drawable.p111, R.drawable.p112, R.drawable.p113, R.drawable.p114, R.drawable.p115,
		R.drawable.p116, R.drawable.p117, R.drawable.p118, R.drawable.p119, R.drawable.p120,
		R.drawable.p121, R.drawable.p122, R.drawable.p123, R.drawable.p124, R.drawable.p125,
		R.drawable.p126, R.drawable.p127, R.drawable.p128, R.drawable.p129, R.drawable.p130,
		R.drawable.p131, R.drawable.p132, R.drawable.p133, R.drawable.p134, R.drawable.p135,
		R.drawable.p136, R.drawable.p137, R.drawable.p138, R.drawable.p139, R.drawable.p140,
		R.drawable.p141, R.drawable.p142, R.drawable.p143, R.drawable.p144, R.drawable.p145,
		R.drawable.p146, R.drawable.p147, R.drawable.p148, R.drawable.p149, R.drawable.p150,
		R.drawable.p151
	};
	private static final int[] SOUNDS = {
		0,
		R.raw.s1, R.raw.s2, R.raw.s3, R.raw.s4, R.raw.s5,
		R.raw.s6, R.raw.s7, R.raw.s8, R.raw.s9, R.raw.s10,
		R.raw.s11, R.raw.s12, R.raw.s13, R.raw.s14, R.raw.s15,
		R.raw.s16, R.raw.s17, R.raw.s18, R.raw.s19, R.raw.s20,
		R.raw.s21, R.raw.s22, R.raw.s23, R.raw.s24, R.raw.s25,
		R.raw.s26, R.raw.s27, R.raw.s28, R.raw.s29, R.raw.s30,
		R.raw.s31, R.raw.s32, R.raw.s33, R.raw.s34, R.raw.s35,
		R.raw.s36, R.raw.s37, R.raw.s38, R.raw.s39, R.raw.s40,
		R.raw.s41, R.raw.s42, R.raw.s43, R.raw.s44, R.raw.s45,
		R.raw.s46, R.raw.s47, R.raw.s48, R.raw.s49, R.raw.s50,
		R.raw.s51, R.raw.s52, R.raw.s53, R.raw.s54, R.raw.s55,
		R.raw.s56, R.raw.s57, R.raw.s58, R.raw.s59, R.raw.s60,
		R.raw.s61, R.raw.s62, R.raw.s63, R.raw.s64, R.raw.s65,
		R.raw.s66, R.raw.s67, R.raw.s68, R.raw.s69, R.raw.s70,
		R.raw.s71, R.raw.s72, R.raw.s73, R.raw.s74, R.raw.s75,
		R.raw.s76, R.raw.s77, R.raw.s78, R.raw.s79, R.raw.s80,
		R.raw.s81, R.raw.s82, R.raw.s83, R.raw.s84, R.raw.s85,
		R.raw.s86, R.raw.s87, R.raw.s88, R.raw.s89, R.raw.s90,
		R.raw.s91, R.raw.s92, R.raw.s93, R.raw.s94, R.raw.s95,
		R.raw.s96, R.raw.s97, R.raw.s98, R.raw.s99, R.raw.s100,
		R.raw.s101, R.raw.s102, R.raw.s103, R.raw.s104, R.raw.s105,
		R.raw.s106, R.raw.s107, R.raw.s108, R.raw.s109, R.raw.s110,
		R.raw.s111, R.raw.s112, R.raw.s113, R.raw.s114, R.raw.s115,
		R.raw.s116, R.raw.s117, R.raw.s118, R.raw.s119, R.raw.s120,
		R.raw.s121, R.raw.s122, R.raw.s123, R.raw.s124, R.raw.s125,
		R.raw.s126, R.raw.s127, R.raw.s128, R.raw.s129, R.raw.s130,
		R.raw.s131, R.raw.s132, R.raw.s133, R.raw.s134, R.raw.s135,
		R.raw.s136, R.raw.s137, R.raw.s138, R.raw.s139, R.raw.s140,
		R.raw.s141, R.raw.s142, R.raw.s143, R.raw.s144, R.raw.s145,
		R.raw.s146, R.raw.s147, R.raw.s148, R.raw.s149, R.raw.s150,
		R.raw.s151
	};

	static int sprite(long id) {
		return id > 0 && id < SPRITES.length ? SPRITES[(int) id] : 0;
	}

	static int sound(long id) {
		return id > 0 && id < SOUNDS.length ? SOUNDS[(int) id] : 0;
	}
}