package com.procsight.services;

import com.procsight.models.Company;
import com.procsight.repositories.CompanyRepository;
import com.procsight.exceptions.CompanyNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CompanyService {

    @Autowired
    private CompanyRepository companyRepository;

    public Company saveCompany(Company company) {
        return companyRepository.save(company);
    }

    public Company getCompanyById(String id) {
        return companyRepository.findById(id)
                .orElseThrow(() -> new CompanyNotFoundException("Company not found with id: " + id));
    }

    public Optional<Company> getCompanyByName(String name) {
        return companyRepository.findByName(name);
    }

    public List<Company> getAllCompanies() {
        return companyRepository.findByIsActiveTrue();
    }

    public List<Company> getCompaniesByIndustry(String industry) {
        return companyRepository.findByIndustry(industry);
    }

    public List<Company> getCompaniesBySubscriptionTier(String tier) {
        return companyRepository.findBySubscriptionTier(tier);
    }

    public Company updateCompany(String id, Company companyDetails) {
        Company company = getCompanyById(id);
        company.setName(companyDetails.getName());
        company.setIndustry(companyDetails.getIndustry());
        company.setSizeCategory(companyDetails.getSizeCategory());
        company.setAnnualRevenue(companyDetails.getAnnualRevenue());
        company.setHeadquartersCountry(companyDetails.getHeadquartersCountry());
        company.setTaxId(companyDetails.getTaxId());
        company.setSubscriptionTier(companyDetails.getSubscriptionTier());
        company.setComplianceRequirements(companyDetails.getComplianceRequirements());
        company.setCustomFields(companyDetails.getCustomFields());
        return companyRepository.save(company);
    }

    public void deleteCompany(String id) {
        Company company = getCompanyById(id);
        company.setIsActive(false);
        companyRepository.save(company);
    }
}