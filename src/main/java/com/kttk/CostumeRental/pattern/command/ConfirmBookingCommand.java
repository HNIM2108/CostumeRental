package com.kttk.CostumeRental.pattern.command;

import com.kttk.CostumeRental.DTO.BookingRequestDTO;
import com.kttk.CostumeRental.service.BookingService;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ConfirmBookingCommand implements ICommand {
    private BookingService bookingService;
    private BookingRequestDTO request;

    @Override
    public Object execute() {
        return bookingService.confirmBooking(request);
    }
}
