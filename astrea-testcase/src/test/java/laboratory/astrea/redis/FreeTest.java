package laboratory.astrea.redis;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public final class FreeTest {

    public static void main(String[] args) {

        final var now = LocalDateTime.parse("2020-12-03T10:00:00");
        System.out.println(now);

        final var beijingZone = now.atZone(ZoneId.of("8"));
        System.out.println(beijingZone);
//
        final var zonedDateTime = beijingZone.withZoneSameInstant(ZoneId.of("America/Chicago"));

        System.out.println(zonedDateTime.toLocalDateTime());


    }
}
