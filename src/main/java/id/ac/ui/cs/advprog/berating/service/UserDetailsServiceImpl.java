package id.ac.ui.cs.advprog.berating.service;

import java.util.HashSet;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final JwtService jwtService;

    @Override
    public UserDetails loadUserByUsername(String token) throws UsernameNotFoundException {
        try {
            // Extract username and role directly from the JWT token
            String username = jwtService.extractUsername(token);
            String role = jwtService.extractRole(token);
            
            if (username == null) {
                throw new UsernameNotFoundException("User not found in token");
            }
            
            Set<GrantedAuthority> grantedAuthoritySet = new HashSet<>();
            if (role != null) {
                grantedAuthoritySet.add(new SimpleGrantedAuthority(role));
            }
            
            return new User(username, "", grantedAuthoritySet);
        } catch (Exception e) {
            throw new UsernameNotFoundException("Failed to extract user details from token", e);
        }
    }
}
