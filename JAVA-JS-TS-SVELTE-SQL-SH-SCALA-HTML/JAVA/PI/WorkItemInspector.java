package it.postecom.rda.orchestrator.utils;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import it.postecom.rda.orchestrator.bean.generated.case1.contrattualizzazione.Contratto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import it.meware.integration.tibco.client.bean.generated.wp.AllocateAndOpenWorkItemResponse;
import it.meware.integration.tibco.client.bean.generated.wp.DataModel;
import it.meware.integration.tibco.client.bean.generated.wp.FieldType;
import it.meware.integration.tibco.client.bean.generated.wp.WorkItem;
import it.meware.integration.tibco.client.bean.generated.wp.WorkItemBody;
import it.meware.integration.tibco.client.bean.generated.wp.WorkItemHeader;
import it.meware.integration.tibco.client.bean.generated.wp.FieldType.ComplexSpec;
import it.postecom.rda.orchestrator.api.domain.WorkItemNameEnum;
import it.postecom.rda.orchestrator.bean.generated.case1.ListaSC;
import it.postecom.rda.orchestrator.bean.generated.case1.ShoppingCart;
import it.postecom.rda.orchestrator.services.converters.WorkItemNameConverter;
import it.postecom.rda.orchestrator.bean.generated.case1.contrattualizzazione.ObjectFactory;
import it.postecom.rda.orchestrator.exceptions.CustomIllegalStateException;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;


public class WorkItemInspector {

    private static final Logger log = LoggerFactory.getLogger(WorkItemInspector.class);

    private XmlUnmarshaller<ShoppingCart> shoppingCartUnmarshaller;
    private XmlUnmarshaller<ListaSC> listaScUnmarshaller;
    private WorkItemNameConverter workItemNameConverter;

    public WorkItemInspector() {
        this.shoppingCartUnmarshaller = new XmlUnmarshaller<>(ShoppingCart.class);
        this.listaScUnmarshaller = new XmlUnmarshaller<>(ListaSC.class);
        this.workItemNameConverter = new WorkItemNameConverter();
    }

    public WorkItemNameEnum getWorkItemName(AllocateAndOpenWorkItemResponse workItemResponse) {

        log.debug("Extracting Work Item Name from: {}", workItemResponse);

        final WorkItemNameEnum name = workItemResponse.getWorkItem().stream()
                .findFirst()
                .map(WorkItem::getHeader)
                .map(WorkItemHeader::getName)
                .map(workItemNameConverter)
                .orElse(WorkItemNameEnum.UNKNOWN);

        log.debug("Extracted Work Item Name: {}", name);

        return name;
    }

    public Optional<ShoppingCart> getShoppingCart(AllocateAndOpenWorkItemResponse workItemResponse) {

        log.debug("Extracting ShoppingCart from: {}", workItemResponse);

        final Optional<ShoppingCart> shoppingCart = workItemResponse.getWorkItem().stream()
                .findFirst()
                .map(WorkItem::getBody)
                .map(WorkItemBody::getDataModel)
                .map(DataModel::getInouts)
                .orElse(Collections.emptyList()).stream()
                .filter(f -> f.getName().equalsIgnoreCase("caseShopCartType"))
                .map(FieldType::getComplexSpec)
                .map(ComplexSpec::getValue)
                .flatMap(List::stream)
                .findFirst()
                .map(Element.class::cast)
                .map(Element::getOwnerDocument)
                .map(XmlUtils::convertShoppingCartDocumentToXmlString)
                .map(shoppingCartUnmarshaller::unmarshal);

        log.debug("Extracted ShoppingCart: {}", shoppingCart);

        return shoppingCart;
    }

