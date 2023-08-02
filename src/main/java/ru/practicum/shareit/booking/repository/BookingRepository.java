package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    //ALL for booker
    Page<Booking> findByBookerIdOrderByStartDesc(Long bookerId, Pageable pageable);

    //ALL for owner
    Page<Booking> findByItemOwnerIdOrderByStartDesc(Long ownerId, Pageable pageable);

    //CURRENT for booker
    Page<Booking> findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(Long bookerId,
            LocalDateTime start, LocalDateTime end, Pageable pageable);

    //CURRENT for owner
    Page<Booking> findByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(Long ownerId,
            LocalDateTime start, LocalDateTime end, Pageable pageable);

    //PAST for booker
    Page<Booking> findByBookerIdAndEndIsBeforeOrderByStartDesc(Long bookerId, LocalDateTime end, Pageable pageable);

    //PAST for owner
    Page<Booking> findByItemOwnerIdAndEndIsBeforeOrderByStartDesc(Long ownerId, LocalDateTime end, Pageable pageable);

    //FUTURE for booker
    Page<Booking> findByBookerIdAndStartIsAfterOrderByStartDesc(Long bookerId, LocalDateTime start, Pageable pageable);

    //FUTURE for owner
    Page<Booking> findByItemOwnerIdAndStartIsAfterOrderByStartDesc(Long ownerId, LocalDateTime start,
            Pageable pageable);

    //WAITING or REJECTED for booker
    Page<Booking> findByBookerIdAndStatusOrderByStartDesc(Long bookerId, BookingStatus status, Pageable pageable);

    //WAITING or REJECTED for owner
    Page<Booking> findByItemOwnerIdAndStatusOrderByStartDesc(Long ownerId, BookingStatus status, Pageable pageable);

    //for lastBooking
    Booking findFirstByItemIdAndStatusAndStartIsBeforeOrderByEndDesc(Long itemId, BookingStatus status,
            LocalDateTime start);

    // for nextBooking
    Booking findFirstByItemIdAndStatusAndStartIsAfterOrderByStartAsc(Long itemId, BookingStatus status,
            LocalDateTime start);

    //for comments
    List<Booking> findByBookerIdAndItemIdAndStatusAndEndIsBefore(Long bookerId, Long itemId, BookingStatus status,
            LocalDateTime end);
}
