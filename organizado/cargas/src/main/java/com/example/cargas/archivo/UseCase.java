package com.example.cargas.archivo;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;

@Slf4j
@RequiredArgsConstructor
public class UseCase {

    public ResponseEntity respuesta(String base64) {
        log.info(base64);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }


    public ResponseEntity stream(String base64, int total) {
        try (FileOutputStream writer = new FileOutputStream(new File("D:/prueba.xlsx"))) {
            writer.write(Base64.getDecoder().decode(base64));
        } catch (IOException e) {
            log.error(e.getMessage());
            return new ResponseEntity(HttpStatus.I_AM_A_TEAPOT);
        }
        return new ResponseEntity(base64, HttpStatus.OK);
    }


    public Mono<ResponseEntity> multiPart(Mono<FilePart> files) {
        log.info("usecase");
        return files.flatMap(file ->
                DataBufferUtils.join(file.content())
                        .map(DataBuffer::asByteBuffer)
                        .map(inStream -> {
                            try {
                                FileOutputStream writer = new FileOutputStream(new File("D:/prueba.xlsx"));
                                writer.write(inStream.array());
                                writer.close();

                            } catch (IOException e) {
                                log.error(e.getMessage());
                                return new ResponseEntity(HttpStatus.I_AM_A_TEAPOT);
                            }
                            return inStream;
                        })
                        .map(bites -> {
                            log.info("longitud -> " + bites.toString());
                            return new ResponseEntity("longitud -> " + bites.toString(), HttpStatus.IM_USED);
                        })
        );
    }

}
