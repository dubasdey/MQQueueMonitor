package org.erc.qmm.mq.agent;

import java.io.UnsupportedEncodingException;
import java.util.Hashtable;

/**
 * The Class CCSID.
 */
public class CCSID {
	
	/** The Constant ht. */
	static final Hashtable<Integer,String> ht = new Hashtable<Integer,String>();

	static {
		addCodepage(0, System.getProperty("file.encoding"));
		addCodepage(37, "IBM-037");
		addCodepage(273, "IBM-273");
		addCodepage(277, "IBM-277");
		addCodepage(278, "IBM-278");
		addCodepage(280, "IBM-280");
		addCodepage(284, "IBM-284");
		addCodepage(285, "IBM-285");
		addCodepage(297, "IBM-297");
		addCodepage(420, "IBM-420");
		addCodepage(424, "IBM-424");
		addCodepage(437, "IBM-437");
		addCodepage(500, "IBM-500");
		addCodepage(737, "IBM-737");
		addCodepage(775, "IBM-775");
		addCodepage(813, "ISO8859-7");
		addCodepage(819, "ISO8859_1");
		addCodepage(838, "IBM-838");
		addCodepage(850, "IBM-850");
		addCodepage(852, "IBM-852");
		addCodepage(855, "IBM-855");
		addCodepage(856, "IBM-856");
		addCodepage(857, "IBM-857");
		addCodepage(860, "IBM-860");
		addCodepage(861, "IBM-861");
		addCodepage(862, "IBM-862");
		addCodepage(863, "IBM-863");
		addCodepage(864, "IBM-864");
		addCodepage(865, "IBM-865");
		addCodepage(866, "IBM-866");
		addCodepage(868, "IBM-868");
		addCodepage(869, "IBM-869");
		addCodepage(870, "IBM-870");
		addCodepage(871, "IBM-871");
		addCodepage(874, "IBM-874");
		addCodepage(875, "IBM-875");
		addCodepage(878, "KOI8-R");
		addCodepage(912, "ISO8859-2");
		addCodepage(913, "ISO8859-3");
		addCodepage(914, "ISO8859-4");
		addCodepage(915, "ISO8859-5");
		addCodepage(916, "ISO8859-8");
		addCodepage(918, "IBM-918");
		addCodepage(920, "ISO8859-9");
		addCodepage(921, "IBM-921");
		addCodepage(922, "IBM-922");
		addCodepage(923, "ISO8859-15");
		addCodepage(930, "IBM-930");
		addCodepage(932, "IBM-932");
		addCodepage(933, "IBM-933");
		addCodepage(935, "IBM-935");
		addCodepage(937, "IBM-937");
		addCodepage(939, "IBM-939");
		addCodepage(942, "IBM-942");
		addCodepage(943, "PCK");
		addCodepage(948, "IBM-948");
		addCodepage(949, "IBM-949");
		addCodepage(950, "IBM-950");
		addCodepage(954, "EUCJIS");
		addCodepage(964, "IBM-964");
		addCodepage(970, "IBM-970");
		addCodepage(1006, "IBM-1006");
		addCodepage(1025, "IBM-1025");
		addCodepage(1026, "IBM-1026");
		addCodepage(1089, "ISO8859-6");
		addCodepage(1097, "IBM-1097");
		addCodepage(1098, "IBM-1098");
		addCodepage(1112, "IBM-1112");
		addCodepage(1122, "IBM-1122");
		addCodepage(1123, "IBM-1123");
		addCodepage(1124, "IBM-1124");

		addCodepage(1208, "UTF-8");
		addCodepage(1250, "WINDOWS-1250");
		addCodepage(1251, "WINDOWS-1251");
		addCodepage(1252, "WINDOWS-1252");
		addCodepage(1253, "WINDOWS-1253");
		addCodepage(1254, "WINDOWS-1254");
		addCodepage(1255, "WINDOWS-1255");
		addCodepage(1256, "WINDOWS-1256");
		addCodepage(1257, "WINDOWS-1257");
		addCodepage(1258, "WINDOWS-1258");
		addCodepage(1381, "IBM-1381");
		addCodepage(1383, "IBM-1383");
		addCodepage(1386, "GBK");
		addCodepage(2022, "JIS");
		addCodepage(5488, "GB18030");
		addCodepage(5601, "KSC5601");
	}

	/**
	 * Adds the codepage.
	 *
	 * @param ccsid the ccsid
	 * @param codepage the codepage
	 */
	public static void addCodepage(int ccsid, String codepage) {
		ht.put(new Integer(ccsid), codepage);
	}

	/**
	 * Gets the codepage.
	 *
	 * @param ccsid the ccsid
	 * @return the codepage
	 * @throws UnsupportedEncodingException the unsupported encoding exception
	 */
	public static String getCodepage(int ccsid) throws UnsupportedEncodingException {
		String codepage = (String) ht.get(new Integer(ccsid));
		if (codepage != null) {
			return codepage;
		}
		throw new UnsupportedEncodingException(ccsid + ": Unknown CCSID");
	}

	/**
	 * Convert.
	 *
	 * @param string the string
	 * @param ccsid the ccsid
	 * @return the byte[]
	 * @throws UnsupportedEncodingException the unsupported encoding exception
	 */
	public static byte[] convert(String string, int ccsid) throws UnsupportedEncodingException {
		return string.getBytes(getCodepage(ccsid));
	}
}