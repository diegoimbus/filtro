package co.moviired.transpiler.integration.iso;

import co.moviired.connector.helper.ISOSocket;
import co.moviired.transpiler.integration.IHermesParser;
import co.moviired.transpiler.integration.iso.parser.IsoParserFactory;
import co.moviired.transpiler.integration.iso.service.IsoHelper;
import co.moviired.transpiler.integration.iso.service.IsoService;
import co.moviired.transpiler.jpa.movii.domain.Product;
import co.moviired.transpiler.jpa.movii.domain.enums.OperationType;
import co.moviired.transpiler.jpa.movii.domain.enums.ProductType;
import co.moviired.transpiler.jpa.movii.repository.IProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.jpos.iso.ISOBasePackager;
import org.jpos.iso.ISOException;
import org.jpos.iso.packager.GenericPackager;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.PageRequest;

import javax.validation.constraints.NotNull;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

@Slf4j
public class IsoController extends ISOSocket {

    private static final String TRAMA_INVALIDA = "trama-invalida";

    private final IProductRepository productRepository;
    private final IsoParserFactory parserFactory;
    private final IsoService isoService;
    private final String xmlPackager;

    public IsoController(
            @NotNull String xmlPackager,
            @NotNull IsoParserFactory pparserFactory,
            @NotNull IsoService pisoService,
            @NotNull IProductRepository productRepository) {
        super();

        this.parserFactory = pparserFactory;
        this.isoService = pisoService;
        this.productRepository = productRepository;
        this.xmlPackager = xmlPackager;
    }

    @Override
    public byte[] commuter(@NotNull String input) {
        // Respuesta predefinida
        String isoMessageResponse = TRAMA_INVALIDA;
        IHermesParser parser = null;
        ProductType productType;

        try {

            // Verificar si es la input de ECHO
            String operation = input.substring(0, 4);
            if (OperationType.ECHO.getCode().equals(operation)) {
                parser = parserFactory.getParser(OperationType.ECHO);
                isoMessageResponse = isoService.proccessEcho(input, parser, OperationType.ECHO).block();

            } else {

                StopWatch watchP = new StopWatch();
                watchP.start();

                // Obtener el parser específico a la operación y verifica el producto
                Product product = getProduct(input);
                if (product == null) {
                    throw new ISOException("PRODUCTO NO REGISTRADO");
                }

                // Obtener el tipo de producto
                productType = ProductType.valueOf(product.getType().getOrdinal());
                if (
                        (productType.getCode().equalsIgnoreCase("DCA")) ||
                                (productType.getCode().equalsIgnoreCase("DCP")) ||
                                (productType.getCode().equalsIgnoreCase("TV")) ||
                                (productType.getCode().equalsIgnoreCase("MP")) ||
                                (productType.getCode().equalsIgnoreCase("MT"))
                ) {
                    parser = parserFactory.getParser(OperationType.parse(operation));
                }

                // Procesar la input
                if (parser == null) {
                    throw new ISOException("TIPO DE PRODUCTO NO REGISTRADO");
                }

                watchP.stop();
                log.info("Preparacion del servicio ISO: {} millis", watchP.getTime());

                isoMessageResponse = isoService.proccess(input, parser, OperationType.parse(operation)).block();
            }

        } catch (Exception ex) {
            // Establecer que la trama recibida no es correcta
            log.error("ISO SERVER: {}. Causa: {}", isoMessageResponse, ex.getMessage(), ex);
        }

        assert isoMessageResponse != null;
        return isoMessageResponse.getBytes(StandardCharsets.UTF_8);
    }

    // Obetener el Producto de la petición
    private Product getProduct(String request) {
        Product product = null;
        try {
            // Cargar el ISO packager
            ISOBasePackager topupPackager = new GenericPackager(new ClassPathResource(xmlPackager).getInputStream());
            Map<Integer, String> isoFields = IsoHelper.getDataIsoFieldFromMessage(topupPackager, request);
            String dataField35 = isoFields.get(35);
            StringTokenizer tokens = new StringTokenizer(dataField35, "|");
            String eancode = tokens.nextToken();
            List<Product> tproduct;
            if (eancode != null) {
                tproduct = productRepository.findByEan(eancode, PageRequest.of(0, 1));
                if (!tproduct.isEmpty()) {
                    product = tproduct.get(0);
                }
            }

        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return product;
    }

}

