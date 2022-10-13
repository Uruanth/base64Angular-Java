package com.file.base.service;

import com.file.base.usecase.DemoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class DemoService {

    private final DemoUseCase useCase;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, path = "api/carga")
    public Mono<ResponseEntity> cargarExcel(@RequestBody DTO request){
        return Mono.just(useCase.respuesta(request.base64));
    }

    @PostMapping(path = "api/encode")
    public Mono<ResponseEntity> encode(@RequestBody DTO request){
        return Mono.just(useCase.encodeXLSX(request.base64.split(",")[1]));
    }

    @GetMapping(path = "api/decode")
    public Mono<ResponseEntity> decode(){
        return Mono.just(useCase.decodeXLSX("base64"));
    }

}
