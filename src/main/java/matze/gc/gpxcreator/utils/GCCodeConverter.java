package matze.gc.gpxcreator.utils;

class GCCodeConverter {
	private static String convert(String input, String srcAlphabet,
			String dstAlphabet) {
		int srcBase = srcAlphabet.length();
		int dstBase = dstAlphabet.length();

		String wet = input;
		int val = 0;
		int mlt = 1;

		while (wet.length() > 0) {
			char digit = wet.charAt(wet.length() - 1);
			int digVal = srcAlphabet.indexOf(digit);
			if (digVal > -1) {
				val += mlt * digVal;
				mlt *= srcBase;
			}
			wet = wet.substring(0, wet.length() - 1);
		}

		int wet2 = val;
		String ret = "";

		while (wet2 >= dstBase) {
			int digitVal = wet2 % dstBase;
			char digit = dstAlphabet.charAt(digitVal);
			ret = digit + ret;
			wet2 /= dstBase;
		}

		char digit = dstAlphabet.charAt(wet2);
		ret = digit + ret;

		return ret;
	}

	int convertToId(String gcCode) {
		String result = convert(
				gcCode.substring(2),
				"0123456789ABCDEFGHJKMNPQRTVWXYZ",
				"0123456789");
		int intResult = Integer.parseInt(result);
		if (intResult < 476656) {
			result = convert(
					gcCode.substring(2),
					"0123456789ABCDEF",
					"0123456789");
		}
		return Integer.parseInt(result);
	}
}
