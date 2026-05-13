package com.chatapp.authservice.service;

import com.chatapp.shared.grpc.LoginDetailsRequest;
import com.chatapp.shared.grpc.LoginDetailsResponse;
import com.chatapp.shared.grpc.UserServiceGrpc;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class GrpcUserDetailsService implements UserDetailsService {

    @GrpcClient("user-service")
    private UserServiceGrpc.UserServiceBlockingStub userServiceStub;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            LoginDetailsResponse response = userServiceStub.getLoginDetails(
                    LoginDetailsRequest.newBuilder().setUsername(username).build()
            );

            return new CustomUserDetails(
                    response.getUsername(),
                    "{noop}" + response.getPassword(),
                    AuthorityUtils.createAuthorityList("ROLE_USER"),
                    response.getUserId()
            );
        } catch (Exception e) {
            System.err.println("gRPC-fel vid inloggning: " + e.getMessage()); // Logga faktiska felet i konsolen
            throw new UsernameNotFoundException("Användare hittades inte: " + username);
        }
    }
}