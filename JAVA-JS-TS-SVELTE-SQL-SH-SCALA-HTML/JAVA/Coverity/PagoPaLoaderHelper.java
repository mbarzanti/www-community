package com.postel.loader.pagopa.helper;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.file.CopyOption;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.http.HttpResponse;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.mfutil.common.exception.FTPException;
import org.mfutil.common.util.DateUtils;
import org.mfutil.common.util.FileRegexpFilter;
import org.mfutil.common.util.FileUtil;
import org.mfutil.common.util.StringUtil;
import org.mfutil.common.util.db.DBConnection;
import org.mfutil.common.util.ftp.FtpUtil;
import org.mfutil.common.util.zip.ZipManager;
import org.mfutil.workflow.bean.JobBean;
import org.mfutil.workflow.bean.ParameterBean;
import org.mfutil.workflow.exception.DAOException;
import org.mfutil.workflow.exception.WorkflowException;
import org.mfutil.workflow.manager.JobManager;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.postel.loader.constants.EventIFA10ErrorEnum;
import com.postel.loader.constants.LoaderConstants;
import com.postel.loader.constants.ValidationConstants;
import com.postel.loader.crypto.DeCryptionUtils;
import com.postel.loader.exception.AcceptanceException;
import com.postel.loader.exception.PagoPaDownloadFileException;
import com.postel.loader.exception.PagoPaException;
import com.postel.loader.exception.PagoPaRuntimeException;
import com.postel.loader.pagopa.bean.BolFileBean;
import com.postel.loader.pagopa.bean.CpxBean;
import com.postel.loader.pagopa.bean.DatiAggiuntiviBean;
import com.postel.loader.pagopa.bean.DuplicatiMassiviFileBean;
import com.postel.loader.pagopa.bean.FileStorageBean;
import com.postel.loader.pagopa.bean.IndexDeclarationBean;
import com.postel.loader.pagopa.bean.LetterSectionBean;
import com.postel.loader.pagopa.bean.NameValueBean;
import com.postel.loader.pagopa.bean.PagoPaNotificheBean;
import com.postel.loader.pagopa.bean.PagoPaPaperEngageDocBean;
import com.postel.loader.pagopa.bean.SAggregationBean;
import com.postel.loader.pagopa.bean.SStampatoreBean;
import com.postel.loader.pagopa.bean.TrkDocumentoBean;
import com.postel.loader.pagopa.dao.PagoPaLoaderDao;
import com.postel.loader.pagopa.sax.CPXFileHandler;
import com.postel.loader.property.LoaderProperties;
import com.postel.loader.util.ApiClientUtil;
import com.postel.loader.util.EncryptionUtils;
import com.postel.loader.util.LoaderFileUtil;
import com.postel.loader.util.ParserInfFile;
import com.postel.loader.util.db.LoaderDBConnectionManager;
import com.postel.loader.util.zip.ZipManager7Zip;
import com.postel.orc.s3.AWSS3Service;
import com.postel.pagopa.constant.PagoPaServiceConstants;
import com.postel.pagopa.data.dao.PagoPaDao;
import com.postel.pagopa.data.model.AcceptanceBean;
import com.postel.pagopa.data.model.ClientConfigBean;
import com.postel.pagopa.data.model.DistintaPickupBean;
import com.postel.pagopa.data.model.DistintaUnicaBean;
import com.postel.pagopa.data.model.DocumentBean;
import com.postel.pagopa.data.model.DocumentDeliveryBean;
import com.postel.pagopa.data.model.FlowBean;
import com.postel.pagopa.data.model.FlowDeliveryBean;
import com.postel.pagopa.data.model.MappingMetadatiBean;
import com.postel.pagopa.data.model.MetadatiClasseDocBean;
import com.postel.pagopa.data.model.TrackingCruscottoInsert;
import com.postel.pagopa.data.model.TrackingDocumentBean;
import com.postel.pagopa.data.model.TrackingFlowBean;
import com.postel.pagopa.enumeration.EventTypeEnum;
import com.postel.pagopa.enumeration.StatoEnum;
import com.postel.pagopa.enumeration.SystemTypeEnum;
import com.postel.restclient.ext.consingress.api.DefaultApi;
import com.postel.restclient.ext.consingress.invoker.ApiResponse;
import com.postel.restclient.ext.consingress.model.InlineObject;
import com.postel.restclient.ext.consingress.model.InlineResponse200;
import com.postel.restclient.ext.consingress.model.OperationResultCodeResponse;
import com.postel.restclient.ext.consingress.model.PaperProgressStatusEvent;
import com.postel.restclient.ext.consingress.model.PreLoadRequest;
import com.postel.restclient.ext.consingress.model.PreLoadResponse;
import com.postel.restclient.formatting.webcheckin.api.CheckInApi;
import com.postel.restclient.formatting.webcheckin.model.CallBackRequestModel;
import com.postel.restclient.formatting.webcheckin.model.FlowInfoRequestModel;
import com.postel.restclient.formatting.webcheckin.model.SignDocsRequestModel;
import com.postel.restclient.pagopa.api.PagopaServicesWfApi;
import com.postel.restclient.pagopa.invoker.ApiClient;
import com.postel.restclient.pagopa.invoker.ApiException;
import com.postel.restclient.pagopa.model.CreateFlowRequest;
import com.postel.restclient.pagopa.model.CreateObjectResponse;

import it.postel.pagopa.opensearchclient.config.OpenSearchConfig;
import it.postel.pagopa.opensearchclient.core.OpenSearchBuilder;

public class PagoPaLoaderHelper {
	private static Log log = LogFactory.getLog(PagoPaLoaderHelper.class);
	private static final int  MEGABYTE = 1024 * 1024;

	public static CpxBean parseCpxXmlFile(File xmlFile) throws ParserConfigurationException, SAXException, IOException {
		CpxBean result = null;
		SAXParserFactory spf = SAXParserFactory.newInstance();
		SAXParser parser = spf.newSAXParser();
		CPXFileHandler cpxFileHandler = new CPXFileHandler();
		parser.parse(xmlFile, (DefaultHandler) cpxFileHandler);
		result = cpxFileHandler.getCpxBean();
		return result;
	}

	public static boolean unzipFile(File workingFile, long jobId, Map<String, ParameterBean> parameters,
			File currentWorkingFolder, Charset charset) {
		boolean result = true;
		String cpxName = FileUtil.getFileNameWithoutExtension(workingFile);
		try {
			boolean decompressione7Zip = Boolean.parseBoolean(LoaderProperties.getPropertyValue(LoaderConstants.DECOMPRESSIONE_7ZIP));
			if(decompressione7Zip)
				ZipManager7Zip.unzip(workingFile, currentWorkingFolder,
						(charset != null) ? charset : Charset.forName("IBM437"));
			else
				ZipManager.unzip(workingFile, currentWorkingFolder,
						(charset != null) ? charset : Charset.forName("IBM437"));
		} catch (Exception e) {
			log.error("Pacchetto di versamento corrotto - Errore nell'UnZip del file CPX: " + cpxName);
			result = false;
		}
		return result;
	}

	public static boolean unzipFile7Zip(File workingFile, long jobId, Map<String, ParameterBean> parameters,
			File currentWorkingFolder, Charset charset) {
		boolean result = true;
		String cpxName = FileUtil.getFileNameWithoutExtension(workingFile);
		try {
			ZipManager7Zip.unzip(workingFile, currentWorkingFolder,
					(charset != null) ? charset : Charset.forName("IBM437"));
		} catch (Exception e) {
			log.error("Pacchetto di versamento corrotto - Errore nell'UnZip del file CPX: " + cpxName);
			result = false;
		}
		return result;
	}

	public static String getUsername(Map<String, ParameterBean> parameters) {
		String username = "batch";
		if (parameters != null && parameters.get("username") != null)
			username = ((ParameterBean) parameters.get("username")).toString();
		return username;
	}

	public static String getErrorForNote(Throwable ex) {
		String note = ExceptionUtils.getStackTrace(ex);
		if (note == null)
			return note;
		return note.substring(0, Math.min(note.length(), 4000));
	}

	public static String getErrorForNote(String descr, int index) {
		if (descr == null)
			return descr;
		return descr.substring(0, Math.min(descr.length(), index));
	}

	public static boolean existsUnusableIndexes() throws DAOException {
		Integer count = PagoPaLoaderDao.countUnusableIndexes(null);
		return (count.intValue() > 0);
	}

	public static boolean unzipFile(File srcFile, File destDir, Charset charset) {
		boolean result = true;
		String fileName = FileUtil.getFileNameWithoutExtension(srcFile);
		try {
			boolean decompressione7Zip = Boolean.parseBoolean(LoaderProperties.getPropertyValue(LoaderConstants.DECOMPRESSIONE_7ZIP));
			if(decompressione7Zip)
				ZipManager7Zip.unzip(srcFile, destDir, (charset != null) ? charset : Charset.forName("IBM437"));
			else
				ZipManager.unzip(srcFile, destDir, (charset != null) ? charset : Charset.forName("IBM437"));				
		} catch (Exception e) {
			log.error("Errore nell'UnZip del file: " + fileName);
			result = false;
		}
		return result;
	}

	public static List<HashMap<String, String>> getFileInfFromCpx(File zipFile, Charset charset, String nomeFile)
			throws Exception {

		List<HashMap<String, String>> listFileInf = new ArrayList<>();
		boolean decompressione7Zip = Boolean.parseBoolean(LoaderProperties.getPropertyValue(LoaderConstants.DECOMPRESSIONE_7ZIP));
		if(decompressione7Zip) { 
			if (zipFile.getName().endsWith(".7z")) {
				// unzip file seven7Z
				  SevenZFile sevenZ = new SevenZFile(zipFile);
				  ParserInfFile parserInf = new ParserInfFile();	
				  try {
			            SevenZArchiveEntry entry;
			            while ((entry = sevenZ.getNextEntry()) != null) {
			                if (!entry.isDirectory() && entry.getName().toLowerCase().endsWith(nomeFile)) {
			                    HashMap<String, String> mapInfData = parserInf.processLineByLineInputStream(sevenZ.getInputStream(entry));
			                    listFileInf.add(mapInfData);
			                }
			            }
			        } finally {
			            if (sevenZ != null)
			                sevenZ.close();
			        }
			} else if (zipFile.getName().endsWith(".zip")) {
				// unzip file zip
				org.apache.commons.compress.archivers.zip.ZipFile zip = new org.apache.commons.compress.archivers.zip.ZipFile(zipFile);
				ParserInfFile parserInf = new ParserInfFile();
				try {
					for (Enumeration<? extends org.apache.commons.compress.archivers.zip.ZipArchiveEntry> e = zip.getEntries(); e.hasMoreElements();) {
						org.apache.commons.compress.archivers.zip.ZipArchiveEntry entry = e.nextElement();
						if (!entry.isDirectory() && entry.getName().toLowerCase().endsWith(nomeFile)) {
							HashMap<String, String> mapInfData = parserInf
									.processLineByLineInputStream(zip.getInputStream(entry));
							listFileInf.add(mapInfData);
						}
					}
				} finally {
					if (zip != null)
						zip.close();
				}
			}
			  
		}else {
			ZipFile zip = new ZipFile(zipFile);
			ParserInfFile parserInf = new ParserInfFile();
			try {
				for (Enumeration<? extends ZipEntry> e = zip.entries(); e.hasMoreElements();) {
					ZipEntry entry = e.nextElement();
					if (!entry.isDirectory() && entry.getName().toLowerCase().endsWith(nomeFile)) {
						HashMap<String, String> mapInfData = parserInf
								.processLineByLineInputStream(zip.getInputStream(entry));
						listFileInf.add(mapInfData);
					}
				}
			} finally {
				if (zip != null)
					zip.close();
			}
		}
		return listFileInf;
	}

	public static List<CpxBean> getFileXmlFromCpx(File zipFile, Charset charset) throws Exception, AcceptanceException {

//		List<CpxBean> listaXml = new ArrayList<>();
//		boolean decompressione7Zip = Boolean.parseBoolean(LoaderProperties.getPropertyValue(LoaderConstants.DECOMPRESSIONE_7ZIP));
//		if(decompressione7Zip) {
//			org.apache.commons.compress.archivers.zip.ZipFile zip = new org.apache.commons.compress.archivers.zip.ZipFile(zipFile);
//			SAXParserFactory spf = SAXParserFactory.newInstance();
//			SAXParser parser = spf.newSAXParser();
//			try {
//				for (Enumeration<? extends org.apache.commons.compress.archivers.zip.ZipArchiveEntry> e = zip.getEntries(); e.hasMoreElements();) {
//					org.apache.commons.compress.archivers.zip.ZipArchiveEntry entry = e.nextElement();
//					if (!entry.isDirectory()) {
//						String name = entry.getName().toLowerCase();
//						if (name.endsWith("_out.xml") || name.endsWith("_i.xml") || name.endsWith("_i.pdf.xml"))
//							try {
//								CPXFileHandler cpxFileHandler = new CPXFileHandler();
//								parser.parse(zip.getInputStream(entry), (DefaultHandler) cpxFileHandler);
//								listaXml.add(cpxFileHandler.getCpxBean());
//							} catch (Exception ex) {
//								throw new AcceptanceException("30",
//										"File xml indici non corretto: parser xml fallito con errore: " + ex.getMessage());
//							}
//					}
//				}
//			} finally {
//				if (zip != null)
//					zip.close();
//			}
	    boolean decompressione7Zip = Boolean.parseBoolean(LoaderProperties.getPropertyValue(LoaderConstants.DECOMPRESSIONE_7ZIP));
	    List<CpxBean> listaXml = new ArrayList<>();

	    if (decompressione7Zip) {
	    	if (zipFile.getName().endsWith(".7z")) {
				// unzip file seven7Z
	    		 SevenZFile sevenZ = null;
	 	        SAXParserFactory spf = SAXParserFactory.newInstance();
	 	        SAXParser parser = spf.newSAXParser();
	 	        try {
	 	            sevenZ = new SevenZFile(zipFile);
	 	            SevenZArchiveEntry entry;
	 	            while ((entry = sevenZ.getNextEntry()) != null) {
	 	                if (!entry.isDirectory()) {
	 	                    String name = entry.getName().toLowerCase();
	 	                    if (name.endsWith("_out.xml") || name.endsWith("_i.xml") || name.endsWith("_i.pdf.xml")) {
	 	                        try {
	 	                            CPXFileHandler cpxFileHandler = new CPXFileHandler();
	 	                            parser.parse(sevenZ.getInputStream(entry), (DefaultHandler) cpxFileHandler);
	 	                            listaXml.add(cpxFileHandler.getCpxBean());
	 	                        } catch (Exception ex) {
	 	                            throw new AcceptanceException("30", "File xml indici non corretto: parser xml fallito con errore: " + ex.getMessage());
	 	                        }
	 	                    }
	 	                }
	 	            }
	 	        } finally {
	 	            if (sevenZ != null) {
	 	                sevenZ.close();
	 	            }
	 	        }
			} else if (zipFile.getName().endsWith(".zip")) {
				// unzip file zip
				org.apache.commons.compress.archivers.zip.ZipFile zip = new org.apache.commons.compress.archivers.zip.ZipFile(zipFile);
				SAXParserFactory spf = SAXParserFactory.newInstance();
				SAXParser parser = spf.newSAXParser();
				try {
					for (Enumeration<? extends org.apache.commons.compress.archivers.zip.ZipArchiveEntry> e = zip.getEntries(); e.hasMoreElements();) {
						org.apache.commons.compress.archivers.zip.ZipArchiveEntry entry = e.nextElement();
						if (!entry.isDirectory()) {
							String name = entry.getName().toLowerCase();
							if (name.endsWith("_out.xml") || name.endsWith("_i.xml") || name.endsWith("_i.pdf.xml"))
								try {
									CPXFileHandler cpxFileHandler = new CPXFileHandler();
									parser.parse(zip.getInputStream(entry), (DefaultHandler) cpxFileHandler);
									listaXml.add(cpxFileHandler.getCpxBean());
								} catch (Exception ex) {
									throw new AcceptanceException("30",
											"File xml indici non corretto: parser xml fallito con errore: " + ex.getMessage());
								}
						}
					}
				} finally {
					if (zip != null)
						zip.close();
				}
			}
	       
		}else {
			ZipFile zip = new ZipFile(zipFile);
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser parser = spf.newSAXParser();
			try {
				for (Enumeration<? extends ZipEntry> e = zip.entries(); e.hasMoreElements();) {
					ZipEntry entry = e.nextElement();
					if (!entry.isDirectory()) {
						String name = entry.getName().toLowerCase();
						if (name.endsWith("_out.xml") || name.endsWith("_i.xml") || name.endsWith("_i.pdf.xml"))
							try {
								CPXFileHandler cpxFileHandler = new CPXFileHandler();
								parser.parse(zip.getInputStream(entry), (DefaultHandler) cpxFileHandler);
								listaXml.add(cpxFileHandler.getCpxBean());
							} catch (Exception ex) {
								throw new AcceptanceException("30",
										"File xml indici non corretto: parser xml fallito con errore: " + ex.getMessage());
							}
					}
				}
			} finally {
				if (zip != null)
					zip.close();
			}
		}
		
		return listaXml;
	}

	public static boolean verificaNomeFileAccettazione(String name, String pattern) {
		String regola = (pattern != null) ? pattern
				: LoaderProperties.getPropertyValue("REGOLA_NOME_FILE_ACCETTAZIONE");
		Pattern p = Pattern.compile(regola);
		if (p.matcher(name).matches())
			return true;
		return false;
	}

	public static String getUpToNChar(String value, int index) {
		if (value == null)
			return value;
		return value.substring(0, Math.min(value.length(), index));
	}

