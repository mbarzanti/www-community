package it.postel.pagopa.cruscotto.controller;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import it.postel.pagopa.cruscotto.config.LoaderProperties;
import it.postel.pagopa.cruscotto.model.out.BaseResponse;
import it.postel.pagopa.cruscotto.utility.CruscottoUtility;
import it.postel.pagopa.opensearchclient.constants.Constants;

@CrossOrigin
@RestController
@RequestMapping("/api/download")
public class DownloadController {

	private static final Logger logger = LoggerFactory.getLogger(DownloadController.class);

	public static final String IN_ELABORAZIONE = "IN_ELABORAZIONE";
	public static final String COMPLETATO = "COMPLETATO";

	@Value("${sla.download.folder_download}")
	private String folderDownload;
	
	@Autowired
	private HttpServletRequest httpServletRequest;
	
	@PostMapping("/buffered")
	public BaseResponse downloadFile(@Valid @RequestParam String fileName, HttpServletResponse response)
			throws IOException {

		BaseResponse resp = new BaseResponse();
		if (logger.isDebugEnabled()) {
			logger.debug("post [/api/download/buffered]: fileName {}", fileName);
		}
		String username = (String) httpServletRequest.getSession().getAttribute("username");
		String fileDownload = folderDownload+username+File.separator+fileName;
		
		File file = new File(fileDownload + ".zip");
		logger.debug("Avvio download file zip: {} ", file.getName());
		if (file.exists()) {
			String contentType = CruscottoUtility.getContentType(file.toString());
			response.setContentType(contentType);
			response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");
			try (InputStream inputStream = new BufferedInputStream(new FileInputStream(file));
					OutputStream outputStream = response.getOutputStream()) {
				byte[] buffer = new byte[8192];
				int bytesRead;
				while ((bytesRead = inputStream.read(buffer)) != -1) {
					outputStream.write(buffer, 0, bytesRead);
					outputStream.flush();
				}
			}
		} else {

			logger.error("#### Errore durante il download del file: {} il file non è presente.",file.getName());
			logger.error("#### ErrorMessage: "+LoaderProperties.getPropertyValue(Constants.ERRORE_500) + " ErrorCode: "+ HttpStatus.INTERNAL_SERVER_ERROR.toString());
			resp.setValid(false);
			resp.setMessage(LoaderProperties.getPropertyValue(Constants.ERRORE_500));
		}
		return resp;
	}

	@GetMapping
	@RequestMapping("/status")
	public BaseResponse status(@Valid @RequestParam String uniqueId) {
		if (logger.isDebugEnabled()) {
			logger.debug(" get [/api/download/status] uniqueId: {} ", uniqueId);
		}
		BaseResponse resp = new BaseResponse();
		String username = (String) httpServletRequest.getSession().getAttribute("username");
		String fileExport = folderDownload+username+File.separator+uniqueId;
		File tmpFile = new File(fileExport + ".tmp");
		File csvFile = new File(fileExport + ".csv");
		File zipFile = new File(fileExport + ".zip");
		if (tmpFile.exists() || csvFile.exists() || (zipFile.exists() && csvFile.exists())) {
			resp.setValid(false);
			resp.setMessage(IN_ELABORAZIONE);
			resp.setResponseObject(uniqueId);
		} else if (zipFile.exists() && !tmpFile.exists() && !csvFile.exists()) {
			resp.setValid(true);
			resp.setMessage(COMPLETATO);
			resp.setResponseObject(uniqueId);
		} else {
			resp.setValid(false);
			resp.setMessage(HttpStatus.NOT_FOUND.toString());
			resp.setResponseObject(uniqueId);
		}
		return resp;
	}
}