package com.retailer.rewards.service;

import com.retailer.rewards.exception.CustomerNotFoundException;
import com.retailer.rewards.model.CustomerReward;
import com.retailer.rewards.model.Transaction;
import com.retailer.rewards.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RewardServiceTest {

    private RewardService rewardService;
    private TransactionRepository transactionRepository;

    @BeforeEach
    void setUp() {
        transactionRepository = mock(TransactionRepository.class);
        rewardService = new RewardService(transactionRepository, 3);
    }

    @Test
    void shouldCalculatePointsForDifferentAmounts() {
        assertEquals(0, rewardService.calculatePoints(50));
        assertEquals(0, rewardService.calculatePoints(50.99));
        assertEquals(25, rewardService.calculatePoints(75));
        assertEquals(50, rewardService.calculatePoints(100));
        assertEquals(52, rewardService.calculatePoints(101));
        assertEquals(90, rewardService.calculatePoints(120));
        assertEquals(250, rewardService.calculatePoints(200));
        assertEquals(0, rewardService.calculatePoints(-10));
    }

    @Test
    void shouldReturnMonthlyAndTotalPointsForCustomer() {
        Transaction latest = new Transaction(
                4, 101, "Anita", 50, LocalDate.of(2026, 3, 12));
        List<Transaction> transactions = Arrays.asList(
                new Transaction(1, 101, "Anita", 120,
                        LocalDate.of(2026, 1, 10)),
                new Transaction(2, 101, "Anita", 75,
                        LocalDate.of(2026, 1, 18)),
                new Transaction(3, 101, "Anita", 200,
                        LocalDate.of(2026, 2, 5)),
                latest);

        when(transactionRepository
                .findTopByCustomerIdOrderByTransactionDateDesc(101))
                .thenReturn(Optional.of(latest));
        when(transactionRepository
                .findByCustomerIdAndTransactionDateBetweenOrderByTransactionDateAsc(
                        101,
                        LocalDate.of(2026, 1, 1),
                        LocalDate.of(2026, 3, 31)))
                .thenReturn(transactions);

        CustomerReward reward = rewardService.getCustomerReward(101);

        assertEquals("Anita", reward.getCustomerName());
        assertEquals(3, reward.getMonthlyRewards().size());
        assertEquals("March 2026",
                reward.getMonthlyRewards().get(2).getMonth());
        assertEquals(0, reward.getMonthlyRewards().get(2).getPoints());
        assertEquals(365, reward.getTotalPoints());
    }

    @Test
    void shouldThrowExceptionForUnknownCustomer() {
        when(transactionRepository
                .findTopByCustomerIdOrderByTransactionDateDesc(999))
                .thenReturn(Optional.empty());

        assertThrows(CustomerNotFoundException.class,
                () -> rewardService.getCustomerReward(999));
    }

    @Test
    void shouldReturnAllCustomersInOneConfiguredPeriod() {
        Transaction latest = new Transaction(
                8, 102, "Rahul", 110, LocalDate.of(2026, 3, 25));
        when(transactionRepository.findTopByOrderByTransactionDateDesc())
                .thenReturn(Optional.of(latest));
        when(transactionRepository
                .findByTransactionDateBetweenOrderByCustomerIdAscTransactionDateAsc(
                        LocalDate.of(2026, 1, 1),
                        LocalDate.of(2026, 3, 31)))
                .thenReturn(Arrays.asList(
                        new Transaction(1, 101, "Anita", 120,
                                LocalDate.of(2026, 1, 10)),
                        new Transaction(5, 102, "Rahul", 95,
                                LocalDate.of(2026, 1, 7)),
                        latest));

        List<CustomerReward> rewards =
                rewardService.getAllCustomerRewards();

        assertEquals(2, rewards.size());
        assertEquals(90, rewards.get(0).getTotalPoints());
        assertEquals(115, rewards.get(1).getTotalPoints());
    }

    @Test
    void shouldReturnEmptyListWhenDatabaseHasNoTransactions() {
        when(transactionRepository.findTopByOrderByTransactionDateDesc())
                .thenReturn(Optional.empty());

        assertEquals(Collections.emptyList(),
                rewardService.getAllCustomerRewards());
    }

    @Test
    void shouldUseConfiguredNumberOfMonths() {
        RewardService eightMonthService =
                new RewardService(transactionRepository, 8);
        Transaction latest = new Transaction(
                1, 101, "Anita", 120, LocalDate.of(2026, 3, 10));
        when(transactionRepository
                .findTopByCustomerIdOrderByTransactionDateDesc(101))
                .thenReturn(Optional.of(latest));
        when(transactionRepository
                .findByCustomerIdAndTransactionDateBetweenOrderByTransactionDateAsc(
                        101,
                        LocalDate.of(2025, 8, 1),
                        LocalDate.of(2026, 3, 31)))
                .thenReturn(Collections.singletonList(latest));

        CustomerReward reward =
                eightMonthService.getCustomerReward(101);

        assertEquals(8, reward.getMonthlyRewards().size());
        assertEquals("August 2025",
                reward.getMonthlyRewards().get(0).getMonth());
        assertEquals("March 2026",
                reward.getMonthlyRewards().get(7).getMonth());
    }

    @Test
    void shouldRejectInvalidRewardPeriodConfiguration() {
        assertThrows(IllegalArgumentException.class,
                () -> new RewardService(transactionRepository, 0));
    }
}
