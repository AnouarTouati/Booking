package com.example.bookingapp.shop;

class ClientPending {
    private String clientName;
    private String clientFirebaseUid;
    private String clientRequestedServices;
    private String clientFakeFirebaseUid;//for people added by the shop owner manually



    public String getClientFakeFirebaseUid() {
        return clientFakeFirebaseUid;
    }

    public ClientPending(String clientName, String clientFirebaseUid, String clientRequestedServices, String clientFakeFirebaseUid) {
        this.clientName = clientName;
        this.clientFirebaseUid = clientFirebaseUid;
        this.clientRequestedServices = clientRequestedServices;
        this.clientFakeFirebaseUid = clientFakeFirebaseUid;
    }

    public String getClientName() {
        return clientName;
    }

    public String getClientFirebaseUid() {
        return clientFirebaseUid;
    }

    public String getClientRequestedServices() {
        return clientRequestedServices;
    }
}
