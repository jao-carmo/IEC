package br.com.fatecads.fatecads.config;

public class UserDetailsImpl implements org.springframework.security.core.userdetails.UserDetails {
    private final br.com.fatecads.fatecads.entity.User user;

    public UserDetailsImpl(br.com.fatecads.fatecads.entity.User user) {
        this.user = user;
    }

    @Override
    public java.util.Collection<? extends org.springframework.security.core.GrantedAuthority> getAuthorities() {
        String role = user.getRole();
        if (role == null || role.isBlank()) {
            role = "ROLE_USER";
        }
        if (!role.startsWith("ROLE_")) {
            role = "ROLE_" + role;
        }
        return java.util.List.of(new org.springframework.security.core.authority.SimpleGrantedAuthority(role));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getLogin();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