	public static List<HashMap<String, String>> getFileInfFromCpxAndCalculateSizeDocs(File zipFile, Charset charset,
			AcceptanceBean acceptanceBean) throws Exception {

		List<HashMap<String, String>> listFileInf = new ArrayList<>();
		boolean decompressione7Zip = Boolean.parseBoolean(LoaderProperties.getPropertyValue(LoaderConstants.DECOMPRESSIONE_7ZIP));
		if(decompressione7Zip) {
			if (zipFile.getName().endsWith(".7z")) {
				// unzip file seven7Z
				SevenZFile sevenZ = new SevenZFile(zipFile);
				ParserInfFile parserInf = new ParserInfFile();
				long size = 0L;
				try {
		            SevenZArchiveEntry entry;
		            while ((entry = sevenZ.getNextEntry()) != null) {
		                if (!entry.isDirectory()) {
		                    String entryName = entry.getName().toLowerCase();
		                    if (!entryName.endsWith(".inf") && !entryName.endsWith("_out.xml") && !entryName.endsWith("_i.xml")
		                            && !entryName.endsWith("_i.pdf.xml")) {
		                        size += entry.getSize();
		                        continue;
		                    }
		                    if (entryName.endsWith(".inf")) {
		                        HashMap<String, String> mapInfData = parserInf.processLineByLineInputStream(sevenZ.getInputStream(entry));
		                        listFileInf.add(mapInfData);
		                        acceptanceBean.setNomeFileInf(entry.getName());
		                    }
		                }
		            }
		            acceptanceBean.setSiSizeDocs(size);
		        } finally {
		            if (sevenZ != null)
		                sevenZ.close();
		        }
			} else if (zipFile.getName().endsWith(".zip")) {
				// unzip file zip
				org.apache.commons.compress.archivers.zip.ZipFile zip = new org.apache.commons.compress.archivers.zip.ZipFile(zipFile);
				ParserInfFile parserInf = new ParserInfFile();
				long size = 0L;
				try {
					for (Enumeration<? extends org.apache.commons.compress.archivers.zip.ZipArchiveEntry> e = zip.getEntries(); e.hasMoreElements();) {
						org.apache.commons.compress.archivers.zip.ZipArchiveEntry entry = e.nextElement();
						if (!entry.isDirectory()) {
							String entryName = entry.getName().toLowerCase();
							if (!entryName.endsWith(".inf") && !entryName.endsWith("_out.xml") && !entryName.endsWith("_i.xml")
									&& !entryName.endsWith("_i.pdf.xml")) {
								size += entry.getSize();
								continue;
							}
							if (entryName.endsWith(".inf")) {
								HashMap<String, String> mapInfData = parserInf
										.processLineByLineInputStream(zip.getInputStream(entry));
								listFileInf.add(mapInfData);
								acceptanceBean.setNomeFileInf(entry.getName());
							}
						}
					}
					acceptanceBean.setSiSizeDocs(size);
				} finally {
					if (zip != null)
						zip.close();
				}
			}
			
			
		}else {

			ZipFile zip = new ZipFile(zipFile);
			ParserInfFile parserInf = new ParserInfFile();
			long size = 0L;
			try {
				for (Enumeration<? extends ZipEntry> e = zip.entries(); e.hasMoreElements();) {
					ZipEntry entry = e.nextElement();
					if (!entry.isDirectory()) {
						String entryName = entry.getName().toLowerCase();
						if (!entryName.endsWith(".inf") && !entryName.endsWith("_out.xml") && !entryName.endsWith("_i.xml")
								&& !entryName.endsWith("_i.pdf.xml")) {
							size += entry.getSize();
							continue;
						}
						if (entryName.endsWith(".inf")) {
							HashMap<String, String> mapInfData = parserInf
									.processLineByLineInputStream(zip.getInputStream(entry));
							listFileInf.add(mapInfData);
							acceptanceBean.setNomeFileInf(entry.getName());
						}
					}
				}
				acceptanceBean.setSiSizeDocs(size);
			} finally {
				if (zip != null)
					zip.close();
			}
		}
		return listFileInf;
	}

	public static String getNomeLottoFromInf(HashMap<String, String> infData) {
		if (infData.containsKey("Nome_lotto") && infData.get("Nome_lotto") != null
				&& !((String) infData.get("Nome_lotto")).trim().isEmpty())
			return infData.get("Nome_lotto");
		if (infData.containsKey("Nomelotto") && infData.get("Nomelotto") != null
				&& !((String) infData.get("Nomelotto")).trim().isEmpty())
			return infData.get("Nomelotto");
		return null;
	}

	public static String getNumeroDocumentiFromInf(HashMap<String, String> infData) {
		if (infData.containsKey("Numero_documenti") && infData.get("Numero_documenti") != null
				&& !((String) infData.get("Numero_documenti")).trim().isEmpty())
			return infData.get("Numero_documenti");
		if (infData.containsKey("numero_documenti") && infData.get("numero_documenti") != null
				&& !((String) infData.get("numero_documenti")).trim().isEmpty())
			return infData.get("numero_documenti");
		return null;
	}

	public static String getNumeroPagineFromInf(HashMap<String, String> infData) {
		if (infData.containsKey("Numero_pagine") && infData.get("Numero_pagine") != null
				&& !((String) infData.get("Numero_pagine")).trim().isEmpty())
			return infData.get("Numero_pagine");
		if (infData.containsKey("numero_pagine") && infData.get("numero_pagine") != null
				&& !((String) infData.get("numero_pagine")).trim().isEmpty())
			return infData.get("numero_pagine");
		return null;
	}

	public static String getNomeProceduraFromInf(HashMap<String, String> infData) {
		if (infData.containsKey("Nome_procedura") && infData.get("Nome_procedura") != null
				&& !((String) infData.get("Nome_procedura")).trim().isEmpty())
			return infData.get("Nome_procedura");
		return null;
	}

	public static String getServizioFromInf(HashMap<String, String> infData) {
		if (infData.containsKey("servizio") && infData.get("servizio") != null
				&& !((String) infData.get("servizio")).trim().isEmpty())
			return infData.get("servizio");
		if (infData.containsKey("Servizio") && infData.get("Servizio") != null
				&& !((String) infData.get("Servizio")).trim().isEmpty())
			return infData.get("Servizio");
		return null;
	}

	public static String getDataInoltroInf(HashMap<String, String> infData) {
		if (infData.containsKey("Data_inoltro") && infData.get("Data_inoltro") != null
				&& !((String) infData.get("Data_inoltro")).trim().isEmpty())
			return infData.get("Data_inoltro");
		if (infData.containsKey("data_inoltro") && infData.get("data_inoltro") != null
				&& !((String) infData.get("data_inoltro")).trim().isEmpty())
			return infData.get("data_inoltro");
		return null;
	}

	public static boolean checkInfoJobFromInf(HashMap<String, String> infData) {
		if (infData.containsKey("[info_job]") || infData.containsKey("[Info_job]"))
			return true;
		return false;
	}

	public static boolean checkInfoArchiveFromInf(HashMap<String, String> infData) {
		if (infData.containsKey("[info_archive]") || infData.containsKey("[Info_archive]"))
			return true;
		return false;
	}

	public static void modifyZipReplaceRootFile(File zipFile, String fileToReplaceName, File newFile)
			throws PagoPaException {
		Path myFilePath = Paths.get(newFile.getPath(), new String[0]);
		Path zipFilePath = Paths.get(zipFile.getPath(), new String[0]);
		try (FileSystem fs = FileSystems.newFileSystem(zipFilePath, (ClassLoader) null)) {
			Path fileInsideZipPath = fs.getPath(File.separator.concat(fileToReplaceName), new String[0]);
			Files.copy(myFilePath, fileInsideZipPath, new CopyOption[] { StandardCopyOption.REPLACE_EXISTING });
		} catch (Exception e) {
			throw new PagoPaException(e);
		}
	}

	public static String dateToGregorianWithFormat(Date date, String format) throws Exception {
		return DateUtils.getFormattedDate(date, format);
	}

	public static String generaFlowId(Date date, long jobId) throws DAOException, ParseException {
		String anno = DateUtils.getFormattedDate((date != null) ? date : new Date(), "yyyy");
		String annolastChar = anno.substring(anno.length() - 1);
		String hexJobId = StringUtil.leftFillStringLength(Long.toHexString(jobId), 7, "0");
		return LoaderProperties.getPropertyValue("ACCETTAZIONE_FLOW_ID_LETTERS").concat(annolastChar).concat(hexJobId);
	}
	
	public static String generaCodiceRun(Date date, long seqRunId) throws DAOException, ParseException {
		String anno = DateUtils.getFormattedDate((date != null) ? date : new Date(), "yyyy");
		String annolastChar = anno.substring(anno.length() - 1);
		String hexJobId = StringUtil.leftFillStringLength(Long.toHexString(seqRunId), 7, "0");
		return annolastChar.concat(hexJobId);
	}

	public static String calcolaBackupPath(String backupFolder, Long jobid, Date jobStartDate) throws ParseException {
		if (jobStartDate == null)
			jobStartDate = new Date();
		String dateVal = DateUtils.getFormattedDate(jobStartDate, "yyyyMMdd");
		String backupDirpath = backupFolder + "/" + dateVal + "/" + jobid;
		return backupDirpath;
	}

	public static void writeFileDeliveryToNasArea(String fileNameParam, String pathNasInvio, File inputFile,
			boolean isTimestamp) throws PagoPaException {
		writeFileDeliveryToNasArea(fileNameParam, pathNasInvio, inputFile, isTimestamp, true);
	}

	public static void writeFileDeliveryToFtpArea(String pathInvio, String ipServerFtp, Integer portServerFtp,
			String usernameFtp, String passwdFtp, File inputFile, String sysDescr, String fileNameParam)
			throws Exception {
		writeFileDeliveryToFtpArea(pathInvio, ipServerFtp, portServerFtp, usernameFtp, passwdFtp, inputFile, sysDescr,
				fileNameParam, true);
	}

	public static void writeFileDeliveryToNasArea(String fileNameParam, String pathNasInvio, File inputFile,
			boolean isTimestamp, boolean createTrailerFile) throws PagoPaException {
		if (pathNasInvio == null)
			throw new PagoPaException("Path invio delivery non configurato nella CONFIG_SISTEMA_CLASSEDOC");
		File outputDir = new File(pathNasInvio);
		if (!outputDir.exists()) {
			log.error("La directory " + outputDir.getPath() + " non esiste");
			throw new PagoPaException("La directory " + outputDir.getPath() + " non esiste");
		}
		File destFile = isTimestamp ? new File(pathNasInvio, FileUtil.getFileNameWithoutExtension(fileNameParam))
				: new File(pathNasInvio, fileNameParam);
		if (isTimestamp && !unzipFile(inputFile, destFile, Charset.forName("IBM437")))
			throw new PagoPaException(
					"Impossibile copiare il file " + inputFile.getPath() + " nella directory " + destFile.getPath());
		if (!destFile.exists() && !LoaderFileUtil.copyFile(inputFile, destFile, true))
			throw new PagoPaException(
					"Impossibile copiare il file " + inputFile.getPath() + " nella directory " + pathNasInvio);
		if (!LoaderFileUtil.setPermissionsRecursivly(destFile, "rwxrwxr-x"))
			throw new PagoPaException(
					"Errore permessi file NFS " + inputFile.getPath() + " nella directory " + pathNasInvio);
		if (createTrailerFile) {
			File fileTrailer = isTimestamp
					? new File(destFile, FileUtil.getFileNameWithoutExtension(fileNameParam).concat(".t"))
					: new File(pathNasInvio, FileUtil.getFileNameWithoutExtension(fileNameParam).concat(".T"));
			if (!fileTrailer.exists() && !LoaderFileUtil.createNewFile(fileTrailer))
				throw new PagoPaException("Impossibile creare il file trailer" + fileTrailer.getPath());
			if (!LoaderFileUtil.setPermissionsRecursivly(fileTrailer, "rwxrwxr-x"))
				throw new PagoPaException(
						"Errore permessi file NFS " + inputFile.getPath() + " nella directory " + pathNasInvio);
		}
	}

	public static void writeFileDeliveryToFtpArea(String pathInvio, String ipServerFtp, Integer portServerFtp,
			String usernameFtp, String passwdFtp, File inputFile, String sysDescr, String fileNameParam,
			boolean createTrailerFile) throws Exception {
		FTPClient ftpClient = null;
		try {
			if (ipServerFtp == null || portServerFtp == null || usernameFtp == null || passwdFtp == null
					|| pathInvio == null)
				throw new PagoPaException("Errore: Configurazione FTP non corretta per sistema: [" + sysDescr + "].");
			String passwdFtpDcrpt = EncryptionUtils.decrypt(passwdFtp);
			ftpClient = FtpUtil.getConnection(ipServerFtp, portServerFtp.intValue(), usernameFtp, passwdFtpDcrpt);
			if (!FtpUtil.existsFolder(ftpClient, pathInvio, ipServerFtp, portServerFtp.intValue(), usernameFtp,
					passwdFtpDcrpt)) {
				log.error("La directory " + pathInvio + " non esiste");
				throw new PagoPaException("La directory " + pathInvio + " non esiste");
			}
			InputStream isFileInput = new ByteArrayInputStream(FileUtils.readFileToByteArray(inputFile));
			FtpUtil.transferFileFromInputStream(ftpClient, isFileInput, fileNameParam, pathInvio, ipServerFtp,
					portServerFtp.intValue(), usernameFtp, passwdFtpDcrpt, true);
			if (createTrailerFile) {
				String trailerFileName = FileUtil.getFileNameWithoutExtension(fileNameParam).concat(".T");
				InputStream is = new ByteArrayInputStream(new byte[0]);
				FtpUtil.transferFileFromInputStream(ftpClient, is, trailerFileName, pathInvio, ipServerFtp,
						portServerFtp.intValue(), usernameFtp, passwdFtpDcrpt, true);
			}
		} catch (Throwable t) {
			log.error("error writeFileDeliveryToFtpArea", t);
			throw new Exception("error writeFileDeliveryToFtpArea", t);
		} finally {
			if (ftpClient != null)
				try {
					FtpUtil.closeConnection(ftpClient);
				} catch (FTPException e) {
					log.error("closeConnection error");
				}
		}
	}

	public static void backupDeliveryFile(String backupFolder, String inputFolderParam, String fileNameParam,
			JobBean job) throws Exception {
		try {
			boolean backupFileDelivery = LoaderProperties.getBooleanPropertyValue("BOOL_BACKUP_FILE_DELIVERY", true);
			if (backupFileDelivery) {
				File inputFile = new File(inputFolderParam, fileNameParam);
				backupFile(backupFolder, inputFile, job);
			}
		} catch (Exception e) {
			log.error("BackupDeliveryFile Error - " + e.getMessage(), e);
			throw new PagoPaException("BackupDeliveryFile Error - " + e.getMessage(), e);
		}
	}

	public static void backupFile(String backupFolder, File inputFile, JobBean job)
			throws PagoPaException, ParseException {
		if (!inputFile.exists())
			throw new PagoPaException("InputFile delivery non trovato nel path " + inputFile.getPath());
		String pathBackup = calcolaBackupPath(backupFolder, Long.valueOf(job.getId()), job.getStartTimestamp());
		File backupFile = new File(pathBackup, inputFile.getName());
		if (!FileUtil.isDirectory(pathBackup) && !FileUtil.makedir(pathBackup)) {
			log.error("Unable to create backupFolder " + pathBackup);
			throw new PagoPaException("Unable to create backupFolder" + pathBackup);
		}
		if (!backupFile.getParentFile().exists())
			backupFile.getParentFile().mkdir();
		if (!LoaderFileUtil.copyFile(inputFile, backupFile, true)) {
			log.error("Unable to backup file " + inputFile.getPath() + " to file " + backupFile.getPath()
					+ " available space " + backupFile.getParentFile().getFreeSpace() + " original file size "
					+ inputFile.length());
			throw new PagoPaException("Unable to backup file " + inputFile.getPath() + " to file "
					+ backupFile.getPath() + " available space " + backupFile.getParentFile().getFreeSpace()
					+ " original file size " + inputFile.length());
		}
	}

	public static String renameReceipt(String originalName) {
		String result = originalName;
		if (originalName != null && StringUtils.countMatches(originalName, "-") > 2)
			result = originalName.substring(0, originalName.indexOf("-"))
					+ originalName.substring(originalName.lastIndexOf("-"));
		return result;
	}

	public static String logRequestResponse(Object inputJson, boolean isRequest, Map<String, String> headers) {
		String result = "";
		String headersVal = "";
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
//		mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"));
		mapper.setDateFormat(new SimpleDateFormat(LoaderConstants.LOG_REQ_RESP_DATE_FORMAT));
		
		org.threeten.bp.format.DateTimeFormatter DATE_TIME_FORMATTER= org.threeten.bp.format.DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss XXX");
		mapper.registerModule((Module) new JavaTimeModule());
		SimpleModule simpleModule = new SimpleModule();
		simpleModule.addSerializer(OffsetDateTime.class, new JsonSerializer<OffsetDateTime>() {
			public void serialize(OffsetDateTime offsetDateTime, JsonGenerator jsonGenerator,
					SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
				jsonGenerator.writeString(DateTimeFormatter.ISO_DATE_TIME.format(offsetDateTime));
			}
		});
		mapper.registerModule((Module) simpleModule);
		mapper.registerModule((Module) new JavaTimeModule());
		SimpleModule simpleModule2 = new SimpleModule();
//		simpleModule2.addSerializer(OffsetDateTime.class, new JsonSerializer<OffsetDateTime>() {
//			public void serialize(OffsetDateTime offsetDateTime, JsonGenerator jsonGenerator,
//					SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
//				jsonGenerator.writeString(
//						DateTimeFormatter.ISO_DATE_TIME.format(OffsetDateTime.parse(offsetDateTime.toString())));
//			}
//		});
		simpleModule2.addSerializer(org.threeten.bp.OffsetDateTime.class, new JsonSerializer<org.threeten.bp.OffsetDateTime>() {
			@Override
			public void serialize(org.threeten.bp.OffsetDateTime offsetDateTime, JsonGenerator jsonGenerator, 
					SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
				jsonGenerator.writeString(DATE_TIME_FORMATTER.format(offsetDateTime));
			}
		});
		mapper.registerModule((Module) simpleModule2);
		try {
			if (inputJson != null)
				result = mapper.writeValueAsString(inputJson);
			log.debug((isRequest ? " REQUEST: " : " RESPONSE: ") + "\n" + result);
			if (headers != null) {
				headersVal = mapper.writeValueAsString(headers);
				log.debug((isRequest ? " REQUEST HEADERS:" : " RESPONSE HEADERS: ") + "\n" + headersVal);
			}
		} catch (IOException e) {
			log.error("Errore formattazione json", e);
		}
		return result;
	}

