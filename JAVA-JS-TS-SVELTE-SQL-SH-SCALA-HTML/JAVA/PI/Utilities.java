package it.poste.utils;

import it.poste.gate.dto.error.*;

import javax.ws.rs.core.*;
import java.time.*;
import java.time.format.*;


public class Utilities {

    private static final DateTimeFormatter standardDateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX");

    public static Response checkDates(String startDate, String endDate) {
        if (startDate != null && endDate != null) {
            ZonedDateTime dateStart, dateEnd;
            try {
                dateStart = ZonedDateTime.parse(startDate, standardDateTimeFormat);
            } catch (DateTimeParseException p) {
            	return Response
                .status(Response.Status.BAD_REQUEST)
                .entity(ErrorMessageDto.builder()
                        .status(Response.Status.BAD_REQUEST.getReasonPhrase())
                        .errorCode(String.valueOf(Response.Status.BAD_REQUEST.getStatusCode()))
                        .errorDescription("The startDate must be in a valid format")
                        .build())
                .build();
            }
            try {
                dateEnd = ZonedDateTime.parse(endDate, standardDateTimeFormat);
            } catch (DateTimeParseException p) {
            	return Response
                        .status(Response.Status.BAD_REQUEST)
                        .entity(ErrorMessageDto.builder()
                                .status(Response.Status.BAD_REQUEST.getReasonPhrase())
                                .errorCode(String.valueOf(Response.Status.BAD_REQUEST.getStatusCode()))
                                .errorDescription("The endDate must be in a valid format")
                                .build())
                        .build();
            }
            if (dateStart.compareTo(dateEnd) > 0) {
            	return Response
                        .status(Response.Status.BAD_REQUEST)
                        .entity(ErrorMessageDto.builder()
                                .status(Response.Status.BAD_REQUEST.getReasonPhrase())
                                .errorCode(String.valueOf(Response.Status.BAD_REQUEST.getStatusCode()))
                                .errorDescription("The startDate must be lower than the endDate")
                                .build())
                        .build();
            }
        } else if (startDate == null && endDate != null) {
        	return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(ErrorMessageDto.builder()
                            .status(Response.Status.BAD_REQUEST.getReasonPhrase())
                            .errorCode(String.valueOf(Response.Status.BAD_REQUEST.getStatusCode()))
                            .errorDescription("The startDate must be provided")
                            .build())
                    .build();
        } else if (startDate != null) {
        	return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(ErrorMessageDto.builder()
                            .status(Response.Status.BAD_REQUEST.getReasonPhrase())
                            .errorCode(String.valueOf(Response.Status.BAD_REQUEST.getStatusCode()))
                            .errorDescription("The endDate must be provided")
                            .build())
                    .build();
        }
        return null;
    }

}
