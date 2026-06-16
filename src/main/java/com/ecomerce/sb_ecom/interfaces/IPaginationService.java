package com.ecomerce.sb_ecom.interfaces;

import com.ecomerce.sb_ecom.payload.pagination.PageInformation;
import org.springframework.data.domain.Page;

public interface IPaginationService {
    <T> PageInformation getPageInfo(Page<T> page);
}