	public static String getStringFromObject(Object inputJson) {
		String result = "";
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		try {
			if (inputJson != null)
				result = mapper.writeValueAsString(inputJson);
		} catch (IOException e) {
			log.error("Errore formattazione json", e);
		}
		return result;
	}

	public static FlowDeliveryBean insertInvioDelivery(JobBean job, DBConnection dbConnection, PagoPaDao pagopaDao,
			String eventNameParam, FlowDeliveryBean flowDelivery) throws Exception {
		FlowBean flowBean = pagopaDao.getLottoByIdLotto(dbConnection, flowDelivery.getFlowId());
		Integer statoDelivery = Integer.valueOf(StatoEnum.getValueFromKey("DeliveryInviata"));
		pagopaDao.updateStatoLottoDelivery(dbConnection, flowDelivery.getFlowDeliveryId(), statoDelivery);
		Integer sourceSystem = flowDelivery.getRifSourceSystem();
		Integer destSystem = flowDelivery.getRifDeliverySystem();
		TrackingFlowBean trkFlowBean = TrackingFlowBean.createFromFlowDeliveryBean(flowBean, flowDelivery,
				EventTypeEnum.DeliveryInviata.getValue(), sourceSystem, destSystem, eventNameParam);
		pagopaDao.insertLottoTracking(dbConnection, trkFlowBean);
		pagopaDao.updateStatoDocumentoDeliveryByIdLottoDel(dbConnection, flowDelivery.getFlowDeliveryId(),
				statoDelivery.intValue());
		List<DocumentDeliveryBean> listDocDelivery = pagopaDao.getListaDocDeliveryByLottoDeliveryId(dbConnection,
				flowDelivery.getFlowDeliveryId());
		if (listDocDelivery != null && listDocDelivery.size() > 0) {
			List<Long> seqListDocTrk = pagopaDao.getListSequenceValue(dbConnection, "SEQ_DOCUMENTO_TRACKING",
					Integer.valueOf(listDocDelivery.size()));
			List<List<Object>> parametersDocTrk = new ArrayList<>();
			for (DocumentDeliveryBean delBean : listDocDelivery) {
				TrackingDocumentBean trkDocBean = TrackingDocumentBean.createFromDeliveryBean(delBean,
						trkFlowBean.getEvent(), sourceSystem, flowDelivery.getFlowId());
				parametersDocTrk.add(TrackingDocumentBean.getInsertParams(trkDocBean, seqListDocTrk.get(0)));
				seqListDocTrk.remove(0);
			}
			pagopaDao.batchUpdate(dbConnection,
					"INSERT INTO DOCUMENTO_TRACKING(TRACKING_ID,RIF_ID_DOCUMENTO,RIF_ID_DOCUMENTO_DELIVERY,RIF_SISTEMA_SORGENTE,RIF_SISTEMA_DESTINAZIONE,EVENTO,DATA_EVENTO,RIF_CLASSE_DOC,ID_LOTTO_CLIENTE,CODICE_LOTTO_CLIENTE,JOB_ID,RIF_ID_LOTTO_DELIVERY,NOME_FILE,SIZE_FILE,DATA_FILE,NUMERO_PAGINE,HASH_FILE_ORIGINALE,HASH_FILE_CALCOLATO,RIF_ESITO_DELIVERY,CODICE_DOCUMENTO,RIF_ID_LOTTO,ZCODE,META_S_01,META_S_02,META_S_03,META_S_04,META_S_05,META_S_06,META_S_07,META_S_08,META_S_09,META_S_10,META_S_11,META_S_12,META_S_13,META_S_14,META_S_15,META_S_16,META_D_01,META_D_02,META_D_03,META_D_04,RIF_ID_CLIENTE_CONFIG,JOB_ID_LOTTO_CLIENTE,COD_MOTIVO_SCARTO,DESCR_MOTIVO_SCARTO,META_S_17, META_S_18, META_S_19, META_S_20, META_S_21, META_S_22, META_S_23, META_S_24, META_S_25, META_S_26, META_S_27, META_S_28, META_S_29, META_S_30, META_S_31, META_S_32, META_S_33, META_S_34, META_S_35, META_S_36, META_S_37, META_S_38, META_S_39, META_S_40, META_D_05, META_D_06, META_D_07, META_D_08, META_D_09, META_D_10,META_S_41, META_S_42, META_S_43, META_S_44, META_S_45, META_S_46, META_S_47, META_S_48, META_S_49, META_S_50) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
					parametersDocTrk);
		}
		return flowDelivery;
	}

	public static byte[] getFileInputStreamFromCpx(File zipFile, Charset charset, String fileName)
			throws Exception, AcceptanceException {
		boolean decompressione7Zip = Boolean.parseBoolean(LoaderProperties.getPropertyValue(LoaderConstants.DECOMPRESSIONE_7ZIP));
		if(decompressione7Zip) {
			if (zipFile.getName().endsWith(".7z")) {
				// unzip file seven7Z
				SevenZFile sevenZ = new SevenZFile(zipFile);
		        try {
		            SevenZArchiveEntry entry;
		            while ((entry = sevenZ.getNextEntry()) != null) {
		                if (!entry.isDirectory()) {
		                    String name = entry.getName();
		                    if (name.equals(fileName)) {
		                        try {
		                            return IOUtils.toByteArray(sevenZ.getInputStream(entry));
		                        } catch (Exception ex) {
		                            throw new AcceptanceException("30", "File xml indici non corretto: parser xml fallito con errore: " + ex.getMessage());
		                        }
		                    }
		                }
		            }
		        } finally {
		            if (sevenZ != null)
		                sevenZ.close();
		        }
			} else if (zipFile.getName().endsWith(".zip")) {
				// unzip file zip
				org.apache.commons.compress.archivers.zip.ZipFile zip = new org.apache.commons.compress.archivers.zip.ZipFile(zipFile);
				try {
					for (Enumeration<? extends org.apache.commons.compress.archivers.zip.ZipArchiveEntry> e = zip.getEntries(); e.hasMoreElements();) {
						org.apache.commons.compress.archivers.zip.ZipArchiveEntry entry = e.nextElement();
						if (!entry.isDirectory()) {
							String name = entry.getName();
							if (name.equals(fileName))
								try {
									return IOUtils.toByteArray(zip.getInputStream(entry));
								} catch (Exception ex) {
									throw new AcceptanceException("30",
											"File xml indici non corretto: parser xml fallito con errore: " + ex.getMessage());
								}
						}
					}
				} finally {
					if (zip != null)
						zip.close();
				}
			}		
			
		}else {
			ZipFile zip = new ZipFile(zipFile);
			try {
				for (Enumeration<? extends ZipEntry> e = zip.entries(); e.hasMoreElements();) {
					ZipEntry entry = e.nextElement();
					if (!entry.isDirectory()) {
						String name = entry.getName();
						if (name.equals(fileName))
							try {
								return IOUtils.toByteArray(zip.getInputStream(entry));
							} catch (Exception ex) {
								throw new AcceptanceException("30",
										"File xml indici non corretto: parser xml fallito con errore: " + ex.getMessage());
							}
					}
				}
			} finally {
				if (zip != null)
					zip.close();
			}
		}
		
		return null;
	}

	public static String getTokenIdpSts() throws Exception {
		OutputStream os = null;
		BufferedReader br = null;
		HttpURLConnection conn = null;
		try {
			Map<String, Object> params = new LinkedHashMap<>();
			params.put("client_id", LoaderProperties.getPropertyValue("WS_TOKEN_IDP_STS_CLIENT_ID_PARAM"));
			params.put("client_secret", LoaderProperties.getPropertyValue("WS_TOKEN_IDP_STS_CLIENT_SECRET_PARAM"));
			params.put("grant_type", LoaderProperties.getPropertyValue("WS_TOKEN_IDP_STS_GRANT_TYPE_PARAM"));
			params.put("scope", LoaderProperties.getPropertyValue("WS_TOKEN_IDP_STS_SCOPE_PARAM"));
			StringBuilder postData = new StringBuilder();
			for (Map.Entry<String, Object> param : params.entrySet()) {
				if (postData.length() != 0)
					postData.append('&');
				postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
				postData.append('=');
				postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
			}
			byte[] postDataBytes = postData.toString().getBytes("UTF-8");
			logRequestResponse(params, true, null);
			String urlVal = LoaderProperties.getPropertyValue("WS_TOKEN_IDP_STS_BASE_PATH");
			URL url = new URL(urlVal);
			conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
			os = conn.getOutputStream();
			os.write(postDataBytes);
			os.flush();
			if (conn.getResponseCode() != 200)
				throw new Exception("Failed : HTTP error code : " + conn.getResponseCode());
			br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			JsonObject respJsonObj = (JsonObject) (new Gson()).fromJson(br, JsonObject.class);
			log.debug(" RESPONSE: \n" + respJsonObj);
			if (respJsonObj.get("access_token") == null)
				throw new PagoPaException("Failed : getTokenIdpSts - access_token is null ");
			return respJsonObj.get("access_token").getAsString();
		} catch (Exception e) {
			throw new Exception(e);
		} finally {
			if (br != null)
				br.close();
			if (os != null)
				os.close();
			if (conn != null)
				conn.disconnect();
		}
	}

	public static void uploadDocWithPresignedUrl(String presignedUrl, InputStream input, String contentType)
			throws PagoPaException {
		uploadDocWithPresignedUrl(presignedUrl, input, contentType, null);
	}
	
	public static void uploadDocFileConsWithPresignedUrl(String presignedUrl, File file, String contentType,
			Map<String, String> headers) throws PagoPaException {
		log.debug("uploadDocFileConsWithPresignedUrl started");
		log.debug("presignedUrl: " + presignedUrl + ", contentType " + contentType + ", headers " + headers);
		CloseableHttpClient httpClient = null;
		
		try {
			HttpPut httpPut = new HttpPut(presignedUrl);
			String contentTypeVal = (contentType != null) ? contentType : "application/octet-stream";
			log.debug("contentTypeVal: " + contentTypeVal);
			httpPut.setHeader("Content-Type", contentTypeVal);
			if (headers != null && !headers.isEmpty())
				for (Map.Entry<String, String> entry : headers.entrySet())
					httpPut.setHeader(entry.getKey(), entry.getValue());
			httpPut.setEntity(new FileEntity(file));
			CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
			// Configurazione del client HTTP con supporto HTTPS e credenziali
			httpClient = HttpClients.custom().setDefaultCredentialsProvider(credentialsProvider).build();
			HttpResponse httpResponsePut = httpClient.execute(httpPut);
			int responseCode = httpResponsePut.getStatusLine().getStatusCode();
			if (responseCode != 200)
				throw new PagoPaException("Failed : HTTP error code : " + responseCode);
			EntityUtils.consume(httpResponsePut.getEntity());
		}
		catch (Exception e) {
			throw new PagoPaException(e);
		} 
	}

	public static void uploadDocWithPresignedUrl(String presignedUrl, InputStream input, String contentType,
			Map<String, String> headers) throws PagoPaException {
		HttpURLConnection connection = null;
		try {
			log.debug("presignedUrl: " + presignedUrl + ", contentType " + contentType + ", headers " + headers);
			String awsReverseProxy = LoaderProperties.getPropertyValue("AWS_REVERSE_PROXY");
			if (awsReverseProxy != null) {
				log.debug("Original presignedUrl" + presignedUrl);
				presignedUrl = presignedUrl.replaceAll("s3.eu-south-1.amazonaws.com", awsReverseProxy);
				log.debug("Modified presignedUrl" + presignedUrl);
			}
			URL url = new URL(presignedUrl);
			connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setRequestMethod("PUT");
			String contentTypeVal = (contentType != null) ? contentType : "application/octet-stream";
			log.debug("contentTypeVal: " + contentTypeVal);
			connection.setRequestProperty("Content-Type", contentTypeVal);
			if (headers != null && !headers.isEmpty())
				for (Map.Entry<String, String> entry : headers.entrySet())
					connection.setRequestProperty(entry.getKey(), entry.getValue());
			OutputStream output = connection.getOutputStream();
			byte[] buffer = new byte[1024];
			int length;
			log.debug("write output started");
			while ((length = input.read(buffer)) > 0) {
				output.write(buffer, 0, length);
			}
			output.flush();
			log.debug("write output ended");
			int respCode = connection.getResponseCode();
			if (respCode != 200)
				throw new PagoPaException("Failed : HTTP error code : " + respCode);
		} catch (Exception e) {
			throw new PagoPaException(e);
		} finally {
			if (connection != null)
				connection.disconnect();
		}
	}

	public static String getMetadatiByNomeMeta(List<MetadatiClasseDocBean> listaMetadatiClasseDoc,
			DocumentDeliveryBean docDelBean, String nomeMeta, String dateFormat) throws PagoPaException {
		MetadatiClasseDocBean metabean = null;
		for (MetadatiClasseDocBean metaB : listaMetadatiClasseDoc) {
			if (nomeMeta.equals(metaB.getEtichetta())) {
				metabean = metaB;
				break;
			}
		}
		if (metabean != null)
			return getMetaVal(docDelBean, metabean, dateFormat);
		return null;
	}

	public static String getMetadatiByNomeMeta(List<MetadatiClasseDocBean> listaMetadatiClasseDoc,
			DocumentDeliveryBean docDelBean, String nomeMeta) throws PagoPaException {
		MetadatiClasseDocBean metabean = null;
		for (MetadatiClasseDocBean metaB : listaMetadatiClasseDoc) {
			if (nomeMeta.equals(metaB.getEtichetta())) {
				metabean = metaB;
				break;
			}
		}
		if (metabean != null)
			return getMetaVal(docDelBean, metabean);
		return null;
	}

	public static String getMetaVal(DocumentDeliveryBean docDelBean, MetadatiClasseDocBean metaB)
			throws PagoPaException {
		return getMetaVal(docDelBean, metaB, (String) null);
	}

	public static String getMetaVal(DocumentDeliveryBean docDelBean, MetadatiClasseDocBean metaB, String metaDateFormat)
			throws PagoPaException {
		try {
			Object metaVal = null;
			String value = null;
			String dateFormat = (metaDateFormat != null) ? metaDateFormat : "yyyy-MM-dd";
			String getMethodName = "getMeta"
					+ metaB.getNomeColMeta().substring(4, metaB.getNomeColMeta().length()).replace("_", "");
			Method mGet = docDelBean.getClass().getDeclaredMethod(getMethodName, new Class[0]);
			metaVal = mGet.invoke(docDelBean, new Object[0]);
			if (metaVal != null)
				if (metaVal instanceof java.sql.Date || metaVal instanceof Date) {
					String metaValDate = null;
					try {
						metaValDate = DateUtils.getFormattedDate((Date) metaVal, dateFormat);
						value = metaValDate;
					} catch (ParseException e) {
						log.error("EnergyInvioArchivioUnico meta parse excetion " + docDelBean.getMetaD01(), e);
					}
				} else {
					value = metaVal.toString();
				}
			return value;
		} catch (IllegalAccessException | java.lang.reflect.InvocationTargetException | NoSuchMethodException
				| SecurityException e) {
			throw new PagoPaException(e);
		}
	}

	public static String getNomeFileOutput(File originalFile, AcceptanceBean acceptanceBean, boolean isValidationError,
			String fileNameWithoutExtension, String dataFileTrailer, long jobId, ClientConfigBean clientConfigBean) {
		return getNomeFileOutput(originalFile, acceptanceBean, isValidationError, fileNameWithoutExtension,
				dataFileTrailer, jobId, clientConfigBean, null);
	}

	public static String getNomeFileOutput(File originalFile, AcceptanceBean acceptanceBean, boolean isValidationError,
			String fileNameWithoutExtension, String dataFileTrailer, long jobId, ClientConfigBean clientConfigBean,
			String extension) {
		String nomeFile = fileNameWithoutExtension;
		if (isValidationError) {
			if (originalFile != null)
				nomeFile = jobId + "_" + originalFile.getName();
		} else {
			String canale = (clientConfigBean.getAccessChannel() != null) ? clientConfigBean.getAccessChannel()
					: clientConfigBean.getClientBean().getAccessChannel();
			String fileExtension = (extension == null) ? FileUtil.getFileExtension(originalFile.getName().toUpperCase())
					: extension;
			nomeFile = acceptanceBean.getFlowId().concat("-").concat(dataFileTrailer).concat("-")
					.concat(acceptanceBean.getUserId()).concat("-").concat(acceptanceBean.getzCode()).concat("-")
					.concat(canale).concat("-").concat(getUpToNChar(fileNameWithoutExtension, 64)).concat(".")
					.concat(fileExtension);
		}
		return nomeFile;
	}

