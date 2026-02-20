package com.infogroup.poste.common;

import java.io.StringWriter;
import java.util.Set;
import java.util.regex.Pattern;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamResult;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.infogroup.poste.common.EBAASConf;

//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;

/**
 * Handler per intercettare le richieste SOAP su tecnologia
 * JAX-WS ed effettuare il trace di informazioni inBound/outBound.
 * 
 * @author Infogroup
 *
 */
public class GectSoapHandler implements SOAPHandler<SOAPMessageContext> {

	private static final String FILE_RIMOSSO_PER_LOG = "**FILE_RIMOSSO_PER_LOG**";
//	private static Logger logger = Logger.getLogger(GectSoapHandler.class);
	private static Logger logger = LogManager.getLogger();

	@Override
	public void close(MessageContext arg0) {
		logger.info("Chiudo il SoapHandler");
	}

	/**
	 * Handler per i fault / errori
	 */
	@Override
	public boolean handleFault(SOAPMessageContext messageContext) {

		logger.debug(getSOAPMessage4Logging(messageContext));

		return false;
	}

	/**
	 * Handler inBound/outBound
	 */
	@Override
	public boolean handleMessage(SOAPMessageContext messageContext) {

		logger.debug(getSOAPMessage4Logging(messageContext));

		return true;
	}

	@Override
	public Set<QName> getHeaders() {
		return null;
	}


	/**
	 * Il metodo trasforma un messaggio SOAP andando a prendere soltanto la parte del messaggio, alleggerendolo degli allegati
	 * per la tracciatura in fase di logging
	 * @param message
	 * @return
	 * @throws TransformerConfigurationException
	 * @throws TransformerFactoryConfigurationError
	 * @throws TransformerException
	 * @throws SOAPException
	 */
	private String getSOAPMessage4Logging(SOAPMessageContext messageContext) {

		SOAPMessage message = messageContext.getMessage();

		String flowDirection = "INBOUND <-- ";
		Boolean outboundProperty = (Boolean) messageContext.get (MessageContext.MESSAGE_OUTBOUND_PROPERTY);
        if (outboundProperty.booleanValue()) {
        	flowDirection = "OUTBOUND --> ";
        }

		String soapMessage4Logging = null;

		try {

			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "no");
			StreamResult result = new StreamResult(new StringWriter());
			transformer.transform(message.getSOAPPart().getContent(), result);

			// Inizio la personalizzazione delle tracciature dei log
			String logOutput = result.getWriter().toString();

			// Nel caso del servizio EsitoFirma, tolgo il tag contenente il PDF
			Pattern esitoFirmaPattern = Pattern.compile("<filePDF>.+</filePDF>", Pattern.DOTALL); 
			logOutput = esitoFirmaPattern.matcher(logOutput).replaceAll("<filePDF>" + FILE_RIMOSSO_PER_LOG + "</filePDF>");

			// Nel caso del servizio SecurePaper, tolgo il tag contenente il PDF
			// Con la RegularExpression '(?:\\w*\\W*)' si cerca anche l'eventuale namespace davanti ai tag
			Pattern securePaperPattern = Pattern.compile("<(?:\\w*\\W*)securedDocument>.+</(?:\\w*\\W*)securedDocument>", Pattern.DOTALL); 
			logOutput = securePaperPattern.matcher(logOutput).replaceAll("<securedDocument>" + FILE_RIMOSSO_PER_LOG + "</securedDocument>");

			// Nel caso del servizio demMifidGianos, tolgo il tag contenente il PDF
			Pattern mifidGianosPattern = Pattern.compile("<pdfQuestionario.+>.+</pdfQuestionario>", Pattern.DOTALL); 
			logOutput = mifidGianosPattern.matcher(logOutput).replaceAll("<pdfQuestionario>" + FILE_RIMOSSO_PER_LOG + "</pdfQuestionario>");

			soapMessage4Logging = flowDirection + logOutput;

		} catch (TransformerFactoryConfigurationError | TransformerException | SOAPException e) {

			logger.error("Errore nella generazione del messaggio SOAP per la scrittura del log.", e);

		}

		return soapMessage4Logging;
	}

}
