package com.chatapp.messageservice.service;

import com.chatapp.shared.grpc.UserProfileRequest;
import com.chatapp.shared.grpc.UserProfileResponse;
import com.chatapp.shared.grpc.UserServiceGrpc;
import io.grpc.StatusRuntimeException;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

@Service
public class UserClientService {

    // Denna annotering injicerar automatiskt gRPC-klienten baserat på konfigurationen i application.yml
    @GrpcClient("user-service")
    private UserServiceGrpc.UserServiceBlockingStub userServiceStub;

    public UserProfileResponse getUserProfile(String userId) {
        try {
            // 1. Bygg förfrågan
            UserProfileRequest request = UserProfileRequest.newBuilder()
                    .setUserId(userId)
                    .build();

            // 2. Gör det synkrona gRPC-anropet över nätverket!
            return userServiceStub.getUserProfile(request);

        } catch (StatusRuntimeException e) {
            // Logga om något går fel (t.ex. om användaren inte hittades eller servern är nere)
            System.err.println("gRPC-anrop misslyckades: " + e.getStatus());
            throw e;
        }
    }
}