	public static void trackScartoUdh(DBConnection dbConnection, PagoPaDao pagopaDao, AcceptanceBean acceptanceBean)
			throws DAOException {
		String event = EventTypeEnum.ScartoUdh.getValue();
		Integer stato = Integer.valueOf(StatoEnum.getValueFromKey(event));
		Integer sourceSystem = Integer.valueOf(SystemTypeEnum.getValueFromKey("Accettazione"));
		FlowBean flowBean = FlowBean.createFromScartoUdh(acceptanceBean, stato, event);
		pagopaDao.insertLotto(dbConnection, flowBean);
		FlowDeliveryBean flowDeliveryBean = FlowDeliveryBean.createFromFlowBean(flowBean, stato, sourceSystem, null,
				(acceptanceBean.getRifStatoFile() != null) ? String.valueOf(acceptanceBean.getRifStatoFile()) : null,
				acceptanceBean.getStatoFile());
		pagopaDao.insertLottoDelivery(dbConnection, flowDeliveryBean);
		TrackingFlowBean trkFlowBean = TrackingFlowBean.createFromFlowDeliveryAndAcceptanceBean(flowBean,
				acceptanceBean, flowDeliveryBean, event, sourceSystem, null);
		pagopaDao.insertLottoTracking(dbConnection, trkFlowBean);
	}

	public static void trackAccettazioneUdh(DBConnection dbConnection, PagoPaDao pagopaDao,
			AcceptanceBean acceptanceBean) throws DAOException {
		String event = EventTypeEnum.AccettazioneUdh.getValue();
		Integer sourceSystem = Integer.valueOf(SystemTypeEnum.getValueFromKey("Accettazione"));
		TrackingFlowBean trkFlowBean = TrackingFlowBean.createFromAcceptanceBean(acceptanceBean, event, sourceSystem,
				null);
		pagopaDao.insertLottoTracking(dbConnection, trkFlowBean);
	}

	public static List<File> getFilesByTrailerAndExtensions(String inputFolder, String fileNameWithoutExtension,
			List<String> extensionsLowerCaseList) {
		String fileNameEscape = fileNameWithoutExtension;
		List<File> listaFileOriginale = new ArrayList<>();
		try {
			Pattern.compile(fileNameWithoutExtension);
		} catch (Exception e) {
			fileNameEscape = fileNameWithoutExtension.replaceAll("[-\\[\\]{}()*+?.,\\\\\\\\^$|#\\\\s]", "\\\\$0");
		}
		String fileNameRegola = "^" + fileNameEscape + "\\.((?![tT]$)[^\\.]+)$";
		File[] accFiles = FileUtil.ascDateOrderedDirList(new File(inputFolder),
				(FileFilter) new FileRegexpFilter(fileNameRegola));
		if (accFiles != null && accFiles.length > 0)
			for (int i = 0; i < extensionsLowerCaseList.size(); i++) {
				String extension = extensionsLowerCaseList.get(i);
				File fileInput = getFileInputByExtensionFromFileList(accFiles, extension);
				if (fileInput != null)
					listaFileOriginale.add(fileInput);
			}
		return listaFileOriginale;
	}

	public static File getFileInputByExtensionFromFileList(File[] accFiles, String extension) {
		for (File inputFile : accFiles) {
			if (FileUtil.getFileExtension(inputFile).equalsIgnoreCase(extension))
				return inputFile;
		}
		return null;
	}

	public static void backupFileContent(String backupFolder, String fileContent, String fileName, JobBean job)
			throws PagoPaException, ParseException, IOException {
		try {
			boolean backupFileDelivery = LoaderProperties.getBooleanPropertyValue("BOOL_BACKUP_FILE_DELIVERY", true);
			if (backupFileDelivery) {
				String pathBackup = calcolaBackupPath(backupFolder, Long.valueOf(job.getId()), job.getStartTimestamp());
				File backupFile = new File(pathBackup, fileName);
				if (!FileUtil.isDirectory(pathBackup) && !FileUtil.makedir(pathBackup)) {
					log.error("Unable to create backupFolder " + pathBackup);
					throw new PagoPaException("Unable to create backupFolder" + pathBackup);
				}
				if (!backupFile.getParentFile().exists())
					backupFile.getParentFile().mkdir();
				FileUtil.writeStringToFile(backupFile, fileContent);
			}
		} catch (Exception e) {
			log.error("BackupDeliveryFile Error - " + e.getMessage(), e);
			throw new PagoPaException("BackupDeliveryFile Error - " + e.getMessage(), e);
		}
	}

	public static String getMetadatiByNomeMetaTrkBean(List<MetadatiClasseDocBean> listaMetadatiClasseDoc,
			TrkDocumentoBean docBean, String nomeMeta, String metaDateFormat) throws PagoPaException {
		MetadatiClasseDocBean metabean = null;
		for (MetadatiClasseDocBean metaB : listaMetadatiClasseDoc) {
			if (nomeMeta.equals(metaB.getEtichetta())) {
				metabean = metaB;
				break;
			}
		}
		if (metabean != null)
			return getMetaValTrkBean(docBean, metabean, metaDateFormat);
		return null;
	}

	public static String getMetaValTrkBean(TrkDocumentoBean docBean, MetadatiClasseDocBean metaB, String metaDateFormat)
			throws PagoPaException {
		try {
			Object metaVal = null;
			String value = null;
			String dateFormat = (metaDateFormat != null) ? metaDateFormat : "dd-MMM-yy HH:mm:ss";
			String getMethodName = "getMeta"
					+ metaB.getNomeColMeta().substring(4, metaB.getNomeColMeta().length()).replace("_", "");
			Method mGet = docBean.getClass().getDeclaredMethod(getMethodName, new Class[0]);
			metaVal = mGet.invoke(docBean, new Object[0]);
			if (metaVal != null)
				if (metaVal instanceof java.sql.Date || metaVal instanceof Date) {
					String metaValDate = null;
					try {
						metaValDate = DateUtils.getFormattedDate((Date) metaVal, dateFormat);
						value = metaValDate;
					} catch (ParseException e) {
						log.error("getMetaValTrkBean meta parse excetion " + docBean.getMetaD01(), e);
					}
				} else {
					value = metaVal.toString();
				}
			return value;
		} catch (IllegalAccessException | java.lang.reflect.InvocationTargetException | NoSuchMethodException
				| SecurityException e) {
			throw new PagoPaException(e);
		}
	}

	public static List<DuplicatiMassiviFileBean> readCsvFileDuplicatiMassivi(File csvFile) throws Exception {
		List<String[]> rowList = (List) new ArrayList<>();
		List<DuplicatiMassiviFileBean> resultList = new ArrayList<>();
		BufferedReader csvReader = null;
		try {
			csvReader = new BufferedReader(new InputStreamReader(new FileInputStream(csvFile), "UTF-8"));
			csvReader.readLine();
			String row = "";
			while ((row = csvReader.readLine()) != null) {
				if (row != null) {
					String[] rowSplit = row.split("\\|");
					rowList.add(rowSplit);
				}
			}
			if (rowList != null)
				for (String[] rowL : rowList) {
					DuplicatiMassiviFileBean duplicatiBean = new DuplicatiMassiviFileBean();
					String codRacc = (rowL[0] != null && !rowL[0].equalsIgnoreCase("NULL")) ? rowL[0] : null;
					String email = (rowL[1] != null && !rowL[1].equalsIgnoreCase("NULL")) ? rowL[1] : null;
					String pec = (rowL[2] != null && !rowL[2].equalsIgnoreCase("NULL")) ? rowL[2] : null;
					String richiedente = (rowL[3] != null && !rowL[3].equalsIgnoreCase("NULL")) ? rowL[3] : null;
					String indirizzoRichiedente = (rowL[4] != null && !rowL[4].equalsIgnoreCase("NULL")) ? rowL[4]
							: null;
					String cittaRichiedente = (rowL[5] != null && !rowL[5].equalsIgnoreCase("NULL")) ? rowL[5] : null;
					String capRichiedente = (rowL[6] != null && !rowL[6].equalsIgnoreCase("NULL")) ? rowL[6] : null;
					String provRichiedente = (rowL[7] != null && !rowL[7].equalsIgnoreCase("NULL")) ? rowL[7] : null;
					duplicatiBean.setCodiceRaccomandata(codRacc);
					duplicatiBean.setEmail(email);
					duplicatiBean.setPec(pec);
					duplicatiBean.setRichiedente(richiedente);
					duplicatiBean.setIndirizzoRichiedente(indirizzoRichiedente);
					duplicatiBean.setCittaRichiedente(cittaRichiedente);
					duplicatiBean.setProvinciaRichiedente(provRichiedente);
					duplicatiBean.setCapRichiedente(capRichiedente);
					resultList.add(duplicatiBean);
				}
		} catch (Exception e) {
			throw new Exception(e);
		} finally {
			if (csvReader != null)
				csvReader.close();
		}
		return resultList;
	}

	public static String getMetadatiByNomeMeta(List<MetadatiClasseDocBean> listaMetadatiClasseDoc, DocumentBean docBean,
			String nomeMeta, String dateFormat) throws PagoPaException {
		MetadatiClasseDocBean metabean = null;
		for (MetadatiClasseDocBean metaB : listaMetadatiClasseDoc) {
			if (nomeMeta.equals(metaB.getEtichetta())) {
				metabean = metaB;
				break;
			}
		}
		if (metabean != null)
			return getMetaVal(docBean, metabean, dateFormat);
		return null;
	}

	public static String getMetaVal(DocumentBean docBean, MetadatiClasseDocBean metaB, String metaDateFormat)
			throws PagoPaException {
		try {
			Object metaVal = null;
			String value = null;
			String dateFormat = (metaDateFormat != null) ? metaDateFormat : "yyyy-MM-dd";
			String getMethodName = "getMeta"
					+ metaB.getNomeColMeta().substring(4, metaB.getNomeColMeta().length()).replace("_", "");
			Method mGet = docBean.getClass().getDeclaredMethod(getMethodName, new Class[0]);
			metaVal = mGet.invoke(docBean, new Object[0]);
			if (metaVal != null)
				if (metaVal instanceof java.sql.Date || metaVal instanceof Date) {
					String metaValDate = null;
					try {
						metaValDate = DateUtils.getFormattedDate((Date) metaVal, dateFormat);
						value = metaValDate;
					} catch (ParseException e) {
						log.error("Meta parse excetion " + docBean.getMetaD01(), e);
					}
				} else {
					value = metaVal.toString();
				}
			return value;
		} catch (IllegalAccessException | java.lang.reflect.InvocationTargetException | NoSuchMethodException
				| SecurityException e) {
			throw new PagoPaException(e);
		}
	}

	public static void invokeWsOrcPagoPa(CreateFlowRequest request) {
		try {
			if (request != null) {
				ApiClient apiClient = new ApiClient();
				apiClient.setHttpClient(ApiClientUtil.getHttpClientWithInterceptor());
				apiClient.setBasePath(LoaderProperties.getPropertyValue("ORC_PAGOPA_SERVICE_BASE_PATH"));
				apiClient.setReadTimeout(LoaderProperties.getIntPropertyValue("REST_CLIENT_READ_TIMEOUT", 10) * 1000);
				PagopaServicesWfApi restApi = new PagopaServicesWfApi(apiClient);
				CreateObjectResponse respVal = restApi.createFlow(request);
				if (respVal.getOutcome() == null
						|| (respVal.getOutcome() != null && respVal.getOutcome().equalsIgnoreCase("KO"))) {
					log.error("Risposta servizio createFlow KO. Jobid" + request.getJobId() + ". "
							+ respVal.getErrorCode() + "/" + respVal.getErrorDescription());
					throw new PagoPaRuntimeException("Risposta servizio createFlow KO. Jobid" + request.getJobId()
							+ ". " + respVal.getErrorCode() + "/" + respVal.getErrorDescription());
				}
			}
		} catch (ApiException e) {
			log.error(e);
			throw new PagoPaRuntimeException(
					"Invocazione WS ORC KO. Jobid" + request.getJobId() + ". " + e.getMessage());
		}
	}

	public static boolean docPresentOnDocufe(ObjectMapper mapper, DocumentBean docBean)
			throws IOException, JsonParseException, JsonMappingException {
		boolean presentSuDocuFe = false;
		DatiAggiuntiviBean datiAggiuntiviBean = null;
		if (!StringUtil.isStringNull(docBean.getDatiAggiuntivi())) {
			datiAggiuntiviBean = (DatiAggiuntiviBean) mapper.readValue(docBean.getDatiAggiuntivi(),
					DatiAggiuntiviBean.class);
		} else {
			datiAggiuntiviBean = new DatiAggiuntiviBean();
		}
		NameValueBean presenzaSuDocumentaleBeanInfo = datiAggiuntiviBean.getInfoBeanByName("presenza_su_documentale");
		if (presenzaSuDocumentaleBeanInfo != null && presenzaSuDocumentaleBeanInfo.getValue().equalsIgnoreCase("true"))
			presentSuDocuFe = true;
		return presentSuDocuFe;
	}

	public static Map<Integer, Map<String, String>> readCsvFileOneAppPregresso(File csvFile) throws Exception {
		String[] strArrHeaderFields = null;
		HashMap<Integer, Map<String, String>> objAggregateData = new HashMap<>();
		String[] strArrValues = new String[20];
		try {
			List<String> fileLinesList = Files.readAllLines(Paths.get(csvFile.getPath(), new String[0]));
			strArrHeaderFields = ((String) fileLinesList.get(0)).split("\";\"");
			for (int i = 1; i < fileLinesList.size(); i++) {
				HashMap<String, String> objDataHashMap = new HashMap<>();
				strArrValues = ((String) fileLinesList.get(i)).split("\";\"");
				for (int j = 0; j < strArrHeaderFields.length; j++) {
					String headerField = (strArrHeaderFields[j] != null)
							? strArrHeaderFields[j].toUpperCase().trim().replaceAll("^\"|\"$", "")
							: "";
					String value = (strArrValues[j] != null) ? strArrValues[j].trim().replaceAll("^\"|\"$", "") : "";
					objDataHashMap.put(headerField, value);
				}
				objAggregateData.put(Integer.valueOf(i), objDataHashMap);
			}
		} catch (Exception ex) {
			log.error(ex);
			throw ex;
		}
		return objAggregateData;
	}

	public static boolean isFilePregressoEcbpweb(String fileName) {
		boolean isEcbpwebFile = false;
		if (fileName != null) {
			String[] fileNameSplit = fileName.split("_");
			if (fileNameSplit.length > 1 && fileNameSplit[1].equalsIgnoreCase("ECBPWEB"))
				isEcbpwebFile = true;
		}
		return isEcbpwebFile;
	}

	public static String getMetaIdFromIndexDeclaration(String metaName,
			Map<String, IndexDeclarationBean> indexesDeclaration) {
		for (Map.Entry<String, IndexDeclarationBean> indexDeclaration : indexesDeclaration.entrySet()) {
			if (indexDeclaration.getValue() != null
					&& ((IndexDeclarationBean) indexDeclaration.getValue()).getName() != null
					&& ((IndexDeclarationBean) indexDeclaration.getValue()).getName().equals(metaName))
				return indexDeclaration.getKey();
		}
		return null;
	}

	public static String getMetaFormatFromIndexDeclaration(String idMeta,
			Map<String, IndexDeclarationBean> indexesDeclaration) {
		for (Map.Entry<String, IndexDeclarationBean> indexDeclaration : indexesDeclaration.entrySet()) {
			if (indexDeclaration.getKey() != null && ((String) indexDeclaration.getKey()).equals(idMeta))
				return (indexDeclaration.getValue() != null)
						? ((IndexDeclarationBean) indexDeclaration.getValue()).getFormat()
						: null;
		}
		return null;
	}

	public static List<String> getMetaMappingNames(String nomeMeta, List<MappingMetadatiBean> mappingMetaDatiList) {
		List<String> nomiConvertiti = new ArrayList<>();
		if (mappingMetaDatiList != null && !mappingMetaDatiList.isEmpty())
			for (MappingMetadatiBean mappingMetaBean : mappingMetaDatiList) {
				if (mappingMetaBean != null && mappingMetaBean.getMetadato() != null
						&& mappingMetaBean.getMetadato().equalsIgnoreCase(nomeMeta))
					nomiConvertiti.add(mappingMetaBean.getNomeConvertito());
			}
		return nomiConvertiti;
	}

	public static boolean existDatFile(File zipFile, Charset charset, String nomeFile) throws Exception {
		boolean existFileDate = false;
		boolean decompressione7Zip = Boolean.parseBoolean(LoaderProperties.getPropertyValue(LoaderConstants.DECOMPRESSIONE_7ZIP));
		if(decompressione7Zip) {
			if (zipFile.getName().endsWith(".7z")) {
				// unzip file seven7Z
				SevenZFile sevenZ = new SevenZFile(zipFile);
		        try {
		            SevenZArchiveEntry entry;
		            while ((entry = sevenZ.getNextEntry()) != null) {
		                if (!entry.isDirectory() && entry.getName().toLowerCase().endsWith(nomeFile)) {
		                    existFileDate = true;
		                    break;
		                }
		            }
		        } finally {
		            if (sevenZ != null)
		                sevenZ.close();
		        }
			} else if (zipFile.getName().endsWith(".zip")) {
				// unzip file zip
				org.apache.commons.compress.archivers.zip.ZipFile zip = new org.apache.commons.compress.archivers.zip.ZipFile(zipFile);
				try {
					for (Enumeration<? extends org.apache.commons.compress.archivers.zip.ZipArchiveEntry> e = zip.getEntries(); e.hasMoreElements();) {
						org.apache.commons.compress.archivers.zip.ZipArchiveEntry entry = e.nextElement();
						if (!entry.isDirectory() && entry.getName().toLowerCase().endsWith(nomeFile)) {
							existFileDate = true;
							break;
						}
					}
				} finally {
					if (zip != null)
						zip.close();
				}
			}
			
		}else {
			ZipFile zip = new ZipFile(zipFile);
			try {
				for (Enumeration<? extends ZipEntry> e = zip.entries(); e.hasMoreElements();) {
					ZipEntry entry = e.nextElement();
					if (!entry.isDirectory() && entry.getName().toLowerCase().endsWith(nomeFile)) {
						existFileDate = true;
						break;
					}
				}
			} finally {
				if (zip != null)
					zip.close();
			}
		}
		return existFileDate;
	}

