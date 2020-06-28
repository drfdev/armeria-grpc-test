package dev.drf.grpc.arm.service;

import com.linecorp.armeria.common.grpc.GrpcSerializationFormats;
import com.linecorp.armeria.server.HttpServiceWithRoutes;
import com.linecorp.armeria.server.docs.DocServiceBuilder;
import com.linecorp.armeria.server.grpc.GrpcService;
import com.linecorp.armeria.spring.ArmeriaServerConfigurator;
import dev.drf.grpc.arm.service.stub.PokemonServiceStub;
import dev.drv.pokemon.api.PokemonRequest;
import dev.drv.pokemon.api.PokemonServiceGrpc;
import dev.drv.pokemon.model.*;
import io.grpc.protobuf.services.ProtoReflectionService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Configuration
public class SpringBootServiceConfiguration {

    @Bean
    public ArmeriaServerConfigurator armeriaServerConfigurator(PokemonServiceStub serviceStub) {
        ThreadLocalRandom random = ThreadLocalRandom.current();

        final PokemonRequest examplePokemonRequest = PokemonRequest.newBuilder()
                .setRequestUid(UUID.randomUUID().toString())
                .setPokemon(Pokemon.newBuilder()
                        .setId(random.nextLong())
                        .setDamage(Damage.newBuilder()
                                .setId(random.nextLong())
                                .setValue(random.nextLong())
                                .build())
                        .setDefence(Defence.newBuilder()
                                .setId(random.nextLong())
                                .setValue(random.nextLong())
                                .build())
                        .setType(PokemonType.newBuilder()
                                .setId(random.nextLong())
                                .setElement(ElementType.FIRE)
                                .setElementValue(random.nextInt())
                                .build())
                        .build())
                .build();

        final HttpServiceWithRoutes grpcService = GrpcService.builder()
                .addService(serviceStub)
                .addService(ProtoReflectionService.newInstance())
                .supportedSerializationFormats(GrpcSerializationFormats.values())
                .enableUnframedRequests(true)
                .build();

        return serverBuilder -> {
            serverBuilder.serviceUnder("/docs", new DocServiceBuilder()
                    .exampleRequestForMethod(PokemonServiceGrpc.SERVICE_NAME, "getPokemon", examplePokemonRequest)
                    .build()
            )
                    .service(grpcService);
        };
    }
}
