package matze.gc.gpxcreator.utils;

import java.io.UnsupportedEncodingException;

public class Nah {
	public static void main(String[] args) throws UnsupportedEncodingException {
		byte[] b = new byte[] { (byte) 0xef, (byte) 0x81, (byte) 0x8a };
		String s = new String(b, "UTF-8");
		System.out.println(s);
		int c = s.charAt(0);
		System.out.println(c);

	}
}
