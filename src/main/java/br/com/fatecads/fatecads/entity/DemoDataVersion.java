package br.com.fatecads.fatecads.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class DemoDataVersion {
    @Id
    private String version;
    public DemoDataVersion(String version) { this.version = version; }
}
