package be.profacile.savefunds.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "company_registry_entries")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyRegistryEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "enterprise_number", nullable = false, unique = true)
    private String enterpriseNumber;

    @Column(nullable = false)
    private String name;

    @Column(name = "legal_form")
    private String legalForm;

    @Column(nullable = false)
    private String status;

    private String address;

    @Column(name = "postal_code")
    private String postalCode;

    private String city;

    @Column(name = "nace_code")
    private String naceCode;

    @Column(name = "activity_label")
    private String activityLabel;

    @Column(nullable = false)
    private boolean active;

    @Column(nullable = false)
    private String source;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
