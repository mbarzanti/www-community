package org.jod.base.crypt;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;

public class UtilsFiles {

	public boolean write(String aRootPath, String aAlias, String aFileName, byte[] values) {
		String alias[] = aAlias.split("-");
		StringBuffer path = new StringBuffer();
		int len = alias[4].length(), i = 0;
		for (i = 0; i < len; i++) {
			path.append(alias[4].charAt(i));
			path.append("/");
		}
		File directory = new File(aRootPath + path.toString());
		if (!directory.exists()) {
			directory.mkdirs();
		}
		return write(aRootPath + path.toString() + aFileName, values);
	}

	public boolean write(String aPath, byte[] values) {

		boolean result = true;
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(aPath);
			fos.write(values);
			fos.flush();
			result = true;
		} catch (Exception e) {
			result = false;
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return result;
	}

	public byte[] read(String aRootPath, String aAlias, String aFileName) {

		String alias[] = aAlias.split("-");
		StringBuffer path = new StringBuffer();
		int len = alias[4].length(), i = 0;
		for (i = 0; i < len; i++) {
			path.append(alias[4].charAt(i));
			path.append("/");
		}

		return read(aRootPath + path.toString() + aFileName);
	}

	public byte[] read(String aPath) {

		byte[] result = null;
		FileInputStream fos = null;
		try {
			File f = new File(aPath);
			long len = f.length();
			result = new byte[(int) len];
			fos = new FileInputStream(f);
			fos.read(result);
		} catch (Exception x) {
			x.printStackTrace();
			result = new byte[0];
		} finally {

			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return result;
	}

	public boolean write(String aContent, String aPath) {

		boolean r = false;
		FileWriter fw = null;
		try {
			fw = new FileWriter(new File(aPath), false);
			fw.write(aContent);
			fw.flush();
			r = true;
		} catch (IOException e) {
			r = false;
			e.printStackTrace();
		} finally {
			try {
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return r;

	}

	public boolean read(StringBuffer aContentHolder, String aPath) {

		boolean r = false;
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(aPath));
			String line = br.readLine();

			while (line != null) {
				aContentHolder.append(line);
				aContentHolder.append("\n");
				line = br.readLine();
			}

			r = true;
		} catch (Exception x) {
			r = false;
			x.printStackTrace();
		}

		finally {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return r;
	}

	public byte[] serialize(Serializable aSerializable) {
		byte[] returned = new byte[0];
		ByteArrayOutputStream baos = null;
		ObjectOutputStream oos = null;
		try {
			baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(baos);
			oos.writeObject(aSerializable);
			returned = baos.toByteArray();

		} catch (Exception x) {
			returned = new byte[0];
			x.printStackTrace();
		} finally {

			if (oos != null) {

				try {
					oos.close();
				} catch (IOException e) {
				}
			}

			if (baos != null) {

				try {
					baos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return returned;
	}

	public Object deserialize(byte[] aData) {
		Object returned = null;
		ObjectInputStream ois = null;
		try {
			ois = new ObjectInputStream(new ByteArrayInputStream(aData));
			returned = ois.readObject();
			ois.close();

		} catch (Exception x) {
			returned = new byte[0];
			x.printStackTrace();
		} finally {

			if (ois != null) {

				try {
					ois.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
		return returned;
	}

	public File workingdir(String aRootPath, String aAlias) {

		String alias[] = aAlias.split("-");
		StringBuffer path = new StringBuffer();
		int len = alias[4].length(), i = 0;
		for (i = 0; i < len; i++) {
			path.append(alias[4].charAt(i));
			path.append("/");
		}
		File directory = new File(aRootPath + path.toString());
		if (!directory.exists()) {
			directory.mkdirs();
		}

		return directory;
	}

	public boolean copyFileUsingStream(File source, File dest) {
		boolean result = false;
		InputStream is = null;
		OutputStream os = null;
		try {
			is = new FileInputStream(source);
			os = new FileOutputStream(dest);
			byte[] buffer = new byte[1024];
			int length;
			while ((length = is.read(buffer)) > 0) {
				os.write(buffer, 0, length);
			}
			result = true;
		} catch (Exception x) {
			result = false;
			x.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				os.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return result;
	}
}
