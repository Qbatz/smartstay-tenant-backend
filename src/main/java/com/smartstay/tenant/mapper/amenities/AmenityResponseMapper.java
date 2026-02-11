package com.smartstay.tenant.mapper.amenities;

import com.smartstay.tenant.Utils.Utils;
import com.smartstay.tenant.dto.BillingDates;
import com.smartstay.tenant.response.amenity.AmenityDetails;
import com.smartstay.tenant.response.amenity.AmenityDetailsResponse;
import com.smartstay.tenant.service.HostelConfigService;

import java.util.Calendar;
import java.util.Date;
import java.util.function.Function;

public class AmenityResponseMapper implements Function<AmenityDetails, AmenityDetailsResponse> {

    private HostelConfigService hostelConfigService;

    public AmenityResponseMapper(HostelConfigService hostelConfigService) {
        this.hostelConfigService = hostelConfigService;
    }

    @Override
    public AmenityDetailsResponse apply(AmenityDetails amenityDetails) {

        String startDateString = null;
        String endDateString = null;
        String dueDateString = null;
        String nextStartDateString = null;
        String nextEndDateString = null;
        String nextDueDateString = null;

        if (Boolean.TRUE.equals(amenityDetails.getIsAssigned())
                && amenityDetails.getAmenityStartDate() != null){
            Date currentDate = new Date();

            //For Testing with Different Dates
//            Calendar dummyCal = Calendar.getInstance();
//            dummyCal.set(2026, Calendar.FEBRUARY, 2);
//            Date currentDate = dummyCal.getTime();

            BillingDates billingDates = hostelConfigService
                    .getBillingRuleOnDate(
                            amenityDetails.getHostelId(),
                            currentDate
                    );

            Date monthStart = billingDates.currentBillStartDate();
            Date monthEnd = billingDates.currentBillEndDate();
            Date dueDate = billingDates.dueDate();

            Date billStartDate = monthStart;

            if (Boolean.TRUE.equals(amenityDetails.getProRate())) {

                Calendar amenityCal = Calendar.getInstance();
                amenityCal.setTime(amenityDetails.getAmenityStartDate());
                int amenityDay = amenityCal.get(Calendar.DAY_OF_MONTH);

                Calendar billCal = Calendar.getInstance();
                billCal.setTime(monthStart);

                int maxDay = billCal.getActualMaximum(Calendar.DAY_OF_MONTH);

                // Clamp day if needed
                int finalDay = Math.min(amenityDay, maxDay);

                billCal.set(Calendar.DAY_OF_MONTH, finalDay);
                int cycleStartDay = billCal.get(Calendar.DAY_OF_MONTH);

                billStartDate = billCal.getTime();
                monthEnd = Utils.findLastDate(cycleStartDay, billStartDate);
                dueDate = Utils.addDaysToDate(
                        billStartDate,
                        billingDates.dueDays()
                );
            }

            Calendar nextCal = Calendar.getInstance();
            nextCal.setTime(billStartDate);
            nextCal.add(Calendar.MONTH, 1);
            Date nextStartDate = nextCal.getTime();

            int cycleStartDay = nextCal.get(Calendar.DAY_OF_MONTH);
            Date nextEndDate = Utils.findLastDate(cycleStartDay, nextStartDate);

            Date nextDueDate = Utils.addDaysToDate(
                    nextStartDate,
                    billingDates.dueDays()
            );

            startDateString = Utils.dateToString(billStartDate);
            endDateString = Utils.dateToString(monthEnd);
            dueDateString = Utils.dateToString(dueDate);
            nextStartDateString = Utils.dateToString(nextStartDate);
            nextEndDateString = Utils.dateToString(nextEndDate);
            nextDueDateString = Utils.dateToString(nextDueDate);
        }

        return new AmenityDetailsResponse(amenityDetails.getAmenityId(),
                amenityDetails.getAmenityName(), amenityDetails.getAmenityAmount(),
                amenityDetails.getDescription(), amenityDetails.getTermsAndCondition(),
                amenityDetails.getProRate(), amenityDetails.getIsAssigned(), startDateString,
                endDateString, dueDateString, nextStartDateString, nextEndDateString,
                nextDueDateString);
    }
}
