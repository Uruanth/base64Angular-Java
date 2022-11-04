package com.example.cargas.archivo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class Service {

    private String completo = "";
    private final UseCase useCase;

    /**
     * Se trae la info en un atributo de base64
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, path = "api/carga/base64")
    public Mono<ResponseEntity> cargarExcel(@RequestBody DTO request) {
        return Mono.just(useCase.respuesta(request.data));
    }


    /**
     * Recibir por partes el archivo en base64
     *
     * @param name
     * @param size
     * @param currentChunk
     * @param totalChunk
     * @param request
     * @return
     */
    @PostMapping(path = "api/stream")
    public Mono<ResponseEntity> stream(@RequestParam("name") String name,
                                       @RequestParam("size") String size,
                                       @RequestParam("currentChunkIndex") String currentChunk,
                                       @RequestParam("totalChunks") String totalChunk,
                                       @RequestBody DTO request) {
        log.info("current: " + currentChunk);
        log.info("total: " + totalChunk);
        int siguiente = Integer.parseInt(currentChunk) + 1;
        if (Integer.parseInt(currentChunk) <= Integer.parseInt(totalChunk)) {
            this.completo += request.data;
            return Mono.just(new ResponseEntity(siguiente, HttpStatus.OK));
        } else {
            String data = this.completo;
            this.completo = "";
            return Mono.just(useCase.stream(data, Integer.parseInt(totalChunk)));
        }
    }


    /**
     * Recibir el archivo mediante multi-part/form
     *
     * @param files
     * @return
     */
    @PostMapping(path = "api/multipart", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity> multiParter3(@RequestPart(value = "files", required = false) Mono<FilePart> files) {
        log.info("entro multipart");
        List<ByteBuffer> byteList = new LinkedList<>();
        if (files == null) {
            return Mono.just(new ResponseEntity<>("No llego nada papi", HttpStatus.I_AM_A_TEAPOT));
        } else {
            return useCase.multiPart(files);
        }
    }

}
