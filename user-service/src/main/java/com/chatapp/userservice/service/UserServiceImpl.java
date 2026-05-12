package com.chatapp.userservice.service;

import com.chatapp.shared.grpc.*;
import com.chatapp.userservice.model.UserEntity;
import com.chatapp.userservice.repository.UserRepository;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

@GrpcService
public class UserServiceImpl extends UserServiceGrpc.UserServiceImplBase {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void getUserProfile(UserProfileRequest request, StreamObserver<UserProfileResponse> responseObserver) {
        String userId = request.getUserId();

        // FIX: Vi måste använda findByUserId eftersom request.getUserId()
        // skickar strängen "user-111", inte databasens interna Long-ID.
        Optional<UserEntity> userOpt = userRepository.findByUserId(userId);

        if (userOpt.isPresent()) {
            UserEntity user = userOpt.get();

            UserProfileResponse response = UserProfileResponse.newBuilder()
                    .setUserId(user.getUserId()) // Använd String-ID:t här
                    .setUsername(user.getUsername())
                    .setEmail(user.getEmail())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } else {
            responseObserver.onError(
                    Status.NOT_FOUND
                            .withDescription("User with ID " + userId + " not found.")
                            .asRuntimeException()
            );
        }
    }

    @Override
    public void getLoginDetails(LoginDetailsRequest request, StreamObserver<LoginDetailsResponse> responseObserver) {
        // Här använder vi redan findByUsername vilket är korrekt
        Optional<UserEntity> userOpt = userRepository.findByUsername(request.getUsername());

        if (userOpt.isPresent()) {
            UserEntity user = userOpt.get();
            LoginDetailsResponse response = LoginDetailsResponse.newBuilder()
                    .setUserId(user.getUserId())
                    .setUsername(user.getUsername())
                    .setPassword(user.getPassword())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } else {
            responseObserver.onError(
                    Status.NOT_FOUND
                            .withDescription("Användaren hittades inte")
                            .asRuntimeException()
            );
        }
    }
}