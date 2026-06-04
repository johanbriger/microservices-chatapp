package com.chatapp.messageservice.service;

import com.chatapp.shared.grpc.UserProfileRequest;
import com.chatapp.shared.grpc.UserProfileResponse;
import com.chatapp.shared.grpc.UserServiceGrpc;
import io.grpc.StatusRuntimeException;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UserClientService {

    private static final Logger logger = LoggerFactory.getLogger(UserClientService.class);

    @GrpcClient("user-service")
    private UserServiceGrpc.UserServiceBlockingStub userServiceStub;

    public UserProfileResponse getUserProfile(String userId) {
        logger.info("Anropar User Service via gRPC för att hämta användarens profil för {}", userId);
        try {
            UserProfileRequest request = UserProfileRequest.newBuilder()
                    .setUserId(userId)
                    .build();

            return userServiceStub.getUserProfile(request);

        } catch (StatusRuntimeException e) {

            logger.error("gRPC-anrop till User Service misslyckades för userId {}: {}", userId, e.getStatus());
            throw e;
        }
    }
}
