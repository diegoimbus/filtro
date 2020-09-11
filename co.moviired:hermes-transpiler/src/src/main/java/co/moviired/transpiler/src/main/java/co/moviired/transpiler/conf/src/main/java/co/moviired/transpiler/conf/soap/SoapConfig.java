package co.moviired.transpiler.conf.soap;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.config.annotation.WsConfigurerAdapter;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.wsdl.wsdl11.Wsdl11Definition;
import org.springframework.xml.xsd.SimpleXsdSchema;
import org.springframework.xml.xsd.XsdSchema;

import java.util.Properties;

@EnableWs
@Configuration
@SuppressWarnings({"unsafe"})
public class SoapConfig extends WsConfigurerAdapter {

    public static final String NAMESPACE_URI = "http://ws.prepaidsale.solidda.koghi.com/";

    @Bean
    public ServletRegistrationBean<MessageDispatcherServlet> messageDispatcherServlet(ApplicationContext applicationContext) {
        MessageDispatcherServlet servlet = new MessageDispatcherServlet();
        servlet.setApplicationContext(applicationContext);
        servlet.setTransformWsdlLocations(true);
        servlet.setTransformSchemaLocations(true);
        return new ServletRegistrationBean<>(servlet, "/PrepaidSale/*");
    }

    @Bean(name = "WSPrepaidSaleService")
    public Wsdl11Definition defaultWsdl11Definition() {
        MyWsdl11Definition wsdl11Definition = new MyWsdl11Definition();
        wsdl11Definition.setPortTypeName("WSPrepaidSaleService");
        wsdl11Definition.setLocationUri("/PrepaidSale");
        wsdl11Definition.setTargetNamespace(NAMESPACE_URI);
        wsdl11Definition.setSchema(xsdSchemas());
        wsdl11Definition.setRequestSuffix("");

        Properties properties = new Properties();
        properties.setProperty("prepaidProductsActivation", NAMESPACE_URI + "/prepaidProductsActivation");
        wsdl11Definition.setSoapActions(properties);

        return wsdl11Definition;
    }

    @Bean
    public XsdSchema xsdSchemas() {
        return new SimpleXsdSchema(new ClassPathResource("xsd/WSPrepaidSaleService.xsd"));
    }

}
