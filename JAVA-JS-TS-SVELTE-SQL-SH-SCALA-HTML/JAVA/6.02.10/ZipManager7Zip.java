package com.postel.loader.util.zip;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;
import org.apache.commons.compress.archivers.sevenz.SevenZOutputFile;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.log4j.Logger;

public class ZipManager7Zip {
	private static final int BUFFER_SIZE = 4096;
	protected static Logger trace = Logger.getLogger(ZipManager7Zip.class);
	private SevenZOutputFile sevenZOutputFile;
	private List<Custom7ZipFile> customZipFiles = new ArrayList();

	public ZipManager7Zip(String var1) throws IOException {
		this.sevenZOutputFile = new SevenZOutputFile(new File(var1));
	}

	public ZipManager7Zip(OutputStream var1) throws IOException {
		this.sevenZOutputFile = new SevenZOutputFile(new File(var1.toString()));
	}

	public static void zipFiles(Collection<Custom7ZipFile> var0, File var1) throws IOException {
		SevenZOutputFile var2 = null;
		try {
			var2 = new SevenZOutputFile(var1);
			Iterator<Custom7ZipFile> var3 = var0.iterator();

			while (var3.hasNext()) {
				Custom7ZipFile var4 = var3.next();
				FileInputStream var5 = new FileInputStream(var4);
				SevenZArchiveEntry var6 = var2.createArchiveEntry(var4, var4.getCustomName());
				var2.putArchiveEntry(var6);
				byte[] var8 = new byte[BUFFER_SIZE];

				int var7;
				while ((var7 = var5.read(var8)) != -1) {
					var2.write(var8, 0, var7);
				}
				var5.close();
				var2.closeArchiveEntry();
			}
			var2.close();
		} catch (IOException var9) {
			if (var2 != null) {
				var2.close();
			}
			throw var9;
		}
	}

	public static void zipFiles(Collection<Custom7ZipFile> var0, File var1, boolean var2) throws IOException {
		SevenZOutputFile var3 = null;
		try {
			var3 = new SevenZOutputFile(var1);
			Iterator var4 = var0.iterator();
			while (var4.hasNext()) {
				Custom7ZipFile var5 = (Custom7ZipFile) var4.next();
				FileInputStream var6 = new FileInputStream(var5);
				SevenZArchiveEntry var7 = var3.createArchiveEntry(var5, var5.getCustomName());
				var3.putArchiveEntry(var7);
				byte[] var9 = new byte[BUFFER_SIZE];

				int var8;
				while ((var8 = var6.read(var9)) != -1) {
					var3.write(var9, 0, var8);
				}
				var3.closeArchiveEntry();
				var6.close();
				if (var2 && !var5.delete()) {
					System.out.println("Unable to delete " + var5.getAbsolutePath());
				}
			}

			var3.close();
		} catch (IOException var10) {
			if (var3 != null) {
				var3.close();
			}

			throw var10;
		}
	}

//	public void addFile(Custom7ZipFile var1) throws IOException {
//		if (!this.customZipFiles.contains(var1) && var1.getAbsoluteFile().exists()) {
//			FileInputStream var2 = new FileInputStream(var1);
//			SevenZArchiveEntry var3 = this.sevenZOutputFile.createArchiveEntry(var1, var1.getCustomName());
//			this.sevenZOutputFile.putArchiveEntry(var3);
//			byte[] var5 = new byte[BUFFER_SIZE];
//
//			int var4;
//			while ((var4 = var2.read(var5)) != -1) {
//				this.sevenZOutputFile.write(var5, 0, var4);
//			}
//
//			this.sevenZOutputFile.closeArchiveEntry();
//			var2.close();
//			this.customZipFiles.add(var1);
//		} else {
//			trace.warn("File not added:" + var1.getCustomName());
//		}
//	}

//	public void addFile(byte[] var1, String var2) throws IOException {
//		Custom7ZipFile var3 = new Custom7ZipFile(var2, var2);
//		if (!this.customZipFiles.contains(var3)) {
//			SevenZArchiveEntry var4 = this.sevenZOutputFile.createArchiveEntry(new File(var2), var2);
//			this.sevenZOutputFile.putArchiveEntry(var4);
//			this.sevenZOutputFile.write(var1);
//			this.sevenZOutputFile.closeArchiveEntry();
//			this.customZipFiles.add(var3);
//		} else {
//			trace.warn("File not added:" + var3.getCustomName());
//		}
//	}

	public void closeZipFile() throws IOException {
		try {
			this.sevenZOutputFile.close();
		} catch (IOException var2) {
			if (this.sevenZOutputFile != null) {
				this.sevenZOutputFile.close();
			}
			throw var2;
		}
	}

