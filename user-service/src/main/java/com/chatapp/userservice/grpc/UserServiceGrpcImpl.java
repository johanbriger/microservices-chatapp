package com.chatapp.userservice.grpc;

import com.chatapp.shared.grpc.LoginDetailsRequest;
import com.chatapp.shared.grpc.LoginDetailsResponse;
import com.chatapp.shared.grpc.UserProfileRequest;
import com.chatapp.shared.grpc.UserProfileResponse;
import com.chatapp.shared.grpc.UserServiceGrpc;
import com.chatapp.userservice.model.UserEntity;
import com.chatapp.userservice.repository.UserRepository;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Optional;

@GrpcService
public class UserServiceGrpcImpl extends UserServiceGrpc.UserServiceImplBase {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceGrpcImpl.class);

    @Autowired
    private UserRepository userRepository;

    @Override
    public void getUserProfile(UserProfileRequest request, StreamObserver<UserProfileResponse> responseObserver) {
        logger.info("gRPC-anrop mottaget: Hämta profil för userId {}", request.getUserId());
        Optional<UserEntity> userOptional = userRepository.findByUserId(request.getUserId());

        if (userOptional.isPresent()) {
            UserEntity user = userOptional.get();
            UserProfileResponse response = UserProfileResponse.newBuilder()
                    .setUserId(user.getUserId())
                    .setUsername(user.getUsername())
                    .setEmail(user.getEmail())
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } else {
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription("Användarprofil hittades inte")
                    .asRuntimeException());
        }
    }

    @Override
    public void getLoginDetails(LoginDetailsRequest request, StreamObserver<LoginDetailsResponse> responseObserver) {
        logger.info("gRPC-anrop mottaget: Hämta inloggningsuppgifter för {}", request.getUsername());
        // Hämtar inloggningsuppgifter baserat på användarnamn (används av Auth Service)
        Optional<UserEntity> userOptional = userRepository.findByUsername(request.getUsername());

        if (userOptional.isPresent()) {
            UserEntity user = userOptional.get();
            LoginDetailsResponse response = LoginDetailsResponse.newBuilder()
                    .setUserId(user.getUserId())
                    .setUsername(user.getUsername())
                    .setPassword(user.getPassword())
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } else {
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription("Användare hittades inte")
                    .asRuntimeException());
        }
    }
}