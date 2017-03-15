/*
 * Copyright (C) 2006 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lxdz.capacity.util;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.Date;

/**
 * Clone of Android's HexDump class, for use in debugging. Cosmetic changes
 * only.
 */
public class HexDump {
	private final static char[] HEX_DIGITS = { '0', '1', '2', '3', '4', '5',
			'6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

	public static String dumpHexString(byte[] array) {
		return dumpHexString(array, 0, array.length);
	}

	public static String dumpHexString(byte[] array, int offset, int length) {
		StringBuilder result = new StringBuilder();

		byte[] line = new byte[16];
		int lineIndex = 0;

		result.append("\n0x");
		result.append(toHexString(offset));

		for (int i = offset; i < offset + length; i++) {
			if (lineIndex == 16) {
				result.append(" ");

				for (int j = 0; j < 16; j++) {
					if (line[j] > ' ' && line[j] < '~') {
						result.append(new String(line, j, 1));
					} else {
						result.append(".");
					}
				}

				result.append("\n0x");
				result.append(toHexString(i));
				lineIndex = 0;
			}

			byte b = array[i];
			result.append(" ");
			result.append(HEX_DIGITS[(b >>> 4) & 0x0F]);
			result.append(HEX_DIGITS[b & 0x0F]);

			line[lineIndex++] = b;
		}

		if (lineIndex != 16) {
			int count = (16 - lineIndex) * 3;
			count++;
			for (int i = 0; i < count; i++) {
				result.append(" ");
			}

			for (int i = 0; i < lineIndex; i++) {
				if (line[i] > ' ' && line[i] < '~') {
					result.append(new String(line, i, 1));
				} else {
					result.append(".");
				}
			}
		}

		return result.toString();
	}

	public static String toHexString(byte b) {
		return toHexString(toByteArray(b));
	}

	public static String toHexString(byte[] array) {
		if (array == null)
			return "";
		return toHexString(array, 0, array.length);
	}

	public static String toHexString(byte[] array, int offset, int length) {
		char[] buf = new char[length * 2];

		int bufIndex = 0;
		for (int i = offset; i < offset + length; i++) {
			byte b = array[i];
			buf[bufIndex++] = HEX_DIGITS[(b >>> 4) & 0x0F];
			buf[bufIndex++] = HEX_DIGITS[b & 0x0F];
		}

		return new String(buf);
	}

	public static String toHexString(int i) {
		return toHexString(toByteArray(i));
	}

	public static String toHexString(short i) {
		return toHexString(toByteArray(i));
	}

	public static byte[] toByteArray(byte b) {
		byte[] array = new byte[1];
		array[0] = b;
		return array;
	}

	public static byte[] toByteArray(int i) {
		byte[] array = new byte[4];

		array[3] = (byte) (i & 0xFF);
		array[2] = (byte) ((i >> 8) & 0xFF);
		array[1] = (byte) ((i >> 16) & 0xFF);
		array[0] = (byte) ((i >> 24) & 0xFF);

		return array;
	}

	public static byte[] toByteArray(short i) {
		byte[] array = new byte[2];

		array[1] = (byte) (i & 0xFF);
		array[0] = (byte) ((i >> 8) & 0xFF);

		return array;
	}

	private static int toByte(char c) {
		if (c >= '0' && c <= '9')
			return (c - '0');
		if (c >= 'A' && c <= 'F')
			return (c - 'A' + 10);
		if (c >= 'a' && c <= 'f')
			return (c - 'a' + 10);

		throw new RuntimeException("Invalid hex char '" + c + "'");
	}

	public static byte[] hexStringToByteArray(String hexString) {
		int length = hexString.length();
		byte[] buffer = new byte[length / 2];

		for (int i = 0; i < length; i += 2) {
			buffer[i / 2] = (byte) ((toByte(hexString.charAt(i)) << 4) | toByte(hexString
					.charAt(i + 1)));
		}

		return buffer;
	}

	// ========BCB码转十进制（去掉最前的零）====
	public static String bcd2Str(byte[] bytes) {
		StringBuffer temp = new StringBuffer(bytes.length * 2);
		for (int i = 0; i < bytes.length; i++) {
			temp.append((byte) ((bytes[i] & 0xf0) >>> 4));
			temp.append((byte) (bytes[i] & 0x0f));
		}
		return temp.toString().substring(0, 1).equalsIgnoreCase("0") ? temp
				.toString().substring(1) : temp.toString();
	}

	// ========BCD码转十进制=================
	public static String bcd2Str1(byte[] bytes) {
		StringBuffer temp = new StringBuffer(bytes.length * 2);
		for (int i = 0; i < bytes.length; i++) {
			temp.append((byte) ((bytes[i] & 0xf0) >>> 4));
			temp.append((byte) (bytes[i] & 0x0f));
		}
		return temp.toString();
	}

	public static byte[] InverByte6(byte a, byte b, byte c, byte d, byte e,
			byte f) {
		// int len=0;
		byte ab[] = new byte[6];
		ab[5] = (byte) (a - (byte) 0x33);
		ab[4] = (byte) (b - (byte) 0x33);
		ab[3] = (byte) (c - (byte) 0x33);
		ab[2] = (byte) (d - (byte) 0x33);
		ab[1] = (byte) (e - (byte) 0x33);
		ab[0] = (byte) (f - (byte) 0x33);
		return ab;

	}

	public static byte[] InverByte3(byte a, byte b, byte c) {
		// int len=0;
		byte ab[] = new byte[3];
		ab[2] = (byte) (a - (byte) 0x33);
		ab[1] = (byte) (b - (byte) 0x33);
		ab[0] = (byte) (c - (byte) 0x33);
		return ab;

	}

	public static byte[] InverByte2(byte a, byte b) {
		// int len=0;
		byte ab[] = new byte[2];
		ab[1] = (byte) (a - (byte) 0x33);
		ab[0] = (byte) (b - (byte) 0x33);
		return ab;

	}

	// ============校验和====================
	public static String CheckSum(byte[] bytes, int length) {

		byte[] K1 = new byte[1];
		K1[0] = getCheckNum(bytes, length);
		String str = BytesToHexString(K1);

		return str;

	}

	/**
	 * 得到校验和数据
	 * 
	 * @param bytes
	 * @param length
	 * @return
	 */
	public static byte getCheckNum(byte[] bytes, int length) {
		byte k = 0;
		for (int i = 0; i < length; i++) {
			k += bytes[i];

		}
		return k;
	}

	private final static byte[] hex = "0123456789ABCDEF".getBytes();

	// 字节组到16进制字符串转换
	public static String BytesToHexString(byte[] b) {
		byte[] buff = new byte[2 * b.length];
		for (int i = 0; i < b.length; i++) {
			buff[2 * i] = hex[(b[i] >> 4) & 0x0f];
			buff[2 * i + 1] = hex[b[i] & 0x0f];
		}
		return new String(buff);
	}

	private static int parse(char c) {
		if (c >= 'a')
			return (c - 'a' + 10) & 0x0f;
		if (c >= 'A')
			return (c - 'A' + 10) & 0x0f;
		return (c - '0') & 0x0f;
	}

	// 16进制加0x33到字节组的转换且反序列
	public static byte[] add33andReverse(String hexstr) {

		byte[] b = new byte[hexstr.length() / 2];
		int j = 0;
		int k = b.length - 1;
		for (int i = 0; i < b.length; i++) {
			char c0 = hexstr.charAt(j++);
			char c1 = hexstr.charAt(j++);

			b[k--] = (byte) ((byte) ((byte) (parse(c0) << 4) | parse(c1)) + 0x33);

		}
		return b;
	}

	/**
	 * 减33H，并且反序
	 * 
	 * @param hexstr
	 * @return
	 */
	public static byte[] subtraction33andReverse(String hexstr) {
		byte[] b = new byte[hexstr.length() / 2];
		int j = hexstr.length() - 1;
		int k = 0;
		for (int i = 0; i < b.length; i++) {
			char c0 = hexstr.charAt(j--);
			char c1 = hexstr.charAt(j--);
			b[k++] = (byte) ((byte) ((byte) (parse(c1) << 4) | parse(c0)) - 0x33);
		}
		return b;
	}

	/**
	 * 把int类型转换成byte[]
	 * @param res
	 * @return
	 */
	public static byte[] int2byte(int res) {
		byte[] targets = new byte[4];

		targets[0] = (byte) (res & 0xff);// 最低位
		targets[1] = (byte) ((res >> 8) & 0xff);// 次低位
		targets[2] = (byte) ((res >> 16) & 0xff);// 次高位
		targets[3] = (byte) (res >>> 24);// 最高位,无符号右移。
		return targets;
	}

	/**
	 * 把四位byte数据转换成int类型
	 * @param res
	 * @return
	 */
	public static int byte2int(byte[] res) {
		// 一个byte数据左移24位变成0x??000000，再右移8位变成0x00??0000
		int targets = (res[0] & 0xff) | ((res[1] << 8) & 0xff00) // | 表示安位或
				| ((res[2] << 24) >>> 8) | (res[3] << 24);
		return targets;
	}
	
	/**
	 * 把2位byte转换成int
	 * @param res
	 * @return
	 */
	public static int byteToint(byte[] res) {
		// int targets = (res[1] & 0xff) | (res[0] & 0xff);
		int targets = ((res[1] & 0xff )<< 8 )  | (res[0] & 0xff);
		return targets;
	}

	public static byte[] intToByteArray(int i) throws Exception {
		ByteArrayOutputStream buf = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(buf);
		System.out.println("i:" + i);
		out.writeInt(i);
		byte[] b = buf.toByteArray();
		System.out.println("i:" + b);
		out.close();
		buf.close();
		return b;
	}

	
	/**
	 * 校验和取反
	 * @param bytes
	 * @param length
	 * @return
	 */
	public static String getCheckNumberReverse(byte[] bytes, int length) {
		byte k = 0;
		for (int i = 0; i < length; i++) {
			k += bytes[i];

		}
		k = (byte) ~k;
		byte[] K1 = new byte[1];
		K1[0] = k;
		String str = BytesToHexString(K1);
		// System.out.println(str);
		return str;

	}
	
	
	/**
	 * 把十进制转换成十六进行，并且反序, 占两个字节
	 * @param value
	 * @return
	 */
	public static String getLowStartData(int value) {
		byte[] b = new byte[2];
		 b[1] = (byte)((value >> 8) & 0xff);
		 b[0] = (byte)(value  & 0xff);
		String str = HexDump.toHexString(b);
		return str;
	}
	
	
	public static String getLowStartDataFour(int value) {
		byte[] b = new byte[4];
		 b[3] = (byte)((value >> 24) & 0xff);
		 b[2] = (byte)((value >> 16) & 0xff);
		 b[1] = (byte)((value >> 8) & 0xff);
		 b[0] = (byte)(value  & 0xff);
		String str = HexDump.toHexString(b);
		return str;
	}
	
	public static String getPrintTime(Date date) {
		byte[] result = new byte[3];
		byte year = (byte)(date.getYear() + 1900 - 2014);;
		byte month = (byte)(date.getMonth() + 1);
		result[2] = (byte)(((year << 4) & 0xf0 ) | (month & 0x0f) );
		byte day = (byte)date.getDate();
		byte hour = (byte)date.getHours();
		byte minute = (byte)date.getMinutes();
		result[1] = (byte)(((day << 3) & 0xf8) | ((hour >> 2)  & 0x07));
		
		result[0] = (byte)(((hour << 6) & 0xc0) | (minute  & 0x3f));
		return HexDump.toHexString(result);
	}
	
	/**
	 * 将int型转换成byte形式的string
	 * @param value
	 * @return
	 */
	public static String intToByteString_one(int value) {
		String result = Integer.toString(value, 16);
		result = result.length() % 2 == 0 ? result : "0" + result;
		return result;
		
	}

}
