package com.smartstay.tenant.mapper.bed;


import com.smartstay.tenant.Utils.Utils;
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
public class BedHistoryMapper {

    private final BedsRepository bedsRepository;
    private final RoomRepository roomsRepository;
    private final FloorRepository floorsRepository;

    public BedHistoryMapper(RoomRepository roomRepository, FloorRepository floorRepository, BedsRepository bedsRepository) {
        this.roomsRepository = roomRepository;
        this.floorsRepository = floorRepository;
        this.bedsRepository = bedsRepository;
    }

    public BedHistory map(CustomersBedHistory history,
                          Date billingStartDate,
                          Date billingEndDate,
                          Date fallbackEndDate) {

        Beds bed = bedsRepository.findById(history.getBedId()).orElse(null);
        Rooms room = roomsRepository.findById(history.getRoomId()).orElse(null);
        Floors floor = floorsRepository.findById(history.getFloorId()).orElse(null);

        long noOfDaysStayed = calculateBillingDays(
                history.getStartDate(),
                history.getEndDate(),
                billingStartDate,
                billingEndDate,
                fallbackEndDate
        );
        Double rent = history.getRentAmount();
        long totalDaysInAMonth = Utils.findNoOfDaysInCurrentMonth(billingStartDate);
        Double rentPerDay = rent / totalDaysInAMonth;
        double rentForStayedDays = rentPerDay * noOfDaysStayed;
        return new BedHistory(bed != null ? bed.getBedName() : "", room != null ? room.getRoomName() : "", floor != null ? floor.getFloorName() : "", noOfDaysStayed, Utils.roundOffWithTwoDigit(rentForStayedDays));
    }

    private long calculateBillingDays(
            Date stayStart,
            Date stayEnd,
            Date billingStart,
            Date billingEnd,
            Date fallbackEnd
    ) {

        if (stayStart == null || billingStart == null || billingEnd == null) {
            return 0;
        }

        if (stayEnd == null) {
            stayEnd = fallbackEnd;
        }

        LocalDate effectiveStart = max(toLocalDate(stayStart), toLocalDate(billingStart));
        LocalDate effectiveEnd = min(toLocalDate(stayEnd), toLocalDate(billingEnd));

        if (effectiveStart.isAfter(effectiveEnd)) {
            return 0;
        }

        return ChronoUnit.DAYS.between(effectiveStart, effectiveEnd) + 1;
    }

    private LocalDate toLocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private LocalDate max(LocalDate a, LocalDate b) {
        return a.isAfter(b) ? a : b;
    }

    private LocalDate min(LocalDate a, LocalDate b) {
        return a.isBefore(b) ? a : b;
    }

}

