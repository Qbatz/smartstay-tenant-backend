package com.smartstay.tenant.repository;

import com.smartstay.tenant.dao.BookingsV1;
import com.smartstay.tenant.response.customer.CustomersBookingDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface BookingsRepository extends JpaRepository<BookingsV1, String> {

    @Query(value = """
            SELECT bookingv1.bed_id as bedId, bookingv1.floor_id as floorId, bookingv1.room_id as roomId, 
            bookingv1.booking_amount as bookingAmount, bookingv1.checkout_date as checkoutDate, 
            bookingv1.rent_amount as rentAmount, bookingv1.leaving_date as leavingDate, bookingv1.notice_date as requestedCheckoutDate, 
            bookingv1.notice_date as noticeDate,  bookingv1.joining_date as joiningDate, bookingv1.booking_id as bookingId, 
            bookingv1.current_status as currentStatus, bookingv1.reason_for_leaving as reasonForLeaving, 
            bookingv1.expected_joining_date as expectedJoiningDate, room.room_name as roomName, flr.floor_name as floorName, 
            bed.bed_name as bedName  FROM bookingsv1 bookingv1 
            left outer join users usr on usr.user_id=bookingv1.created_by 
            left outer join rooms room on room.room_id=bookingv1.room_id 
            left outer join floors flr on flr.floor_id=bookingv1.floor_id 
            left outer join beds bed on bed.bed_id=bookingv1.bed_id 
            where bookingv1.customer_id=:customerId  order by bookingv1.joining_date desc limit 1
            """, nativeQuery = true)
    CustomersBookingDetails getCustomerBookingDetails(@Param("customerId") String customerId);

    ;

}
