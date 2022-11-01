package com.file.base.service;

import com.file.base.usecase.DemoUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class DemoService {

    private String completo = "";
    private final DemoUseCase useCase;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, path = "api/carga")
    public Mono<ResponseEntity> cargarExcel(@RequestBody DTO request) {
        return Mono.just(useCase.respuesta(request.data));
    }

    @PostMapping(path = "api/encode")
    public Mono<ResponseEntity> encode(@RequestBody DTO request) {
        return Mono.just(useCase.encodeXLSX(request.data.split(",")[1]));
    }

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
    Recibe un solo archivo
    */
    @PostMapping(path = "api/multipart", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity> multiParter(@RequestPart(value = "files", required = false) FilePart files) {
        log.info("entro multipart");
        List<ByteBuffer> byteList = new LinkedList<>();
        if (files == null) {
            return Mono.just(new ResponseEntity<>("No llego nada papi", HttpStatus.I_AM_A_TEAPOT));
        } else {
            log.info("file = " + files.filename());
//            files.content().subscribe(parte -> {
//                log.info("parte", parte);
//            });
            return useCase.multiPart(files);

        }
//            return Mono.just(new ResponseEntity<>(HttpStatus.OK));
    }

    /**
    Recibe una lista de archivos
    */
    @PostMapping(path = "api/multipart2", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<ResponseEntity> multiParter2(@RequestPart(value = "files", required = false) List<FilePart> files) {
        log.info("entro multipart");
        log.info("tamaño" + files.size());
        if (files == null) {
            return Mono.just(new ResponseEntity<>("No llego nada papi", HttpStatus.I_AM_A_TEAPOT));
        } else {
            log.info("file = " + files.get(0).filename());
            log.info("file = " + files.get(1).filename());
            files.get(0).content().subscribe(parte -> {
//                log.info("parte", parte);
            });
            return Mono.just(new ResponseEntity<>("todo normal mono", HttpStatus.OK));

        }
    }

    @GetMapping(path = "api/decode")
    public Mono<ResponseEntity> decode() {

        return Mono.just(useCase.decodeXLSX("base64"));
    }

}
