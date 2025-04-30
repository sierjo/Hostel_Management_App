package com.hotel.Hotel.repo;

import com.hotel.Hotel.entity.Booking;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByRoomId(Long roomId);
    List<Booking> findByBookingConfirmationCode(String confirmationCOde);
    List<Booking> findByUserId(Long userId);
}
