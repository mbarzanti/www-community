package it.poste.entity.bff.controllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import it.poste.dependencies.exceptions.model.OrchestratorInvokationExceptionType;
import it.poste.entity.bff.model.cgs.CgsConditionListResponse;
import it.poste.entity.bff.services.CgsServiceImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/cgs")
@Api("Controller for getting and setting data from/to FDI")
public class CGSController {
    private static final Logger LOG = LogManager.getLogger(CGSController.class);

        @Autowired
        CgsServiceImpl cgsService;

    @GetMapping(value = "/downloadpdf/{id}")
    @ApiOperation(value= "getDownloadData", notes = "Il servizio permette il recupero del pdf del contratto del servizio di firma digitale remota.")
    public ResponseEntity<byte[]> getDownloadData(@PathVariable String id)  {
        LOG.info("Start method getDownloadData");
        byte[] output =  org.apache.commons.codec.binary.Base64.decodeBase64(cgsService.getTermsConditionsPdf(id));

        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_PDF);
        header.set(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=Proposta e Condizioni Generali del Servizio Posta Elettronica Certifica.pdf");
        header.setContentLength(output.length);
        LOG.info("End method getDownloadData");

        return new ResponseEntity<>(output, header, HttpStatus.OK);
    }


    @GetMapping(value ="/download/contract")
    @ApiOperation(value= "getDownloadContract", notes = "Il servizio permette il recupero del tamplate contratto standard.")
    public ResponseEntity<Resource> getDownloadContract() throws IOException {
        LOG.info("Start method getDownloadContract");

        Resource resource = new ClassPathResource("termsAndConditions.pdf");

        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_PDF);
        header.set(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=termsAndConditions.pdf");
        header.setContentLength(resource.contentLength());

        LOG.info("Start method getDownloadContract");

        return new ResponseEntity<>(resource,header,HttpStatus.OK);
    }

    @GetMapping
    @RequestMapping("/get-cgs")
    CgsConditionListResponse getCgs(){
        LOG.info("Start method getDownloadContract");

        try {

            CgsConditionListResponse cgs = cgsService.getCgsConditions1();
            LOG.info("End method getDownloadContract");
            return cgs;

        } catch (OrchestratorInvokationExceptionType e) {
            LOG.error("ERROR IN get-cgs "+ e);
            LOG.error(e.getMessage());
            e.printStackTrace();
        }
        LOG.info("End method getDownloadContract");
        return null;
    }

}