    public List<ListaSC> getBuyers(AllocateAndOpenWorkItemResponse workItemResponse) {

        log.debug("Extracting Buyers from: {}", workItemResponse);

        final List<ListaSC> buyers = workItemResponse.getWorkItem().stream()
                .findFirst()
                .map(WorkItem::getBody)
                .map(WorkItemBody::getDataModel)
                .map(DataModel::getInouts)
                .orElse(Collections.emptyList()).stream()
                .filter(f -> f.getName().equalsIgnoreCase("listaBuyer"))
                .map(FieldType::getComplexSpec)
                .map(ComplexSpec::getValue)
                .flatMap(List::stream)
                .map(Element.class::cast)
                .map(Element::getOwnerDocument)
                .map(XmlUtils::convertListaScDocumentToXmlString)
                .map(listaScUnmarshaller::unmarshal)
                .collect(Collectors.toList());

        log.debug("Extracted Buyers: {}", buyers);

        return buyers;
    }

    public List<ListaSC> getResponsabiliIIILivello(AllocateAndOpenWorkItemResponse workItemResponse) {

        log.debug("Extracting Responsabili Livello 3 from: {}", workItemResponse);

        final List<ListaSC> resps = workItemResponse.getWorkItem().stream()
                .findFirst()
                .map(WorkItem::getBody)
                .map(WorkItemBody::getDataModel)
                .map(DataModel::getInputs)
                .orElse(Collections.emptyList()).stream()
                .filter(f -> f.getName().equalsIgnoreCase("listaRespTerzoLiv"))
                .map(FieldType::getComplexSpec)
                .map(ComplexSpec::getValue)
                .flatMap(List::stream)
                .map(Element.class::cast)
                .map(Element::getOwnerDocument)
                .map(XmlUtils::convertListaScDocumentToXmlString)
                .map(listaScUnmarshaller::unmarshal)
                .collect(Collectors.toList());

        log.debug("Extracted Responsabili III Livello: {}", resps);

        return resps;
    }

    public List<ListaSC> getResponsabiliIILivello(AllocateAndOpenWorkItemResponse workItemResponse) {

        log.debug("Extracting Responsabili II Livello from: {}", workItemResponse);

        final List<ListaSC> resps = workItemResponse.getWorkItem().stream()
                .findFirst()
                .map(WorkItem::getBody)
                .map(WorkItemBody::getDataModel)
                .map(DataModel::getInouts)
                .orElse(Collections.emptyList()).stream()
                .filter(f -> f.getName().equalsIgnoreCase("listaResponsabili"))
                .map(FieldType::getComplexSpec)
                .map(ComplexSpec::getValue)
                .flatMap(List::stream)
                .map(Element.class::cast)
                .map(Element::getOwnerDocument)
                .map(XmlUtils::convertListaScDocumentToXmlString)
                .map(listaScUnmarshaller::unmarshal)
                .collect(Collectors.toList());

        log.debug("Extracted Responsabili II Livello: {}", resps);

        return resps;
    }

    public List<Object> getOrCreateNotaTypeList(AllocateAndOpenWorkItemResponse workItemResponse) {

        final List<FieldType> inouts = workItemResponse.getWorkItem()
                .get(0)
                .getBody()
                .getDataModel()
                .getInouts();

        final Optional<FieldType> caseNotaType = inouts.stream()
                .filter(f -> "caseNotaType".equals(f.getName()))
                .findFirst();

        List<Object> notes = null;

        /*
         * caseNotaType available, extract the notes list
         */
        if (caseNotaType.isPresent()) {

            notes = caseNotaType
                    .map(FieldType::getComplexSpec)
                    .map(ComplexSpec::getValue)
                    .get();

        } else {

            /*
             * caseNotaType missing, create one adding a new
             * FieldType and ComplexSpec
             */

            final FieldType field = new FieldType();
            field.setName("caseNotaType");
            field.setArray(true);
            field.setComplexSpec(new ComplexSpec());

            inouts.add(field);

            notes = field.getComplexSpec().getValue();
        }

        return notes;
    }

    public Optional<FieldType> getInOutField(WorkItemBody workItem, String fieldName) {
        return workItem.getDataModel().getInouts().stream()
                .filter(f -> fieldName.equals(f.getName()))
                .findFirst();
    }

    public FieldType getMandatoryInOutField(WorkItemBody workItem, String fieldName) {
        return getInOutField(workItem, fieldName)
                .orElseThrow(() -> new CustomIllegalStateException("Expected inout field " + fieldName + " not found in work item"));
    }

