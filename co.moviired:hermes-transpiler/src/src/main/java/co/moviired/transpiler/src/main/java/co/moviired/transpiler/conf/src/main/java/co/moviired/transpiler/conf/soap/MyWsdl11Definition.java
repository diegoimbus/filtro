package co.moviired.transpiler.conf.soap;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.StringUtils;
import org.springframework.ws.wsdl.wsdl11.ProviderBasedWsdl4jDefinition;
import org.springframework.ws.wsdl.wsdl11.Wsdl11Definition;
import org.springframework.ws.wsdl.wsdl11.provider.InliningXsdSchemaTypesProvider;
import org.springframework.ws.wsdl.wsdl11.provider.SoapProvider;
import org.springframework.ws.wsdl.wsdl11.provider.SuffixBasedMessagesProvider;
import org.springframework.ws.wsdl.wsdl11.provider.SuffixBasedPortTypesProvider;
import org.springframework.xml.xsd.XsdSchema;
import org.springframework.xml.xsd.XsdSchemaCollection;

import javax.xml.transform.Source;
import java.util.Properties;

public class MyWsdl11Definition implements Wsdl11Definition, InitializingBean {

    private final InliningXsdSchemaTypesProvider typesProvider = new InliningXsdSchemaTypesProvider();

    private final SuffixBasedMessagesProvider messagesProvider = new MySuffixBasedMessagesProvider();

    private final SuffixBasedPortTypesProvider portTypesProvider = new MySuffixBasedPortTypesProvider();

    private final SoapProvider soapProvider = new SoapProvider();

    private final ProviderBasedWsdl4jDefinition delegate = new ProviderBasedWsdl4jDefinition();

    private String serviceName;

    public MyWsdl11Definition() {
        delegate.setTypesProvider(typesProvider);
        delegate.setMessagesProvider(messagesProvider);
        delegate.setPortTypesProvider(portTypesProvider);
        delegate.setBindingsProvider(soapProvider);
        delegate.setServicesProvider(soapProvider);
    }

    public final void setTargetNamespace(String targetNamespace) {
        delegate.setTargetNamespace(targetNamespace);
    }

    public final void setSchema(XsdSchema schema) {
        typesProvider.setSchema(schema);
    }

    public final void setSchemaCollection(XsdSchemaCollection schemaCollection) {
        typesProvider.setSchemaCollection(schemaCollection);
    }

    public final void setPortTypeName(String portTypeName) {
        portTypesProvider.setPortTypeName(portTypeName);
    }

    public final void setRequestSuffix(String requestSuffix) {
        portTypesProvider.setRequestSuffix(requestSuffix);
        messagesProvider.setRequestSuffix(requestSuffix);
    }

    public final void setResponseSuffix(String responseSuffix) {
        portTypesProvider.setResponseSuffix(responseSuffix);
        messagesProvider.setResponseSuffix(responseSuffix);
    }

    public final void setFaultSuffix(String faultSuffix) {
        portTypesProvider.setFaultSuffix(faultSuffix);
        messagesProvider.setFaultSuffix(faultSuffix);
    }

    public final void setCreateSoap11Binding(boolean createSoap11Binding) {
        soapProvider.setCreateSoap11Binding(createSoap11Binding);
    }

    public final void setCreateSoap12Binding(boolean createSoap12Binding) {
        soapProvider.setCreateSoap12Binding(createSoap12Binding);
    }

    public final void setSoapActions(Properties soapActions) {
        soapProvider.setSoapActions(soapActions);
    }

    public final void setTransportUri(String transportUri) {
        soapProvider.setTransportUri(transportUri);
    }

    public final void setLocationUri(String locationUri) {
        soapProvider.setLocationUri(locationUri);
    }

    public final void setServiceName(String pserviceName) {
        soapProvider.setServiceName(pserviceName);
        this.serviceName = pserviceName;
    }

    @Override
    public final void afterPropertiesSet() throws Exception {
        if (!StringUtils.hasText(delegate.getTargetNamespace()) && typesProvider.getSchemaCollection() != null &&
                typesProvider.getSchemaCollection().getXsdSchemas().length > 0) {
            XsdSchema schema = typesProvider.getSchemaCollection().getXsdSchemas()[0];
            setTargetNamespace(schema.getTargetNamespace());
        }
        if (!StringUtils.hasText(serviceName) && StringUtils.hasText(portTypesProvider.getPortTypeName())) {
            soapProvider.setServiceName(portTypesProvider.getPortTypeName() + "Service");
        }
        delegate.afterPropertiesSet();
    }

    @Override
    public final Source getSource() {
        return delegate.getSource();
    }
}

