package dev.drf.grpc.arm.service.stub;

import com.linecorp.armeria.common.RequestContext;
import com.linecorp.armeria.server.ServiceRequestContext;
import dev.drv.pokemon.api.Error;
import dev.drv.pokemon.api.*;
import io.grpc.stub.StreamObserver;
import org.springframework.stereotype.Service;

@Service
public class PokemonServiceStub extends PokemonServiceGrpc.PokemonServiceImplBase {
    @Override
    public void getPokemon(PokemonRequest request, StreamObserver<PokemonResponse> responseObserver) {
        ServiceRequestContext ctx = RequestContext.current();
        ctx.blockingTaskExecutor().submit(() -> {
            try {
                PokemonResponse response = buildResponse(request);
                responseObserver.onNext(response);
            } catch (Exception err) {
                responseObserver.onNext(buildError(request.getRequestUid(), err));
            } finally {
                responseObserver.onCompleted();
            }
        });
    }

    private PokemonResponse buildResponse(PokemonRequest request) {
        return PokemonResponse.newBuilder()
                .setRequestUid(request.getRequestUid())
                .setStatus(Status.SUCCESS)
                .build();
    }

    private PokemonResponse buildError(String uuid, Exception err) {
        return PokemonResponse.newBuilder()
                .setRequestUid(uuid)
                .setStatus(Status.ERROR)
                .addErrors(Error.newBuilder()
                        .setCode(err.getClass().toString())
                        .setMessage(err.getMessage())
                        .build())
                .build();
    }
}
