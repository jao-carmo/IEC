package br.com.fatecads.fatecads.repository;

import br.com.fatecads.fatecads.entity.DemoDataVersion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DemoDataVersionRepository extends JpaRepository<DemoDataVersion, String> {}