	public static Map<Integer, Map<String, String>> getFileDatFromCpx(File zipFile, Charset charset, String nomeFile) throws Exception {
	    Map<Integer, Map<String, String>> datFileContent = null;
	    boolean decompressione7Zip = Boolean.parseBoolean(LoaderProperties.getPropertyValue(LoaderConstants.DECOMPRESSIONE_7ZIP));
	    if (decompressione7Zip) {
	    	if (zipFile.getName().endsWith(".7z")) {
				// unzip file seven7Z
		        SevenZFile sevenZ = new SevenZFile(zipFile);
		        try {
		            SevenZArchiveEntry entry;
		            while ((entry = sevenZ.getNextEntry()) != null) {
		                if (!entry.isDirectory() && entry.getName().toLowerCase().endsWith(nomeFile)) {
		                    datFileContent = readDatFileOneAppOnline(sevenZ.getInputStream(entry));
		                    break;
		                }
		            }
		        } finally {
		            if (sevenZ != null)
		                sevenZ.close();
		        }
			} else if (zipFile.getName().endsWith(".zip")) {
				// unzip file zip
				org.apache.commons.compress.archivers.zip.ZipFile zip = new org.apache.commons.compress.archivers.zip.ZipFile(zipFile);
				try {
					for (Enumeration<? extends org.apache.commons.compress.archivers.zip.ZipArchiveEntry> e = zip.getEntries(); e.hasMoreElements();) {
						org.apache.commons.compress.archivers.zip.ZipArchiveEntry entry = e.nextElement();
						if (!entry.isDirectory() && entry.getName().toLowerCase().endsWith(nomeFile)) {
							datFileContent = readDatFileOneAppOnline(zip.getInputStream(entry));
							break;
						}
					}
				} finally {
					if (zip != null)
						zip.close();
				}
			}
	    } else {
	        ZipFile zip = new ZipFile(zipFile);
	        try {
	            for (Enumeration<? extends ZipEntry> e = zip.entries(); e.hasMoreElements();) {
	                ZipEntry entry = e.nextElement();
	                if (!entry.isDirectory() && entry.getName().toLowerCase().endsWith(nomeFile)) {
	                    datFileContent = readDatFileOneAppOnline(zip.getInputStream(entry));
	                    break;
	                }
	            }
	        } finally {
	            if (zip != null)
	                zip.close();
	        }
	    }
	    return datFileContent;
	}
	public static Map<Integer, Map<String, String>> readDatFileOneAppOnline(InputStream fileIs) throws Exception {
		String[] strArrHeaderFields = null;
		HashMap<Integer, Map<String, String>> objAggregateData = new HashMap<>();
		Scanner scanner = null;
		try {
			scanner = new Scanner(fileIs);
			strArrHeaderFields = scanner.nextLine().split("\",\"");
			int i = 1;
			while (scanner.hasNextLine()) {
				HashMap<String, String> objDataHashMap = new HashMap<>();
				String[] strArrValues = scanner.nextLine().split("\",\"");
				for (int j = 0; j < strArrHeaderFields.length; j++)
					objDataHashMap.put(
							(strArrHeaderFields[j] != null) ? strArrHeaderFields[j].trim().replaceAll("^\"|\"$", "")
									: null,
							(strArrValues[j] != null) ? strArrValues[j].trim().replaceAll("^\"|\"$", "") : null);
				objAggregateData.put(Integer.valueOf(i), objDataHashMap);
				i++;
			}
		} catch (Exception ex) {
			log.error(ex);
			throw ex;
		} finally {
			if (scanner != null)
				scanner.close();
		}
		return objAggregateData;
	}

	public static String getMetaValueFromXmlIndiciUsingMapping(String etichetta, LetterSectionBean letterSection,
			Map<String, IndexDeclarationBean> indexesDeclaration, List<MappingMetadatiBean> listaMappingMeta) {
		String idMeta = getMetaIdFromIndexDeclaration(etichetta, indexesDeclaration);
		if (StringUtil.isStringNull(idMeta)) {
			List<String> nomiConvertitiList = getMetaMappingNames(etichetta, listaMappingMeta);
			for (String nome : nomiConvertitiList) {
				idMeta = getMetaIdFromIndexDeclaration(nome, indexesDeclaration);
				if (!StringUtil.isStringNull(idMeta))
					break;
			}
		}
		return (String) letterSection.getIndexes().get(idMeta);
	}

	public static String getMetaIdFromIndexDeclarationUsingMapping(String metaName,
			Map<String, IndexDeclarationBean> indexesDeclaration, List<MappingMetadatiBean> listaMappingMeta) {
		String idMeta = getMetaIdFromIndexDeclaration(metaName, indexesDeclaration);
		if (StringUtil.isStringNull(idMeta)) {
			List<String> nomiConvertitiList = getMetaMappingNames(metaName, listaMappingMeta);
			for (String nome : nomiConvertitiList) {
				idMeta = getMetaIdFromIndexDeclaration(nome, indexesDeclaration);
				if (!StringUtil.isStringNull(idMeta))
					break;
			}
		}
		return idMeta;
	}

	public static String getMetdaValFromDatFileUsingMapping(Map<String, String> rowValuesMap, String metaName,
			List<MappingMetadatiBean> listaMappingMeta) {
		String valMeta = rowValuesMap.get(metaName);
		if (StringUtil.isStringNull(valMeta)) {
			List<String> nomiConvertitiList = getMetaMappingNames(metaName, listaMappingMeta);
			for (String nome : nomiConvertitiList) {
				valMeta = rowValuesMap.get(nome);
				if (!StringUtil.isStringNull(valMeta))
					break;
			}
		}
		return valMeta;
	}

	public static List<HashMap<String, String>> getFileInfFromTimestamp(File timestampDir, String nomeFile)
			throws Exception {
		List<HashMap<String, String>> listFileInf = new ArrayList<>();
		File[] timestamFiles = (timestampDir != null) ? timestampDir.listFiles() : null;
		if (timestamFiles != null && timestamFiles.length > 0)
			for (File file : timestamFiles) {
				if (file.getName().toLowerCase().endsWith(nomeFile)) {
					ParserInfFile parserInf = new ParserInfFile(file.getPath());
					HashMap<String, String> mapInfData = parserInf.processLineByLine();
					listFileInf.add(mapInfData);
				}
			}
		return listFileInf;
	}

	public static boolean existDatFileTimestamp(File timestampDir, String nomeFile) throws Exception {
		File[] timestamFiles = (timestampDir != null) ? timestampDir.listFiles() : null;
		boolean existFileDate = false;
		if (timestamFiles != null && timestamFiles.length > 0)
			for (File file : timestamFiles) {
				if (file.getName().toLowerCase().endsWith(nomeFile)) {
					existFileDate = true;
					break;
				}
			}
		return existFileDate;
	}

	public static Map<Integer, Map<String, String>> getFileDatFromTimestamp(File timestampDir, String nomeFile)
			throws Exception {
		Map<Integer, Map<String, String>> datFileContent = null;
		File[] timestamFiles = (timestampDir != null) ? timestampDir.listFiles() : null;
		InputStream datFileIs = null;
		try {
			if (timestamFiles != null && timestamFiles.length > 0)
				for (File file : timestamFiles) {
					if (file.getName().toLowerCase().endsWith(nomeFile)) {
						datFileIs = new FileInputStream(file);
						datFileContent = readDatFileOneAppOnline(datFileIs);
						break;
					}
				}
		} finally {
			if (datFileIs != null)
				datFileIs.close();
		}
		return datFileContent;
	}

	public static List<CpxBean> getFileXmlFromTimestamp(File timestampDir) throws Exception {
		File[] timestamFiles = (timestampDir != null) ? timestampDir.listFiles() : null;
		List<CpxBean> listaXml = new ArrayList<>();
		SAXParserFactory spf = SAXParserFactory.newInstance();
		SAXParser parser = spf.newSAXParser();
		if (timestamFiles != null && timestamFiles.length > 0)
			for (File file : timestamFiles) {
				String name = file.getName().toLowerCase();
				if (name.endsWith("_out.xml") || name.endsWith("_i.xml") || name.endsWith("_i.pdf.xml"))
					try {
						CPXFileHandler cpxFileHandler = new CPXFileHandler();
						parser.parse(file.getPath(), (DefaultHandler) cpxFileHandler);
						listaXml.add(cpxFileHandler.getCpxBean());
					} catch (Exception ex) {
						throw new AcceptanceException("30",
								"File xml indici non corretto: parser xml fallito con errore: " + ex.getMessage());
					}
			}
		return listaXml;
	}

	public static String getMetadatiByNomeMeta(List<MetadatiClasseDocBean> listaMetadatiClasseDoc,
			TrackingDocumentBean bean, String nomeMeta, String dateFormat) throws PagoPaException {
		MetadatiClasseDocBean metabean = null;
		for (MetadatiClasseDocBean metaB : listaMetadatiClasseDoc) {
			if (nomeMeta.equals(metaB.getEtichetta())) {
				metabean = metaB;
				break;
			}
		}
		if (metabean != null)
			return getMetaVal(bean, metabean, dateFormat);
		return null;
	}

	public static String getMetaVal(TrackingDocumentBean bean, MetadatiClasseDocBean metaB) throws PagoPaException {
		return getMetaVal(bean, metaB, (String) null);
	}

	public static String getMetaVal(TrackingDocumentBean bean, MetadatiClasseDocBean metaB, String metaDateFormat)
			throws PagoPaException {
		try {
			Object metaVal = null;
			String value = null;
			String dateFormat = (metaDateFormat != null) ? metaDateFormat : "yyyy-MM-dd";
			String getMethodName = "getMeta"
					+ metaB.getNomeColMeta().substring(4, metaB.getNomeColMeta().length()).replace("_", "");
			Method mGet = bean.getClass().getDeclaredMethod(getMethodName, new Class[0]);
			metaVal = mGet.invoke(bean, new Object[0]);
			if (metaVal != null)
				if (metaVal instanceof java.sql.Date || metaVal instanceof Date) {
					String metaValDate = null;
					try {
						metaValDate = DateUtils.getFormattedDate((Date) metaVal, dateFormat);
						value = metaValDate;
					} catch (ParseException e) {
						log.error("EnergyInvioArchivioUnico meta parse excetion " + bean.getMetaD01(), e);
					}
				} else {
					value = metaVal.toString();
				}
			return value;
		} catch (IllegalAccessException | java.lang.reflect.InvocationTargetException | NoSuchMethodException
				| SecurityException e) {
			throw new PagoPaException(e);
		}
	}

	public static List<CpxBean> getFileXmlFromZipInsideCpx(File zipFile, Charset charset)
			throws Exception, AcceptanceException {
		List<CpxBean> listaXml = new ArrayList<>();
		boolean decompressione7Zip = Boolean.parseBoolean(LoaderProperties.getPropertyValue(LoaderConstants.DECOMPRESSIONE_7ZIP));
		if(decompressione7Zip) {
			if (zipFile.getName().endsWith(".7z")) {
				// unzip file seven7Z
				SevenZFile sevenZ = new SevenZFile(zipFile);
		        SAXParserFactory spf = SAXParserFactory.newInstance();
		        SAXParser parser = spf.newSAXParser();
		        String regFileName = "^.+((?i)_I)\\..+\\.((?i)xml)$";
		        try {
		            SevenZArchiveEntry entry;
		            while ((entry = sevenZ.getNextEntry()) != null) {
		                if (!entry.isDirectory()) {
		                    String name = entry.getName().toLowerCase();
		                    if (name.endsWith("_i.7z") || name.endsWith("_i.zip")) {
		                    	InputStream isSevenZ = sevenZ.getInputStream(entry);                   	
		                        SevenZFile innerSevenZ = SevenZFile.builder().setInputStream(isSevenZ).get();
		                        try {
		                            SevenZArchiveEntry entryZipIn;
		                            while ((entryZipIn = innerSevenZ.getNextEntry()) != null) {
		                                if (!entryZipIn.isDirectory() && entryZipIn.getName().matches(regFileName)) {
		                                    ByteArrayOutputStream out = null;
		                                    InputStream is = null;
		                                    try {
		                                        out = new ByteArrayOutputStream();
		                                        IOUtils.copy(innerSevenZ.getInputStream(entryZipIn), out);
		                                        is = new ByteArrayInputStream(out.toByteArray());
		                                        CPXFileHandler cpxFileHandler = new CPXFileHandler();
		                                        parser.parse(is, (DefaultHandler) cpxFileHandler);
		                                        listaXml.add(cpxFileHandler.getCpxBean());
		                                    } catch (Exception ex) {
		                                        throw new AcceptanceException("30", "File xml indici non corretto: parser xml fallito con errore: " + ex.getMessage());
		                                    } finally {
		                                        if (out != null)
		                                            out.close();
		                                        if (is != null)
		                                            is.close();
		                                    }
		                                }
		                            }
		                        } finally {
		                            if (innerSevenZ != null)
		                                innerSevenZ.close();
		                        }
		                    }
		                }
		            }
		        } finally {
		            if (sevenZ != null)
		                sevenZ.close();
		        }
			} else if (zipFile.getName().endsWith(".zip")) {
				// unzip file zip
				org.apache.commons.compress.archivers.zip.ZipFile zip = new org.apache.commons.compress.archivers.zip.ZipFile(zipFile);
				SAXParserFactory spf = SAXParserFactory.newInstance();
				SAXParser parser = spf.newSAXParser();
				String regFileName = "^.+((?i)_I)\\..+\\.((?i)xml)$";
				try {
					for (Enumeration<? extends org.apache.commons.compress.archivers.zip.ZipArchiveEntry> e = zip.getEntries(); e.hasMoreElements();) {
						org.apache.commons.compress.archivers.zip.ZipArchiveEntry entry = e.nextElement();
						if (!entry.isDirectory()) {
							String name = entry.getName().toLowerCase();
							if (name.endsWith("_i.zip")) {
								org.apache.commons.compress.archivers.zip.ZipArchiveInputStream zipIn = null;
								try {
									zipIn = new org.apache.commons.compress.archivers.zip.ZipArchiveInputStream(zip.getInputStream(entry));
									ZipEntry entryZipIn;
									while ((entryZipIn = zipIn.getNextEntry()) != null) {
										if (!entryZipIn.isDirectory() && entryZipIn.getName().matches(regFileName)) {
											ByteArrayOutputStream out = null;
											InputStream is = null;
											try {
												out = new ByteArrayOutputStream();
												IOUtils.copy(zipIn, out);
												is = new ByteArrayInputStream(out.toByteArray());
												CPXFileHandler cpxFileHandler = new CPXFileHandler();
												parser.parse(is, (DefaultHandler) cpxFileHandler);
												listaXml.add(cpxFileHandler.getCpxBean());
											} catch (Exception ex) {
												throw new AcceptanceException("30",
														"File xml indici non corretto: parser xml fallito con errore: "
																+ ex.getMessage());
											} finally {
												if (out != null)
													out.close();
												if (is != null)
													is.close();
											}
										}
									}
								} finally {
									if (zipIn != null)
										zipIn.close();
								}
							}
						}
					}
				} finally {
					if (zip != null)
						zip.close();
				}
			}
			
		}else {
			ZipFile zip = new ZipFile(zipFile);
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser parser = spf.newSAXParser();
			String regFileName = "^.+((?i)_I)\\..+\\.((?i)xml)$";
			try {
				for (Enumeration<? extends ZipEntry> e = zip.entries(); e.hasMoreElements();) {
					ZipEntry entry = e.nextElement();
					if (!entry.isDirectory()) {
						String name = entry.getName().toLowerCase();
						if (name.endsWith("_i.zip")) {
							ZipInputStream zipIn = null;
							try {
								zipIn = new ZipInputStream(zip.getInputStream(entry));
								ZipEntry entryZipIn;
								while ((entryZipIn = zipIn.getNextEntry()) != null) {
									if (!entryZipIn.isDirectory() && entryZipIn.getName().matches(regFileName)) {
										ByteArrayOutputStream out = null;
										InputStream is = null;
										try {
											out = new ByteArrayOutputStream();
											IOUtils.copy(zipIn, out);
											is = new ByteArrayInputStream(out.toByteArray());
											CPXFileHandler cpxFileHandler = new CPXFileHandler();
											parser.parse(is, (DefaultHandler) cpxFileHandler);
											listaXml.add(cpxFileHandler.getCpxBean());
										} catch (Exception ex) {
											throw new AcceptanceException("30",
													"File xml indici non corretto: parser xml fallito con errore: "
															+ ex.getMessage());
										} finally {
											if (out != null)
												out.close();
											if (is != null)
												is.close();
										}
									}
								}
							} finally {
								if (zipIn != null)
									zipIn.close();
							}
						}
					}
				}
			} finally {
				if (zip != null)
					zip.close();
			}
		}		
		return listaXml;
	}

