package org.nectarframework.base.tools;

import java.nio.charset.CharsetEncoder;
import java.security.SecureRandom;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class RandUtils {
	public static final SecureRandom srand = new SecureRandom();

	public static Random rand(boolean secure) {
		return (secure) ? srand : ThreadLocalRandom.current();
	}

	public static long nextLong() {
		return rand(false).nextLong();
	}

	public static int nextInt() {
		return rand(false).nextInt();
	}

	public static int nextInt(int bound) {
		if (bound == 0)
			return 0;
		return rand(false).nextInt(bound);
	}


	/**
	 * a random float between 0 and 1
	 * 
	 * @return
	 */
	private static float nextFloat() {
		return rand(false).nextFloat();
	}
	
	/**
	 * returns a random integer between i inclusive and j exclusive.
	 * 
	 * @param i
	 * @param j
	 * @return
	 */

	public static int nextInt(int i, int j) {
		if (i == j)
			return j;
		return nextInt(j - i) + i;
	}

	public static short nextShort() {
		return (short) rand(false).nextInt(Short.MAX_VALUE + 1);
	}

	public static byte nextByte() {
		return (byte) rand(false).nextInt(Byte.MAX_VALUE + 1);
	}

	public static byte[] nextByteArray(int length) {
		byte[] b = new byte[length];
		for (int t = 0; t < length; t++) {
			b[t] = nextByte();
		}
		return b;
	}

	/**
	 * Create a String of between i and j in length, of random characters A
	 * through Z.
	 * 
	 * @param i
	 * @param j
	 * @return
	 */

	public static String nextPlainStringUpperCase(int i, int j) {
		int len = nextInt(i, j);
		byte[] sa = new byte[len];
		for (int t = 0; t < len; t++) {
			sa[t] = nextUpperCase();
		}
		return new String(sa);
	}
	
	public static byte nextUpperCase() {
		return (byte) (65 + nextInt(26));
	}

	public static byte nextLowerCase() {
		return (byte) (97 + nextInt(26));
	}

	/**
	 * Create a String of between i and j in length, of random characters a
	 * through z.
	 * 
	 * @param i
	 * @param j
	 * @return
	 */

	public static String nextPlainStringLowerCase(int i, int j) {
		int len = nextInt(i, j);
		byte[] sa = new byte[len];
		for (int t = 0; t < len; t++) {
			sa[t] = nextLowerCase();
		}
		return new String(sa);
	}

	/**
	 * Create a String of between i and j in length, of random characters a-z
	 * and A-Z. The ratio determines the probability of upperCase characters.
	 * (0.1 gives 10% uppercase, 90% lower case on average) through z.
	 * 
	 * @param i
	 * @param j
	 * @param d
	 * @return
	 */
	public static String nextPlainStringMixedCase(int i, int j, float d) {
		int len = nextInt(i, j);
		byte[] sa = new byte[len];
		for (int t = 0; t < len; t++) {
			if (nextFloat() <= d) {
				sa[t] = nextUpperCase();
			} else {
				sa[t] = nextLowerCase();
			}
		}
		return new String(sa);
	}


	/**
	 * Create a String of between i and j in length, of random UTF-8 characters.
	 * 
	 * @param i
	 * @param j
	 * @return
	 */
	public static String nextUTF8String(int i, int j) {
		int len = nextInt(i, j);
		StringBuffer sb = new StringBuffer(len);
		CharsetEncoder ce = StringTools.getCharset().newEncoder();
		while (sb.length() < len) {
			char c = (char) (nextInt() & Character.MAX_VALUE);
			if (Character.isDefined(c) && ce.canEncode(c)) {
				sb.append(c);
			}
		}
		return sb.toString();
	}

}
