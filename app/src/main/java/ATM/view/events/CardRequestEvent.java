package ATM.view.events;

import ATM.view.callbackComms.UserRequest;

public class CardRequestEvent extends Event<CardRequests> {

    public CardRequestEvent(CardRequests requests) {
        super(requests);
    }

}