	public static List<CpxBean> getFileXmlFromZipInsideTimestamp(File timestampDir) throws Exception {
		File[] timestamFiles = timestampDir != null ? timestampDir.listFiles() : null;
		List<CpxBean> listaXml = new ArrayList<CpxBean>();
		SAXParserFactory spf = SAXParserFactory.newInstance();
		SAXParser parser = spf.newSAXParser();
		// nome file xml nel file zip: "_I.*.xml"
		String regFileName = "^.+((?i)_I)\\..+\\.((?i)xml)$";
		if (timestamFiles != null && timestamFiles.length > 0) {
			for (File file : timestamFiles) {
				String name = file.getName().toLowerCase();
				if (name.endsWith("_i.zip") || name.endsWith("_i.7z")) {
					boolean decompressione7Zip = Boolean.parseBoolean(LoaderProperties.getPropertyValue(LoaderConstants.DECOMPRESSIONE_7ZIP));
//					if(decompressione7Zip) {
//						org.apache.commons.compress.archivers.zip.ZipFile zip = new org.apache.commons.compress.archivers.zip.ZipFile(file);
//						try {
//							for (Enumeration e = zip.getEntries(); e.hasMoreElements();) {
//								org.apache.commons.compress.archivers.zip.ZipArchiveEntry entry = (org.apache.commons.compress.archivers.zip.ZipArchiveEntry) e.nextElement();
//								if (!entry.isDirectory()) {
//									String nameFileInZip = entry.getName().toLowerCase();
//									if (nameFileInZip.matches(regFileName)) {
//										try {
//											CPXFileHandler cpxFileHandler = new CPXFileHandler();
//											parser.parse(zip.getInputStream(entry), cpxFileHandler);
//											listaXml.add(cpxFileHandler.getCpxBean());
//										} catch (Exception ex) {
//											throw new AcceptanceException(
//													ValidationConstants.ACCETTAZIONE_FILE_XML_CORROTTO,
//													"File xml indici non corretto: parser xml fallito con errore: "
//															+ ex.getMessage());
//										}
//									}
//								}
//							}
//						} finally {
//							if (zip != null) {
//								zip.close();
//							}
//						}
//					}
					if (decompressione7Zip) {
						if (file.getName().endsWith(".7z")) {
							// unzip file seven7Z
							SevenZFile sevenZ = new SevenZFile(file);
						    try {
						        SevenZArchiveEntry entry;
						        while ((entry = sevenZ.getNextEntry()) != null) {
						            if (!entry.isDirectory()) {
						                String nameFileInSevenZ = entry.getName().toLowerCase();
						                if (nameFileInSevenZ.matches(regFileName)) {
						                    try {
						                        CPXFileHandler cpxFileHandler = new CPXFileHandler();
						                        parser.parse(sevenZ.getInputStream(entry), cpxFileHandler);
						                        listaXml.add(cpxFileHandler.getCpxBean());
						                    } catch (Exception ex) {
						                        throw new AcceptanceException(
						                                ValidationConstants.ACCETTAZIONE_FILE_XML_CORROTTO,
						                                "File xml indici non corretto: parser xml fallito con errore: " + ex.getMessage());
						                    }
						                }
						            }
						        }
						    } finally {
						        if (sevenZ != null) {
						            sevenZ.close();
						        }
						    }
						} else if (file.getName().endsWith(".zip")) {
							// unzip file zip
							org.apache.commons.compress.archivers.zip.ZipFile zip = new org.apache.commons.compress.archivers.zip.ZipFile(file);
							try {
								for (Enumeration e = zip.getEntries(); e.hasMoreElements();) {
									org.apache.commons.compress.archivers.zip.ZipArchiveEntry entry = (org.apache.commons.compress.archivers.zip.ZipArchiveEntry) e.nextElement();
									if (!entry.isDirectory()) {
										String nameFileInZip = entry.getName().toLowerCase();
										if (nameFileInZip.matches(regFileName)) {
											try {
												CPXFileHandler cpxFileHandler = new CPXFileHandler();
												parser.parse(zip.getInputStream(entry), cpxFileHandler);
												listaXml.add(cpxFileHandler.getCpxBean());
											} catch (Exception ex) {
												throw new AcceptanceException(
														ValidationConstants.ACCETTAZIONE_FILE_XML_CORROTTO,
														"File xml indici non corretto: parser xml fallito con errore: "
																+ ex.getMessage());
											}
										}
									}
								}
							} finally {
								if (zip != null) {
									zip.close();
								}
							}
						}
					    
					}
					else {

						ZipFile zip = new ZipFile(file);
						try {
							for (Enumeration e = zip.entries(); e.hasMoreElements();) {
								ZipEntry entry = (ZipEntry) e.nextElement();
								if (!entry.isDirectory()) {
									String nameFileInZip = entry.getName().toLowerCase();
									if (nameFileInZip.matches(regFileName)) {
										try {
											CPXFileHandler cpxFileHandler = new CPXFileHandler();
											parser.parse(zip.getInputStream(entry), cpxFileHandler);
											listaXml.add(cpxFileHandler.getCpxBean());
										} catch (Exception ex) {
											throw new AcceptanceException(
													ValidationConstants.ACCETTAZIONE_FILE_XML_CORROTTO,
													"File xml indici non corretto: parser xml fallito con errore: "
															+ ex.getMessage());
										}
									}
								}
							}
						} finally {
							if (zip != null) {
								zip.close();
							}
						}
					}
					
				}
			}
		}
		return listaXml;
	}

	public static File downloadFileWithPresignedUrl(String fileURL, String outputDir)
			throws PagoPaDownloadFileException {
		HttpURLConnection httpConn = null;
		File savedFile = null;
		try {
			URL url = new URL(fileURL);
			httpConn = (HttpURLConnection) url.openConnection();
			int responseCode = httpConn.getResponseCode();
			if (responseCode == 200) {
				String fileName = "";
				String disposition = httpConn.getHeaderField("Content-Disposition");
				String contentType = httpConn.getContentType();
				int contentLength = httpConn.getContentLength();
				if (disposition != null) {
					int index = disposition.indexOf("filename=");
					if (index > 0)
						fileName = disposition.substring(index + 10, disposition.length() - 1);
				} else {
					if (fileURL.contains("?")) {
						String substringBasePath = fileURL.substring(0, fileURL.indexOf("?"));
						fileName = substringBasePath.substring(substringBasePath.lastIndexOf("/") + 1);
					} else {
						fileName = fileURL.substring(fileURL.lastIndexOf("/") + 1, fileURL.length());
					}
					fileName = (fileName != null) ? fileName : "undefined";
					if (!fileName.contains(".") && contentType != null
							&& contentType.equalsIgnoreCase("application/pdf"))
						fileName = fileName + ".pdf";
				}
				log.debug("Content-Type = " + contentType);
				log.debug("Content-Disposition = " + disposition);
				log.debug("Content-Length = " + contentLength);
				log.debug("fileName = " + fileName);
				InputStream inputStream = httpConn.getInputStream();
				savedFile = new File(outputDir, fileName);
				FileOutputStream outputStream = new FileOutputStream(savedFile);
				int bytesRead = -1;
				byte[] buffer = new byte[4096];
				while ((bytesRead = inputStream.read(buffer)) != -1)
					outputStream.write(buffer, 0, bytesRead);
				outputStream.close();
				inputStream.close();
				log.debug("File downloaded. Name: " + fileName);
			} else {
				log.debug("No file to download. Server replied HTTP code: " + responseCode);
//				throw new PagoPaDownloadFileException("Failed : HTTP error code : " + responseCode);
			}
		} catch (Exception e) {
			log.error("Error downloadFile ", e);
			throw new PagoPaDownloadFileException(e);
		} finally {
			if (httpConn != null)
				httpConn.disconnect();
		}
		return savedFile;
	}

	public static AWSS3Service getAWSS3Service() {
		String accessKey = LoaderProperties.getPropertyValue("S3_ACCESS_KEY");
		String secretKey = LoaderProperties.getPropertyValue("S3_SECRET_KEY");
		String endPoint = LoaderProperties.getPropertyValue("S3_ENDPOINT");
		String bucketName = LoaderProperties.getPropertyValue("S3_BUCKET_NAME");
		if (StringUtil.isStringNull(bucketName))
			return null;
		return new AWSS3Service(accessKey, secretKey, endPoint, bucketName);
	}

	public static String calcolaPercorsoS3(Date dateS3Path, String fileName, String pathBase, Long idFileStorage) {
		// ppa_input(pathBase)/anno/mese/giorno/idFileStorage_nomeFile
		String percorsoS3 = null;
		LocalDate localDate = dateS3Path.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		int giornoInt = localDate.getDayOfMonth();
		int meseInt = localDate.getMonthValue();
		int annoInt = localDate.getYear();

		String mese = (meseInt < 10 ? '0' : "") + new Integer(meseInt).toString();
		String giorno = (giornoInt < 10 ? '0' : "") + new Integer(giornoInt).toString();
		percorsoS3 = pathBase + "/" + annoInt + "/" + mese + "/" + giorno + "/" + idFileStorage + "/" + fileName;
		return percorsoS3;
	}

	public static String getPagoPaRequestId() throws DAOException {
		Long requestIdSeq = Long.valueOf(PagoPaLoaderDao.getSequenceValue(null, "SEQ_REQUEST_ID"));
		String requestIdPrefix = LoaderProperties.getPropertyValue("REQUEST_PREFIX");
		return requestIdPrefix + "_" + String.valueOf(requestIdSeq);
	}

	public static String getPagoPaNpsoRequestId() throws DAOException {
		Long requestIdSeq = Long.valueOf(PagoPaLoaderDao.getSequenceValue(null, "SEQ_REQUEST_ID_NPSO"));
		String requestIdPrefix = LoaderProperties.getPropertyValue("REQUEST_PREFIX");
		return requestIdPrefix + "_NPSO_" + String.valueOf(requestIdSeq);
	}

	public static List<String> getPagoPaRequestIdList(DBConnection dbConnection, Integer seqNumber) throws Exception {
		PagoPaDao pagopaServiceDao = new PagoPaDao();
		List<Long> seqList = pagopaServiceDao.getListSequenceValue(dbConnection, "SEQ_REQUEST_ID", seqNumber);
		List<String> result = new ArrayList<>();
		String requestIdPrefix = LoaderProperties.getPropertyValue("REQUEST_PREFIX");
		for (Long seqVal : seqList)
			result.add(requestIdPrefix + "_" + String.valueOf(seqVal));
		return result;
	}
	
//	Metodo riscritto per calcolo hash256 di file di grandi dimensioni
//	public static String getBinaryFileChecksum(File file) throws IOException {
//		byte[] sha3Hex = null;
//		sha3Hex = (new DigestUtils("SHA-256")).digest(FileUtil.getBytes(file));
//		return Base64.getEncoder().encodeToString(sha3Hex);
//	}
	
	public static String getBinaryFileChecksum(File file) throws IOException {
		byte[] sha3Hex = null;		
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			FileInputStream fis = new FileInputStream(file);
			byte[] byteArray = new byte[1024];
			int bytesCount = 0;
			while ((bytesCount = fis.read(byteArray)) != -1) {
				digest.update(byteArray, 0, bytesCount);
			}
			fis.close();
			sha3Hex = digest.digest();
		} catch (NoSuchAlgorithmException e) {
			log.error("Errore nel calcolo dell' Hash SHA-256 per il file " + file.getName(), e);
		} catch (FileNotFoundException e) {
			log.error("Errore nel calcolo dell' Hash SHA-256 per il file " + file.getName(), e);
		} catch (IOException e) {
			log.error("Errore nel calcolo dell' Hash SHA-256 per il file " + file.getName(), e);
		}
		return Base64.getEncoder().encodeToString(sha3Hex);
	}

	public static void invokeExtPushProgressEvents(List<PaperProgressStatusEvent> request) throws PagoPaException {
		try {
			log.debug("start invokeExtPushProgressEvents");
			ApiResponse<OperationResultCodeResponse> resp = callWsExtPushProgressEvents(request);
			if (resp == null || (resp.getStatusCode() != 200 && resp.getStatusCode() != 202))
				throw new PagoPaException("Failed : HTTP error code : "
						+ ((resp != null) ? Integer.valueOf(resp.getStatusCode()) : null));
			log.debug("end invokeExtPushProgressEvents. Response " + resp);
		} catch (com.postel.restclient.ext.consingress.invoker.ApiException e) {
			int statusCode = e.getCode();
			String respBody = e.getResponseBody();
			log.error("", (Throwable) e);
			log.error("Invocazione WS /v1/push-progress-events KO. Message:" + e.getMessage() + ", code " + statusCode
					+ ", respBody " + respBody);
			JSONObject jsonObjectRes = null;
			if (!StringUtil.isStringNull(respBody))
				jsonObjectRes = getBodyAsJsonObj(respBody);
			String resultCode = (jsonObjectRes != null && !jsonObjectRes.isNull("resultCode"))
					? jsonObjectRes.getString("resultCode")
					: null;
			if (statusCode == 400 && resultCode != null
					&& resultCode.equalsIgnoreCase(EventIFA10ErrorEnum.ERR_404_00.getKey())) {
				log.debug("Invocazione WS /v1/push-progress-events response 404");
			} else {
				throw new PagoPaException("Invocazione WS /v1/push-progress-events KO. Message:" + e.getMessage()
						+ ", code " + e.getCode() + ", respBody " + respBody);
			}
			log.error(e);
		}
	}

	public static com.postel.restclient.ext.consingress.invoker.ApiResponse<OperationResultCodeResponse> callWsExtPushProgressEvents(
			List<PaperProgressStatusEvent> request) throws com.postel.restclient.ext.consingress.invoker.ApiException {
		com.postel.restclient.ext.consingress.invoker.ApiClient apiClient = new com.postel.restclient.ext.consingress.invoker.ApiClient();
		apiClient.setHttpClient(ApiClientUtil.getHttpClientWithInterceptor());
		apiClient.setVerifyingSsl(LoaderProperties.getBooleanPropertyValue(LoaderConstants.WS_VERIFY_SSL, true));
		apiClient.setBasePath(LoaderProperties.getPropertyValue(LoaderConstants.EXT_CHANNEL_SERVICE_BASE_PATH));
		apiClient.setReadTimeout(
				LoaderProperties.getIntPropertyValue(LoaderConstants.REST_CLIENT_READ_TIMEOUT, 10) * 1000);

		DefaultApi apiConsIngress = new DefaultApi(apiClient);

		String xPagopaExtchServiceId = LoaderProperties
				.getPropertyValue(LoaderConstants.EXT_PROGRESS_EVENTS_EXTCH_SERVICE_ID);
		String xApiKey = LoaderProperties.getPropertyValue(LoaderConstants.EXT_PROGRESS_EVENTS_EXTCH_API_KEY);

		PagoPaLoaderHelper.logRequestResponse(request, true, null);
		com.postel.restclient.ext.consingress.invoker.ApiResponse<OperationResultCodeResponse> resp = apiConsIngress
				.sendPaperProgressStatusRequestWithHttpInfo(xPagopaExtchServiceId, xApiKey, request);
		PagoPaLoaderHelper.logRequestResponse(resp, false, null);
		return resp;
	}

	public static String getTokenMso() throws PagoPaException {
		String clientSecret = LoaderProperties.getPropertyValue("WS_TOKEN_MSO_CLIENT_SECRET_PARAM");
		String clientId = LoaderProperties.getPropertyValue("WS_TOKEN_MSO_CLIENT_ID_PARAM");
		String scope = LoaderProperties.getPropertyValue("WS_TOKEN_MSO_SCOPE_PARAM");
		String grantType = LoaderProperties.getPropertyValue("WS_TOKEN_MSO_GRANT_TYPE_PARAM");
		String uri = LoaderProperties.getPropertyValue("WS_TOKEN_MSO_URI");
		return getToken(clientSecret, clientId, scope, grantType, uri);
	}

	public static String getToken(String clientSecret, String clientId, String scope, String grantType, String uri)
			throws PagoPaException {
		String token = null;
		try {
			String input = "client_id=" + clientId + "&scope=" + scope + "&client_secret=" + clientSecret
					+ "&grant_type=" + grantType;
			String urlB = uri;
			URL url = new URL(urlB);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Transfer-Encoding", "application/x-www-form-urlencoded");
			OutputStream os = conn.getOutputStream();
			os.write(input.getBytes(), 0, input.length());
			os.flush();
			if (conn.getResponseCode() != 200)
				throw new PagoPaException("Failed : HTTP error code : " + conn.getResponseCode());
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String output = br.lines().collect(Collectors.joining());
			log.debug("Output from Server .... \n");
			log.debug(output);
			conn.disconnect();
			if (output != null) {
				JSONObject jObject = new JSONObject(output);
				token = (jObject.get("access_token") != null) ? (String) jObject.get("access_token") : null;
			}
		} catch (Throwable t) {
			throw new PagoPaException(t);
		}
		return token;
	}

	public static void invokeWsFormattingNewFlow(FlowInfoRequestModel request, Long jobId) throws PagoPaException {
		try {
			log.debug("start invokeWsFormatting jobId " + jobId);
			com.postel.restclient.formatting.webcheckin.invoker.ApiClient apiClient = new  com.postel.restclient.formatting.webcheckin.invoker.ApiClient();
			apiClient.setHttpClient(ApiClientUtil.getHttpClientWithInterceptor());
			apiClient.setVerifyingSsl(LoaderProperties.getBooleanPropertyValue("WS_VERIFY_SSL", true));
			apiClient.setBasePath(LoaderProperties.getPropertyValue("FORMATTING_WEBCHECKIN_SERVICE_BASE_PATH"));
			apiClient.setReadTimeout(LoaderProperties.getIntPropertyValue("REST_CLIENT_READ_TIMEOUT", 10) * 1000);
			CheckInApi apiSender = new CheckInApi(apiClient);
			String tokenResp = getTokenMso();
			apiClient.addDefaultHeader("Authorization", "Bearer " + tokenResp);
			PagoPaLoaderHelper.logRequestResponse(request, true, null);
			com.postel.restclient.formatting.webcheckin.invoker.ApiResponse<Void> resp = apiSender.webcheckinNewflowPutWithHttpInfo(request);

			if(resp==null || resp.getStatusCode()!=HttpURLConnection.HTTP_OK) {
				log.error("Risposta servizio createFlow KO. Jobid" + resp);
				throw new PagoPaRuntimeException("Risposta servizio createFlow KO. Jobid" + jobId);
			}
		} catch (com.postel.restclient.formatting.webcheckin.invoker.ApiException e) {
			log.error(e);
			throw new PagoPaRuntimeException("Invocazione WS FMT KO. Jobid" + jobId + ". " + e.getMessage());
		}
		log.debug("end invokeWsFormatting jobId " + jobId);
	}

	public static void invokeWsFormattingCheckRequest(CallBackRequestModel request, Long jobId) throws PagoPaException {
		try {
			log.debug("start invokeWsFormatting jobId " + jobId);
			com.postel.restclient.formatting.webcheckin.invoker.ApiClient apiClient = new  com.postel.restclient.formatting.webcheckin.invoker.ApiClient();
			apiClient.setHttpClient(ApiClientUtil.getHttpClientWithInterceptor());
			apiClient.setVerifyingSsl(LoaderProperties.getBooleanPropertyValue("WS_VERIFY_SSL", true));
			apiClient.setBasePath(LoaderProperties.getPropertyValue("FORMATTING_WEBCHECKIN_SERVICE_BASE_PATH"));
			apiClient.setReadTimeout(LoaderProperties.getIntPropertyValue("REST_CLIENT_READ_TIMEOUT", 10) * 1000);
			CheckInApi apiSender = new CheckInApi(apiClient);
			String tokenResp = getTokenMso();
			apiClient.addDefaultHeader("Authorization", "Bearer " + tokenResp);
			
			PagoPaLoaderHelper.logRequestResponse(request, true, null);
			com.postel.restclient.formatting.webcheckin.invoker.ApiResponse<Void> resp = apiSender.webcheckinCheckrequestPutWithHttpInfo(request);

			if(resp==null || resp.getStatusCode()!=HttpURLConnection.HTTP_OK) {
				log.error("Risposta servizio createFlow KO. Jobid" + resp);
				throw new PagoPaException("Risposta servizio createFlow KO. Jobid" + jobId);
			}
		} catch (com.postel.restclient.formatting.webcheckin.invoker.ApiException e) {
			log.error(e);
			throw new PagoPaException("Invocazione WS FMT KO. Jobid" + jobId + ". " + e.getMessage());
		}
		log.debug("end invokeWsFormatting jobId " + jobId);
	}

	public static String uploadFileToPagoPaStorage(Long jobId, File file, String hash256)
			throws DAOException, PagoPaDownloadFileException, IOException, PagoPaException {
		return uploadFileToPagoPaStorage(jobId, file, hash256, null, false);
	}

	public static String uploadFileToPagoPaStorage(Long jobId, File file, String hash256, String contentType)
			throws DAOException, PagoPaDownloadFileException, IOException, PagoPaException {
		return uploadFileToPagoPaStorage(jobId, file, hash256, contentType, false);
	}

	public static String uploadFileToPagoPaStorage(Long jobId, File file, String hash256, String contentType, boolean isFlussoCons)
			throws DAOException, PagoPaDownloadFileException, IOException, PagoPaException {
		String presignedUrl = null;
		InputStream input = null;
		String ppaStorageKey = null;
		String ppaSecret = null;
		String contentT = (contentType == null) ? "application/octet-stream" : contentType;
		try {
			log.debug("start invoke attachment-preload  jobId " + jobId);
			com.postel.restclient.ext.consingress.invoker.ApiClient apiClient = new com.postel.restclient.ext.consingress.invoker.ApiClient();
			apiClient.setHttpClient(ApiClientUtil.getHttpClientWithInterceptor());
			apiClient.setVerifyingSsl(LoaderProperties.getBooleanPropertyValue("WS_VERIFY_SSL", true));
			apiClient.setBasePath(LoaderProperties.getPropertyValue("EXT_CHANNEL_SERVICE_BASE_PATH"));
			apiClient.setReadTimeout(LoaderProperties.getIntPropertyValue("REST_CLIENT_READ_TIMEOUT", 10) * 1000);
			DefaultApi apiConsIngress = new DefaultApi(apiClient);
			String xPagopaExtchServiceId = LoaderProperties.getPropertyValue("EXT_ATTACH_PRELOAD_EXTCH_SERVICE_ID");
			String xApiKey = LoaderProperties.getPropertyValue("EXT_ATTACH_PRELOAD_API_KEY");
			InlineObject inlineObject = new InlineObject();
			PreLoadRequest preloadsItem = new PreLoadRequest();
			String requestId = getPagoPaRequestId();
			preloadsItem.setContentType(contentT);
			preloadsItem.setPreloadIdx(requestId);
			preloadsItem.setSha256(hash256);
			inlineObject.addPreloadsItem(preloadsItem);
			ApiResponse<InlineResponse200> resp = apiConsIngress
					.presignedUploadRequestWithHttpInfo(xPagopaExtchServiceId, xApiKey, inlineObject);
			logRequestResponse(resp, false, null);
			if (resp == null || resp.getStatusCode() != 200 || resp.getData() == null
					|| ((InlineResponse200) resp.getData()).getPreloads() == null
					|| ((InlineResponse200) resp.getData()).getPreloads().get(0) == null || StringUtil.isStringNull(
							((PreLoadResponse) ((InlineResponse200) resp.getData()).getPreloads().get(0)).getUrl())) {
				log.error("Risposta servizio /v1/attachment-preload KO. jobId " + jobId + ". resp: " + resp);
				throw new PagoPaDownloadFileException("Risposta servizio /v1/attachment-preload KO");
			}
			presignedUrl = ((PreLoadResponse) ((InlineResponse200) resp.getData()).getPreloads().get(0)).getUrl();
			ppaStorageKey = ((PreLoadResponse) ((InlineResponse200) resp.getData()).getPreloads().get(0)).getKey();
			ppaSecret = ((PreLoadResponse) ((InlineResponse200) resp.getData()).getPreloads().get(0)).getSecret();
			Map<String, String> headers = new HashMap<>();
			headers.put("x-amz-meta-secret", ppaSecret);
			headers.put("x-amz-checksum-sha256", hash256);

			log.debug("start uploadPresignedUrl  jobId " + jobId);
			log.debug("File: " + file.getAbsolutePath());
			if(isFlussoCons) {
				uploadDocFileConsWithPresignedUrl(presignedUrl, file, contentT, headers);
			}
			else {
				input = new ByteArrayInputStream(FileUtil.getBytes(file));
				uploadDocWithPresignedUrl(presignedUrl, input, contentT, headers);
			}
			log.debug("end uploadPresignedUrl  jobId " + jobId);
		} catch (com.postel.restclient.ext.consingress.invoker.ApiException e) {
			log.error(e);
			throw new PagoPaException("Invocazione WS /v1/attachments-preload KO. " + e.getMessage());
		} finally {
			if (input != null)
				input.close();
		}
		log.debug("end invoke attachments-preload and uploadPresignedUrl  jobId " + jobId);
		return ppaStorageKey;
	}

