package com.example.coifsalonbusiness.shop;

class ClientPending {
    private final String clientName;
    private final String clientFirebaseUid;
    private final String clientRequestedServices;
    private final String clientFakeFirebaseUid;//for people added by the shop owner manually



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
