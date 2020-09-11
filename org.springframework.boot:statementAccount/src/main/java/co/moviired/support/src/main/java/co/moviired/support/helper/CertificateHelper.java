package co.moviired.support.helper;

import co.moviired.support.domain.entity.redshift.ExtractData;
import co.moviired.support.domain.enums.Product;
import co.moviired.support.util.UtilsHelper;
import com.itextpdf.html2pdf.HtmlConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public final class CertificateHelper {

    private CertificateHelper() {
        // Not is necessary this implementation
    }

    public static byte[] generatePdf(String pathTemplate, String token, Map<String, String> parameters) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(new ClassPathResource(pathTemplate).getFile()))) {
            StringBuilder template = new StringBuilder();
            String st;
            while ((st = br.readLine()) != null) {
                template.append(st);
            }
            String templateString = template.toString();
            for (Map.Entry<String, String> entry : parameters.entrySet()) {
                templateString = templateString.replace("${" + entry.getKey() + "}", entry.getValue());
            }
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            HtmlConverter.convertToPdf(templateString, byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            log.error("Error generating certificate for token {}: {}", token, e.getMessage());
            throw e;
        }
    }

    public static Map<String, String> getParametersForExtractCertificate(List<ExtractData> data) {
        HashMap<String, String> map = new HashMap<>();
        boolean isSetUserData = false;
        ArrayList<Product> activeProducts = new ArrayList<>();
        Double totalSales = 0.0;
        for (ExtractData extractData : data) {
            if (!isSetUserData) {
                map.put("user_msisdn", extractData.getPhoneNumber());
                map.put("user_name", extractData.getUserName());
                map.put("document_number", extractData.getDocumentNumber());
                map.put("store_name", extractData.getStoreName());

                map.put("initial_date", UtilsHelper.getDateStringForExtract(extractData.getYear(), extractData.getMonth(), true));
                map.put("end_date", UtilsHelper.getDateStringForExtract(extractData.getYear(), extractData.getMonth(), false));

                map.put("initial_balance", UtilsHelper.getCurrencyFormat(extractData.getInitialBalance()));

                isSetUserData = true;
            }

            Product product = Product.getByProductName(extractData.getProductName());

            boolean active = false;
            if (product.getCountPlacerHolder() != null) {
                map.put(product.getCountPlacerHolder(), String.valueOf(extractData.getCount()));
                active = true;
            }

            if (product.getValuePlaceHolder() != null) {
                map.put(product.getValuePlaceHolder(), UtilsHelper.getCurrencyFormat(extractData.getTotal()));
                if (product.isUseForCalculateTotalSales()) {
                    totalSales += extractData.getTotal();
                }
                active = true;
            }

            if (active) {
                activeProducts.add(product);
            }
        }

        activeProducts.add(Product.TOTAL_SALES);
        map.put(Product.TOTAL_SALES.getValuePlaceHolder(), UtilsHelper.getCurrencyFormat(totalSales));

        return getActiveInactiveProducts(map, activeProducts);
    }

    private static Map<String, String> getActiveInactiveProducts(Map<String, String> map, List<Product> activeProducts) {
        for (Product product : Product.values()) {
            if (product.getDisplayPlaceHolder() != null) {
                map.put(product.getDisplayPlaceHolder(), activeProducts.contains(product) ? "" : "none");
            }
        }
        return map;
    }
}

