package com.ecomerce.sb_ecom.service;

import com.ecomerce.sb_ecom.interfaces.IPaginationService;
import com.ecomerce.sb_ecom.payload.pagination.PageInformation;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public class PaginationService implements IPaginationService {

    @NotNull
    public <T> PageInformation getPageInfo(Page<T> page) {
        PageInformation pageInfo = new PageInformation();
        pageInfo.setPageSize(page.getSize());
        pageInfo.setPageNumber(page.getNumber() + 1);
        pageInfo.setTotalElements(page.getTotalElements());
        pageInfo.setTotalPages(page.getTotalPages());
        pageInfo.setLast(page.isLast());

        return pageInfo;
    }
}
