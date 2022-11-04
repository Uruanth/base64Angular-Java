package com.file.base.usecase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Mono;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class DemoUseCase {
    public ResponseEntity respuesta(String body) {
        String temporal = body.split(",")[1];
        int tam = body.split(",").length;
        log.info("tamaño :" + tam);
        log.info("entro: =>  " + temporal);
        return new ResponseEntity(HttpStatus.OK);
    }


    public ResponseEntity decodeXLSX(String base64) {

        byte[] bytesArchivo = new byte[0];
        try {

            /**
             * Leer el archivo y codificarlo en base64
             */
            bytesArchivo = Files.readAllBytes(Paths.get("D:/Sura/seguridad.xlsx"));
            String baseLocal64 = Base64.getEncoder().encodeToString(bytesArchivo);

            /**
             * Decodificar base64 y crear el archivo excel
             */
            FileOutputStream writer = new FileOutputStream(new File("D:/Sura/prueba.xlsx"));
            writer.write(Base64.getDecoder().decode(baseLocal64));
            writer.close();

        } catch (IOException e) {
            log.error(e.getMessage());
            return new ResponseEntity(HttpStatus.I_AM_A_TEAPOT);
        }


        /**
         * Leer datos de un archivo excel
         */
        try {
            FileInputStream file = new FileInputStream("D:/Sura/prueba.xlsx");
            XSSFWorkbook libro = new XSSFWorkbook(file);
            log.info("consulto libro");
            log.warn(libro.getSheetAt(0).getRow(0).getCell(0).getStringCellValue());
            libro.close();

        } catch (IOException e) {
            log.error(e.getMessage());
            return new ResponseEntity(HttpStatus.I_AM_A_TEAPOT);
        }
        return new ResponseEntity(HttpStatus.OK);
    }

    public ResponseEntity encodeXLSX(String base64) {
        log.info("entro metodo encode");
        FileOutputStream writer = null;
        try {
            writer = new FileOutputStream(new File("D:/prueba.xlsx"));
            writer.write(Base64.getDecoder().decode(base64));
            writer.close();
        } catch (IOException e) {
            log.error(e.getMessage());
        }

        return new ResponseEntity(HttpStatus.OK);
    }

    public ResponseEntity stream(String base64, int total) {
//        log.info("entro metodo stream: " + base64);

        try (FileOutputStream writer = new FileOutputStream(new File("D:/prueba.xlsx"))) {
            writer.write(Base64.getDecoder().decode(base64));
            writer.close();
        } catch (IOException e) {
            log.error(e.getMessage());
            return new ResponseEntity(HttpStatus.I_AM_A_TEAPOT);
        }

        return new ResponseEntity(base64, HttpStatus.OK);
    }

    /**
     * Este funciona
     *
     * @param file las partes del archiv de forma reactiva
     * @return Algo para el front
     */
    public Mono<ResponseEntity> multiPart2(FilePart file) {
        log.info("entro al useCase");
        return file.content()
                .next()
                .map(DataBuffer::asInputStream)
                .map(input -> {
                    log.info("entro al map");
                    try {
                        XSSFWorkbook libro = new XSSFWorkbook(input);
                        log.info("consulto libro");
                        log.warn(libro.getSheetAt(0).getRow(0).getCell(0).getStringCellValue());
                        log.warn(libro.getSheetAt(0).getRow(0).getCell(1).getStringCellValue());
                        log.warn(libro.getSheetAt(0).getRow(0).getCell(2).getStringCellValue());
                        libro.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        log.error(e.getMessage());
                    }
                    return new ResponseEntity(HttpStatus.OK);
                });
    }

    ByteBuffer contador;

    public Mono<ResponseEntity> multiPart(FilePart file) {
        List<DataBuffer> otro = new LinkedList<>();

        try (FileOutputStream writer = new FileOutputStream(new File("D:/prueba.xlsx"))) {
            file.content()
//                .onBackpressureBuffer(1000)
//                    .buffer(10000)
                    .collectList()
                    .subscribe(item -> {
                        log.info("item: -> " + item.size());
                        item.forEach(objeto -> {
//                            this.contador.put(objeto.asByteBuffer().array());
                            try {
                                writer.getChannel().write(objeto.asByteBuffer());
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        });
                        log.info("adad " + this.contador.array().length);
//                        item.forEach(lista -> {
//                            lista.forEach(objeto -> {
//                                this.contador = this.contador + objeto.asByteBuffer().array().length;
//                                otro.add(objeto);
//                            });
//                        });

                    });
        } catch (IOException e) {
            log.error(e.getMessage());
        }

//        try {
//            log.info("entro al useCase");
//            log.info(file.filename());
//            File nuevo = new File("D:/pp.xlsx");
//            file.transferTo(nuevo)
//                    .then();
//            XSSFWorkbook libro = new XSSFWorkbook(nuevo);
//            log.info("consulto libro");
//            log.warn(libro.getSheetAt(0).getRow(0).getCell(0).getStringCellValue());
//            libro.close();
//
//        } catch (IOException e) {
//            log.error(e.getMessage());
//        } catch (InvalidFormatException e) {
//            throw new RuntimeException(e);
//        }
        return Mono.just(new ResponseEntity<>(HttpStatus.IM_USED));

    }

    public Mono<ResponseEntity> multiPart3(Mono<FilePart> files) {
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
