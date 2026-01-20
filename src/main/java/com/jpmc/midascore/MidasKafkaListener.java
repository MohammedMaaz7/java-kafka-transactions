package com.jpmc.midascore;

import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.repository.UserRepository;
import com.jpmc.midascore.repository.TransactionRecordRepository;
import com.jpmc.midascore.foundation.Transaction;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class MidasKafkaListener {

    private final UserRepository userRepository;
    private final TransactionRecordRepository transactionRecordRepository;

    public MidasKafkaListener(UserRepository userRepository,
                              TransactionRecordRepository transactionRecordRepository) {
        this.userRepository = userRepository;
        this.transactionRecordRepository = transactionRecordRepository;
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

        // Validate sender balance
        if (sender.getBalance() < amount) return;

        // Update balances
        sender.setBalance(sender.getBalance() - amount);
        receiver.setBalance(receiver.getBalance() + amount);

        // Save users
        userRepository.save(sender);
        userRepository.save(receiver);

        // Save transaction record
        TransactionRecord tr = new TransactionRecord(sender, receiver, amount);
        transactionRecordRepository.save(tr);
    }
}
