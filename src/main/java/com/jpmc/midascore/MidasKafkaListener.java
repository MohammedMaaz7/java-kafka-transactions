package com.jpmc.midascore;

import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.repository.UserRepository;
import com.jpmc.midascore.repository.TransactionRecordRepository;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.foundation.Incentive;
import com.jpmc.midascore.service.IncentiveClient;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class MidasKafkaListener {

    private final UserRepository userRepository;
    private final TransactionRecordRepository transactionRecordRepository;
    private final IncentiveClient incentiveClient;

    public MidasKafkaListener(UserRepository userRepository,
                              TransactionRecordRepository transactionRecordRepository,
                              IncentiveClient incentiveClient) {
        this.userRepository = userRepository;
        this.transactionRecordRepository = transactionRecordRepository;
        this.incentiveClient = incentiveClient;
    }

    @KafkaListener(
            topics = "${general.kafka-topic}",
            groupId = "midas-group"
    )
    public void handleTransaction(Transaction tx) {

        // Load users
        UserRecord sender = userRepository.findById(tx.getSenderId());
        UserRecord receiver = userRepository.findById(tx.getRecipientId());

        // Validate users
        if (sender == null || receiver == null) return;

        float amount = tx.getAmount();

        // Validate balance
        if (sender.getBalance() < amount) return;

        // -------------------------
        // CALL INCENTIVE API (Task 4)
        // -------------------------
        Incentive incentive = incentiveClient.fetchIncentive(tx);
        float incentiveAmount = incentive != null ? incentive.getAmount() : 0f;

        // -------------------------
        // UPDATE BALANCES
        // -------------------------
        sender.setBalance(sender.getBalance() - amount);                // sender loses ONLY amount
        receiver.setBalance(receiver.getBalance() + amount + incentiveAmount); // receiver gets amount + incentive

        // Save users
        userRepository.save(sender);
        userRepository.save(receiver);

        // -------------------------
        // SAVE TRANSACTION RECORD
        // -------------------------
        TransactionRecord tr = new TransactionRecord(sender, receiver, amount);
        tr.setIncentive(incentiveAmount);
        transactionRecordRepository.save(tr);
    }
}
