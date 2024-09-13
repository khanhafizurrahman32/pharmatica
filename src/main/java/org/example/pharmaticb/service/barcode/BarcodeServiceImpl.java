package org.example.pharmaticb.service.barcode;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code128Writer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
@Slf4j
public class BarcodeServiceImpl implements BarcodeService {
    @Override
    public byte[] generateBarcode(String billNumber) {
        BufferedImage barcodeImage;
        try {
            barcodeImage = generateBarcodeImage(billNumber);
        } catch (IOException e) {
            log.error("Generate Barcode Failed", e);
            return new byte[]{};
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        try {
            ImageIO.write(barcodeImage, "png", byteArrayOutputStream);
        } catch (IOException e) {
            log.error("Conversion from Image to byte array Failed", e);
            return new byte[]{};
        }

        return byteArrayOutputStream.toByteArray();
    }

    private BufferedImage generateBarcodeImage(String billNumber) throws IOException {
        Code128Writer writer = new Code128Writer();
        BitMatrix bitMatrix = writer.encode(billNumber, BarcodeFormat.CODE_128, 300, 150);
        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }
}
