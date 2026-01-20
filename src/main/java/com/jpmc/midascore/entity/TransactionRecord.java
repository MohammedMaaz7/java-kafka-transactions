package com.jpmc.midascore.entity;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
public class TransactionRecord implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The sender user
    @ManyToOne
    @JoinColumn(name = "sender_id")
    private UserRecord from;

    // The receiver user
    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private UserRecord to;

    private float amount;

    public TransactionRecord() {
    }

    public TransactionRecord(UserRecord from, UserRecord to, float amount) {
        this.from = from;
        this.to = to;
        this.amount = amount;
    }

    public Long getId() {
        return id;
    }

    public UserRecord getFrom() {
        return from;
    }

    public void setFrom(UserRecord from) {
        this.from = from;
    }

    public UserRecord getTo() {
        return to;
    }

    public void setTo(UserRecord to) {
        this.to = to;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }
}