    public Optional<FieldType> getInputField(WorkItemBody workItem, String fieldName) {
        return workItem.getDataModel().getInputs().stream()
                .filter(f -> fieldName.equals(f.getName()))
                .findFirst();
    }

    public FieldType getMandatoryInputField(WorkItemBody workItem, String fieldName) {
        return getInputField(workItem, fieldName)
                .orElseThrow(() -> new CustomIllegalStateException("Expected input field " + fieldName + " not found in work item"));
    }

    public Optional<FieldType> getOutputField(WorkItemBody workItem, String fieldName) {
        return workItem.getDataModel().getOutputs().stream()
                .filter(f -> fieldName.equals(f.getName()))
                .findFirst();
    }

    public FieldType getMandatoryOutputField(WorkItemBody workItem, String fieldName) {
        return getOutputField(workItem, fieldName)
                .orElseThrow(() -> new CustomIllegalStateException("Expected output field " + fieldName + " not found in work item"));
    }

    public void setShoppingCartUnmarshaller(XmlUnmarshaller<ShoppingCart> shoppingCartUnmarshaller) {
        this.shoppingCartUnmarshaller = Objects.requireNonNull(shoppingCartUnmarshaller);
    }

    public String getIdContrattoApprovazioneLivello3(WorkItem workItem) {
        Optional<FieldType> field = this.getInOutField(workItem.getBody(), "P_IdContratto");
        return field.orElseThrow(() -> new CustomIllegalStateException("No field P_IdContratto found"))
                .getSimpleSpec().getValue().stream()
                .findFirst().orElseThrow(() -> new CustomIllegalStateException("No value found"));
    }

    public String getIdContrattoInvioAttoAcquisto(WorkItem workItem) {
        Optional<FieldType> field = this.getInputField(workItem.getBody(), "P_IdContratto");
        return field.orElseThrow(() -> new CustomIllegalStateException("No field P_IdContratto found"))
                .getSimpleSpec().getValue().stream()
                .findFirst().orElseThrow(() -> new CustomIllegalStateException("No value found"));
    }

    public String getFieldContratto(WorkItem workItem, FieldName fieldName) {
        FieldType field = this.getInputField(workItem.getBody(), "caseContratto_SP").orElseThrow(() -> new CustomIllegalStateException("field caseContratto_SP not found"));
        Node valueNode = (Node) field.getComplexSpec().getValue().stream().findFirst().orElseThrow(() -> new CustomIllegalStateException("Values list is empty"));
        NodeList childNodes = valueNode.getChildNodes();
        int len = childNodes.getLength();
        Node contrattoElement = null;
        for (int i = 0; i < len; i++) {
            Node node = childNodes.item(i);

            if (node.getLocalName().equals("ContrattoElement")) {
                contrattoElement = node;
                break;
            }
        }

        if (contrattoElement == null) {
            throw new CustomIllegalStateException("WorkItem does not contain contratto element");
        }

        try {
            final JAXBContext context = JAXBContext.newInstance(ObjectFactory.class);
            final Unmarshaller unmarshaller = context.createUnmarshaller();
            JAXBElement<Contratto> contratto = (JAXBElement<Contratto>) unmarshaller.unmarshal(contrattoElement);
            PropertyDescriptor pd = new PropertyDescriptor(fieldName.getName(), Contratto.class);
            Method method = pd.getReadMethod();
            Object obj = method.invoke(contratto.getValue());
            return (String) obj;
        } catch (JAXBException | IntrospectionException | IllegalAccessException | InvocationTargetException e) {
            throw new CustomIllegalStateException("Unknown document format, contratto not found", e);
        }
    }

    public enum FieldName {
        TIPO_DOC_ACQUISTO("tipoDocAcquisto"),
        ID_CONTRATTO_SAP("idContrattoSAP");

        private final String name;

        FieldName(String s) {
            name = s;
        }

        public boolean equalsName(String otherName) {
            return name.equals(otherName);
        }

        public String getName() {
            return this.name;
        }

        public String toString() {
            return this.name;
        }
    }
}
