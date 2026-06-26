package be.profacile.savefunds.domain.service.impl;

import be.profacile.savefunds.domain.entity.AuditLog;
import be.profacile.savefunds.domain.entity.Entreprise;
import be.profacile.savefunds.domain.entity.User;
import be.profacile.savefunds.domain.enums.AuditAction;
import be.profacile.savefunds.domain.enums.AuditOutcome;
import be.profacile.savefunds.domain.repository.AuditLogRepository;
import be.profacile.savefunds.domain.repository.EntrepriseRepository;
import be.profacile.savefunds.domain.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final EntrepriseRepository entrepriseRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public AuditLog record(
            User user,
            Long entrepriseId,
            AuditAction action,
            AuditOutcome outcome,
            String resourceType,
            Long resourceId,
            String details
    ) {
        AuditLog auditLog = new AuditLog();
        auditLog.setUserId(user.getId());
        auditLog.setUserEmail(user.getEmail());
        auditLog.setEntreprise(resolveEntreprise(entrepriseId));
        auditLog.setAction(action);
        auditLog.setOutcome(outcome);
        auditLog.setResourceType(resourceType);
        auditLog.setResourceId(resourceId);
        auditLog.setDetails(details);
        return auditLogRepository.save(auditLog);
    }

    @Override
    public List<AuditLog> findLastForEntreprise(Long entrepriseId) {
        return auditLogRepository.findTop50ByEntrepriseIdOrderByCreatedAtDesc(entrepriseId);
    }

    private Entreprise resolveEntreprise(Long entrepriseId) {
        if (entrepriseId == null) {
            return null;
        }
        return entrepriseRepository.findById(entrepriseId).orElse(null);
    }
}
