package org.maciejszuwarowski.domain.fundraisingevent;

enum FundraisingEventMessages {
    FUNDRAISING_EVENT_CREATED_SUCCESSFULLY("Fundraising event was created successfully!"),
    MONEY_TRANSFERRED_TO_FUNDRAISING_EVENT_ACCOUNT_SUCCESSFULLY("Money was transferred successfully"),
    FUNDRAISING_EVENT_SAVED_SUCCESSFULLY("Fundraising event was saved successfully!");

    final String message;

    FundraisingEventMessages(String message) {
        this.message = message;
    }
}
