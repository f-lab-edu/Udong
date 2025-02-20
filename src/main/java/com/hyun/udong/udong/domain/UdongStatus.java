package com.hyun.udong.udong.domain;

public enum UdongStatus {

    PREPARE, IN_PROGRESS, DONE;

    public UdongStatus nextStatus() {
        if (this.equals(PREPARE)) {
            return IN_PROGRESS;
        }
        if (this.equals(IN_PROGRESS)) {
            return DONE;
        }
        return PREPARE;
    }

    public boolean isPrepare() {
        return this.equals(PREPARE);
    }
}
