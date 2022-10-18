package com.file.base.usecase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

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
}
