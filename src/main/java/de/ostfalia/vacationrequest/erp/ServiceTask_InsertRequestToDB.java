package de.ostfalia.vacationrequest.erp;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import de.ostfalia.vacationrequest.DatabaseConnection;

import java.sql.Connection;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.DayOfWeek;
import java.time.temporal.ChronoUnit;
import java.time.ZoneId;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.Date;

public class ServiceTask_InsertRequestToDB implements JavaDelegate {
    @Override
    public void execute(DelegateExecution execution) throws Exception {
        Date dateStart = (Date) execution.getVariable("REQUESTVACATION_START");
        LocalDate requestVacationStart = LocalDate.ofInstant(dateStart.toInstant(), ZoneId.systemDefault()) ;

        Date dateEnd = (Date) execution.getVariable("REQUESTVACATION_END");
        LocalDate requestVacationEnd = LocalDate.ofInstant(dateEnd.toInstant(), ZoneId.systemDefault()) ;;

        long requestVacationDays = ChronoUnit.DAYS.between(requestVacationStart, requestVacationEnd);
        long requestVacationBusinessDays = countBusinessDaysBetween(requestVacationStart, requestVacationEnd);

        Connection connection = DatabaseConnection.getConnection();
        CallableStatement max = connection.prepareCall("SELECT MAX(idUrlaubsantrag) FROM Urlaubsantrag");
        ResultSet resultSet = max.executeQuery();
        int idRequestVacation = !resultSet.next() ? 0 : resultSet.getInt(1) + 1;
        resultSet.close();

        execution.setVariable("REQUESTVACATION_ID", idRequestVacation);

        String sql = "INSERT INTO `vacation_request`.`Urlaubsantrag` (`idUrlaubsantrag`, `idMitarbeiter`, `Beginn`, `Ende`, `Kalendertage`, `Arbeitstage`, `Wochenendtage`, `Feiertage`) VALUES (?, ?, ?, ?, ?, ?, ?, ?);";

        CallableStatement callableStatement = connection.prepareCall(sql);
        callableStatement.setInt(1, idRequestVacation);
        callableStatement.setInt(2, (int) execution.getVariable("EMPLOYEE_ID"));
        callableStatement.setObject(3, java.sql.Date.valueOf(requestVacationStart));
        callableStatement.setObject(4, java.sql.Date.valueOf(requestVacationEnd));
        callableStatement.setLong(5, requestVacationDays);
        callableStatement.setLong(6, requestVacationBusinessDays);
        callableStatement.setLong(7, requestVacationDays-requestVacationBusinessDays);
        callableStatement.setInt(8, 0);
        callableStatement.executeUpdate();

        callableStatement.close();
        connection.close();
    }
    private static long countBusinessDaysBetween(LocalDate startDate, LocalDate endDate) {
        Predicate<LocalDate> isWeekend = date -> date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY;
        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);
        long businessDays = Stream.iterate(startDate, date -> date.plusDays(1)).limit(daysBetween).filter((isWeekend).negate()).count();

        return businessDays;
    }
}

