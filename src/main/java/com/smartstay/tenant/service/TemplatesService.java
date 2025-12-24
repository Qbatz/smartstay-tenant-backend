package com.smartstay.tenant.service;


import com.smartstay.tenant.dao.BillTemplates;
import com.smartstay.tenant.repository.BillTemplatesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TemplatesService {

    @Autowired
    private BillTemplatesRepository templateRepository;


    public BillTemplates getTemplateByHostelId(String hostelId) {
        return templateRepository.getByHostelId(hostelId);
    }
}