	@SuppressWarnings("deprecation")
	public static void unzip(File var0, File var1, Charset var2) throws IOException {
		if (!var1.mkdirs()) {
			System.out.println("Unable to create " + var1.getAbsolutePath());
		}
		if (var0.getName().endsWith(".7z")) {
			// unzip file seven7Z
			SevenZFile var3 = null;
			try {
				var3 = new SevenZFile(var0);
				SevenZArchiveEntry var4;
				while ((var4 = var3.getNextEntry()) != null) {
					if (!var4.getName().equals("") && !var4.getName().equals("\\")) {
						File var5 = new File(var1, var4.getName());
						InputStream var6 = null;
						try {
							var6 = var3.getInputStream(var4);
							if (!var4.isDirectory()) {
								if (!(new File(var5.getParent())).exists()) {
									(new File(var5.getParent())).mkdirs();
								}
								copyData(var6, var5);
							} else {
								var5.mkdir();
							}
						} finally {
							if (var6 != null) {
								var6.close();
							}
						}
					}
				}
			} finally {
				if (var3 != null) {
					var3.close();
				}
			}
		} else if (var0.getName().endsWith(".zip")) {
			// unzip file zip
			ZipFile var3 = null;
			try {
				if (var2 == null) {
					var3 = new ZipFile(var0);
				} else {
					var3 = new ZipFile(var0, var2.toString());
				}
				Enumeration var4 = var3.getEntries();
				while (var4.hasMoreElements()) {
					ZipArchiveEntry var5 = (ZipArchiveEntry) var4.nextElement();
					if (!var5.getName().equals("") && !var5.getName().equals("\\")) {
						File var6 = new File(var1, var5.getName());
						InputStream var7 = null;
						try {
							var7 = var3.getInputStream(var5);
							if (!var5.isDirectory()) {
								if (!(new File(var6.getParent())).exists()) {
									(new File(var6.getParent())).mkdirs();
								}
								copyData(var7, var6);
							} else {
								var6.mkdir();
							}
						} finally {
							try {
								if (var7 != null) {
									var7.close();
								}
							} catch (IOException var20) {
								trace.error("Zip Error - ", var20);
								throw var20;
							}
						}
						var7 = null;
					}
				}
			} finally {
				if (var3 != null) {
					var3.close();
				}
			}
		}
	}

	public static void unzip(File var0, File var1) throws IOException {
		unzip(var0, var1, (Charset) null);
	}

	public static void unzipSingleLevel(File var0, File var1) throws IOException {
		unzipSingleLevel(var0, var1, (Charset) null);
	}

	public static void unzipSingleLevel(File var0, File var1, Charset var2) throws IOException {
		if (!var1.exists() && !var1.mkdirs()) {
			System.out.println("Unable to create " + var1.getAbsolutePath());
		}
		if (var0.getName().endsWith(".7z")) {
			//unzip file seven7z 
			SevenZFile var3 = null;
			try {
				var3 = new SevenZFile(var0);

				SevenZArchiveEntry var4;
				while ((var4 = var3.getNextEntry()) != null) {
					if (!var4.getName().equals("") && !var4.getName().equals("\\")) {
						String var5 = var4.getName();
						if (var5.indexOf("/") != -1 && !var5.endsWith("/")) {
							var5 = var5.substring(var5.lastIndexOf("/") + 1);
						}
						File var6 = new File(var1, var5);
						if (!var4.isDirectory()) {
							copyData(var3.getInputStream(var4), var6);
						} else {
							System.out.println("dir");
						}
					}
				}
			} finally {
				if (var3 != null) {
					var3.close();
				}
			}
		} else if (var0.getName().endsWith(".zip")) {
			//unzip file zip
			ZipArchiveInputStream var3 = null;
		      try {
		         if (var2 == null) {
		            var3 = new ZipArchiveInputStream(new FileInputStream(var0));
		         } 
		         else {
		            var3 = new ZipArchiveInputStream(new FileInputStream(var0), var2.toString());
		         }

		         for(ZipArchiveEntry var4 = var3.getNextEntry(); var4 != null; var4 = var3.getNextEntry()) {
		            if (!var4.getName().equals("") && !var4.getName().equals("\\")) {
		               String var5 = var4.getName();
		               if (var5.indexOf("/") != -1 && !var5.endsWith("/")) {
		                  var5 = var5.substring(var5.lastIndexOf("/") + 1);
		               }
		               File var6 = new File(var1, var5);
		               if (!var4.isDirectory()) {
		            	   copyData(var3, var6);
		               } else {
		                  System.out.println("dir");
		               }
		            }
		            var3.close();
		         }
		      } finally {
		         if (var3 != null) {
		            var3.close();
		         }
		      }
		}
			
		
	}

	private static void copyData(InputStream var0, File var1) throws IOException {
		try (BufferedOutputStream var2 = new BufferedOutputStream(new FileOutputStream(var1))) {
			byte[] var3 = new byte[BUFFER_SIZE];
			int var4;
			while ((var4 = var0.read(var3)) != -1) {
				var2.write(var3, 0, var4);
			}
		}
	}
}
