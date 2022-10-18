package com.file.base.service;

import com.file.base.usecase.DemoUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

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

    @GetMapping(path = "api/decode")
    public Mono<ResponseEntity> decode() {

        return Mono.just(useCase.decodeXLSX("base64"));
    }

}
