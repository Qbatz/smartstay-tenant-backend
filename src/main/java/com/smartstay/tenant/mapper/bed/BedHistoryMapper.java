package com.smartstay.tenant.mapper.bed;


import com.smartstay.tenant.dao.Beds;
import com.smartstay.tenant.dao.CustomersBedHistory;
import com.smartstay.tenant.dao.Floors;
import com.smartstay.tenant.dao.Rooms;
import com.smartstay.tenant.dto.invoice.BedHistory;
import com.smartstay.tenant.repository.BedsRepository;
import com.smartstay.tenant.repository.FloorRepository;
import com.smartstay.tenant.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class BedHistoryMapper {

    private final BedsRepository bedsRepository;
    private final RoomRepository roomsRepository;
    private final FloorRepository floorsRepository;

    public BedHistory map(CustomersBedHistory history) {

        Beds bed = bedsRepository.findById(history.getBedId()).orElse(null);
        Rooms room = roomsRepository.findById(history.getRoomId()).orElse(null);
        Floors floor = floorsRepository.findById(history.getFloorId()).orElse(null);

        long noOfDaysStayed = calculateDays(history.getStartDate(), history.getEndDate());

        return new BedHistory(bed != null ? bed.getBedName() : "", room != null ? room.getRoomName() : "", floor != null ? floor.getFloorName() : "", noOfDaysStayed, history.getRentAmount());
    }

    private long calculateDays(Date startDate, Date endDate) {

        if (startDate == null) {
            return 0;
        }

        LocalDate start = toLocalDate(startDate);
        LocalDate end = endDate != null ? toLocalDate(endDate) : LocalDate.now();

        long days = ChronoUnit.DAYS.between(start, end);

        return Math.max(days, 1);
    }

    private LocalDate toLocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }
}

