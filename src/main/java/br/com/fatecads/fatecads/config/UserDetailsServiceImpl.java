package br.com.fatecads.fatecads.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import br.com.fatecads.fatecads.entity.User;
import br.com.fatecads.fatecads.repository.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {

        User user = userRepository.findByLogin(login)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found: " + login)
                );

        return new UserDetailsImpl(user);
    }

}