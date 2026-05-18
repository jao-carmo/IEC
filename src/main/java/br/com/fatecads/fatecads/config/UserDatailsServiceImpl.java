package br.com.fatecads.fatecads.config;

@org.springframework.stereotype.Service
public class UserDatailsServiceImpl implements org.springframework.security.core.userdetails.UserDetailsService {
    private final br.com.fatecads.fatecads.repository.UserRepository userRepository;

    @org.springframework.beans.factory.annotation.Autowired
    public UserDatailsServiceImpl(br.com.fatecads.fatecads.repository.UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public org.springframework.security.core.userdetails.UserDetails loadUserByUsername(String username)
            throws org.springframework.security.core.userdetails.UsernameNotFoundException {
        br.com.fatecads.fatecads.entity.User user = userRepository.findByLogin(username)
                .orElseThrow(() -> new org.springframework.security.core.userdetails.UsernameNotFoundException("User not found"));
        return new UserDetailsImpl(user);
    }
}
