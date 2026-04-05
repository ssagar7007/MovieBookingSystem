package com.sagar.MovieBookingSystem.Service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Service to manage distributed seat locking using Redis with TTL.
 * Prevents double-booking of seats under concurrent requests.
 */
@Service
@Slf4j
public class SeatLockService {

    private static final String SEAT_LOCK_PREFIX = "seat-lock:";
    private static final long LOCK_TTL_SECONDS = 300; // 5 minutes
    private static final String LOCK_VALUE = "locked";

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * Attempts to acquire a lock for a specific seat in a show.
     * Returns true if lock was successfully acquired, false if seat is already locked.
     *
     * @param showId   ID of the show
     * @param seatNumber The seat number to lock
     * @return true if lock acquired successfully, false otherwise
     */
    public boolean acquireLock(Long showId, String seatNumber) {
        String lockKey = generateLockKey(showId, seatNumber);
        try {
            Boolean lockAcquired = redisTemplate.opsForValue()
                    .setIfAbsent(lockKey, LOCK_VALUE, LOCK_TTL_SECONDS, TimeUnit.SECONDS);

            if (Boolean.TRUE.equals(lockAcquired)) {
                log.debug("Lock acquired for show {} seat {}", showId, seatNumber);
                return true;
            } else {
                log.warn("Failed to acquire lock for show {} seat {} - already locked", showId, seatNumber);
                return false;
            }
        } catch (Exception e) {
            log.error("Error acquiring lock for show {} seat {}: {}", showId, seatNumber, e.getMessage());
            throw new RuntimeException("Failed to acquire seat lock", e);
        }
    }

    /**
     * Acquires locks for multiple seats in a show.
     * All locks must be acquired successfully, otherwise release previously acquired locks.
     *
     * @param showId ID of the show
     * @param seatNumbers List of seat numbers to lock
     * @return true if all locks acquired successfully, false if any lock failed
     */
    public boolean acquireMultipleLocks(Long showId, java.util.List<String> seatNumbers) {
        for (String seatNumber : seatNumbers) {
            if (!acquireLock(showId, seatNumber)) {
                // Release all previously acquired locks
                seatNumbers.forEach(seat -> releaseLock(showId, seat));
                log.warn("Failed to acquire all locks for show {}. Rolling back locks for seats {}", showId, seatNumbers);
                return false;
            }
        }
        return true;
    }

    /**
     * Releases a lock for a specific seat.
     *
     * @param showId ID of the show
     * @param seatNumber The seat number to unlock
     */
    public void releaseLock(Long showId, String seatNumber) {
        String lockKey = generateLockKey(showId, seatNumber);
        try {
            redisTemplate.delete(lockKey);
            log.debug("Lock released for show {} seat {}", showId, seatNumber);
        } catch (Exception e) {
            log.error("Error releasing lock for show {} seat {}: {}", showId, seatNumber, e.getMessage());
        }
    }

    /**
     * Releases locks for multiple seats.
     *
     * @param showId ID of the show
     * @param seatNumbers List of seat numbers to unlock
     */
    public void releaseMultipleLocks(Long showId, java.util.List<String> seatNumbers) {
        seatNumbers.forEach(seatNumber -> releaseLock(showId, seatNumber));
        log.info("Locks released for show {} seats {}", showId, seatNumbers);
    }

    /**
     * Checks if a specific seat is locked.
     *
     * @param showId ID of the show
     * @param seatNumber The seat number to check
     * @return true if seat is locked, false otherwise
     */
    public boolean isLocked(Long showId, String seatNumber) {
        String lockKey = generateLockKey(showId, seatNumber);
        try {
            Object value = redisTemplate.opsForValue().get(lockKey);
            return value != null;
        } catch (Exception e) {
            log.error("Error checking lock status for show {} seat {}: {}", showId, seatNumber, e.getMessage());
            return true; // Assume locked on error for safety
        }
    }

    /**
     * Extends the TTL of an existing lock.
     *
     * @param showId ID of the show
     * @param seatNumber The seat number whose lock to extend
     * @return true if TTL was extended, false if lock doesn't exist
     */
    public boolean extendLock(Long showId, String seatNumber) {
        String lockKey = generateLockKey(showId, seatNumber);
        try {
            Boolean expired = redisTemplate.expire(lockKey, LOCK_TTL_SECONDS, TimeUnit.SECONDS);
            if (Boolean.TRUE.equals(expired)) {
                log.debug("Lock TTL extended for show {} seat {}", showId, seatNumber);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("Error extending lock for show {} seat {}: {}", showId, seatNumber, e.getMessage());
            return false;
        }
    }

    /**
     * Generates a unique key for a seat lock in Redis.
     *
     * @param showId ID of the show
     * @param seatNumber The seat number
     * @return The Redis key for the seat lock
     */
    private String generateLockKey(Long showId, String seatNumber) {
        return SEAT_LOCK_PREFIX + showId + ":" + seatNumber;
    }

    /**
     * Gets the current TTL for a lock in seconds.
     *
     * @param showId ID of the show
     * @param seatNumber The seat number
     * @return TTL in seconds, or -2 if key doesn't exist, -1 if no expiry set
     */
    public Long getLockTTL(Long showId, String seatNumber) {
        String lockKey = generateLockKey(showId, seatNumber);
        try {
            return redisTemplate.getExpire(lockKey, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("Error getting lock TTL for show {} seat {}: {}", showId, seatNumber, e.getMessage());
            return -1L;
        }
    }
}



