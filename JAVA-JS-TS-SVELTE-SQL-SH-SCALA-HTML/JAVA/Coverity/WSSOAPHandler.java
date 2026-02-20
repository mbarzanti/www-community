package it.postel.geax.utils.ws;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WSSOAPHandler implements SOAPHandler<SOAPMessageContext> {
	private static final Logger log = LoggerFactory.getLogger(WSSOAPHandler.class);

	private boolean logMessagesEnabled = false;
	
	public WSSOAPHandler(boolean logMessages) {
		this.logMessagesEnabled = logMessages;
	}
	
	@Override	
	public boolean handleMessage(SOAPMessageContext context) {
		Boolean isOutBound = (Boolean) context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
		
		if (isOutBound.booleanValue()) {
			setHeaders(context);
		} else {
			// inbound
		}
		
		logSoapMessage(context);
		
		return true;
	}

	@Override
	public boolean handleFault(SOAPMessageContext context) {
		logSoapMessage(context);
		return false;
	}

	@Override
	public void close(MessageContext context) {
		//
	}

    @Override
    public Set<QName> getHeaders() {
        return new HashSet<QName>();
    }

	
	protected void setHeaders(SOAPMessageContext context) {
//		try {
//			SOAPHeader header = context.getMessage().getSOAPHeader();
//			if(wsTrovaConfigurationBean.getHeader().getHeaderPrefix() != null && !wsTrovaConfigurationBean.getHeader().getHeaderPrefix().isEmpty()) {
//				header.setPrefix("S");
//			}
//			SOAPFactory factory = SOAPFactory.newInstance();
//			SOAPElement baseElement = factory.createElement(wsTrovaConfigurationBean.getHeader().getBaseElement().getName(), wsTrovaConfigurationBean.getHeader().getBaseElement().getPrefix(), wsTrovaConfigurationBean.getHeader().getBaseElement().getUri());
//			String prefix = wsTrovaConfigurationBean.getHeader().getInternalElements().getPrefix();
//			String uri = wsTrovaConfigurationBean.getHeader().getInternalElements().getUri();
//			List<WsTrovaConfigurationHeaderInternalElementsElementBean> listElements = wsTrovaConfigurationBean.getHeader().getInternalElements().getElement();
//			if (listElements != null) {
//				for(WsTrovaConfigurationHeaderInternalElementsElementBean entry : listElements) {
//					SOAPElement element = factory.createElement(entry.getKey(), prefix, uri);
//					element.addTextNode(entry.getValue());
//					baseElement.addChildElement(element);
//				}
//			}
//			header.addChildElement(baseElement);
//		} catch (Exception e) {
//			log.error("", e);
//		}
	}
	
	protected void logSoapMessage(SOAPMessageContext context) {
		if(logMessagesEnabled) {
			if(log.isInfoEnabled()) {
				String endpointAddress = (String) context.get(BindingProvider.ENDPOINT_ADDRESS_PROPERTY);
				Boolean isOutBound = (Boolean) context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
				SOAPMessage soapMsg = context.getMessage();
		
				try {
					if (Boolean.TRUE.equals(isOutBound)) {
						log.info("________________outbound message________________");
					} else {
						log.info("________________inbound message________________");
					}
					log.info("Address: [{}]", endpointAddress);
		
					final TransformerFactory transformerFactory = TransformerFactory.newInstance();
					final Transformer transformer = transformerFactory.newTransformer();
		
					// Format it
					transformer.setOutputProperty(OutputKeys.INDENT, "yes");
					transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
		
					final Source soapContent = soapMsg.getSOAPPart().getContent();
		
					final ByteArrayOutputStream baos = new ByteArrayOutputStream();
					final StreamResult result = new StreamResult(baos);
					transformer.transform(soapContent, result);
		
					log.info(baos.toString("UTF-8"));
					log.info("_______________end message_________________");
				} catch (SOAPException | TransformerException | UnsupportedEncodingException e) {
					log.error("", e);
				}  
			} else {
				log.warn("Log dei messaggi abilitato ma il livello di log non è INFO, quindi non posso stampare nulla");
			}
		}
    }

}