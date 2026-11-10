package com.smartstay.tenant.mapper.amenities;

import com.smartstay.tenant.Utils.Utils;
import com.smartstay.tenant.dao.AmenitiesV1;
import com.smartstay.tenant.dao.AmenityRequest;
import com.smartstay.tenant.dao.CustomersAmenity;
import com.smartstay.tenant.dto.BillingDates;
import com.smartstay.tenant.response.amenity.AmenityDetailsResponse;
import com.smartstay.tenant.service.HostelConfigService;

import java.util.Calendar;
import java.util.Date;
import java.util.function.Function;

public class AmenityResponseMapper implements Function<AmenitiesV1, AmenityDetailsResponse> {

    private final HostelConfigService hostelConfigService;
    private final AmenityRequest amenityRequest;
    private final CustomersAmenity customersAmenity;

    public AmenityResponseMapper(HostelConfigService hostelConfigService,
                                 AmenityRequest amenityRequest,
                                 CustomersAmenity customersAmenity) {
        this.hostelConfigService = hostelConfigService;
        this.amenityRequest = amenityRequest;
        this.customersAmenity = customersAmenity;
    }

    @Override
    public AmenityDetailsResponse apply(AmenitiesV1 amenity) {

        String startDateString = null;
        String endDateString = null;
        String dueDateString = null;
        String nextStartDateString = null;
        String nextEndDateString = null;
        String nextDueDateString = null;
        String amenityStartDate = null;
        String amenityEndDate = null;
        boolean isAssigned = false;
        Date customersAmenityStartDate = null;
        Date customersAmenityEndDate = null;

        if (customersAmenity != null) {
            isAssigned = true;
            customersAmenityStartDate = customersAmenity.getStartDate();
            customersAmenityEndDate = customersAmenity.getEndDate();
        }

        if (isAssigned && customersAmenityStartDate != null){

            amenityStartDate = Utils.dateToString(customersAmenityStartDate);
            if (customersAmenityEndDate != null){
                amenityEndDate = Utils.dateToString(customersAmenityEndDate);
            }

            Date currentDate = new Date();

            if (customersAmenityEndDate == null) {

////                For Testing with Different Dates
//                Calendar dummyCal = Calendar.getInstance();
//                dummyCal.set(2026, Calendar.FEBRUARY, 2);
//                Date currentDate = dummyCal.getTime();

                BillingDates billingDates = hostelConfigService
                        .getBillingRuleOnDate(amenity.getHostelId(), currentDate);

                Date monthStart = billingDates.currentBillStartDate();
                Date monthEnd = billingDates.currentBillEndDate();
                Date dueDate = billingDates.dueDate();

                Date billStartDate = monthStart;

                if (Boolean.TRUE.equals(amenity.getIsProRate())) {

                    Calendar amenityCal = Calendar.getInstance();
                    amenityCal.setTime(customersAmenityStartDate);
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
        }

        boolean isRequestRaised = false;
        Long amenityRequestId = null;
        String requestDate = null;
        String requestTime = null;
        String actionType = null;
        if (amenityRequest != null) {
            isRequestRaised = true;
            amenityRequestId = amenityRequest.getAmenityRequestId();
            if (amenityRequest.getRequestedDate() != null){
                requestDate = Utils.dateToString(amenityRequest.getRequestedDate());
                requestTime = Utils.dateToTime(amenityRequest.getRequestedDate());
            }
            actionType = amenityRequest.getActionType();
        }

        return new AmenityDetailsResponse(amenity.getAmenityId(), amenity.getAmenityName(),
                amenity.getAmenityAmount(), amenity.getDescription(), amenity.getTermsAndCondition(),
                amenity.getIsProRate(), isAssigned, isRequestRaised, amenityRequestId, requestDate,
                requestTime, startDateString, endDateString, dueDateString, nextStartDateString,
                nextEndDateString, nextDueDateString, actionType, amenityStartDate, amenityEndDate);
    }
}