//	public static ObjectMapper getObjectMapperSbagliato() {
//		ObjectMapper mapper = new ObjectMapper();
//		mapper.enable(SerializationFeature.INDENT_OUTPUT);
//		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//		mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
//		mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"));
//		final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss XXX");
//		mapper.registerModule((Module) new JavaTimeModule());
//		SimpleModule simpleModule = new SimpleModule();
//		simpleModule.addSerializer(OffsetDateTime.class, new JsonSerializer<OffsetDateTime>() {
//			public void serialize(OffsetDateTime offsetDateTime, JsonGenerator jsonGenerator,
//					SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
//				jsonGenerator.writeString(DateTimeFormatter.ISO_DATE_TIME.format(offsetDateTime));
//			}
//		});
//		mapper.registerModule((Module) simpleModule);
//		mapper.registerModule((Module) new JavaTimeModule());
//		SimpleModule simpleModule2 = new SimpleModule();
//		simpleModule2.addSerializer(OffsetDateTime.class, new JsonSerializer<OffsetDateTime>() {
//			public void serialize(OffsetDateTime offsetDateTime, JsonGenerator jsonGenerator,
//					SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
//				jsonGenerator.writeString(DateTimeFormatter.ISO_DATE_TIME.format(offsetDateTime));
//			}
//		});
//		mapper.registerModule((Module) simpleModule2);
//		mapper.registerModule((Module) new JavaTimeModule());
//		SimpleModule simpleModule3 = new SimpleModule();
//		simpleModule3.addDeserializer(OffsetDateTime.class, new JsonDeserializer<OffsetDateTime>() {
//			public OffsetDateTime deserialize(JsonParser jsonParser, DeserializationContext ctxt)
//					throws IOException, JsonProcessingException {
//				String dateAsString = jsonParser.getText();
//				if (dateAsString == null)
//					return null;
//				return OffsetDateTime.parse(dateAsString, DateTimeFormatter.ISO_DATE_TIME);
//			}
//		});
//		mapper.registerModule((Module) simpleModule3);
//		mapper.registerModule((Module) new JavaTimeModule());
//		SimpleModule simpleModule4 = new SimpleModule();
//		simpleModule4.addDeserializer(OffsetDateTime.class, new JsonDeserializer<OffsetDateTime>() {
//			public OffsetDateTime deserialize(JsonParser jsonParser, DeserializationContext ctxt)
//					throws IOException, JsonProcessingException {
//				String dateAsString = jsonParser.getText();
//				if (dateAsString == null)
//					return null;
//				return OffsetDateTime.parse(dateAsString, DATE_TIME_FORMATTER);
//			}
//		});
//		mapper.registerModule((Module) simpleModule4);
//		return mapper;
//	}
	
	public static ObjectMapper getObjectMapper() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		mapper.setDateFormat(new SimpleDateFormat(LoaderConstants.LOG_REQ_RESP_DATE_FORMAT));
		
		org.threeten.bp.format.DateTimeFormatter DATE_TIME_FORMATTER= org.threeten.bp.format.DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss XXX");
		
		mapper.registerModule(new JavaTimeModule());
		SimpleModule simpleModule = new SimpleModule();
		simpleModule.addSerializer(OffsetDateTime.class, new JsonSerializer<OffsetDateTime>() {
			@Override
			public void serialize(OffsetDateTime offsetDateTime, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
				jsonGenerator.writeString(DateTimeFormatter.ISO_DATE_TIME.format(offsetDateTime));
			}
		});
		mapper.registerModule(simpleModule);

		mapper.registerModule(new JavaTimeModule());
		SimpleModule simpleModule2 = new SimpleModule();
		simpleModule2.addSerializer(org.threeten.bp.OffsetDateTime.class, new JsonSerializer<org.threeten.bp.OffsetDateTime>() {
			@Override
			public void serialize(org.threeten.bp.OffsetDateTime offsetDateTime, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
				jsonGenerator.writeString(DATE_TIME_FORMATTER.format(offsetDateTime));
			}
		});
		mapper.registerModule(simpleModule2);
		
		
		
		//deserialization module
		mapper.registerModule(new JavaTimeModule());
		SimpleModule simpleModule3 = new SimpleModule();
		simpleModule3.addDeserializer(OffsetDateTime.class, new JsonDeserializer<OffsetDateTime>() {
			@Override
			public OffsetDateTime deserialize(JsonParser jsonParser, DeserializationContext ctxt)
					throws IOException, JsonProcessingException {
				 String dateAsString = jsonParser.getText();
			        if (dateAsString == null) {
			           // throw new IOException("OffsetDateTime argument is null.");
			            return null;
			        }
			        return OffsetDateTime.parse(dateAsString, DateTimeFormatter.ISO_DATE_TIME);
			}
		});
		mapper.registerModule(simpleModule3);

		mapper.registerModule(new JavaTimeModule());
		SimpleModule simpleModule4 = new SimpleModule();
		simpleModule4.addDeserializer(org.threeten.bp.OffsetDateTime.class, new JsonDeserializer<org.threeten.bp.OffsetDateTime>() {
			@Override
			public org.threeten.bp.OffsetDateTime deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException, JsonProcessingException {
				String dateAsString = jsonParser.getText();
				if (dateAsString == null) {
					// throw new IOException("OffsetDateTime argument is null.");
					return null;
				}
				return org.threeten.bp.OffsetDateTime.parse(dateAsString,DATE_TIME_FORMATTER);
			}
		});
		mapper.registerModule(simpleModule4);

		return mapper;
	}

	public static void savePagoPaNotifiche(DBConnection dbConnection, List<PaperProgressStatusEvent> request,
			Long jobId) throws PagoPaException {
		try {
			log.debug("start savePagoPaNotifiche");
			Integer maxElemNr = Integer
					.valueOf(LoaderProperties.getIntPropertyValue("EXT_PROGRESS_EVENTS_MAX_ELEMENTS_NUMBER", 10));
			List<List<PaperProgressStatusEvent>> subSets = ListUtils.partition(request, maxElemNr.intValue());
			for (List<PaperProgressStatusEvent> subList : subSets) {
				ObjectMapper mapper = getObjectMapper();
				PagoPaNotificheBean notificaBean = new PagoPaNotificheBean();
				notificaBean.setJobId(jobId);
				String requestJson = mapper.writeValueAsString(subList);
				notificaBean.setRequestJson(requestJson);
				PagoPaLoaderDao.insertPagoPaNotificheBean(dbConnection, notificaBean);
			}
			log.debug("end savePagoPaNotifiche");
		} catch (Exception e) {
			throw new PagoPaException(e);
		}
	}

	private static JSONObject getBodyAsJsonObj(String bodyString) {
		try {
			return new JSONObject(bodyString);
		} catch (JSONException e) {
			log.error("getBodyAsJsonObj error parsing body to jsonObject ", (Throwable) e);
			return null;
		}
	}
	
	public static void invokeWsFormattingNewSignFlow(SignDocsRequestModel request, Long jobId) throws PagoPaException {
		try {
			log.debug("start invokeWsFormattingNewSignFlow jobId " + jobId);
			com.postel.restclient.formatting.webcheckin.invoker.ApiClient apiClient = new  com.postel.restclient.formatting.webcheckin.invoker.ApiClient();
			apiClient.setHttpClient(ApiClientUtil.getHttpClientWithInterceptor());
			apiClient.setVerifyingSsl(LoaderProperties.getBooleanPropertyValue("WS_VERIFY_SSL", true));
			apiClient.setBasePath(LoaderProperties.getPropertyValue("FORMATTING_WEBCHECKIN_SERVICE_BASE_PATH"));
			apiClient.setReadTimeout(LoaderProperties.getIntPropertyValue("REST_CLIENT_READ_TIMEOUT", 10) * 1000);
			CheckInApi apiSender = new CheckInApi(apiClient);
			String tokenResp = getTokenMso();
			apiClient.addDefaultHeader("Authorization", "Bearer " + tokenResp);
			PagoPaLoaderHelper.logRequestResponse(request, true, null);
			com.postel.restclient.formatting.webcheckin.invoker.ApiResponse<Void> resp = apiSender.webcheckinNewsignflowPutWithHttpInfo(request);

			if(resp==null || resp.getStatusCode()!=HttpURLConnection.HTTP_OK) {
				log.error("Risposta servizio newSignFlow KO. Jobid" + resp);
				throw new PagoPaRuntimeException("Risposta servizio newSignFlow KO. Jobid" + jobId);
			}
		} catch (com.postel.restclient.formatting.webcheckin.invoker.ApiException e) {
			log.error(e);
			throw new PagoPaRuntimeException("Invocazione WS FMT KO. Jobid" + jobId + ". " + e.getMessage());
		}
		log.debug("end invokeWsFormattingNewSignFlow jobId " + jobId);
	}
	
	public static OpenSearchClient getOpenSearchService() throws Exception {
		String hostname = LoaderProperties.getPropertyValue(LoaderConstants.OPENSEARCH_HOSTNAME_PARAM);
		String port = LoaderProperties.getPropertyValue(LoaderConstants.OPENSEARCH_PORT_PARAM);
		String username = LoaderProperties.getPropertyValue(LoaderConstants.OPENSEARCH_USERNAME_PARAM);
		String password = LoaderProperties.getPropertyValue(LoaderConstants.OPENSEARCH_PASSWORD_PARAM).substring(8);
						
		String keystoreFileName=LoaderProperties.getPropertyValue(LoaderConstants.KEYSTORE_FILE_NAME);
		String keystoreFilePath=LoaderProperties.getPropertyValue(LoaderConstants.KEYSTORE_FILE_PATH);
		String keystoreFilePassword=LoaderProperties.getPropertyValue(LoaderConstants.KEYSTORE_FILE_PASSWORD).substring(8);
		String keystoreFileParametersSecretPassword=LoaderProperties.getPropertyValue(LoaderConstants.KEYSTORE_FILE_PARAMETERS_SECRET_PASSWORD).substring(8);

		String pass= DeCryptionUtils.decryptTextByKeystore( keystoreFilePassword, keystoreFileParametersSecretPassword,
				password,keystoreFilePath+"/"+keystoreFileName, LoaderConstants.SECRET_PARAMETER_KEYSTORE);
		
		boolean ssl_enable = Boolean.valueOf(LoaderProperties.getPropertyValue(LoaderConstants.OPENSEARCH_SSL_ENABLE_PARAM));
		String ssl_trusstore_file = LoaderProperties.getPropertyValue(LoaderConstants.OPENSEARCH_SSL_TRUSTSTORE_FILE_PARAM);
		String ssl_trusstore_password = LoaderProperties.getPropertyValue(LoaderConstants.OPENSEARCH_SSL_TRUSTSTORE_PASSWORD_PARAM);
		
		
//		return OpenSearchBuilder.builderWithoutSSL(hostname, port, username, password);

		OpenSearchConfig instance = OpenSearchConfig.getInstance();
		OpenSearchConfig.setDbOpensearchHostname(hostname);
		instance.setDbOpensearchHostname(hostname);
		instance.setDbOpensearchPort(port);
		instance.setDbOpensearchUsername(username);
		instance.setDbOpensearchPassword(pass);
		instance.setDbOpensearchSslEnable(ssl_enable);
		instance.setDbOpensearchTruststoreFile(ssl_trusstore_file);
		instance.setDbOpensearchTruststorePassword(ssl_trusstore_password);
		return OpenSearchBuilder.buildClient(instance);
	}
	
	public static TrackingCruscottoInsert prepareTrackingCruscottoDistintaUnica(DistintaUnicaBean distintaUnicaBean, Date now,
			Map<String, Integer> mapStatoLogico, String statusCode) {
		TrackingCruscottoInsert trackBean = new TrackingCruscottoInsert();
		
		trackBean.setDataStatoLogico(now);
		trackBean.setRifStatoLogico(mapStatoLogico.get(statusCode));
		trackBean.setNomeIndice(PagoPaServiceConstants.NOME_INDICE_DISTINTA_UNICA);
		trackBean.setPrenotazioneId(distintaUnicaBean.getReservationId());
		trackBean.setSpedizioneId(distintaUnicaBean.getShippingId());
		trackBean.setNomeFileFlusso(distintaUnicaBean.getFileName());
		trackBean.setRifStampatoreEffettivo(distintaUnicaBean.getPrinterId());
		trackBean.setRifRecapitista(distintaUnicaBean.getCourierId());
		trackBean.setFileObjectId(distintaUnicaBean.getFileObjectId());
		trackBean.setRifIdFileStorage(distintaUnicaBean.getFileStorageId());
		trackBean.setDataRicezione(distintaUnicaBean.getInsertDate());
		trackBean.setRequestIdSistema(distintaUnicaBean.getRequestId());
		
		return trackBean;
	}
	
	public static TrackingCruscottoInsert prepareTrackingCruscottoDistintaPickup(DistintaPickupBean distintaPickupBean, Date now,
			Map<String, Integer> mapStatoLogico, String statusCode, List<String> prenotazioniIdlist) {
		
		TrackingCruscottoInsert trackBean = new TrackingCruscottoInsert();
		
		trackBean.setDataStatoLogico(now);
		trackBean.setRifStatoLogico(mapStatoLogico.get(statusCode));
		trackBean.setTipologiaFlusso(PagoPaServiceConstants.TIPOLOGIA_DISTINTA_PICKUP);
		trackBean.setNomeIndice(PagoPaServiceConstants.NOME_INDICE_DISTINTA_PICKUP);
		trackBean.setSpedizioneId(distintaPickupBean.getShippingId());
		trackBean.setRifStampatoreEffettivo(distintaPickupBean.getPrinterId());
		trackBean.setRifRecapitista(distintaPickupBean.getCourierId());
		trackBean.setNomeFileFlusso(distintaPickupBean.getFileName());
		trackBean.setRequestIdSistema(distintaPickupBean.getRequestId());
		trackBean.setDataRicezione(distintaPickupBean.getInsertDate());

		String elencoPrenotazioniId = "";
		for (String prenotazioneId : prenotazioniIdlist) {
			if (elencoPrenotazioniId.length() > 0)
				elencoPrenotazioniId = elencoPrenotazioniId + ",";
			elencoPrenotazioniId = elencoPrenotazioniId + prenotazioneId;
		}

		trackBean.setElencoPrenotazioniIdDistintaUnica(elencoPrenotazioniId);
		
		return trackBean;
	}
	
	public static TrackingCruscottoInsert prepareTrackingCruscottoFlussoStampa(Date now, Map<String,Integer> mapStatoLogico, String statusCode, String flowName, String customerFlowCodeParam, FileStorageBean fileStorageBean, List<BolFileBean> bolFileBeanList, String codiceStampatoreEffettivo) {
		TrackingCruscottoInsert trackBean = new TrackingCruscottoInsert();
		
		if(statusCode.equalsIgnoreCase("CON004")) {
			trackBean.setDataRicezione(now);
		}
		trackBean.setDataStatoLogico(now);
		trackBean.setRifStatoLogico(mapStatoLogico.get(statusCode));
		trackBean.setCodiceLottoCliente(customerFlowCodeParam);
		trackBean.setTipologiaFlusso(PagoPaServiceConstants.TIPOLOGIA_FLUSSO_DI_STAMPA);
		trackBean.setNomeIndice(PagoPaServiceConstants.NOME_INDICE_FLUSSI_STAMPA);
		if(flowName!=null) {
			trackBean.setCodiceLotto(flowName);
		}		
		if(fileStorageBean!=null) {
			trackBean.setNomeFileFlusso(fileStorageBean.getFileName());
			trackBean.setRifStampatoreOriginale(fileStorageBean.getFileName().split("_")[1]);
			trackBean.setRifRecapitista(fileStorageBean.getFileName().split("_")[2]);
		}

		if(codiceStampatoreEffettivo!=null) {
			trackBean.setRifStampatoreEffettivo(codiceStampatoreEffettivo);
		}
		if(bolFileBeanList!=null) {
			trackBean.setNumDoc(bolFileBeanList.size());
		}		
		
		return trackBean;
	}
	
	public static TrackingCruscottoInsert prepareTrackingCruscottoNotificaState(Date now, Map<String,Integer> mapStatoLogico, String statusCode, String requestId) {
		return prepareTrackingCruscottoNotificaState(now, mapStatoLogico, statusCode, requestId, null);
	}
	
	public static TrackingCruscottoInsert prepareTrackingCruscottoNotificaState(Date now, Map<String,Integer> mapStatoLogico, String statusCode, String requestId, String note) {
		TrackingCruscottoInsert trackingBean = new TrackingCruscottoInsert();
		
		trackingBean.setRequestId(requestId);
        trackingBean.setDataStatoLogico(now);
        trackingBean.setRifStatoLogico(mapStatoLogico.get(statusCode));
        trackingBean.setNomeIndice(PagoPaServiceConstants.NOME_INDICE_NOTIFICA);
        trackingBean.setNote(PagoPaLoaderHelper.getErrorForNote(note, 4000));
        
		return trackingBean;
	}
	
	public static boolean aggregateDocsAndStartWf(SAggregationBean beanAggregation, String workingFolderParam, String workflowNameParam, String configClienteIdParam, String activeServiceName) throws PagoPaException {
		log.debug("start aggregateDocsByTime aggregationId " + beanAggregation.getAggregationId());
		DBConnection dbConnection = null;
		boolean result = false;
		List<PagoPaPaperEngageDocBean> docListCut = new ArrayList<PagoPaPaperEngageDocBean>(); 
		int countFlows = 0;
	    List<Long> jobIdList= new ArrayList<Long>(); 
	    Date fileDate;
	    Long idSeqCodiceRun;
	    String codiceRun = null;
	    long maxSizeZip = 0;
	    long minSizeZip = 0;
	    boolean sevenZip = false;
	    String compressione = LoaderProperties.getPropertyValue("COMPRESSIONE_7ZIP");
	    if(compressione==null)
	    	throw new PagoPaException("Nessun parametro trovato per COMPRESSIONE_7ZIP");
	    if(compressione.equalsIgnoreCase("true"))
	    	sevenZip = true;
		try {
			dbConnection = LoaderDBConnectionManager.getInstance().getDBConnection();
			
			//Per ogni aggregato, recupero tutta la lista dei requestId già ordinati
			List<PagoPaPaperEngageDocBean> docList = PagoPaLoaderDao.getListPpaRequestDocBeanForAggregation(dbConnection, beanAggregation, StatoEnum.STATO_1002.getValue());
			if(docList.size()!=0) {
				//	        Calcolo il codice_run
			    fileDate = new Date();
				idSeqCodiceRun = Long.valueOf(PagoPaLoaderDao.getSequenceValue(dbConnection, "SEQ_GO_ID"));
				codiceRun = PagoPaLoaderHelper.generaCodiceRun(fileDate, idSeqCodiceRun);	
//			    Recupero la size massima per lo stampatore
			    Integer stampatoreId = docList.get(0).getStampatoreIdLogico();
			    if(stampatoreId==0)
			     throw new PagoPaException("Nessun stampatoreId trovato per id aggregazione: " + beanAggregation.getAggregationId()); 	
			    
				SStampatoreBean stampatore = PagoPaLoaderDao.getListStampatori(dbConnection).stream()
				  .filter(request -> Integer.toString(stampatoreId).equals(request.getId()))
				  .findAny()
				  .orElse(null);
				 
				if(stampatore==null)
				    throw new PagoPaException("Nessuno stampatore trovato per stampatore_id: " + stampatoreId); 	
			    
				maxSizeZip = stampatore.getFlussoMaxSizeMB();
			    minSizeZip = stampatore.getFlussoMinSizeMB();
			   
			    if(maxSizeZip==0 || minSizeZip==0)
			     throw new PagoPaException("Parametri flusso_min_size_mb, flusso_max_size_mb non trovati per stampatoreId= " + stampatoreId + ", id aggregazione: " + beanAggregation.getAggregationId()); 
			    else {
			    	maxSizeZip=maxSizeZip*MEGABYTE;
			    	minSizeZip=minSizeZip*MEGABYTE;
			    }
			    
			}
			while(docList.size()>0) {				
//				Sono nel caso di notifiche estere
				if(docList.get(0).getProductType().equalsIgnoreCase("RIR") || docList.get(0).getProductType().equalsIgnoreCase("RIS")) {
					countFlows = createFlowsInternational(workingFolderParam, workflowNameParam, configClienteIdParam,
							dbConnection, docListCut, countFlows, jobIdList, docList, codiceRun, maxSizeZip, sevenZip);
				}
				else {
				    countFlows = createFlowsNational(workingFolderParam, workflowNameParam, configClienteIdParam,
							dbConnection, docListCut, countFlows, jobIdList, docList, codiceRun, maxSizeZip,
							minSizeZip, sevenZip);
				}
				log.debug(countFlows + " flussi totali creati per codiceRun= " + codiceRun);
				}
//			Aggiorno i job aggiungendo il parametro del numero totale dei fussi
			for(Long jobId : jobIdList) {
				JobManager.setParameter(dbConnection, jobId, LoaderConstants.PARAM_TOTAL_FLOWS_PARAM_NAME, StringUtil.leftFillStringLength(Long.toHexString(countFlows), 3, "0"));
				JobManager.setParameter(dbConnection, jobId, LoaderConstants.PARAM_ACTIVESERVICE_NAME, activeServiceName);
			}
			
			beanAggregation.setLastExecDate(new Date()); 
			PagoPaLoaderDao.updateSAggregationLastExec(dbConnection, beanAggregation);
			
			dbConnection.commit();
		} catch (Throwable e) {
			if(dbConnection!=null){
				dbConnection.rollback();
			}
			log.error("Errore checkAndAggregateDoc");
			throw new PagoPaException(e);
		} finally {
			if(dbConnection!=null){
				dbConnection.close();
			}
		}
		log.debug("end aggregateDocsByTime aggregationId " + beanAggregation.getAggregationId());
		return result;
	}

	private static int createFlowsInternational(String workingFolderParam, String workflowNameParam,
			String configClienteIdParam, DBConnection dbConnection, List<PagoPaPaperEngageDocBean> docListCut,
			int countFlows, List<Long> jobIdList, List<PagoPaPaperEngageDocBean> docList, String codiceRun,
			long maxSizeZip, boolean sevenZip) throws WorkflowException, DAOException {
		double startSum = docListCut.stream().mapToDouble(obj->obj.getSizeCompressed(sevenZip)).sum();
		while(docList.size()>0) {
			int i = 0;
			while(startSum<maxSizeZip) {
				if (startSum + docList.get(i).getSizeCompressed(sevenZip) <= maxSizeZip) {
					docListCut.add(docList.get(i));
//					Rimuovo dalla lista il reqId che ho aggiunto alla lista cut.
					String reqId = docList.get(i).getRequestId();
					docList.removeIf(request -> reqId.equalsIgnoreCase(request.getRequestId()));
					i++;
				}
				else {
//					Se ho raggiunto la dimensione chiudo il flusso e creo il job
					countFlows = closeFlow(workingFolderParam, workflowNameParam, configClienteIdParam, dbConnection,
							docList, codiceRun, countFlows, jobIdList, docListCut);
					break;
				}
			}
		}
		return countFlows;
	}

	private static int createFlowsNational(String workingFolderParam, String workflowNameParam,
			String configClienteIdParam, DBConnection dbConnection, List<PagoPaPaperEngageDocBean> docListCut,
			int countFlows, List<Long> jobIdList, List<PagoPaPaperEngageDocBean> docList, String codiceRun,
			long maxSizeZip, long minSizeZip, boolean sevenZip) throws WorkflowException, DAOException {
		List<String> capList;
		double sizeStart;
		double sizeToAdd;
		capList = docList.stream().map(PagoPaPaperEngageDocBean::getReceiverCap).distinct().collect(Collectors.toList());
		for(String cap : capList) {
			sizeStart = docListCut.stream().mapToDouble(obj->obj.getSizeCompressed(sevenZip)).sum();
			sizeToAdd = docList.stream().filter(request -> cap.equals(request.getReceiverCap())).mapToDouble(obj->obj.getSizeCompressed(sevenZip)).sum();
//						Se non ho raggiunto la soglia, aggiungo tutti i request successivi con lo stesso cap
			if(sizeStart + sizeToAdd<=maxSizeZip) {
				docListCut.addAll(docList.stream().filter(request -> cap.equals(request.getReceiverCap())).collect(Collectors.toList()));
//							Tolgo i cap, aggiunti alla lista cut, dalla lista originale.
				docList.removeIf(request -> cap.equals(request.getReceiverCap()));
//							Se ho inserito l'ultimo cap, creo il flusso ed esco.
				if(docList.size()==0) {
					countFlows = closeFlow(workingFolderParam, workflowNameParam, configClienteIdParam,
							dbConnection, docList, codiceRun, countFlows, jobIdList, docListCut);
					break;
				}
			}
			else {
//							Verifico se il pacchetto precedente ha raggiunto la soglia minima, allora chiudo il flusso
				if(sizeStart>=minSizeZip) {
					countFlows = closeFlow(workingFolderParam, workflowNameParam, configClienteIdParam,
							dbConnection, docList, codiceRun, countFlows, jobIdList, docListCut);
				}
//							Altrimenti aggiungo uno ad uno i request id del cap che non entrava nel seguente flusso
				else {
					List<PagoPaPaperEngageDocBean> docCapList = docList.stream().filter(request -> cap.equals(request.getReceiverCap())).collect(Collectors.toList());
					int i = 0;
					double startSum = docListCut.stream().mapToDouble(obj->obj.getSizeCompressed(sevenZip)).sum();
					while(startSum < maxSizeZip) {
//						Solo se sum+size nuovo file<maxSizeZip, allora aggiungo il file altrimenti esco
						if(startSum + docCapList.get(i).getSizeCompressed(sevenZip) <= maxSizeZip) {
							docListCut.add(docCapList.get(i));
//							Rimuovo dalla lista il reqId che ho aggiunto alla lista cut.
							String reqId = docCapList.get(i).getRequestId();
							docList.removeIf(request -> reqId.equalsIgnoreCase(request.getRequestId()));
							startSum+=docCapList.get(i).getSizeCompressed(sevenZip);
							i++;
						}
						else {
//							Se ho raggiunto la dimensione chiudo il flusso e creo il job
							countFlows = closeFlow(workingFolderParam, workflowNameParam, configClienteIdParam,
									dbConnection, docList, codiceRun, countFlows, jobIdList, docListCut);
							break;
						}
					}
				}
//							Esco dal ciclo for.
				break;
			}
		}
		return countFlows;
	}

	private static int closeFlow(String workingFolderParam, String workflowNameParam, String configClienteIdParam,
			DBConnection dbConnection, List<PagoPaPaperEngageDocBean> docList, String codiceRun, int countFlows,
			List<Long> jobIdList, List<PagoPaPaperEngageDocBean> docListCut) throws WorkflowException, DAOException {
		countFlows++;
		jobIdList.add(createNewJob(workingFolderParam, workflowNameParam, configClienteIdParam, dbConnection,
				docListCut, StringUtil.leftFillStringLength(Long.toHexString(countFlows), 3, "0"), codiceRun));
		docListCut.clear();
		return countFlows;
	}

	private static Long createNewJob(String workingFolderParam, String workflowNameParam, String configClienteIdParam,
			DBConnection dbConnection, List<PagoPaPaperEngageDocBean> docListCut, String countFlows, String codiceRun) throws WorkflowException, DAOException {
		Map<String, Object> wfParameters = new HashMap<String, Object>();
		wfParameters.put(LoaderConstants.PARAM_WORKING_FOLDER_PARAM_NAME, workingFolderParam);
		wfParameters.put(LoaderConstants.PARAM_CONFIG_CLIENTE_ID_PARAM_NAME, configClienteIdParam);
		wfParameters.put(LoaderConstants.PARAM_COUNT_FLOW_PARAM_NAME, countFlows);
		wfParameters.put(LoaderConstants.PARAM_CODICE_RUN_PARAM_NAME, codiceRun);
		//Avvio job
		Long jobId = JobManager.startNewJob(dbConnection, workflowNameParam, wfParameters, null);


		List<List<Object>> parametersList = new ArrayList<List<Object>>();
		for(PagoPaPaperEngageDocBean bean : docListCut) {
			bean.setJobId(jobId);
			bean.setRifStato(StatoEnum.STATO_1003.getValue());//in lavorazione
			
			List<Object> parameters = new ArrayList<Object>();
			parameters.add(StatoEnum.STATO_1003.getValue());
			parameters.add(new Date());
			parameters.add(jobId);
			parameters.add(bean.getRequestDocId());
			parametersList.add(parameters);
		}
		PagoPaLoaderDao.batchUpdate(dbConnection, PagoPaLoaderDao.QUERY_UPPDATE_PPA_REQDOC_INLAVORAZIONE, parametersList);
		return jobId;

	}

}
