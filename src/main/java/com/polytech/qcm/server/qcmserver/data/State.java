package com.polytech.qcm.server.qcmserver.data;

public enum State {
    COMPLETE, INCOMPLETE, STARTED;

    public String stateName(){return "STATE_" + name();}
}
