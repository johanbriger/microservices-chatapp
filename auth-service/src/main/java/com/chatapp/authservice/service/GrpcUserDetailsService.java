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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service
public class GrpcUserDetailsService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(GrpcUserDetailsService.class);

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
            logger.error("gRPC-fel vid inloggning för användare {}: {}", username, e.getMessage());
            throw new UsernameNotFoundException("Användare hittades inte: " + username);
        }
    }
}