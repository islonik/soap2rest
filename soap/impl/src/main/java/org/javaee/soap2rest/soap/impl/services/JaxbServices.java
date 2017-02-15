package org.javaee.soap2rest.soap.impl.services;

import org.javaee.soap2rest.soap.impl.generated.ds.ws.DSRequest;
import org.javaee.soap2rest.soap.impl.generated.ds.ws.DSResponse;

import javax.enterprise.context.Dependent;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.stream.XMLStreamException;
import java.io.StringWriter;
import java.io.Writer;

/**
 * Provide logics for serialization and deserialization from Xml to Object and visa versa.
 * JAXBContext is thread safe and should only be created once and reused to avoid the cost of initializing the metadata multiple times.
 * Marshaller and Unmarshaller are not thread safe, but are lightweight to create and could be created per operation.
 */
@Dependent
public class JaxbServices {

    private final JAXBContext dsResponse;

    private final JAXBContext dsRequest;

    public JaxbServices() throws JAXBException {
        dsResponse = JAXBContext.newInstance(DSResponse.class);
        dsRequest = JAXBContext.newInstance(DSRequest.class);
    }

    public <T> String getDS(T ds) {
        try {
            Writer writer = new StringWriter();

            if (ds instanceof DSRequest) {
                Marshaller dsRequestMarshaller = dsRequest.createMarshaller();
                dsRequestMarshaller.marshal(ds, writer);
            } else {
                Marshaller dsResponseMarshaller = dsResponse.createMarshaller();
                dsResponseMarshaller.marshal(ds, writer);
            }
            return writer.toString();
        } catch (JAXBException jaxbe) {
            return jaxbe.getMessage();
        }
    }

    public DSResponse getDsResponse(SOAPMessage message) throws JAXBException, XMLStreamException, SOAPException  {
        Unmarshaller dsResponseUnmarshaller = dsResponse.createUnmarshaller();
        return (DSResponse) dsResponseUnmarshaller.unmarshal(message.getSOAPBody().extractContentAsDocument());
    }

    public DSRequest getDsRequest(SOAPMessage message) throws JAXBException, XMLStreamException, SOAPException {
        Unmarshaller dsRequestUnmarshaller = dsRequest.createUnmarshaller();
        return (DSRequest)dsRequestUnmarshaller.unmarshal(message.getSOAPBody().extractContentAsDocument());
    }

    public void setDsResponse(DSResponse dsResponse, SOAPBody soapBody) throws JAXBException {
        Marshaller marshaller = this.dsResponse.createMarshaller();
        marshaller.marshal(dsResponse, soapBody);
    }
}
