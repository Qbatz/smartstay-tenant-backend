package com.smartstay.tenant.response.customer;

import com.smartstay.tenant.dto.hostel.HostelWithRentDTO;

import java.util.List;

public record CustomerHostelListWrapper(List<HostelWithRentDTO> activeStays,
                                        List<HostelWithRentDTO> previousStays) {
}
