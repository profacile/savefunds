package be.profacile.savefunds.domain.service.company;

import be.profacile.savefunds.api.dto.response.CompanyRegistryImportResponse;
import org.springframework.web.multipart.MultipartFile;

public interface CompanyRegistryImportService {
    CompanyRegistryImportResponse importCsv(MultipartFile file);
}
