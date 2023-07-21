package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    //ALL for booker
    List<Booking> findByBookerIdOrderByStartDesc(Long bookerId);

    //ALL for owner
    List<Booking> findByItemOwnerIdOrderByStartDesc(Long ownerId);

    //CURRENT for booker
    List<Booking> findByBookerIdAndStatusAndStartIsBeforeAndEndIsAfterOrderByStartDesc(Long bookerId,
            BookingStatus status, LocalDateTime start, LocalDateTime end);

    //CURRENT for owner
    List<Booking> findByItemOwnerIdAndStatusAndStartIsBeforeAndEndIsAfterOrderByStartDesc(Long ownerId,
            BookingStatus status, LocalDateTime start, LocalDateTime end);

    //PAST for booker
    List<Booking> findByBookerIdAndEndIsBeforeOrderByStartDesc(Long bookerId, LocalDateTime end);

    //PAST for owner
    List<Booking> findByItemOwnerIdAndEndIsBeforeOrderByStartDesc(Long ownerId, LocalDateTime end);

    //FUTURE for booker
    List<Booking> findByBookerIdAndStartIsAfterOrderByStartDesc(Long bookerId, LocalDateTime start);

    //FUTURE for owner
    List<Booking> findByItemOwnerIdAndStartIsAfterOrderByStartDesc(Long ownerId, LocalDateTime start);

    //WAITING or REJECTED for booker
    List<Booking> findByBookerIdAndStatusOrderByStartDesc(Long bookerId, BookingStatus status);

    //WAITING or REJECTED for owner
    List<Booking> findByItemOwnerIdAndStatusOrderByStartDesc(Long ownerId, BookingStatus status);

    //for lastBooking
    List<Booking> findByItemIdAndEndIsBeforeOrderByEndDesc(Long itemId, LocalDateTime end);

    // for nextBooking
    List<Booking> findByItemIdAndStartIsAfterOrderByStartAsc(Long itemId, LocalDateTime start);
}
